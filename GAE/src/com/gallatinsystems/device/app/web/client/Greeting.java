package com.gallatinsystems.device.app.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Greeting implements EntryPoint {
	private VerticalPanel mainPanel = new VerticalPanel();
	private TextBox newSymbolTextBox = new TextBox();
	private TextBox greetingTextBox = new TextBox();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private Button addStockButton = new Button("Add");
	private TextBox rotateStatus = new TextBox();
	private Tree surveyTree = new Tree();

	public Greeting() {
		System.out.println("Created");
	}

	/**
	 * Entry point classes define <code>onModuleLoad()</code>.
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	public void onModuleLoad() {

		final GreetingServiceAsync svc = (GreetingServiceAsync) GWT
				.create(GreetingService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) svc;
		endpoint.setServiceEntryPoint("/greet");
		final Image image = new Image();
		final String urlString = "http://waterforpeople.s3.amazonaws.com/images/africa/malawi/test.jpg";
		final AsyncCallback callback = new AsyncCallback() {

			public void onFailure(Throwable caught) {

			}

			public void onSuccess(Object result) {
				// greetingTextBox.setText((String)result);
				Integer random = Random.nextInt();
				image.setUrl(urlString + "?random=" + random);
				rotateStatus.setVisible(false);
				image.setVisible(true);
			}

		};

		// Point the image at a real URL.
		image
				.setUrl("http://waterforpeople.s3.amazonaws.com/images/africa/malawi/test.jpg");
		image.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				svc.rotateImage("test.jpg", callback);
				image.setVisible(false);
				rotateStatus.setText("Rotating: " + urlString);
				rotateStatus.setVisible(true);

			}

		});

		TreeItem outerRoot = new TreeItem("Item 1");
		outerRoot.addItem("Item 1-1");
		outerRoot.addItem("Item 1-2");
		outerRoot.addItem("Item 1-3");
		outerRoot.addItem(new CheckBox("Item 1-4"));
		surveyTree.addItem(outerRoot);

		TreeItem innerRoot = new TreeItem("Item 1-5");
		innerRoot.addItem("Item 1-5-1");
		innerRoot.addItem("Item 1-5-2");
		innerRoot.addItem("Item 1-5-3");
		innerRoot.addItem("Item 1-5-4");
		innerRoot.addItem(new CheckBox("Item 1-5-5"));

		outerRoot.addItem(innerRoot);

		addPanel.add(surveyTree);

		// Assemble Add Stock panel.
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);
		addPanel.add(newSymbolTextBox);
		addPanel.add(greetingTextBox);
		addPanel.add(image);
		rotateStatus.setVisible(false);
		addPanel.add(rotateStatus);

		// Assemble Main panel.
		mainPanel.add(addPanel);

		// Associate the Main panel with the HTML host page.
		RootPanel.get("greeting").add(mainPanel);

		surveyTree.addSelectionHandler(new SelectionHandler() {
			public void onSelection(SelectionEvent event) {
				Window.alert(((TreeItem)event.getSelectedItem()).getText());
			}
		});

		newSymbolTextBox.setFocus(true);
		// Listen for mouse events on the Add button.
		addStockButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				greetingTextBox.setText("ack" + " "
						+ newSymbolTextBox.getText());
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
