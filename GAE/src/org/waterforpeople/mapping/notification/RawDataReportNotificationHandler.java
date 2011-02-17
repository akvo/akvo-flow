package org.waterforpeople.mapping.notification;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;

/**
 * notifier that is capable of generating the raw data report for a survey and
 * emailing it to a list of recipients
 * 
 * @author Christopher Fagiani
 * 
 */
public class RawDataReportNotificationHandler implements NotificationHandler {
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
		sendMail("http://watermapmonitordev.appspot.com",bos);
		if (hist.getChecksum() == null
				|| !hist.getChecksum().equals(newChecksum)) {
			hist.setChecksum(newChecksum);
			DateFormat df = new SimpleDateFormat("MMddyyyy");
			if ("false".equalsIgnoreCase(PropertyUtil
					.getProperty("attachreport"))) {
				String fileName = "rawDataReport-" + entityId + "-"
						+ df.format(new Date()) + ".txt";
				UploadUtil.upload(bos, fileName, PropertyUtil
						.getProperty("reportS3Path"), PropertyUtil
						.getProperty("surveyuploadurl"), PropertyUtil
						.getProperty("aws_identifier"), PropertyUtil
						.getProperty("reportS3Policy"), PropertyUtil
						.getProperty("reportS3Sig"), "text/plain");
				StringTokenizer strTok = new StringTokenizer(destinations,
						NotificationRequest.DELIMITER);
				TreeMap<String, String> addr = new TreeMap<String, String>();
				while (strTok.hasMoreTokens()) {
					String name = strTok.nextToken();
					addr.put(name, name);
				}
				MailUtil.sendMail(FROM_ADDRESS, "FLOW", addr,
						"FLOW Raw Data Report",
						"Please see the latest raw data report here: "
								+ PropertyUtil.getProperty("surveyuploadurl")
								+ PropertyUtil.getProperty("reportS3Path")
								+ "/" + fileName);
			} else {
				MailUtil.sendMail(FROM_ADDRESS, destinations,
						NotificationRequest.DELIMITER, "FLOW Raw Data Report",
						"Please see the latest raw data report", bos
								.toByteArray(), "rawDataReport.txt",
						"text/plain");
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
	
	private void sendMail(String serverBase, ByteArrayOutputStream bos){
		MailService mailService = MailServiceFactory.getMailService();
		MailService.Message message = new MailService.Message();
		message.setSender("dru.borden@gmail.com");
		message.setSubject("title");
		message.setTo("dru.borden@gmail.com");
		message.setHtmlBody("<HTML>File Attached</HTML>");
		MailService.Attachment attachment =
		        new MailService.Attachment("rawdata.txt", bos.toByteArray());
		message.setAttachments(attachment);
		try {
			mailService.send(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
