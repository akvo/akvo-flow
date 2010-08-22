package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.util.Map;

/**
 * interface for any importer to be run via the import applet
 * 
 * @author Christopher Fagiani
 * 
 */
public interface DataImporter {

	public Map<String,String> validate(File file);

	public void executeImport(File file, String serverBase);
}
