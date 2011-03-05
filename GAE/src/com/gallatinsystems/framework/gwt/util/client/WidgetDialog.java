package com.gallatinsystems.framework.gwt.util.client;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Simple dialog box for displaying a single widget
 * 
 * @author Christopher Fagiani
 * 
 */
public class WidgetDialog extends DialogBox {

	private CompletionListener listener;

	public WidgetDialog(String title, Widget widget, CompletionListener listen) {
		// Set the dialog box's caption.
		setText(title);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		listener = listen;

		DockPanel contentPane = new DockPanel();
		setPopupPosition(Window.getClientWidth() / 4,
				Window.getClientHeight() / 4);
		contentPane.add(widget, DockPanel.CENTER);

		Button ok = new Button("Done");
		contentPane.add(ok, DockPanel.SOUTH);
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		setWidget(contentPane);
	}

	public WidgetDialog(String title, Widget widget) {
		this(title, widget, null);
	}

	@Override
	public boolean onKeyDownPreview(char key, int modifiers) {
		switch (key) {
		case KeyCodes.KEY_ESCAPE:
			hide();
			return true;
		}
		return false;
	}

	public void showCentered() {
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getClientHeight() - offsetHeight) / 2) >> 0;
				setPopupPosition(left, top);
			}
		});
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
}