package org.waterforpeople.mapping.app.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.waterforpeople.mapping.analytics.dao.SurveyQuestionSummaryDao;
import org.waterforpeople.mapping.analytics.domain.SurveyQuestionSummary;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;
import org.waterforpeople.mapping.app.web.dto.SurveyRestResponse;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.QuestionOptionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.dao.TranslationDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.Question.Type;
import com.gallatinsystems.survey.domain.Translation.ParentType;

public class SurveyRestServlet extends AbstractRestApiServlet {
	private static final Logger log = Logger.getLogger(TaskServlet.class
			.getName());

	private SurveyGroupDAO sgDao;
	private SurveyDAO surveyDao;
	private QuestionOptionDao optionDao;
	private TranslationDao translationDao;
	private QuestionGroupDao qgDao;
	private QuestionDao qDao;
	private SurveyQuestionSummaryDao summaryDao;

	public SurveyRestServlet() {
		setMode(JSON_MODE);
		sgDao = new SurveyGroupDAO();
		surveyDao = new SurveyDAO();
		optionDao = new QuestionOptionDao();
		translationDao = new TranslationDao();
		qgDao = new QuestionGroupDao();
		qDao = new QuestionDao();
		summaryDao = new SurveyQuestionSummaryDao();
	}

	private static final long serialVersionUID = 1165507917062204859L;

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new SurveyRestRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		SurveyRestResponse response = new SurveyRestResponse();
		SurveyRestRequest importReq = (SurveyRestRequest) req;
		Boolean questionSaved = null;
		if (SurveyRestRequest.SAVE_QUESTION_ACTION
				.equals(importReq.getAction())) {
			questionSaved = saveQuestion(importReq.getSurveyGroupName(),
					importReq.getSurveyName(),
					importReq.getQuestionGroupName(), importReq
							.getQuestionType(), importReq.getQuestionText(),
					importReq.getOptions(), importReq.getDependQuestion(),
					importReq.getAllowOtherFlag(), importReq
							.getAllowMultipleFlag(), importReq
							.getMandatoryFlag(), importReq.getQuestionId(),
					importReq.getQuestionGroupOrder());
			response.setCode("200");
			response.setMessage("Record Saved status: " + questionSaved);
		} else if (SurveyRestRequest.LIST_GROUP_ACTION.equals(importReq
				.getAction())) {
			response.setDtoList(listQuestionGroups(new Long(importReq
					.getSurveyId())));
		} else if (SurveyRestRequest.LIST_QUESTION_ACTION.equals(importReq
				.getAction())) {
			response.setDtoList(listQuestions(new Long(importReq
					.getQuestionGroupId())));
		} else if (SurveyRestRequest.GET_SUMMARY_ACTION.equals(importReq
				.getAction())) {
			response.setDtoList(listSummaries(new Long(importReq
					.getQuestionId())));
		}

		return response;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		JSONObject obj = new JSONObject(resp, true);
		getResponse().getWriter().println(obj.toString());
	}

	private List<QuestionGroupDto> listQuestionGroups(Long surveyId) {
		TreeMap<Integer, QuestionGroup> groups = qgDao
				.listQuestionGroupsBySurvey(surveyId);
		List<QuestionGroupDto> dtoList = new ArrayList<QuestionGroupDto>();
		if (groups != null) {
			for (QuestionGroup q : groups.values()) {
				QuestionGroupDto dto = new QuestionGroupDto();
				DtoMarshaller.copyToDto(q, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	private List<QuestionDto> listQuestions(Long groupId) {
		TreeMap<Integer, Question> questions = qDao
				.listQuestionsByQuestionGroup(groupId, false);
		List<QuestionDto> dtoList = new ArrayList<QuestionDto>();
		if (questions != null) {
			for (Question q : questions.values()) {
				QuestionDto dto = new QuestionDto();
				dto.setText(q.getText());
				dto.setKeyId(q.getKey().getId());
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	private List<SurveySummaryDto> listSummaries(Long questionId) {
		List<SurveyQuestionSummary> summaries = summaryDao
				.listByQuestion(questionId.toString());
		List<SurveySummaryDto> dtoList = new ArrayList<SurveySummaryDto>();
		if (summaries != null) {
			for (SurveyQuestionSummary s : summaries) {
				SurveySummaryDto dto = new SurveySummaryDto();
				dto.setCount(s.getCount());
				dto.setResponseText(s.getResponse());
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	private Boolean saveQuestion(String surveyGroupName, String surveyName,
			String questionGroupName, String questionType, String questionText,
			String options, String dependentQuestion, Boolean allowOtherFlag,
			Boolean allowMultipleFlag, Boolean mandatoryFlag,
			Integer questionOrder, Integer questionGroupOrder)
			throws UnsupportedEncodingException {

		// TODO: Change Impl Later if we support multiple langs
		surveyName = parseLangMap(surveyName).get("en");
		questionGroupName = parseLangMap(questionGroupName).get("en");

		SurveyGroup sg = null;
		if (surveyGroupName != null) {
			sg = sgDao.findBySurveyGroupName(surveyGroupName);
		}

		if (sg == null) {
			sg = new SurveyGroup();
			sg.setCode(surveyGroupName);
			sgDao.save(sg);
		}

		Survey survey = null;
		String surveyPath = surveyGroupName;
		survey = surveyDao.getByPath(surveyName, surveyPath);

		if (survey == null) {
			survey = new Survey();
			survey.setName(surveyName);
			survey.setPath(surveyPath);
			survey.setCode(surveyName);
			survey.setSurveyGroupId(sg.getKey().getId());
			surveyDao.save(survey);
		}

		QuestionGroup qg = null;
		String qgPath = surveyGroupName + "/" + surveyName;
		qg = qgDao.getByPath(questionGroupName, qgPath);

		if (qg == null) {
			qg = new QuestionGroup();
			qg.setName(questionGroupName);
			qg.setCode(questionGroupName);
			qg.setPath(qgPath);
			qg.setOrder(questionGroupOrder);
			survey.addQuestionGroup(questionGroupOrder, qg);
			qg.setSurveyId(survey.getKey().getId());
			qgDao.save(qg);
		}

		String questionPath = qgPath + "/" + questionGroupName;
		Question q = qDao.getByPath(questionOrder, questionPath);
		if (q == null) {
			q = new Question();
		} else {
			// if the question already exists, delete it's children so we don't
			// get duplicates
			if (Question.Type.OPTION == q.getType()) {
				optionDao.deleteOptionsForQuestion(q.getKey().getId());
			}
			translationDao.deleteTranslationsForParent(q.getKey().getId(),
					ParentType.QUESTION_TEXT);
		}
		q.setText(parseLangMap(questionText).get("en"));
		q.setPath(questionPath);
		q.setOrder(questionOrder);
		q.setReferenceId(questionOrder.toString());
		q.setQuestionGroupId(qg.getKey().getId());
		q.setSurveyId(survey.getKey().getId());

		for (Map.Entry<String, String> qTextItem : parseLangMap(questionText)
				.entrySet()) {
			if (!qTextItem.getKey().equals("en")) {
				Translation t = new Translation();
				t.setLanguageCode(qTextItem.getKey());
				t.setText(qTextItem.getValue());
				t.setParentType(ParentType.QUESTION_TEXT);
				q.addTranslation(t);
			}
		}

		if (questionType.equals("GEO")){
			q.setType(Question.Type.GEO);
		}
		else if (questionType.equals("FREE_TEXT")){
			q.setType(Question.Type.FREE_TEXT);
		}
		else if (questionType.equals("OPTION")) {
			q.setAllowMultipleFlag(allowMultipleFlag);
			q.setAllowOtherFlag(allowOtherFlag);
			q.setType(Type.OPTION);
			int i = 1;
			for (QuestionOptionContainer qoc : parseQuestionOption(options)) {
				QuestionOption qo = new QuestionOption();
				qo.setText(qoc.getOption());
				qo.setCode(qoc.getOption());
				qo.setOrder(i++);
				if (qoc.getAltLangs() != null) {
					for (QuestionOptionContainer altOpt : qoc.getAltLangs()) {
						Translation t = new Translation();
						t.setLanguageCode(altOpt.langCode);
						t.setText(altOpt.getOption());
						t.setParentType(ParentType.QUESTION_TEXT);
						qo.addTranslation(t);
					}
				}
				q.addQuestionOption(qo);
			}
		} else if (questionType.equals("PHOTO")) {
			q.setType(Question.Type.PHOTO);
		} else if (questionType.equals("NUMBER")) {
			q.setType(Question.Type.NUMBER);
		}else if (questionType.equals("NAME")){
			q.setType(Question.Type.NAME);
		}

		if (mandatoryFlag != null){
			q.setMandatoryFlag(mandatoryFlag);
		}

		// deal with options and dependencies

		if (dependentQuestion != null && dependentQuestion.trim().length() > 1) {
			String[] parts = dependentQuestion.split("\\|");
			Integer quesitonOrderId = new Integer(parts[0]);
			String answer = parts[1];
			Question dependsOnQuestion = qDao.getByPath(quesitonOrderId,
					questionPath);
			if (dependsOnQuestion != null) {
				q.setDependentFlag(true);
				q.setDependentQuestionId(dependsOnQuestion.getKey().getId());
				q.setDependentQuestionAnswer(answer);
			}

		} else {
			q.setDependentFlag(false);
		}
		qDao.save(q);

		// now update the question id in the children and save
		if (q.getQuestionOptionMap() != null) {
			for (QuestionOption opt : q.getQuestionOptionMap().values()) {
				opt.setQuestionId(q.getKey().getId());
				optionDao.save(opt);
				if (opt.getTranslationMap() != null) {
					for (Translation t : opt.getTranslationMap().values()) {
						t.setParentId(opt.getKey().getId());
						t.setParentType(ParentType.QUESTION_OPTION);
						translationDao.save(t);
					}
				}
			}
		}
		if (q.getTranslationMap() != null) {
			for (Translation t : q.getTranslationMap().values()) {
				t.setParentId(q.getKey().getId());
				t.setParentType(ParentType.QUESTION_TEXT);
				translationDao.save(t);
			}
		}

		qg.addQuestion(questionOrder, q);
		qgDao.save(qg);
		surveyDao.save(survey);
		sgDao.save(sg);
		log.info("Just saved " + surveyGroupName + ":" + surveyName + ":"
				+ questionGroupName + ":" + questionOrder);
		return true;
	}

	private ArrayList<QuestionOptionContainer> parseQuestionOption(
			String questionOption) {
		ArrayList<QuestionOptionContainer> qoList = new ArrayList<QuestionOptionContainer>();

		String[] parts = questionOption.split("#");

		if (parts != null && parts.length == 1) {
			// if parts is only 1 then we either have 1 option with multiple
			// languages or only 1 option
			if (!parts[0].contains("|")) {
				// if there is no pipe, then it's 1 language only
				String[] options = parts[0].split(";");
				for (int i = 0; i < options.length; i++) {
					QuestionOptionContainer opt = parseEnglishOnlyOption(options[i]
							.trim());
					if (opt != null) {
						qoList.add(opt);
					}
				}
			} else {
				// if we're here, we have only 1 option but in multiple
				// languages
				qoList.add(composeContainer(parts[0]));
			}
		} else if (parts != null) {
			for (String option : parts) {

				qoList.add(composeContainer(option));
			}
		}
		return qoList;
	}

	private QuestionOptionContainer composeContainer(String option) {
		Map<String, String> langVals = parseLangMap(option);
		String english = langVals.remove("en");
		QuestionOptionContainer container = new QuestionOptionContainer("en",
				english);
		for (Map.Entry<String, String> entry : langVals.entrySet()) {
			container.addAltLang(new QuestionOptionContainer(entry.getKey(),
					entry.getValue()));
		}
		return container;
	}

	private HashMap<String, String> parseLangMap(String unparsedLangParam) {
		HashMap<String, String> langMap = new HashMap<String, String>();

		String[] parts = unparsedLangParam.split(";");
		for (String item : parts) {
			String[] langParts = item.split("\\|");
			if (langParts.length == 1) {
				// if there is no language indicator, assume it's English
				langMap.put("en", langParts[0].trim());
			} else {
				langMap.put(langParts[0].trim(), langParts[1].trim());
			}
		}
		return langMap;
	}

	/**
	 * handles parsing of the "old" style question options that only have a
	 * single language. The language will be defaulted to English
	 * 
	 * @param option
	 * @return
	 */
	private QuestionOptionContainer parseEnglishOnlyOption(String option) {
		QuestionOptionContainer opt = null;
		String[] val = option.split("\\|");
		String value = null;
		if (val.length == 2) {
			value = val[1];
		} else if (val.length == 1) {
			value = val[0];
		}
		if (value != null) {
			opt = new QuestionOptionContainer("en", value);
		}
		return opt;
	}

	private class QuestionOptionContainer {

		private String langCode = null;
		private String option = null;
		private List<QuestionOptionContainer> altLangs;

		public QuestionOptionContainer(String langCode, String optionText) {
			this.setLangCode(langCode);
			this.setOption(optionText);
		}

		public List<QuestionOptionContainer> getAltLangs() {
			return altLangs;
		}

		public void addAltLang(QuestionOptionContainer container) {
			if (altLangs == null) {
				altLangs = new ArrayList<QuestionOptionContainer>();
			}
			altLangs.add(container);
		}

		public void setLangCode(String langCode) {
			this.langCode = langCode;
		}

		@SuppressWarnings("unused")
		public String getLangCode() {
			return langCode;
		}

		public void setOption(String option) {
			this.option = option;
		}

		public String getOption() {
			return option;
		}
	}
}
