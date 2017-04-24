/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.dataexport;

import java.util.HashMap;
import java.util.Map;

import com.gallatinsystems.framework.dataexport.applet.DataExporter;
import com.gallatinsystems.framework.dataexport.applet.DataImportExportFactory;
import com.gallatinsystems.framework.dataexport.applet.DataImporter;

/**
 * Factory to create a DataExporter or DataImporter instance based on the type passed in
 *
 * @author Christopher Fagiani
 */
public class SurveyDataImportExportFactory implements DataImportExportFactory {

    private static final Map<String, Class<? extends DataExporter>> EXPORTER_MAP = new HashMap<String, Class<? extends DataExporter>>() {
        private static final long serialVersionUID = 6562869574473763867L;

        {
            put("SURVEY_SUMMARY", SurveySummaryExporter.class);
            put("RAW_DATA", GraphicalSurveySummaryExporter.class);
            put("SURVEY_FORM", SurveyFormExporter.class);
            put("GRAPHICAL_SURVEY_SUMMARY",
                    GraphicalSurveySummaryExporter.class);
            put("OFFLINE_REPORT", OfflineExport.class);
        }
    };

    private static final Map<String, Class<? extends DataImporter>> IMPORTER_MAP = new HashMap<String, Class<? extends DataImporter>>() {
        private static final long serialVersionUID = 6562869574473763867L;

        {
            put("SURVEY_SPREADSHEET", SurveySpreadsheetImporter.class);
            put("RAW_DATA", RawDataSpreadsheetImporter.class);
            put("BULK_SURVEY", SurveyBulkUploader.class);
        }
    };

    @Override
    public DataExporter getExporter(String type) {
        Class<? extends DataExporter> exporterClass = EXPORTER_MAP.get(type);
        if (exporterClass != null) {
            try {
                return exporterClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Could not initilaize constructor");
            }
        } else {
            throw new RuntimeException("Unknown Exporter Type: " + type);
        }
    }

    @Override
    public DataImporter getImporter(String type) {
        Class<? extends DataImporter> importerClass = IMPORTER_MAP.get(type);
        if (importerClass != null) {
            try {
                return importerClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Could not initilaize constructor");
            }
        } else {
            throw new RuntimeException("Unknown Importer Type: " + type);
        }
    }
}
