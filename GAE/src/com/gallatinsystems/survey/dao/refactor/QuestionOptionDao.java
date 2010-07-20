package com.gallatinsystems.survey.dao.refactor;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.refactor.QuestionOption;

public class QuestionOptionDao extends BaseDAO<QuestionOption> {
	public QuestionOptionDao() {
		super(QuestionOption.class);
	}
}
