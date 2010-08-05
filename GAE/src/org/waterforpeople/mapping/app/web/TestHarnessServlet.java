package org.waterforpeople.mapping.app.web;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.io.IOException;
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
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.user.UserConfigDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;
import org.waterforpeople.mapping.app.gwt.server.survey.SurveyServiceImpl;
import org.waterforpeople.mapping.app.gwt.server.user.UserServiceImpl;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.CommunityDao;
import org.waterforpeople.mapping.dao.SurveyAttributeMappingDao;
import org.waterforpeople.mapping.dao.SurveyContainerDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.Community;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyAttributeMapping;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.domain.SurveyQuestion;
import org.waterforpeople.mapping.domain.TechnologyType;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.Status;
import org.waterforpeople.mapping.domain.SurveyQuestion.QuestionAnswerType;
import org.waterforpeople.mapping.helper.AccessPointHelper;
import org.waterforpeople.mapping.helper.GeoRegionHelper;
import org.waterforpeople.mapping.helper.KMLHelper;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.device.dao.DeviceDAO;
import com.gallatinsystems.device.domain.Device;
import com.gallatinsystems.device.domain.Device.DeviceType;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.gis.geography.domain.Country;
import com.gallatinsystems.gis.map.dao.MapFragmentDao;
import com.gallatinsystems.gis.map.domain.MapFragment;
import com.gallatinsystems.gis.map.domain.MapFragment.FRAGMENTTYPE;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.QuestionHelpMediaDao;
import com.gallatinsystems.survey.dao.QuestionOptionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.SurveyXMLFragmentDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionHelpMedia;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyXMLFragment;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Question.Type;
import com.gallatinsystems.survey.domain.Translation.ParentType;
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
				builder.append("1,").append("" + i).append(",test,").append(
						20 + i + ",").append(30 + i + "\n");
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

		} else if ("loadLots".equals(action)) {
			MapFragmentDao mfDao = new MapFragmentDao();
			AccessPointDao apDao = new AccessPointDao();

			for (int i = 0; i < 2000; i++) {
				AccessPoint ap = new AccessPoint();
				ap.setCollectionDate(new Date());
				ap.setLatitude(15 + (new Random().nextDouble() / 10));
				ap.setLongitude(-90 + (new Random().nextDouble() / 10));
				ap.setAltitude(0.0);
				ap.setCommunityCode("test" + new Date());
				ap.setCommunityName("test" + new Date());
				ap.setPhotoURL("http://test.com");
				ap.setPointType(AccessPoint.AccessPointType.WATER_POINT);
				ap.setPointStatus(AccessPoint.Status.FUNCTIONING_OK);
				if (i % 2 == 0)
					ap.setCountryCode("MW");
				else
					ap.setCountryCode("GT");
				if (i % 2 == 0)
					ap.setTypeTechnologyString("Kiosk");
				else
					ap.setTypeTechnologyString("Afridev Handpump");
				apDao.save(ap);
				MapSummarizer ms = new MapSummarizer();
				ms.performSummarization("" + ap.getKey().getId(), "");
				if (i % 50 == 0)
					log.log(Level.INFO, "Loaded to " + i);
			}
			try {
				resp.getWriter().println("Finished loading aps");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if ("testAPKml".equals(action)) {
			MapFragmentDao mfDao = new MapFragmentDao();
			// for (MapFragment item : mfDao.list("all")) {
			// mfDao.delete(item);
			// }
			//			
			// BaseDAO<Country> countryDao = new
			// BaseDAO<Country>(Country.class);
			// for(Country c:countryDao.list("all"))
			// countryDao.delete(c);
			//			
			// Country c1 = new Country();
			// c1.setIsoAlpha2Code("MW");
			// countryDao.save(c1);
			// Country c2 = new Country();
			// c2.setIsoAlpha2Code("GT");
			// countryDao.save(c2);
			//			
			// AccessPointDao apDao = new AccessPointDao();
			// for (AccessPoint ap : apDao.list("all")) {
			// apDao.delete(ap);
			//
			// }
			// for (int i = 0; i < 2000; i++) {
			// AccessPoint ap = new AccessPoint();
			// ap.setCollectionDate(new Date());
			// ap.setLatitude(15 + (new Random().nextDouble() / 10));
			// ap.setLongitude(-90 + (new Random().nextDouble() / 10));
			// ap.setAltitude(0.0);
			// ap.setCommunityCode("test" + new Date());
			// ap.setCommunityName("test" + new Date());
			// ap.setPhotoURL("http://test.com");
			// ap.setPointType(AccessPoint.AccessPointType.WATER_POINT);
			// ap.setPointStatus(AccessPoint.Status.FUNCTIONING_OK);
			// if (i % 2 == 0)
			// ap.setCountryCode("MW");
			// else
			// ap.setCountryCode("GT");
			// if (i % 2 == 0)
			// ap.setTypeTechnologyString("Kiosk");
			// else
			// ap.setTypeTechnologyString("Afridev Handpump");
			// apDao.save(ap);
			// MapSummarizer ms = new MapSummarizer();
			// ms.performSummarization("" + ap.getKey().getId(), "");
			//
			// }

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
					null, null, FRAGMENTTYPE.GLOBAL_ALL_PLACEMARKS, "all");
			try {

				for (MapFragment mfItem : mfList) {
					String contents = ZipUtil
							.unZip(mfItem.getBlob().getBytes());
					log
							.log(Level.INFO, "Contents Length: "
									+ contents.length());
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
			SurveyGroupDAO sgDao = new SurveyGroupDAO();
			SurveyDAO surveyDao = new SurveyDAO();
			QuestionGroupDao qgDao = new QuestionGroupDao();
			QuestionDao qDao = new QuestionDao();
			BaseDAO<Translation> tDao = new BaseDAO<Translation>(
					Translation.class);
			QuestionHelpMediaDao qhmDao = new QuestionHelpMediaDao();
			QuestionOptionDao qoDao = new QuestionOptionDao();

			for (SurveyGroup sg : sgDao.list("all"))
				sgDao.delete(sg);
			for (Survey s : surveyDao.list("all"))
				surveyDao.delete(s);
			for (QuestionGroup qg : qgDao.list("all"))
				qgDao.delete(qg);
			for (Question q : qDao.list("all"))
				qDao.delete(q);
			for (Translation t : tDao.list("all"))
				tDao.delete(t);
			for (QuestionOption qo : qoDao.list("all"))
				qoDao.delete(qo);
			for (QuestionHelpMedia qhm : qhmDao.list("all"))
				qhmDao.delete(qhm);
			try {
				resp.getWriter().println("Finished deleting survey graph");
			} catch (IOException iex) {
				log.log(Level.SEVERE, "couldn't delete surveyGraph" + iex);
			}
		}

		else if ("saveSurveyGroupRefactor".equals(action)) {

			com.gallatinsystems.survey.dao.SurveyGroupDAO sgDao = new com.gallatinsystems.survey.dao.SurveyGroupDAO();
			BaseDAO<Translation> tDao = new BaseDAO<Translation>(
					Translation.class);

			for (Translation t : tDao.list("all"))
				tDao.delete(t);
			// clear out old surveys
			List<SurveyGroup> sgList = sgDao.list("all");
			for (SurveyGroup item : sgList)
				sgDao.delete(item);

			try {
				resp.getWriter().println("Finished clearing surveyGroup table");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
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
				for (int j = 0; j < 10; j++) {
					com.gallatinsystems.survey.domain.Survey survey = new com.gallatinsystems.survey.domain.Survey();
					survey.setName(j + ":" + new Date());
					survey.setSurveyGroupId(sg.getKey().getId());
					survey = surveyDao.save(survey);
					Translation t = new Translation();
					t.setLanguageCode("es");
					t.setText(j + ":" + new Date());
					t.setParentType(ParentType.SURVEY_NAME);
					t.setParentId(survey.getKey().getId());
					tDao.save(t);
					survey.addTranslation(t);
					for (int k = 0; k < 10; k++) {
						com.gallatinsystems.survey.domain.QuestionGroup qg = new com.gallatinsystems.survey.domain.QuestionGroup();
						qg.setName("en:" + j + new Date());
						qg.setDesc("en:desc: " + j + new Date());
						qg.setCode("en:" + j + new Date());
						qg.setSurveyId(survey.getKey().getId());
						qg.setOrder(k);
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
								tqhm
										.setParentType(ParentType.QUESTION_HELP_MEDIA_TEXT);
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
				log.log(Level.INFO, "Finished Saving sg: "
						+ sg.getKey().toString());
			}
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

		} else if ("testSurveyQuestion".equals(action)) {
			SurveyDAO surveyDao = new SurveyDAO();
			SurveyQuestion q = new SurveyQuestion();
			q.setId("q6");
			q.setText("Source");
			q.setType(QuestionAnswerType.option);
			surveyDao.save(q);
			q = new SurveyQuestion();
			q.setId("q7");
			q.setText("Collection Point");
			q.setType(QuestionAnswerType.option);
			surveyDao.save(q);

			SurveyQuestionSummary summ = new SurveyQuestionSummary();
			summ.setQuestionId("q6");
			summ.setCount(new Long(10));
			summ.setResponse("Spring");
			surveyDao.save(summ);
			summ = new SurveyQuestionSummary();
			summ.setQuestionId("q6");
			summ.setCount(new Long(7));
			summ.setResponse("Borehole");
			surveyDao.save(summ);
			summ = new SurveyQuestionSummary();
			summ.setQuestionId("q6");
			summ.setCount(new Long(22));
			summ.setResponse("Surface Water");
			surveyDao.save(summ);
			summ = new SurveyQuestionSummary();
			summ.setQuestionId("q7");
			summ.setCount(new Long(19));
			summ.setResponse("Public Handpump");
			surveyDao.save(summ);
			summ = new SurveyQuestionSummary();
			summ.setQuestionId("q7");
			summ.setCount(new Long(32));
			summ.setResponse("Yard Connection");
			surveyDao.save(summ);
			summ = new SurveyQuestionSummary();
			summ.setQuestionId("q7");
			summ.setCount(new Long(3));
			summ.setResponse("House Connection");
			surveyDao.save(summ);

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
		} else if ("createQuestionLookup".equals(action)) {
			SurveyDAO surveyDao = new SurveyDAO();
			SurveyQuestion q = new SurveyQuestion();
			q.setId("qm5");
			q.setText("Technology Type");
			q.setType(QuestionAnswerType.option);
			surveyDao.save(q);
			q = new SurveyQuestion();
			q.setId("qm8");
			q.setText("Is farthest household within 500m");
			q.setType(QuestionAnswerType.option);
			surveyDao.save(q);
			q = new SurveyQuestion();
			q.setId("qm9");
			q.setText("Management Structure");
			q.setType(QuestionAnswerType.option);
			surveyDao.save(q);
			q = new SurveyQuestion();
			q.setId("qm10");
			q.setText("Status");
			q.setType(QuestionAnswerType.option);
			surveyDao.save(q);
		} else if ("testUser".equals(action)) {
			UserServiceImpl userSvc = new UserServiceImpl();
			UserDto u = userSvc.getCurrentUserConfig();
			if (u != null) {
				UserConfigDto cdto = userSvc.findUserConfigItem(u
						.getEmailAddress(), "DASHBOARD", "System Summary");
				if (cdto != null) {
					System.out.println("HI: " + cdto.getValue());
					cdto.setValue("0,0");
					userSvc.updateUserConfigItem(u.getEmailAddress(),
							"DASHBOARD", cdto);
					cdto = userSvc.findUserConfigItem(u.getEmailAddress(),
							"DASHBOARD", "System Summary");
					System.out.println("HI: " + cdto.getValue());
				}
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
							ap
									.setGeocells(GeocellManager
											.generateGeoCell(new Point(ap
													.getLatitude(), ap
													.getLongitude())));
							apDao.save(ap);
						}
					}
				}
			}
		} else if ("createDevice".equals(action)) {
			Device d = new Device();
			d.setCreatedDateTime(new Date());
			d.setDeviceGroup("testgroup");
			d.setDeviceType(DeviceType.CELL_PHONE_ANDROID);
			d.setEsn("123");
			d.setPhoneNumber("1234567890");
			d.setInServiceDate(new Date());
			DeviceDAO deviceDao = new DeviceDAO();
			deviceDao.save(d);
			d = new Device();
			d.setCreatedDateTime(new Date());
			d.setDeviceGroup("anothergroup");
			d.setDeviceType(DeviceType.CELL_PHONE_ANDROID);
			d.setEsn("123");
			d.setPhoneNumber("5555555555");
			d.setInServiceDate(new Date());
			deviceDao.save(d);

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
					surveyDao.delete(survey);
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
					qDao.delete(q);
				}
				resp.getWriter().println("Deleted all Questions");

				QuestionOptionDao qoDao = new QuestionOptionDao();
				List<QuestionOption> qoList = qoDao.list("all");
				for (QuestionOption qo : qoList)
					qoDao.delete(qo);
				resp.getWriter().println("Deleted all QuestionOptions");

				resp.getWriter().println("Deleted all questions");
				/*
				 * for (int t = 0; t < 2; t++) { SurveyGroupDto sgd = new
				 * SurveyGroupDto(); sgd.setCode("Survey Group :" + t);
				 * sgd.setDescription("Test Survey Group: " + t); for (int i =
				 * 0; i < 2; i++) { SurveyDto surveyDto = new SurveyDto();
				 * surveyDto.setName("Survey a:" + i);
				 * surveyDto.setDescription("test : " + i); for (int q = 0; q <
				 * 5; q++) { QuestionGroupDto qgd = new QuestionGroupDto();
				 * qgd.setCode("Question Group: " + q);
				 * qgd.setDescription("Question Group Desc: " + q); for (int j =
				 * 0; j < 3; j++) { QuestionHelpDto qhd = new QuestionHelpDto();
				 * qhd.setResourceUrl("www.waterforpeople.org");
				 * qhd.setText("help text");
				 * 
				 * QuestionOptionDto qo = new QuestionOptionDto();
				 * qo.setCode("opt1"); qo.setText("Question Option 1 Display");
				 * 
				 * OptionContainerDto optionContainerDto = new
				 * OptionContainerDto();
				 * optionContainerDto.setAllowOtherFlag(false);
				 * 
				 * optionContainerDto.addQuestionOption(qo);
				 * 
				 * QuestionDto qd = new QuestionDto(); QuestionDto qd1 = new
				 * QuestionDto(); qd.setText("Question Test: " + j);
				 * qd.setType(QuestionType.FREE_TEXT); qd.setTip("test tip" +
				 * j); qd.setValidationRule("validation rule : " + j);
				 * qd.addQuestionHelp(qhd); qgd.addQuestion(qd, j);
				 * 
				 * qd1.setText("option question" + j);
				 * qd1.setType(QuestionType.OPTION);
				 * qd1.setOptionContainerDto(optionContainerDto);
				 * qd1.addQuestionHelp(qhd); qd1.setTip("test tip" + j);
				 * qd1.setValidationRule("validation rule : " + j);
				 * qgd.addQuestion(qd1, j + 4); }
				 * surveyDto.addQuestionGroup(qgd); }
				 * surveyDto.setVersion("Version: " + i);
				 * sgd.addSurvey(surveyDto); } SurveyGroupDto sgDto = new
				 * SurveyServiceImpl().save(sgd); }
				 */

				resp.getWriter().println(
						"Finished deleting and reloading SurveyGroup graph");
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
		} else if ("testFindSurvey".equals(action)) {
			SurveyServiceImpl ssI = new SurveyServiceImpl();
			SurveyDto dto = ssI.loadFullSurvey(2349L);
		} else if ("createTestSurveyForEndToEnd".equals(action)) {
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
		} else if ("deleteSurveyFragments".equals(action)) {
			SurveyXMLFragmentDao sxmlfDao = new SurveyXMLFragmentDao();
			List<SurveyXMLFragment> frags = sxmlfDao.list("all");
			if (frags != null) {
				for (SurveyXMLFragment frag : frags) {
					sxmlfDao.delete(frag);
				}
			}
		}

	}
}
