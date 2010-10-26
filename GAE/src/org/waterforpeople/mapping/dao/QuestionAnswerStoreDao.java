package org.waterforpeople.mapping.dao;

import java.util.List;

import org.waterforpeople.mapping.domain.QuestionAnswerStore;

import com.gallatinsystems.framework.dao.BaseDAO;

public class QuestionAnswerStoreDao extends BaseDAO<QuestionAnswerStore> {

	public QuestionAnswerStoreDao(){
		super(QuestionAnswerStore.class);
	}
	
	
	public List<QuestionAnswerStore> listBySurvey(Long surveyId){
		return super.listByProperty("surveyId", surveyId, "Long");
		
	}
	public List<QuestionAnswerStore> listByQuestion(Long questionId){
		return super.listByProperty("questionId", questionId.toString(), "String");
	}
}
