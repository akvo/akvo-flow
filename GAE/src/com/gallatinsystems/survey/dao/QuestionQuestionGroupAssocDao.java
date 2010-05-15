package com.gallatinsystems.survey.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.QuestionQuestionGroupAssoc;

public class QuestionQuestionGroupAssocDao extends BaseDAO<QuestionQuestionGroupAssoc> {

	public QuestionQuestionGroupAssocDao(Class e) {
		super(e);
		// TODO Auto-generated constructor stub
	}
	
	public QuestionQuestionGroupAssocDao(){
		super(QuestionQuestionGroupAssoc.class);
	}
	
	public QuestionQuestionGroupAssoc save(QuestionQuestionGroupAssoc obj){
		return super.save(obj);
	}
	
	public List<QuestionQuestionGroupAssoc> listByQuestionGroupId(Long questionGroupId){
		return super.listByProperty("questionGroupId", questionGroupId, "Long");
	}
	
	public List<QuestionQuestionGroupAssoc> listByQuestionId(Long questionId){
		return super.listByProperty("questionId", questionId, "Long");
	}
	
}
