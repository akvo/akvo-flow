package org.waterforpeople.mapping.dataexport;

import java.util.HashMap;
import java.util.Map;

import com.gallatinsystems.framework.dataexport.applet.DataExporter;

/**
 * Factory to create a DataExporter instance based on the ExportType passed in
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataExporterFactory {

	private static final Map<String, Class<? extends DataExporter>> EXPORTER_MAP = new HashMap<String, Class<? extends DataExporter>>() {
		private static final long serialVersionUID = 6562869574473763867L;

		{
			put("ACCESS_POINT", AccessPointExporter.class);
			put("SURVEY_SUMMARY", SurveySummaryExporter.class);
			put("RAW_DATA", RawDataExporter.class);
		}
	};

	public static DataExporter getExporter(String type) {
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
}
