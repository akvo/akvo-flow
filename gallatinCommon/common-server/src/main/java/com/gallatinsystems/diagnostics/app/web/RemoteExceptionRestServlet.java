package com.gallatinsystems.diagnostics.app.web;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.diagnostics.app.web.dto.RemoteExceptionRequest;
import com.gallatinsystems.diagnostics.dao.RemoteStacktraceDao;
import com.gallatinsystems.diagnostics.domain.RemoteStacktrace;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.framework.rest.exception.RestException;
import com.google.appengine.api.datastore.Text;

/**
 * servlet for saving stack traces posted by the devices
 * 
 * @author Christopher Fagiani
 * 
 */
public class RemoteExceptionRestServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = 1831040260541847041L;
	private RemoteStacktraceDao stacktraceDao;

	public RemoteExceptionRestServlet() {
		stacktraceDao = new RemoteStacktraceDao();
	}

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest exReq = new RemoteExceptionRequest();
		exReq.populateFromHttpRequest(req);
		return exReq;
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		RestResponse resp = new RestResponse();
		RemoteExceptionRequest exReq = (RemoteExceptionRequest) req;
		if (RemoteExceptionRequest.SAVE_TRACE_ACTION.equals(req.getAction())) {
			RemoteStacktrace trace = new RemoteStacktrace();
			trace.setErrorDate(exReq.getDate());
			trace.setSoftwareVersion(exReq.getVersion());
			trace.setDeviceIdentifier(exReq.getDeviceIdent());
			trace.setPhoneNumber(exReq.getPhoneNumber());
			trace.setStackTrace(new Text(exReq.getStackTrace()));
			stacktraceDao.save(trace);
		} else {
			throw new RestException(new RestError(RestError.BAD_DATATYPE_CODE,
					RestError.BAD_DATATYPE_MESSAGE, "Action: "
							+ req.getAction() + " not supported"),
					"Bad Action value", null);
		}
		return resp;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
	}

}
