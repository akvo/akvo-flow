package org.waterforpeople.mapping.portal.client.widgets;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * portlet for manual upload of data zip files
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataUploadPortlet extends Portlet implements ClickHandler,
		SubmitCompleteHandler {

	public static final String NAME = "Data Upload";
	public static final String DESCRIPTION = "Manual upload of data zip files";
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;
	private static final String NOTIFICATION_URL = "/processor";
	private static final String UPLOAD_URL = "http://waterforpeople.s3.amazonaws.com/";
	private static final String S3_KEY = "1JZZVDSNFFQYF23ZYJ02";
	private static final String S3_POLICY = "eyJleHBpcmF0aW9uIjogIjIwMTAtMTAtMDJUMDA6MDA6MDBaIiwgICJjb25kaXRpb25zIjogWyAgICAgeyJidWNrZXQiOiAid2F0ZXJmb3JwZW9wbGUifSwgICAgIFsic3RhcnRzLXdpdGgiLCAiJGtleSIsICJkZXZpY2V6aXAvIl0sICAgIHsiYWNsIjogInB1YmxpYy1yZWFkIn0sICAgIHsic3VjY2Vzc19hY3Rpb25fcmVkaXJlY3QiOiAiaHR0cDovL3d3dy5nYWxsYXRpbnN5c3RlbXMuY29tL1N1Y2Nlc3NVcGxvYWQuaHRtbCJ9LCAgICBbInN0YXJ0cy13aXRoIiwgIiRDb250ZW50LVR5cGUiLCAiIl0sICAgIFsiY29udGVudC1sZW5ndGgtcmFuZ2UiLCAwLCAzMTQ1NzI4XSAgXX0=";
	private static final String S3_SIG = "7/fo9v4qamQJjnbga529k3iZMZE=";

	private Button submitBtn;
	private FormPanel form;
	private FileUpload upload;
	private Label statusLabel;
	private VerticalPanel contentPane;

	public DataUploadPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		contentPane = new VerticalPanel();
		Label instructions = new Label(
				"Manual upload of handheld data. You can export the data from the handheld's SD card and upload from your computer");
		contentPane.add(instructions);
		VerticalPanel tempPanel = new VerticalPanel();
		form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction(UPLOAD_URL);
		form.addSubmitCompleteHandler(this);
		tempPanel.add(new Hidden("key", "devicezip/${filename}"));
		tempPanel.add(new Hidden("AWSAccessKeyId", S3_KEY));
		tempPanel.add(new Hidden("acl", "public-read"));
		tempPanel.add(new Hidden("success_action_redirect",
				"http://www.gallatinsystems.com/SuccessUpload.html"));
		tempPanel.add(new Hidden("policy", S3_POLICY));
		tempPanel.add(new Hidden("signature", S3_SIG));
		tempPanel.add(new Hidden("Content-Type", "application/zip"));

		form.setWidth("275px");

		upload = new FileUpload();
		upload.setName("file");
		tempPanel.add(upload);
		form.setWidget(tempPanel);
		contentPane.add(form);

		submitBtn = new Button("Upload");
		submitBtn.addClickHandler(this);
		contentPane.add(submitBtn);
		statusLabel = new Label();
		statusLabel.setVisible(false);
		contentPane.add(statusLabel);
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
		if (event.getSource() == submitBtn) {
			submitBtn.setVisible(false);
			statusLabel.setText("Uploading...");
			statusLabel.setVisible(true);
			form.submit();
		}

	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		submitBtn.setVisible(true);
		if (event.getResults() == null) {
			statusLabel.setText("Upload complete.");
			String filename = upload.getFilename();
			// create a form to submit the processing notification. This form
			// MUST be added to the contentPane before it can be submitted
			if (filename != null) {
				FormPanel tempForm = new FormPanel();
				tempForm.setMethod(FormPanel.METHOD_GET);
				tempForm.setAction(NOTIFICATION_URL);
				VerticalPanel vPanel = new VerticalPanel();
				vPanel.add(new Hidden("action", "submit"));
				vPanel.add(new Hidden("fileName", filename));
				tempForm.setWidget(vPanel);
				tempForm.setVisible(false);
				contentPane.add(tempForm);
				tempForm.addSubmitCompleteHandler(new SubmitCompleteHandler() {

					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						contentPane.remove((Widget) event.getSource());
					}
				});
				tempForm.submit();
			}
		} else {
			statusLabel.setText("Upload error. Please try again.");
		}
	}
}
