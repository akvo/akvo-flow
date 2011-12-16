package com.gallatinsystems.framework.dataexport.applet;

import java.io.File;
import java.util.Map;

/**
 * interface for any importer to be run via the import applet
 * 
 * @author Christopher Fagiani
 * 
 */
public interface DataImporter {

	/**
	 * validates the format of the import file
	 * 
	 * @param file
	 * @return - map of error messages
	 */
	public Map<Integer, String> validate(File file);

	/**
	 * executes the import by reading from file and calling methods on the server.
	 * 
	 * @param file
	 * @param serverBase
	 * @param criteria
	 */
	public void executeImport(File file, String serverBase,
			Map<String, String> criteria);

}
