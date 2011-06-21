package com.gallatinsystems.framework.dataexport.applet;

import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JLabel;

/**
 * simple applet to allow us to export data from google app engine
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataExportAppletImpl extends AbstractDataImportExportApplet {

	private static final long serialVersionUID = 944163825066341210L;
	private static final String EXPORT_TYPE_PARAM = "exportType";
	private static final String OPTIONS_PARAM = "options";
	private JLabel statusLabel;
	private DataImportExportFactory dataExporterFactory;

	public void init() {
		statusLabel = new JLabel();
		getContentPane().add(statusLabel);
		String type = getParameter(EXPORT_TYPE_PARAM);
		dataExporterFactory = getDataImportExportFactory();
		doExport(type, getConfigCriteria(), getServerBase(), parseCriteria(getParameter(OPTIONS_PARAM)));
	}
	
	public void doExport(String type, Map<String, String> criteriaMap,
			String serverBase, Map<String, String> options) {
		JFileChooser chooser = new JFileChooser();

		chooser.showSaveDialog(this);
		if (chooser.getSelectedFile() != null) {
			DataExporter exporter = dataExporterFactory.getExporter(type);
			statusLabel.setText("Exporting...");
			if (serverBase.trim().endsWith("/")) {
				serverBase = serverBase.trim().substring(0,
						serverBase.lastIndexOf("/"));
			}
			exporter.export(criteriaMap, chooser.getSelectedFile(), serverBase,
					options);
			statusLabel.setText("Export Complete");
		}
	}
}
