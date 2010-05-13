package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyActivityDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyQuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.domain.SurveyQuestion;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.survey.app.web.client.dto.SurveyGroupDto;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyServiceImpl extends RemoteServiceServlet implements
		SurveyService {
	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(DeviceManagerServlet.class.getName());

	private static final long serialVersionUID = 5557965649047558451L;
	private SurveyDAO surveyDao;

	public SurveyServiceImpl() {
		surveyDao = new SurveyDAO();
	}

	@Override
	public SurveyDto[] listSurvey() {

		List<Survey> surveys = surveyDao.list(Constants.ALL_RESULTS);
		SurveyDto[] surveyDtos = null;
		if (surveys != null) {
			surveyDtos = new SurveyDto[surveys.size()];
			for (int i = 0; i < surveys.size(); i++) {
				SurveyDto dto = new SurveyDto();
				Survey s = surveys.get(i);

				dto.setName(s.getName());
				dto.setVersion(s.getVersion() != null ? s.getVersion()
						.toString() : "");
				dto.setKeyId(s.getKey().getId());
				surveyDtos[i] = dto;
			}
		}
		return surveyDtos;
	}

	public ArrayList<SurveyGroupDto> listSurveyGroups(String cursorString) {
		ArrayList<SurveyGroupDto> surveyGroupDtoList = new ArrayList<SurveyGroupDto>();
		for (SurveyGroup canonical : surveyDao.listSurveyGroup(cursorString)) {
			SurveyGroupDto dto = new SurveyGroupDto();
			DtoMarshaller.copyToDto(canonical, dto);
		}
		return surveyGroupDtoList;
	}

	/**
	 * Aggregates counts of survey activity between the dates specified based on
	 * the rollUpType.
	 * 
	 * @param startDate
	 *            - if null, no start date constraint is used
	 * @param endDate
	 *            - if null, no end date constraint is used
	 * @param rollUpType
	 *            - can be either DATE or REGION (use constants defined in the
	 *            service interface)
	 */
	@Override
	public SurveyActivityDto[] listSurveyActivityByDate(Date startDate,
			Date endDate, String rollUpType) {
		List items = surveyDao.countSurveyInstance(null, null,
				SurveyService.DATE_ROLL_UP);
		return null;
	}

	/**
	 * This method will return a list of all the questions that have a specific
	 * type code
	 */
	public SurveyQuestionDto[] listSurveyQuestionByType(String typeCode) {

		SurveyDAO dao = new SurveyDAO();
		List<SurveyQuestion> qList = dao.listQuestionByType(typeCode);
		SurveyQuestionDto[] dtoList = null;
		if (qList != null) {
			dtoList = new SurveyQuestionDto[qList.size()];
			for (int i = 0; i < qList.size(); i++) {
				SurveyQuestionDto qDto = new SurveyQuestionDto();
				qDto.setQuestionId(qList.get(i).getId());
				qDto.setQuestionType(typeCode);
				qDto.setQuestionText(qList.get(i).getText());
				dtoList[i] = qDto;
			}
		}
		return dtoList;
	}

	
	public ArrayList<SurveyDto> getSurveyGroup(String surveyGroupCode) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
