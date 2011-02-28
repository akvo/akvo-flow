package org.waterforpeople.mapping.dataexport;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDto;
import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDtoResponse;
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
	private VelocityEngine engine;

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	ClassLoader cl = null;

	public void init() {
		cl = this.getClass().getClassLoader();
		engine = new VelocityEngine();
		engine.setProperty("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.NullLogChute");
		try {
			engine.init();
		} catch (Exception e) {
			System.out.println("Could not initialize velocity" + e);
		}
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

		private void processFile(String fileName, ArrayList<String> countryList)
				throws Exception {
			okButton.setEnabled(false);
			System.out.println("Calling GenerateDocument");
			VelocityContext context = new VelocityContext();
			File f = new File(fileName);
			if (!f.exists()) {
				f.createNewFile();
			}
			ZipOutputStream zipOut = null;
			ByteArrayOutputStream bos = null;
			try {

				bos = new ByteArrayOutputStream();
				zipOut = new ZipOutputStream(new FileOutputStream(fileName));
				zipOut.setLevel(ZipOutputStream.DEFLATED);
				ZipEntry entry = new ZipEntry("ap.kml");
				zipOut.putNextEntry(entry);

				zipOut.write(mergeContext(context, "template/DocumentHead.vm")
						.getBytes("UTF-8"));
				for (String countryCode : countryList) {
					int i = 0;
					String cursor = null;
					PlacemarkDtoResponse pdr = BulkDataServiceClient
							.fetchPlacemarks(countryCode, serverBase, cursor);
					if (pdr != null) {
						cursor = pdr.getCursor();
						List<PlacemarkDto> placemarkDtoList = pdr.getDtoList();
						System.out.println("Starting to process: "
								+ countryCode);
						writePlacemark(placemarkDtoList, zipOut);
						System.out.println("processed: " + countryCode + " : "
								+ i);
						while (cursor != null) {
							pdr = BulkDataServiceClient.fetchPlacemarks(
									countryCode, serverBase, cursor);
							if (pdr != null) {
								if (pdr.getCursor() != null)
									cursor = pdr.getCursor();
								else
									cursor = null;
								placemarkDtoList = pdr.getDtoList();
								System.out.println("Starting to process: "
										+ countryCode);
								writePlacemark(placemarkDtoList, zipOut);
								System.out
										.println("Fetching next set of records for: "
												+ countryCode + " : " + i++);
							} else {
								break;
							}
						}
					}
				}
				zipOut.write(mergeContext(context, "template/DocumentFooter.vm")
						.getBytes("UTF-8"));
				zipOut.closeEntry();
				zipOut.close();
				System.out.println("Finished Writing File");
			} catch (Exception ex) {
				System.out.println(ex + " " + ex.getMessage() + " ");
				ex.printStackTrace(System.out);
			}

			status.setText("Completed writing kml to " + fileName);
			okButton.setEnabled(true);
		}

		private void writePlacemark(List<PlacemarkDto> placemarkDtoList,
				ZipOutputStream zipOut) throws Exception {
			if (placemarkDtoList != null) {
				for (PlacemarkDto pm : placemarkDtoList) {
					if (pm != null) {
						if (pm.getCollectionDate() != null
								&& pm.getLatitude() != null
								&& pm.getLatitude() != 0
								&& pm.getLongitude() != null
								&& pm.getLongitude() != 0) {
							VelocityContext vc = new VelocityContext();
							vc.put("timestamp", pm.getCollectionDate());
							vc.put("pinStyle", pm.getPinStyle());
							vc.put("balloon", pm.getPlacemarkContents());
							vc.put("longitude", pm.getLongitude());
							vc.put("latitude", pm.getLatitude());
							vc.put("altitude", pm.getAltitude());
							vc.put("communityCode", pm.getCommunityCode());
							String placemark = mergeContext(vc,
									"template/PlacemarksNewLook.vm");
							zipOut.write(placemark.getBytes("UTF-8"));
						}
					}
				}
			}
		}

		private String path = null;

		public void actionPerformed(ActionEvent e) {
			boolean isValid = true;
			if (e.getSource() == cancelButton) {
				cancelled = true;
			} else if (e.getSource() == selectFileButton) {
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				fc.setAcceptAllFileFilterUsed(false);
				int returnVal = fc.showOpenDialog(InputDialog.this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fc.getCurrentDirectory();
					path = file.getPath();
				} else {

				}

			} else if (e.getSource() == okButton) {
				try {
					String filelocation = path + "/kmloutput.kmz";
					System.out.println("File to save to: " + filelocation);
					// Temp
					ArrayList<String> countryList = new ArrayList<String>();
					countryList.add("MW");
					countryList.add("RW");
					countryList.add("BO");
					countryList.add("PE");
					countryList.add("GT");
					countryList.add("IN");
					countryList.add("NI");
					countryList.add("SV");
					countryList.add("LR");
					countryList.add("HT");
					countryList.add("ID");
					countryList.add("SD");
					countryList.add("NG");
					countryList.add("EC");
					processFile(filelocation, countryList);
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

		public void setStatusText(String text) {
			status.setText(text);
		}
	}

	/**
	 * merges a hydrated context with a template identified by the templateName
	 * passed in.
	 * 
	 * @param context
	 * @param templateName
	 * @return
	 * @throws Exception
	 */
	private String mergeContext(VelocityContext context, String templateName)
			throws Exception {
		String templateContents = loadResourceAsString(templateName);
		StringWriter writer = new StringWriter();
		Velocity.evaluate(context, writer, "mystring", templateContents);
		return writer.toString();
	}

	private String loadResourceAsString(String resourceName) throws Exception {
		InputStream in = cl.getResourceAsStream(resourceName);
		String resourceContents = readInputStreamAsString(in);
		return resourceContents;
	}

	public static String readInputStreamAsString(InputStream in)
			throws IOException {

		BufferedInputStream bis = new BufferedInputStream(in);
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		int result = bis.read();
		while (result != -1) {
			byte b = (byte) result;
			buf.write(b);
			result = bis.read();
		}
		return buf.toString();
	}
}
