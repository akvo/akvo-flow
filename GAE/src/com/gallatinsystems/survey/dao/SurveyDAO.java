package com.gallatinsystems.survey.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.xml.bind.JAXBException;

import org.waterforpeople.mapping.domain.AccessPoint;

import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionHelp;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.QuestionQuestionGroupAssoc;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.xml.Survey;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;

public class SurveyDAO extends BaseDAO {
	PersistenceManager pm;
	private static final Logger log = Logger
			.getLogger(DeviceManagerServlet.class.getName());

	public Long save(String surveyDefinition) {
		Long id = 0L;
		SurveyContainer sc = new SurveyContainer();
		com.google.appengine.api.datastore.Text surveyText = new com.google.appengine.api.datastore.Text(
				surveyDefinition);
		sc.setSurveyDocument(surveyText);
		pm.makePersistent(sc);
		return sc.getKey().getId();
	}

	public String getForTest() {
		//Question quest = super.getByKey(300L);
		StringBuilder sb = new StringBuilder();
		List<Question> questionList = super.list(new Question());

		List<QuestionQuestionGroupAssoc> qqgaList = super
				.list(new QuestionQuestionGroupAssoc());
		for (QuestionQuestionGroupAssoc qqga : qqgaList) {
			sb.append(qqga.toString());
			if (qqga.getQuestion() != null) {
				sb.append("------->" + qqga.getQuestion().toString());
				for (QuestionOption oo : qqga.getQuestion().getOptionsList()) {
					sb.append(oo.toString());
				}
			}
		}

		List<com.gallatinsystems.survey.domain.Survey> surveyList = super
				.list(new com.gallatinsystems.survey.domain.Survey());

		for (com.gallatinsystems.survey.domain.Survey survey : surveyList) {
			// sb.append(survey.toString());
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
		question.setType(Question.QuestionType.OPTION);
		question.addOption(questionOption);
		question.addOption(questionOption2);
		question.addQuestionHelp(questionHelp);

		// super.save(question);

		QuestionGroup questionGroup = new QuestionGroup();
		questionGroup.setCode("gpr" + new Date().toString());
		questionGroup.setDescription("Test Desc");

		// super.save(questionGroup);

		QuestionQuestionGroupAssoc qqga = new QuestionQuestionGroupAssoc();
		qqga.setQuestion(question);
		qqga.setQuestionGroup(questionGroup);

		super.save(qqga);

		com.gallatinsystems.survey.domain.Survey survey = new com.gallatinsystems.survey.domain.Survey();
		survey.setCreatedDateTime(new Date());
		survey.setCreateUserId(1L);
		survey.setName("test");
		survey.setVersion(1);

		super.save(survey);
		
		
		log.info("BaseDAO test survey key: " + survey.getKey().getId());		
	}

	public Survey get(Long id) {
		SurveyContainer surveyContainer = null;

		javax.jdo.Query query = pm.newQuery(SurveyContainer.class);
		query.setFilter("id == idParam");
		query.declareParameters("Long idParam");
		List<SurveyContainer> results = (List<SurveyContainer>) query
				.execute(id);
		if (results.size() > 0) {
			surveyContainer = results.get(0);
		}

		SurveyXMLAdapter sxa = new SurveyXMLAdapter();
		Survey survey = null;
		try {
			survey = sxa.unmarshall(surveyContainer.getSurveyDocument()
					.toString());
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return survey;
	}

	public String getSurveyDocument(Long id) {
		SurveyContainer surveyContainer = null;

		javax.jdo.Query query = pm.newQuery(SurveyContainer.class);
		query.setFilter("id == idParam");
		query.declareParameters("Long idParam");
		List<SurveyContainer> results = (List<SurveyContainer>) query
				.execute(id);
		if (results.size() > 0) {
			surveyContainer = results.get(0);
		}

		return surveyContainer.getSurveyDocument().getValue();
	}

	public List<SurveyContainer> listSurveyContainers() {
		SurveyContainer surveyContainer = null;

		javax.jdo.Query query = pm.newQuery(SurveyContainer.class);

		List<SurveyContainer> results = (List<SurveyContainer>) query.execute();
		return results;
	}

	private void init() {
		pm = super.getPersistenceManager();
	}

	public SurveyDAO() {
		init();
	}
}
