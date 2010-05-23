package com.gallatinsystems.survey.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.QuestionQuestionGroupAssoc;

public class QuestionQuestionGroupAssocDao extends
		BaseDAO<QuestionQuestionGroupAssoc> {

	public QuestionQuestionGroupAssocDao() {
		super(QuestionQuestionGroupAssoc.class);
	}

	public List<QuestionQuestionGroupAssoc> listByQuestionGroupId(
			Long questionGroupId) {
		return listByProperty("questionGroupId", questionGroupId, "Long");
	}

	public List<QuestionQuestionGroupAssoc> listByQuestionId(Long questionId) {
		return listByProperty("questionId", questionId, "Long");
	}

}
