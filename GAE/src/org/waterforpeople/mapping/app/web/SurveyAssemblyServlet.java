package org.waterforpeople.mapping.app.web;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import org.waterforpeople.mapping.app.web.dto.SurveyAssemblyRequest;
import org.waterforpeople.mapping.dao.SurveyContainerDao;

import com.gallatinsystems.common.util.UploadUtil;
import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.SurveyXMLFragmentDao;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.ScoringRule;
import com.gallatinsystems.survey.domain.SurveyContainer;
import com.gallatinsystems.survey.domain.SurveyXMLFragment;
import com.gallatinsystems.survey.domain.Translation;
import com.gallatinsystems.survey.domain.SurveyXMLFragment.FRAGMENT_TYPE;
import com.gallatinsystems.survey.domain.xml.AltText;
import com.gallatinsystems.survey.domain.xml.Dependency;
import com.gallatinsystems.survey.domain.xml.Help;
import com.gallatinsystems.survey.domain.xml.ObjectFactory;
import com.gallatinsystems.survey.domain.xml.Option;
import com.gallatinsystems.survey.domain.xml.Options;
import com.gallatinsystems.survey.domain.xml.Score;
import com.gallatinsystems.survey.domain.xml.Scoring;
import com.gallatinsystems.survey.domain.xml.ValidationRule;
import com.gallatinsystems.survey.xml.SurveyXMLAdapter;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

public class SurveyAssemblyServlet extends AbstractRestApiServlet {
	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(SurveyAssemblyServlet.class.getName());

	private static final long serialVersionUID = -6044156962558183224L;
	public static final String FREE_QUESTION_TYPE = "free";
	public static final String OPTION_QUESTION_TYPE = "option";
	public static final String GEO_QUESTION_TYPE = "geo";
	public static final String VIDEO_QUESTION_TYPE = "video";
	public static final String PHOTO_QUESTION_TYPE = "photo";
	public static final String SCAN_QUESTION_TYPE = "scan";

	private static final String SURVEY_UPLOAD_URL = "surveyuploadurl";
	private static final String SURVEY_UPLOAD_DIR = "surveyuploaddir";
	private static final String SURVEY_UPLOAD_SIG = "surveyuploadsig";
	private static final String SURVEY_UPLOAD_POLICY = "surveyuploadpolicy";
	private static final String S3_ID = "aws_identifier";

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new SurveyAssemblyRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		RestResponse response = new RestResponse();
		SurveyAssemblyRequest importReq = (SurveyAssemblyRequest) req;
		if (SurveyAssemblyRequest.ASSEMBLE_SURVEY.equalsIgnoreCase(importReq
				.getAction())) {
			assembleSurvey(importReq.getSurveyId());
		} else if (SurveyAssemblyRequest.DISPATCH_ASSEMBLE_QUESTION_GROUP
				.equalsIgnoreCase(importReq.getAction())) {
			this.dispatchAssembleQuestionGroup(importReq.getSurveyId(),
					importReq.getQuestionGroupId());
		} else if (SurveyAssemblyRequest.ASSEMBLE_QUESTION_GROUP
				.equalsIgnoreCase(importReq.getAction())) {
			assembleQuestionGroups(importReq.getSurveyId());
		} else if (SurveyAssemblyRequest.DISTRIBUTE_SURVEY
				.equalsIgnoreCase(importReq.getAction())) {
			uploadSurvey(importReq.getSurveyId());
		} else if (SurveyAssemblyRequest.CLEANUP.equalsIgnoreCase(importReq
				.getAction())) {
			cleanupFragments(importReq.getSurveyId());
		}

		return response;
	}

	/**
	 * uploads full survey XML to S3
	 * 
	 * @param surveyId
	 */
	private void uploadSurvey(Long surveyId) {
		SurveyContainerDao scDao = new SurveyContainerDao();
		SurveyContainer sc = scDao.findBySurveyId(surveyId);
		Properties props = System.getProperties();
		String document = sc.getSurveyDocument().getValue();
		UploadUtil.sendStringAsFile(sc.getSurveyId() + ".xml", document, props
				.getProperty(SURVEY_UPLOAD_DIR), props
				.getProperty(SURVEY_UPLOAD_URL), props.getProperty(S3_ID),
				props.getProperty(SURVEY_UPLOAD_POLICY), props
						.getProperty(SURVEY_UPLOAD_SIG), "text/xml");

		ByteArrayOutputStream os = ZipUtil.generateZip(document, sc
				.getSurveyId()
				+ ".xml");

		UploadUtil.upload(os, sc.getSurveyId() + ".zip", props
				.getProperty(SURVEY_UPLOAD_DIR), props
				.getProperty(SURVEY_UPLOAD_URL), props.getProperty(S3_ID),
				props.getProperty(SURVEY_UPLOAD_POLICY), props
						.getProperty(SURVEY_UPLOAD_SIG), "application/zip");

		sendQueueMessage(SurveyAssemblyRequest.CLEANUP, surveyId, null);
	}

	/**
	 * deletes fragments for the survey
	 * 
	 * @param surveyId
	 */
	private void cleanupFragments(Long surveyId) {
		SurveyXMLFragmentDao sxmlfDao = new SurveyXMLFragmentDao();
		sxmlfDao.deleteFragmentsForSurvey(surveyId);
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// TODO Auto-generated method stub

	}

	private void assembleSurvey(Long surveyId) {

		/**************
		 * 1, Select survey based on surveyId 2. Retrieve all question groups
		 * fire off queue tasks
		 */
		QuestionGroupDao qgDao = new QuestionGroupDao();
		TreeMap<Integer, QuestionGroup> qgList = qgDao
				.listQuestionGroupsBySurvey(surveyId);
		if (qgList != null) {
			ArrayList<Long> questionGroupIdList = new ArrayList<Long>();
			StringBuilder builder = new StringBuilder();
			int count = 1;
			for (QuestionGroup item : qgList.values()) {
				questionGroupIdList.add(item.getKey().getId());
				builder.append(item.getKey().getId());
				if (count < qgList.size()) {
					builder.append(",");
				}
				count++;
			}
			count = 0;

			sendQueueMessage(
					SurveyAssemblyRequest.DISPATCH_ASSEMBLE_QUESTION_GROUP,
					surveyId, builder.toString());
		}
	}

	/**
	 * sends a message to the task queue for survey assembly
	 * 
	 * @param action
	 * @param surveyId
	 * @param questionGroups
	 */
	private void sendQueueMessage(String action, Long surveyId,
			String questionGroups) {
		Queue surveyAssemblyQueue = QueueFactory.getQueue("surveyAssembly");
		TaskOptions task = url("/app_worker/surveyassembly").param("action",
				action).param("surveyId", surveyId.toString());
		if (questionGroups != null) {
			task.param("questionGroupId", questionGroups);
		}
		surveyAssemblyQueue.add(task);
	}

	private void dispatchAssembleQuestionGroup(Long surveyId,
			String questionGroupIds) {
		boolean isLast = true;
		String currentId = questionGroupIds;
		String remainingIds = null;
		if (questionGroupIds.contains(",")) {
			isLast = false;
			currentId = questionGroupIds.substring(0, questionGroupIds
					.indexOf(","));
			remainingIds = questionGroupIds.substring(questionGroupIds
					.indexOf(",") + 1);
		}

		QuestionDao questionDao = new QuestionDao();
		QuestionGroupDao questionGroupDao = new QuestionGroupDao();
		QuestionGroup group = questionGroupDao.getByKey(Long
				.parseLong(currentId));
		TreeMap<Integer, Question> questionList = questionDao
				.listQuestionsByQuestionGroup(Long.parseLong(currentId), true);

		StringBuilder sb = new StringBuilder("<questionGroup><heading>")
				.append(group.getCode()).append("</heading>");
		int count = 0;

		if (questionList != null) {
			for (Question q : questionList.values()) {
				sb.append(marshallQuestion(q));
				count++;
			}
		}
		SurveyXMLFragment sxf = new SurveyXMLFragment();
		sxf.setSurveyId(surveyId);
		sxf.setQuestionGroupId(Long.parseLong(currentId));
		sxf.setFragmentOrder(group.getOrder());
		sxf.setFragment(new Text(sb.append("</questionGroup>").toString()));

		sxf.setFragmentType(FRAGMENT_TYPE.QUESTION_GROUP);
		SurveyXMLFragmentDao sxmlfDao = new SurveyXMLFragmentDao();
		sxmlfDao.save(sxf);
		if (isLast) {
			// Assemble the fragments
			sendQueueMessage(SurveyAssemblyRequest.ASSEMBLE_QUESTION_GROUP,
					surveyId, null);

		} else {
			sendQueueMessage(
					SurveyAssemblyRequest.DISPATCH_ASSEMBLE_QUESTION_GROUP,
					surveyId, remainingIds);
		}
	}

	private String marshallQuestion(Question q) {

		SurveyXMLAdapter sax = new SurveyXMLAdapter();
		ObjectFactory objFactory = new ObjectFactory();
		com.gallatinsystems.survey.domain.xml.Question qXML = objFactory
				.createQuestion();
		qXML.setId(new String("" + q.getKey().getId() + ""));
		// ToDo fix
		qXML.setMandatory("false");
		if (q.getText() != null) {
			com.gallatinsystems.survey.domain.xml.Text t = new com.gallatinsystems.survey.domain.xml.Text();
			t.setContent(q.getText());
			qXML.setText(t);
		}
		if (q.getTip() != null) {
			Help tip = new Help();
			com.gallatinsystems.survey.domain.xml.Text t = new com.gallatinsystems.survey.domain.xml.Text();
			t.setContent(q.getTip());
			tip.setText(t);
			qXML.setHelp(tip);
		}

		if (q.getValidationRule() != null) {
			ValidationRule validationRule = objFactory.createValidationRule();

			// TODO set validation rule xml
			// validationRule.setAllowDecimal(value)
		}
		qXML.setAltText(formAltText(q.getTranslationMap()));

		if (q.getType().equals(Question.Type.FREE_TEXT)) {
			qXML.setType(FREE_QUESTION_TYPE);
		} else if (q.getType().equals(Question.Type.GEO)) {
			qXML.setType(GEO_QUESTION_TYPE);
		} else if (q.getType().equals(Question.Type.NUMBER)) {
			qXML.setType(FREE_QUESTION_TYPE);
			ValidationRule vrule = new ValidationRule();
			vrule.setValidationType("numeric");
			vrule.setSigned("false");
			qXML.setValidationRule(vrule);
		} else if (q.getType().equals(Question.Type.OPTION)) {
			qXML.setType(OPTION_QUESTION_TYPE);
		} else if (q.getType().equals(Question.Type.PHOTO)) {
			qXML.setType(PHOTO_QUESTION_TYPE);
		} else if (q.getType().equals(Question.Type.VIDEO)) {
			qXML.setType(VIDEO_QUESTION_TYPE);
		} else if (q.getType().equals(Question.Type.SCAN)) {
			qXML.setType(SCAN_QUESTION_TYPE);
		} else if (q.getType().equals(Question.Type.NAME)) {
			qXML.setType(FREE_QUESTION_TYPE);
			ValidationRule vrule = new ValidationRule();
			vrule.setValidationType("name");
			qXML.setValidationRule(vrule);
		}

		if (q.getOrder() != null) {
			qXML.setOrder(q.getOrder().toString());
		}
		if (q.getMandatoryFlag() != null) {
			qXML.setMandatory(q.getMandatoryFlag().toString());
		}
		Dependency dependency = objFactory.createDependency();
		if (q.getDependentQuestionId() != null) {
			dependency.setQuestion(q.getDependentQuestionId().toString());
			dependency.setAnswerValue(q.getDependentQuestionAnswer());
			qXML.setDependency(dependency);
		}

		if (q.getQuestionOptionMap() != null
				&& q.getQuestionOptionMap().size() > 0) {
			Options options = objFactory.createOptions();
			if (q.getAllowOtherFlag() != null) {
				options.setAllowOther(q.getAllowOtherFlag().toString());
			}
			if (q.getAllowMultipleFlag() != null) {
				options.setAllowMultiple(q.getAllowMultipleFlag().toString());
			}

			ArrayList<Option> optionList = new ArrayList<Option>();
			for (QuestionOption qo : q.getQuestionOptionMap().values()) {
				Option option = objFactory.createOption();
				com.gallatinsystems.survey.domain.xml.Text t = new com.gallatinsystems.survey.domain.xml.Text();
				t.setContent(qo.getText());
				option.addContent(t);
				option.setValue(qo.getCode());
				List<AltText> altTextList = formAltText(qo.getTranslationMap());
				if (altTextList != null) {
					for (AltText alt : altTextList) {
						option.addContent(alt);
					}
				}
				optionList.add(option);
			}
			options.setOption(optionList);

			qXML.setOptions(options);
		}

		if (q.getScoringRules() != null) {
			Scoring scoring = new Scoring();

			for (ScoringRule rule : q.getScoringRules()) {
				Score score = new Score();
				if (scoring.getType() == null) {
					scoring.setType(rule.getType().toLowerCase());
				}
				score.setRangeHigh(rule.getRangeMax());
				score.setRangeLow(rule.getRangeMin());
				score.setValue(rule.getValue());
				scoring.addScore(score);
			}
			if (scoring.getScore() != null && scoring.getScore().size() > 0) {
				qXML.setScoring(scoring);
			}
		}

		String questionDocument = null;
		try {
			questionDocument = sax.marshal(qXML);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		questionDocument = questionDocument
				.replace(
						"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>",
						"");
		return questionDocument;
	}

	private List<AltText> formAltText(Map<String, Translation> translationMap) {
		List<AltText> altTextList = new ArrayList<AltText>();
		if (translationMap != null) {
			for (Translation lang : translationMap.values()) {
				AltText alt = new AltText();
				alt.setContent(lang.getText());
				alt.setType("translation");
				alt.setLanguage(lang.getLanguageCode());
				altTextList.add(alt);
			}
		}

		return altTextList;
	}

	private void assembleQuestionGroups(Long surveyId) {
		SurveyXMLFragmentDao sxmlfDao = new SurveyXMLFragmentDao();
		List<SurveyXMLFragment> sxmlfList = sxmlfDao.listSurveyFragments(
				surveyId, SurveyXMLFragment.FRAGMENT_TYPE.QUESTION_GROUP);
		StringBuilder sbQG = new StringBuilder();
		for (SurveyXMLFragment item : sxmlfList) {
			sbQG.append(item.getFragment().getValue());
		}
		StringBuilder completeSurvey = new StringBuilder();
		String surveyHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><survey>";
		String surveyFooter = "</survey>";
		completeSurvey.append(surveyHeader);
		completeSurvey.append(sbQG.toString());
		sbQG = null;
		completeSurvey.append(surveyFooter);

		SurveyContainerDao scDao = new SurveyContainerDao();
		SurveyContainer sc = scDao.findBySurveyId(surveyId);
		if (sc == null) {
			sc = new SurveyContainer();
		}
		sc.setSurveyDocument(new Text(completeSurvey.toString()));
		sc.setSurveyId(surveyId);

		scDao.save(sc);

		sendQueueMessage(SurveyAssemblyRequest.DISTRIBUTE_SURVEY, surveyId,
				null);
	}
}