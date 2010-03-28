package org.waterforpeople.mapping.portal.server.service;

import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;

import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyServiceImpl extends RemoteServiceServlet implements
		SurveyService {
	private static final Logger log = Logger
			.getLogger(DeviceManagerServlet.class.getName());

	private static final long serialVersionUID = 5557965649047558451L;

	@Override
	public SurveyDto[] listSurvey() {
		SurveyDAO surveyDao = new SurveyDAO();
		List<Survey> surveys = surveyDao.list();
		SurveyDto[] surveyDtos = null;
		if (surveys != null) {
			surveyDtos = new SurveyDto[surveys.size()];
			for (int i = 0; i < surveys.size(); i++) {
				SurveyDto dto = new SurveyDto();
				Survey s = surveys.get(i);

				dto.setName(s.getName());
				dto.setVersion(s.getVersion() != null ? s.getVersion()
						.toString() : "");
				surveyDtos[i] = dto;
			}
		}
		return surveyDtos;
	}

}
