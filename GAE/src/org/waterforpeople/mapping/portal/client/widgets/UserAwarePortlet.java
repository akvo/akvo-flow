package org.waterforpeople.mapping.portal.client.widgets;

import java.util.Set;

import org.waterforpeople.mapping.app.gwt.client.user.UserConfigDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserService;
import org.waterforpeople.mapping.app.gwt.client.user.UserServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * base class for any portlets that need to load or store state to and from
 * UserConfig objects. On click of the config button, this class will persist
 * the portlet config to the datastore. Similarly, on load, it will iterate over
 * the config items in the userDTO passed in and find the one that pertains to
 * this portlet. To make this work, subclasses must implement the
 * getConfigItemName method such that it returns the unique name of the portlet
 * config object it is interested in.
 * 
 * @author Christophre Fagiani
 * 
 */
public abstract class UserAwarePortlet extends Portlet {

	private static final String PORTLET_CONFIG_GROUP = "PORTLET";
	private UserDto currentUser;
	private UserConfigDto portletConfig;
	private UserServiceAsync userService;

	/**
	 * instantiate the superclass and then find "our" config
	 * 
	 * @param title
	 * @param scrollable
	 * @param configurable
	 * @param width
	 * @param height
	 * @param user
	 */
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
							setConfig(portletConfig.getValue());
							break;
						}
					}
				}
			}
		}
		userService = GWT.create(UserService.class);
	}

	/**
	 * when about to remove the portlet, delete the config object
	 */
	@Override
	protected boolean getReadyForRemove() {
		if (portletConfig != null) {
			userService.deletePortletConfig(portletConfig, currentUser
					.getEmailAddress(), new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					// no-op
				}

				@Override
				public void onSuccess(Void result) {
					// no-op
				}
			});
		}
		return true;
	}

	/**
	 * method that returns the name of the configuration object for this
	 * portlet. Subclasses should override this method.
	 * 
	 * @return
	 */
	protected String getConfigItemName() {
		return null;
	}

	/**
	 * call the user service to update the configItem. If the currentUser is
	 * null, this will do nothing.
	 */
	protected void saveConfig() {
		if (currentUser != null && getConfigItemName() != null) {
			if (portletConfig == null) {
				portletConfig = new UserConfigDto();
			}
			portletConfig.setGroup(PORTLET_CONFIG_GROUP);
			portletConfig.setName(getConfigItemName());
			portletConfig.setValue(getConfig());
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

	/**
	 * persist the config to the database
	 */
	@Override
	protected void handleConfigClick() {
		if (currentUser != null) {
			saveConfig();
		}
	}
}
