package org.waterforpeople.mapping.app.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.app.web.dto.MappingSummarizationRequest;
import org.waterforpeople.mapping.app.web.dto.MappingSummarizationResponse;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.helper.MappingSummarizationHelper;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

/**
 * Servlet that will process summarization API requests
 * 
 * @author Christopher Fagiani
 * 
 */
public class MappingSummarizationServlet extends AbstractRestApiServlet {
	private MappingSummarizationHelper helper;

	public MappingSummarizationServlet() {
		helper = new MappingSummarizationHelper();
	}

	/**
	 * converts the incoming servlet request into a MappingSummarizationRequest
	 */
	@Override
	protected RestRequest convertRequest(HttpServletRequest req)
			throws Exception {
		String action = req.getParameter(RestRequest.ACTION_PARAM);
		RestRequest restRequest = new MappingSummarizationRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	/**
	 * looks at the action in the request object and dispatches handling to the
	 * appropriate method.
	 */
	@Override
	protected RestResponse handleRequest(RestRequest request) throws Exception {
		MappingSummarizationResponse response = new MappingSummarizationResponse();
		MappingSummarizationRequest summReq = (MappingSummarizationRequest) request;
		String result = helper.processSummarization(summReq.getRegionUUID(),
				summReq.getSummarizationType());
		response.setColorCode(result);
		return response;
	}

	/**
	 * write the response to the servlet response object
	 */
	@Override
	protected void writeOkResponse(RestResponse restResponse,
			HttpServletResponse resp) throws Exception {
		// for now, just output the value
		String val = ((MappingSummarizationResponse) restResponse)
				.getColorCode();
		if (val != null) {
			resp.getWriter().print("The status color is: " + val);
		} else {
			resp.getWriter().print("NO DATA");
		}
	}
}
