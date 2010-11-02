package org.waterforpeople.mapping.app.web.dto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

/**
 * represents the data sent over by the an SMS gateway
 * 
 * @author Christopher Fagiani
 * 
 */
public class SMSRestRequest extends RestRequest {

	private static final long serialVersionUID = -4090095229806070007L;
	private static final String API_ID_PARAM = "Api_id";
	private static final String FROM_PARAM = "from";
	private static final String TO_PARAM = "to";
	private static final String TIME_PARAM = "timestamp";
	private static final String CHARSET_PARAM = "charset";
	private static final String TEXT_PARAM = "text";

	private static final DateFormat DATE_FMT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private String apiId;
	private String from;
	private String to;
	private Date timestamp;
	private String text;
	private String charset;

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getApiId() {
		return apiId;
	}

	public void setApiId(String apiId) {
		this.apiId = apiId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	protected void populateErrors() {
		if (text == null) {
			addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
					RestError.MISSING_PARAM_ERROR_MESSAGE, TEXT_PARAM));
		}

	}

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		text = req.getParameter(TEXT_PARAM);
		if (text != null) {
			text = text.trim();
		}
		from = req.getParameter(FROM_PARAM);
		to = req.getParameter(TO_PARAM);
		String dateString = req.getParameter(TIME_PARAM);
		if (dateString != null) {
			try {
				dateString = DATE_FMT.format(dateString);
			} catch (Exception e) {
				addError(new RestError(RestError.BAD_DATATYPE_CODE,
						RestError.BAD_DATATYPE_MESSAGE,
						"Date format: yyyy-MM-dd HH:mm:ss"));
			}
		}
		charset = req.getParameter(CHARSET_PARAM);
		apiId = req.getParameter(API_ID_PARAM);
	}

}
