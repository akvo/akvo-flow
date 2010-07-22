package org.waterforpeople.mapping.app.web;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.OptionContainerDao;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.QuestionQuestionGroupAssocDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.OptionContainer;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.QuestionQuestionGroupAssoc;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;

public class SurveyRestServlet extends AbstractRestApiServlet {

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
							.getMandatoryFlag(), importReq.getQuestionId());
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
			Integer questionOrder) throws UnsupportedEncodingException {

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
		if (surveyName != null)
			survey = surveyDao.getByPath(surveyName, surveyGroupName);

		if (survey == null) {
			survey = new Survey();
			survey.setName(surveyName);
			survey.setPath(surveyGroupName);
			survey = surveyDao.save(survey, sg.getKey().getId());
		}

		QuestionGroup qg = new QuestionGroup();
		if (questionGroupName != null) {
			qg = qgDao.getByPath(questionGroupName, surveyGroupName + "/" + surveyName);
		}

		if (qg == null) {
			String path =surveyGroupName + "/" + surveyName;
			qg = new QuestionGroup();
			qg.setCode(questionGroupName);
			qg.setPath(path);
			qg = qgDao.save(qg, survey.getKey().getId());
		}

		Question q = new Question();
		q.setText(questionText);
		q.setOrder(questionOrder);

		if (questionType.equals("GEO"))
			q.setType(QuestionDto.QuestionType.GEO);
		else if (questionType.equals("FREE_TEXT"))
			q.setType(QuestionDto.QuestionType.FREE_TEXT);
		else if (questionType.equals("OPTION")) {
			OptionContainerDao ocDao = new OptionContainerDao();
			OptionContainer oc = new OptionContainer();
			oc.setAllowMultipleFlag(allowMultipleFlag);
			oc.setAllowOtherFlag(allowOtherFlag);
			q.setType(QuestionDto.QuestionType.OPTION);
			String[] optionsParts = options.split(";");
			for (String option : optionsParts) {
				String[] parts = option.split("\\|");
				String value = parts[0];
				String text = parts[1];
				QuestionOption qo = new QuestionOption();
				qo.setCode(value);
				qo.setText(text);
				oc.addQuestionOption(qo);
			}
			ocDao.save(oc);
			q.setOptionContainer(oc);
		} else if (questionType.equals("PHOTO"))
			q.setType(QuestionDto.QuestionType.PHOTO);
		else if (questionType.equals("NUMBER"))
			q.setType(QuestionDto.QuestionType.NUMBER);

		// deal with options and dependencies

		q = qDao.save(q);

		QuestionQuestionGroupAssocDao qqgaDao = new QuestionQuestionGroupAssocDao();
		QuestionQuestionGroupAssoc qqga = new QuestionQuestionGroupAssoc();
		qqga.setQuestionGroupId(qg.getKey().getId());
		qqga.setQuestionId(q.getKey().getId());
		qqga.setOrder(questionOrder);
		qqgaDao.save(qqga);

		return true;
	}
}
