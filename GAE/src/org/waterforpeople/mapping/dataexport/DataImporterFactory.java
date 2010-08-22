package org.waterforpeople.mapping.dataexport;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for initializing data importers
 * 
 * @author Christopher Fagiani
 *
 */
public class DataImporterFactory {
	private static final Map<String, Class<? extends DataImporter>> IMPORTER_MAP = new HashMap<String, Class<? extends DataImporter>>() {
		private static final long serialVersionUID = 6562869574473763867L;

		{
			put("SURVEY_SPREADSHEET", SurveySpreadsheetImporter.class);
		}
	};

	public static DataImporter getImporter(String type) {
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
