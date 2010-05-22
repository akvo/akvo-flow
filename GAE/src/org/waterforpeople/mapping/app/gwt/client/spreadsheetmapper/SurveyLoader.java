package org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

public class SurveyLoader implements EntryPoint {
	public static final String NAME = "Survey Manager Portlet";
	public static final String DESCRIPTION = "Manages Create/Edit/Delete of Surveys";
	private static String title = "";
	private static Boolean scrollable = true;
	private static Boolean configurable = false;
	private static final int HEIGHT = 800;
	private static final int WIDTH = 1080;
	private SpreadsheetMappingAttributeServiceAsync svc = null;
	ListBox spreadsheetLB = new ListBox();
	HorizontalPanel contentPanel = new HorizontalPanel();
	Button processSpreadsheetButton = new Button();
	
	private void buildHeader() {
		svc = GWT.create(SpreadsheetMappingAttributeService.class);
		bindListSpreadsheets();
		contentPanel.add(processSpreadsheetButton);
	}
	
	
	private void addClickHandlers(){
		processSpreadsheetButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				svc.processSurveySpreadsheet(spreadsheetLB.getItemText(spreadsheetLB.getSelectedIndex()), new AsyncCallback(){

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(Object result) {
						Window.alert("Imported Survey");
						
					}
					
				});
			}
			
		});
	}

	private void bindListSpreadsheets() {
		svc.listSpreadsheets(new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ArrayList<String> result) {
				for (String spreadsheetName : result) {
					spreadsheetLB.addItem(spreadsheetName);
				}
				contentPanel.add(spreadsheetLB);
			}

		});
	}

	@Override
	public void onModuleLoad() {
		// TODO Auto-generated method stub

	}

}
