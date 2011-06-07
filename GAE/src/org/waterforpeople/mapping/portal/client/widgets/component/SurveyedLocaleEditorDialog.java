package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog box to display the surveyed locale editor in either readOnly or
 * read-write mode.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyedLocaleEditorDialog extends WidgetDialog implements
		ClickHandler, CompletionListener {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String CRITERIA_KEY = "APcriteria";
	private SurveyedLocaleEditorWidget editWidget;
	private Button okButton;

	public SurveyedLocaleEditorDialog(CompletionListener listener,
			SurveyedLocaleDto dto, boolean allowEdit) {
		super(TEXT_CONSTANTS.surveyedLocaleManager(), null, true, listener);
		Panel panel = new VerticalPanel();
		editWidget = new SurveyedLocaleEditorWidget(allowEdit, dto, this);
		panel.add(editWidget);
		Panel buttonPanel = new HorizontalPanel();
		okButton = new Button(TEXT_CONSTANTS.close());
		okButton.addClickHandler(this);
		buttonPanel.add(okButton);
		panel.add(buttonPanel);
		setContentWidget(panel);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == okButton) {
			hide(true);
			Map<String, Object> payload = new HashMap<String, Object>();
			notifyListener(true, payload);
		}
	}

	@Override
	public void operationComplete(boolean wasSuccessful,
			Map<String, Object> payload) {
		if (wasSuccessful) {
			hide(true);
			notifyListener(wasSuccessful, payload);
		}
	}
}