package org.waterforpeople.mapping.dataexport;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDto;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.common.Constants;

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

	public void init() {
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

		final String PLACEMARK_PREAMBLE = null;
		final String PLACEMARK_CONCLUSION = null;

		private void processFile() throws Exception {
			okButton.setEnabled(false);
			String kml = generateDocument();
			PrintWriter pw = new PrintWriter("testkml.txt");
			pw.print(kml);
			if (pw != null)
				pw.close();
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
		Template t = engine.getTemplate(templateName);
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		context = null;
		return writer.toString();
	}

	public String generateDocument() {
		try {
			VelocityContext context = new VelocityContext();
			List<PlacemarkDto> placemarkDtoList = BulkDataServiceClient
					.fetchPlacemarks("MW", serverBase);
			StringBuilder sbPlacemarks = new StringBuilder();
			for (PlacemarkDto pm : placemarkDtoList) {
				VelocityContext vc = new VelocityContext();
				vc.put("timestamp", pm.getCollectionDate());
				vc.put("pinStyle", null);
				vc.put("balloon", pm.getPlacemarkContents());
				vc.put("longitude", pm.getLongitude());
				vc.put("latitude", pm.getLatitude());
				vc.put("altitude", pm.getAltitude());
				sbPlacemarks.append(mergeContext(vc, "template/PlacemarksNewLook.vm"));
			}
			context.put("template/folderContents", sbPlacemarks.toString());
			return mergeContext(context, "template/Document.vm");
		} catch (Exception ex) {
			System.out.println("SEVERE: Could create kml" + ex);
		}
		return null;
	}

}
