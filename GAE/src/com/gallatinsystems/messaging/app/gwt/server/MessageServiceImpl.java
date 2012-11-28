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

package com.gallatinsystems.messaging.app.gwt.server;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.messaging.app.gwt.client.MessageDto;
import com.gallatinsystems.messaging.app.gwt.client.MessageService;
import com.gallatinsystems.messaging.dao.MessageDao;
import com.gallatinsystems.messaging.domain.Message;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class MessageServiceImpl extends RemoteServiceServlet implements MessageService {
	private static final long serialVersionUID = 2231511038552768648L;
	private MessageDao messageDao;

	public MessageServiceImpl() {
		messageDao = new MessageDao();
	}

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
			Long id, String cursor) {
		List<Message> messages = messageDao.listBySubject(actionType, id,
				cursor);
		ResponseDto<ArrayList<MessageDto>> response = new ResponseDto<ArrayList<MessageDto>>();
		ArrayList<MessageDto> dtoList = new ArrayList<MessageDto>();
		if (messages != null) {
			for (Message m : messages) {
				MessageDto dto = new MessageDto();
				DtoMarshaller.copyToDto(m, dto);
				dtoList.add(dto);
			}
			response.setCursorString(MessageDao.getCursor(messages));
			response.setPayload(dtoList);
		}
		return response;
	}

	/**
	 * deletes a message identified by the key passed in.
	 * 
	 * @param keyId
	 */
	public void deleteMessage(Long keyId) {
		Message m = messageDao.getByKey(keyId);
		if (m != null) {
			messageDao.delete(m);
		}
	}
}
