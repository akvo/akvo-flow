package org.waterforpeople.mapping.notification;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import org.waterforpeople.mapping.dataexport.RawDataExporter;

import com.gallatinsystems.common.util.MailUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.notification.NotificationHandler;
import com.gallatinsystems.notification.NotificationRequest;

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

	public RawDataReportNotificationHandler() {
		super();
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
		MailUtil.sendMail(FROM_ADDRESS, destinations,
				NotificationRequest.DELIMITER, "FLOW Raw Data Report",
				"Please see the latest raw data report", bos.toByteArray(),
				"rawDataReport.csv", "text/plain");
		pw.close();
	}

}
