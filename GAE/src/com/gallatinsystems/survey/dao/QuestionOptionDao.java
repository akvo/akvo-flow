package com.gallatinsystems.survey.dao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.QuestionOption;

public class QuestionOptionDao extends BaseDAO<QuestionOption> {

	public QuestionOptionDao(Class e) {
		super(e);
		// TODO Auto-generated constructor stub
	}
	public QuestionOptionDao(){
		super(QuestionOption.class);
	}
	
	public QuestionOption save(QuestionOption item){
		return super.save(item);
	}

}
