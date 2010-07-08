package org.waterforpeople.mapping.app.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.waterforpeople.mapping.app.gwt.server.spreadsheetmapper.SpreadsheetMappingAttributeServiceImpl;
import org.waterforpeople.mapping.app.web.dto.SpreadsheetImportRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class SpreadsheetImportServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = 4037072154702352658L;

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new SpreadsheetImportRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;
	}

	@Override
	protected RestResponse handleRequest(RestRequest request) throws Exception {
		RestResponse response = new RestResponse();
		SpreadsheetImportRequest importReq = (SpreadsheetImportRequest) request;
		if (SpreadsheetImportRequest.PROCESS_FILE_ACTION
				.equalsIgnoreCase(importReq.getAction())) {
			HttpSession session = super.getRequest().getSession(true);
			
			session.putValue("sessionToken", importReq.getSessionToken());
			SpreadsheetMappingAttributeServiceImpl mappingService = new SpreadsheetMappingAttributeServiceImpl();	
			
			mappingService.processSurveySpreadsheet(importReq.getIdentifier(),importReq.getStartRow(), importReq.getGroupId());
		}
		return response;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// no-op
	}

}
