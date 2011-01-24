package com.gallatinsystems.framework.gwt.util.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * simple utility class for showing a message dialog in GWT that consists of a
 * header and a body that is html
 * 
 * @author Christopher Fagiani
 * 
 */
public class MessageDialog extends DialogBox {
	private HTML content;
	private DockPanel dock;

	public MessageDialog(String title, String bodyHtml, boolean suppressButton,
			final ClickHandler okHandler) {
		setText(title);
		dock = new DockPanel();
		content = new HTML(bodyHtml);

		dock.add(content, DockPanel.CENTER);
		if (!suppressButton) {
			HorizontalPanel hp = new HorizontalPanel();
			Button ok = new Button("OK");

			ok.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					MessageDialog.this.hide();
					if (okHandler != null) {
						okHandler.onClick(event);
					}
				}
			});
			hp.add(ok);
			// only add Cancel button if there is a click handler
			if (okHandler != null) {
				Button cancel = new Button("Cancel");
				hp.add(cancel);
				cancel.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						MessageDialog.this.hide();
					}
				});
			}
			dock.add(hp, DockPanel.SOUTH);
		}
		setWidget(dock);
	}

	public MessageDialog(String title, String bodyHtml, boolean suppressButton) {
		this(title, bodyHtml, suppressButton, null);
	}

	public MessageDialog(String title, String bodyHtml) {
		this(title, bodyHtml, false);
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
}
