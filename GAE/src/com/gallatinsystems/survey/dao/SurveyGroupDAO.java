package com.gallatinsystems.survey.dao;

import java.util.logging.Logger;

import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.SurveyGroup;

public class SurveyGroupDAO extends BaseDAO<SurveyGroup> {
	private static final Logger log = Logger.getLogger(SurveyGroupDAO.class
			.getName());

	public SurveyGroupDAO(Class<SurveyGroup> e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

}
