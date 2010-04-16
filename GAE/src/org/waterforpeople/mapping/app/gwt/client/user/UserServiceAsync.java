package org.waterforpeople.mapping.app.gwt.client.user;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserServiceAsync {

	void listUser(AsyncCallback<UserDto[]> callback);

	void getCurrentUserConfig(AsyncCallback<UserDto> callback);

	void saveUser(UserDto user, AsyncCallback<Void> callback);

	void updateUserConfigItem(String emailAddress, String configGroup,
			UserConfigDto confDto, AsyncCallback<Void> callback);

	void findUserConfigItem(String emailAddress, String configGroup,
			String configName, AsyncCallback<UserConfigDto> callback);

	void deletePortletConfig(UserConfigDto dto, String emailAddress,
			AsyncCallback<Void> callback);

}
