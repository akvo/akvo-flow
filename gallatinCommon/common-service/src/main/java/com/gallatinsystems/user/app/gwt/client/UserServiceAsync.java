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
