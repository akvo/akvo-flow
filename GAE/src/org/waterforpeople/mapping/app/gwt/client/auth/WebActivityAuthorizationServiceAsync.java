package org.waterforpeople.mapping.app.gwt.client.auth;

import java.util.ArrayList;
import java.util.HashMap;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WebActivityAuthorizationServiceAsync {

	void deleteAuthorization(WebActivityAuthorizationDto dto,
			AsyncCallback<Void> callback);

	void isAuthorized(String token, String activityName,
			AsyncCallback<WebActivityAuthorizationDto> callback);

	void listAuthorizations(String cursor,
			AsyncCallback<ResponseDto<ArrayList<WebActivityAuthorizationDto>>> callback);

	void saveAuthorization(WebActivityAuthorizationDto authDto,
			AsyncCallback<WebActivityAuthorizationDto> callback);

	void listUserAuthorizations(
			String activityName,
			AsyncCallback<ResponseDto<HashMap<BaseDto, WebActivityAuthorizationDto>>> callback);

}
