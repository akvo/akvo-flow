package com.gallatinsystems.survey.dao;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.SurveyQuestionGroupAssoc;

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
	
	public QuestionGroup save(QuestionGroup item, Long surveyId){
		item = save(item);
		SurveyQuestionGroupAssoc sqga = new SurveyQuestionGroupAssoc();
		sqga.setSurveyId(surveyId);
		sqga.setQuestionGroupId(item.getKey().getId());
		SurveyQuestionGroupAssocDao sqgaDao = new SurveyQuestionGroupAssocDao();
		sqgaDao.save(sqga);
		return item;
	}
	
	public void delete(QuestionGroup item, Long surveyId){
		//ToDo Handle delete and cascades
		SurveyQuestionGroupAssocDao sqgaDao = new SurveyQuestionGroupAssocDao();
		SurveyQuestionGroupAssoc sgqa = new SurveyQuestionGroupAssoc();
		sgqa.setSurveyId(surveyId);
		sgqa.setQuestionGroupId(item.getKey().getId());
		sqgaDao.delete(sgqa);
		super.delete(item);
	}

	public QuestionGroup getById(Long id) {
		return super.getByKey(id);
	}

	public QuestionGroup getId(String questionGroupCode) {
		return super.findByProperty("code", questionGroupCode, "String");
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

}
