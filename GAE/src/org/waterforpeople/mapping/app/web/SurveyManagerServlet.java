/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.web;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.app.web.dto.SurveyManagerRequest;
import org.waterforpeople.mapping.dao.DeviceFilesDao;
import org.waterforpeople.mapping.dao.SurveyContainerDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.Device.DeviceType;
import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyGroup;

public class SurveyManagerServlet extends AbstractRestApiServlet {
	private static final Logger log = Logger
			.getLogger(SurveyManagerServlet.class.getName());
	private static final long serialVersionUID = 4400244780977729721L;

	/**
	 * As this backend does *NOT* contain monitoring features capabilities, we
	 * always send this values to enable APK backwards compatibility
	 */
	private final static String IS_IN_MONITORING_GROUP = "false";
	private final static String NEW_LOCALE_SURVEYID = "null";
	private final static String UNKNOWN = "unknown";

	private DeviceDAO deviceDao;

	public SurveyManagerServlet() {
		super();
		deviceDao = new DeviceDAO();
		setMode(AbstractRestApiServlet.XML_MODE);
	}

	//Return a list all the surveys the device needs
	//use imei or phone number for lookup
	private String getSurveyForPhone(String devicePhoneNumber, String imei) {
		DeviceSurveyJobQueueDAO dsjqDAO = new DeviceSurveyJobQueueDAO();
		SurveyDAO surveyDao = new SurveyDAO();
		SurveyGroupDAO sgDao = new SurveyGroupDAO();
		Map<Long, Double> versionMap = new HashMap<Long, Double>();
		StringBuilder sb = new StringBuilder();
		for (DeviceSurveyJobQueue dsjq : dsjqDAO.get(devicePhoneNumber, imei)) {
			String surveyGroupId = "";
			String surveyGroupName = UNKNOWN;
			Double ver = versionMap.get(dsjq.getSurveyID());
			if (ver == null) {
				Survey s = surveyDao.getById(dsjq.getSurveyID());
				SurveyGroup sg = sgDao.getByKey(s.getSurveyGroupId());
				if (s != null && sg != null) {
					if (s.getVersion() != null) {
						versionMap.put(dsjq.getSurveyID(), s.getVersion());
						ver = s.getVersion();
					} else {
						versionMap.put(dsjq.getSurveyID(), new Double(1.0));
						ver = new Double(1.0);
					}

					surveyGroupId = String.valueOf(s.getSurveyGroupId());
					surveyGroupName = sg.getCode();
				} else {
					// for testing so I can mock a version for local survey
					Random rand = new Random();
					ver = rand.nextDouble();
				}
			}
			sb.append(devicePhoneNumber + "," + dsjq.getSurveyID() + ","
					+ dsjq.getName() + "," + dsjq.getLanguage() + "," + ver + ","
					+ surveyGroupId + "," + surveyGroupName + "," 
					+ IS_IN_MONITORING_GROUP + "," + NEW_LOCALE_SURVEYID
					+ "\n");
		}
		return sb.toString();
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new SurveyManagerRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		// TODO: this API should be refactored to always take an ACTION
		SurveyManagerRequest mgrReq = (SurveyManagerRequest) req;
		RestResponse resp = new RestResponse();
		if (mgrReq.getAction() == null) {
			if (mgrReq.getSurveyDoc() != null) {
				// resp.setMessage("Survey : "
				// + surveyDAO.save(mgrReq.getSurveyDoc()));
			} else if (mgrReq.getSurveyInstanceId() != null) {
				SurveyInstanceDAO siDAO = new SurveyInstanceDAO();
				SurveyInstance si = siDAO
						.getByKey(mgrReq.getSurveyInstanceId());
				if (si != null) {
					resp.setMessage(si.toString());
				} else {
					resp.setMessage("No Survey Instance Found");
				}

			} else if (mgrReq.getSurveyId() != null) {
				SurveyContainerDao scDao = new SurveyContainerDao();
				SurveyContainer container = scDao.findBySurveyId(mgrReq
						.getSurveyId());
				if (container != null) {

					resp.setMessage(container.getSurveyDocument().getValue());
				} else {
					resp.setMessage("No Survey Found");
				}
			}

		} else if (SurveyManagerRequest.GET_AVAIL_DEVICE_SURVEY_ACTION
				.equalsIgnoreCase(req.getAction())) {
			//Report which surveys the device should have
			Device dev = null;
			if (mgrReq.getPhoneNumber() != null || mgrReq.getImei() != null) {
				resp.setMessage(getSurveyForPhone(mgrReq.getPhoneNumber(), mgrReq.getImei()));
				// now check to see if we need to update the device
				if (mgrReq.getImei() != null){ 
					dev = deviceDao.getByImei(mgrReq.getImei());
				}
				if (dev == null){
					dev = deviceDao.get(mgrReq.getPhoneNumber());
				}
				if (dev != null) {
					if (mgrReq.getDeviceId() != null
							&& mgrReq.getDeviceId().trim().length() > 0) {
						dev.setDeviceIdentifier(mgrReq.getDeviceId());
					}
				} else {
					// we need to create the device since we haven't seen it
					// before
					dev = new Device();
					dev.setEsn(mgrReq.getImei());
					dev.setPhoneNumber(mgrReq.getPhoneNumber());
					dev.setDeviceType(DeviceType.CELL_PHONE_ANDROID);
					dev.setDeviceIdentifier(mgrReq.getDeviceId());
					dev.setLastLocationBeaconTime(new Date());
					dev.setGallatinSoftwareManifest(mgrReq.getVersion());
					deviceDao.save(dev);
				}
			}
		} else if (SurveyManagerRequest.GET_SURVEY_HEADER_ACTION
				.equalsIgnoreCase(req.getAction())) {
			if (mgrReq.getSurveyId() != null) {
				SurveyDAO surveyDao = new SurveyDAO();
				SurveyGroupDAO sgDao = new SurveyGroupDAO();
				Survey survey = surveyDao.getById(mgrReq.getSurveyId());
				String surveyGroupName = UNKNOWN;
				if (survey != null) {
					SurveyGroup sg = sgDao.getByKey(survey.getSurveyGroupId());
					if (sg != null && sg.getCode() != null) {
						surveyGroupName = sg.getCode();
					}
					StringBuilder sb = new StringBuilder();
					sb.append(survey.getKey().getId() + ",")
							.append(survey.getName() != null ? survey.getName()
									: "Survey " + survey.getKey().getId())
							.append(",")
							.append(survey.getDefaultLanguageCode() != null ? survey
									.getDefaultLanguageCode() : "en")
							.append(",")
							.append(survey.getVersion() != null ? survey
									.getVersion() : "1")
							.append(",")
							.append(survey.getSurveyGroupId().toString())
							.append(",")
							.append(surveyGroupName)
							.append(",")
							.append(IS_IN_MONITORING_GROUP)
							.append(",")
							.append(NEW_LOCALE_SURVEYID);
					resp.setMessage(sb.toString());
				}
			}
			if (mgrReq.getPhoneNumber() != null
					&& mgrReq.getPhoneNumber().trim().length() > 0) {
				// check for a device
				Device device = deviceDao.get(mgrReq.getPhoneNumber());
				if (device == null) {
					// create a device if missing
					device = new Device();
					device.setDeviceType(DeviceType.CELL_PHONE_ANDROID);
					device.setDeviceIdentifier(mgrReq.getDeviceId());
					device.setPhoneNumber(mgrReq.getPhoneNumber());
					device.setLastLocationBeaconTime(new Date());
					device.setGallatinSoftwareManifest(mgrReq.getVersion());
					deviceDao.save(device);
				}
			}
		} else if (SurveyManagerRequest.GET_ZIP_FILE_URL_ACTION
				.equalsIgnoreCase(req.getAction())) {
			DeviceFilesDao dfDao = new DeviceFilesDao();
			List<DeviceFiles> dfList = dfDao.listDeviceFilesByDate(
					mgrReq.getStartDate(), "all");
			if (dfList != null) {
				Set<String> uxUrlList = new HashSet<String>();
				for (DeviceFiles df : dfList) {

					uxUrlList.add(df.getURI());

				}
				StringBuilder sb = new StringBuilder();
				for (String item : uxUrlList)
					sb.append(item + "\n");
				resp.setMessage(sb.toString());
			}
		}
		return resp;
	}

	@Override
	protected void writeOkResponse(RestResponse response) throws Exception {
		try {
			HttpServletResponse resp = getResponse();
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().print(response.getMessage());
		} catch (IOException e) {
			log.log(Level.SEVERE,
					"Could not write survey manager response to http output stream",
					e);
		}
	}
}
