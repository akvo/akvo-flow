package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.Orientation;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.SelectionMode;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
	private SurveySelectionWidget selector;
	private Button okButton;
	private Button cancelButton;
	private Label messageLabel;

	public SurveySelectionDialog(CompletionListener listener,
			boolean allowMultiple) {
		super(TEXT_CONSTANTS.selectSurvey(), null, true, listener);
		Panel panel = new VerticalPanel();
		messageLabel = new Label();
		Panel buttonPanel = new HorizontalPanel();
		
		selector = new SurveySelectionWidget(Orientation.HORIZONTAL,
				TerminalType.SURVEY,allowMultiple?SelectionMode.MULTI:SelectionMode.SINGLE);
		panel.add(selector);
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
				notifyListener(true, payload);
			}
		}
	}
}
