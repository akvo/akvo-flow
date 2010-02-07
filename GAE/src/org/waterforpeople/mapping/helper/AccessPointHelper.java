package org.waterforpeople.mapping.helper;

import java.util.ArrayList;

import org.waterforpeople.mapping.dao.AccessPointDAO;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.GeoCoordinates;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.survey.dao.SurveyDAO;

public class AccessPointHelper {

	public void processSurveyInstance(Long surveyId) {
		// Get the survey and QuestionAnswerStore
		// Get the surveyDefinition

		SurveyInstanceDAO sid = new SurveyInstanceDAO();
		SurveyInstance si = sid.get(surveyId);
		ArrayList<QuestionAnswerStore> questionAnswerList = si
				.getQuestionAnswersStore();
		SurveyDAO surveyDAO = new SurveyDAO();
		// Hardcoded for dev need to identify the map key between SurveyInstance
		// and Survey
		// Survey surveyDefinition = surveyDAO.get(39L);

		/*
		 * For Monday I am mapping questionIds from QuestionAnswer to values in
		 * mappingsurvey this needs to be replaced by a mapping between the two
		 * tables for Tuesday Lat/Lon/Alt from q4 geo 
		 * WaterPointPhotoURL = qm2
		 * typeOfWaterPointTech = qm5 
		 * communityCode = qm1 
		 * constructionDate = qm4
		 * numberOfHouseholdsUsingWaterPoint = qm6 
		 * costPer = qm7
		 * farthestHouseholdfromWaterPoint = qm8
		 * CurrentManagementStructureWaterPoint = qm9 waterSystemStatus = qm10
		 * sanitationPointPhotoURL =q3 waterPointCaption = qm3
		 */

		AccessPoint ap = new AccessPoint();

		for (QuestionAnswerStore qas : questionAnswerList) {
			
			
			if (qas.getQuestionID().equals("qm3")) {
				GeoCoordinates geoC = new GeoCoordinates()
						.extractGeoCoordinate(qas.getValue());
				ap.setLatitude(geoC.getLatitude());
				ap.setLongitude(geoC.getLongitude());
				ap.setAltitude(geoC.getAltitude());
			}else if(qas.getQuestionID().equals("qm2")){
				ap.setWaterPointPhotoURL(qas.getValue());
			}else if(qas.getQuestionID().equals("qm1")){
				ap.setCommunityCode(qas.getValue());
			}else if(qas.getQuestionID().equals("qm5")){
				ap.setTypeOfWaterPointTechnology(qas.getValue());
			}else if(qas.getQuestionID().equals("q4")){
				ap.setConstructionDateOfWaterPoint(qas.getValue());
			}else if(qas.getQuestionID().equals("qm6")){
				ap.setNumberOfHouseholdsUsingWaterPoint(qas.getValue());
			}else if(qas.getQuestionID().equals("qm7")){
				ap.setCostPer(qas.getValue());
			}else if(qas.getQuestionID().equals("qm8")){
				ap.setFarthestHouseholdfromWaterPoint(qas.getValue());
			}else if(qas.getQuestionID().equals("qm10")){
				ap.setWaterSystemStatus(qas.getValue());
			}
			
		}
		AccessPointDAO apDAO = new AccessPointDAO();
		apDAO.save(ap);
		
		
	}

}
