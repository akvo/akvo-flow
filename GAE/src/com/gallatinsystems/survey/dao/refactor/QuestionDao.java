package com.gallatinsystems.survey.dao.refactor;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.refactor.Question;

public class QuestionDao extends BaseDAO<Question> {
	public QuestionDao() {
		super(Question.class);
	}
}
