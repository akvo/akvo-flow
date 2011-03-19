package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Dialog box that can be used to construct an AccessPointSearchCriteria object
 * 
 * TODO: refactor this, SurveySelectorDialog and WidgetDialog
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointFilterDialog extends DialogBox implements ClickHandler {

	public static final String CRITERIA_KEY = "APcriteria";
	private CompletionListener listener;
	private AccessPointSearchControl searchControl;
	private Button okButton;
	private Button cancelButton;

	public AccessPointFilterDialog(CompletionListener listener) {
		this.listener = listener;
		setText("Specify Access Point Filters");
		setAnimationEnabled(true);
		setGlassEnabled(true);
		Panel panel = new VerticalPanel();
		searchControl = new AccessPointSearchControl();
		panel.add(searchControl);
		Panel buttonPanel = new HorizontalPanel();
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
			hide(true);
			Map<String, Object> payload = new HashMap<String, Object>();
			payload.put(CRITERIA_KEY, searchControl.getSearchCriteria());
			notifyListener(true, payload);
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
