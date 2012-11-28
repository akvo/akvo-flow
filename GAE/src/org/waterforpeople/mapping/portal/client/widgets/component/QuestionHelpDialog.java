/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog box used for adding help media to survey questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionHelpDialog extends DialogBox {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private QuestionDto questionDto;
	private SurveyServiceAsync surveyService;
	private CompletionListener listener;
	private Label loadingLabel;
	private DockPanel contentPane;
	private FlexTable helpTable;
	private boolean enabled;

	/**
	 * instantiates and displays the dialog box using the translations present
	 * in the questionDto. If the question has options, the translations for the
	 * options will be shown as well.
	 * 
	 * @param dto
	 * @param listener
	 */
	public QuestionHelpDialog(QuestionDto dto, CompletionListener listener) {
		setText(TEXT_CONSTANTS.editHelpMedia());
		enabled = true;
		setAnimationEnabled(true);
		setGlassEnabled(true);
		questionDto = dto;
		this.listener = listener;
		surveyService = GWT.create(SurveyService.class);
		loadingLabel = new Label(TEXT_CONSTANTS.loading());
		contentPane = new DockPanel();
		setPopupPosition(Window.getClientWidth() / 4,
				Window.getClientHeight() / 4);
		contentPane.add(loadingLabel, DockPanel.CENTER);

		HorizontalPanel buttonPanel = new HorizontalPanel();

		Button ok = new Button(TEXT_CONSTANTS.save());
		Button cancel = new Button(TEXT_CONSTANTS.discardChanges());
		buttonPanel.add(ok);
		buttonPanel.add(cancel);
		contentPane.add(buttonPanel, DockPanel.SOUTH);
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				saveHelp();
			}
		});
		cancel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		setWidget(contentPane);
		loadData();
	}

	private void loadData() {
		surveyService.listHelpByQuestion(questionDto.getKeyId(),
				new AsyncCallback<List<QuestionHelpDto>>() {

					@Override
					public void onSuccess(List<QuestionHelpDto> result) {
						Widget w = buildContent(result);
						contentPane.remove(loadingLabel);
						contentPane.add(w, DockPanel.CENTER);
					}

					@Override
					public void onFailure(Throwable caught) {
						loadingLabel.setText(TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());

					}
				});
	}

	private void saveHelp() {
		enabled = false;
		if (helpTable != null && helpTable.getRowCount() > 0) {
			List<QuestionHelpDto> helpDtos = new ArrayList<QuestionHelpDto>();
			for (int i = 0; i < helpTable.getRowCount(); i++) {
				QuestionHelpDto dto = ((HelpMediaWidget) helpTable.getWidget(i,
						0)).getHelpDto();
				dto.setQuestionId(questionDto.getKeyId());
				helpDtos.add(dto);
			}
			surveyService.saveHelp(helpDtos,
					new AsyncCallback<List<QuestionHelpDto>>() {

						@Override
						public void onFailure(Throwable caught) {
							enabled = true;
						}

						@Override
						public void onSuccess(List<QuestionHelpDto> result) {
							enabled = true;
							questionDto.setQuestionHelpList(result);
							hide();
							notifyListeners();
						}
					});
		} else {
			hide();
		}
	}

	/**
	 * builds the UI.
	 * 
	 * @return
	 */
	private Widget buildContent(List<QuestionHelpDto> questionHelpList) {
		VerticalPanel vPanel = new VerticalPanel();
		helpTable = new FlexTable();
		if (questionHelpList != null) {
			int count = 0;
			for (QuestionHelpDto help : questionHelpList) {
				helpTable.insertRow(count);
				helpTable.setWidget(count, 0, new HelpMediaWidget(help));
				count++;
			}
		}
		vPanel.add(helpTable);
		HorizontalPanel buttonPanel = new HorizontalPanel();
		Button addButton = new Button(TEXT_CONSTANTS.addHelpItem());
		addButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int idx = helpTable.insertRow(helpTable.getRowCount());
				helpTable.setWidget(idx, 0, new HelpMediaWidget(null));
			}
		});
		buttonPanel.add(addButton);
		vPanel.add(buttonPanel);

		return vPanel;
	}

	private void notifyListeners() {
		if (listener != null) {
			listener.operationComplete(true, null);
		}
	}

	/**
	 * allow the user to press escape to close
	 */
	@Override
	public boolean onKeyDownPreview(char key, int modifiers) {
		if (enabled) {
			switch (key) {
			case KeyCodes.KEY_ESCAPE:
				hide();
				return true;
			}
		}
		return false;

	}
}
