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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.Orientation;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.SelectionMode;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog box that allows selection of a survey
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveySelectionDialog extends WidgetDialog implements ClickHandler {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String SURVEY_KEY = "survey";
	public static final String LANG_KEY = "lang";
	private SurveySelectionWidget selector;
	private Widget additionalControls;
	private Button okButton;
	private Button cancelButton;
	private Label messageLabel;
	private ListBox languageBox;

	public SurveySelectionDialog(CompletionListener listener,
			boolean allowMultiple, Widget additionalControls,
			boolean useLangSelection) {
		super(TEXT_CONSTANTS.selectSurvey(), null, true, listener);
		this.additionalControls = additionalControls;
		Panel panel = new VerticalPanel();
		messageLabel = new Label();
		Panel buttonPanel = new HorizontalPanel();

		selector = new SurveySelectionWidget(Orientation.HORIZONTAL,
				TerminalType.SURVEY, allowMultiple ? SelectionMode.MULTI
						: SelectionMode.SINGLE);
		panel.add(selector);
		if (additionalControls != null) {
			panel.add(additionalControls);
		}
		if (useLangSelection) {
			languageBox = new ListBox(false);
			String locale = com.google.gwt.i18n.client.LocaleInfo
					.getCurrentLocale().getLocaleName();
			languageBox.addItem(TEXT_CONSTANTS.english(), "en");
			languageBox.addItem(TEXT_CONSTANTS.french(), "fr");
			languageBox.addItem(TEXT_CONSTANTS.spanish(), "es");
			languageBox.addItem(TEXT_CONSTANTS.kinyarwanda(), "rw");
			//Issue #3 additions:
			languageBox.addItem(TEXT_CONSTANTS.chichewa(), "ny");
			languageBox.addItem(TEXT_CONSTANTS.bengali(), "bn");
			languageBox.addItem(TEXT_CONSTANTS.hindi(), "hi");
			languageBox.addItem(TEXT_CONSTANTS.quechua(), "qu");			

			if ("en".equalsIgnoreCase(locale)) {
				languageBox.setSelectedIndex(0);
			} else if ("fr".equalsIgnoreCase(locale)) {
				languageBox.setSelectedIndex(1);
			} else if ("sp".equalsIgnoreCase(locale)) {
				languageBox.setSelectedIndex(2);
			} else if ("rw".equalsIgnoreCase(locale)) {
				languageBox.setSelectedIndex(3);
			} else {
				languageBox.setSelectedIndex(0);
			}
			panel.add(ViewUtil.formFieldPair(TEXT_CONSTANTS.language(),
					languageBox, ViewUtil.DEFAULT_INPUT_LABEL_CSS));
		}
		panel.add(messageLabel);
		messageLabel.setVisible(false);
		okButton = new Button(TEXT_CONSTANTS.ok());
		okButton.addClickHandler(this);
		cancelButton = new Button(TEXT_CONSTANTS.cancel());
		cancelButton.addClickHandler(this);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		panel.add(buttonPanel);
		setContentWidget(panel);
	}

	public Widget getAdditionalControls() {
		return additionalControls;
	}

	public SurveySelectionDialog(CompletionListener listener,
			boolean allowMultiple) {
		this(listener, allowMultiple, null, false);
	}

	public SurveySelectionDialog(CompletionListener listener) {
		this(listener, true);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == cancelButton) {
			hide(true);
		} else if (event.getSource() == okButton) {
			if (selector.getSelectedSurveyIds() == null
					|| selector.getSelectedSurveyIds().size() == 0) {
				messageLabel.setText(TEXT_CONSTANTS.selectSurveyFirst());
				messageLabel.setVisible(true);
			} else {
				hide(true);
				Map<String, Object> payload = new HashMap<String, Object>();
				List<Long> ids = selector.getSelectedSurveyIds();
				payload.put(SURVEY_KEY, ids.get(0));
				if (languageBox != null) {
					payload.put(LANG_KEY,
							ViewUtil.getListBoxSelection(languageBox, false));
				}
				notifyListener(true, payload);
			}
		}
	}
}
