/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.web;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.waterforpeople.mapping.app.gwt.server.spreadsheetmapper.SpreadsheetMappingAttributeServiceImpl;
import org.waterforpeople.mapping.app.web.dto.SpreadsheetImportRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;



public class SpreadsheetImportServlet extends AbstractRestApiServlet {
	private static final Logger log = Logger
	.getLogger(SpreadsheetImportServlet.class.getName());
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
			
			SpreadsheetMappingAttributeServiceImpl mappingService = new SpreadsheetMappingAttributeServiceImpl();	
			String algorithm=importReq.getKeySpec();
			
			KeyFactory keyFactory = java.security.KeyFactory.getInstance(algorithm);
			org.apache.commons.codec.binary.Base64 b64encoder = new org.apache.commons.codec.binary.Base64();
			byte[] keyContents = b64encoder.decode(importReq.getKey());
			EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyContents);
			log.info("SessionToken: " + importReq.getSessionToken()+"Algo: " + algorithm + " key: " + importReq.getKey() + " keySpec: " + importReq.getKeySpec());
			PrivateKey key = keyFactory.generatePrivate(privateKeySpec);
			
			mappingService.processSurveySpreadsheetAsync(importReq.getSessionToken(),key,importReq.getIdentifier(),importReq.getStartRow(), importReq.getGroupId());
		}
		return response;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		// no-op
	}

}
