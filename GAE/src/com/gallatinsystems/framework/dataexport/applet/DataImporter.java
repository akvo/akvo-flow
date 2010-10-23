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

	public Map<Integer,String> validate(File file);

	public void executeImport(File file, String serverBase);
}
