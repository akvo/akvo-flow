package org.waterforpeople.mapping.notification;

import java.io.StringWriter;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.editorial.dao.EditorialPageDao;
import com.gallatinsystems.editorial.domain.EditorialPage;
import com.gallatinsystems.notification.NotificationRequest;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyalValue;

/**
 * This handler sends notifications in response to survey events
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyEventNotificationHandler extends BaseNotificationHandler {

	public static String[] EVENTS = { "surveySubmission", "surveyApproval" };
	public static final String APP_ED_PAGE_TEMPLATE = "Survey Approval Email Template";
	public static final String SUB_ED_PAGE_TEMPLATE = "Survey Submission Email Template";
	private static final String APPROVAL_HEADER = "A submission has been approved:";
	private static final String APPROVAL_FOOTER = "Please visit the dashboard to view the details";
	private static final String SUBMISSION_HEADER = "A submission has been received:";
	private static final String SUBMISSION_FOOTER = "Please visit the daashboard to approve";
	private static final String SUBMISSION_SUBJECT = "FLOW Submisson Received";
	private static final String APPROVAL_SUBJECT = "FLOW Submission Approval";

	private SurveyedLocaleDao localeDao;
	private SurveyInstanceDAO instDao;
	private SurveyDAO surveyDao;

	public SurveyEventNotificationHandler() {
		super();
		localeDao = new SurveyedLocaleDao();
		instDao = new SurveyInstanceDAO();
		surveyDao = new SurveyDAO();

	}

	@Override
	public void generateNotification(String type, Long entityId,
			String destinations, String destOptions, String serverBase) {
		List<SurveyalValue> values = localeDao
				.listSurveyalValuesByInstance(entityId);
		StringBuilder contents = new StringBuilder();

		TreeMap<String, String> linkAddrList = new TreeMap<String, String>();
		StringTokenizer strTok = new StringTokenizer(destinations,
				NotificationRequest.DELIMITER);
		while (strTok.hasMoreTokens()) {
			String item = strTok.nextToken();

			linkAddrList.put(item, item);

		}

		if (values != null) {
			for (SurveyalValue val : values) {
				contents.append(
						val.getMetricName() != null ? val.getMetricName() : val
								.getQuestionText()).append(": ")
						.append(val.getStringValue()).append("\n");
			}
		}
		String body = null;
		EditorialPageDao edDao = new EditorialPageDao();
		String head = null;
		String foot = null;
		String pageName = null;
		if (EVENTS[0].equalsIgnoreCase(type)) {
			pageName = SUB_ED_PAGE_TEMPLATE;
			head = SUBMISSION_HEADER;
			foot = SUBMISSION_FOOTER;
		} else if (EVENTS[1].equalsIgnoreCase(type)) {
			pageName = APP_ED_PAGE_TEMPLATE;
			head = APPROVAL_HEADER;
			foot = APPROVAL_FOOTER;

		}
		EditorialPage page = edDao.findByTargetPage(pageName);
		if (page == null) {
			body = formBody(head, contents.toString(), foot);
		} else {
			VelocityEngine engine = new VelocityEngine();
			engine.setProperty("runtime.log.logsystem.class",
					"org.apache.velocity.runtime.log.NullLogChute");
			try {
				engine.init();
				VelocityContext context = new VelocityContext();
				context.put("surveyInstanceId", entityId);
				SurveyInstance inst = instDao.getByKey(entityId);
				if (inst != null) {
					context.put(
							"submitter",
							inst.getSubmitterName() != null ? inst
									.getSubmitterName() : "");
					context.put("submissionDate", inst.getCollectionDate());
					Survey s = surveyDao.getById(inst.getSurveyId());
					if (s != null) {
						context.put("surveyId", s.getKey().getId());
						context.put("surveyName", s.getName());
						context.put("surveyPath", s.getPath());
					}
				}
				StringWriter writer = new StringWriter();
				if (engine.evaluate(context, writer, "LOG", page.getTemplate()
						.getValue())) {
					body = writer.toString();
				}
			} catch (Exception e) {

			}
		}
		sendMail(linkAddrList,
				EVENTS[0].equalsIgnoreCase(type) ? SUBMISSION_SUBJECT
						: APPROVAL_SUBJECT, body);
	}

	private String formBody(String header, String contents, String footer) {
		return new StringBuilder(header).append("\n").append(contents)
				.append("\n").append(footer).toString();
	}

}
