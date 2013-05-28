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
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.ListBasedWidget;
import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * lists existing surveyAssignment objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAssignmentListWidget extends ListBasedWidget implements
		ContextAware {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private Map<String, Object> bundle;
	private Map<Widget, SurveyAssignmentDto> assignmentMap;
	private SurveyAssignmentServiceAsync surveyAssignmentService;
	private Grid dataGrid;
	private String currentCursor;

	public SurveyAssignmentListWidget(PageController controller) {
		super(controller);
		currentCursor = null;
		surveyAssignmentService = GWT.create(SurveyAssignmentService.class);
		assignmentMap = new HashMap<Widget, SurveyAssignmentDto>();

	}

	private void loadData() {
		if (dataGrid != null) {
			dataGrid.removeFromParent();
		}
		surveyAssignmentService
				.listSurveyAssignments(
						currentCursor,
						new AsyncCallback<ResponseDto<ArrayList<SurveyAssignmentDto>>>() {

							@Override
							public void onSuccess(
									ResponseDto<ArrayList<SurveyAssignmentDto>> result) {
								toggleLoading(false);
								if (result != null) {
									setCursor(result.getCursorString());
									if (result.getPayload() != null) {
										dataGrid = new Grid(result.getPayload()
												.size()+1, 3);
										for (int i = 0; i < result.getPayload()
												.size(); i++) {
											Label l = createListEntry(result
													.getPayload().get(i)
													.getName());
											dataGrid.setWidget(i, 0, l);
											assignmentMap.put(l, result
													.getPayload().get(i));
											Button b = createButton(
													ClickMode.EDIT,
													TEXT_CONSTANTS.edit());
											assignmentMap.put(b, result
													.getPayload().get(i));
											dataGrid.setWidget(i, 1, b);
											Button e = createButton(
													ClickMode.DELETE,
													TEXT_CONSTANTS.delete());
											dataGrid.setWidget(i, 2, e);
											assignmentMap.put(e, result
													.getPayload().get(i));
										}
										HorizontalPanel navPanel = new HorizontalPanel();
										if(getCurrentPage()>0){
											navPanel.add(getPreviousButtion());
										}
										if(result.getPayload().size()>0 && result.getCursorString()!= null){
											navPanel.add(getNextButton());											
										}
										dataGrid.setWidget(result.getPayload().size(), 0, navPanel);
										addWidget(dataGrid);
										
									}
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								toggleLoading(false);
								MessageDialog errDia = new MessageDialog(
										TEXT_CONSTANTS.error(), TEXT_CONSTANTS
												.errorTracePrefix()
												+ " "
												+ caught.getLocalizedMessage());
								errDia.showCentered();
							}
						});
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		flushContext();
		loadData();
	}

	@Override
	protected void handleItemClick(Object source, ClickMode mode) {
		bundle.put(BundleConstants.SURVEY_ASSIGNMENT, assignmentMap.get(source));
		if (ClickMode.EDIT == mode || ClickMode.OPEN == mode) {
			openPage(SurveyAssignmentEditWidget.class, bundle);
		} else if (ClickMode.DELETE == mode) {
			setWorking(true);
			surveyAssignmentService.deleteSurveyAssignment(
					assignmentMap.get(source), new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							setWorking(false);
							flushContext();
							MessageDialog errDia = new MessageDialog(
									TEXT_CONSTANTS.error(), TEXT_CONSTANTS
											.errorTracePrefix()
											+ " "
											+ caught.getLocalizedMessage());
							errDia.showCentered();
						}

						@Override
						public void onSuccess(Void result) {
							setWorking(false);
							flushContext();
							loadData();

						}
					});
		}else if (ClickMode.NEXT_PAGE == mode){
			loadDataPage(1);
			currentCursor = getCursor(getCurrentPage()-1);
			loadData();
		}else if (ClickMode.PREV_PAGE == mode){
			loadDataPage(-1);
			currentCursor = getCursor(getCurrentPage()-1);
			loadData();
		}
	}

	@Override
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		return bundle;
	}

	@Override
	public void flushContext() {
		if (bundle != null) {
			bundle.remove(BundleConstants.SURVEY_ASSIGNMENT);
		}
	}

	@Override
	public void persistContext(String buttonText,CompletionListener listener) {
		if (listener != null) {
			listener.operationComplete(true, getContextBundle(true));
		}
	}

}
