package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	private Button importLocalButton;
	private HTML appletWidget;

	public SurveyLoaderPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		buildHeader();
	}

	private void buildHeader() {
		contentPanel = new HorizontalPanel();
		/*	spreadsheetLB = new ListBox();
		contentPanel.add(new Label("Spreadsheets: "));
		contentPanel.add(spreadsheetLB);
		processSpreadsheetButton = new Button("Import Google Spreadsheet");
		processSpreadsheetButton.addClickHandler(this);
		svc = GWT.create(SpreadsheetMappingAttributeService.class);
		bindListSpreadsheets();
		contentPanel.add(processSpreadsheetButton);*/
		importLocalButton = new Button("Import Local Sheet");
		importLocalButton.addClickHandler(this);
		contentPanel.add(importLocalButton);

		appletWidget = new HTML();
		contentPanel.add(appletWidget);
		setWidget(contentPanel);

	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == processSpreadsheetButton) {
			String selection = spreadsheetLB.getItemText(spreadsheetLB
					.getSelectedIndex());
			if (selection != null && selection.trim().length() > 0) {
				svc.processSurveySpreadsheet(selection, -2, null,
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								MessageDialog errDia = new MessageDialog(
										"Error",
										"Cannot import spreadsheet into survey.");
								errDia.showRelativeTo(processSpreadsheetButton);
							}

							@Override
							public void onSuccess(Void result) {
								Window.alert("Imported Survey");
							}
						});
			}
		} else if (event.getSource() == importLocalButton) {
			String appletString = "<applet width='100' height='30' code=org.waterforpeople.mapping.dataexport.DataImportAppletImpl width=256 height=256 archive='exporterapplet.jar,poi-3.5-signed.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
			appletString += "<PARAM name='importType' value='SURVEY_SPREADSHEET'>";
			appletString += "</applet>";
			appletWidget.setHTML(appletString);
		}
	}

	@SuppressWarnings("unused")
	private void bindListSpreadsheets() {

		svc.listSpreadsheets(new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onFailure(Throwable caught) {

				MessageDialog errDia = new MessageDialog("Error",
						"Cannot list spreadsheets.");
				errDia.showRelativeTo(processSpreadsheetButton);
				Window.open("/authsub", "_self", "");
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
