package com.gallatinsystems.framework.rest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gallatinsystems.framework.rest.exception.RestException;

/**
 * Base class for any REST apis. It handles via a template method the following
 * actions: set the response type (based on the mode set via servlet init
 * params) read the request (delegating to subclasses) validating the request
 * writing the response (delegating to subclasses) or writing the error to the
 * response
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class AbstractRestApiServlet extends HttpServlet {
	private static Logger log = Logger.getLogger(AbstractRestApiServlet.class
			.getName());
	private static final long serialVersionUID = -8553345034709944772L;
	private static final String XML_MODE = "XML";
	private static final String JSON_MODE = "JSON";
	@SuppressWarnings("unused")
	private static final String PLAINTEXT_MODE = "TEXT";
	private String mode;
	private ThreadLocal<HttpServletRequest> requests;
	private ThreadLocal<HttpServletResponse> responses;

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		executeRequest(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		executeRequest(req, resp);
	}

	private void executeRequest(HttpServletRequest req, HttpServletResponse resp) {
		try {
			checkThreadLocal();
			requests.set(req);
			responses.set(resp);
			setContentType(resp);
			RestRequest restReq = convertRequest();
			restReq.validate();
			RestResponse restResp = handleRequest(restReq);
			writeOkResponse(restResp);
		} catch (RestException e) {
			writeErrorResponse(e.getError(), resp);
		} catch (Exception e) {
			// we get here if we get some unexpected exception that does not
			// derive from RestException
			writeErrorResponse(new RestError(), resp);
			log.log(Level.SEVERE,"Could not execute rest request",e);
		} finally {
			// null out the request/response objects so we don't leak memory
			requests.set(null);
			responses.set(null);
		}
	}

	/**
	 * converts a servlet request to a RestRequest
	 * 
	 * @return
	 * @throws Exception
	 */
	protected abstract RestRequest convertRequest() throws Exception;

	/**
	 * performs the api specific action. If the servlet can support multiple api
	 * methods, this method should look a the action field in the http request
	 * and delegate appropriately.
	 * 
	 * @param req
	 * @return
	 * @throws Exception
	 */
	protected abstract RestResponse handleRequest(RestRequest req)
			throws Exception;

	/**
	 * writes non-error response to the the output stream
	 * 
	 * @param restResponse
	 * @param resp
	 * @throws Exception
	 */
	protected abstract void writeOkResponse(RestResponse resp) throws Exception;

	/**
	 * sets the content type of the response based on the value in web.xml. If
	 * no value is specified, we assume plaintext
	 * 
	 * @param resp
	 */
	private void setContentType(HttpServletResponse resp) {
		if (XML_MODE.equalsIgnoreCase(mode)) {
			resp.setContentType("text/xml;charset=utf-8");
		} else if (JSON_MODE.equalsIgnoreCase(mode)) {
			resp.setContentType("application/javascript;charset=utf-8");
		} else {
			resp.setContentType("text/plain");
		}
	}

	/**
	 * writes the contents of the RestError to the response
	 * 
	 * @param err
	 * @param resp
	 */
	private void writeErrorResponse(RestError err, HttpServletResponse resp) {
		try {
			// TODO: error should honor content type (i.e. xml, json or text)
			resp.getWriter().print(err.toString());
		} catch (IOException e) {
			log.log(Level.SEVERE,"Could not write to servlet response object",e);
		}
	}

	/**
	 * gets the thread local request
	 * 
	 * @return
	 */
	protected HttpServletRequest getRequest() {
		return requests.get();
	}

	/**
	 * gets the thread local response
	 * 
	 * @return
	 */
	protected HttpServletResponse getResponse() {
		return responses.get();
	}

	/**
	 * initializes thread local objects if they're null
	 */
	private void checkThreadLocal() {
		if (requests == null) {
			requests = new ThreadLocal<HttpServletRequest>();
		}
		if (responses == null) {
			responses = new ThreadLocal<HttpServletResponse>();
		}
	}
}