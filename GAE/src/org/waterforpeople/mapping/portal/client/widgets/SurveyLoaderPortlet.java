package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
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

	private static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);
	public static final String NAME = TEXT_CONSTANTS.surveyLoaderPortletTitle();
	public static final String DESCRIPTION = TEXT_CONSTANTS.surveyLoaderPortletDescription();
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
		importLocalButton = new Button(TEXT_CONSTANTS.importLocalSheet());
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
										TEXT_CONSTANTS.error(),TEXT_CONSTANTS.errorTracePrefix()+" "+caught.getLocalizedMessage());
								errDia.showRelativeTo(processSpreadsheetButton);
							}

							@Override
							public void onSuccess(Void result) {
								Window.alert(TEXT_CONSTANTS.importComplete());
							}
						});
			}
		} else if (event.getSource() == importLocalButton) {
			String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataImportAppletImpl width=256 height=256 archive='exporterapplet.jar,poi-3.5-signed.jar,common-applet-1.0-SNAPSHOT.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
			appletString += "<PARAM name='importType' value='SURVEY_SPREADSHEET'>";
			appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";			
			appletString += "</applet>";
			appletWidget.setHTML(appletString);
		}
	}

	@SuppressWarnings("unused")
	private void bindListSpreadsheets() {

		svc.listSpreadsheets(new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onFailure(Throwable caught) {

				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.error(),TEXT_CONSTANTS.errorTracePrefix()+" "+caught.getLocalizedMessage());
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
