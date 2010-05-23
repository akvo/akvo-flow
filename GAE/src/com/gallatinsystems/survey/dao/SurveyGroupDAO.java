package com.gallatinsystems.survey.dao;

import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionQuestionGroupAssoc;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyQuestionGroupAssoc;
import com.gallatinsystems.survey.domain.SurveySurveyGroupAssoc;

public class SurveyGroupDAO extends BaseDAO<SurveyGroup> {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SurveyGroupDAO.class
			.getName());

	public SurveyGroupDAO() {
		super(SurveyGroup.class);
	}	

	public SurveyGroup save(SurveyGroup item) {

		/*
		 * Steps to save SurveyGroup and all dependent objects 1. Save
		 * SurveyGroup 2. Save Survey 3. Save SurveySurveyGroupAssoc 4. Save
		 * QuestionGroup 5. Save SurveyQuestionGroupAssoc 5. Save Question 6.
		 * Save QuestionQuestionGroupAssoc
		 */

		item = super.save(item);
		SurveyDAO surveyDao = new SurveyDAO();
		SurveySurveyGroupAssocDao ssgaDAO = new SurveySurveyGroupAssocDao();
		QuestionGroupDao qgDao = new QuestionGroupDao();
		SurveyQuestionGroupAssocDao sqgaDao = new SurveyQuestionGroupAssocDao();		
		QuestionDao questionDAO = new QuestionDao();

		Long surveyGroupId = item.getKey().getId();
		if (item.getSurveyList() != null && item.getSurveyList().size() > 0) {
			for (Survey surveyItem : item.getSurveyList()) {
				surveyItem = surveyDao.save(surveyItem);
				Long surveyId = surveyItem.getKey().getId();
				SurveySurveyGroupAssoc ssga = new SurveySurveyGroupAssoc();
				ssga.setSurveyGroupId(surveyGroupId);
				ssga.setSurveyId(surveyId);
				ssgaDAO.save(ssga);
				// Save Question Group
				if (surveyItem.getQuestionGroupList() != null) {
					for (QuestionGroup qg : surveyItem.getQuestionGroupList()) {
						qg = qgDao.save(qg);
						SurveyQuestionGroupAssoc sqga = new SurveyQuestionGroupAssoc();
						sqga.setQuestionGroupId(qg.getKey().getId());
						sqga.setSurveyId(surveyItem.getKey().getId());
						sqgaDao.save(sqga);
						if (qg.getQuestionMap() != null) {
							for (Entry<Integer, Question> questionEntry : qg
									.getQuestionMap().entrySet()) {
								Question question = questionEntry.getValue();
								//Integer order = questionEntry.getKey();
								question = questionDAO.save(question, qg
										.getKey().getId());
								// QuestionQuestionGroupAssoc qqga = new
								// QuestionQuestionGroupAssoc();
								// qqga.setQuestionGroupId(qg.getKey().getId());
								//qqga.setQuestionId(question.getKey().getId());
								// qqga.setOrder(order);
								// qqgaDao.save(qqga);
							}
						}
					}
				}
			}
		}
		return item;
	}

	public SurveyGroup getByKey(Long id, boolean includeQuestions) {
		SurveyGroup sg = super.getByKey(id);
		SurveySurveyGroupAssocDao ssgaDao = new SurveySurveyGroupAssocDao();
		List<SurveySurveyGroupAssoc> list = ssgaDao.listBySurveyGroupId(id);
		SurveyDAO surveyDao = new SurveyDAO();

		QuestionGroupDao qgDao = new QuestionGroupDao();
		SurveyQuestionGroupAssocDao sqgaDao = new SurveyQuestionGroupAssocDao();
		QuestionQuestionGroupAssocDao qqgaDao = new QuestionQuestionGroupAssocDao();
		BaseDAO<Question> questionDAO = new BaseDAO<Question>(Question.class);
		// get survey group
		// get surveys for survey group
		// get question groups from survey
		// get questions from question groups

		if (includeQuestions) {

			for (SurveySurveyGroupAssoc item : list) {				
				List<SurveyQuestionGroupAssoc> surveyGroupQuestionAssocList = sqgaDao
						.listBySurveyId(item.getSurveyId());
				Survey survey = surveyDao.getById(item.getSurveyId());
				for (SurveyQuestionGroupAssoc itemSQGA : surveyGroupQuestionAssocList) {
					QuestionGroup qg = qgDao.getById(itemSQGA
							.getQuestionGroupId());
					List<QuestionQuestionGroupAssoc> qqgaList = qqgaDao
							.listByQuestionGroupId(qg.getKey().getId());
					for (QuestionQuestionGroupAssoc qqgaItem : qqgaList) {
						Question question = questionDAO.getByKey(qqgaItem
								.getQuestionId());
						qg.addQuestion(question, qqgaItem.getOrder());
					}
					survey.addQuestionGroup(qg);
				}
				sg.addSurvey(survey);
			}
		}
		return sg;
	}

	public List<SurveyGroup> list(String cursorString, Boolean loadSurveyFlag,
			Boolean loadQuestionGroupFlag, Boolean loadQuestionFlag) {
		List<SurveyGroup> sgList = null;
		sgList = super.list(cursorString);
		if (sgList != null) {
			SurveyDAO surveyDao = new SurveyDAO();

			QuestionGroupDao qgDao = new QuestionGroupDao();
			SurveyQuestionGroupAssocDao sqgaDao = new SurveyQuestionGroupAssocDao();
			QuestionQuestionGroupAssocDao qqgaDao = new QuestionQuestionGroupAssocDao();
			BaseDAO<Question> questionDAO = new BaseDAO<Question>(
					Question.class);
			for (SurveyGroup sg : sgList) {
				SurveySurveyGroupAssocDao ssgaDao = new SurveySurveyGroupAssocDao();
				List<SurveySurveyGroupAssoc> list = ssgaDao
						.listBySurveyGroupId(sg.getKey().getId());
				if (list.size() > 0 && loadSurveyFlag) {
					for (SurveySurveyGroupAssoc item : list) {
						Survey survey = surveyDao.getById(item.getSurveyId());
						List<SurveyQuestionGroupAssoc> surveyGroupQuestionAssocList = sqgaDao
								.listBySurveyId(item.getSurveyId());
						if (surveyGroupQuestionAssocList.size() > 0
								&& loadQuestionGroupFlag) {
							for (SurveyQuestionGroupAssoc itemSQGA : surveyGroupQuestionAssocList) {
								QuestionGroup qg = qgDao.getById(itemSQGA
										.getQuestionGroupId());
								List<QuestionQuestionGroupAssoc> qqgaList = qqgaDao
										.listByQuestionGroupId(qg.getKey()
												.getId());
								if (qqgaList.size() > 0 && loadQuestionFlag)
									for (QuestionQuestionGroupAssoc qqgaItem : qqgaList) {
										Question question = questionDAO
												.getByKey(qqgaItem
														.getQuestionId());
										qg.addQuestion(question, qqgaItem
												.getOrder());
									}
								survey.addQuestionGroup(qg);
							}
						}
						sg.addSurvey(survey);
					}
				}
			}
		}
		return sgList;
	}

}
