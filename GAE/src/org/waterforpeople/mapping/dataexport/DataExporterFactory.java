package org.waterforpeople.mapping.dataexport;

import java.util.HashMap;
import java.util.Map;

import com.gallatinsystems.framework.dataexport.DataExporter;
import com.gallatinsystems.framework.dataexport.applet.DataExportApplet;


/**
 * Factory to create a DataExporter instance based on the ExportType passed in
 * 
 * @author Christopher Fagiani
 *
 */
public class DataExporterFactory {

	private static final Map<DataExportApplet.ExportType, Class<? extends DataExporter>> EXPORTER_MAP = new HashMap<DataExportApplet.ExportType, Class<? extends DataExporter>>() {		
		private static final long serialVersionUID = 6562869574473763867L;

		{
			put(DataExportApplet.ExportType.ACCESS_POINT, AccessPointExporter.class);
		}
	};

	public static DataExporter getExporter(DataExportApplet.ExportType type) {
		Class<? extends DataExporter> exporterClass = EXPORTER_MAP.get(type);
		if (exporterClass != null) {
			try {
				return (DataExporter)exporterClass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Could not initilaize constructor");
			}
		} else {
			throw new RuntimeException("Unknown Exporter Type");
		}		
	}
}
