package org.waterforpeople.mapping.app.gwt.client.user;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserServiceAsync {

	void listUser(AsyncCallback<UserDto[]> callback);

	void getCurrentUserConfig(AsyncCallback<UserDto> callback);

	void saveUser(UserDto user, AsyncCallback<Void> callback);

}
