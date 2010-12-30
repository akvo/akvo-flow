package com.gallatinsystems.framework.gwt.component;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ListBasedWidget extends Composite implements
		ClickHandler, MouseOverHandler, MouseOutHandler {

	private static final String LOADING_CSS = "loading-label";
	private static final String LOADING_TEXT = "Loading. Please wait...";
	public static final String LIST_ITEM_CSS = "clickable-list-item";
	private static final String LIST_ITEM_HOVER_CSS = "red-hover";

	private Label loadingLabel;
	private VerticalPanel panel;
	private PageController controller;


	protected ListBasedWidget(PageController controller) {
		this.controller = controller;
		panel = new VerticalPanel();
		loadingLabel = new Label();
		loadingLabel.setText(LOADING_TEXT);
		loadingLabel.setStylePrimaryName(LOADING_CSS);
		panel.add(loadingLabel);
		initWidget(panel);
	}

	public Label createListEntry(String text) {
		Label l = new Label();
		l.setStylePrimaryName(LIST_ITEM_CSS);
		l.setText(text);
		l.addMouseOutHandler(this);
		l.addMouseOverHandler(this);
		l.addClickHandler(this);
		return l;
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		((Label) event.getSource()).removeStyleName(LIST_ITEM_HOVER_CSS);
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		((Label) event.getSource()).addStyleName(LIST_ITEM_HOVER_CSS);
	}

	@Override
	public void onClick(ClickEvent event) {
		handleItemClick(event.getSource());
	}

	protected void toggleLoading(boolean show) {
		loadingLabel.setVisible(show);
	}

	protected void addWidget(Widget w) {
		panel.add(w);
	}
	
	protected void openPage(Class clazz, Map<String, Object> bundle){
		controller.openPage(clazz,bundle);
	}

	protected abstract void handleItemClick(Object source);
}
