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

package org.waterforpeople.mapping.app.gwt.client.auth;

import java.util.ArrayList;
import java.util.HashMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * service for interacting with web activity authorization objects
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("webactauthrpcservice")
public interface WebActivityAuthorizationService extends RemoteService {

	/**
	 * returns a WebActivityAuthorizationDto if the request is authorized for
	 * the given token/activityName combination. If the request is not
	 * authorized, null is returned.
	 * 
	 * @param token
	 * @param activityName
	 * @return
	 */
	public WebActivityAuthorizationDto isAuthorized(String token,
			String activityName);

	/**
	 * persists an authorization object (update if it exists).
	 * 
	 * @param authDto
	 * @return
	 */
	public WebActivityAuthorizationDto saveAuthorization(
			WebActivityAuthorizationDto authDto);

	/**
	 * deletes an authorization object
	 * 
	 * @param dto
	 */
	public void deleteAuthorization(WebActivityAuthorizationDto dto);

	/**
	 * lists all authorizations
	 * 
	 * @param cursor
	 * @return
	 */
	public ResponseDto<ArrayList<WebActivityAuthorizationDto>> listAuthorizations(
			String cursor);

	/**
	 * lists all authorizations keyed by the dto identified by the payload for a
	 * given user/activity
	 * 
	 * @param activityName
	 * @return
	 */
	public ResponseDto<HashMap<BaseDto, WebActivityAuthorizationDto>> listUserAuthorizations(
			String activityName);
}
