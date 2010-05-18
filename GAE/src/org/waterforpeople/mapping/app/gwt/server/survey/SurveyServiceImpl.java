package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyQuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.domain.SurveyQuestion;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Survey.SurveyStatus;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;
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

	public ArrayList<SurveyGroupDto> listSurveyGroups(String cursorString,
			Boolean loadSurveyFlag, Boolean loadQuestionGroupFlag,
			Boolean loadQuestionFlag) {
		ArrayList<SurveyGroupDto> surveyGroupDtoList = new ArrayList<SurveyGroupDto>();
		SurveyGroupDAO surveyGroupDao = new SurveyGroupDAO();
		for (SurveyGroup canonical : surveyGroupDao.list(cursorString,
				loadSurveyFlag, loadQuestionGroupFlag, loadQuestionFlag)) {
			SurveyGroupDto dto = new SurveyGroupDto();
			DtoMarshaller.copyToDto(canonical, dto);
			dto.setSurveyList(null);
			if (canonical.getSurveyList() != null
					&& canonical.getSurveyList().size() > 0) {
				for (Survey survey : canonical.getSurveyList()) {
					SurveyDto surveyDto = new SurveyDto();
					DtoMarshaller.copyToDto(survey, surveyDto);
					surveyDto.setQuestionGroupList(null);
					if (survey.getQuestionGroupList() != null
							&& survey.getQuestionGroupList().size() > 0) {
						for (QuestionGroup questionGroup : survey
								.getQuestionGroupList()) {
							QuestionGroupDto questionGroupDto = new QuestionGroupDto();
							DtoMarshaller.copyToDto(questionGroup,
									questionGroupDto);
							if (questionGroup.getQuestionMap() != null
									&& questionGroup.getQuestionMap().size() > 0) {
								for (Entry questionEntry : questionGroup
										.getQuestionMap().entrySet()) {
									Question question = (Question) questionEntry
											.getValue();
									Integer order = (Integer) questionEntry
											.getKey();
									QuestionDto questionDto = new QuestionDto();
									DtoMarshaller.copyToDto(question,
											questionDto);
									questionGroupDto.addQuestion(questionDto,
											order);
								}
							}
							surveyDto.addQuestionGroup(questionGroupDto);
						}
					}
				}
			}
			surveyGroupDtoList.add(dto);
		}
		return surveyGroupDtoList;
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
				if (s.getStatus() != null) {
					dto.setStatus(s.getStatus().toString());
				}
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

	/**
	 * fully hydrates a single survey object
	 */
	public SurveyDto loadFullSurvey(Long surveyId) {
		Survey survey = surveyDao.loadFullSurvey(surveyId);
		SurveyDto dto = null;
		if (survey != null) {
			dto = new SurveyDto();
			DtoMarshaller.copyToDto(survey, dto);
			dto.setQuestionGroupList(null);
			if (survey.getQuestionGroupList() != null) {
				ArrayList<QuestionGroupDto> qGroupDtoList = new ArrayList<QuestionGroupDto>();
				for (QuestionGroup qg : survey.getQuestionGroupList()) {
					QuestionGroupDto qgDto = new QuestionGroupDto();
					DtoMarshaller.copyToDto(qg, qgDto);
					qgDto.setQuestionMap(null);
					qGroupDtoList.add(qgDto);
					if (qg.getQuestionMap() != null) {
						HashMap<Integer, QuestionDto> qDtoMap = new HashMap<Integer, QuestionDto>();
						for (Entry<Integer, Question> entry : qg
								.getQuestionMap().entrySet()) {
							QuestionDto qdto = new QuestionDto();
							DtoMarshaller.copyToDto(entry.getValue(), qdto);
							qdto.setOptionsList(null);
							qdto.setQuestionHelpList(null);
							// TODO: marshall options/help
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

	@Override
	public SurveyDto loadFullSurvey(String surveyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SurveyDto> listSurveysForSurveyGroup(String surveyGroupCode) {
		List<Survey> surveyList = surveyDao
				.getSurveyForSurveyGroup(surveyGroupCode);
		List<SurveyDto> surveyDtoList = new ArrayList<SurveyDto>();
		for (Survey canonical : surveyList) {
			SurveyDto dto = new SurveyDto();
			DtoMarshaller.copyToDto(canonical, dto);
			surveyDtoList.add(dto);
		}
		return surveyDtoList;
	}

	@Override
	public List<QuestionDto> listQuestionForQuestionGroup(
			String questionGroupCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<QuestionGroupDto> listQuestionGroupsBySurvey(
			String surveyId) {
		QuestionGroupDao questionGroupDao = new QuestionGroupDao();
		List<QuestionGroup> questionGroupList = questionGroupDao
				.listQuestionGroupsBySurvey(new Long(surveyId));
		ArrayList<QuestionGroupDto> questionGroupDtoList = new ArrayList<QuestionGroupDto>();
		for (QuestionGroup canonical : questionGroupList) {
			QuestionGroupDto dto = new QuestionGroupDto();
			DtoMarshaller.copyToDto(canonical, dto);
			questionGroupDtoList.add(dto);
		}
		return questionGroupDtoList;
	}

	@Override
	public ArrayList<QuestionDto> listQuestionsByQuestionGroup(
			String questionGroupId) {
		QuestionDao questionDao = new QuestionDao();
		List<Question> questionList = questionDao
				.listQuestionsByQuestionGroup(questionGroupId);
		java.util.ArrayList<QuestionDto> questionDtoList = new ArrayList<QuestionDto>();
		for (Question canonical : questionList) {
			QuestionDto dto = new QuestionDto();
			// DtoMarshaller.copyToDto(canonical, dto);
			dto.setKeyId(canonical.getKey().getId());
			dto.setText(canonical.getText());
			dto.setType(canonical.getType());
			questionDtoList.add(dto);
		}
		return questionDtoList;
	}

	@Override
	public void deleteQuestion(QuestionDto value, Long questionGroupId) {
		QuestionDao questionDao = new QuestionDao();
		Question canonical = new Question();
		DtoMarshaller.copyToCanonical(canonical, value);
		questionDao.delete(canonical, questionGroupId);

	}

	@Override
	public QuestionDto saveQuestion(QuestionDto value, Long questionGroupId) {
		QuestionDao questionDao = new QuestionDao();
		Question question = new Question();
		// question.setKey(KeyFactory.createKey(question.getClass()
		// .getSimpleName(), value.getKeyId()));
		// question.setText(value.getText());
		// question.setType(value.getType());
		DtoMarshaller.copyToCanonical(question, value);
		question = questionDao.save(question, questionGroupId);
		value.setKeyId(question.getKey().getId());
		value.setText(question.getText());
		value.setType(question.getType());
		return value;
	}

	@Override
	public QuestionGroupDto saveQuestionGroup(QuestionGroupDto dto,
			Long surveyId) {
		QuestionGroup questionGroup = new QuestionGroup();
		DtoMarshaller.copyToCanonical(questionGroup, dto);
		QuestionGroupDao questionGroupDao = new QuestionGroupDao();
		questionGroup = questionGroupDao.save(questionGroup, surveyId);
		return null;
	}

	@Override
	public SurveyDto saveSurvey(SurveyDto surveyDto, Long surveyGroupId) {
		Survey canonical = new Survey();
		canonical.setStatus(SurveyStatus.IN_PROGRESS);
		DtoMarshaller.copyToCanonical(canonical, surveyDto);
		canonical = new SurveyDAO().save(canonical, surveyGroupId);
		DtoMarshaller.copyToDto(canonical, surveyDto);
		return surveyDto;

	}

	@Override
	public SurveyGroupDto saveSurveyGroup(SurveyGroupDto dto) {
		SurveyGroup canonical = new SurveyGroup();
		SurveyGroupDAO surveyGroupDao = new SurveyGroupDAO();
		DtoMarshaller.copyToCanonical(canonical, dto);
		canonical = surveyGroupDao.save(canonical);
		DtoMarshaller.copyToDto(canonical, dto);
		return dto;
	}

	@Override
	public String publishSurvey(Long surveyId) {
		try {
			SurveyDAO surveyDao = new SurveyDAO();
			Survey survey = surveyDao.loadFullSurvey(surveyId);
			SurveyXMLAdapter sax = new SurveyXMLAdapter();
			com.gallatinsystems.survey.domain.xml.Survey surveyXML = new com.gallatinsystems.survey.domain.xml.Survey();
			for (QuestionGroup qg : survey.getQuestionGroupList()) {
				for (Entry<Integer, Question> qEntry : qg.getQuestionMap()
						.entrySet()) {
					Question q = qEntry.getValue();
					com.gallatinsystems.survey.domain.xml.Question qXML = new com.gallatinsystems.survey.domain.xml.Question();
					// ToDo marshall xml
					// qXML.setText(q.getText());
				}
			}
			String surveyDocument = sax.marshal(surveyXML);
			surveyDao.save(surveyId, surveyDocument);
			survey.setStatus(SurveyStatus.PUBLISHED);
			surveyDao.save(survey);

		} catch (Exception ex) {
			ex.printStackTrace();
			return "Could not publish survey " + ex.getMessage();
		}

		return "Survey successfully published";
	}

}
