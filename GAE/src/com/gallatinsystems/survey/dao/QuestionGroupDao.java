package com.gallatinsystems.survey.dao;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.SurveyQuestionGroupAssoc;

public class QuestionGroupDao extends BaseDAO<QuestionGroup> {

	public QuestionGroupDao() {
		super(QuestionGroup.class);
	}

	public QuestionGroup save(QuestionGroup item, Long surveyId, Integer order) {
		item = save(item);
		SurveyQuestionGroupAssoc sqga = new SurveyQuestionGroupAssoc();
		sqga.setSurveyId(surveyId);
		sqga.setQuestionGroupId(item.getKey().getId());
		SurveyQuestionGroupAssocDao sqgaDao = new SurveyQuestionGroupAssocDao();
		if (order != null)
			sqga.setOrder(order);
		sqgaDao.save(sqga);
		return item;
	}

	public void delete(QuestionGroup item, Long surveyId) {
		// ToDo Handle delete and cascades
		SurveyQuestionGroupAssocDao sqgaDao = new SurveyQuestionGroupAssocDao();
		SurveyQuestionGroupAssoc sgqa = new SurveyQuestionGroupAssoc();
		sgqa.setSurveyId(surveyId);
		sgqa.setQuestionGroupId(item.getKey().getId());
		sqgaDao.delete(sgqa);
		delete(item);
	}

	public QuestionGroup getId(String questionGroupCode) {
		return findByProperty("code", questionGroupCode, "String");
	}

	public List<QuestionGroup> listQuestionGroupsBySurvey(Long surveyId) {
		SurveyQuestionGroupAssocDao sqgDao = new SurveyQuestionGroupAssocDao();
		List<SurveyQuestionGroupAssoc> sgqList = sqgDao
				.listBySurveyId(surveyId);
		List<QuestionGroup> qgList = new ArrayList<QuestionGroup>();
		for (SurveyQuestionGroupAssoc sqga : sgqList) {
			QuestionGroup qg = super.getByKey(sqga.getQuestionGroupId());
			qgList.add(qg);
		}
		return qgList;
	}

	@SuppressWarnings("unchecked")
	public QuestionGroup getByPath(String code, String path) {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(QuestionGroup.class);
		query.setFilter(" path == pathParam && code == codeParam");
		query.declareParameters("String pathParam, String codeParam");
		List<QuestionGroup> results = (List<QuestionGroup>) query.execute(path, code);
		if (results != null && results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}
}
