package org.waterforpeople.mapping.dataexport;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDto;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

public class KMLApplet extends JApplet implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -450706177231338054L;
	private static final String SERVER_PATH = "/rawdataimport?action=";
	private JLabel statusLabel;

	private String date;
	private String country;
	private String serverBase;

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	public void init() {
		statusLabel = new JLabel();
		getContentPane().add(statusLabel);
		serverBase = getCodeBase().toString();
		if (serverBase.trim().endsWith("/")) {
			serverBase = serverBase.trim().substring(0,
					serverBase.lastIndexOf("/"));
		}
		System.out.println("ServerBase: " + serverBase);
		System.out.println("updated");
		InputDialog dia = new InputDialog();
		if (!dia.isCancelled()) {

			Thread worker = new Thread(this);
			worker.start();

		} else {
			statusLabel.setText("Cancelled");
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
			selectFileButton = new JButton("Select location to save KML");
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
			setTitle("Choose location to save KMZ");
			setModal(true);
			setVisible(true);
		}

		private void processFile() throws Exception {
			okButton.setEnabled(false);
			List<PlacemarkDto> placemarkDtoList = BulkDataServiceClient.fetchPlacemarks("MW",
					serverBase);
			for (PlacemarkDto pm : placemarkDtoList) {
				System.out.println(pm.getCommunityCode());
				System.out.println(pm.getPlacemarkContents());
			}
			status.setText("Completed Import of Raw Data.");
			okButton.setEnabled(true);
		}

		public void actionPerformed(ActionEvent e) {
			boolean isValid = true;
			if (e.getSource() == cancelButton) {
				cancelled = true;
			} else if (e.getSource() == selectFileButton) {
				int returnVal = fc.showOpenDialog(InputDialog.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
				} else {

				}

			} else if (e.getSource() == okButton) {
				try {
					processFile();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			if (isValid) {
				// setVisible(false);
			}
		}

		public boolean isCancelled() {
			return cancelled;
		}
	}

}
