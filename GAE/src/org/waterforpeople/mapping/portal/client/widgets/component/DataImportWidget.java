package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.MenuBasedWidget;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.gis.app.gwt.client.GISSupportConstants;
import com.google.gwt.core.client.GWT;
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

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private Panel appletPanel;
	private Button surveyImportButton;
	private Button rawDataImportButton;
	private Button importGISButton;
	private Button fixedFormatImportButton;

	public DataImportWidget() {
		Panel contentPanel = new VerticalPanel();
		Grid grid = new Grid(4, 2);
		contentPanel.add(grid);
		appletPanel = new VerticalPanel();
		contentPanel.add(appletPanel);
		surveyImportButton = initButton(TEXT_CONSTANTS.importSurvey());
		grid.setWidget(0, 0, surveyImportButton);
		grid.setWidget(0, 1,
				createDescription(TEXT_CONSTANTS.importSurveyDescription()));
		rawDataImportButton = initButton(TEXT_CONSTANTS.rawDataImport());
		grid.setWidget(1, 0, rawDataImportButton);
		grid.setWidget(1, 1,
				createDescription(TEXT_CONSTANTS.rawDataImportDescription()));

		importGISButton = initButton(TEXT_CONSTANTS.importGISData());
		grid.setWidget(2, 0, importGISButton);
		grid.setWidget(2, 1,
				createDescription(TEXT_CONSTANTS.importGISDataDescriptions()));

		fixedFormatImportButton = initButton(TEXT_CONSTANTS
				.importFixedFormatFile());
		grid.setWidget(3, 0, fixedFormatImportButton);
		grid.setWidget(3, 1, createDescription(TEXT_CONSTANTS
				.importFixedFormatFileDescription()));
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
								String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataImportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar'>";
								appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
								appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
								appletString += "<PARAM name='importType' value='RAW_DATA'>";
								appletString += "<PARAM name='criteria' value=surveyId="
										+ payload
												.get(SurveySelectionDialog.SURVEY_KEY)
										+ ">";
								appletString += "</applet>";
								HTML html = new HTML();
								html.setHTML(appletString);
								appletPanel.add(html);
							}
						}
					});
			surveyDia.showCentered();
		} else if (event.getSource() == importGISButton) {
			GISSetupDialog gisDia = new GISSetupDialog(
					new CompletionListener() {
						@Override
						public void operationComplete(boolean wasSuccessful,
								Map<String, Object> payload) {
							if (wasSuccessful
									&& payload != null
									&& payload
											.get(GISSupportConstants.COORDINATE_SYSTEM_TYPE_PARAM) != null) {
								String appletString = "<applet width='100' height='30' code=com.gallatinsystems.gis.app.GeometryLoader width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar,jts-1.11-serializable-indexes.jar,gt-api-2.6.5.jar,gt-main-2.6.5.jar,gt-metadata-2.6.5.jar,gt-shapefile-2.6.5.jar'>";
								appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
								appletString += "<PARAM name='"
										+ GISSupportConstants.COORDINATE_SYSTEM_TYPE_PARAM
										+ "' value='"
										+ payload
												.get(GISSupportConstants.COORDINATE_SYSTEM_TYPE_PARAM)
										+ "'>";
								appletString += "<PARAM name='"
										+ GISSupportConstants.CENTRAL_MERIDIAN_PARAM
										+ "' value='"
										+ payload
												.get(GISSupportConstants.CENTRAL_MERIDIAN_PARAM)
										+ "'>";
								appletString += "<PARAM name='"
										+ GISSupportConstants.COUNTRY_CODE_PARAM
										+ "' value='"
										+ payload
												.get(GISSupportConstants.COUNTRY_CODE_PARAM)
										+ "'>";
								appletString += "<PARAM name='"
										+ GISSupportConstants.GIS_FEATURE_TYPE_PARAM
										+ "' value='"
										+ payload
												.get(GISSupportConstants.GIS_FEATURE_TYPE_PARAM)
										+ "'>";
								appletString += "<PARAM name='"
										+ GISSupportConstants.UTM_ZONE_PARAM
										+ "' value='"
										+ payload
												.get(GISSupportConstants.UTM_ZONE_PARAM)
										+ "'>";
								appletString += "</applet>";
								HTML html = new HTML();
								html.setHTML(appletString);
								appletPanel.add(html);
							}
						}
					});
			gisDia.showCentered();
		} else if (event.getSource() == fixedFormatImportButton) {
			SurveySelectionDialog surveyDia = new SurveySelectionDialog(
					new CompletionListener() {
						@Override
						public void operationComplete(boolean wasSuccessful,
								Map<String, Object> payload) {
							if (wasSuccessful
									&& payload != null
									&& payload
											.get(SurveySelectionDialog.SURVEY_KEY) != null) {
								String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataImportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar'>";
								appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
								appletString += "<PARAM name='importType' value='FIXED_FORMAT'>";
								appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
								appletString += "<PARAM name='criteria' value=surveyId="
										+ payload
												.get(SurveySelectionDialog.SURVEY_KEY)
										+ ">";
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
