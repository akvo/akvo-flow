package org.waterforpeople.mapping.helper;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
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
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.gallatinsystems.common.util.DateUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

public class AccessPointHelper {

	private static final String VALUE_TYPE = "VALUE";
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

	public void processSurveyInstance(String surveyId) {
		// Get the survey and QuestionAnswerStore
		// Get the surveyDefinition

		SurveyInstanceDAO sid = new SurveyInstanceDAO();
		SurveyInstance si = sid.getByKey(Long.parseLong(surveyId));
		ArrayList<QuestionAnswerStore> questionAnswerList = si
				.getQuestionAnswersStore();

		// Hardcoded for dev need to identify the map key between SurveyInstance
		// and Survey
		// Survey surveyDefinition = surveyDAO.get(39L);

		/*
		 * For Monday I am mapping questionIds from QuestionAnswer to values in
		 * mappingsurvey this needs to be replaced by a mapping between the two
		 * tables for Tuesday Lat/Lon/Alt from q4 geo WaterPointPhotoURL = qm2
		 * typeOfWaterPointTech = qm5 communityCode = qm1 constructionDate = qm4
		 * numberOfHouseholdsUsingWaterPoint = qm6 costPer = qm7
		 * farthestHouseholdfromWaterPoint = qm8
		 * CurrentManagementStructureWaterPoint = qm9 waterSystemStatus = qm10
		 * sanitationPointPhotoURL =q3 waterPointCaption = qm3
		 */

		AccessPoint ap;

		ap = parseAccessPoint(new Long(surveyId), questionAnswerList,
				AccessPoint.AccessPointType.WATER_POINT);
		saveAccessPoint(ap);

	}

	private AccessPoint parseAccessPoint(Long surveyId,
			ArrayList<QuestionAnswerStore> questionAnswerList,
			AccessPoint.AccessPointType accessPointType) {
		AccessPoint ap = null;
		List<SurveyAttributeMapping> mappings = mappingDao
				.listMappingsBySurvey(surveyId);
		if (mappings == null) {
			if (accessPointType == AccessPointType.WATER_POINT) {
				ap = hardCodedparseWaterPoint(questionAnswerList);
			} else if (accessPointType == AccessPointType.SANITATION_POINT) {

			}
		} else {
			ap = parseAccessPoint(surveyId, questionAnswerList, mappings);
		}
		return ap;
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
	private AccessPoint parseAccessPoint(Long surveyId,
			ArrayList<QuestionAnswerStore> questionAnswerList,
			List<SurveyAttributeMapping> mappings) {
		AccessPoint ap = new AccessPoint();
		if (questionAnswerList != null) {
			Properties props = System.getProperties();
			String photo_url_root = props.getProperty("photo_url_root");
			for (QuestionAnswerStore qas : questionAnswerList) {
				String field = getFieldForQuestion(mappings, qas
						.getQuestionID());
				if (field != null) {
					try {
						if (GEO_TYPE.equalsIgnoreCase(qas.getType())) {
							GeoCoordinates geoC = new GeoCoordinates()
									.extractGeoCoordinate(qas.getValue());
							ap.setLatitude(geoC.getLatitude());
							ap.setLongitude(geoC.getLongitude());
							ap.setAltitude(geoC.getAltitude());
						} else {
							// if it's a value or OTHER type
							Field f = ap.getClass().getField(field);
							if (!f.isAccessible()) {
								f.setAccessible(true);
							}
							if (PHOTO_TYPE.equalsIgnoreCase(qas.getType())) {
								String[] photoParts = qas.getValue().split("/");
								String newURL = photo_url_root + photoParts[2];
								f.set(ap, newURL);
							} else {
								if (f.getType() == String.class) {
									f.set(ap, qas.getValue());
								} else if (f.getType() == AccessPoint.Status.class) {
									String val = qas.getValue();
									if ("High".equalsIgnoreCase(val)) {
										f
												.set(
														ap,
														AccessPoint.Status.FUNCTIONING_HIGH);
									} else if ("Ok".equalsIgnoreCase(val)) {
										f
												.set(
														ap,
														AccessPoint.Status.FUNCTIONING_OK);
									} else {
										f
												.set(
														ap,
														AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS);
									}
								}
							}
						}
					} catch (NoSuchFieldException e) {
						logger
								.log(
										Level.SEVERE,
										"Could not map field to access point: "
												+ field
												+ ". Check the surveyAttribueMapping for surveyId "
												+ surveyId);
					} catch (IllegalAccessException e) {
						logger.log(Level.SEVERE,
								"Could not set field to access point: " + field
										+ ". Illegal access.");
					}
				}
			}
			ap.setCollectionDate(new Date());
		}
		return ap;
	}

	private String getFieldForQuestion(List<SurveyAttributeMapping> mappings,
			String questionId) {
		if(mappings != null){
			for(SurveyAttributeMapping mapping: mappings){
				if(mapping.getSurveyQuestionId().equals(questionId)){
					return mapping.getAttributeName();
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
			ArrayList<QuestionAnswerStore> questionAnswerList) {
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
					if ("High".equalsIgnoreCase(val)) {
						ap.setPointStatus(AccessPoint.Status.FUNCTIONING_HIGH);
					} else if ("Ok".equalsIgnoreCase(val)) {
						ap.setPointStatus(AccessPoint.Status.FUNCTIONING_OK);
					} else {
						ap
								.setPointStatus(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS);
					}
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
			ap.setGeocells(GeocellManager.generateGeoCell(new Point(ap
					.getLatitude(), ap.getLongitude())));
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

}
