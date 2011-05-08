package com.gallatinsystems.notification;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * Encapsulates requests to the notification task queue
 * 
 * @author Christopher Fagiani
 * 
 */
public class NotificationRequest extends RestRequest {

	private static final long serialVersionUID = 5751114948240808962L;
	public static final String DELIMITER = "||";
	public static final String DEST_PARAM = "destinations";
	public static final String ENTITY_PARAM = "entityId";
	public static final String TYPE_PARAM = "type";
	public static final String METHOD_PARAM = "method";
	public static final String DEST_OPT_PARAM = "destOptions";

	private String destinations;
	private Long entityId;
	private String type;
	private String method;
	private String destOptions;

	public String getDestOptions() {
		return destOptions;
	}

	public void setDestOptions(String destOptions) {
		this.destOptions = destOptions;
	}

	public String getDestinations() {
		return destinations;
	}

	public void setDestinations(String destinations) {
		this.destinations = destinations;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	protected void populateErrors() {
		if (entityId == null) {
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, ENTITY_PARAM
							+ " is mandatory"));
		} else if (type == null || type.length() == 0) {
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, TYPE_PARAM
							+ " is mandatory"));
		}
	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if (req.getParameter(ENTITY_PARAM) != null) {
			try {
				entityId = Long.parseLong(req.getParameter(ENTITY_PARAM));
			} catch (NumberFormatException e) {
				// no-op
			}
		}
		if (req.getParameter(TYPE_PARAM) != null) {
			type = req.getParameter(TYPE_PARAM).trim();
		}
		destinations = req.getParameter(DEST_PARAM);
		destOptions = req.getParameter(DEST_OPT_PARAM);
		method = req.getParameter(METHOD_PARAM);

	}
}
