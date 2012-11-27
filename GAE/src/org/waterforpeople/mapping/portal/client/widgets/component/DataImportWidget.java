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

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.app.gwt.client.util.UploadConstants;

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
	private static UploadConstants UPLOAD_CONSTANTS = GWT
			.create(UploadConstants.class);
	private Panel appletPanel;

	private Button rawDataImportButton;
	
	private Button fixedFormatImportButton;
	private Button bulkImportButton;

	public DataImportWidget() {
		Panel contentPanel = new VerticalPanel();
		Grid grid = new Grid(3, 2);
		contentPanel.add(grid);
		appletPanel = new VerticalPanel();
		contentPanel.add(appletPanel);

		rawDataImportButton = initButton(TEXT_CONSTANTS.rawDataImport());
		grid.setWidget(0, 0, rawDataImportButton);
		grid.setWidget(0, 1,
				createDescription(TEXT_CONSTANTS.rawDataImportDescription()));

	
		fixedFormatImportButton = initButton(TEXT_CONSTANTS
				.importFixedFormatFile());
		grid.setWidget(1, 0, fixedFormatImportButton);
		grid.setWidget(1, 1, createDescription(TEXT_CONSTANTS
				.importFixedFormatFileDescription()));

		bulkImportButton = initButton(TEXT_CONSTANTS.bulkImportSurveys());
		grid.setWidget(2, 0, bulkImportButton);
		grid.setWidget(
				2,
				1,
				createDescription(TEXT_CONSTANTS.bulkImportSurveysDescription()));

		initWidget(contentPanel);
	}

	@Override
	public void onClick(ClickEvent event) {
		appletPanel.clear();
		 if (event.getSource() == rawDataImportButton) {
			SurveySelectionDialog surveyDia = new SurveySelectionDialog(
					new CompletionListener() {
						@Override
						public void operationComplete(boolean wasSuccessful,
								Map<String, Object> payload) {
							if (wasSuccessful
									&& payload != null
									&& payload
											.get(SurveySelectionDialog.SURVEY_KEY) != null) {
								String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataImportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.7-20101029.jar,poi-ooxml-3.7-20101029.jar,poi-ooxml-schemas-3.7-20101029.jar,xbean.jar,dom4j-1.6.1.jar,gdata-core-1.0.jar'>";
								appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
								appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
								appletString += "<param name='java_arguments' value='-Xmx512m'>";
								appletString += "<PARAM name='importType' value='RAW_DATA'>";
								appletString += "<PARAM name='criteria' value='k:="
										+ UPLOAD_CONSTANTS.apiKey()
										+ ";surveyId:="
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
		}  else if (event.getSource() == fixedFormatImportButton) {
			SurveySelectionDialog surveyDia = new SurveySelectionDialog(
					new CompletionListener() {
						@Override
						public void operationComplete(boolean wasSuccessful,
								Map<String, Object> payload) {
							if (wasSuccessful
									&& payload != null
									&& payload
											.get(SurveySelectionDialog.SURVEY_KEY) != null) {
								String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataImportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar,gdata-core-1.0.jar'>";
								appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
								appletString += "<PARAM name='importType' value='FIXED_FORMAT'>";
								appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
								appletString += "<PARAM name='criteria' value='k:="
										+ UPLOAD_CONSTANTS.apiKey()
										+ ";surveyId:="
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
		} else if (event.getSource() == bulkImportButton) {
			String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataImportAppletImpl width=256 height=256 archive='exporterapplet.jar,gdata-core-1.0.jar'>";
			appletString += "<PARAM name='importType' value='BULK_SURVEY'>";
			appletString += "<PARAM name='selectionMode' value='dir'>";
			appletString += "<param name='java_arguments' value='-Xmx1024m'>";
			appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
			appletString += "<PARAM name='criteria' value='imagePolicy:="
					+ UPLOAD_CONSTANTS.imageS3Policy() + ";imageSig:="
					+ UPLOAD_CONSTANTS.imageS3Sig() + ";dataPolicy:="
					+ UPLOAD_CONSTANTS.surveyDataS3Policy() + ";dataSig:="
					+ UPLOAD_CONSTANTS.surveyDataS3Sig() + ";awsId:="
					+ UPLOAD_CONSTANTS.s3Id() + ";uploadBase:="
					+ UPLOAD_CONSTANTS.uploadUrl() + ";k:="
					+ UPLOAD_CONSTANTS.apiKey() + "'>";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		}
	}

}
