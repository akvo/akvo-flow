package com.gallatinsystems.framework.dataexport.applet;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

/**
 * simple applet to allow us to export data from google app engine
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataExportAppletImpl extends JApplet {

	private static final long serialVersionUID = 944163825066341210L;
	private static final String EXPORT_TYPE_PARAM = "exportType";
	private static final String CRITERIA_PARAM = "criteria";
	private static final String FACTORY_PARAM = "factoryClass";
	private static final String SERVER_BASE_OVERRIDE_PARAM = "serverOverride";
	private JLabel statusLabel;
	private DataImportExportFactory dataExporterFactory;

	public void init() {
		statusLabel = new JLabel();
		getContentPane().add(statusLabel);
		String type = getParameter(EXPORT_TYPE_PARAM);
		Map<String, String> criteria = parseCriteria(getParameter(CRITERIA_PARAM));
		String factoryClass = getParameter(FACTORY_PARAM);
		String serverBase = getParameter(SERVER_BASE_OVERRIDE_PARAM);
		if(serverBase == null || serverBase.trim().length()==0){
			serverBase = getCodeBase().toString();
		}
		if (factoryClass != null) {
			try {
				dataExporterFactory = (DataImportExportFactory) Class.forName(
						factoryClass).newInstance();
			} catch (Exception e) {
				System.err.println("Could not instantiate factory: "
						+ factoryClass);
				e.printStackTrace(System.err);
			}
		}
		doExport(type, criteria, serverBase);
	}

	private Map<String, String> parseCriteria(String source) {
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

	public void doExport(String type, Map<String, String> criteriaMap,
			String serverBase) {
		JFileChooser chooser = new JFileChooser();

		chooser.showSaveDialog(this);
		if (chooser.getSelectedFile() != null) {
			DataExporter exporter = dataExporterFactory.getExporter(type);
			statusLabel.setText("Exporting...");
			if (serverBase.trim().endsWith("/")) {
				serverBase = serverBase.trim().substring(0,
						serverBase.lastIndexOf("/"));
			}
			exporter.export(criteriaMap, chooser.getSelectedFile(), serverBase);
			statusLabel.setText("Export Complete");
		}

	}

}
