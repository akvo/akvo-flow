package org.waterforpeople.mapping.app.web;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.DeviceFiles;
import org.waterforpeople.mapping.domain.Status.StatusCode;

import services.S3Driver;

public class TaskServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(TaskServlet.class
			.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String action = req.getParameter("action");
		String fileName = req.getParameter("fileName");
		if (action != null) {
			log.info("	TaskServlet->action->" + action);
			log.info("  TaskServlet->action->" + fileName);
			if (action.equals("processFile")) {
				if (fileName != null) {
					/*
					 * Callback URL for Google TaskQueue to call to begin
					 * processing zip file 1. Get the zip file from S3 2. Open
					 * file 3. Save the meta-data to the DB 4. Explode the
					 * Images 5. Process the images (resize) 6. Save the images
					 * back to S3
					 */
					log.info("	Task->processFile");
					processFile(null);

				}
			}
		}
	}

	private static final int BUFFER = 2048;

	public static void main(String[] args) {
		TaskServlet ts = new TaskServlet();
		ts.processFile(null);
	}

	private URL url;

	private void processFile(String fileName) {
		try {
			url = new URL(
					"http://waterforpeople.s3.amazonaws.com/devicezip/wfp183214725759.zip");
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			ZipInputStream zis = new ZipInputStream(bis);
			ArrayList<String> lines = extractDataFromZip(zis);
			Object obj = marshallDataToObject(lines, AccessPoint.class
					.getName());
			saveObject(obj);
			zis.close();
		} catch (MalformedURLException e) {
			// ...
		} catch (IOException e) {
			// ...
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> extractDataFromZip(ZipInputStream zis)
			throws IOException {
		ArrayList<String> lines = new ArrayList<String>();
		String line = null;
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			log.info("Unzipping: " + entry.getName());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];
			int size;
			while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
			line = out.toString();

			if (entry.getName().endsWith("txt")) {
				String[] linesSplit = line.split("\n");
				for (String s : linesSplit) {
					lines.add(s);
				}
			} else {
				S3Driver s3 = new S3Driver();
				String[] imageParts = entry.getName().split("/");
				//comment out while testing locally
				s3.uploadFile("dru-test", imageParts[1], out.toByteArray());
				out.close();
			}
			zis.closeEntry();
		}

		return lines;
	}

	private Object marshallDataToObject(ArrayList<String> lines,
			String classname) {

		if (classname.contains("AccessPoint")) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
			java.util.Date date = new java.util.Date();
			String dateTime = dateFormat.format(date);

			Double lat = 0.0;
			Double lon = 0.0;
			Double alt = 0.0;

			String communityCode = null;
			String urlPhoto = "";

			for (String line : lines) {
				if (line.contains("qm1")) {
					// CommunityCode question
					String[] splitContents = line.split(",");
					communityCode = splitContents[3];
				} else if (line.contains("qm2")) {
					// image
					String[] splitContents = line.split(",");
					String[] imageParts = splitContents[3].split("/");
					urlPhoto = "http://waterforpeople.s3.amazonaws.com/images/dev/"
							+ imageParts[2];
				} else if (line.contains("qm3")) {
					// geo
					String[] splitContents = line.split(",");
					GeoCoordinate geo = extractGeoCoordinate(splitContents[3]);
					log.info(geo.toString());
					lat = geo.getLatitude();
					lon = geo.getLongitude();
					alt = geo.getAltitude();
				}
			}

			AccessPoint ap = new AccessPoint(lat, lon, alt, communityCode,
					urlPhoto, null);
			return ap;

		}

		return null;
	}

	private String getNowDateTimeFormatted() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
		java.util.Date date = new java.util.Date();
		String dateTime = dateFormat.format(date);
		return dateTime;
	}

	private void saveObject(Object obj) throws URISyntaxException {
		pm = PMF.get().getPersistenceManager();
		if (obj.getClass().equals(AccessPoint.class)) {
			log.info("got Accesspoint to save");
			pm.makePersistent(obj);
		}

		// save status of processing file to db

		DeviceFiles df = new DeviceFiles();

		df.setProcessDate(getNowDateTimeFormatted());
		df.setProcessedStatus(StatusCode.PROCESSED_NO_ERRORS);
		df.setURI(url.toURI().toString());
		pm.makePersistent(df);
		pm.close();
	}

	PersistenceManager pm = null;

	private GeoCoordinate extractGeoCoordinate(String line) {
		GeoCoordinate geo = new GeoCoordinate();
		String[] coordinates = line.split("\\|");
		geo.setLatitude(new Double(coordinates[0]));
		geo.setLongitude(new Double(coordinates[1]));
		geo.setAltitude(new Double(coordinates[2]));
		return geo;
	}

	class GeoCoordinate {
		private Double latitude;
		private Double longitude;
		private Double altitude;

		public Double getLatitude() {
			return latitude;
		}

		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}

		public Double getLongitude() {
			return longitude;
		}

		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}

		public Double getAltitude() {
			return altitude;
		}

		public void setAltitude(Double altitude) {
			this.altitude = altitude;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("GeoCoordinates:");
			sb.append("\n--Latitude: " + this.latitude);
			sb.append("\n--Longitude: " + this.longitude);
			sb.append("\n--Altitude: " + this.altitude);
			return sb.toString();
		}

	}
}
