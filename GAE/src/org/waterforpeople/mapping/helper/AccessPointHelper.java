package org.waterforpeople.mapping.helper;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.GeoCoordinates;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.common.util.DateUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;

public class AccessPointHelper {

	private static Logger logger = Logger.getLogger(AccessPointHelper.class
			.getName());

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
		BaseDAO<AccessPoint> apDAO = new BaseDAO<AccessPoint>(AccessPoint.class);
		ap = parseAccessPoint(questionAnswerList,
				AccessPoint.AccessPointType.WATER_POINT);
		apDAO.save(ap);

	}

	private AccessPoint parseAccessPoint(
			ArrayList<QuestionAnswerStore> questionAnswerList,
			AccessPoint.AccessPointType accessPointType) {
		AccessPoint ap = null;
		if (accessPointType == AccessPointType.WATER_POINT) {
			ap = parseWaterPoint(questionAnswerList);
		} else if (accessPointType == AccessPointType.SANITATION_POINT) {

		}
		return ap;
	}

	private AccessPoint parseWaterPoint(
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
				if (val !=null){
					if("High".equalsIgnoreCase(val)){
						ap.setPointStatus(AccessPoint.Status.FUNCTIONING_HIGH);
					}else if ("Ok".equalsIgnoreCase(val)){
						ap.setPointStatus(AccessPoint.Status.FUNCTIONING_OK);
					}else{
						ap.setPointStatus(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS);
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
		BaseDAO<AccessPoint> apDao = new BaseDAO<AccessPoint>(AccessPoint.class);
		ap = apDao.save(ap);
		Queue summQueue = QueueFactory.getQueue("dataSummarization");
		summQueue.add(url("/app_worker/datasummarization").param("objectKey",
				ap.getKey().getId() + "").param("type", "AccessPoint"));
		return ap;
	}

	public List<AccessPoint> listAccessPoint() {
		BaseDAO<AccessPoint> apDao = new BaseDAO<AccessPoint>(AccessPoint.class);
		return apDao.list();
	}

}
