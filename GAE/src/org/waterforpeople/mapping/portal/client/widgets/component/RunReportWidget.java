package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.Map;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * widget to the user to run any of the data export applets.
 * 
 * @author Christopher Fagiani
 * 
 */
public class RunReportWidget extends Composite implements ClickHandler {

	private static final String DESC_CSS = "description-text";
	private static final String BUTTON_CSS = "admin-button";

	private Grid grid;
	private Panel appletPanel;
	private Panel contentPanel;
	private Button apReportButton;
	private Button rawDataReportButton;
	private Button summaryReportButton;
	private Button kmlButton;
	private Button surveyFormButton;

	public RunReportWidget() {
		contentPanel = new VerticalPanel();
		grid = new Grid(5, 2);
		appletPanel = new VerticalPanel();
		contentPanel.add(grid);
		contentPanel.add(appletPanel);
		apReportButton = initButton("Access Point Report");
		grid.setWidget(0, 0, apReportButton);
		grid
				.setWidget(
						0,
						1,
						createDescription("Generates a report containing all data for Access Points in the system. Upon selection, the user may specify search criteria that will be used to filter the results."));
		kmlButton = initButton("Google Earth File");
		grid.setWidget(1, 0, kmlButton);
		grid
				.setWidget(
						1,
						1,
						createDescription("Exports a Google Earth KMZ file. The file, when opened in Google Earth, will show the location of each Access Point in the system."));
		rawDataReportButton = initButton("Raw Data Report");
		grid.setWidget(2, 0, rawDataReportButton);
		grid
				.setWidget(
						2,
						1,
						createDescription("Exports all submitted raw data for a single survey. This report will contain all responses collected for the survey."));
		summaryReportButton = initButton("Survey Summary Report");
		grid.setWidget(3, 0, summaryReportButton);
		grid
				.setWidget(
						3,
						1,
						createDescription("Exports a summary report for a single survey. This report will contain the response frequencies for each question in the survey."));
		surveyFormButton = initButton("Survey Form");
		grid.setWidget(4, 0, surveyFormButton);
		grid
				.setWidget(
						4,
						1,
						createDescription("Generates a printable form that can be used to conduct a paper-based survey."));
		initWidget(contentPanel);
	}

	/**
	 * constructs a new button, sets its style and adds this class as a click
	 * handler
	 * 
	 * @param buttonText
	 * @return
	 */
	private Button initButton(String buttonText) {
		Button button = new Button(buttonText);
		button.addClickHandler(this);
		button.setStylePrimaryName(BUTTON_CSS);
		return button;
	}

	/**
	 * constructs a new label containing the text passed in and sets the style
	 * to the description css
	 * 
	 * @param text
	 * @return
	 */
	private Label createDescription(String text) {
		Label desc = new Label();
		desc.setStylePrimaryName(DESC_CSS);
		desc.setText(text);
		return desc;
	}

	@Override
	public void onClick(ClickEvent event) {
		appletPanel.clear();
		if (event.getSource() == kmlButton) {
			String appletString = "<applet width='100' height='30' code=org.waterforpeople.mapping.dataexport.KMLApplet width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar, velocity-1.6.2-dep.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		} else if (event.getSource() == apReportButton) {
			/*
			 * String appletString =
			 * "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar'>"
			 * ; appletString +=
			 * "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar'><PARAM name='cache-version' value'1.3, 1.0'>"
			 * ; appletString +=
			 * "<PARAM name='exportType' value='ACCESS_POINT'>"; appletString +=
			 * "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>"
			 * ; AccessPointSearchCriteriaDto crit = formSearchCriteria(); if
			 * (crit != null) { appletString += "<PARAM name='criteria' value='"
			 * + crit.toDelimitedString() + "'>"; } appletString += "</applet>";
			 * HTML html = new HTML(); html.setHTML(appletString);
			 */
		} else {
			final Object eventSource = event.getSource();
			SurveySelectionDialog surveyDia = new SurveySelectionDialog(
					new CompletionListener() {
						@Override
						public void operationComplete(boolean wasSuccessful,
								Map<String, Object> payload) {
							if (wasSuccessful
									&& payload != null
									&& payload
											.get(SurveySelectionDialog.SURVEY_KEY) != null) {
								handleSurveySelection(
										eventSource,
										(Long) payload
												.get(SurveySelectionDialog.SURVEY_KEY));
							}
						}
					});
			surveyDia.showCentered();
		}
	}

	private void handleSurveySelection(Object eventSource, Long surveyId) {
		if (eventSource == rawDataReportButton) {
			String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
			appletString += "<PARAM name='exportType' value='RAW_DATA'>";
			appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
			appletString += "<PARAM name='criteria' value=surveyId=" + surveyId
					+ ">";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		} else if (eventSource == surveyFormButton) {
			String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
			appletString += "<PARAM name='exportType' value='SURVEY_FORM'>";
			appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
			appletString += "<PARAM name='criteria' value=surveyId=" + surveyId
					+ ">";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		} else if (eventSource == summaryReportButton) {
			String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
			appletString += "<PARAM name='exportType' value='SURVEY_SUMMARY'>";
			appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
			appletString += "<PARAM name='criteria' value=surveyId=" + surveyId
					+ ">";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		}

	}

}
