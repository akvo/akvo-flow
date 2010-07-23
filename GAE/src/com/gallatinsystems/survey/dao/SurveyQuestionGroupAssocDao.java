package com.gallatinsystems.survey.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.SurveyQuestionGroupAssoc;

public class SurveyQuestionGroupAssocDao extends BaseDAO<SurveyQuestionGroupAssoc> {

	
	public SurveyQuestionGroupAssocDao(){
		super(SurveyQuestionGroupAssoc.class);
	}
	
	public SurveyQuestionGroupAssoc save(SurveyQuestionGroupAssoc obj){
		return super.save(obj);
	}
	
	public List<SurveyQuestionGroupAssoc> listBySurveyId(Long surveyId){
		return super.listByProperty("surveyId",surveyId, "Long");
	}
	
	public List<SurveyQuestionGroupAssoc> listByQuestionGroupId(Long questionGroupId){
		return super.listByProperty("questionGroupId", questionGroupId, "Long");
	}

	@SuppressWarnings("unchecked")
	public void delete(SurveyQuestionGroupAssoc item){
		if(item.getKey()==null){
			Long surveyId = item.getSurveyId();
			Long questionGroupId = item.getQuestionGroupId();
			PersistenceManager pm = PersistenceFilter.getManager();
			List<SurveyQuestionGroupAssoc> results = null;

			String paramName = "surveyIdParam";
			String paramName2 = "questionGroupIdParam";
			
			javax.jdo.Query query = pm.newQuery(SurveyQuestionGroupAssoc.class);
			query.setFilter( "surveyId == " + paramName + "&& questionGroupId ==" + paramName2);
			query.declareParameters("Long" + " " + paramName);
			query.declareParameters("Long" + " " + paramName2);
			
			results = (List<SurveyQuestionGroupAssoc>) query.execute(surveyId,questionGroupId);

			super.delete(results.get(0));
		}else{
			super.delete(item);
		}
	}
}
