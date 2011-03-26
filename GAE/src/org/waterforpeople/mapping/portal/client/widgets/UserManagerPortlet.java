package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.util.PermissionConstants;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.UserManagerWidget;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;

/**
 * Portlet for adding / removing users from the application.
 * 
 * @author Christopher Fagiani
 * 
 */
public class UserManagerPortlet extends UserAwarePortlet {
	
	private static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);

	public static final String NAME = TEXT_CONSTANTS.userManagerPortletTitle();
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;

	public UserManagerPortlet(UserDto user) {
		super(NAME, true, false, false, WIDTH, HEIGHT, user);

		if (user.hasPermission(PermissionConstants.EDIT_USER)) {
			UserManagerWidget managerWidget = new UserManagerWidget();
			setContent(managerWidget);
		} else {
			MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS.adminOnly(),TEXT_CONSTANTS.adminOnlyMessage());					
			errDia.show();
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

}
