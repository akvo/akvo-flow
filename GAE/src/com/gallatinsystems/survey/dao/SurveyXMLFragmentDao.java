package com.gallatinsystems.survey.dao;

import java.util.List;

import org.waterforpeople.mapping.app.web.dto.SurveyAssemblyRequest;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.SurveyXMLFragment;


public class SurveyXMLFragmentDao extends BaseDAO<SurveyXMLFragment> {

	public SurveyXMLFragmentDao(Class<SurveyXMLFragment> e) {
		super(e);
		// TODO Auto-generated constructor stub
	}
	
	public SurveyXMLFragmentDao(){
		super(SurveyXMLFragment.class);
	}
	
	public List<SurveyXMLFragment> listSurveyFragments(Long surveyId, SurveyXMLFragment.FRAGMENT_TYPE type){
		List<SurveyXMLFragment> surveyFragmentList = super.listByProperty("surveyId", surveyId, "Long", SurveyXMLFragment.class);
		return surveyFragmentList;
	}

}
