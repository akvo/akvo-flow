package org.waterforpeople.mapping.portal.client.widgets;

import java.util.Set;

import org.waterforpeople.mapping.app.gwt.client.user.UserConfigDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserService;
import org.waterforpeople.mapping.app.gwt.client.user.UserServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class UserAwarePortlet extends Portlet {

	private static final String PORTLET_CONFIG_GROUP = "PORTLET";
	private UserDto currentUser;
	private UserConfigDto portletConfig;
	private UserServiceAsync userService;

	public UserAwarePortlet(String title, boolean scrollable,
			boolean configurable, int width, int height, UserDto user) {
		super(title, scrollable, configurable, width, height);
		if (user != null) {
			currentUser = user;
			if (user.getConfig() != null) {
				Set<UserConfigDto> dtoList = user.getConfig().get(
						PORTLET_CONFIG_GROUP);
				if (dtoList != null) {
					for (UserConfigDto dto : dtoList) {
						if (dto.getName().equals(getConfigItemName())) {
							portletConfig = dto;
							break;
						}
					}
				}
			}
		}
		userService = GWT.create(UserService.class);
	}

	/**
	 * method that reutnrs the name of the configuration object for this
	 * portlet. Subclasses should override this method.
	 * 
	 * @return
	 */
	protected String getConfigItemName() {
		return null;
	}

	protected void saveConfig(String value) {
		if (currentUser != null && getConfigItemName() != null) {
			if (portletConfig == null) {
				portletConfig = new UserConfigDto();
			}
			portletConfig.setGroup(PORTLET_CONFIG_GROUP);
			portletConfig.setName(getConfigItemName());
			portletConfig.setValue(value);
			userService.updateUserConfigItem(currentUser.getEmailAddress(),
					PORTLET_CONFIG_GROUP, portletConfig,
					new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							// no-op

						}

						@Override
						public void onFailure(Throwable caught) {
							// no-op

						}
					});
		}
	}
}
