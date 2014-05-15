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
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.datanucleus.store.appengine.query.JDOCursorHelper;
import org.waterforpeople.mapping.analytics.dao.AccessPointMetricSummaryDao;
import org.waterforpeople.mapping.analytics.dao.AccessPointStatusSummaryDao;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.AccessPointMetricSummary;
import org.waterforpeople.mapping.analytics.domain.AccessPointStatusSummary;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.server.accesspoint.AccessPointManagerServiceImpl;
import org.waterforpeople.mapping.app.gwt.server.devicefiles.DeviceFilesServiceImpl;
import org.waterforpeople.mapping.app.gwt.server.survey.SurveyAssignmentServiceImpl;
import org.waterforpeople.mapping.app.gwt.server.survey.SurveyServiceImpl;
import org.waterforpeople.mapping.app.web.dto.DataProcessorRequest;
import org.waterforpeople.mapping.app.web.rest.security.AppRole;
import org.waterforpeople.mapping.app.web.test.AccessPointMetricSummaryTest;
import org.waterforpeople.mapping.app.web.test.AccessPointTest;
import org.waterforpeople.mapping.app.web.test.DeleteObjectUtil;
import org.waterforpeople.mapping.app.web.test.StandardScoringTest;
import org.waterforpeople.mapping.app.web.test.StandardTestLoader;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.AccessPointMetricMappingDao;
import org.waterforpeople.mapping.dao.CommunityDao;
import org.waterforpeople.mapping.dao.DeviceFilesDao;
import org.waterforpeople.mapping.dao.QuestionAnswerStoreDao;
import org.waterforpeople.mapping.dao.SurveyAttributeMappingDao;
import org.waterforpeople.mapping.dao.SurveyContainerDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.dataexport.DeviceFilesReplicationImporter;
import org.waterforpeople.mapping.dataexport.SurveyReplicationImporter;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.Status;
import org.waterforpeople.mapping.domain.AccessPointMetricMapping;
import org.waterforpeople.mapping.domain.Community;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.Status.StatusCode;
import org.waterforpeople.mapping.domain.SurveyAssignment;
import org.waterforpeople.mapping.domain.SurveyAttributeMapping;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.domain.TechnologyType;
import org.waterforpeople.mapping.helper.AccessPointHelper;
import org.waterforpeople.mapping.helper.GeoRegionHelper;
import org.waterforpeople.mapping.helper.KMLHelper;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.Device.DeviceType;
import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.diagnostics.dao.RemoteStacktraceDao;
import com.gallatinsystems.diagnostics.domain.RemoteStacktrace;
import com.gallatinsystems.editorial.dao.EditorialPageDao;
import com.gallatinsystems.editorial.domain.EditorialPage;
import com.gallatinsystems.editorial.domain.EditorialPageContent;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.exceptions.IllegalDeletionException;
import com.gallatinsystems.gis.coordinate.utilities.Coordinate;
import com.gallatinsystems.gis.coordinate.utilities.CoordinateUtilities;
import com.gallatinsystems.gis.geography.dao.CountryDao;
import com.gallatinsystems.gis.geography.dao.SubCountryDao;
import com.gallatinsystems.gis.geography.domain.Country;
import com.gallatinsystems.gis.geography.domain.SubCountry;
import com.gallatinsystems.gis.location.GeoLocationServiceGeonamesImpl;
import com.gallatinsystems.gis.location.GeoPlace;
import com.gallatinsystems.gis.map.dao.MapFragmentDao;
import com.gallatinsystems.gis.map.dao.OGRFeatureDao;
import com.gallatinsystems.gis.map.domain.Geometry;
import com.gallatinsystems.gis.map.domain.Geometry.GeometryType;
import com.gallatinsystems.gis.map.domain.MapFragment;
import com.gallatinsystems.gis.map.domain.MapFragment.FRAGMENTTYPE;
import com.gallatinsystems.gis.map.domain.OGRFeature;
import com.gallatinsystems.gis.map.domain.OGRFeature.FeatureType;
import com.gallatinsystems.metric.dao.MetricDao;
import com.gallatinsystems.metric.domain.Metric;
import com.gallatinsystems.notification.NotificationRequest;
import com.gallatinsystems.notification.helper.NotificationHelper;
import com.gallatinsystems.standards.domain.StandardScoreBucket;
import com.gallatinsystems.survey.dao.DeviceSurveyJobQueueDAO;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.QuestionHelpMediaDao;
import com.gallatinsystems.survey.dao.QuestionOptionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyTaskUtil;
import com.gallatinsystems.survey.dao.TranslationDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Question.Type;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyXMLFragment;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Translation.ParentType;
import com.gallatinsystems.surveyal.app.web.SurveyalRestRequest;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleClusterDao;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.gallatinsystems.surveyal.domain.SurveyedLocaleCluster;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.User;
import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.stdimpl.GCacheFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class TestHarnessServlet extends HttpServlet {
	private static Logger log = Logger.getLogger(TestHarnessServlet.class
			.getName());
	private static final long serialVersionUID = -5673118002247715049L;

	@SuppressWarnings({ "unused", "rawtypes" })
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		if ("setupTestUser".equals(action)) {
			setupTestUser();
		} else if ("computeDistanceAlongBearing".equals(action)) {
			com.gallatinsystems.gis.coordinate.utilities.CoordinateUtilities cu = new CoordinateUtilities();
			Coordinate startingPoint = new Coordinate(Double.parseDouble(req
					.getParameter("lat")), Double.parseDouble(req
					.getParameter("lon")));

			Double distance = Double.parseDouble(req.getParameter("distance"));
			Double bearing = Double.parseDouble(req.getParameter("bearing"));

			Coordinate newPoint = cu.computePointAlongBearingDistance(
					startingPoint, distance, bearing);
			try {
				resp.getWriter().println("New Point : " + newPoint.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if ("fixQuestionAnswerStoreDates".equals(action)) {
			// DataFixes df = new DataFixes();
			// df.generateTestData();

			Queue queue = QueueFactory.getDefaultQueue();

			queue.add(TaskOptions.Builder
					.withUrl("/app_worker/questionanswerstorecleanup"));
			log.info("submiting task QAS CollectionDate Cleanup");
		} else if ("reprocessFiles".equalsIgnoreCase(action)) {
			DeviceFilesDao dfDao = new DeviceFilesDao();
			String cursor = null;
			List<DeviceFiles> files = null;
			do {
				files = dfDao.listDeviceFilesByStatus(StatusCode.IN_PROGRESS,
						cursor);
				if (files != null) {
					cursor = DeviceFilesDao.getCursor(files);
					Queue queue = QueueFactory.getDefaultQueue();
					for (DeviceFiles fi : files) {
						queue.add(TaskOptions.Builder
								.withUrl("/app_worker/task")
								.param("action", "processFile")
								.param("fileName",
										fi.getURI()
												.substring(
														fi.getURI()
																.lastIndexOf(
																		"/") + 1)));
					}
				}
			} while (files != null && files.size() > 0 && cursor != null);
		} else if ("testStandardScoring".equals(action)) {
			StandardTestLoader stl = new StandardTestLoader(req, resp);
			stl.runTest();
		} else if ("listStandardScoringResults".equals(action)) {
			StandardTestLoader stl = new StandardTestLoader(req, resp);
			String countryCode = null;
			String communityCode = null;
			String accessPointCode = null;
			if (req.getParameter("countryCode") != null) {
				countryCode = req.getParameter("countryCode");
			}
			if (req.getParameter("communityCode") != null) {
				communityCode = req.getParameter("communityCode");
			}
			if (req.getParameter("accessPointCode") != null) {
				accessPointCode = req.getParameter("accessPointCode");
			}
			String cursorString = null;
			if (req.getParameter("cursorString") != null) {
				cursorString = req.getParameter("cursorString");
			}
			stl.listResults(countryCode, communityCode, accessPointCode,
					cursorString);
		} else if ("testDistanceRule".equals(action)) {
			DeleteObjectUtil dou = new DeleteObjectUtil();
			dou.deleteAllObjects("AccessPointScoreComputationItem");
			dou.deleteAllObjects("AccessPointScoreDetail");
			// AccessPointTest apt = new AccessPointTest();
			// apt.loadWPDistanceTestData(resp);
			// apt.loadHHDistanceTestData(resp);
			// AccessPointDao apDao = new AccessPointDao();
			// List<AccessPoint> apList = apDao.list("all");
			// AccessPointHelper aph = new AccessPointHelper();
			// for (AccessPoint ap : apList) {
			// aph.computeDistanceRule(ap);
			// }
			// try {
			// resp.getWriter().println("Completed test distance rule");
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		} else if ("setupDevEnv".equals(action)) {
			try {
				DeleteObjectUtil dou = new DeleteObjectUtil();
				dou.deleteAllObjects("AccessPoint");
				dou.deleteAllObjects("StandardScoreBucket");
				dou.deleteAllObjects("StandardScoring");
				resp.getWriter().println(
						"About to configure development environment");

				setupTestUser();
				resp.getWriter().println("Completed setting up test user");
				resp.getWriter().println("Completed setting up permissions");
				AccessPointTest apt = new AccessPointTest();
				apt.loadLots(resp, 100);
				StandardScoringTest sct = new StandardScoringTest();
				sct.populateData();
				setupMetrics();
				resp.getWriter().println("Completed setting scorebuckets");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("populateAccessPointMetric".equals(action)) {
			AccessPointMetricSummaryTest apMST = new AccessPointMetricSummaryTest();
			apMST.runTest(resp);
		} else if ("reorderQuestionsByCollectionDate".equals(action)) {
			try {
				Long surveyId = Long.parseLong(req.getParameter("surveyId"));
				SurveyDAO surveyDao = new SurveyDAO();
				QuestionDao qDao = new QuestionDao();

				Survey survey = surveyDao.loadFullSurvey(surveyId);
				int i = 0;
				for (Map.Entry<Integer, QuestionGroup> qGEntry : survey
						.getQuestionGroupMap().entrySet()) {
					List<Question> qList = qDao
							.listQuestionsByQuestionGroupOrderByCreatedDateTime(qGEntry
									.getValue().getKey().getId());
					for (Question q : qDao
							.listQuestionsByQuestionGroupOrderByCreatedDateTime(qGEntry
									.getValue().getKey().getId())) {
						q.setOrder(i + 1);
						qDao.save(q);
						resp.getWriter().println(
								q.getOrder() + " :Change: " + q.getText());
						++i;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("fixQuestionOrder".equals(action)) {

			Long surveyId = Long.parseLong(req.getParameter("surveyId"));

			QuestionDao qDao = new QuestionDao();

			// this is the list in ascending order by the "order" field
			List<Question> qList = qDao.listQuestionsBySurvey(surveyId);
			if (qList != null) {
				Map<Long, Integer> groupMaxCount = new HashMap<Long, Integer>();
				for (Question q : qList) {
					Integer max = groupMaxCount.get(q.getQuestionGroupId());
					if (max == null) {
						max = 1;
					} else {
						max = max + 1;
					}
					// since q is still attached, this should be all we need to
					// do
					q.setOrder(max);
					groupMaxCount.put(q.getQuestionGroupId(), max);
				}

			}
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
		} else if ("testGeoLocation".equals(action)) {
			OGRFeatureDao ogrFeatureDao = new OGRFeatureDao();
			GeoLocationServiceGeonamesImpl gs = new GeoLocationServiceGeonamesImpl();
			String lat = req.getParameter("lat");
			String lon = req.getParameter("lon");
			GeoPlace geoPlace = gs.manualLookup(lat, lon);
			try {
				if (geoPlace != null) {
					resp.getWriter().println(
							"Found: " + geoPlace.getCountryName() + ":"
									+ geoPlace.getCountryCode() + " for " + lat
									+ ", " + lon);
					geoPlace = gs.resolveSubCountry(lat, lon,
							geoPlace.getCountryCode());
				}
				if (geoPlace != null)
					resp.getWriter().println(
							"Found: " + geoPlace.getCountryCode() + ":"
									+ geoPlace.getSub1() + ":"
									+ geoPlace.getSub2() + ":"
									+ geoPlace.getSub3() + ":"
									+ geoPlace.getSub4() + ":"
									+ geoPlace.getSub5() + ":"
									+ geoPlace.getSub6() + " for " + lat + ", "
									+ lon);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("testDetailedGeoLocation".equals(action)) {
			OGRFeatureDao ogrFeatureDao = new OGRFeatureDao();
			GeoLocationServiceGeonamesImpl gs = new GeoLocationServiceGeonamesImpl();
			String lat = req.getParameter("lat");
			String lon = req.getParameter("lon");
			GeoPlace geoPlace = gs.manualLookup(lat, lon, OGRFeature.FeatureType.SUB_COUNTRY_OTHER);
			try {
				if (geoPlace != null) {
					resp.getWriter().println(
							"Found: " + geoPlace.getCountryName() + ":"
									+ geoPlace.getCountryCode() + " for " + lat
									+ ", " + lon);
					geoPlace = gs.resolveSubCountry(lat, lon,
							geoPlace.getCountryCode());
				}
				if (geoPlace != null)
					resp.getWriter().println(
							"Found: " + geoPlace.getCountryCode() + ":"
									+ geoPlace.getSub1() + ":"
									+ geoPlace.getSub2() + ":"
									+ geoPlace.getSub3() + ":"
									+ geoPlace.getSub4() + ":"
									+ geoPlace.getSub5() + ":"
									+ geoPlace.getSub6() + " for " + lat + ", "
									+ lon);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("RemapAPToSub".equals(action)) {
			AccessPointDao apDao = new AccessPointDao();

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

		} else if ("printOGRFeature".equals(action)) {
			OGRFeatureDao ogrFeatureDao = new OGRFeatureDao();
			FeatureType featureType = FeatureType.valueOf(req
					.getParameter("featureType"));
			List<OGRFeature> ogrFeatureList = ogrFeatureDao
					.listByCountryAndType(req.getParameter("countryCode"),
							featureType, null);
			try {
				int i = 1;
				if (ogrFeatureList != null && !ogrFeatureList.isEmpty()) {
					for (OGRFeature item : ogrFeatureList) {
						resp.getWriter().println(
								"i: " + i + " sub1" + item.getSub1()
										+ " sub2: " + item.getSub2());
						resp.getWriter().println(
								"        OGRFeature: " + item.toString());
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if ("resetLRAP".equals(action)) {
			try {

				AccessPointDao apDao = new AccessPointDao();
				Random rand = new Random();
				for (AccessPoint ap : apDao.list("all")) {
					if ((ap.getCountryCode() == null || ap.getCountryCode()
							.equals("US"))
							&& (ap.getLatitude() != null && ap.getLongitude() != null)) {
						if (ap.getLatitude() > 5.0 && ap.getLatitude() < 11) {
							if (ap.getLongitude() < -9
									&& ap.getLongitude() > -11) {
								ap.setCountryCode("LR");
								apDao.save(ap);
								resp.getWriter()
										.println(
												"Found "
														+ ap.getCommunityCode()
														+ "mapped to US changing mapping to LR \n");

							}
						}
					} else if (ap.getCommunityCode() == null) {
						ap.setCommunityCode(rand.nextLong() + "");
						apDao.save(ap);
						resp.getWriter().println(
								"Found " + ap.getCommunityCode()
										+ "added random community code \n");
					}
				}

			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not execute test", e);
			}

		} else if ("populateAccessPointMetricSummary".equals(action)) {
			OGRFeatureDao ogrFeatureDao = new OGRFeatureDao();
			AccessPointMetricSummaryDao apmsDao = new AccessPointMetricSummaryDao();
			// List<OGRFeature> ogrList =
			// ogrFeatureDao.listByCountryAndType("LR",
			// FeatureType.SUB_COUNTRY_OTHER);
			Boolean firstTimeFlag = false;
			// for (OGRFeature item : ogrList) {
			// AccessPointMetricSummary apms = new AccessPointMetricSummary();
			// apms.setCount(1L);
			// apms.setCountry("LR");
			// apms.setSubLevel(1);
			// apms.setSubValue(item.getSub1());
			// apms.setShardNum(1);
			// apms.setParentSubName("LR");
			// apms.setMetricName("WATER_POINT");
			// apmsDao.save(apms);
			// }
		} else if ("populateStandardScoring".equals(action)) {
			StandardScoringTest sst = new StandardScoringTest();
			sst.populateData();
		} else if ("clearSurveyInstanceQAS".equals(action)) {
			// QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
			// for (QuestionAnswerStore qas : qasDao.list("all")) {
			// qasDao.delete(qas);
			// }
			// SurveyInstanceDAO siDao = new SurveyInstanceDAO();
			// for (SurveyInstance si : siDao.list("all")) {
			// siDao.delete(si);
			// }
			AccessPointDao apDao = new AccessPointDao();
			for (AccessPoint ap : apDao.list("all"))
				apDao.delete(ap);
		} else if ("SurveyInstance".equals(action)) {
			SurveyInstanceDAO siDao = new SurveyInstanceDAO();
			List<SurveyInstance> siList = siDao.listSurveyInstanceBySurveyId(
					1362011L, null);

			Cursor cursor = JDOCursorHelper.getCursor(siList);
			int i = 0;
			while (siList.size() > 0) {
				for (SurveyInstance si : siList) {
					System.out.println(i++ + " " + si.toString());

					String surveyInstanceId = new Long(si.getKey().getId())
							.toString();
					Queue queue = QueueFactory.getDefaultQueue();

					queue.add(TaskOptions.Builder
							.withUrl("/app_worker/surveytask")
							.param("action", "reprocessMapSurveyInstance")
							.param("id", surveyInstanceId));
					log.info("submiting task for SurveyInstanceId: "
							+ surveyInstanceId);
				}
				siList = siDao.listSurveyInstanceBySurveyId(1362011L,
						cursor.toWebSafeString());
				cursor = JDOCursorHelper.getCursor(siList);
			}
			System.out.println("finished");

		} else if ("rotateImage".equals(action)) {

			AccessPointManagerServiceImpl apmI = new AccessPointManagerServiceImpl();
			String test1 = "http://waterforpeople.s3.amazonaws.com/images/wfpPhoto10062903227521.jpg";
			// String test2 =
			// "http://waterforpeople.s3.amazonaws.com/images/hn/ch003[1].jpg";
			writeImageToResponse(resp, test1);
			apmI.setUploadS3Flag(false);
			// apmI.rotateImage(test2);
		} else if ("clearSurveyGroupGraph".equals(action)) {
			SurveyGroupDAO sgDao = new SurveyGroupDAO();
			sgDao.delete(sgDao.list("all"));
			SurveyDAO surveyDao = new SurveyDAO();
			surveyDao.delete(surveyDao.list("all"));
			QuestionGroupDao qgDao = new QuestionGroupDao();
			qgDao.delete(qgDao.list("all"));
			QuestionDao qDao = new QuestionDao();
			qDao.delete(qDao.list("all"));
			QuestionHelpMediaDao qhDao = new QuestionHelpMediaDao();
			qhDao.delete(qhDao.list("all"));
			QuestionOptionDao qoDao = new QuestionOptionDao();
			qoDao.delete(qoDao.list("all"));
			TranslationDao tDao = new TranslationDao();
			tDao.delete(tDao.list("all"));

		} else if ("replicateDeviceFiles".equals(action)) {
			SurveyInstanceDAO siDao = new SurveyInstanceDAO();
			for (SurveyInstance si : siDao.list("all")) {
				siDao.delete(si);
			}

			QuestionAnswerStoreDao qasDao = new QuestionAnswerStoreDao();
			for (QuestionAnswerStore qas : qasDao.list("all")) {
				qasDao.delete(qas);
			}

			DeviceFilesDao dfDao = new DeviceFilesDao();
			for (DeviceFiles df : dfDao.list("all")) {
				dfDao.delete(df);
			}
			DeviceFilesReplicationImporter dfri = new DeviceFilesReplicationImporter();
			dfri.executeImport("http://watermapmonitordev.appspot.com",
					"http://localhost:8888");
			Set<String> dfSet = new HashSet<String>();
			for (DeviceFiles df : dfDao.list("all")) {
				dfSet.add(df.getURI());
			}
			DeviceFilesServiceImpl dfsi = new DeviceFilesServiceImpl();
			int i = 0;
			try {
				resp.getWriter().println(
						"Found " + dfSet.size() + " distinct files to process");
				for (String s : dfSet) {
					dfsi.reprocessDeviceFile(s);

					resp.getWriter().println(
							"submitted " + s + " for reprocessing");

					i++;
					if (i > 10)
						break;
				}
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not execute test", e);
			}

		} else if ("addDeviceFiles".equals(action)) {
			DeviceFilesDao dfDao = new DeviceFilesDao();

			DeviceFiles df = new DeviceFiles();
			df.setURI("http://waterforpeople.s3.amazonaws.com/devicezip/wfp1737657928520.zip");
			df.setCreatedDateTime(new Date());
			df.setPhoneNumber("a4:ed:4e:54:ef:6d");
			df.setChecksum("1149406886");
			df.setProcessedStatus(StatusCode.ERROR_INFLATING_ZIP);
			DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
			java.util.Date date = new java.util.Date();
			String dateTime = dateFormat.format(date);
			df.setProcessDate(dateTime);
			dfDao.save(df);

		} else if ("populateScoreBuckets".equals(action)) {
			BaseDAO<StandardScoreBucket> scDao = new BaseDAO<StandardScoreBucket>(
					StandardScoreBucket.class);
			ArrayList<String> scoreBuckets = new ArrayList<String>();
			scoreBuckets.add("WATERPOINTLEVELOFSERVICE");
			scoreBuckets.add("WATERPOINTSUSTAINABILITY");
			scoreBuckets.add("PUBLICINSTITUTIONLEVELOFSERVICE");
			scoreBuckets.add("PUBLICINSTITUTIONSUSTAINABILITY");
			for (String item : scoreBuckets) {
				StandardScoreBucket sbucket = new StandardScoreBucket();
				sbucket.setName(item);
				scDao.save(sbucket);
			}
		} else if ("testSaveRegion".equals(action)) {
			GeoRegionHelper geoHelp = new GeoRegionHelper();
			ArrayList<String> regionLines = new ArrayList<String>();
			for (int i = 0; i < 10; i++) {
				StringBuilder builder = new StringBuilder();
				builder.append("1,").append("" + i).append(",test,")
						.append(20 + i + ",").append(30 + i + "\n");
				regionLines.add(builder.toString());
			}
			geoHelp.processRegionsSurvey(regionLines);
			try {
				resp.getWriter().print("Save complete");
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not save test region", e);
			}
		} else if ("clearAccessPoint".equals(action)) {
			try {

				AccessPointDao apDao = new AccessPointDao();
				for (AccessPoint ap : apDao.list("all")) {
					apDao.delete(ap);
					try {
						resp.getWriter().print(
								"Finished Deleting AP: " + ap.toString());
					} catch (IOException e) {
						log.log(Level.SEVERE, "Could not delete ap");
					}
				}
				resp.getWriter().print("Deleted AccessPoints complete");
				BaseDAO<AccessPointStatusSummary> apsDao = new BaseDAO<AccessPointStatusSummary>(
						AccessPointStatusSummary.class);
				for (AccessPointStatusSummary item : apsDao.list("all")) {
					apsDao.delete(item);
				}
				resp.getWriter().print("Deleted AccessPointStatusSummary");
				MapFragmentDao mfDao = new MapFragmentDao();
				for (MapFragment item : mfDao.list("all")) {
					mfDao.delete(item);
				}
				resp.getWriter().print("Cleared MapFragment Table");
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not clear AP and APStatusSummary",
						e);
			}

		} else if ("loadErrorPoints".equals(action)) {
			MapFragmentDao mfDao = new MapFragmentDao();
			AccessPointDao apDao = new AccessPointDao();
			for (int j = 0; j < 1; j++) {
				Double lat = 0.0;
				Double lon = 0.0;
				for (int i = 0; i < 5; i++) {
					AccessPoint ap = new AccessPoint();
					ap.setLatitude(lat);
					ap.setLongitude(lon);
					Calendar calendar = Calendar.getInstance();
					Date today = new Date();
					calendar.setTime(today);
					calendar.add(Calendar.YEAR, -1 * i);
					System.out
							.println("AP: " + ap.getLatitude() + "/"
									+ ap.getLongitude() + "Date: "
									+ calendar.getTime());
					ap.setCollectionDate(calendar.getTime());
					ap.setAltitude(0.0);
					ap.setCommunityCode("test" + new Date());
					ap.setCommunityName("test" + new Date());
					ap.setPhotoURL("http://test.com");
					ap.setPointType(AccessPoint.AccessPointType.WATER_POINT);
					if (i == 0)
						ap.setPointStatus(AccessPoint.Status.FUNCTIONING_HIGH);
					else if (i == 1)
						ap.setPointStatus(AccessPoint.Status.FUNCTIONING_OK);
					else if (i == 2)
						ap.setPointStatus(Status.FUNCTIONING_WITH_PROBLEMS);
					else
						ap.setPointStatus(Status.NO_IMPROVED_SYSTEM);

					if (i % 2 == 0)
						ap.setTypeTechnologyString("Kiosk");
					else
						ap.setTypeTechnologyString("Afridev Handpump");
					apDao.save(ap);

					// ms.performSummarization("" + ap.getKey().getId(), "");
					if (i % 50 == 0)
						log.log(Level.INFO, "Loaded to " + i);
				}
			}
			try {
				resp.getWriter().println("Finished loading aps");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("loadLots".equals(action)) {
			AccessPointTest apt = new AccessPointTest();
			apt.loadLots(resp, 500);
		} else if ("loadCountries".equals(action)) {
			Country c = new Country();
			c.setIsoAlpha2Code("HN");
			c.setName("Honduras");
			c.setIncludeInExternal(true);
			c.setCentroidLat(14.7889035);
			c.setCentroidLon(-86.9500379);
			c.setZoomLevel(8);

			BaseDAO<Country> countryDAO = new BaseDAO<Country>(Country.class);
			countryDAO.save(c);

			Country c2 = new Country();
			c2.setIsoAlpha2Code("MW");
			c2.setName("Malawi");
			c2.setIncludeInExternal(true);
			c2.setCentroidLat(-13.0118377);
			c2.setCentroidLon(33.9984484);
			c2.setZoomLevel(7);
			
			countryDAO.save(c2);
			
			Country c3 = new Country();
			c3.setIsoAlpha2Code("UG");
			c3.setName("Uganda");
			c3.setIncludeInExternal(true);
			c3.setCentroidLat(1.1027);
			c3.setCentroidLon(32.3968);
			c3.setZoomLevel(7);
			
			countryDAO.save(c3);

			Country c4 = new Country();
			c4.setIsoAlpha2Code("KE");
			c4.setName("Kenya");
			c4.setIncludeInExternal(true);
			c4.setCentroidLat(-1.26103461);
			c4.setCentroidLon(36.74724467);
			c4.setZoomLevel(7);
			
			countryDAO.save(c4);
		} else if ("testAPKml".equals(action)) {

			MapFragmentDao mfDao = new MapFragmentDao();

			BaseDAO<TechnologyType> ttDao = new BaseDAO<TechnologyType>(
					TechnologyType.class);
			List<TechnologyType> ttList = ttDao.list("all");
			for (TechnologyType tt : ttList)
				ttDao.delete(tt);

			TechnologyType tt = new TechnologyType();
			tt.setCode("Afridev Handpump");
			tt.setName("Afridev Handpump");
			ttDao.save(tt);

			TechnologyType tt2 = new TechnologyType();
			tt2.setCode("Kiosk");
			tt2.setName("Kiosk");
			ttDao.save(tt2);

			KMLHelper kmlHelper = new KMLHelper();
			kmlHelper.buildMap();

			List<MapFragment> mfList = mfDao
					.searchMapFragments("ALL", null, null,
							FRAGMENTTYPE.GLOBAL_ALL_PLACEMARKS, "all", null,
							null);
			try {

				for (MapFragment mfItem : mfList) {
					String contents = ZipUtil
							.unZip(mfItem.getBlob().getBytes());
					log.log(Level.INFO, "Contents Length: " + contents.length());
					resp.setContentType("application/vnd.google-earth.kmz+xml");
					ServletOutputStream out = resp.getOutputStream();
					resp.setHeader("Content-Disposition",
							"inline; filename=waterforpeoplemapping.kmz;");

					out.write(mfItem.getBlob().getBytes());
					out.flush();
				}
			} catch (IOException ie) {
				log.log(Level.SEVERE, "Could not list fragment");
			}
		} else if ("deleteSurveyGraph".equals(action)) {
			deleteAll(SurveyGroup.class);
			deleteAll(Survey.class);
			deleteAll(QuestionGroup.class);
			deleteAll(Question.class);
			deleteAll(Translation.class);
			deleteAll(QuestionOption.class);
			deleteAll(QuestionHelpMedia.class);
			try {
				resp.getWriter().println("Finished deleting survey graph");
			} catch (IOException iex) {
				log.log(Level.SEVERE, "couldn't delete surveyGraph" + iex);
			}
		}

		else if ("saveSurveyGroupRefactor".equals(action)) {
			SurveyGroupDAO sgDao = new SurveyGroupDAO();
			createSurveyGroupGraph(resp);
			try {
				List<SurveyGroup> savedSurveyGroups = sgDao.list("all");
				for (SurveyGroup sgItem : savedSurveyGroups) {
					resp.getWriter().println("SG: " + sgItem.getCode());
					for (Survey survey : sgItem.getSurveyList()) {
						resp.getWriter().println(
								"   Survey:" + survey.getName());
						for (Map.Entry<Integer, QuestionGroup> entry : survey
								.getQuestionGroupMap().entrySet()) {
							resp.getWriter().println(
									"     QuestionGroup: " + entry.getKey()
											+ ":" + entry.getValue().getDesc());
							for (Map.Entry<Integer, Question> questionEntry : entry
									.getValue().getQuestionMap().entrySet()) {
								resp.getWriter().println(
										"         Question"
												+ questionEntry.getKey()
												+ ":"
												+ questionEntry.getValue()
														.getText());
								for (Map.Entry<Integer, QuestionHelpMedia> qhmEntry : questionEntry
										.getValue().getQuestionHelpMediaMap()
										.entrySet()) {
									resp.getWriter().println(
											"             QuestionHelpMedia"
													+ qhmEntry.getKey()
													+ ":"
													+ qhmEntry.getValue()
															.getText());
									/*
									 * for (Key tKey : qhmEntry.getValue()
									 * .getAltTextKeyList()) { Translation t =
									 * tDao.getByKey(tKey);
									 * resp.getWriter().println(
									 * "                 QHMAltText" +
									 * t.getLanguageCode() + ":" + t.getText());
									 * }
									 */
								}
							}
						}
					}
				}
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not save sg");
			}

		} else if ("createAP".equals(action)) {
			AccessPoint ap = new AccessPoint();
			ap.setCollectionDate(new Date());
			ap.setCommunityCode(new Random().toString());
			ap.setPointStatus(Status.FUNCTIONING_OK);
			ap.setLatitude(47.3);
			ap.setLongitude(9d);
			ap.setCountryCode("SZ");
			ap.setPointType(AccessPointType.WATER_POINT);
			AccessPointHelper helper = new AccessPointHelper();
			helper.saveAccessPoint(ap);

		} else if ("createInstance".equals(action)) {
			SurveyInstance si = new SurveyInstance();
			si.setCollectionDate(new Date());
			ArrayList<QuestionAnswerStore> store = new ArrayList<QuestionAnswerStore>();
			QuestionAnswerStore ans = new QuestionAnswerStore();
			ans.setQuestionID("2166031");
			ans.setValue("12.379456758498787|-85.53869247436275|548.0|1kvc9dqy");
			ans.setType("GEO");
			ans.setSurveyId(1360012L);
			store.add(ans);
			si.setQuestionAnswersStore(store);
			si.setUuid("12345");
			SurveyInstanceDAO dao = new SurveyInstanceDAO();
			si = dao.save(si);
			ans.setSurveyInstanceId(si.getKey().getId());
			dao.save(ans);
			// Queue summQueue = QueueFactory.getQueue("dataSummarization");
			// summQueue.add(TaskOptions.Builder
			// .withUrl("/app_worker/datasummarization")
			// .param("objectKey", si.getKey().getId() + "")
			// .param("type", "SurveyInstance"));
		} else if ("createCommunity".equals(action)) {
			CommunityDao dao = new CommunityDao();
			Country c = new Country();
			c.setIsoAlpha2Code("CA");
			c.setName("Canada");
			c.setDisplayName("Canada");
			Community comm = new Community();
			comm.setCommunityCode("ON");
			dao.save(c);

			comm.setCountryCode("CA");
			comm.setLat(54.99);
			comm.setLon(-74.72);

			dao.save(comm);

			c = new Country();
			c.setIsoAlpha2Code("US");
			c.setName("United States");
			c.setDisplayName("Unites States");
			comm = new Community();
			comm.setCommunityCode("Omaha");
			comm.setCountryCode("US");
			comm.setLat(34.99);
			comm.setLon(-74.72);

			dao.save(c);
			dao.save(comm);

		} else if ("addPhone".equals(action)) {
			String phoneNumber = req.getParameter("phoneNumber");
			Device d = new Device();
			d.setPhoneNumber(phoneNumber);
			d.setDeviceType(DeviceType.CELL_PHONE_ANDROID);

			if (req.getParameter("esn") != null)
				d.setEsn(req.getParameter("esn"));
			if (req.getParameter("gallatinSoftwareManifest") != null)
				d.setGallatinSoftwareManifest(req
						.getParameter("gallatinSoftwareManifest"));

			d.setInServiceDate(new Date());
			DeviceDAO deviceDao = new DeviceDAO();
			deviceDao.save(d);
			try {
				resp.getWriter().println("finished adding " + phoneNumber);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if ("createAPSummary".equals(action)) {
			AccessPointStatusSummary sum = new AccessPointStatusSummary();
			sum.setCommunity("ON");
			sum.setCountry("CA");
			sum.setType(AccessPointType.WATER_POINT.toString());
			sum.setYear("2000");
			sum.setStatus(AccessPoint.Status.FUNCTIONING_HIGH);
			AccessPointStatusSummaryDao dao = new AccessPointStatusSummaryDao();
			dao.save(sum);

			sum = new AccessPointStatusSummary();
			sum.setCommunity("ON");
			sum.setCountry("CA");
			sum.setType(AccessPointType.WATER_POINT.toString());
			sum.setYear("2001");
			sum.setStatus(AccessPoint.Status.FUNCTIONING_HIGH);
			dao.save(sum);

			sum = new AccessPointStatusSummary();
			sum.setCommunity("ON");
			sum.setCountry("CA");
			sum.setType(AccessPointType.WATER_POINT.toString());
			sum.setYear("2003");
			sum.setStatus(AccessPoint.Status.FUNCTIONING_HIGH);
			dao.save(sum);

			sum = new AccessPointStatusSummary();
			sum.setCommunity("ON");
			sum.setCountry("CA");
			sum.setType(AccessPointType.WATER_POINT.toString());
			sum.setYear("2004");
			sum.setStatus(AccessPoint.Status.FUNCTIONING_OK);
			dao.save(sum);

			sum.setCommunity("NY");
			sum.setCountry("US");
			sum.setType(AccessPointType.WATER_POINT.toString());
			sum.setYear("2000");
			sum.setStatus(AccessPoint.Status.FUNCTIONING_HIGH);
			dao.save(sum);

			sum = new AccessPointStatusSummary();
			sum.setCommunity("NY");
			sum.setCountry("US");
			sum.setType(AccessPointType.WATER_POINT.toString());
			sum.setYear("2001");
			sum.setStatus(AccessPoint.Status.FUNCTIONING_HIGH);
			dao.save(sum);

			sum = new AccessPointStatusSummary();
			sum.setCommunity("NY");
			sum.setCountry("US");
			sum.setType(AccessPointType.WATER_POINT.toString());
			sum.setYear("2003");
			sum.setStatus(AccessPoint.Status.FUNCTIONING_HIGH);
			dao.save(sum);

			sum = new AccessPointStatusSummary();
			sum.setCommunity("NY");
			sum.setCountry("US");
			sum.setType(AccessPointType.WATER_POINT.toString());
			sum.setYear("2004");
			sum.setStatus(AccessPoint.Status.FUNCTIONING_OK);
			dao.save(sum);
		} else if ("createApHistory".equals(action)) {
			GregorianCalendar cal = new GregorianCalendar();
			AccessPointHelper apHelper = new AccessPointHelper();

			AccessPoint ap = new AccessPoint();
			cal.set(Calendar.YEAR, 2000);
			ap.setCollectionDate(cal.getTime());
			ap.setCommunityCode("Geneva");
			ap.setPointStatus(Status.FUNCTIONING_OK);
			ap.setLatitude(47.3);
			ap.setLongitude(9d);
			ap.setNumberOfHouseholdsUsingPoint(300l);
			ap.setCostPer(43.20);
			ap.setPointType(AccessPointType.WATER_POINT);

			apHelper.saveAccessPoint(ap);

			ap = new AccessPoint();
			cal.set(Calendar.YEAR, 2001);
			ap.setCollectionDate(cal.getTime());
			ap.setCommunityCode("Geneva");
			ap.setPointStatus(Status.FUNCTIONING_OK);
			ap.setLatitude(47.3);
			ap.setLongitude(9d);
			ap.setNumberOfHouseholdsUsingPoint(317l);
			ap.setCostPer(40.20);
			ap.setPointType(AccessPointType.WATER_POINT);

			apHelper.saveAccessPoint(ap);

			ap = new AccessPoint();
			cal.set(Calendar.YEAR, 2002);
			ap.setCollectionDate(cal.getTime());
			ap.setCommunityCode("Geneva");
			ap.setPointStatus(Status.FUNCTIONING_OK);
			ap.setLatitude(47.3);
			ap.setLongitude(9d);
			ap.setNumberOfHouseholdsUsingPoint(340l);
			ap.setCostPer(37.20);
			ap.setPointType(AccessPointType.WATER_POINT);

			apHelper.saveAccessPoint(ap);

			ap = new AccessPoint();
			cal.set(Calendar.YEAR, 2003);
			ap.setCollectionDate(cal.getTime());
			ap.setCommunityCode("Geneva");
			ap.setPointStatus(Status.FUNCTIONING_HIGH);
			ap.setLatitude(47.3);
			ap.setLongitude(9d);
			ap.setNumberOfHouseholdsUsingPoint(340l);
			ap.setCostPer(34.20);
			ap.setPointType(AccessPointType.WATER_POINT);

			apHelper.saveAccessPoint(ap);

			ap = new AccessPoint();
			cal.set(Calendar.YEAR, 2004);
			ap.setCollectionDate(cal.getTime());
			ap.setCommunityCode("Geneva");
			ap.setPointStatus(Status.FUNCTIONING_OK);
			ap.setLatitude(47.3);
			ap.setLongitude(9d);
			ap.setNumberOfHouseholdsUsingPoint(338l);
			ap.setCostPer(38.20);
			ap.setPointType(AccessPointType.WATER_POINT);

			apHelper.saveAccessPoint(ap);

			ap = new AccessPoint();
			cal.set(Calendar.YEAR, 2000);
			ap.setCollectionDate(cal.getTime());
			ap.setCommunityCode("Omaha");
			ap.setPointStatus(Status.FUNCTIONING_HIGH);
			ap.setLatitude(40.87d);
			ap.setLongitude(-95.2d);
			ap.setNumberOfHouseholdsUsingPoint(170l);
			ap.setCostPer(19.20);
			ap.setPointType(AccessPointType.WATER_POINT);

			apHelper.saveAccessPoint(ap);

			ap = new AccessPoint();
			cal.set(Calendar.YEAR, 2001);
			ap.setCollectionDate(cal.getTime());
			ap.setCommunityCode("Omaha");
			ap.setPointStatus(Status.FUNCTIONING_HIGH);
			ap.setLatitude(40.87d);
			ap.setLongitude(-95.2d);
			ap.setNumberOfHouseholdsUsingPoint(201l);
			ap.setCostPer(19.00);
			ap.setPointType(AccessPointType.WATER_POINT);

			apHelper.saveAccessPoint(ap);

			ap = new AccessPoint();
			cal.set(Calendar.YEAR, 2002);
			ap.setCollectionDate(cal.getTime());
			ap.setCommunityCode("Omaha");
			ap.setPointStatus(Status.FUNCTIONING_HIGH);
			ap.setLatitude(40.87d);
			ap.setLongitude(-95.2d);
			ap.setNumberOfHouseholdsUsingPoint(211l);
			ap.setCostPer(17.20);
			ap.setPointType(AccessPointType.WATER_POINT);

			apHelper.saveAccessPoint(ap);

			ap = new AccessPoint();
			cal.set(Calendar.YEAR, 2003);
			ap.setCollectionDate(cal.getTime());
			ap.setCommunityCode("Omaha");
			ap.setPointStatus(Status.FUNCTIONING_WITH_PROBLEMS);
			ap.setLatitude(40.87d);
			ap.setLongitude(-95.2d);
			ap.setNumberOfHouseholdsUsingPoint(220l);
			ap.setCostPer(25.20);
			ap.setPointType(AccessPointType.WATER_POINT);

			apHelper.saveAccessPoint(ap);

			ap = new AccessPoint();
			cal.set(Calendar.YEAR, 2004);
			ap.setCollectionDate(cal.getTime());
			ap.setCommunityCode("Omaha");
			ap.setPointStatus(Status.FUNCTIONING_OK);
			ap.setLatitude(40.87d);
			ap.setLongitude(-95.2d);
			ap.setNumberOfHouseholdsUsingPoint(175l);
			ap.setCostPer(24.20);
			ap.setPointType(AccessPointType.WATER_POINT);

			apHelper.saveAccessPoint(ap);
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
		} else if ("loadExistingSurvey".equals(action)) {
			SurveyGroup sg = new SurveyGroup();

			sg.setKey(KeyFactory.createKey(SurveyGroup.class.getSimpleName(),
					2L));
			sg.setName("test" + new Date());
			sg.setCode("test" + new Date());
			SurveyGroupDAO sgDao = new SurveyGroupDAO();
			sgDao.save(sg);
			Survey s = new Survey();
			s.setKey(KeyFactory.createKey(Survey.class.getSimpleName(), 2L));
			s.setName("test" + new Date());
			s.setSurveyGroupId(sg.getKey().getId());
			SurveyDAO surveyDao = new SurveyDAO();
			surveyDao.save(s);

		} else if ("saveAPMapping".equals(action)) {
			SurveyAttributeMapping mapping = new SurveyAttributeMapping();
			mapping.setAttributeName("status");
			mapping.setObjectName(AccessPoint.class.getCanonicalName());
			mapping.setSurveyId(1L);
			mapping.setSurveyQuestionId("q1");
			SurveyAttributeMappingDao samDao = new SurveyAttributeMappingDao();
			samDao.save(mapping);
		} else if ("listAPMapping".equals(action)) {
			SurveyAttributeMappingDao samDao = new SurveyAttributeMappingDao();
			List<SurveyAttributeMapping> mappings = samDao
					.listMappingsBySurvey(1L);
			if (mappings != null) {
				System.out.println(mappings.size());
			}
		} else if ("saveSurveyGroup".equals(action)) {
			try {
				SurveyGroupDAO sgDao = new SurveyGroupDAO();
				List<SurveyGroup> sgList = sgDao.list("all");
				for (SurveyGroup sg : sgList) {
					sgDao.delete(sg);
				}
				resp.getWriter().println("Deleted all survey groups");

				SurveyDAO surveyDao = new SurveyDAO();
				List<Survey> surveyList = surveyDao.list("all");
				for (Survey survey : surveyList) {
					try {
						surveyDao.delete(survey);
					} catch (IllegalDeletionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				resp.getWriter().println("Deleted all surveys");

				resp.getWriter().println("Deleted all surveysurveygroupassocs");
				QuestionGroupDao qgDao = new QuestionGroupDao();
				List<QuestionGroup> qgList = qgDao.list("all");
				for (QuestionGroup qg : qgList) {
					qgDao.delete(qg);
				}
				resp.getWriter().println("Deleted all question groups");

				QuestionDao qDao = new QuestionDao();
				List<Question> qList = qDao.list("all");
				for (Question q : qList) {
					try {
						qDao.delete(q);
					} catch (IllegalDeletionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				resp.getWriter().println("Deleted all Questions");

				QuestionOptionDao qoDao = new QuestionOptionDao();
				List<QuestionOption> qoList = qoDao.list("all");
				for (QuestionOption qo : qoList)
					qoDao.delete(qo);
				resp.getWriter().println("Deleted all QuestionOptions");

				resp.getWriter().println("Deleted all questions");

				resp.getWriter().println(
						"Finished deleting and reloading SurveyGroup graph");
			} catch (IOException e) {

				e.printStackTrace();
			}
		} else if ("testPublishSurvey".equals(action)) {
			try {
				SurveyGroupDto sgDto = new SurveyServiceImpl()
						.listSurveyGroups(null, true, false, false)
						.getPayload().get(0);
				resp.getWriter().println(
						"Got Survey Group: " + sgDto.getCode() + " Survey: "
								+ sgDto.getSurveyList().get(0).getKeyId());
				SurveyContainerDao scDao = new SurveyContainerDao();
				SurveyContainer sc = scDao.findBySurveyId(sgDto.getSurveyList()
						.get(0).getKeyId());
				if (sc != null) {
					scDao.delete(sc);
					resp.getWriter().println(
							"Deleted existing SurveyContainer for: "
									+ sgDto.getSurveyList().get(0).getKeyId());
				}
				resp.getWriter().println(
						"Result of publishing survey: "
								+ new SurveyServiceImpl().publishSurvey(sgDto
										.getSurveyList().get(0).getKeyId()));
				sc = scDao.findBySurveyId(sgDto.getSurveyList().get(0)
						.getKeyId());
				resp.getWriter().println(
						"Survey Document result from publish: \n\n\n\n"
								+ sc.getSurveyDocument().getValue());
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else if ("createTestSurveyForEndToEnd".equals(action)) {
			createTestSurveyForEndToEnd();
		} else if ("deleteSurveyFragments".equals(action)) {
			deleteAll(SurveyXMLFragment.class);
		} else if ("migratePIToSchool".equals(action)) {
			try {
				resp.getWriter().println(
						"Has more? "
								+ migratePointType(
										AccessPointType.PUBLIC_INSTITUTION,
										AccessPointType.SCHOOL));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("createDevice".equals(action)) {
			DeviceDAO devDao = new DeviceDAO();
			Device device = new Device();
			device.setPhoneNumber("9175667663");
			device.setDeviceType(DeviceType.CELL_PHONE_ANDROID);
			devDao.save(device);
		} else if ("reprocessSurveys".equals(action)) {
			try {
				reprocessSurveys(req.getParameter("date"));
			} catch (ParseException e) {
				try {
					resp.getWriter().println("Couldn't reprocess: " + e);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else if ("importallsurveys".equals(action)) {
			// Only run in dev hence hardcoding
			SurveyReplicationImporter sri = new SurveyReplicationImporter();
			sri.executeImport("http://watermapmonitordev.appspot.com", null, null);
			// sri.executeImport("http://localhost:8888",
			// "http://localhost:8888");

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
		} else if ("rescoreap".equals(action)) {
			TaskOptions options = TaskOptions.Builder
					.withUrl("/app_worker/dataprocessor")
					.param(DataProcessorRequest.ACTION_PARAM,
							DataProcessorRequest.RESCORE_AP_ACTION)
					.param(DataProcessorRequest.COUNTRY_PARAM,
							req.getParameter("country"));
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
		} else if ("fixNameQuestion".equals(action)) {
			if (req.getParameter("questionId") == null) {
				try {
					resp.getWriter().println(
							"questionId is a required parameter");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				fixNameQuestion(req.getParameter("questionId"));
			}
		} else if ("createSurveyAssignment".equals(action)) {
			Device device = new Device();
			device.setDeviceType(DeviceType.CELL_PHONE_ANDROID);
			device.setPhoneNumber("1111111111");
			device.setInServiceDate(new Date());

			BaseDAO<Device> deviceDao = new BaseDAO<Device>(Device.class);
			deviceDao.save(device);
			SurveyAssignmentServiceImpl sasi = new SurveyAssignmentServiceImpl();
			SurveyAssignmentDto dto = new SurveyAssignmentDto();
			SurveyDAO surveyDao = new SurveyDAO();
			List<Survey> surveyList = surveyDao.list("all");
			SurveyAssignment sa = new SurveyAssignment();
			BaseDAO<SurveyAssignment> surveyAssignmentDao = new BaseDAO<SurveyAssignment>(
					SurveyAssignment.class);
			sa.setCreatedDateTime(new Date());
			sa.setCreateUserId(-1L);
			ArrayList<Long> deviceList = new ArrayList<Long>();
			deviceList.add(device.getKey().getId());
			sa.setDeviceIds(deviceList);
			ArrayList<SurveyDto> surveyDtoList = new ArrayList<SurveyDto>();

			for (Survey survey : surveyList) {
				sa.addSurvey(survey.getKey().getId());
				SurveyDto surveyDto = new SurveyDto();
				surveyDto.setKeyId(survey.getKey().getId());
				surveyDtoList.add(surveyDto);
			}
			sa.setStartDate(new Date());
			sa.setEndDate(new Date());
			sa.setName(new Date().toString());

			DeviceDto deviceDto = new DeviceDto();
			deviceDto.setKeyId(device.getKey().getId());
			deviceDto.setPhoneNumber(device.getPhoneNumber());
			ArrayList<DeviceDto> deviceDtoList = new ArrayList<DeviceDto>();
			deviceDtoList.add(deviceDto);
			dto.setDevices(deviceDtoList);
			dto.setSurveys(surveyDtoList);
			dto.setEndDate(new Date());
			dto.setLanguage("en");
			dto.setName("Test Assignment: " + new Date().toString());
			dto.setStartDate(new Date());
			sasi.saveSurveyAssignment(dto);

			// sasi.deleteSurveyAssignment(dto);
		} else if ("populateAssignmentId".equalsIgnoreCase(action)) {
			populateAssignmentId(Long.parseLong(req
					.getParameter("assignmentId")));
		} else if ("testDSJQDelete".equals(action)) {
			DeviceSurveyJobQueueDAO dsjDAO = new DeviceSurveyJobQueueDAO();
			Calendar cal = Calendar.getInstance();
			Date now = cal.getTime();
			cal.add(Calendar.DAY_OF_MONTH, -10);
			Date then = cal.getTime();
			DeviceSurveyJobQueue dsjq = new DeviceSurveyJobQueue();
			dsjq.setDevicePhoneNumber("2019561591");
			dsjq.setEffectiveEndDate(then);
			Random rand = new Random();
			dsjq.setAssignmentId(rand.nextLong());
			dsjDAO.save(dsjq);

			DeviceSurveyJobQueue dsjq2 = new DeviceSurveyJobQueue();
			dsjq2.setDevicePhoneNumber("2019561591");
			cal.add(Calendar.DAY_OF_MONTH, 20);
			dsjq2.setEffectiveEndDate(cal.getTime());
			dsjq2.setAssignmentId(rand.nextLong());
			dsjDAO.save(dsjq2);

			DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
			List<DeviceSurveyJobQueue> dsjqList = dsjqDao
					.listAssignmentsWithEarlierExpirationDate(new Date());
			for (DeviceSurveyJobQueue item : dsjqList) {
				SurveyTaskUtil.spawnDeleteTask("deleteDeviceSurveyJobQueue",
						item.getAssignmentId());
			}
		} else if ("loadDSJ".equals(action)) {
			SurveyDAO surveyDao = new SurveyDAO();
			List<Survey> surveyList = surveyDao.list("all");
			for (Survey item : surveyList) {
				DeviceSurveyJobQueueDAO dsjDAO = new DeviceSurveyJobQueueDAO();
				Calendar cal = Calendar.getInstance();
				Date now = cal.getTime();
				cal.add(Calendar.DAY_OF_MONTH, -10);
				Date then = cal.getTime();
				DeviceSurveyJobQueue dsjq = new DeviceSurveyJobQueue();
				dsjq.setDevicePhoneNumber("2019561591");
				dsjq.setEffectiveEndDate(then);
				Random rand = new Random();
				dsjq.setAssignmentId(rand.nextLong());
				dsjq.setSurveyID(item.getKey().getId());
				dsjDAO.save(dsjq);
			}

			for (int i = 0; i < 20; i++) {
				DeviceSurveyJobQueueDAO dsjDAO = new DeviceSurveyJobQueueDAO();
				Calendar cal = Calendar.getInstance();
				Date now = cal.getTime();
				cal.add(Calendar.DAY_OF_MONTH, -10);
				Date then = cal.getTime();
				DeviceSurveyJobQueue dsjq = new DeviceSurveyJobQueue();
				dsjq.setDevicePhoneNumber("2019561591");
				dsjq.setEffectiveEndDate(then);
				Random rand = new Random();
				dsjq.setAssignmentId(rand.nextLong());
				dsjq.setSurveyID(rand.nextLong());
				dsjDAO.save(dsjq);

			}
			try {
				resp.getWriter().println("finished");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if ("deleteUnusedDSJQueue".equals(action)) {
			try {
				SurveyDAO surveyDao = new SurveyDAO();
				List<Key> surveyIdList = surveyDao.listSurveyIds();
				List<Long> ids = new ArrayList<Long>();

				for (Key key : surveyIdList)
					ids.add(key.getId());

				DeviceSurveyJobQueueDAO dsjqDao = new DeviceSurveyJobQueueDAO();
				List<DeviceSurveyJobQueue> deleteList = new ArrayList<DeviceSurveyJobQueue>();
				for (DeviceSurveyJobQueue item : dsjqDao.listAllJobsInQueue()) {
					Long dsjqSurveyId = item.getSurveyID();
					Boolean found = ids.contains(dsjqSurveyId);
					if (!found) {
						deleteList.add(item);
						resp.getWriter().println(
								"Marking " + item.getId() + " survey: "
										+ item.getSurveyID() + " for deletion");
					}
				}
				dsjqDao.delete(deleteList);

				resp.getWriter().println("finished");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if ("testListTrace".equals(action)) {
			listStacktrace();
		} else if ("createEditorialContent".equals(action)) {
			createEditorialContent(req.getParameter("pageName"));
		} else if ("generateEditorialContent".equals(action)) {
			try {
				resp.getWriter().print(
						generateEditorialContent(req.getParameter("pageName")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("populateperms".equals(action)) {
			populatePermissions();
		} else if ("testnotif".equals(action)) {
			sendNotification(req.getParameter("surveyId"));
		} else if ("popsurvey".equals(action)) {
			SurveyDAO sDao = new SurveyDAO();
			List<Survey> sList = sDao.list(null);
			QuestionDao questionDao = new QuestionDao();
			List<Question> qList = questionDao.listQuestionByType(sList.get(0)
					.getKey().getId(), Question.Type.FREE_TEXT);
			SurveyInstanceDAO instDao = new SurveyInstanceDAO();
			for (int i = 0; i < 10; i++) {
				SurveyInstance instance = new SurveyInstance();

				instance.setSurveyId(sList.get(0).getKey().getId());

				instance = instDao.save(instance);

				for (int j = 0; j < qList.size(); j++) {
					QuestionAnswerStore ans = new QuestionAnswerStore();
					ans.setQuestionID(qList.get(j).getKey().getId() + "");
					ans.setValue("" + j * i);
					ans.setSurveyInstanceId(instance.getKey().getId());
					// ans.setSurveyInstance(instance);
					instDao.save(ans);
				}
			}
			try {
				resp.getWriter().print(sList.get(0).getKey().getId());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if ("testnotifhelper".equals(action)) {
			NotificationHelper helper = new NotificationHelper("rawDataReport",
					null);
			helper.execute();
		} else if ("testremotemap".equals(action)) {
			createDevice("12345", 40.78, -73.95);
			createDevice("777", 43.0, -78.8);
			RemoteStacktrace st = new RemoteStacktrace();
			st.setAcknowleged(false);
			st.setPhoneNumber("12345");
			st.setErrorDate(new Date());
			st.setStackTrace(new Text("blah"));
			RemoteStacktraceDao dao = new RemoteStacktraceDao();
			dao.save(st);
			st = new RemoteStacktrace();
			st.setAcknowleged(false);
			st.setErrorDate(new Date());
			st.setPhoneNumber("777");
			st.setStackTrace(new Text("ugh"));
			dao.save(st);
		} else if ("createMetricMapping".equals(action)) {
			createMetricMapping(req.getParameter("metric"));
		} else if ("listMetricSummaries".equals(action)) {
			try {
				resp.getWriter().print(
						listMetricSummaries(
								new Integer(req.getParameter("level")),
								req.getParameter("name")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if ("deleteMetricSummaries".equals(action)) {
			deleteMetricSummaries(new Integer(req.getParameter("level")),
					req.getParameter("name"));
		} else if ("createSurveyQuestionSummary".equals(action)) {
			SurveyQuestionSummary sum = new SurveyQuestionSummary();
			sum.setCount(10L);
			sum.setResponse("TEST");
			sum.setQuestionId("2166031");
			SurveyQuestionSummaryDao dao = new SurveyQuestionSummaryDao();
			dao.save(sum);
			sum = new SurveyQuestionSummary();
			sum.setCount(20L);
			sum.setResponse("OTHER");
			sum.setQuestionId("2166031");
			dao.save(sum);
			sum = new SurveyQuestionSummary();
			sum.setCount(30L);
			sum.setResponse("GWAR");
			sum.setQuestionId("2166031");
			dao.save(sum);
		} else if ("createCountry".equals(action)) {
			Country country = new Country();
			country.setIsoAlpha2Code("LR");
			country.setName("Liberia");
			CountryDao dao = new CountryDao();
			dao.save(country);
		} else if ("populateSubCountry".equals(action)) {
			try {
				String country = req.getParameter("country");
				if (country != null) {
					Queue summQueue = QueueFactory
							.getQueue("dataSummarization");
					summQueue.add(TaskOptions.Builder
							.withUrl("/app_worker/datasummarization")
							.param("objectKey", country)
							.param("type", "OGRFeature"));
				} else {
					resp.getWriter()
							.println("country is a mandatory parameter");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("listSubCountry".equals(action)) {
			try {
				String country = req.getParameter("country");
				if (country != null) {
					SubCountryDao subDao = new SubCountryDao();
					List<SubCountry> results = subDao.list(null);
					if (results != null) {
						for (SubCountry c : results) {
							resp.getWriter().println(c.toString());
						}
					}
				} else {
					resp.getWriter()
							.println("country is a mandatory parameter");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("clearcache".equals(action)) {
			CacheFactory cacheFactory;
			try {
				cacheFactory = CacheManager.getInstance().getCacheFactory();
				Cache cache = cacheFactory.createCache(Collections.emptyMap());
				cache.clear();
			} catch (CacheException e) {
				e.printStackTrace();
			}
		} else if ("startProjectFlagUpdate".equals(action)) {
			DataProcessorRestServlet.sendProjectUpdateTask(
					req.getParameter("country"), null);
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
		} else if ("fixNullSubmitter".equals(action)) {
			TaskOptions options = TaskOptions.Builder.withUrl(
					"/app_worker/dataprocessor").param(
					DataProcessorRequest.ACTION_PARAM,
					DataProcessorRequest.FIX_NULL_SUBMITTER_ACTION);
			com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
					.getDefaultQueue();
			queue.add(options);
		} else if ("createVals".equals(action)) {
			SurveyedLocaleDao localeDao = new SurveyedLocaleDao();
			List<SurveyedLocale> lList = localeDao.list(null);
			if (lList != null && lList.size() > 0) {
				List<SurveyalValue> valList = new ArrayList<SurveyalValue>();
				for (int i = 0; i < 50; i++) {
					SurveyalValue val = new SurveyalValue();
					val.setSurveyedLocaleId(lList.get(0).getKey().getId());
					val.setStringValue("val:" + i);
					val.setQuestionText("TEXT: " + i);
					val.setLocaleType(lList.get(0).getLocaleType());
					val.setQuestionType("FREE_TEXT");
					val.setSurveyInstanceId(lList.get(0)
							.getLastSurveyalInstanceId());
					valList.add(val);
				}

				localeDao.save(valList);
			}
		} else if ("fixDuplicateOtherText".equals(action)) {
			TaskOptions options = TaskOptions.Builder.withUrl(
					"/app_worker/dataprocessor").param(
					DataProcessorRequest.ACTION_PARAM,
					DataProcessorRequest.FIX_DUPLICATE_OTHER_TEXT_ACTION);
			com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
					.getDefaultQueue();
			queue.add(options);
		} else if ("fixNullGroupNames".equals(action)) {
			fixNullQuestionGroupNames();
		} else if ("trimOptions".equals(action)) {
			TaskOptions options = TaskOptions.Builder.withUrl(
					"/app_worker/dataprocessor").param(
					DataProcessorRequest.ACTION_PARAM,
					DataProcessorRequest.TRIM_OPTIONS);
			if (req.getParameter("bypassBackend") == null
					|| !req.getParameter("bypassBackend").equals("true")) {
				// change the host so the queue invokes the backend
				options = options
						.header("Host",
								BackendServiceFactory.getBackendService()
										.getBackendAddress("dataprocessor"));
			}
			com.google.appengine.api.taskqueue.Queue queue = com.google.appengine.api.taskqueue.QueueFactory
					.getDefaultQueue();
			queue.add(options);
		} else if ("testTemplateOverride".equals(action)) {
			KMLGenerator gen = new KMLGenerator();
			SurveyedLocale ap = new SurveyedLocale();
			ap.setAmbiguous(false);
			ap.setCountryCode("US");
			ap.setCreatedDateTime(new Date());
			ap.setCurrentStatus("OK");
			ap.setIdentifier("1234");
			ap.setLastSurveyedDate(new Date());
			ap.setLatitude(12d);
			ap.setLongitude(12d);
			try {
				String result = gen.bindPlacemark(ap,
						"localePlacemarkExternal.vm", null);
				resp.getWriter().println(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ("setSuperUser".equals(action)) {
			String email = req.getParameter("email");
			UserDao udao = new UserDao();
			User u = udao.findUserByEmail(email);
			if (u != null) {
				u.setSuperAdmin(true);
			}

		}else if("fixImages".equals(action)){
			String surveyId = req.getParameter("surveyId");
			String find = req.getParameter("find");
			String replace = req.getParameter("replace");
			if(surveyId != null && !surveyId.trim().isEmpty() && find!=null && !find.trim().isEmpty()){
				fixBadImage(surveyId,find,replace);
			}
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
							DataProcessorRequest.RECOMPUTE_LOCALE_CLUSTERS);
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
			try {
				// we try to parse the surveyId before we go further, and
				// we fail completely if we can't parse the surveyId.
				Long surveyId = Long.parseLong(req.getParameter("surveyId"));

				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(TaskOptions.Builder.withUrl("/app_worker/dataprocessor")
						.param(DataProcessorRequest.ACTION_PARAM,
						DataProcessorRequest.POP_QUESTION_ORDER_FIELDS_ACTION)
						.param("cursor", "")
						.param(DataProcessorRequest.SURVEY_ID_PARAM, surveyId.toString()));
			} catch (NumberFormatException e){
				log.log(Level.SEVERE, "surveyId provided not valid: " + req.getParameter("surveyId"));
			}
		}
	}

	private void fixBadImage(String surveyId, String findString, String replaceString){
		QuestionAnswerStoreDao dao = new QuestionAnswerStoreDao();
		String replaceVal = replaceString !=null?replaceString:"";
		List<QuestionAnswerStore> responses = dao.listByTypeAndDate("PHOTO", new Long(surveyId), null, Constants.ALL_RESULTS, null);
		if(responses != null){
			for(QuestionAnswerStore resp: responses){
				if(resp.getValue()!=null && resp.getValue().contains(findString)){
					resp.setValue(resp.getValue().replace(findString, replaceVal));
				}
			}
		}
	}
	private void fixNullQuestionGroupNames() {
		QuestionGroupDao dao = new QuestionGroupDao();
		List<QuestionGroup> groups = dao.listQuestionGroupsByName(null);
		if (groups != null) {
			for (QuestionGroup g : groups) {
				g.setName(g.getCode());
			}
		}
	}

	private void deleteMetricSummaries(Integer level, String name) {
		AccessPointMetricSummaryDao sumDao = new AccessPointMetricSummaryDao();
		AccessPointMetricSummary prototype = new AccessPointMetricSummary();
		prototype.setMetricName(name);
		prototype.setSubLevel(level);
		List<AccessPointMetricSummary> sumList = sumDao.listMetrics(prototype);
		if (sumList != null && sumList.size() > 0) {
			sumDao.delete(sumList);
		}
	}

	private String listMetricSummaries(Integer level, String name) {
		AccessPointMetricSummaryDao sumDao = new AccessPointMetricSummaryDao();
		AccessPointMetricSummary prototype = new AccessPointMetricSummary();
		prototype.setMetricName(name);
		prototype.setSubLevel(level);
		List<AccessPointMetricSummary> sumList = sumDao.listMetrics(prototype);
		StringBuilder buf = new StringBuilder();
		if (sumList != null) {
			for (AccessPointMetricSummary m : sumList) {
				buf.append(m.toString() + ", shard: " + m.getShardNum())
						.append("\n");
			}
		}
		return buf.toString();
	}

	private void createMetricMapping(String name) {
		AccessPointMetricMappingDao dao = new AccessPointMetricMappingDao();
		List<AccessPointMetricMapping> mappingList = dao.findMappings(null,
				null, name);
		if (mappingList == null || mappingList.isEmpty()) {
			AccessPointMetricMapping map = new AccessPointMetricMapping();
			map.setFieldName(name);
			map.setMetricName(name);
			dao.save(map);
		}
	}

	private Device createDevice(String num, Double lat, Double lon) {
		DeviceDAO devDao = new DeviceDAO();
		Device d = devDao.get(num);
		if (d == null) {
			d = new Device();
			d.setPhoneNumber(num);
		}
		if (lat != null) {
			d.setLastKnownLat(lat);
			d.setLastKnownLon(lon);
		}
		return devDao.save(d);
	}

	private void sendNotification(String surveyId) {
		// com.google.appengine.api.taskqueue.Queue queue =
		// com.google.appengine.api.taskqueue.QueueFactory.getQueue("notification");
		Queue queue = QueueFactory.getQueue("notification");

		queue.add(TaskOptions.Builder
				.withUrl("/notificationprocessor")
				.param(NotificationRequest.DEST_PARAM,
						"christopher.fagiani@gmail.com||christopher.fagiani@gmail.com")
				.param(NotificationRequest.DEST_OPT_PARAM, "ATTACHMENT||LINK")
				.param(NotificationRequest.SUB_ENTITY_PARAM, surveyId)
				.param(NotificationRequest.TYPE_PARAM, "fieldStatusReport"));
	}

	private void populatePermissions() {
		UserDao userDao = new UserDao();
		List<Permission> permList = userDao.listPermissions();
		if (permList == null) {
			permList = new ArrayList<Permission>();
		}
		savePerm("Edit Survey", permList, userDao);
		savePerm("Edit Users", permList, userDao);
		savePerm("Edit Access Point", permList, userDao);
		savePerm("Edit Editorial Content", permList, userDao);
		savePerm("Import Survey Data", permList, userDao);
		savePerm("Import Access Point Data", permList, userDao);
		savePerm("Upload Survey Data", permList, userDao);
		savePerm("Edit Raw Data", permList, userDao);
		savePerm("Admin", permList, userDao);
		savePerm("Publish Survey", permList, userDao);
		savePerm("Run Reports", permList, userDao);
		savePerm("Edit Immutability", permList, userDao);
		savePerm("View Messages", permList, userDao);
	}

	private void savePerm(String name, List<Permission> permList,
			UserDao userDao) {
		Permission p = new Permission(name);
		boolean found = false;
		for (Permission perm : permList) {
			if (perm.equals(p)) {
				found = true;
				break;
			}
		}
		if (!found) {
			userDao.save(p);
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

	private String generateEditorialContent(String pageName) {
		String content = "";
		EditorialPageDao dao = new EditorialPageDao();
		EditorialPage p = dao.findByTargetPage(pageName);
		List<EditorialPageContent> contentList = dao.listContentByPage(p
				.getKey().getId());

		try {
			RuntimeServices runtimeServices = RuntimeSingleton
					.getRuntimeServices();
			StringReader reader = new StringReader(p.getTemplate().getValue());
			SimpleNode node = runtimeServices.parse(reader, "dynamicTemplate");
			Template template = new Template();
			template.setRuntimeServices(runtimeServices);
			template.setData(node);
			template.initDocument();
			Context ctx = new VelocityContext();
			ctx.put("pages", contentList);
			StringWriter writer = new StringWriter();
			template.merge(ctx, writer);
			content = writer.toString();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not initialize velocity", e);
		}
		return content;
	}

	private void createEditorialContent(String pageName) {
		EditorialPageDao dao = new EditorialPageDao();

		EditorialPage page = new EditorialPage();
		page.setTargetFileName(pageName);
		page.setType("landing");
		page.setTemplate(new Text(
				"<html><head><title>Test Generated</title></head><body><h1>This is a test</h1><ul>#foreach( $pageContent in $pages )<li>$pageContent.heading : $pageContent.text.value</li>#end</ul>"));
		page = dao.save(page);
		EditorialPageContent content = new EditorialPageContent();
		List<EditorialPageContent> contentList = new ArrayList<EditorialPageContent>();
		content.setHeading("Heading 1");
		content.setText(new Text("this is some text"));
		content.setSortOrder(1L);
		content.setEditorialPageId(page.getKey().getId());
		contentList.add(content);
		content = new EditorialPageContent();
		content.setHeading("Heading 2");
		content.setText(new Text("this is more text"));
		content.setSortOrder(2L);
		content.setEditorialPageId(page.getKey().getId());
		contentList.add(content);
		dao.save(contentList);

	}

	private void listStacktrace() {
		RemoteStacktraceDao traceDao = new RemoteStacktraceDao();

		List<RemoteStacktrace> result = null;
		result = traceDao.listStacktrace(null, null, false, null);
		if (result != null) {
			System.out.println(result.size() + "");
		}
		result = traceDao.listStacktrace(null, null, true, null);
		if (result != null) {
			System.out.println(result.size() + "");
		}

		result = traceDao.listStacktrace("12345", null, true, null);
		if (result != null) {
			System.out.println(result.size() + "");
		}
		result = traceDao.listStacktrace("12345", "12345", true, null);
		if (result != null) {
			System.out.println(result.size() + "");
		}

		result = traceDao.listStacktrace(null, "12345", true, null);
		if (result != null) {
			System.out.println(result.size() + "");
		}

		result = traceDao.listStacktrace("12345", null, false, null);
		if (result != null) {
			System.out.println(result.size() + "");
		}
		result = traceDao.listStacktrace("12345", "12345", false, null);
		if (result != null) {
			System.out.println(result.size() + "");
		}

		result = traceDao.listStacktrace(null, "12345", false, null);
		if (result != null) {
			System.out.println(result.size() + "");
		}
	}

	private void populateAssignmentId(Long assignmentId) {
		BaseDAO<SurveyAssignment> assignmentDao = new BaseDAO<SurveyAssignment>(
				SurveyAssignment.class);
		SurveyAssignment assignment = assignmentDao.getByKey(assignmentId);
		DeviceSurveyJobQueueDAO jobDao = new DeviceSurveyJobQueueDAO();
		if (assignment != null) {
			for (Long sid : assignment.getSurveyIds()) {
				jobDao.updateAssignmentIdForSurvey(sid, assignmentId);
			}
		}
	}

	private void fixNameQuestion(String questionId) {
		Queue summQueue = QueueFactory.getQueue("dataUpdate");
		summQueue.add(TaskOptions.Builder.withUrl("/app_worker/dataupdate")
				.param("objectKey", questionId + "")
				.param("type", "NameQuestionFix"));
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

	private void reprocessSurveys(String date) throws ParseException {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		Date startDate = null;
		if (date != null) {
			DateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

			startDate = sdf.parse(date);

			List<SurveyInstance> instances = dao.listByDateRange(startDate,
					null);
			if (instances != null) {
				AccessPointHelper aph = new AccessPointHelper();
				for (SurveyInstance instance : instances) {
					aph.processSurveyInstance(instance.getKey().getId() + "");
				}
			}
		}
	}

	private boolean migratePointType(AccessPointType source,
			AccessPointType dest) {
		AccessPointDao pointDao = new AccessPointDao();
		List<AccessPoint> list = pointDao.searchAccessPoints(null, null, null,
				null, source.toString(), null, null, null, null, null, null,
				null);

		if (list != null && list.size() > 0) {
			for (AccessPoint point : list) {
				point.setPointType(dest);
				pointDao.save(point);
			}
		}
		if (list != null && list.size() == 20) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T extends BaseDomain> void deleteAll(Class<T> type) {
		BaseDAO<T> baseDao = new BaseDAO(type);
		List<T> items = baseDao.list("all");
		if (items != null) {
			for (T item : items) {
				baseDao.delete(item);
			}
		}
	}

	private void createTestSurveyForEndToEnd() {
		SurveyGroupDto sgd = new SurveyGroupDto();
		sgd.setCode("E2E Test");
		sgd.setDescription("end2end test");

		SurveyDto surveyDto = new SurveyDto();
		surveyDto.setDescription("e2e test");
		SurveyServiceImpl surveySvc = new SurveyServiceImpl();

		QuestionGroupDto qgd = new QuestionGroupDto();
		qgd.setCode("Question Group 1");
		qgd.setDescription("Question Group Desc");

		QuestionDto qd = new QuestionDto();
		qd.setText("Access Point Name:");
		qd.setType(QuestionType.FREE_TEXT);
		qgd.addQuestion(qd, 0);

		qd = new QuestionDto();
		qd.setText("Location:");
		qd.setType(QuestionType.GEO);
		qgd.addQuestion(qd, 1);

		qd = new QuestionDto();
		qd.setText("Photo");
		qd.setType(QuestionType.PHOTO);
		qgd.addQuestion(qd, 2);

		surveyDto.addQuestionGroup(qgd);

		surveyDto.setVersion("Version: 1");
		sgd.addSurvey(surveyDto);
		sgd = surveySvc.save(sgd);
		System.out.println(sgd.getKeyId());
	}

	private void writeImageToResponse(HttpServletResponse resp, String urlString) {
		resp.setContentType("image/jpeg");
		try {
			ServletOutputStream out = resp.getOutputStream();
			URL url = new URL(urlString);
			InputStream in = url.openStream();

			byte[] buffer = new byte[2048];
			int size;

			while ((size = in.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, size);
			}
			in.close();
			out.flush();
		} catch (Exception ex) {

		}
	}

	private void createSurveyGroupGraph(HttpServletResponse resp) {
		com.gallatinsystems.survey.dao.SurveyGroupDAO sgDao = new com.gallatinsystems.survey.dao.SurveyGroupDAO();
		BaseDAO<Translation> tDao = new BaseDAO<Translation>(Translation.class);

		for (Translation t : tDao.list("all"))
			tDao.delete(t);
		// clear out old surveys
		List<SurveyGroup> sgList = sgDao.list("all");
		for (SurveyGroup item : sgList)
			sgDao.delete(item);

		try {
			resp.getWriter().println("Finished clearing surveyGroup table");
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		SurveyDAO surveyDao = new SurveyDAO();
		QuestionGroupDao questionGroupDao = new QuestionGroupDao();
		QuestionDao questionDao = new QuestionDao();
		QuestionOptionDao questionOptionDao = new QuestionOptionDao();
		QuestionHelpMediaDao helpDao = new QuestionHelpMediaDao();
		for (int i = 0; i < 2; i++) {
			com.gallatinsystems.survey.domain.SurveyGroup sg = new com.gallatinsystems.survey.domain.SurveyGroup();
			sg.setCode(i + ":" + new Date());
			sg.setName(i + ":" + new Date());
			sg = sgDao.save(sg);
			for (int j = 0; j < 2; j++) {
				com.gallatinsystems.survey.domain.Survey survey = new com.gallatinsystems.survey.domain.Survey();
				survey.setCode(j + ":" + new Date());
				survey.setName(j + ":" + new Date());
				survey.setSurveyGroupId(sg.getKey().getId());
				survey.setPath(sg.getCode());
				survey = surveyDao.save(survey);
				Translation t = new Translation();
				t.setLanguageCode("es");
				t.setText(j + ":" + new Date());
				t.setParentType(ParentType.SURVEY_NAME);
				t.setParentId(survey.getKey().getId());
				tDao.save(t);
				survey.addTranslation(t);
				for (int k = 0; k < 3; k++) {
					com.gallatinsystems.survey.domain.QuestionGroup qg = new com.gallatinsystems.survey.domain.QuestionGroup();
					qg.setName("en:" + j + new Date());
					qg.setDesc("en:desc: " + j + new Date());
					qg.setCode("en:" + j + new Date());
					qg.setSurveyId(survey.getKey().getId());
					qg.setOrder(k);
					qg.setPath(sg.getCode() + "/" + survey.getCode());
					qg = questionGroupDao.save(qg);

					Translation t2 = new Translation();
					t2.setLanguageCode("es");
					t2.setParentType(ParentType.QUESTION_GROUP_NAME);
					t2.setText("es:" + k + new Date());
					t2.setParentId(qg.getKey().getId());
					tDao.save(t2);
					qg.addTranslation(t2);

					for (int l = 0; l < 2; l++) {
						com.gallatinsystems.survey.domain.Question q = new com.gallatinsystems.survey.domain.Question();
						q.setType(Type.OPTION);
						q.setAllowMultipleFlag(false);
						q.setAllowOtherFlag(false);
						q.setDependentFlag(false);
						q.setMandatoryFlag(true);
						q.setQuestionGroupId(qg.getKey().getId());
						q.setOrder(l);
						q.setText("en:" + l + ":" + new Date());
						q.setTip("en:" + l + ":" + new Date());
						q.setPath(sg.getCode() + "/" + survey.getCode() + "/"
								+ qg.getCode());
						q.setSurveyId(survey.getKey().getId());
						q = questionDao.save(q);

						Translation tq = new Translation();
						tq.setLanguageCode("es");
						tq.setText("es" + l + ":" + new Date());
						tq.setParentType(ParentType.QUESTION_TEXT);
						tq.setParentId(q.getKey().getId());
						tDao.save(tq);
						q.addTranslation(tq);
						for (int m = 0; m < 10; m++) {
							com.gallatinsystems.survey.domain.QuestionOption qo = new com.gallatinsystems.survey.domain.QuestionOption();
							qo.setOrder(m);
							qo.setText(m + ":" + new Date());
							qo.setCode(m + ":" + new Date());
							qo.setQuestionId(q.getKey().getId());
							qo = questionOptionDao.save(qo);

							Translation tqo = new Translation();
							tqo.setLanguageCode("es");
							tqo.setText("es:" + m + ":" + new Date());
							tqo.setParentType(ParentType.QUESTION_OPTION);
							tqo.setParentId(qo.getKey().getId());
							tDao.save(tqo);
							qo.addTranslation(tqo);
							q.addQuestionOption(qo);
						}
						for (int n = 0; n < 10; n++) {
							com.gallatinsystems.survey.domain.QuestionHelpMedia qhm = new com.gallatinsystems.survey.domain.QuestionHelpMedia();
							qhm.setText("en:" + n + ":" + new Date());
							qhm.setType(QuestionHelpMedia.Type.PHOTO);
							qhm.setResourceUrl("http://test.com/" + n + ".jpg");
							qhm.setQuestionId(q.getKey().getId());
							qhm = helpDao.save(qhm);

							Translation tqhm = new Translation();
							tqhm.setLanguageCode("es");
							tqhm.setText("es:" + n + ":" + new Date());
							tqhm.setParentType(ParentType.QUESTION_HELP_MEDIA_TEXT);
							tqhm.setParentId(qhm.getKey().getId());
							tDao.save(tqhm);
							qhm.addTranslation(tqhm);
							q.addHelpMedia(n, qhm);
						}
						qg.addQuestion(l, q);
					}
					survey.addQuestionGroup(k, qg);
				}
				sg.addSurvey(survey);
			}
			log.log(Level.INFO, "Finished Saving sg: " + sg.getKey().toString());
		}
	}

	private void setupMetrics() {
		MetricDao metricDao = new MetricDao();
		List<Metric> metrics = metricDao.listMetrics(null, null, null, "test",
				null);
		if (metrics != null) {
			metricDao.delete(metrics);
		}
		List<Metric> newMetrics = new ArrayList<Metric>();
		Metric m = new Metric();
		m.setName("Status");
		m.setOrganization("test");
		newMetrics.add(m);
		m = new Metric();
		m.setName("Households Served");
		m.setOrganization("test");
		newMetrics.add(m);
		metricDao.save(newMetrics);
	}
}
