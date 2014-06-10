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

package com.gallatinsystems.framework.dataexport.applet;

import java.io.File;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JLabel;

import org.waterforpeople.mapping.dataexport.RawDataExporter;

/**
 * simple applet to allow us to export data from google app engine
 * 
 * @author Christopher Fagiani
 */
public class DataExportAppletImpl extends AbstractDataImportExportApplet {

    private static final long serialVersionUID = 944163825066341210L;
    private static final String EXPORT_TYPE_PARAM = "exportType";
    private static final String OPTIONS_PARAM = "options";
    private JLabel statusLabel;
    private DataImportExportFactory dataExporterFactory;
    private Boolean useTabFlag = false;

    /**
     * initializes the UI, constructs the exporter factory then invokes the export method.
     */
    public void init() {
        statusLabel = new JLabel();
        getContentPane().add(statusLabel);
        String type = getParameter(EXPORT_TYPE_PARAM);
        dataExporterFactory = getDataImportExportFactory();
        doExport(type, getConfigCriteria(), getServerBase(),
                parseCriteria(getParameter(OPTIONS_PARAM)));
    }

    /**
     * launches a JFileChooser to prompt the user to specify an output file. If the file is
     * supplied, will then invoke the export method on the exporter returned from the factory..
     * 
     * @param type
     * @param criteriaMap
     * @param serverBase
     * @param options
     */
    public void doExport(String type, Map<String, String> criteriaMap,
            String serverBase, Map<String, String> options) {
        final JFileChooser chooser = new JFileChooser();
        final String surveyId = criteriaMap.containsKey("surveyId") ? criteriaMap
                .get("surveyId") : null;
        String ext = ".xlsx";
        if ("SURVEY_FORM".equalsIgnoreCase(type)) {
            ext = ".xls";
        }
        final String fileName = type + (surveyId != null ? "-" + surveyId : "")
                + ext;

        chooser.setSelectedFile(new File(fileName));
        chooser.showSaveDialog(this);

        if (chooser.getSelectedFile() != null) {
            DataExporter exporter = dataExporterFactory.getExporter(type);
            statusLabel.setText("Exporting...");
            if (serverBase.trim().endsWith("/")) {
                serverBase = serverBase.trim().substring(0,
                        serverBase.lastIndexOf("/"));
            }
            if (options.containsKey("generateTabFormat")) {
                if (options.get("generateTabFormat").trim() != null
                        && !options.get("generateTabFormat").trim().equalsIgnoreCase(""))
                    useTabFlag = Boolean.parseBoolean(options.get("generateTabFormat"));
            }
            if (type.equalsIgnoreCase("RAW_DATA") && useTabFlag) {
                RawDataExporter rde = new RawDataExporter();
                rde.export(criteriaMap, chooser.getSelectedFile(), serverBase, options);
            } else {
                exporter.export(criteriaMap, chooser.getSelectedFile(),
                        serverBase, options);
            }
            statusLabel.setText("Export Complete");
        }
    }
}
