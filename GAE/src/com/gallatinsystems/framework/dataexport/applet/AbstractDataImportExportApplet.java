package com.gallatinsystems.framework.dataexport.applet;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JApplet;

/**
 * Base class that provides common functionality used by the data import/export
 * applets that make use of the DataImporter and DataExporter framework
 * 
 * @author Christopher Fagiani
 * 
 */
public class AbstractDataImportExportApplet extends JApplet {

	private static final long serialVersionUID = 2018425813551644493L;
	private static final String FACTORY_PARAM = "factoryClass";
	private static final String SERVER_BASE_OVERRIDE_PARAM = "serverOverride";
	private static final String CRITERIA_PARAM = "criteria";

	/**
	 * uses the factoryClass applet parameter to instantiate a factory class.
	 * This parameter MUST be set in the applet tag
	 * 
	 * @return
	 */
	protected DataImportExportFactory getDataImportExportFactory() {
		String factoryClass = getParameter(FACTORY_PARAM);
		DataImportExportFactory dataImporterFactory = null;
		if (factoryClass != null) {
			try {
				dataImporterFactory = (DataImportExportFactory) Class.forName(
						factoryClass).newInstance();
			} catch (Exception e) {
				System.err.println("Could not instantiate factory: "
						+ factoryClass);
				e.printStackTrace(System.err);
			}
		} else {
			System.err.println("Factory must be specified");
		}
		return dataImporterFactory;
	}

	/**
	 * parses configuration criteria set in the applet tag
	 * 
	 * @param source
	 * @return
	 */
	protected Map<String, String> parseCriteria(String source) {
		Map<String, String> crit = new HashMap<String, String>();
		if (source != null) {
			StringTokenizer strTok = new StringTokenizer(source, ";");
			while (strTok.hasMoreTokens()) {
				String[] parts = strTok.nextToken().split("=");
				if (parts.length == 2) {
					crit.put(parts[0], parts[1]);
				}
			}
		}
		return crit;
	}

	/**
	 * returns the server base that hosts this applet (unless it has been
	 * overridden in the applet configuration)
	 * 
	 * @return
	 */
	protected String getServerBase() {
		String serverBase = getParameter(SERVER_BASE_OVERRIDE_PARAM);
		if (serverBase == null || serverBase.trim().length() == 0) {
			serverBase = getCodeBase().toString();
		}
		return serverBase;
	}

	/**
	 * reads the criteria applet config parameter and returns a map of
	 * key/values
	 * 
	 * @return
	 */
	protected Map<String, String> getConfigCriteria() {
		return parseCriteria(getParameter(CRITERIA_PARAM));
	}
}
