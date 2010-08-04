package org.waterforpeople.mapping.app.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;

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

		SurveyGroupDAO sgDao = new SurveyGroupDAO();
		SurveyDAO surveyDao = new SurveyDAO();
		QuestionGroupDao qgDao = new QuestionGroupDao();
		QuestionDao qDao = new QuestionDao();

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
		if (sg.getSurveyList() != null)
			for (Survey item : sg.getSurveyList()) {
				if (survey.getCode().equals(surveyName)) {
					survey = item;
				}
			}

		if (survey == null) {
			survey = new Survey();
			survey.setNameMap(parseLangMap(surveyName));
			survey.setPath(surveyGroupName);
			survey.setCode(surveyName);
		}

		QuestionGroup qg = null;
		if (survey.getQuestionGroupMap() != null)
			for (Map.Entry<Integer, QuestionGroup> qgEntry : survey
					.getQuestionGroupMap().entrySet()) {
				if (qgEntry.getValue().getCode().equals(questionGroupName))
					qg = qgEntry.getValue();
			}

		if (qg == null) {
			
			String path = surveyGroupName + "/" + surveyName;
			qg = new QuestionGroup();
			qg.setCode(questionGroupName);
			qg.setNameMap(parseLangMap(questionGroupName));
			qg.setPath(path);
			qg.setDescMap(parseLangMap(questionGroupName));
			qg.setNameMap(parseLangMap(questionGroupName));
		}

		survey.addQuestionGroup(questionGroupOrder, qg);

		Question q = new Question();
		q.setTextMap(parseLangMap(questionText));
		q.setOrder(questionOrder);

		if (questionType.equals("GEO"))
			q.setType(Question.Type.GEO);
		else if (questionType.equals("FREE"))
			q.setType(Question.Type.TEXT);
		else if (questionType.equals("OPTION")) {
			// oc.setAllowMultipleFlag(allowMultipleFlag);
			// oc.setAllowOtherFlag(allowOtherFlag);
			// q.setType(QuestionDto.QuestionType.OPTION);
			// String[] optionsParts = options.split(";");
			// for (String option : optionsParts) {
			// String[] parts = option.split("\\|");
			// String value = parts[0];
			// String text = parts[1];
			// QuestionOption qo = new QuestionOption();
			// qo.setCode(value);
			// qo.setText(text);
			// oc.addQuestionOption(qo);
			// }
			// ocDao.save(oc);
			// q.setOptionContainer(oc);
		} else if (questionType.equals("PHOTO"))
			q.setType(Question.Type.PHOTO);
		else if (questionType.equals("NUMBER"))
			q.setType(Question.Type.NUMBER);

		if (mandatoryFlag != null)
			q.setMandatoryFlag(mandatoryFlag);

		// deal with options and dependencies
		String questionPath = surveyGroupName + "/" + surveyName;
		q.setPath(questionPath);

		if (dependentQuestion != null && dependentQuestion.trim().length() > 1) {
			String[] parts = dependentQuestion.split("\\|");
			Integer quesitonOrderId = new Integer(parts[0]);
			String answer = parts[1];
			Question dependsOnQuestion = qDao.getByPath(quesitonOrderId,
					questionPath);
			if (dependsOnQuestion != null) {
				// QuestionDependency qd = new QuestionDependency();
				// qd.setAnswerValue(answer);
				// qd.setQuestionId(dependsOnQuestion.getKey().getId());
				// q.setDependQuestion(qd);
			}

		}
		qg.addQuestion(questionOrder, q);
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
}
