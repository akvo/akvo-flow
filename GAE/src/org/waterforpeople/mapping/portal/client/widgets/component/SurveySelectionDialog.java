package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.Orientation;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog box that allows selection of a survey
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveySelectionDialog extends DialogBox implements ClickHandler {

	public static final String SURVEY_KEY = "survey";
	private SurveySelectionWidget selector;
	private Button okButton;
	private Button cancelButton;
	private CompletionListener listener;
	private Label messageLabel;

	public SurveySelectionDialog(CompletionListener listener) {
		this.listener = listener;
		setText("Select Survey");
		setAnimationEnabled(true);
		setGlassEnabled(true);
		Panel panel = new VerticalPanel();
		messageLabel = new Label();
		Panel buttonPanel = new HorizontalPanel();
		selector = new SurveySelectionWidget(Orientation.HORIZONTAL,
				TerminalType.SURVEY);
		panel.add(selector);
		panel.add(messageLabel);
		messageLabel.setVisible(false);
		okButton = new Button("Ok");
		okButton.addClickHandler(this);
		cancelButton = new Button("Cancel");
		cancelButton.addClickHandler(this);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		panel.add(buttonPanel);

		setWidget(panel);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == cancelButton) {
			hide(true);
		} else if (event.getSource() == okButton) {
			if (selector.getSelectedSurveyIds() == null
					|| selector.getSelectedSurveyIds().size() == 0) {
				messageLabel
						.setText("Please select a survey before you click 'Ok'");
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

	/**
	 * if a listener has been installed, calls the operationComplete method to
	 * signal that the dialog box action is done
	 * 
	 * @param wasSuccessful
	 * @param payload
	 */
	protected void notifyListener(boolean wasSuccessful,
			Map<String, Object> payload) {
		if (listener != null) {
			listener.operationComplete(wasSuccessful, payload);
		}
	}

	/**
	 * shows the dialog box in the center of the screen
	 */
	public void showCentered() {
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setPopupPosition(left, top);
			}
		});
	}

}
