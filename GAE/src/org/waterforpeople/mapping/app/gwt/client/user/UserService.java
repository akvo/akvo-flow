package org.waterforpeople.mapping.app.gwt.client.user;

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
	public UserDto getCurrentUserConfig();

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
}
