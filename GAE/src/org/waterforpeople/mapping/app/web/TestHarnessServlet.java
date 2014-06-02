/*
 *  Copyright (C) 2010-2013 Stichting Akvo (Akvo Foundation)
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.app.web.rest.security.AppRole;
import org.waterforpeople.mapping.app.web.test.DeleteObjectUtil;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.gis.map.dao.OGRFeatureDao;
import com.gallatinsystems.gis.map.domain.Geometry;
import com.gallatinsystems.gis.map.domain.Geometry.GeometryType;
import com.gallatinsystems.gis.map.domain.OGRFeature;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleClusterDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocaleCluster;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class TestHarnessServlet extends HttpServlet {
	private static Logger log = Logger.getLogger(TestHarnessServlet.class
			.getName());
	private static final long serialVersionUID = -5673118002247715049L;

	@Override
	@SuppressWarnings({ "unused", "rawtypes" })
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		if ("setupTestUser".equals(action)) {
			setupTestUser();
		} else if ("deleteGeoData".equals(action)) {
			try {
				OGRFeatureDao ogrFeatureDao = new OGRFeatureDao();
				for (OGRFeature item : ogrFeatureDao.list("all")) {
					resp.getWriter().println(
							"deleting: " + item.getCountryCode());
					ogrFeatureDao.delete(item);
				}
				resp.getWriter().println("Finished");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("loadOGRFeature".equals(action)) {
			OGRFeature ogrFeature = new OGRFeature();
			ogrFeature.setName("clan-21061011");
			ogrFeature.setProjectCoordinateSystemIdentifier("World_Mercator");
			ogrFeature.setGeoCoordinateSystemIdentifier("GCS_WGS_1984");
			ogrFeature.setDatumIdentifier("WGS_1984");
			ogrFeature.setSpheroid(6378137D);
			ogrFeature.setReciprocalOfFlattening(298.257223563);
			ogrFeature.setCountryCode("LR");
			ogrFeature.addBoundingBox(223700.015625, 481399.468750,
					680781.375000, 945462.437500);
			Geometry geo = new Geometry();
			geo.setType(GeometryType.POLYGON);
			String coords = "497974.5625 557051.875,498219.03125 557141.75,498655.34375 557169.4375,499001.65625 557100.1875,499250.96875 556933.9375,499167.875 556615.375,499230.1875 556407.625,499392.78125 556362.75,499385.90625 556279.875,499598.5 556067.3125,499680.25 555952.8125,499218.5625 554988.875,498775.65625 554860.1875,498674.5 554832.5625,498282.0 554734.4375,498020.34375 554554.5625,497709.59375 554374.6875,497614.84375 554374.6875,497519.46875 554369.1875,497297.3125 554359.9375,496852.96875 554355.3125,496621.125 554351.375,496695.75 554454.625,496771.59375 554604.625,496836.3125 554734.0625,496868.65625 554831.125,496847.09375 554863.4375,496760.8125 554863.4375,496663.75 554928.125,496620.625 554992.875,496555.90625 555025.1875,496448.0625 554992.875,496372.5625 555025.1875,496351.0 555133.0625,496415.71875 555197.75,496480.40625 555294.8125,496480.40625 555381.0625,496430.875 555430.75,496446.0625 555547.375,496490.53125 555849.625,496526.09375 556240.75,496721.65625 556596.375,496924.90625 556774.1875,497006.125 556845.25,497281.71875 556978.625,497610.625 556969.6875,497859.53125 556969.6875,497974.5625 557051.875";
			for (String item : coords.split(",")) {
				String[] coord = item.split(" ");
				geo.addCoordinate(Double.parseDouble(coord[0]),
						Double.parseDouble(coord[1]));
			}
			ogrFeature.setGeometry(geo);
			ogrFeature.addGeoMeasure("CLNAME", "STRING", "Loisiana Township");
			ogrFeature.addGeoMeasure("COUNT", "FLOAT", "1");
			BaseDAO<OGRFeature> ogrDao = new BaseDAO<OGRFeature>(
					OGRFeature.class);
			ogrDao.save(ogrFeature);
			try {
				resp.getWriter()
						.println("OGRFeature: " + ogrFeature.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("generateGeocells".equals(action)) {
			AccessPointDao apDao = new AccessPointDao();
			List<AccessPoint> apList = apDao.list(null);
			if (apList != null) {
				for (AccessPoint ap : apList) {

					if (ap.getGeocells() == null
							|| ap.getGeocells().size() == 0) {
						if (ap.getLatitude() != null
								&& ap.getLongitude() != null) {
							ap.setGeocells(GeocellManager.generateGeoCell(new Point(
									ap.getLatitude(), ap.getLongitude())));
							apDao.save(ap);
						}
					}
				}
			}
		} else if ("importsinglesurvey".equals(action)) {
			TaskOptions options = TaskOptions.Builder
					.withUrl("/app_worker/dataprocessor")
					.param(DataProcessorRequest.ACTION_PARAM,
							DataProcessorRequest.IMPORT_REMOTE_SURVEY_ACTION)
					.param(DataProcessorRequest.SOURCE_PARAM,
							req.getParameter("source"))
					.param(DataProcessorRequest.SURVEY_ID_PARAM,
							req.getParameter("surveyId"))
					.param(DataProcessorRequest.API_KEY_PARAM,
							req.getParameter("apiKey"));
			com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
					.getDefaultQueue();
			queue.add(options);
		} else if ("deleteSurveyResponses".equals(action)) {
			if (req.getParameter("surveyId") == null) {
				try {
					resp.getWriter()
							.println("surveyId is a required parameter");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {

				deleteSurveyResponses(
						Long.parseLong(req.getParameter("surveyId")),
						Integer.parseInt(req.getParameter("count")));
			}
		} else if ("changeLocaleType".equals(action)) {
			String surveyId = req
					.getParameter(DataProcessorRequest.SURVEY_ID_PARAM);
			if (surveyId == null) {
				try {
					resp.getWriter()
					.println("surveyId parameter missing");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}

			TaskOptions options = TaskOptions.Builder.withUrl(
				"/app_worker/dataprocessor").param(
				DataProcessorRequest.ACTION_PARAM,
				DataProcessorRequest.CHANGE_LOCALE_TYPE_ACTION);

			if (req.getParameter("bypassBackend") == null
					|| !req.getParameter("bypassBackend").equals("true")) {
				// change the host so the queue invokes the backend
				options = options.header("Host",BackendServiceFactory.getBackendService()
					.getBackendAddress("dataprocessor"));
			}
			options.param(DataProcessorRequest.SURVEY_ID_PARAM, surveyId);
			com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
					.getDefaultQueue();
			queue.add(options);
		} else if ("addTranslationFields".equals(action)) {
			TaskOptions options = TaskOptions.Builder.withUrl(
					"/app_worker/dataprocessor").param(
							DataProcessorRequest.ACTION_PARAM,
							DataProcessorRequest.ADD_TRANSLATION_FIELDS);
			com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
					.getDefaultQueue();
			queue.add(options);
		} else if (DataProcessorRequest.REBUILD_QUESTION_SUMMARY_ACTION
				.equals(action)) {
			TaskOptions options = TaskOptions.Builder.withUrl(
					"/app_worker/dataprocessor").param(
					DataProcessorRequest.ACTION_PARAM,
					DataProcessorRequest.REBUILD_QUESTION_SUMMARY_ACTION);

			if (req.getParameter("bypassBackend") == null
					|| !req.getParameter("bypassBackend").equals("true")) {
				// change the host so the queue invokes the backend
				options = options
						.header("Host",
								BackendServiceFactory.getBackendService()
										.getBackendAddress("dataprocessor"));
			}
			String surveyId = req
					.getParameter(DataProcessorRequest.SURVEY_ID_PARAM);
			if (surveyId != null && surveyId.trim().length() > 0) {
				options.param(DataProcessorRequest.SURVEY_ID_PARAM, surveyId);
			}

			com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
					.getDefaultQueue();
			queue.add(options);

		} else if ("deleteallqsum".equals(action)) {
			DeleteObjectUtil dou = new DeleteObjectUtil();
			dou.deleteAllObjects("SurveyQuestionSummary");
		} else if (DataProcessorRequest.DELETE_DUPLICATE_QAS.equals(action)) {
			final TaskOptions options = TaskOptions.Builder
					.withUrl("/app_worker/dataprocessor")
					.param(DataProcessorRequest.ACTION_PARAM,
							DataProcessorRequest.DELETE_DUPLICATE_QAS)
					.header("Host",
							BackendServiceFactory.getBackendService()
									.getBackendAddress("dataprocessor"));
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(options);
			try {
				resp.getWriter().print("Request Processed - Check the logs");
			} catch (Exception e) {
				// no-op
			}
		} else if (DataProcessorRequest.RECOMPUTE_LOCALE_CLUSTERS.equals(action)) {
			SurveyedLocaleClusterDao slcDao = new SurveyedLocaleClusterDao();
			// first, delete all clusters
			for (SurveyedLocaleCluster slc : slcDao.list("all")) {
				slcDao.delete(slc);
			}

			// initialize the memcache
			Cache cache = null;
			Map props = new HashMap();
			try {
				CacheFactory cacheFactory = CacheManager.getInstance()
						.getCacheFactory();
				cache = cacheFactory.createCache(props);
				cache.clear();
			} catch (Exception e) {
				log.log(Level.SEVERE,
						"Couldn't initialize cache: " + e.getMessage(), e);
			}

			final TaskOptions options = TaskOptions.Builder
					.withUrl("/app_worker/dataprocessor")
					.param(DataProcessorRequest.ACTION_PARAM,
							DataProcessorRequest.RECOMPUTE_LOCALE_CLUSTERS)
					.header("Host", BackendServiceFactory.getBackendService()
							.getBackendAddress("dataprocessor"));
			Queue queue = QueueFactory.getDefaultQueue();
			queue.add(options);
			try {
				resp.getWriter().print("Request Processed - Check the logs");
			} catch (Exception e) {
				// no-op
			}
		} else if (DataProcessorRequest.RECREATE_LOCALES.equals(action)){
			Queue queue = QueueFactory.getDefaultQueue();
			SurveyDAO surveyDao = new SurveyDAO();
			List<Survey> sList = surveyDao.list("all");
			for (Survey s : sList){
				log.log(Level.INFO, "Running Remap for survey: " + s.getKey().getId());
				queue.add(TaskOptions.Builder
						.withUrl("/app_worker/surveyalservlet")
						.param(SurveyalRestRequest.ACTION_PARAM,
								SurveyalRestRequest.RERUN_ACTION)
						.param(SurveyalRestRequest.SURVEY_ID_PARAM,
								"" + s.getKey().getId()));
			}
		} else if ("addCreationSurveyIdToLocale".equals(action)) {
			final TaskOptions options = TaskOptions.Builder.withUrl(
					"/app_worker/dataprocessor")
							.param(DataProcessorRequest.ACTION_PARAM,
									DataProcessorRequest.ADD_CREATION_SURVEY_ID_TO_LOCALE);
			com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
					.getDefaultQueue();
			queue.add(options);
			try {
				resp.getWriter().print("Request Processed - Check the logs");
			} catch (Exception e) {
				// no-op
			}
		} else if ("populateQuestionOrders".equals(action)) {
			log.log(Level.INFO, "Populating question and question group orders: ");
			Queue queue = QueueFactory.getDefaultQueue();
			TaskOptions to = TaskOptions.Builder
					.withUrl("/app_worker/dataprocessor")
					.param(DataProcessorRequest.ACTION_PARAM,
							DataProcessorRequest.POP_QUESTION_ORDER_FIELDS_ACTION)
					.param("cursor", "")
					.header("host",
							BackendServiceFactory.getBackendService().getBackendAddress(
									"dataprocessor"));
			if (req.getParameter("surveyId") != null){
				try {
					// if we have a surveyId, try to parse it to long here
					// if we fail, we break of the whole operation
					// we don't use the parsed value
					Long surveyId = Long.parseLong(req.getParameter("surveyId"));
					queue.add(to.param(DataProcessorRequest.SURVEY_ID_PARAM, surveyId.toString()));
				} catch (NumberFormatException e){
					log.log(Level.SEVERE, "surveyId provided not valid: " + req.getParameter("surveyId"));
				}
			} else {
				// if we don't have a surveyId, we want to populate all surveys
				// so we fire the task without the surveyId parameter.
				queue.add(to);
			}
		}
	}

	private void setupTestUser() {
		UserDao userDao = new UserDao();
		User user = userDao.findUserByEmail("test@example.com");
		if (user == null) {
			user = new User();
			user.setEmailAddress("test@example.com");
		}
		user.setSuperAdmin(true);
		user.setPermissionList(String.valueOf(AppRole.SUPER_ADMIN.getLevel()));
		userDao.save(user);
	}

	private boolean deleteSurveyResponses(Long surveyId, Integer count) {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();

		List<SurveyInstance> instances = dao.listSurveyInstanceBySurvey(
				surveyId, count != null ? count : 100);

		if (instances != null) {
			for (SurveyInstance instance : instances) {
				dao.deleteSurveyInstance(instance);
			}
			return true;
		}
		return false;
	}
}
