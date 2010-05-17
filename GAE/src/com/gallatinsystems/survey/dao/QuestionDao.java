package com.gallatinsystems.survey.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionQuestionGroupAssoc;



public class QuestionDao extends BaseDAO<Question> {

	public QuestionDao(Class<Question> e) {
		super(e);
		// TODO Auto-generated constructor stub
	}
	
	public QuestionDao(){
		super(Question.class);
	}
	
	public List<Question> listQuestionsByQuestionGroup(String questionGroupCode){
		PersistenceManager pm = PersistenceFilter.getManager();
		QuestionGroup qg = new QuestionGroupDao().getId(questionGroupCode);
		List<QuestionQuestionGroupAssoc> qqgaList = new QuestionQuestionGroupAssocDao().listByQuestionGroupId(qg.getKey().getId());
		List<Long> questionIdList = new ArrayList<Long>();
		for(QuestionQuestionGroupAssoc item: qqgaList){
			questionIdList.add(item.getQuestionId());
		}
		javax.jdo.Query query = pm.newQuery(Question.class);
		final String propertyName = "";
		final String paramName = "";
		final String propertyType= "";
		
		
		query.setFilter(propertyName + " == " + paramName);
		query.declareParameters(propertyType + " " + paramName);
		return (List<Question>) query.execute(questionIdList);
		
		
		
	}

}
