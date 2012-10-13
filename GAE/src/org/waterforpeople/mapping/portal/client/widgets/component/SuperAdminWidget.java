/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceApplicationDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceApplicationService;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceApplicationServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.app.gwt.client.util.UploadConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.FileUploadWidget.FileUploadHandler;

import com.gallatinsystems.framework.gwt.component.MenuBasedWidget;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.gallatinsystems.gis.app.gwt.client.GISSupportConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

public class SuperAdminWidget extends MenuBasedWidget {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static UploadConstants UPLOAD_CONSTANTS = GWT
			.create(UploadConstants.class);
	private Button surveyImportButton;
	private Button offlineReportButton;
	private Button importGISButton;
	private Button uploadApkButton;
	private Panel appletPanel;
	private WidgetDialog uploadDialog;
	private FileUploadWidget uploadWidget;
	private static final String DEFAULT_APP_CODE = "fieldSurvey";
	private static final String DEFAULT_DEVICE_TYPE = "androidPhone";

	public SuperAdminWidget() {
		Panel contentPanel = new VerticalPanel();
		Grid grid = new Grid(4, 2);
		contentPanel.add(grid);
		appletPanel = new VerticalPanel();
		contentPanel.add(appletPanel);

		surveyImportButton = initButton(TEXT_CONSTANTS.importSurvey());
		offlineReportButton = initButton(TEXT_CONSTANTS.offlineReport());
		grid.setWidget(0, 0, surveyImportButton);
		grid.setWidget(0, 1,
				createDescription(TEXT_CONSTANTS.importSurveyDescription()));
		grid.setWidget(1, 0, offlineReportButton);
		grid.setWidget(1, 1,
				createDescription(TEXT_CONSTANTS.offlineReportDesc()));
		importGISButton = initButton(TEXT_CONSTANTS.importGISData());
		grid.setWidget(2, 0, importGISButton);
		grid.setWidget(2, 1,
				createDescription(TEXT_CONSTANTS.importGISDataDescriptions()));

		uploadApkButton = initButton(TEXT_CONSTANTS.uploadApp());
		grid.setWidget(3, 0, uploadApkButton);
		grid.setWidget(3, 1,
				createDescription(TEXT_CONSTANTS.uploadAppDescription()));

		initWidget(contentPanel);
	}

	@Override
	public void onClick(ClickEvent event) {
		appletPanel.clear();
		if (event.getSource() == surveyImportButton) {
			String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataImportAppletImpl width=256 height=256 archive='exporterapplet.jar,poi-3.5-signed.jar,json.jar,gdata-core-1.0.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
			appletString += "<PARAM name='importType' value='SURVEY_SPREADSHEET'>";
			appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
			appletString += "<PARAM name='criteria' value=k:="
					+ UPLOAD_CONSTANTS.apiKey() + "'>";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		} else if (event.getSource() == offlineReportButton) {
			SurveySelectionDialog surveyDia = new SurveySelectionDialog(
					new CompletionListener() {
						@Override
						public void operationComplete(boolean wasSuccessful,
								Map<String, Object> payload) {
							if (wasSuccessful
									&& payload != null
									&& payload.get(SurveySelectionDialog.SURVEY_KEY) != null) {
								String lang = (String) payload
										.get(SurveySelectionDialog.LANG_KEY);
								handleSurveySelection((Long) payload
										.get(SurveySelectionDialog.SURVEY_KEY),
										lang);
							}
						}
					}, false, null, true);
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
								String appletString = "<applet width='100' height='30' code=com.gallatinsystems.gis.app.GeometryLoader width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar,jts-1.11-serializable-indexes.jar,gt-api-2.6.5.jar,gt-main-2.6.5.jar,gt-metadata-2.6.5.jar,gt-shapefile-2.6.5.jar,gdata-core-1.0.jar'>";
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
		} else if (event.getSource() == uploadApkButton) {
			handleApkUpload();
		}

	}

	private void handleApkUpload() {
		VerticalPanel panel = new VerticalPanel();
		final TextBox versionField = new TextBox();
		panel.add(ViewUtil.formFieldPair(TEXT_CONSTANTS.version(),
				versionField, null));
		uploadWidget = new FileUploadWidget(new FileUploadHandler() {

			@Override
			public void onSubmitSuccess(SubmitCompleteEvent event,
					String fileName) {

				if (uploadWidget.hasUploaded()) {
					saveVersion(versionField.getText());
				}
			}

			@Override
			public void configureUploadMap(Map<String, String> uploadMap,
					String fileName) {
				uploadMap.put(FileUploadHandler.PATH,
						UPLOAD_CONSTANTS.apkS3Path());
				uploadMap.put(FileUploadHandler.SIG,
						UPLOAD_CONSTANTS.apkS3Sig());
				uploadMap.put(FileUploadHandler.POLICY,
						UPLOAD_CONSTANTS.apkS3Policy());
				uploadMap.put(FileUploadHandler.CONTENT_TYPE,
						UPLOAD_CONSTANTS.apkContentType());
			}

			@Override
			public boolean isReadyToUpload() {
				if (ViewUtil.isTextPopulated(versionField)) {
					return true;
				} else {
					MessageDialog dia = new MessageDialog(
							TEXT_CONSTANTS.error(),
							TEXT_CONSTANTS.versionMandatory());
					dia.showCentered();
					return false;
				}
			}
		}, "apk");
		panel.add(uploadWidget);
		uploadDialog = new WidgetDialog(TEXT_CONSTANTS.uploadApp(), panel);
		uploadDialog.showCentered();

	}

	private void saveVersion(String versionString) {
		DeviceApplicationServiceAsync devAppService = GWT
				.create(DeviceApplicationService.class);
		DeviceApplicationDto appDto = new DeviceApplicationDto();
		appDto.setAppCode(DEFAULT_APP_CODE);
		appDto.setDeviceType(DEFAULT_DEVICE_TYPE);
		appDto.setVersion(versionString);
		devAppService.save(appDto, new AsyncCallback<DeviceApplicationDto>() {

			@Override
			public void onSuccess(DeviceApplicationDto result) {
				uploadDialog.hide();
			}

			@Override
			public void onFailure(Throwable caught) {
				MessageDialog dia = new MessageDialog(TEXT_CONSTANTS.error(),
						TEXT_CONSTANTS.saveFailed());
				dia.showCentered();

			}
		});

	}

	private void handleSurveySelection(Long surveyId, String locale) {
		String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,jcommon-1.0.16.jar,jfreechart-1.0.13.jar,poi-3.7-20101029.jar,poi-ooxml-3.7-20101029.jar,poi-ooxml-schemas-3.7-20101029.jar,xbean.jar,dom4j-1.6.1.jar,gdata-core-1.0.jar'>";
		appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
		appletString += "<PARAM name='exportType' value='OFFLINE_REPORT'>";
		appletString += "<param name='java_arguments' value='-Xmx1024m'>";
		appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
		appletString += "<PARAM name='criteria' value=surveyId:=" + surveyId
				+ ">";
		appletString += "<PARAM name='options' value='locale:=" + locale;
		appletString += ";performRollup:=false";
		appletString += ";imgPrefix:=" + UPLOAD_CONSTANTS.uploadUrl()
				+ UPLOAD_CONSTANTS.imageS3Path() + "/'>";
		appletString += "</applet>";
		HTML html = new HTML();
		html.setHTML(appletString);
		appletPanel.add(html);
	}

}
