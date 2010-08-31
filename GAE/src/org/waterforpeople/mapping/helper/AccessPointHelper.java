package org.waterforpeople.mapping.helper;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.SurveyAttributeMappingDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.GeoCoordinates;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyAttributeMapping;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.gallatinsystems.common.util.DateUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

public class AccessPointHelper {

	private static final String GEO_TYPE = "GEO";
	private static final String PHOTO_TYPE = "IMAGE";
	private SurveyAttributeMappingDao mappingDao;

	private static Logger logger = Logger.getLogger(AccessPointHelper.class
			.getName());

	public AccessPointHelper() {
		mappingDao = new SurveyAttributeMappingDao();
	}

	public AccessPoint getAccessPoint(Long id) {
		BaseDAO<AccessPoint> apDAO = new BaseDAO<AccessPoint>(AccessPoint.class);
		return apDAO.getByKey(id);
	}

	public void processSurveyInstance(String surveyInstanceId) {
		// Get the survey and QuestionAnswerStore
		// Get the surveyDefinition

		SurveyInstanceDAO sid = new SurveyInstanceDAO();

		List<QuestionAnswerStore> questionAnswerList = sid
				.listQuestionAnswerStore(Long.parseLong(surveyInstanceId), null);

		Collection<AccessPoint> apList;
		if (questionAnswerList != null && questionAnswerList.size() > 0) {
			apList = parseAccessPoint(new Long(questionAnswerList.get(0)
					.getSurveyId()), questionAnswerList,
					AccessPoint.AccessPointType.WATER_POINT);
			if (apList != null) {
				for (AccessPoint ap : apList) {
					saveAccessPoint(ap);
				}
			}
		}
	}

	private Collection<AccessPoint> parseAccessPoint(Long surveyId,
			List<QuestionAnswerStore> questionAnswerList,
			AccessPoint.AccessPointType accessPointType) {
		Collection<AccessPoint> apList = null;
		List<SurveyAttributeMapping> mappings = mappingDao
				.listMappingsBySurvey(surveyId);
		if (mappings == null) {
			if (accessPointType == AccessPointType.WATER_POINT) {
				AccessPoint ap = hardCodedparseWaterPoint(questionAnswerList);
				apList = new ArrayList<AccessPoint>();
				apList.add(ap);
			} else if (accessPointType == AccessPointType.SANITATION_POINT) {

			}
		} else {
			apList = parseAccessPoint(surveyId, questionAnswerList, mappings);
		}
		return apList;
	}

	/**
	 * uses the saved mappings for the survey definition to parse values in the
	 * questionAnswerStore into attributes of an AccessPoint object
	 * 
	 * TODO: figure out way around known limitation of only having 1 GEO
	 * response per survey
	 * 
	 * @param questionAnswerList
	 * @param mappings
	 * @return
	 */
	private Collection<AccessPoint> parseAccessPoint(Long surveyId,
			List<QuestionAnswerStore> questionAnswerList,
			List<SurveyAttributeMapping> mappings) {
		HashMap<String, AccessPoint> apMap = new HashMap<String, AccessPoint>();
		if (questionAnswerList != null) {
			Properties props = System.getProperties();
			String photo_url_root = props.getProperty("photo_url_root");
			for (QuestionAnswerStore qas : questionAnswerList) {
				SurveyAttributeMapping mapping = getMappingForQuestion(
						mappings, qas.getQuestionID());
				if (mapping != null) {
					List<String> types = mapping.getApTypes();
					if (types == null || types.size() == 0) {
						// default the list to be access point if nothing is
						// specified (for backward compatibility)
						types.add(AccessPointType.WATER_POINT.toString());
					}
					for (String type : types) {
						try {
							AccessPoint ap = apMap.get(type);
							if (ap == null) {
								ap = new AccessPoint();
								ap.setPointType(AccessPointType.valueOf(type));
								ap.setCollectionDate(new Date());
								apMap.put(type, ap);
							}
							if (GEO_TYPE.equalsIgnoreCase(qas.getType())) {
								GeoCoordinates geoC = new GeoCoordinates()
										.extractGeoCoordinate(qas.getValue());
								ap.setLatitude(geoC.getLatitude());
								ap.setLongitude(geoC.getLongitude());
								ap.setAltitude(geoC.getAltitude());
							} else {
								// if it's a value or OTHER type
								Field f = ap.getClass().getDeclaredField(
										mapping.getAttributeName());
								if (!f.isAccessible()) {
									f.setAccessible(true);
								}
								if (PHOTO_TYPE.equalsIgnoreCase(qas.getType())) {
									String[] photoParts = qas.getValue().split(
											"/");
									String newURL = photo_url_root
											+ photoParts[2];
									f.set(ap, newURL);
								} else {
									if (f.getType() == String.class) {
										f.set(ap, qas.getValue());
									} else if (f.getType() == AccessPoint.Status.class) {
										String val = qas.getValue();
										f.set(ap, encodeStatus(val, ap
												.getPointType()));
									}
								}
							}
						} catch (NoSuchFieldException e) {
							logger
									.log(
											Level.SEVERE,
											"Could not map field to access point: "
													+ mapping
															.getAttributeName()
													+ ". Check the surveyAttribueMapping for surveyId "
													+ surveyId);
						} catch (IllegalAccessException e) {
							logger.log(Level.SEVERE,
									"Could not set field to access point: "
											+ mapping.getAttributeName()
											+ ". Illegal access.");
						}
					}
				}
			}

		}
		return apMap.values();
	}

	private SurveyAttributeMapping getMappingForQuestion(
			List<SurveyAttributeMapping> mappings, String questionId) {
		if (mappings != null) {
			for (SurveyAttributeMapping mapping : mappings) {
				if (mapping.getSurveyQuestionId().equals(questionId)) {
					return mapping;
				}
			}
		}
		return null;
	}

	/**
	 * parses values from the questionAnswerStore into an AccessPoint based on
	 * hard-coded survey question ids. this should really only be used for
	 * testing using the default, pre-installed mapping survey resident on the
	 * device.
	 * 
	 * @param questionAnswerList
	 * @return
	 */
	private AccessPoint hardCodedparseWaterPoint(
			List<QuestionAnswerStore> questionAnswerList) {
		AccessPoint ap = new AccessPoint();
		Properties props = System.getProperties();

		String photo_url_root = props.getProperty("photo_url_root");
		for (QuestionAnswerStore qas : questionAnswerList) {

			if (qas.getQuestionID().equals("qm1")) {
				ap.setCommunityCode(qas.getValue());
			} else if (qas.getQuestionID().equals("qm1a")) {
				GeoCoordinates geoC = new GeoCoordinates()
						.extractGeoCoordinate(qas.getValue());
				ap.setLatitude(geoC.getLatitude());
				ap.setLongitude(geoC.getLongitude());
				ap.setAltitude(geoC.getAltitude());
			} else if (qas.getQuestionID().equals("qm2")) {
				// Change photourl to s3 url
				String[] photoParts = qas.getValue().split("/");
				String newURL = photo_url_root + photoParts[2];
				ap.setPhotoURL(newURL);
			} else if (qas.getQuestionID().equals("qm3")) {
				// photo caption
				ap.setPointPhotoCaption(qas.getValue());
			} else if (qas.getQuestionID().equals("qm4")) {
				if (qas.getValue() != null) {
					try {
						ap.setConstructionDate(DateUtil.getYearOnlyDate(qas
								.getValue()));
					} catch (NumberFormatException e) {
						logger.log(Level.SEVERE,
								"non-integer year in construction date field",
								e);
					}
					ap.setConstructionDateYear(qas.getValue());
				}
			} else if (qas.getQuestionID().equals("qm5")) {
				// TODO: implement Technology type lookup
				// ap.setTypeTechnology(qas.getValue());
			} else if (qas.getQuestionID().equals("qm6")) {
				if (qas.getValue() != null) {
					try {
						ap.setNumberOfHouseholdsUsingPoint(new Long(qas
								.getValue().trim()));
					} catch (NumberFormatException e) {
						ap.setNumberOfHouseholdsUsingPoint(0l);
					}
				}
			} else if (qas.getQuestionID().equals("qm7")) {
				ap.setCostPer(new Double(qas.getValue()));
			} else if (qas.getQuestionID().equals("qm8")) {
				ap.setFarthestHouseholdfromPoint(qas.getValue());
			} else if (qas.getQuestionID().equals("qm9")) {
				// Current mgmt structure
				ap.setCurrentManagementStructurePoint(qas.getValue());
			} else if (qas.getQuestionID().equals("qm10")) {
				String val = qas.getValue();
				if (val != null) {
					ap.setPointStatus(encodeStatus(val, ap.getPointType()));
				}
			}
			ap.setPointType(AccessPoint.AccessPointType.WATER_POINT);
			// for now hardcode the data to now
			ap.setCollectionDate(new Date());
		}

		return ap;
	}

	/**
	 * saves an access point and fires off a summarization message
	 * 
	 * @param ap
	 * @return
	 */
	public AccessPoint saveAccessPoint(AccessPoint ap) {
		AccessPointDao apDao = new AccessPointDao();
		if (ap.getGeocells() == null || ap.getGeocells().size() == 0) {
			if (ap.getLatitude() != null && ap.getLongitude() != null) {
				ap.setGeocells(GeocellManager.generateGeoCell(new Point(ap
						.getLatitude(), ap.getLongitude())));
			}
		}
		ap = apDao.save(ap);
		Queue summQueue = QueueFactory.getQueue("dataSummarization");
		summQueue.add(url("/app_worker/datasummarization").param("objectKey",
				ap.getKey().getId() + "").param("type", "AccessPoint"));
		return ap;
	}

	public List<AccessPoint> listAccessPoint(String cursorString) {
		AccessPointDao apDao = new AccessPointDao();

		return apDao.list(cursorString);
	}

	private AccessPoint.Status encodeStatus(String statusVal,
			AccessPoint.AccessPointType pointType) {
		AccessPoint.Status status = null;
		statusVal = statusVal.toLowerCase().trim();
		if (pointType.equals(AccessPointType.WATER_POINT)) {

			if ("functioning but with problems".equals(statusVal)) {
				status = AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS;
			} else if ("broken down system".equals(statusVal)) {
				status = AccessPoint.Status.BROKEN_DOWN;
			} else if ("no improved system".equals(statusVal))
				status = AccessPoint.Status.NO_IMPROVED_SYSTEM;
			else if ("functioning and meets government standards"
					.equals(statusVal))
				status = AccessPoint.Status.FUNCTIONING_HIGH;
			else if ("high".equalsIgnoreCase(statusVal)) {
				status = AccessPoint.Status.FUNCTIONING_HIGH;
			} else if ("ok".equalsIgnoreCase(statusVal)) {
				status = AccessPoint.Status.FUNCTIONING_OK;
			} else {
				status = AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS;
			}
		} else if (pointType.equals(AccessPointType.SANITATION_POINT)) {
			if ("latrine full".equals(statusVal))
				status = AccessPoint.Status.LATRINE_FULL;
			else if ("Latrine used but technical problems evident"
					.toLowerCase().trim().equals(statusVal))
				status = AccessPoint.Status.LATRINE_USED_TECH_PROBLEMS;
			else if ("Latrine not being used due to structural/technical problems"
					.toLowerCase().equals(statusVal))
				status = AccessPoint.Status.LATRINE_NOT_USED_TECH_STRUCT_PROBLEMS;
			else if ("Do not Know".toLowerCase().equals(statusVal))
				status = AccessPoint.Status.LATRINE_DO_NOT_KNOW;
			else if ("Functional".toLowerCase().equals(statusVal))
				status = AccessPoint.Status.LATRINE_FUNCTIONAL;
		} else {
			if ("functioning but with problems".equals(statusVal)) {
				status = AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS;
			} else if ("broken down system".equals(statusVal)) {
				status = AccessPoint.Status.BROKEN_DOWN;
			} else if ("no improved system".equals(statusVal))
				status = AccessPoint.Status.NO_IMPROVED_SYSTEM;
			else if ("functioning and meets government standards"
					.equals(statusVal))
				status = AccessPoint.Status.FUNCTIONING_HIGH;
			else if ("high".equalsIgnoreCase(statusVal)) {
				status = AccessPoint.Status.FUNCTIONING_HIGH;
			} else if ("ok".equalsIgnoreCase(statusVal)) {
				status = AccessPoint.Status.FUNCTIONING_OK;
			} else {
				status = AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS;
			}
		}
		return status;
	}
}
