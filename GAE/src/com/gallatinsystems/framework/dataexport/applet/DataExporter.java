package com.gallatinsystems.framework.dataexport.applet;

import java.io.File;
import java.util.Map;

/**
 * Interface all classes that perform an export should implement so it can be
 * used with the DataExporterFactory
 * 
 * @author Christopher Fagiani
 * 
 */
public interface DataExporter {

	public void export(Map<String, String> criteria, File fileName, String serverBase, Map<String,String> options);
}
