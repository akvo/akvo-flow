package org.waterforpeople.mapping.app.gwt.server.survey;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDependencyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionHelpDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyQuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.SurveyAssemblyRequest;
import org.waterforpeople.mapping.dao.SurveyContainerDao;
import org.waterforpeople.mapping.domain.SurveyQuestion;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.OptionContainer;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionDependency;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionHelp;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Survey.SurveyStatus;
import com.gallatinsystems.survey.domain.xml.Dependency;
import com.gallatinsystems.survey.domain.xml.Heading;
import com.gallatinsystems.survey.domain.xml.ObjectFactory;
import com.gallatinsystems.survey.domain.xml.Option;
import com.gallatinsystems.survey.domain.xml.Options;
import com.gallatinsystems.survey.domain.xml.Text;
import com.gallatinsystems.survey.domain.xml.Tip;
import com.gallatinsystems.survey.domain.xml.ValidationRule;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
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
					dto.addSurvey(surveyDto);
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
	public QuestionDto[] listSurveyQuestionByType(QuestionType type) {

		QuestionDao questionDao = new QuestionDao();
		List<Question> qList = questionDao.listQuestionByType(type);
		QuestionDto[] dtoList = null;
		if (qList != null) {
			dtoList = new QuestionDto[qList.size()];
			for (int i = 0; i < qList.size(); i++) {
				dtoList[i] = marshalQuestionDto(qList.get(i));
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
					Question q = marshalQuestion(qDto.getValue());
					qg.addQuestion(q, qDto.getKey());
				}
				survey.addQuestionGroup(qg);
			}
			surveyGroup.addSurvey(survey);
		}

		DtoMarshaller.copyToDto(sgDao.save(surveyGroup), value);
		return value;
	}

	private QuestionDto marshalQuestionDto(Question q) {
		QuestionDto qDto = new QuestionDto();

		qDto.setKeyId(q.getKey().getId());

		if (q.getText() != null)
			qDto.setText(q.getText());
		if (q.getTip() != null)
			qDto.setTip(q.getTip());
		if (q.getType() != null)
			qDto.setType(q.getType());
		if (q.getValidationRule() != null)
			qDto.setValidationRule(q.getValidationRule());

		if (q.getQuestionHelpList() != null) {
			ArrayList<QuestionHelp> qHList = q.getQuestionHelpList();
			for (QuestionHelp qh : qHList) {
				QuestionHelpDto qhDto = new QuestionHelpDto();
				// Beanutils throws a concurrent exception so need
				// to copy props by hand
				qhDto.setResourceUrl(qh.getResourceUrl());
				qhDto.setText(qhDto.getText());
				qDto.addQuestionHelp(qhDto);
			}
		}

		if (q.getOptionContainer() != null) {
			OptionContainer oc = q.getOptionContainer();
			OptionContainerDto ocDto = new OptionContainerDto();
			ocDto.setKeyId(oc.getKey().getId());
			if (oc.getAllowOtherFlag() != null)
				ocDto.setAllowOtherFlag(oc.getAllowOtherFlag());
			if (oc.getAllowMultipleFlag() != null)
				ocDto.setAllowMultipleFlag(oc.getAllowMultipleFlag());

			if (oc.getOptionsList() != null) {
				ArrayList<QuestionOption> optionList = oc.getOptionsList();
				for (QuestionOption qo : optionList) {
					QuestionOptionDto ooDto = new QuestionOptionDto();
					ooDto.setKeyId(qo.getKey().getId());
					if (qo.getCode() != null)
						ooDto.setCode(qo.getCode());
					if (qo.getText() != null)
						ooDto.setText(qo.getText());
					ocDto.addQuestionOption(ooDto);

				}
			}
			qDto.setOptionContainerDto(ocDto);
		}

		if (q.getDependQuestion() != null) {
			QuestionDependency qd = q.getDependQuestion();
			QuestionDependencyDto qdDto = new QuestionDependencyDto();
			qdDto.setKeyId(qd.getKey().getId());
			qdDto.setQuestionId(qd.getQuestionId());
			qdDto.setAnswerValue(qd.getAnswerValue());
			qDto.setQuestionDependency(qdDto);
		}
		return qDto;
	}

	private Question marshalQuestion(QuestionDto qdto) {
		Question q = new Question();
		if (qdto.getKeyId() != null)
			q.setKey((KeyFactory.createKey(Question.class.getSimpleName(), qdto
					.getKeyId())));

		if (qdto.getText() != null)
			q.setText(qdto.getText());
		if (qdto.getTip() != null)
			q.setTip(qdto.getTip());
		if (qdto.getType() != null)
			q.setType(qdto.getType());
		if (qdto.getValidationRule() != null)
			q.setValidationRule(qdto.getValidationRule());

		if (qdto.getQuestionHelpList() != null) {
			ArrayList<QuestionHelpDto> qHListDto = qdto.getQuestionHelpList();
			for (QuestionHelpDto qhDto : qHListDto) {
				QuestionHelp qh = new QuestionHelp();
				// Beanutils throws a concurrent exception so need
				// to copy props by hand
				qh.setResourceUrl(qhDto.getResourceUrl());
				qh.setText(qhDto.getText());
				q.addQuestionHelp(qh);
			}
		}

		if (qdto.getOptionContainerDto() != null) {
			OptionContainerDto ocDto = qdto.getOptionContainerDto();
			OptionContainer oc = new OptionContainer();
			if (ocDto.getKeyId() != null)
				oc.setKey(KeyFactory.createKey(OptionContainer.class
						.getSimpleName(), ocDto.getKeyId()));
			if (ocDto.getAllowOtherFlag() != null)
				oc.setAllowOtherFlag(ocDto.getAllowOtherFlag());
			if (ocDto.getAllowMultipleFlag() != null)
				oc.setAllowMultipleFlag(ocDto.getAllowMultipleFlag());

			if (ocDto.getOptionsList() != null) {
				ArrayList<QuestionOptionDto> optionDtoList = ocDto
						.getOptionsList();
				for (QuestionOptionDto qoDto : optionDtoList) {
					QuestionOption oo = new QuestionOption();
					if (qoDto.getKeyId() != null)
						oo.setKey((KeyFactory.createKey(QuestionOption.class
								.getSimpleName(), qoDto.getKeyId())));
					if (qoDto.getCode() != null)
						oo.setCode(qoDto.getCode());
					if (qoDto.getText() != null)
						oo.setText(qoDto.getText());
					oc.addQuestionOption(oo);

				}
			}
			q.setOptionContainer(oc);
		}
		if (qdto.getQuestionDependency() != null) {
			QuestionDependency qd = new QuestionDependency();
			if (qdto.getQuestionDependency().getKeyId() != null)
				qd.setKey(KeyFactory.createKey(QuestionDependency.class
						.getSimpleName(), qdto.getQuestionDependency()
						.getKeyId()));

			qd.setQuestionId(qdto.getQuestionDependency().getQuestionId());
			qd.setAnswerValue(qdto.getQuestionDependency().getAnswerValue());
			q.setDependQuestion(qd);

		}

		return q;
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
						TreeMap<Integer, QuestionDto> qDtoMap = new TreeMap<Integer, QuestionDto>();
						for (Entry<Integer, Question> entry : qg
								.getQuestionMap().entrySet()) {
							QuestionDto qdto = marshalQuestionDto(entry
									.getValue());

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
			String questionGroupId, boolean needDetails) {
		QuestionDao questionDao = new QuestionDao();
		List<Question> questionList = questionDao.listQuestionsByQuestionGroup(
				questionGroupId, needDetails);
		java.util.ArrayList<QuestionDto> questionDtoList = new ArrayList<QuestionDto>();
		for (Question canonical : questionList) {
			QuestionDto dto = marshalQuestionDto(canonical);

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
		Question question = marshalQuestion(value);
		question = questionDao.save(question, questionGroupId);
		if(question.getDependQuestion() != null){
			BaseDAO<QuestionDependency> dependencyDao = new BaseDAO<QuestionDependency>(QuestionDependency.class);
			dependencyDao.save(question.getDependQuestion());
		}

		return marshalQuestionDto(question);
	}

	@Override
	public QuestionGroupDto saveQuestionGroup(QuestionGroupDto dto,
			Long surveyId) {
		QuestionGroup questionGroup = new QuestionGroup();
		DtoMarshaller.copyToCanonical(questionGroup, dto);
		QuestionGroupDao questionGroupDao = new QuestionGroupDao();
		questionGroup = questionGroupDao.save(questionGroup, surveyId,null);
		DtoMarshaller.copyToDto(questionGroup, dto);
		return dto;
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

	public static final String FREE_QUESTION_TYPE = "free";
	public static final String OPTION_QUESTION_TYPE = "option";
	public static final String GEO_QUESTION_TYPE = "geo";
	public static final String VIDEO_QUESTION_TYPE = "video";
	public static final String PHOTO_QUESTION_TYPE = "photo";
	public static final String SCAN_QUESTION_TYPE = "scan";

	@Override
	public void publishSurveyAsync(Long surveyId) {
		Queue surveyAssemblyQueue = QueueFactory.getQueue("surveyAssembly");
		surveyAssemblyQueue.add(url("/app_worker/surveyassembly").param(
				"action", SurveyAssemblyRequest.ASSEMBLE_SURVEY).param(
				"surveyId", surveyId.toString()));
	}

	@Override
	public String publishSurvey(Long surveyId) {
		try {
			SurveyDAO surveyDao = new SurveyDAO();
			Survey survey = surveyDao.loadFullSurvey(surveyId);
			SurveyXMLAdapter sax = new SurveyXMLAdapter();
			ObjectFactory objFactory = new ObjectFactory();

			// System.out.println("XML Marshalling for survey: " + surveyId);
			com.gallatinsystems.survey.domain.xml.Survey surveyXML = objFactory
					.createSurvey();
			ArrayList<com.gallatinsystems.survey.domain.xml.QuestionGroup> questionGroupXMLList = new ArrayList<com.gallatinsystems.survey.domain.xml.QuestionGroup>();
			for (QuestionGroup qg : survey.getQuestionGroupList()) {
				// System.out.println("	QuestionGroup: " + qg.getKey().getId() +
				// ":"
				// + qg.getCode() + ":" + qg.getDescription());
				com.gallatinsystems.survey.domain.xml.QuestionGroup qgXML = objFactory
						.createQuestionGroup();
				Heading heading = objFactory.createHeading();
				heading.setContent(qg.getCode());
				qgXML.setHeading(heading);

				// TODO: implement questionGroup order attribute
				// qgXML.setOrder(qg.getOrder());
				ArrayList<com.gallatinsystems.survey.domain.xml.Question> questionXMLList = new ArrayList<com.gallatinsystems.survey.domain.xml.Question>();
				if (qg.getQuestionMap() != null) {
					for (Entry<Integer, Question> qEntry : qg.getQuestionMap()
							.entrySet()) {
						Question q = qEntry.getValue();
						com.gallatinsystems.survey.domain.xml.Question qXML = objFactory
								.createQuestion();
						qXML.setId(new String("" + q.getKey().getId() + ""));
						// ToDo fix
						qXML.setMandatory("false");
						if (q.getText() != null) {
							Text text = new Text();
							text.setContent(q.getText());
							qXML.setText(text);
						}
						if (q.getTip() != null) {
							Tip tip = new Tip();
							tip.setContent(q.getTip());
							qXML.setTip(tip);
						}

						if (q.getValidationRule() != null) {
							ValidationRule validationRule = objFactory
									.createValidationRule();

							// ToDo set validation rule xml
							// validationRule.setAllowDecimal(value)
						}

						// ToDo marshall xml
						// qXML.setText(q.getText());

						if (q.getType().equals(QuestionType.FREE_TEXT))
							qXML.setType(FREE_QUESTION_TYPE);
						else if (q.getType().equals(QuestionType.GEO))
							qXML.setType(GEO_QUESTION_TYPE);
						else if (q.getType().equals(QuestionType.NUMBER)) {
							qXML.setType(FREE_QUESTION_TYPE);
							ValidationRule vrule = new ValidationRule();
							vrule.setValidationType("numeric");
							vrule.setSigned("false");
							qXML.setValidationRule(vrule);
						} else if (q.getType().equals(QuestionType.OPTION))
							qXML.setType(OPTION_QUESTION_TYPE);
						else if (q.getType().equals(QuestionType.PHOTO))
							qXML.setType(PHOTO_QUESTION_TYPE);
						else if (q.getType().equals(QuestionType.VIDEO))
							qXML.setType(VIDEO_QUESTION_TYPE);
						else if (q.getType().equals(QuestionType.SCAN))
							qXML.setType(SCAN_QUESTION_TYPE);

						if (qEntry.getKey() != null)
							qXML.setOrder(qEntry.getKey().toString());
						// ToDo set dependency xml
						Dependency dependency = objFactory.createDependency();
						if (q.getDependQuestion() != null) {
							dependency.setQuestion(q.getDependQuestion()
									.getQuestionId().toString());
							dependency.setAnswerValue(q.getDependQuestion()
									.getAnswerValue());
							qXML.setDependency(dependency);
						}

						if (q.getOptionContainer() != null) {
							OptionContainer oc = q.getOptionContainer();
							// System.out.println("			OptionContainer: " +
							// oc.getKey().getId()
							// + ":" + oc.getAllowMultipleFlag() + ":"
							// + oc.getAllowOtherFlag());
							Options options = objFactory.createOptions();
							// if(oc.getAllowMultipleFlag()!=null)
							// options.setAllowMultiple()
							if (oc.getAllowOtherFlag() != null)
								options.setAllowOther(oc.getAllowOtherFlag()
										.toString());

							if (oc.getOptionsList() != null) {
								ArrayList<Option> optionList = new ArrayList<Option>();
								// System.out.println("				ocList size:" +
								// optionList.size());
								for (QuestionOption qo : oc.getOptionsList()) {
									// System.out.println("						option:" +
									// qo.getKey().getId()
									// + ":" + qo.getCode() + ":"
									// + qo.getText());
									Option option = objFactory.createOption();
									option.setContent(qo.getText());
									option.setValue(qo.getCode());
									optionList.add(option);
								}
								options.setOptionList(optionList);
							}
							qXML.setOptions(options);
						}
						questionXMLList.add(qXML);
					}
				}
				qgXML.setQuestion(questionXMLList);
				questionGroupXMLList.add(qgXML);
			}
			surveyXML.setQuestionGroup(questionGroupXMLList);
			String surveyDocument = sax.marshal(surveyXML);
			SurveyContainerDao scDao = new SurveyContainerDao();
			SurveyContainer sc = new SurveyContainer();
			sc.setSurveyId(surveyId);
			sc.setSurveyDocument(new com.google.appengine.api.datastore.Text(
					surveyDocument));
			SurveyContainer scFound = scDao.findBySurveyId(sc.getSurveyId());
			if (scFound != null) {
				scFound.setSurveyDocument(sc.getSurveyDocument());
				scDao.save(scFound);
			} else
				scDao.save(sc);
			survey.setStatus(SurveyStatus.PUBLISHED);
			surveyDao.save(survey);

		} catch (Exception ex) {
			ex.printStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append("Could not publish survey: \n cause: " + ex.getCause()
					+ " \n message" + ex.getMessage() + "\n stack trace:  ");
			// for (StackTraceElement ste : ex.getStackTrace()) {
			// sb.append("        " + ste + "\n");
			// }
			return sb.toString();
		}

		return "Survey successfully published";
	}

	@Override
	public QuestionDto loadQuestionDetails(Long questionId) {
		QuestionDao questionDao = new QuestionDao();
		Question canonical = questionDao.getByKey(questionId);
		if (canonical != null) {
			return marshalQuestionDto(canonical);
		} else {
			return null;
		}
	}

}
