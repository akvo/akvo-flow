package com.gallatinsystems.notification;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * 
 * Receives task queue work items and calls the appropriate handler based on the
 * type (implemented in sub classes)
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class NotificationProcessor extends AbstractRestApiServlet {
	private static final long serialVersionUID = -4726303161585277346L;

	protected Map<String, String> notificationTypeMap;

	public NotificationProcessor() {
		super();
		notificationTypeMap = new HashMap<String, String>();
		initializeTypeMapping();
	}

	protected abstract void initializeTypeMapping();

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		NotificationRequest restRequest = new NotificationRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	/**
	 * looks up the appropriate notification handler in the type map (using the
	 * Type passed in on the request) and invokes it
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected RestResponse handleRequest(RestRequest request) throws Exception {
		RestResponse response = new RestResponse();
		NotificationRequest notificationRequest = (NotificationRequest) request;
		String className = notificationTypeMap.get(notificationRequest
				.getType());
		if (className != null) {
			Class cls = Class.forName(className);
			NotificationHandler handler = (NotificationHandler) cls
					.newInstance();
			HttpServletRequest req = getRequest();
			String serverBase = req.getScheme()
					+ "://"
					+ req.getServerName()
					+ ((req.getLocalPort() != 80 && req.getLocalPort() != 443 && req.getLocalPort() != 0) ? ":"
							+ req.getLocalPort()
							: "");
			handler.generateNotification(notificationRequest.getEntityId(),
					notificationRequest.getDestinations(), notificationRequest.getDestOptions(),serverBase);
		} else {
			log("Could not find notification handler for type: "
					+ notificationRequest.getType());
		}
		return response;
	}
}
