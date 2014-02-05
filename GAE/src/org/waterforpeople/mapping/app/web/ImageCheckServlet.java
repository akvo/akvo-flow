/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class ImageCheckServlet extends AbstractRestApiServlet {

	private static final long serialVersionUID = 9187987692591327059L;

	@Override
	protected RestRequest convertRequest() throws Exception {
		return new RestRequest() {

			private static final long serialVersionUID = 5774835861536685383L;

			@Override
			protected void populateFields(HttpServletRequest req)
					throws Exception {

			}

			@Override
			protected void populateErrors() {

			}
		};
	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		return new RestResponse();
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
	}

}
