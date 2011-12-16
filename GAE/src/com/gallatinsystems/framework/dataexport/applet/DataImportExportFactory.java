package com.gallatinsystems.framework.dataexport.applet;

/**
 * Factory responsible for returning a DataExporter instance that is identified
 * by the key passed in.
 * 
 * @author Christopher Fagiani
 * 
 */
public interface DataImportExportFactory {
	/**
	 * gets a DataExporter corresponding to the type passed in
	 * 
	 * @param type
	 * @return - DataExporter instance or null if not found
	 */
	public DataExporter getExporter(String type);

	/**
	 * gets a DataImporter corresponding to the type passed in
	 * 
	 * @param type
	 * @return - DataImporter instance or null if not found
	 */
	public DataImporter getImporter(String type);
}
