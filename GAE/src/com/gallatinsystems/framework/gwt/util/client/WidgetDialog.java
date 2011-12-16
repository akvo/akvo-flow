package com.gallatinsystems.framework.gwt.util.client;

import java.util.Map;

import com.google.gwt.core.client.GWT;
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
	private static FrameworkTextConstants TEXT_CONSTANTS = GWT
			.create(FrameworkTextConstants.class);

	private CompletionListener listener;
	private Button closeButton;
	private DockPanel contentPane;
	private boolean hideControls;

	/**
	 * initializes and displays a new dialog with the default controls
	 * 
	 * @param title
	 * @param widget
	 * @param listen
	 */
	public WidgetDialog(String title, Widget widget, CompletionListener listen) {
		this(title, widget, false, listen);
	}

	/**
	 * constructs a basic WidgetDialog that has the title and content widget
	 * specified. The close button will be displayed and NO listener will be
	 * bound.
	 * 
	 * @param title
	 * @param widget
	 */
	public WidgetDialog(String title, Widget widget) {
		this(title, widget, null);
	}

	/**
	 * initializes a new dialog with the title and content widget displayed.
	 * noControls flag determines whether or not to show the close button.
	 * 
	 * @param title
	 * @param widget
	 * @param noControls
	 * @param listen
	 */
	public WidgetDialog(String title, Widget widget, boolean noControls,
			CompletionListener listen) {
		hideControls = noControls;
		// Set the dialog box's caption.
		setText(title);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		listener = listen;

		contentPane = new DockPanel();
		setPopupPosition(Window.getClientWidth() / 4,
				Window.getClientHeight() / 4);
		if (widget != null) {
			contentPane.add(widget, DockPanel.CENTER);
		}
		if (!hideControls) {
			closeButton = new Button(TEXT_CONSTANTS.close());
			contentPane.add(closeButton, DockPanel.SOUTH);
			closeButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					hide();
				}
			});
		}
		setWidget(contentPane);
	}

	/**
	 * sets the contentPane for this dialog to be the widget passed in.
	 * 
	 * @param w
	 */
	public void setContentWidget(Widget w) {
		contentPane.add(w, DockPanel.CENTER);
	}

	/**
	 * allows the user to press "escape" to close the dialog. if hideControls is
	 * true, then this is suppressed (since the normal close button is hidden in
	 * that case)
	 * 
	 * @param key
	 * @param modifiers
	 * @return
	 */
	@Override
	public boolean onKeyDownPreview(char key, int modifiers) {
		switch (key) {
		case KeyCodes.KEY_ESCAPE:
			if (!hideControls) {
				hide();
				return true;
			}
		}
		return false;
	}

	/**
	 * shows the dialog in the center of the viewport
	 */
	public void showCentered() {
		setPopupPositionAndShow(new PopupPanel.PositionCallback() {
			public void setPosition(int offsetWidth, int offsetHeight) {
				int left = ((Window.getScrollLeft() + Window.getClientWidth() - offsetWidth) / 2) >> 0;
				int top = ((Window.getScrollTop() + Window.getClientHeight() - offsetHeight) / 2) >> 0;
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