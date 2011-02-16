package org.waterforpeople.mapping.notification;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

import org.waterforpeople.mapping.dataexport.RawDataExporter;

import com.gallatinsystems.common.util.MD5Util;
import com.gallatinsystems.common.util.MailUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.notification.NotificationHandler;
import com.gallatinsystems.notification.NotificationRequest;
import com.gallatinsystems.notification.dao.NotificationSubscriptionDao;
import com.gallatinsystems.notification.domain.NotificationHistory;

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
		NotificationHistory hist = getHistory(TYPE, entityId);
		String newChecksum = MD5Util.generateChecksum(bos.toByteArray());
		if (hist.getChecksum() == null
				|| !hist.getChecksum().equals(newChecksum)) {
			hist.setChecksum(newChecksum);
			MailUtil.sendMail(FROM_ADDRESS, destinations,
					NotificationRequest.DELIMITER, "FLOW Raw Data Report",
					"Please see the latest raw data report", bos.toByteArray(),
					"rawDataReport.txt", "text/plain");
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
