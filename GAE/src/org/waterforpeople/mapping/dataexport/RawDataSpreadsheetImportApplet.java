package org.waterforpeople.mapping.dataexport;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;

public class RawDataSpreadsheetImportApplet extends JApplet implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1555395969037695230L;
	private static final String SERVER_PATH = "/rawdataimport?action=";
	private JLabel statusLabel;
	private String surveyId;
	private String date;
	private String country;
	private String serverBase;

	
	public void init(){
		System.out.println("About to create Panel");
		statusLabel = new JLabel();
		getContentPane().add(statusLabel);
		serverBase = getCodeBase().toString();
		if (serverBase.trim().endsWith("/")) {
			serverBase = serverBase.trim().substring(0,
					serverBase.lastIndexOf("/"));
		}
		System.out.println("ServerBase: " + serverBase);
		InputDialog dia = new InputDialog();
		if (!dia.isCancelled()) {
			if (country != null && surveyId != null && date != null) {
				Thread worker = new Thread(this);
				worker.start();
			} else {
				statusLabel.setText("Applet misconfigured");
			}
		} else {
			statusLabel.setText("Cancelled");
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
		private JComboBox surveyGroupCB;
		private JComboBox surveyNameCB;
		private JTextField surveyField;
		private JButton okButton;
		private JButton cancelButton;
		private JLabel status;
		private boolean cancelled;
		private TreeMap<String, Long> surveyMap = null;

		public InputDialog() {
			super();
			System.out.println("Inside InputDialog");
			//sri = new SurveyReplicationImporter();
			surveyGroupCB = new JComboBox();
			//setupSurveyGroupCB();
			surveyNameCB = new JComboBox();
			okButton = new JButton("Ok");
			cancelButton = new JButton("Cancel");
			status = new JLabel();

			JPanel contentPane = new JPanel(new GridLayout(5, 2, 10, 10));
			contentPane.add(new JLabel("Survey Group: "));
			contentPane.add(surveyGroupCB);
			contentPane.add(new JLabel("Survey: "));
			contentPane.add(surveyNameCB);
			contentPane.add(okButton);
			contentPane.add(cancelButton);
			contentPane.add(status);
			setContentPane(contentPane);
			cancelButton.addActionListener(this);
			okButton.addActionListener(this);
			setSize(300, 200);
			setTitle("Enter Backout Parameters");
			setModal(true);
			setVisible(true);
		}

		private TreeMap<String, Long> surveyGroupMap = new TreeMap<String, Long>();
		private SurveyReplicationImporter sri = null;

		private void setupSurveyGroupCB() {
			try {
				List<SurveyGroup> sgList = sri.fetchSurveyGroups(serverBase);
				for (SurveyGroup sgItem : sgList) {
					surveyGroupCB.addItem(sgItem.getCode());
					surveyGroupMap.put(sgItem.getCode(), sgItem.getKey()
							.getId());
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			surveyGroupCB.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent ev) {
					if (ev.getActionCommand().equals("")) {
						Long surveyGroupId = surveyGroupMap.get(((JComboBox) ev
								.getSource()).getSelectedItem());
						setupSurveyCB(surveyGroupId);
					}
				}

			});
		}

		private void setupSurveyCB(Long surveyGroupId) {
			try {
				List<Survey> surveyList = sri.fetchSurveys(surveyGroupId,
						serverBase);
				surveyMap = new TreeMap<String, Long>();
				for (Survey surveyItem : surveyList) {
					surveyNameCB.addItem(surveyItem.getCode());
					surveyMap.put(surveyItem.getCode(), surveyItem.getKey()
							.getId());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			surveyNameCB.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent ev) {
					Long surveyId = surveyMap.get(((JComboBox) ev.getSource())
							.getSelectedItem());
					enableFileBrowse();

				}

			});
		}

		private void enableFileBrowse() {

		}

		public void actionPerformed(ActionEvent e) {
			boolean isValid = true;
			if (e.getSource() == cancelButton) {
				cancelled = true;
			} else {
				surveyId = surveyField.getText().trim();

				if (surveyId == null || surveyId.length() == 0) {
					status.setText("SurveyId is missing");
					isValid = false;
				}

			}
			if (isValid) {
				setVisible(false);
			}
		}

		public boolean isCancelled() {
			return cancelled;
		}
	}

}
