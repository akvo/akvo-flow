package com.gallatinsystems.device.app.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Greeting implements EntryPoint {
	private VerticalPanel mainPanel = new VerticalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private TextBox greetingTextBox = new TextBox();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private Button addStockButton = new Button("Add");
	private TextBox rotateStatus = new TextBox();

	public Greeting() {
		System.out.println("Created");
	}

	/**
	 * Entry point classes define <code>onModuleLoad()</code>.
	 */
	public void onModuleLoad() {
		
		final GreetingServiceAsync svc = (GreetingServiceAsync) GWT.create(GreetingService.class);
		ServiceDefTarget endpoint=(ServiceDefTarget)svc;
		endpoint.setServiceEntryPoint("/greet");
		final Image image = new Image();
		final String urlString = "http://waterforpeople.s3.amazonaws.com/images/africa/malawi/test.jpg";
		final AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			public void onSuccess(Object result) {
				// TODO Auto-generated method stub
				//greetingTextBox.setText((String)result);
				Integer random  = Random.nextInt();
				image.setUrl(urlString+"?random="+random);
				rotateStatus.setVisible(false);
				image.setVisible(true);
			}
			
		};
		
		
		
		
		// Point the image at a real URL.
		image.setUrl("http://waterforpeople.s3.amazonaws.com/images/africa/malawi/test.jpg");
		image.addClickHandler(new ClickHandler(){
		
			public void onClick(ClickEvent event) {
				svc.rotateImage("test.jpg", callback);
				image.setVisible(false);
				rotateStatus.setText("Rotating: " + urlString);
				rotateStatus.setVisible(true);
				
			}
			
		});
		
		// TODO Assemble Add Stock panel.
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);
		addPanel.add(newSymbolTextBox);
		addPanel.add(greetingTextBox);
		addPanel.add(image);
		rotateStatus.setVisible(false);
		addPanel.add(rotateStatus);
		
		// Assemble Main panel.
		mainPanel.add(addPanel);

		// TODO Associate the Main panel with the HTML host page.
		RootPanel.get("greeting").add(mainPanel);

		// TODO Move cursor focus to the input box.
		newSymbolTextBox.setFocus(true);
		// Listen for mouse events on the Add button.
		addStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				greetingTextBox.setText("ack" + " " + newSymbolTextBox.getText());
				svc.greetServer(newSymbolTextBox.getText(), callback);
				
			}
		});

		// Listen for keyboard events in the input box.
		newSymbolTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {

				}
			}
		});

	}

}
