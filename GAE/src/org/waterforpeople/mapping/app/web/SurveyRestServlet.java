package org.waterforpeople.mapping.app.web;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
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
	/**
	 * 
	 */
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
		RestResponse response = new RestResponse();
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
		}
		response.setCode("200");
		response.setMessage("Record Saved status: " + questionSaved);
		return response;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// TODO Auto-generated method stub

	}

	private Boolean saveQuestion(String surveyGroupName, String surveyName,
			String questionGroupName, String questionType, String questionText,
			String options, String dependentQuestion, Boolean allowOtherFlag,
			Boolean allowMultipleFlag, Boolean mandatoryFlag,
			Integer questionOrder, Integer questionGroupOrder)
			throws UnsupportedEncodingException {

		// TODO:Change Impl Later if we support multiple langs
		surveyName = parseLangMap(surveyName).get("en");
		questionGroupName = parseLangMap(questionGroupName).get("en");

		SurveyGroupDAO sgDao = new SurveyGroupDAO();
		SurveyDAO surveyDao = new SurveyDAO();
		QuestionGroupDao qgDao = new QuestionGroupDao();
		QuestionDao qDao = new QuestionDao();
		BaseDAO<Translation> tDao = new BaseDAO<Translation>(Translation.class);
		SurveyGroup sg = null;
		if (surveyGroupName != null) {
			sg = sgDao.findBySurveyGroupName(surveyGroupName);
		}

		if (sg == null) {
			sg = new SurveyGroup();
			sg.setCode(surveyGroupName);
			sg = sgDao.save(sg);
		}

		Survey survey = null;
		String surveyPath = surveyGroupName;
		survey = surveyDao.getByPath(surveyName, surveyPath);

		if (survey == null) {
			survey = new Survey();
			survey.setName(surveyName);
			survey.setPath(surveyPath);
			survey.setCode(surveyName);
		}

		QuestionGroup qg = null;
		String qgPath = surveyGroupName + "/" + surveyName;
		qg = qgDao.getByPath(questionGroupName, qgPath);

		if (qg == null) {
			qg = new QuestionGroup();
			qg.setName(questionGroupName);
			qg.setCode(questionGroupName);
			qg.setPath(qgPath);
			survey.addQuestionGroup(questionGroupOrder, qg);
		}

		Question q = new Question();
		String questionPath = qgPath + "/" + questionGroupName;
		q.setText(parseLangMap(questionText).get("en"));
		q.setPath(questionPath);
		q.setOrder(questionOrder);
		q.setReferenceId(questionOrder.toString());

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

		if (questionType.equals("GEO"))
			q.setType(Question.Type.GEO);
		else if (questionType.equals("FREE_TEXT"))
			q.setType(Question.Type.FREE_TEXT);
		else if (questionType.equals("OPTION")) {
			q.setAllowMultipleFlag(allowMultipleFlag);
			q.setAllowOtherFlag(allowOtherFlag);
			q.setType(Type.OPTION);
			QuestionOption qo = new QuestionOption();
			for(QuestionOptionContainer qoc:parseQuestionOption(options)){
				if(qoc.getLangCode().equals("en")){
					qo.setCode(qoc.getOption());
					qo.setText(qoc.getOption());
				}else{
					Translation t = new Translation();
					t.setLanguageCode(qoc.langCode);
					t.setText(qoc.getOption());
					t.setParentType(ParentType.QUESTION_TEXT);
					qo.addTranslation(t);
				}
			}
			q.addQuestionOption(qo);
		} else if (questionType.equals("PHOTO"))
			q.setType(Question.Type.PHOTO);
		else if (questionType.equals("NUMBER"))
			q.setType(Question.Type.NUMBER);

		if (mandatoryFlag != null)
			q.setMandatoryFlag(mandatoryFlag);

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

		}else{
			q.setDependentFlag(false);
		}
		qDao.save(q);
		qg.addQuestion(questionOrder, q);
		qgDao.save(qg);
		surveyDao.save(survey);
		sgDao.save(sg);
		log.info("Just saved " + surveyGroupName + ":" + surveyName + ":"
				+ questionGroupName + ":" + questionOrder);
		return true;
	}

	private HashMap<String, String> parseLangMap(String unparsedLangParam) {
		HashMap<String, String> langMap = new HashMap<String, String>();

		String[] parts = unparsedLangParam.split(";");
		for (String item : parts) {
			String[] langParts = item.split("\\|");
			langMap.put(langParts[0], langParts[1]);
		}
		return langMap;
	}

	private ArrayList<QuestionOptionContainer> parseQuestionOption(String questionOption) {
		ArrayList<QuestionOptionContainer> qoList = new ArrayList<QuestionOptionContainer>();

		String[] parts = questionOption.split("#");
		for (String item : parts) {
			for (Map.Entry<String, String> entry : parseLangMap(item)
					.entrySet()) {
				qoList
						.add(new QuestionOptionContainer(entry.getKey(), entry
								.getValue()));
			}
		}
		return qoList;
	}

	private class QuestionOptionContainer {

		public QuestionOptionContainer(String langCode, String optionText) {
			this.setLangCode(langCode);
			this.setOption(optionText);
		}

		private String langCode = null;
		private String option = null;

		public void setLangCode(String langCode) {
			this.langCode = langCode;
		}

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
