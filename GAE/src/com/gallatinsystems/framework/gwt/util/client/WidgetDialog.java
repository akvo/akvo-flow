package com.gallatinsystems.framework.gwt.util.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Simple dialog box for displaying a single widget
 * 
 * @author Christopher Fagiani
 * 
 */
public class WidgetDialog extends DialogBox {

	public WidgetDialog(String title, Widget widget) {
		// Set the dialog box's caption.
		setText(title);
		setAnimationEnabled(true);
		setGlassEnabled(true);

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

	@Override
	public boolean onKeyDownPreview(char key, int modifiers) {
		switch (key) {
		case KeyCodes.KEY_ESCAPE:
			hide();
			return true;
		}
		return false;
	}
}