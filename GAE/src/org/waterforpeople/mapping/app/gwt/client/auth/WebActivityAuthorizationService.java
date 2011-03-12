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
