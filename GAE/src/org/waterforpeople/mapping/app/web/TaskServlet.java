package org.waterforpeople.mapping.app.web;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.TaskRequest;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.ProcessingAction;
import org.waterforpeople.mapping.domain.Status.StatusCode;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.helper.AccessPointHelper;
import org.waterforpeople.mapping.helper.GeoRegionHelper;

import services.S3Driver;

import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.image.GAEImageAdapter;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

public class TaskServlet extends AbstractRestApiServlet {

	private static final String DEVICE_FILE_PATH = "http://waterforpeople.s3.amazonaws.com/devicezip/";
	private static final String REGION_FLAG = "regionFlag=true";
	private static final long serialVersionUID = -2607990749512391457L;
	private static final Logger log = Logger.getLogger(TaskServlet.class
			.getName());
	private AccessPointHelper aph;
	private SurveyInstanceDAO siDao;

	public TaskServlet() {
		aph = new AccessPointHelper();
		siDao = new SurveyInstanceDAO();
	}

	private ArrayList<SurveyInstance> processFile(String fileName,
			String phoneNumber, String checksum, Integer offset) {
		ArrayList<SurveyInstance> surveyInstances = new ArrayList<SurveyInstance>();

		try {
			BaseDAO<DeviceFiles> dfDao = new BaseDAO<DeviceFiles>(DeviceFiles.class);
			URL url = new URL(DEVICE_FILE_PATH + fileName);
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			ZipInputStream zis = new ZipInputStream(bis);

			DeviceFiles deviceFile = new DeviceFiles();
			deviceFile.setProcessDate(getNowDateTimeFormatted());
			deviceFile.setProcessedStatus(StatusCode.IN_PROGRESS);
			deviceFile.setURI(url.toURI().toString());
			deviceFile.setPhoneNumber(phoneNumber);
			deviceFile.setChecksum(checksum);
			Date collectionDate = new Date();

			ArrayList<String> unparsedLines = null;
			try {
				unparsedLines = extractDataFromZip(zis);
			} catch (IOException iex) {
				// Error unzipping the response file
				deviceFile.setProcessedStatus(StatusCode.ERROR_INFLATING_ZIP);
			}

			if (unparsedLines != null && unparsedLines.size() > 0) {
				if (REGION_FLAG.equals(unparsedLines.get(0))) {
					unparsedLines.remove(0);
					GeoRegionHelper grh = new GeoRegionHelper();
					grh.processRegionsSurvey(unparsedLines);
				} else {

					int lineNum = offset;
					String curId = null;
					while (lineNum < unparsedLines.size()) {
						String[] parts = unparsedLines.get(lineNum).split(",");
						if (parts.length >= 2) {
							if (curId == null) {
								curId = parts[1];
							} else {
								// if this isn't the first time through and
								// we are seeing a new id, break since we'll
								// process that in another call
								if (!curId.equals(parts[1])) {
									break;
								}
							}
						}
						lineNum++;
					}

					Long userID = 1L;
					SurveyInstance inst = siDao.save(collectionDate,
							deviceFile, userID,
							unparsedLines.subList(offset, lineNum));
					surveyInstances.add(inst);

					if (lineNum < unparsedLines.size()) {
						// if we haven't processed everything yet, invoke a
						// new service
						Queue queue = QueueFactory.getDefaultQueue();
						queue.add(url("/app_worker/task")
								.param("action", "processFile")
								.param("fileName", fileName)
								.param("offset", lineNum + ""));
					}
				}
			}
			dfDao.save(deviceFile);
			zis.close();
			
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not process data file", e);
		}

		return surveyInstances;
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

	private String getNowDateTimeFormatted() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
		java.util.Date date = new java.util.Date();
		String dateTime = dateFormat.format(date);
		return dateTime;
	}

	private ProcessingAction dispatch(String surveyKey) {
		ProcessingAction pa = new ProcessingAction();

		pa.setAction("addAccessPoint");
		pa.setDispatchURL("/worker/task");
		pa.addParam("surveyId", surveyKey);
		return pa;
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new TaskRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest request) throws Exception {
		RestResponse response = new RestResponse();
		TaskRequest taskReq = (TaskRequest) request;
		if (TaskRequest.PROCESS_FILE_ACTION.equalsIgnoreCase(taskReq
				.getAction())) {
			ingestFile(taskReq);
		} else if (TaskRequest.ADD_ACCESS_POINT_ACTION.equalsIgnoreCase(taskReq
				.getAction())) {
			addAccessPoint(taskReq);
		}
		return response;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// no-op
	}

	private void addAccessPoint(TaskRequest req) {
		Long surveyInstanceId = req.getSurveyId();
		log.info("Received Task Queue calls for surveyInstanceId: "
				+ surveyInstanceId);

		aph.processSurveyInstance(surveyInstanceId.toString());
	}

	/**
	 * handles the callback from the device indicating that a new data file is
	 * available. This method will call processFile to retrieve the file and
	 * persist the data to the data store it will then add access points for
	 * each water point in the survey responses.
	 * 
	 * @param req
	 */
	@SuppressWarnings("unchecked")
	private void ingestFile(TaskRequest req) {
		if (req.getFileName() != null) {
			log.info("	Task->processFile");
			ArrayList<SurveyInstance> surveyInstances = processFile(
					req.getFileName(), req.getPhoneNumber(), req.getChecksum(),
					req.getOffset());
			Queue summQueue = QueueFactory.getQueue("dataSummarization");
			for (SurveyInstance instance : surveyInstances) {
				ProcessingAction pa = dispatch(instance.getKey().getId() + "");
				TaskOptions options = url(pa.getDispatchURL());
				Iterator it = pa.getParams().keySet().iterator();
				while (it.hasNext()) {
					options.param("key", (String) it.next());
				}
				log.info("Received Task Queue calls for surveyInstanceKey: "
						+ instance.getKey().getId() + "");
				aph.processSurveyInstance(instance.getKey().getId() + "");
				summQueue.add(url("/app_worker/datasummarization").param(
						"objectKey", instance.getKey().getId() + "").param(
						"type", "SurveyInstance"));
			}
		}
	}

}
