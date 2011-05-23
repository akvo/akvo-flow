package com.gallatinsystems.messaging.app.gwt.server;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.messaging.app.gwt.client.MessageDto;
import com.gallatinsystems.messaging.app.gwt.client.MessageService;
import com.gallatinsystems.messaging.dao.MessageDao;
import com.gallatinsystems.messaging.domain.Message;

public class MessageServiceImpl implements MessageService {

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
