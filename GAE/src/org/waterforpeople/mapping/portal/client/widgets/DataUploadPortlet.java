package org.waterforpeople.mapping.portal.client.widgets;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * portlet for manual upload of data zip files
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataUploadPortlet extends Portlet implements ClickHandler {

	public static final String NAME = "Data Upload";
	public static final String DESCRIPTION = "Manual upload of data zip files";
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private Button browseBtn;
	private Button submitBtn;	
	private FormPanel form;
	private FileUpload upload;

	public DataUploadPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		VerticalPanel contentPane = new VerticalPanel();
		Label instructions = new Label(
				"Manual upload of handheld data. You can export the data from the handheld's SD card and upload from your computer");
		contentPane.add(instructions);

		form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setWidth("275px");

		upload = new FileUpload();
		upload.setName("upload");
		contentPane.add(upload);

		submitBtn = new Button("Upload");
		submitBtn.addClickHandler(this);
		contentPane.add(submitBtn);
		setWidget(contentPane);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected boolean getReadyForRemove() {
		return false;
	}

	@Override
	protected void handleConfigClick() {
		// no-op

	}

	@Override
	public void handleEvent(PortletEvent e) {
		// no-op

	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == browseBtn) {

		}

	}

}
