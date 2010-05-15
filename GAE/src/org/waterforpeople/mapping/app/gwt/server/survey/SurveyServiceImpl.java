package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyActivityDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyQuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.domain.SurveyQuestion;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SurveyServiceImpl extends RemoteServiceServlet implements
		SurveyService {

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

	/**
	 * lists all surveys for a group
	 */
	@Override
	public ArrayList<SurveyDto> listSurveysByGroup(String surveyGroupId) {
		SurveyDAO dao = new SurveyDAO();
		List<Survey> surveys = dao.getSurveyForSurveyGroup(surveyGroupId);
		ArrayList<SurveyDto> surveyDtos = null;
		if (surveys != null) {
			surveyDtos = new ArrayList<SurveyDto>();
			for (Survey s : surveys) {
				SurveyDto dto = new SurveyDto();

				dto.setName(s.getName());
				dto.setVersion(s.getVersion() != null ? s.getVersion()
						.toString() : "");
				dto.setKeyId(s.getKey().getId());
				surveyDtos.add(dto);
			}
		}
		return surveyDtos;
	}

	@Override
	public SurveyGroupDto save(SurveyGroupDto value) {
		SurveyGroupDAO sgDao = new SurveyGroupDAO();
		SurveyGroup surveyGroup = new SurveyGroup();
		DtoMarshaller.copyToCanonical(surveyGroup, value);
		surveyGroup.setSurveyList(null);
		for (SurveyDto item : value.getSurveyList()) {
			// SurveyDto item = value.getSurveyList().get(0);
			Survey survey = new Survey();
			DtoMarshaller.copyToCanonical(survey, item);
			survey.setQuestionGroupList(null);
			for (QuestionGroupDto qgDto : item.getQuestionGroupList()) {
				QuestionGroup qg = new QuestionGroup();
				DtoMarshaller.copyToCanonical(qg, qgDto);
				qg.setQuestionList(null);
				for (Entry<Integer, QuestionDto> qDto : qgDto.getQuestionMap()
						.entrySet()) {
					Question q = new Question();
					DtoMarshaller.copyToCanonical(q, qDto.getValue());
					qg.addQuestion(q, qDto.getKey());
				}
				survey.addQuestionGroup(qg);
			}
			surveyGroup.addSurvey(survey);
		}

		DtoMarshaller.copyToDto(sgDao.save(surveyGroup), value);
		return value;
	}

	public void test() {
		int t = 0;
		SurveyGroupDto sgd = new SurveyGroupDto();
		sgd.setCode("Survey Group :" + t);
		sgd.setDescription("Test Survey Group: " + t);
		for (int i = 0; i < 5; i++) {
			SurveyDto surveyDto = new SurveyDto();
			surveyDto.setDescription("test : " + i);
			for (int q = 0; q < 10; q++) {
				QuestionGroupDto qgd = new QuestionGroupDto();
				qgd.setCode("Question Group: " + q);
				qgd.setDescription("Question Group Desc: " + q);
				for (int j = 0; j < 10; j++) {
					QuestionDto qd = new QuestionDto();
					qd.setText("Question Test: " + j);
					qd.setType(QuestionType.FREE_TEXT);
					qgd.addQuestion(qd, j);
				}
				surveyDto.addQuestionGroup(qgd);
			}
			surveyDto.setVersion("Version: " + i);
			sgd.addSurvey(surveyDto);
		}
		save(sgd);
		
		SurveyGroupDAO sgDAO = new SurveyGroupDAO();
		SurveyGroup sgResult = sgDAO.getByKey(sgd.getKeyId());
		log.info("SurveyGroup Result: " + sgResult);
	}

	/**
	 * fully hydrates a single survey object
	 */
	public SurveyDto loadFullSurvey(Long surveyId) {
		Survey survey = surveyDao.getById(surveyId);
		SurveyDto dto = null;
		if (survey != null) {
			dto = new SurveyDto();
			DtoMarshaller.copyToDto(survey, dto);
			if (survey.getQuestionGroupList() != null) {
				ArrayList<QuestionGroupDto> qGroupDtoList = new ArrayList<QuestionGroupDto>();
				for (QuestionGroup qg : survey.getQuestionGroupList()) {
					QuestionGroupDto qgDto = new QuestionGroupDto();
					DtoMarshaller.copyToDto(qg, qgDto);
					qGroupDtoList.add(qgDto);
					if (qg.getQuestionMap() != null) {
						HashMap<Integer, QuestionDto> qDtoMap = new HashMap<Integer, QuestionDto>();
						for (Entry<Integer, Question> entry : qg
								.getQuestionMap().entrySet()) {
							QuestionDto qdto = new QuestionDto();
							DtoMarshaller.copyToDto(entry.getValue(), qdto);
							qDtoMap.put(entry.getKey(), qdto);
						}
						qgDto.setQuestionMap(qDtoMap);
					}
				}
				dto.setQuestionGroupList(qGroupDtoList);
			}
		}
		return dto;
	}
}
