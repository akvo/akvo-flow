package org.waterforpeople.mapping.dao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.SurveyContainer;

public class SurveyContainerDao extends BaseDAO<SurveyContainer> {

	public SurveyContainerDao(){
		super(SurveyContainer.class);
	}
	
	public SurveyContainer findBySurveyId(Long surveyId){
		return super.findByProperty("surveyId", surveyId, "Long");
	}
	

}
