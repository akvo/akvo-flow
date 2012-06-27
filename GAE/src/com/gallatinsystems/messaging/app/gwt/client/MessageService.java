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

package com.gallatinsystems.messaging.app.gwt.client;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * service for manipulation of Message objects
 * @author Christopher Fagiani
 *
 */
@RemoteServiceRelativePath("messagerpcservice")
public interface MessageService extends RemoteService {

	/**
	 * lists MessageDtos in a paginated fashion. The list may be filtered by
	 * supplying either actionType, objectId or both. The list will be ordered
	 * by update date in a descending fashion.
	 * 
	 * @param actionType
	 * @param id
	 * @param cursor
	 * @return
	 */
	public ResponseDto<ArrayList<MessageDto>> listMessages(String actionType,
			Long id, String cursor);

	/**
	 * deletes a message identified by the key passed in.
	 * 
	 * @param keyId
	 */
	public void deleteMessage(Long keyId);
}
