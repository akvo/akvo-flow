package com.gallatinsystems.survey.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.domain.SurveyQuestion;

import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionHelp;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.QuestionQuestionGroupAssoc;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.survey.domain.SurveyQuestionGroupAssoc;
import com.gallatinsystems.survey.domain.SurveySurveyGroupAssoc;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;

public class SurveyDAO extends BaseDAO<Survey> {
	private static final Logger log = Logger
			.getLogger(DeviceManagerServlet.class.getName());

	public SurveyDAO() {
		super(Survey.class);
	}

	public SurveyGroup save(SurveyGroup surveyGroup) {
		return super.save(surveyGroup);
	}
	public Survey save(Survey survey, Long surveyGroupId){
		survey = super.save(survey);
		SurveySurveyGroupAssoc ssga = new SurveySurveyGroupAssoc();
		ssga.setSurveyId(survey.getKey().getId());
		ssga.setSurveyGroupId(surveyGroupId);
		SurveySurveyGroupAssocDao ssgaDao = new SurveySurveyGroupAssocDao();
		ssgaDao.save(ssga);
		return survey;
	}

	public Survey getById(Long id) {
		return super.getByKey(id);
	}

	/**
	 * loads a full survey object (whole object graph, including questions)
	 * 
	 * @param id
	 * @return
	 */
	public Survey loadFullSurvey(Long id) {
		Survey survey = getById(id);
		if (survey != null) {
			QuestionGroupDao qGroupDao = new QuestionGroupDao();
			SurveyQuestionGroupAssocDao surveyQGAssocDao = new SurveyQuestionGroupAssocDao();
			QuestionQuestionGroupAssocDao qqGroupAssocDao = new QuestionQuestionGroupAssocDao();
			QuestionDao questionDao = new QuestionDao();
			// get all the question groups for the survey
			List<SurveyQuestionGroupAssoc> surveyGroupQuestionAssocList = surveyQGAssocDao
					.listBySurveyId(survey.getKey().getId());
			// for each question group id, load the full question group and then
			// populate its questions
			for (SurveyQuestionGroupAssoc itemSQGA : surveyGroupQuestionAssocList) {
				QuestionGroup qg = qGroupDao.getByKey(itemSQGA
						.getQuestionGroupId());
				List<QuestionQuestionGroupAssoc> qqgaList = qqGroupAssocDao
						.listByQuestionGroupId(qg.getKey().getId());
				int i = 0;
				for (QuestionQuestionGroupAssoc qqgaItem : qqgaList) {
					Question question = questionDao.getByKey(qqgaItem
							.getQuestionId());
					//ToDo fix
					qg.addQuestion(question, i++);
				}
				survey.addQuestionGroup(qg);
			}
		}
		return survey;
	}

	public Long save(Long surveyId, String surveyDocument) {
		SurveyContainer sc = new SurveyContainer();
		sc.setSurveyId(surveyId);
		com.google.appengine.api.datastore.Text surveyText = new com.google.appengine.api.datastore.Text(
				surveyDocument);
		sc.setSurveyDocument(surveyText);
		sc = super.save(sc);
		return sc.getKey().getId();
	}

	public String getForTest() {
		// Question quest = super.getByKey(300L);
		StringBuilder sb = new StringBuilder();

		List<QuestionQuestionGroupAssoc> qqgaList = super.list(
				QuestionQuestionGroupAssoc.class, "all");
		for (QuestionQuestionGroupAssoc qqga : qqgaList) {
			sb.append(qqga.toString());
			// if (qqga.getQuestion() != null) {
			// sb.append("------->" + qqga.getQuestion().toString());
			// for (QuestionOption oo : qqga.getQuestion().getOptionsList()) {
			// sb.append(oo.toString());
			// }
			// }
		}
		return sb.toString();
	}


	public void test() {

		QuestionOption questionOption = new QuestionOption();
		questionOption.setCode("test");
		questionOption.setText("test text");

		QuestionOption questionOption2 = new QuestionOption();
		questionOption.setCode("test2");
		questionOption.setText("test text");

		QuestionHelp questionHelp = new QuestionHelp();
		questionHelp.setText("Help");
		questionHelp
				.setResourceUrl("http://gallatinsystems.com/help/help.html");

		Question question = new Question();
		question.setText("Question2");
		question.setType(QuestionDto.QuestionType.OPTION);
		question.addQuestionHelp(questionHelp);

		// super.save(question);

		QuestionGroup questionGroup = new QuestionGroup();
		questionGroup.setCode("gpr" + new Date().toString());
		questionGroup.setDescription("Test Desc");

		// super.save(questionGroup);

		QuestionQuestionGroupAssoc qqga = new QuestionQuestionGroupAssoc();
		// qqga.setQuestion(question);
		// qqga.setQuestionGroup(questionGroup);

		super.save(qqga);

		com.gallatinsystems.survey.domain.Survey survey = new com.gallatinsystems.survey.domain.Survey();
		survey.setCreatedDateTime(new Date());
		survey.setCreateUserId(1L);
		survey.setName("test");
		survey.setVersion(1);

		super.save(survey);

		log.info("BaseDAO test survey key: " + survey.getKey().getId());
	}

	public com.gallatinsystems.survey.domain.xml.Survey get(Long id) {
		SurveyContainer surveyContainer = getByKey(id, SurveyContainer.class);

		SurveyXMLAdapter sxa = new SurveyXMLAdapter();
		com.gallatinsystems.survey.domain.xml.Survey survey = null;
		try {
			survey = sxa.unmarshall(surveyContainer.getSurveyDocument()
					.toString());
		} catch (JAXBException e) {
			log.log(Level.SEVERE, "Could not unmarshal xml", e);
		}
		return survey;
	}

	public String getSurveyDocument(Long id) {
		SurveyContainer surveyContainer = getByKey(id, SurveyContainer.class);
		return surveyContainer.getSurveyDocument().getValue();
	}

	public List<SurveyContainer> listSurveyContainers() {
		return super.list(SurveyContainer.class, "all");
	}

	/**
	 * lists all questions of a given type (across all surveys)
	 */
	public List<SurveyQuestion> listQuestionByType(String questionType) {
		return listByProperty("type", questionType, "String",
				SurveyQuestion.class);
	}

	public List<SurveyGroup> listSurveyGroup(String cursorString) {
		return super.list(SurveyGroup.class, cursorString);
	}

	/**
	 * Fetches all surveys for a survey group NOTE: this is duplicative of
	 * SurveyGroupDao.getById
	 * 
	 * @param surveyGroupId
	 * @return
	 */
	public List<Survey> getSurveyForSurveyGroup(String surveyGroupId) {
		List<Survey> surveyList = null;
		SurveySurveyGroupAssocDao surveySurveyGroupAssocDao = new SurveySurveyGroupAssocDao();
		List<SurveySurveyGroupAssoc> surveySurveyGroupAssocList = surveySurveyGroupAssocDao
				.listBySurveyGroupId(new Long(surveyGroupId));
		if (surveySurveyGroupAssocList != null) {
			surveyList = new ArrayList<Survey>();
			for (SurveySurveyGroupAssoc assoc : surveySurveyGroupAssocList) {
				Survey s = getByKey(assoc.getSurveyId());
				if (s != null) {
					surveyList.add(s);
				}
			}
		}
		return surveyList;
	}

}
