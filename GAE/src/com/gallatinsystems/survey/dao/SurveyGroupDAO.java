package com.gallatinsystems.survey.dao;

import java.util.List;
import java.util.logging.Logger;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveySurveyGroupAssoc;

public class SurveyGroupDAO extends BaseDAO<SurveyGroup> {
	private static final Logger log = Logger.getLogger(SurveyGroupDAO.class
			.getName());

	public SurveyGroupDAO(){
		super(SurveyGroup.class);
	}
	public SurveyGroupDAO(Class<SurveyGroup> e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	public SurveyGroup save(SurveyGroup item) {

		item = super.save(item);
		Long surveyGroupId = item.getKey().getId();
		SurveyDAO surveyDao = new SurveyDAO();
		BaseDAO<SurveySurveyGroupAssoc> ssgaDAO = new BaseDAO<SurveySurveyGroupAssoc>(
				SurveySurveyGroupAssoc.class);
		for (Survey surveyItem : item.getSurveyList()) {
			surveyItem = surveyDao.save(surveyItem);
			Long surveyId = surveyItem.getKey().getId();
			SurveySurveyGroupAssoc ssga = new SurveySurveyGroupAssoc();
			ssga.setSurveyGroupId(surveyGroupId);
			ssga.setSurveyId(surveyId);
			ssgaDAO.save(ssga);
		}

		return item;
	}

	public SurveyGroup getByKey(Long id) {
		SurveyGroup sg = super.getByKey(id);
		SurveySurveyGroupAssocDao ssgaDao = new SurveySurveyGroupAssocDao();
		List<SurveySurveyGroupAssoc> list = ssgaDao.listBySurveyGroupId(id);
		SurveyDAO surveyDao = new SurveyDAO();

		for (SurveySurveyGroupAssoc item : list) {
			sg.addSurvey(surveyDao.getById(item.getSurveyId()));
		}

		return sg;
	}

}
