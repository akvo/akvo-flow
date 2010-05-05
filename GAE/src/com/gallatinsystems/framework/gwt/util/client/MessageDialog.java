package com.gallatinsystems.framework.gwt.util.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * simple utility class for showing a message dialog in GWT that consists of a
 * header and a body that is html
 * 
 * @author Christopher Fagiani
 * 
 */
public class MessageDialog extends DialogBox {

	public MessageDialog(String title, String bodyHtml) {
		setText(title);
		DockPanel dock = new DockPanel();

		HTML content = new HTML(bodyHtml);

		dock.add(content, DockPanel.CENTER);

		Button ok = new Button("OK");

		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				MessageDialog.this.hide();
			}
		});
		dock.add(ok, DockPanel.SOUTH);
		setWidget(dock);
	}
}
