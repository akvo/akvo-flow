package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeService;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

/**
 * Portlet that allows import of a google docs spreadsheet into a Survey object.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyLoaderPortlet extends Portlet implements ClickHandler {

	public static final String NAME = "Survey Loader";
	public static final String DESCRIPTION = "Allows loading of surveys from Google Docs Spreadsheets";
	private static final int WIDTH = 300;
	private static final int HEIGHT = 300;

	private SpreadsheetMappingAttributeServiceAsync svc;
	private ListBox spreadsheetLB;
	private HorizontalPanel contentPanel;
	private Button processSpreadsheetButton;

	public SurveyLoaderPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		buildHeader();
	}

	private void buildHeader() {
		contentPanel = new HorizontalPanel();
		spreadsheetLB = new ListBox();
		contentPanel.add(new Label("Spreadsheets: "));
		contentPanel.add(spreadsheetLB);
		processSpreadsheetButton = new Button("Import");
		processSpreadsheetButton.addClickHandler(this);
		svc = GWT.create(SpreadsheetMappingAttributeService.class);
		bindListSpreadsheets();
		contentPanel.add(processSpreadsheetButton);
		setWidget(contentPanel);

	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void onClick(ClickEvent event) {
		String selection = spreadsheetLB.getItemText(spreadsheetLB
				.getSelectedIndex());
		if (selection != null && selection.trim().length() > 0) {
			svc.processSurveySpreadsheet(selection, new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(Void result) {
					Window.alert("Imported Survey");
				}
			});
		}
	}

	private void bindListSpreadsheets() {

		svc.listSpreadsheets(new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onFailure(Throwable caught) {

				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ArrayList<String> result) {
				if (result != null) {
					for (String spreadsheetName : result) {
						spreadsheetLB.addItem(spreadsheetName);
					}
					contentPanel.add(spreadsheetLB);
				}
			}
		});
	}
}
