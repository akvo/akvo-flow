package com.gallatinsystems.survey.dao;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.xml.bind.JAXBException;

import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.domain.SurveyQuestion;

import com.gallatinsystems.device.app.web.DeviceManagerServlet;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionHelp;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.QuestionQuestionGroupAssoc;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyGroup;
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

	public Long save(String surveyDefinition) {
		SurveyContainer sc = new SurveyContainer();
		com.google.appengine.api.datastore.Text surveyText = new com.google.appengine.api.datastore.Text(
				surveyDefinition);
		sc.setSurveyDocument(surveyText);
		sc = super.save(sc);
		return sc.getKey().getId();
	}

	public String getForTest() {
		// Question quest = super.getByKey(300L);
		StringBuilder sb = new StringBuilder();

		List<QuestionQuestionGroupAssoc> qqgaList = super
				.list(QuestionQuestionGroupAssoc.class);
		for (QuestionQuestionGroupAssoc qqga : qqgaList) {
			sb.append(qqga.toString());
			if (qqga.getQuestion() != null) {
				sb.append("------->" + qqga.getQuestion().toString());
				for (QuestionOption oo : qqga.getQuestion().getOptionsList()) {
					sb.append(oo.toString());
				}
			}
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public List countSurveyInstance(Date startDate, Date endDate,
			String rollUpType) {

		//TODO: find another way to do this since GAE doesn't support groupBy
		PersistenceManager pm =PersistenceFilter.getManager();
		List results = null;
		StringBuilder params = new StringBuilder();
		javax.jdo.Query query = pm.newQuery(SurveyInstance.class);
		if (startDate != null) {
			query.setFilter("collectionDate" + " >= startDateParam");
			params.append("Date startDateParam");
		}
		if ("collectionDate".equals(rollUpType)) {
			query.setResult("collectionDate, count(this)");
		} else {
			// TODO: add region to survey instance so we can do location roll-up
			query.setResult("collectionDate, count(this)");
		}
		if (endDate != null) {
			query.setFilter("collectionDate" + " <= endDateParam");
			if (startDate != null) {
				params.append(" ");
			}
			params.append("Date endDateParam");
		}
		if (rollUpType != null) {
			query.setGrouping(rollUpType);
		}
		if (startDate != null || endDate != null) {
			query.declareParameters(params.toString());
			if (startDate != null && endDate != null) {
				results = (List) query.execute(startDate, endDate);
			} else if (startDate != null) {
				results = (List) query.execute(startDate);
			} else {
				results = (List) query.execute(endDate);
			}
		} else {
			results = (List) query.execute();
		}
		return results;
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
		return super.list(SurveyContainer.class);
	}
	
	/**
	* lists all questions of a given type (across all surveys)
	*/
	public List<SurveyQuestion> listQuestionByType(String questionType){
		return listByProperty("type",questionType,"String",SurveyQuestion.class);
	}
}
