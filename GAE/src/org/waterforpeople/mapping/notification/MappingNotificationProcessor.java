package org.waterforpeople.mapping.notification;

import java.util.HashMap;

import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.notification.NotificationProcessor;

/**
 * handles notifications specific to surveys
 * 
 * @author Christopher Fagiani
 * 
 */
public class MappingNotificationProcessor extends NotificationProcessor {

	private static final long serialVersionUID = -9055657567284631932L;

	

	@Override
	protected void initializeTypeMapping() {
		notificationTypeMap = new HashMap<String,String>();
		notificationTypeMap.put(RawDataReportNotificationHandler.TYPE, RawDataReportNotificationHandler.class.getCanonicalName());

	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// no-op

	}
}
