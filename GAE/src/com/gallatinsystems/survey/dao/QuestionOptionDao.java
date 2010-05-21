package com.gallatinsystems.survey.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.OptionContainer;
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
//		QuestionOption qoNew = new QuestionOption();
//		qoNew.setCode(item.getCode());
//		qoNew.setText(item.getText());
//		return super.saveAndFlush(qoNew);
		return super.save(item);
	}

	
}
