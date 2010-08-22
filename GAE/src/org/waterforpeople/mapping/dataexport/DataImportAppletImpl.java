package org.waterforpeople.mapping.dataexport;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextPane;

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
			Map<String, String> errorMap = importer.validate(chooser
					.getSelectedFile());
			if (errorMap.size() == 0) {
				if (serverBase.trim().endsWith("/")) {
					serverBase = serverBase.trim().substring(0,
							serverBase.lastIndexOf("/"));
				}
				importer.executeImport(chooser.getSelectedFile(), serverBase);
				statusLabel.setText("Import Complete");
			} else {
				statusLabel.setText("Vailidation Failed");
				StringBuilder builder = new StringBuilder();
				builder.append("The survey has the following errors:\n");
				for (Entry<String, String> entry : errorMap.entrySet()) {
					builder.append("Row ").append(entry.getKey()).append(": ")
							.append(entry.getValue()).append("\n\n");
				}
				final JDialog dia = new JDialog();
				dia.setTitle("Validation Failure");
				final JTextPane text = new JTextPane();
				text.setEditable(false);
				text.setText(builder.toString());
				dia.getContentPane().setLayout(new BorderLayout());
				dia.getContentPane().add(text, BorderLayout.CENTER);
				dia.setSize(400, 400);
				JButton okButton = new JButton("Ok");
				okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						dia.setVisible(false);
						text.setText("");
					}
				});
				dia.getContentPane().add(okButton, BorderLayout.SOUTH);
				dia.setVisible(true);			
			}

		}

	}
}
