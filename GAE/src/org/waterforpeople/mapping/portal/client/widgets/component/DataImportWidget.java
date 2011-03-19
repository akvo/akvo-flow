package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.Map;

import com.gallatinsystems.framework.gwt.component.MenuBasedWidget;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Widget that can be used to launch any of the Data Import applets
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataImportWidget extends MenuBasedWidget {

	private Panel appletPanel;
	private Button surveyImportButton;
	private Button rawDataImportButton;

	public DataImportWidget() {
		Panel contentPanel = new VerticalPanel();
		Grid grid = new Grid(2, 2);
		contentPanel.add(grid);
		appletPanel = new VerticalPanel();
		contentPanel.add(appletPanel);
		surveyImportButton = initButton("Import Survey");
		grid.setWidget(0, 0, surveyImportButton);
		grid.setWidget(0, 1,
				createDescription("Imports a survey definition spreadsheet."));
		rawDataImportButton = initButton("Raw Data Import");
		grid.setWidget(1, 0, rawDataImportButton);
		grid
				.setWidget(
						1,
						1,
						createDescription("Imports raw data spreadsheets that were created using the Raw Data report. This is typically used to re-import data after it has been cleansed"));
		initWidget(contentPanel);
	}

	@Override
	public void onClick(ClickEvent event) {
		appletPanel.clear();
		if (event.getSource() == surveyImportButton) {
			String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataImportAppletImpl width=256 height=256 archive='exporterapplet.jar,poi-3.5-signed.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
			appletString += "<PARAM name='importType' value='SURVEY_SPREADSHEET'>";
			appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		} else if (event.getSource() == rawDataImportButton) {
			SurveySelectionDialog surveyDia = new SurveySelectionDialog(
					new CompletionListener() {
						@Override
						public void operationComplete(boolean wasSuccessful,
								Map<String, Object> payload) {
							if (wasSuccessful
									&& payload != null
									&& payload
											.get(SurveySelectionDialog.SURVEY_KEY) != null) {
								String appletString = "<applet width='100' height='30' code=org.waterforpeople.mapping.dataexport.RawDataSpreadsheetImportApplet width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar'>";
								appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
								appletString += "<PARAM name='exportType' value='SURVEY_FORM'>";
								appletString += "<PARAM name='surveyId' value='"
										+ payload
												.get(SurveySelectionDialog.SURVEY_KEY)
										+ "'>";
								appletString += "</applet>";
								HTML html = new HTML();
								html.setHTML(appletString);
								appletPanel.add(html);
							}
						}
					});
			surveyDia.showCentered();
		}
	}

}
