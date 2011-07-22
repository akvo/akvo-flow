package org.waterforpeople.mapping.notification;

import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.gallatinsystems.notification.NotificationRequest;
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

	private static final String APPROVAL_HEADER = "A submission has been approved:";
	private static final String APPROVAL_FOOTER = "Please visit the dashboard to view the details";
	private static final String SUBMISSION_HEADER = "A submission has been received:";
	private static final String SUBMISSION_FOOTER = "Please visit the daashboard to approve";
	private static final String SUBMISSION_SUBJECT = "FLOW Submisson Received";
	private static final String APPROVAL_SUBJECT = "FLOW Submission Approval";

	private SurveyedLocaleDao localeDao;

	public SurveyEventNotificationHandler() {
		super();
		localeDao = new SurveyedLocaleDao();
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
		if (EVENTS[0].equalsIgnoreCase(type)) {
			body = formBody(SUBMISSION_HEADER, contents.toString(),
					SUBMISSION_FOOTER);
		} else if (EVENTS[1].equalsIgnoreCase(type)) {
			body = formBody(APPROVAL_HEADER, contents.toString(),
					APPROVAL_FOOTER);
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
