package org.waterforpeople.mapping.dataexport;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeMap;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RawDataSpreadsheetImportApplet extends JApplet implements Runnable {
	
	private static final long serialVersionUID = 1555395969037695230L;
	private static final String SERVER_PATH = "/rawdataimport?action=";
	private JLabel statusLabel;

	private String date;
	private String country;
	private String serverBase;

	String surveyId = null;

	public void init() {

		System.out.println("About to create Panel got surveyId: " + surveyId);
		statusLabel = new JLabel();
		getContentPane().add(statusLabel);
		//TODO: hack for testing only
		if (getParameter("surveyId") != null){
			surveyId = getParameter("surveyId");
			serverBase = getCodeBase().toString();
			if (serverBase.trim().endsWith("/")) {
				serverBase = serverBase.trim().substring(0,
						serverBase.lastIndexOf("/"));
			}
			System.out.println("ServerBase: " + serverBase);
			InputDialog dia = new InputDialog();
			if (!dia.isCancelled()) {
				if ( surveyId != null ) {
					Thread worker = new Thread(this);
					worker.start();
				} else {
					statusLabel.setText("Applet misconfigured");
				}
			} else {
				statusLabel.setText("Cancelled");
			}
		}else{
			statusLabel.setText("Problem getting surveyId from page.  Cannot continue");
		}
		
	}

	@Override
	public void run() {

	}

	private String invokeRemoteMethod(String queryString) throws Exception {
		URL url = new URL(serverBase + SERVER_PATH + queryString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder builder = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		reader.close();
		return builder.toString();
	}

	/**
	 * Private class to handle updating of the UI thread from our worker thread
	 */
	private class StatusUpdater implements Runnable {

		private String status;

		public StatusUpdater(String val) {
			status = val;
		}

		public void run() {
			statusLabel.setText(status);
		}
	}

	private class InputDialog extends JDialog implements ActionListener {

		private static final long serialVersionUID = -2875321125734363515L;
		private JButton selectFileButton;
		private JButton okButton;
		private final JFileChooser fc = new JFileChooser();
		private JButton cancelButton;
		private JLabel status;
		private boolean cancelled;
		private TreeMap<String, Long> surveyMap = null;
		private File file;

		public InputDialog() {
			super();
			System.out.println("Inside InputDialog");
			selectFileButton = new JButton("Select XLS file");
			okButton = new JButton("Ok");
			cancelButton = new JButton("Cancel");
			status = new JLabel();

			JPanel contentPane = new JPanel(new GridLayout(5, 2, 10, 10));
			contentPane.add(selectFileButton);			
			contentPane.add(okButton);
			contentPane.add(cancelButton);
			contentPane.add(status);
			setContentPane(contentPane);
			cancelButton.addActionListener(this);
			okButton.addActionListener(this);
			selectFileButton.addActionListener(this);
			setSize(300, 200);
			setTitle("Choose Raw Data File");
			setModal(true);
			setVisible(true);
		}

		private TreeMap<String, Long> surveyGroupMap = new TreeMap<String, Long>();
		private SurveyReplicationImporter sri = null;

		private void processFile() {
			okButton.setEnabled(false);
			RawDataSpreadsheetImporter rdsi = new RawDataSpreadsheetImporter();
			rdsi.setSurveyId(new Long(surveyId));
			rdsi.executeImport(file, serverBase);
			status.setText("Completed Import of Raw Data.");
			okButton.setEnabled(true);
		}

		public void actionPerformed(ActionEvent e) {
			boolean isValid = true;
			if (e.getSource() == cancelButton) {
				cancelled = true;
				setVisible(false);
			} else if (e.getSource() == selectFileButton) {
				int returnVal = fc.showOpenDialog(InputDialog.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
				} 
			} else if (e.getSource() == okButton) {
				processFile();
			}
			if (isValid) {
				//setVisible(false);
			}
		}

		public boolean isCancelled() {
			return cancelled;
		}
	}

}
