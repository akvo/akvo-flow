package com.gallatinsystems.messaging.app.gwt.client;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MessageServiceAsync {

	void listMessages(String actionType, Long id, String cursor,
			AsyncCallback<ResponseDto<ArrayList<MessageDto>>> callback);

	void deleteMessage(Long keyId, AsyncCallback<Void> callback);

}
