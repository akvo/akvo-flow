package com.gallatinsystems.messaging.app.gwt.client;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

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
}
