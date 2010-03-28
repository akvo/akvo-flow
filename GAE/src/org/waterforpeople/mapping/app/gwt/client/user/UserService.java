package org.waterforpeople.mapping.app.gwt.client.user;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userrpcservice")
public interface UserService extends RemoteService{

	public UserDto[] listUser();

	public UserConfigDto getCurrentUserConfig();
}
