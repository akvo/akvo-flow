package org.waterforpeople.mapping.app.web;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.analytics.MapSummarizer;
import org.waterforpeople.mapping.analytics.dao.AccessPointStatusSummaryDao;
import org.waterforpeople.mapping.analytics.domain.AccessPointStatusSummary;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.server.accesspoint.AccessPointManagerServiceImpl;
import org.waterforpeople.mapping.app.gwt.server.survey.SurveyAssignmentServiceImpl;
import org.waterforpeople.mapping.app.gwt.server.survey.SurveyServiceImpl;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.CommunityDao;
import org.waterforpeople.mapping.dao.DeviceFilesDao;
import org.waterforpeople.mapping.dao.SurveyAttributeMappingDao;
import org.waterforpeople.mapping.dao.SurveyContainerDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.dataexport.SurveyReplicationImporter;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.Status;
import org.waterforpeople.mapping.domain.Community;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyAssignment;
import org.waterforpeople.mapping.domain.SurveyAttributeMapping;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.domain.TechnologyType;
import org.waterforpeople.mapping.helper.AccessPointHelper;
import org.waterforpeople.mapping.helper.GeoRegionHelper;
import org.waterforpeople.mapping.helper.KMLHelper;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.Device.DeviceType;
import com.gallatinsystems.device.domain.DeviceFiles;
import com.gallatinsystems.device.domain.DeviceSurveyJobQueue;
import com.gallatinsystems.diagnostics.dao.RemoteStacktraceDao;
import com.gallatinsystems.diagnostics.domain.RemoteStacktrace;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.framework.exceptions.IllegalDeletionException;
import com.gallatinsystems.gis.geography.domain.Country;
import com.gallatinsystems.gis.map.dao.MapFragmentDao;
import com.gallatinsystems.gis.map.domain.MapFragment;
import com.gallatinsystems.gis.map.domain.MapFragment.FRAGMENTTYPE;
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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

public class TestHarnessServlet extends HttpServlet {
	private static Logger log = Logger.getLogger(TestHarnessServlet.class
			.getName());
	private static final long serialVersionUID = -5673118002247715049L;

	@SuppressWarnings("unused")
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String action = req.getParameter("action");
		if ("testSurveyOrdering".equals(action)) {
			SurveyGroupDAO sgDao = new SurveyGroupDAO();
			SurveyGroup sgItem = sgDao.list("all").get(0);
			sgItem = sgDao.getByKey(sgItem.getKey().getId(), true);

		} else if ("rotateImage".equals(action)) {
			AccessPointManagerServiceImpl apmI = new AccessPointManagerServiceImpl();
			String test1 = "http://waterforpeople.s3.amazonaws.com/images/wfpPhoto10062903227521.jpg";
			// String test2 =
			// "http://waterforpeople.s3.amazonaws.com/images/hn/ch003[1].jpg";
			writeImageToResponse(resp, test1);
			apmI.setUploadS3Flag(false);
			writeImageToResponse(resp, apmI.rotateImage(test1));
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

			/*
			 * createSurveyGroupGraph(resp); //SurveyGroupDAO sgDao = new
			 * SurveyGroupDAO(); List<SurveyGroup> sgList = sgDao.list("all");
			 * Survey survey = sgList.get(0).getSurveyList().get(0);
			 * QuestionAnswerStore qas = new QuestionAnswerStore();
			 * qas.setArbitratyNumber(1L);
			 * qas.setSurveyId(survey.getKey().getId()); qas.setQuestionID("1");
			 * qas.setValue("test"); QuestionAnswerStoreDao qasDao = new
			 * QuestionAnswerStoreDao(); qasDao.save(qas);
			 * 
			 * 
			 * for(SurveyGroup sg: sgList) sgDao.delete(sg);
			 */
		} else if ("addDeviceFiles".equals(action)) {
			DeviceFilesDao dfDao = new DeviceFilesDao();
			for (int i = 0; i < 100; i++) {
				DeviceFiles df = new DeviceFiles();
				df.setURI("http://www.yahoo.com/x.zip");
				df.setCreatedDateTime(new Date());
				df.setPhoneNumber("2019561591");
				DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
				java.util.Date date = new java.util.Date();
				String dateTime = dateFormat.format(date);
				df.setProcessDate(dateTime);
				dfDao.save(df);
			}
			Date today, yesterday;
			Calendar calendar;

			today = new Date();
			calendar = Calendar.getInstance();
			calendar.setTime(today);
			calendar.add(Calendar.DATE, -1);
			yesterday = calendar.getTime();
			for (DeviceFiles df : dfDao.listDeviceFilesByDate(yesterday, "all")) {
				try {
					resp.getWriter().print(
							df.getUploadDateTime() + " " + df.getURI() + "\n");
				} catch (IOException e) {
					log.log(Level.SEVERE, "could not get device files", e);
				}
			}
		} else if ("testBaseDomain".equals(action)) {

			SurveyDAO surveyDAO = new SurveyDAO();
			String outString = surveyDAO.getForTest();
			BaseDAO<AccessPoint> pointDao = new BaseDAO<AccessPoint>(
					AccessPoint.class);
			AccessPoint point = new AccessPoint();
			point.setLatitude(78d);
			point.setLongitude(43d);
			pointDao.save(point);
			try {
				resp.getWriter().print(outString);
			} catch (IOException e) {
				log.log(Level.SEVERE, "Could not execute test", e);
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
					MapSummarizer ms = new MapSummarizer();
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
			MapFragmentDao mfDao = new MapFragmentDao();
			AccessPointDao apDao = new AccessPointDao();
			for (int j = 0; j < 1; j++) {
				double lat = -15 + (new Random().nextDouble() / 10);
				double lon = 35 + (new Random().nextDouble() / 10);
				for (int i = 0; i < 15; i++) {
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
					// ap.setCollectionDate(calendar.getTime());
					ap.setAltitude(0.0);
					ap.setCommunityCode("test" + new Date());
					ap.setCommunityName("test" + new Date());
					ap.setPhotoURL("http://waterforpeople.s3.amazonaws.com/images/peru/pc28water.jpg");
					ap.setProvideAdequateQuantity(true);
					ap.setHasSystemBeenDown1DayFlag(false);
					ap.setMeetGovtQualityStandardFlag(true);
					ap.setMeetGovtQuantityStandardFlag(false);
					ap.setCurrentManagementStructurePoint("Community Board");
					ap.setDescription("Waterpoint");
					ap.setDistrict("test district");
					ap.setEstimatedHouseholds(100L);
					ap.setEstimatedPeoplePerHouse(11L);
					ap.setFarthestHouseholdfromPoint("Yes");
					ap.setNumberOfHouseholdsUsingPoint(100L);
					ap.setConstructionDateYear("2001");
					ap.setCostPer(1.0);
					ap.setCountryCode("MW");
					ap.setConstructionDate(new Date());
					ap.setCollectionDate(new Date());
					ap.setPhotoName("Water point");
					if (i % 2 == 0)
						ap.setPointType(AccessPoint.AccessPointType.WATER_POINT);
					else if (i % 3 == 0)
						ap.setPointType(AccessPoint.AccessPointType.SANITATION_POINT);
					else
						ap.setPointType(AccessPoint.AccessPointType.PUBLIC_INSTITUTION);
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
					MapSummarizer ms = new MapSummarizer();
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

		} else if ("loadCountries".equals(action)) {
			Country c = new Country();
			c.setIsoAlpha2Code("HN");
			c.setName("Honduras");

			BaseDAO<Country> countryDAO = new BaseDAO<Country>(Country.class);
			countryDAO.save(c);

			Country c2 = new Country();
			c2.setIsoAlpha2Code("MW");
			c2.setName("Malawi");
			countryDAO.save(c2);
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

			List<MapFragment> mfList = mfDao.searchMapFragments("ALL", null,
					null, null, FRAGMENTTYPE.GLOBAL_ALL_PLACEMARKS, "all",
					null, null);
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
			ap.setCommunityCode("Geneva");
			ap.setPointStatus(Status.FUNCTIONING_OK);
			ap.setLatitude(47.3);
			ap.setLongitude(9d);
			ap.setPointType(AccessPointType.WATER_POINT);
			AccessPointHelper helper = new AccessPointHelper();
			helper.saveAccessPoint(ap);

		} else if ("createInstance".equals(action)) {
			SurveyInstance si = new SurveyInstance();
			si.setCollectionDate(new Date());
			ArrayList<QuestionAnswerStore> store = new ArrayList<QuestionAnswerStore>();
			QuestionAnswerStore ans = new QuestionAnswerStore();
			ans.setQuestionID("q2");
			ans.setValue("Geneva");
			store.add(ans);
			si.setQuestionAnswersStore(store);
			SurveyInstanceDAO dao = new SurveyInstanceDAO();
			si = dao.save(si);
			Queue summQueue = QueueFactory.getQueue("dataSummarization");
			summQueue.add(url("/app_worker/datasummarization").param(
					"objectKey", si.getKey().getId() + "").param("type",
					"SurveyInstance"));
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
			sg.setName("test"+new Date());
			sg.setCode("test"+new Date());
			SurveyGroupDAO sgDao = new SurveyGroupDAO();
			sgDao.save(sg);
			Survey s = new Survey();
			s.setKey(KeyFactory.createKey(Survey.class.getSimpleName(),
					2L));
			s.setName("test"+new Date());
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
						.listSurveyGroups(null, true, false, false).get(0);
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
		}else if ("importallsurveys".equals(action)){
			//Only run in dev hence hardcoding
			SurveyReplicationImporter sri = new SurveyReplicationImporter();
			//sri.executeImport("http://watermapmonitordev.appspot.com", "http://localhost:8888");
			sri.executeImport("http://localhost:8888", "http://localhost:8888");
			
		}
		else if ("deleteSurveyResponses".equals(action)) {
			if (req.getParameter("surveyId") == null) {
				try {
					resp.getWriter()
							.println("surveyId is a required parameter");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {

				deleteSurveyResponses(
						Integer.parseInt(req.getParameter("surveyId")),
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
		}
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
		summQueue.add(url("/app_worker/dataupdate").param("objectKey",
				questionId + "").param("type", "NameQuestionFix"));
	}

	private boolean deleteSurveyResponses(Integer surveyId, Integer count) {
		SurveyInstanceDAO dao = new SurveyInstanceDAO();
		List<SurveyInstance> instances = dao.listSurveyInstanceBySurvey(
				new Long(surveyId), count != null ? count : 100);

		if (instances != null) {
			for (SurveyInstance instance : instances) {
				List<QuestionAnswerStore> questions = dao
						.listQuestionAnswerStore(instance.getKey().getId(),
								count);
				if (questions != null) {
					dao.delete(questions);
				}
				dao.delete(instance);
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
					null, null);
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
				null, source.toString(), null, null, null, null, null, null);

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

	@SuppressWarnings("unchecked")
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

	private void writeImageToResponse(HttpServletResponse resp,
			byte[] imageBytes) {
		resp.setContentType("image/jpeg");
		try {
			ServletOutputStream out = resp.getOutputStream();

			out.write(imageBytes, 0, imageBytes.length);
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
							qhm.setUrl("http://test.com/" + n + ".jpg");
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
}
