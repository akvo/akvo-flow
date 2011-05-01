package com.gallatinsystems.framework.dataexport.applet;


/**
 * Factory responsible for returning a DataExporter instance that is identified
 * by the key passed in.
 * 
 * @author Christopher Fagiani
 * 
 */
public interface DataImportExportFactory {
	public DataExporter getExporter(String type);
	public DataImporter getImporter(String type);
}
