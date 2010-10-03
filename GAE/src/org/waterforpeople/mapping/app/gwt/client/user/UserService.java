package org.waterforpeople.mapping.app.gwt.client.user;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userrpcservice")
public interface UserService extends RemoteService {

	public UserDto[] listUser();

	/**
	 * gets the currently logged in user (using the session to determine who is
	 * logged in)
	 * 
	 * @return
	 */
	public UserDto getCurrentUserConfig(boolean createIfNotFound);

	/**
	 * persists a user and it's dependent UserConfig items
	 * 
	 * @param user
	 */
	public void saveUser(UserDto user);

	/**
	 * updates a single userConfig object to an existing user. both the user and
	 * the config object must exist to use this method
	 * 
	 * @param emailAddress
	 * @param configGroup
	 * @param confDto
	 */
	public void updateUserConfigItem(String emailAddress, String configGroup,
			UserConfigDto confDto);

	/**
	 * finds a single userConfig item by name
	 * 
	 * @param emailAddress
	 * @param configGroup
	 * @param configName
	 * @return
	 */
	public UserConfigDto findUserConfigItem(String emailAddress,
			String configGroup, String configName);

	/**
	 * deletes a config from the datastore
	 * 
	 * @param dto
	 * @param emailAddress
	 */
	public void deletePortletConfig(UserConfigDto dto, String emailAddress);

	/**
	 * searches for users
	 * 
	 * @param userName
	 * @param emailAddress
	 * @param sortBy
	 * @param sortDir
	 * @param cursor
	 * @return
	 */
	public ResponseDto<ArrayList<UserDto>> listUsers(String userName,
			String emailAddress, String sortBy, String sortDir, String cursor);

	/**
	 * deletes the user with the given id
	 * 
	 * @param id
	 */
	public void deleteUser(Long id);
}
