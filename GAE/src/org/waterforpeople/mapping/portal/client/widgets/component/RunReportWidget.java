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

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationItemDto;
import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationService;
import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.app.gwt.client.util.UploadConstants;

import com.gallatinsystems.framework.gwt.component.MenuBasedWidget;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * widget to the user to run any of the data export applets.
 * 
 * @author Christopher Fagiani
 * 
 */
public class RunReportWidget extends MenuBasedWidget {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static UploadConstants UPLOAD_CONSTANTS = GWT
			.create(UploadConstants.class);
	private static final String TAB_RDR_FILE_FLAG = "useTabRDRFlag";

	private Grid grid;
	private Panel appletPanel;
	private Panel contentPanel;
	private Button apReportButton;
	private Button rawDataReportButton;
	private Button summaryReportButton;
	private Button comprehensiveReportButton;
	private Button kmlButton;
	private Button surveyFormButton;
	private Boolean useTabRDRFlag;

	public RunReportWidget() {
		ConfigurationServiceAsync cfgService = GWT
		.create(ConfigurationService.class);
		cfgService.getConfigurationItem(TAB_RDR_FILE_FLAG,
				new AsyncCallback<ConfigurationItemDto>() {

					@Override
					public void onFailure(Throwable caught) {
						setUseTabRDRFlag(false);
						
					}

					@Override
					public void onSuccess(ConfigurationItemDto result) {
						if (result == null || result.getValue() == null) {
							setUseTabRDRFlag(false);
						} else {
							setUseTabRDRFlag(Boolean.parseBoolean(result.getValue()));
						}
					}
				});
		contentPanel = new VerticalPanel();
		grid = new Grid(7, 2);
		appletPanel = new VerticalPanel();
		contentPanel.add(grid);
		apReportButton = initButton(TEXT_CONSTANTS.accessPointReport());
		grid.setWidget(0, 0, apReportButton);
		grid.setWidget(
				0,
				1,
				createDescription(TEXT_CONSTANTS.accessPointReportDescription()));
		kmlButton = initButton(TEXT_CONSTANTS.googleEarthFile());
		grid.setWidget(1, 0, kmlButton);
		grid.setWidget(1, 1,
				createDescription(TEXT_CONSTANTS.googleEarthFileDescription()));
		rawDataReportButton = initButton(TEXT_CONSTANTS.rawDataReport());
		grid.setWidget(2, 0, rawDataReportButton);
		grid.setWidget(2, 1,
				createDescription(TEXT_CONSTANTS.rawDataReportDescription()));
		summaryReportButton = initButton(TEXT_CONSTANTS.surveySummaryReport());
		grid.setWidget(3, 0, summaryReportButton);
		grid.setWidget(3, 1, createDescription(TEXT_CONSTANTS
				.surveySummaryReportDescription()));
		comprehensiveReportButton = initButton(TEXT_CONSTANTS
				.comprehensiveReport());
		grid.setWidget(4, 0, comprehensiveReportButton);
		grid.setWidget(4, 1, createDescription(TEXT_CONSTANTS
				.comprehensiveReportDescription()));

		surveyFormButton = initButton(TEXT_CONSTANTS.surveyForm());
		grid.setWidget(5, 0, surveyFormButton);
		grid.setWidget(5, 1,
				createDescription(TEXT_CONSTANTS.surveyFormDescription()));
		grid.setWidget(6, 0, appletPanel);
		initWidget(contentPanel);
	}

	@Override
	public void onClick(ClickEvent event) {
		appletPanel.clear();
		if (event.getSource() == kmlButton) {
			String appletString = "<applet width='100' height='30' code=org.waterforpeople.mapping.dataexport.KMLApplet width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar,velocity-1.6.2-dep.jar,gdata-core-1.0.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar, velocity-1.6.2-dep.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		} else if (event.getSource() == apReportButton) {
			AccessPointFilterDialog filterDia = new AccessPointFilterDialog(
					new CompletionListener() {

						@Override
						public void operationComplete(boolean wasSuccessful,
								Map<String, Object> payload) {
							if (wasSuccessful
									&& payload
											.get(AccessPointFilterDialog.CRITERIA_KEY) != null) {
								String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,gdata-core-1.0.jar'>";
								appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
								appletString += "<PARAM name='exportType' value='ACCESS_POINT'>";
								appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
								AccessPointSearchCriteriaDto crit = (AccessPointSearchCriteriaDto) payload
										.get(AccessPointFilterDialog.CRITERIA_KEY);
								if (crit != null) {
									appletString += "<PARAM name='criteria' value='"
											+ crit.toDelimitedString() + "'>";
								}
								appletString += "</applet>";
								HTML html = new HTML();
								html.setHTML(appletString);
								appletPanel.add(html);
							}
						}
					});
			filterDia.showCentered();
		} else {
			final Object eventSource = event.getSource();
			final Widget rollupControl;
			final CheckBox summaryBox;
			final CheckBox chartBox;
			if (eventSource == comprehensiveReportButton) {
				VerticalPanel vPanel = new VerticalPanel();
				HorizontalPanel rollupPanel = new HorizontalPanel();
				rollupPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS
						.generateSummariesByGeography()));				
				summaryBox = new CheckBox();
				summaryBox.setValue(true);
				rollupPanel.add(summaryBox);
				vPanel.add(rollupPanel);
				
				HorizontalPanel imgPanel = new HorizontalPanel();
				imgPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.omitCharts()));
				chartBox = new CheckBox();
				chartBox.setValue(false);
				imgPanel.add(chartBox);
				vPanel.add(imgPanel);
				
				rollupControl = vPanel;
			} else {
				rollupControl = null;
				summaryBox = null;
				chartBox = null;
			}
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
								handleSurveySelection(
										eventSource,
										(Long) payload
												.get(SurveySelectionDialog.SURVEY_KEY),
										summaryBox != null ? summaryBox
												.getValue() : true, lang, chartBox != null? chartBox.getValue():false);
							}
						}
					}, false, rollupControl, true);
			surveyDia.showCentered();
		}
	}

	private void handleSurveySelection(Object eventSource, Long surveyId,
			boolean doRollups, String locale, boolean omitCharts) {
		if (eventSource == rawDataReportButton) {
			String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,jcommon-1.0.16.jar,jfreechart-1.0.13.jar,poi-3.8-20120326.jar,poi-ooxml-3.8-20120326.jar,poi-ooxml-schemas-3.8-20120326.jar,xbean.jar,dom4j-1.6.1.jar,gdata-core-1.0.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
			appletString += "<PARAM name='exportType' value='RAW_DATA'>";
			appletString += "<param name='java_arguments' value='-Xmx1024m'>";
			appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
			appletString += "<PARAM name='criteria' value=surveyId:="
					+ surveyId + ">";
			appletString += "<PARAM name='options' value='exportMode:=RAW_DATA;locale:="
					+ locale
					+ ";imgPrefix:="
					+ UPLOAD_CONSTANTS.uploadUrl()
					+ UPLOAD_CONSTANTS.imageS3Path()
					+ ";"
					+ "generateTabFormat="
					+ getUseTabRDRFlag()+ "/'>";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		} else if (eventSource == surveyFormButton) {
			String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar,gdata-core-1.0.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
			appletString += "<PARAM name='exportType' value='SURVEY_FORM'>";
			appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
			appletString += "<PARAM name='criteria' value=surveyId:="
					+ surveyId + ">";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		} else if (eventSource == summaryReportButton) {
			String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,gdata-core-1.0.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
			appletString += "<PARAM name='exportType' value='SURVEY_SUMMARY'>";
			appletString += "<param name='java_arguments' value='-Xmx1024m'>";
			appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
			appletString += "<PARAM name='criteria' value=surveyId:="
					+ surveyId + ">";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		} else if (eventSource == comprehensiveReportButton) {
			String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,jcommon-1.0.16.jar,jfreechart-1.0.13.jar,poi-3.8-20120326.jar,poi-ooxml-3.8-20120326.jar,poi-ooxml-schemas-3.8-20120326.jar,xbean.jar,dom4j-1.6.1.jar,gdata-core-1.0.jar'>";
			appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
			appletString += "<PARAM name='exportType' value='GRAPHICAL_SURVEY_SUMMARY'>";
			appletString += "<param name='java_arguments' value='-Xmx1024m'>";
			appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
			appletString += "<PARAM name='criteria' value=surveyId:="
					+ surveyId + ">";
			appletString += "<PARAM name='options' value='locale:=" + locale;
			if (!doRollups) {
				appletString += ";performRollup:=false";
			}
			if(omitCharts){
				appletString+= ";nocharts:=true";
			}
			appletString += ";imgPrefix:=" + UPLOAD_CONSTANTS.uploadUrl()
					+ UPLOAD_CONSTANTS.imageS3Path() + "/'>";
			appletString += "</applet>";
			HTML html = new HTML();
			html.setHTML(appletString);
			appletPanel.add(html);
		}

	}

	public void setUseTabRDRFlag(Boolean useTabRDRFlag) {
		this.useTabRDRFlag = useTabRDRFlag;
	}

	public Boolean getUseTabRDRFlag() {
		return useTabRDRFlag;
	}

}
