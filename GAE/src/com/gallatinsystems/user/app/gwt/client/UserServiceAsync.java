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
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserServiceAsync {

	void listUser(AsyncCallback<UserDto[]> callback);

	void getCurrentUserConfig(boolean createIfNotFound, AsyncCallback<UserDto> callback);

	void saveUser(UserDto user, AsyncCallback<Void> callback);

	void updateUserConfigItem(String emailAddress, String configGroup,
			UserConfigDto confDto, AsyncCallback<Void> callback);

	void findUserConfigItem(String emailAddress, String configGroup,
			String configName, AsyncCallback<UserConfigDto> callback);

	void deletePortletConfig(UserConfigDto dto, String emailAddress,
			AsyncCallback<Void> callback);

	void listUsers(String userName, String emailAddress, String sortBy,
			String sortDir, String cursor,
			AsyncCallback<ResponseDto<ArrayList<UserDto>>> callback);

	void deleteUser(Long id, AsyncCallback<Void> callback);

	void listPermissions(AsyncCallback<List<PermissionDto>> callback);

}
