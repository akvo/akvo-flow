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

package com.gallatinsystems.user.app.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * service for getting user info from the server
 * @author Christopher Fagiani
 *
 */
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
	
	/**
	 * lists all permissions
	 * @return
	 */
	public List<PermissionDto> listPermissions();
}
