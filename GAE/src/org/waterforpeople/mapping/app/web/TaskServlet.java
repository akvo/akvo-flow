package org.waterforpeople.mapping.app.web;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.DeviceFiles;
import org.waterforpeople.mapping.domain.GeoCoordinates;
import org.waterforpeople.mapping.domain.ProcessingAction;
import org.waterforpeople.mapping.domain.Status.StatusCode;
import org.waterforpeople.mapping.helper.AccessPointHelper;
import org.waterforpeople.mapping.helper.GeoRegionHelper;

import services.S3Driver;

import com.gallatinsystems.image.GAEImageAdapter;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

public class TaskServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(TaskServlet.class
			.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
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
					ArrayList<String> surveyIds = processFile(fileName);
					for (String ids : surveyIds) {
						ProcessingAction pa = dispatch(ids);
						Queue queue = QueueFactory.getDefaultQueue();
						TaskOptions options = url(pa.getDispatchURL());
						Iterator it = pa.getParams().keySet().iterator();
						while (it.hasNext()) {
							options.param("key", (String) it.next());
						}
						// queue.add(options);
						log.info("Received Task Queue calls for surveyId: "
								+ ids);
						AccessPointHelper aph = new AccessPointHelper();
						aph.processSurveyInstance(new Long(ids));
					}
				}
			} else if (action.equals("addAccessPoints")) {
				Long surveyId = new Long(req.getParameter("surveyId"));
				log.info("Received Task Queue calls for surveyId: " + surveyId);
				AccessPointHelper aph = new AccessPointHelper();
				aph.processSurveyInstance(surveyId);
			}
		}
	}

	private static final int BUFFER = 2048;

	public static void main(String[] args) {
		TaskServlet ts = new TaskServlet();
		ts.processFile(null);
	}

	private URL url;

	private ArrayList<String> processFile(String fileName) {
		ArrayList<String> surveyIds = new ArrayList<String>();
		try {
			url = new URL("http://waterforpeople.s3.amazonaws.com/devicezip/"
					+ fileName);
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			ZipInputStream zis = new ZipInputStream(bis);
			ArrayList<String> unparsedLines = extractDataFromZip(zis);
			DeviceFiles deviceFile = new DeviceFiles();
			deviceFile.setProcessDate(getNowDateTimeFormatted());
			deviceFile.setProcessedStatus(StatusCode.PROCESSED_NO_ERRORS);
			deviceFile.setURI(url.toURI().toString());
			Date collectionDate = new Date();

			if (unparsedLines.get(0).equals("regionFlag=true")) {
				unparsedLines.remove(0);
				GeoRegionHelper grh  = new GeoRegionHelper();
				grh.processRegionsSurvey(unparsedLines);
			} else {

				Long userID = 1L;

				// Object obj = marshallDataToObject(lines, AccessPoint.class
				// .getName());
				// saveObject(obj);
				SurveyInstanceDAO siDAO = new SurveyInstanceDAO();

				Long surveyId = siDAO.save(collectionDate, deviceFile, userID,
						unparsedLines);
				// if survey
				surveyIds.add(surveyId.toString());
			}
			zis.close();
		} catch (MalformedURLException e) {
			// ...
		} catch (IOException e) {
			// ...
		} catch (Exception e) {
			e.printStackTrace();
		}
		return surveyIds;
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
				if (entry.getName().equals("regions.txt")) {
					lines.add("regionFlag=true");
				}
				String[] linesSplit = line.split("\n");
				for (String s : linesSplit) {
					lines.add(s);
				}
			} else {
				S3Driver s3 = new S3Driver();
				String[] imageParts = entry.getName().split("/");
				// comment out while testing locally
				try {
					// GAEImageAdapter gaeIA = new GAEImageAdapter();
					// byte[] resizedImage =
					// gaeIA.resizeImage(out.toByteArray(), 500, 500);
					// s3.uploadFile("dru-test", imageParts[1], resizedImage);
					GAEImageAdapter gaeImg = new GAEImageAdapter();
					byte[] newImage = gaeImg.resizeImage(out.toByteArray(),
							500, 500);
					s3.uploadFile("dru-test", imageParts[1], newImage);
					// add queue call to resize
					Queue queue = QueueFactory.getDefaultQueue();

					queue.add(url("imageprocessor").param("imageURL",
							imageParts[1]));
					log.info("submiting image resize for imageURL: "
							+ imageParts[1]);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				out.close();
			}
			zis.closeEntry();
		}

		return lines;
	}

	// to get rid of
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
					GeoCoordinates geo = new GeoCoordinates()
							.extractGeoCoordinate(splitContents[3]);
					log.info(geo.toString());
					lat = geo.getLatitude();
					lon = geo.getLongitude();
					alt = geo.getAltitude();
				}
			}

			/*
			 * AccessPoint ap = new AccessPoint(lat, lon, alt, communityCode,
			 * urlPhoto, null);
			 */
			// return ap;
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

	private ProcessingAction dispatch(String surveyId) {
		ProcessingAction pa = new ProcessingAction();

		HashMap params = new HashMap();

		pa.setAction("addAccessPoint");
		pa.setDispatchURL("/worker/task");
		pa.addParam("surveyId", surveyId);
		return pa;
	}

}
