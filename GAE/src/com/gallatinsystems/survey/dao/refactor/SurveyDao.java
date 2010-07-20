package com.gallatinsystems.survey.dao.refactor;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.refactor.Survey;


public class SurveyDao extends BaseDAO<Survey> {

	public SurveyDao(){
		super(Survey.class);
	}
}
