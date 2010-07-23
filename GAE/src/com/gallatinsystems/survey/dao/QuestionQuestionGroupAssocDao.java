package com.gallatinsystems.survey.dao;

import java.util.Collections;
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
		List<QuestionQuestionGroupAssoc> assocList = listByProperty("questionGroupId", questionGroupId, "Long", "order");
		if(assocList != null){
			Collections.sort(assocList);
		}
		return assocList;
	}

	public List<QuestionQuestionGroupAssoc> listByQuestionId(Long questionId) {
		return listByProperty("questionId", questionId, "Long");
	}

}
