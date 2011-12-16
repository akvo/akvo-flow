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

	/**
	 * exports the data to a file specified by the fileName parameter.
	 * 
	 * @param criteria
	 * @param fileName
	 * @param serverBase
	 * @param options
	 */
	public void export(Map<String, String> criteria, File fileName, String serverBase, Map<String,String> options);
}
