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
 * 
 */
public class SurveyDataImportExportFactory implements DataImportExportFactory {

	private static final Map<String, Class<? extends DataExporter>> EXPORTER_MAP = new HashMap<String, Class<? extends DataExporter>>() {
		private static final long serialVersionUID = 6562869574473763867L;

		{
			put("ACCESS_POINT", AccessPointExporter.class);
			put("SURVEY_SUMMARY", SurveySummaryExporter.class);
			put("RAW_DATA", GraphicalSurveySummaryExporter.class);
			put("SURVEY_FORM", SurveyFormExporter.class);
			put("GRAPHICAL_SURVEY_SUMMARY",GraphicalSurveySummaryExporter.class);
		}
	};
	
	private static final Map<String, Class<? extends DataImporter>> IMPORTER_MAP = new HashMap<String, Class<? extends DataImporter>>() {
		private static final long serialVersionUID = 6562869574473763867L;

		{
			put("SURVEY_SPREADSHEET", SurveySpreadsheetImporter.class);
			put("RAW_DATA", RawDataSpreadsheetImporter.class);
			put("FIXED_FORMAT",FixedFormatRawDataImporter.class);
		}
	};

	public DataExporter getExporter(String type) {
		Class<? extends DataExporter> exporterClass = EXPORTER_MAP.get(type);
		if (exporterClass != null) {
			try {
				return (DataExporter) exporterClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Could not initilaize constructor");
			}
		} else {
			throw new RuntimeException("Unknown Exporter Type: " + type);
		}
	}
	
	

	public DataImporter getImporter(String type) {
		Class<? extends DataImporter> importerClass = IMPORTER_MAP.get(type);
		if (importerClass != null) {
			try {
				return (DataImporter) importerClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Could not initilaize constructor");
			}
		} else {
			throw new RuntimeException("Unknown Importer Type: " + type);
		}
	}
}
