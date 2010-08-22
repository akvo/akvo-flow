package org.waterforpeople.mapping.dataexport;

import javax.swing.JApplet;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

/**
 * applet wrapper for data import utilities
 * 
 * @author Christopher Fagiani
 * 
 */
public class DataImportAppletImpl extends JApplet {

	private static final long serialVersionUID = -545153291195490725L;
	private JLabel statusLabel;

	public void init() {
		statusLabel = new JLabel();
		getContentPane().add(statusLabel);
		String type = getParameter("importType");
		doImport(type, getCodeBase().toString());
	}

	public void doImport(String type, String serverBase) {
		JFileChooser chooser = new JFileChooser();

		chooser.showOpenDialog(this);
		if (chooser.getSelectedFile() != null) {
			DataImporter importer = DataImporterFactory.getImporter(type);
			statusLabel.setText("Validating...");
			if (importer.validate(chooser.getSelectedFile())) {
				if (serverBase.trim().endsWith("/")) {
					serverBase = serverBase.trim().substring(0,
							serverBase.lastIndexOf("/"));
				}
				importer.executeImport(chooser.getSelectedFile(), serverBase);
			} else {
				statusLabel.setText("Vailidation Failed");
			}
			statusLabel.setText("Import Complete");
		}

	}

}
