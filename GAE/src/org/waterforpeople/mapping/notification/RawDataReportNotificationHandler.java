package org.waterforpeople.mapping.notification;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.waterforpeople.mapping.dataexport.RawDataExporter;

import com.gallatinsystems.common.util.MD5Util;
import com.gallatinsystems.common.util.MailUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.common.util.UploadUtil;
import com.gallatinsystems.notification.NotificationHandler;
import com.gallatinsystems.notification.NotificationRequest;
import com.gallatinsystems.notification.dao.NotificationSubscriptionDao;
import com.gallatinsystems.notification.domain.NotificationHistory;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;

/**
 * notifier that is capable of generating the raw data report for a survey and
 * emailing it to a list of recipients
 * 
 * @author Christopher Fagiani
 * 
 */
public class RawDataReportNotificationHandler implements NotificationHandler {
	private static final String EMAIL_BODY = "Please see the latest raw data report here (Recommend you open in Microsoft Excel) for Survey : ";
	private static final String EMAIL_TITLE = "FLOW Raw Data Report for survey: ";
	private static final String REPORT_S3_SIG = "reportS3Sig";
	private static final String REPORT_S3_POLICY = "reportS3Policy";
	private static final String AWS_IDENTIFIER = "aws_identifier";
	private static final String SURVEY_UPLOAD_URL = "surveyuploadurl";
	private static final String REPORT_S3_PATH = "reportS3Path";
	private static final String DATE_DISPLAY_FORMAT = "MMddyyyy";
	private static final String ATTACH_REPORT_FLAG = "attachreport";
	public static final String TYPE = "rawDataReport";
	private final static String EMAIL_FROM_ADDRESS_KEY = "emailFromAddress";
	private static String FROM_ADDRESS;
	private static final Logger log = Logger
			.getLogger(RawDataReportNotificationHandler.class.getName());

	private NotificationSubscriptionDao dao;

	public RawDataReportNotificationHandler() {
		super();
		dao = new NotificationSubscriptionDao();
		FROM_ADDRESS = PropertyUtil.getProperty(EMAIL_FROM_ADDRESS_KEY);
	}

	/**
	 * generates the report and sends it as an email attachment
	 * 
	 */
	@Override
	public void generateNotification(Long entityId, String destinations,
			String serverBase) {
		RawDataExporter exporter = new RawDataExporter();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(bos);
		exporter.export(serverBase, entityId, pw);
		pw.flush();
		NotificationHistory hist = getHistory(TYPE, entityId);
		String newChecksum = MD5Util.generateChecksum(bos.toByteArray());
		SurveyDAO surveyDao = new SurveyDAO();
		Survey survey = surveyDao.getById(entityId);
		String emailTitle = EMAIL_TITLE + survey.getPath() + "/"
				+ survey.getName();
		String emailBody = EMAIL_BODY + survey.getPath() + "/"
				+ survey.getName() + " ";

		if (hist.getChecksum() == null
				|| !hist.getChecksum().equals(newChecksum)) {
			hist.setChecksum(newChecksum);
			DateFormat df = new SimpleDateFormat(DATE_DISPLAY_FORMAT);
			if ("false".equalsIgnoreCase(PropertyUtil
					.getProperty(ATTACH_REPORT_FLAG))) {
				String fileName = "rawDataReport-" + entityId + "-"
						+ df.format(new Date()) + ".txt";
				UploadUtil.upload(bos, fileName,
						PropertyUtil.getProperty(REPORT_S3_PATH),
						PropertyUtil.getProperty(SURVEY_UPLOAD_URL),
						PropertyUtil.getProperty(AWS_IDENTIFIER),
						PropertyUtil.getProperty(REPORT_S3_POLICY),
						PropertyUtil.getProperty(REPORT_S3_SIG), "text/plain");
				StringTokenizer strTok = new StringTokenizer(destinations,
						NotificationRequest.DELIMITER);
				TreeMap<String, String> addr = new TreeMap<String, String>();
				while (strTok.hasMoreTokens()) {
					String name = strTok.nextToken();
					addr.put(name, name);
				}
				MailUtil.sendMail(FROM_ADDRESS, "FLOW", addr, emailTitle,
						emailBody + PropertyUtil.getProperty(SURVEY_UPLOAD_URL)
								+ PropertyUtil.getProperty(REPORT_S3_PATH)
								+ "/" + fileName);
			} else {
				String surveyCodeFormatted = null;
				if (survey.getCode() != null) {
					surveyCodeFormatted = survey.getCode().replace(" ", "_");
				} else {
					surveyCodeFormatted = "RawDataReport";
				}
				MailUtil.sendMail(FROM_ADDRESS, destinations,
						NotificationRequest.DELIMITER, emailTitle, emailBody,
						bos.toByteArray(),
						surveyCodeFormatted + "_" + df.format(new Date())
								+ ".txt", "text/plain");
			}
			NotificationSubscriptionDao.saveNotificationHistory(hist);
		}
		pw.close();
	}

	protected NotificationHistory getHistory(String type, Long id) {

		NotificationHistory hist = dao.findNotificationHistory(type, id);
		if (hist == null) {
			hist = new NotificationHistory();
			hist.setType(type);
			hist.setEntityId(id);
		}
		return hist;
	}

}
