package com.gallatinsystems.survey.dao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.QuestionGroup;

public class QuestionGroupDao extends BaseDAO<QuestionGroup> {

	public QuestionGroupDao(Class e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	public QuestionGroupDao() {
		super(QuestionGroup.class);
	}

	public QuestionGroup save(QuestionGroup item) {
		return super.save(item);
	}

	public QuestionGroup getById(Long id) {
		return super.getByKey(id);
	}

	public QuestionGroup getId(String questionGroupCode) {
		return super.findByProperty("code", questionGroupCode, "String");
	}

}
