package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.portal.client.widgets.component.UserManagerWidget;

import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.gallatinsystems.user.app.gwt.client.UserServiceAsync;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Portlet for adding / removing users from the application.
 * 
 * @author Christopher Fagiani
 * 
 */
public class UserManagerPortlet extends UserAwarePortlet {

	public static final String NAME = "User Management";
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;	

	public UserManagerPortlet(UserDto user) {
		super(NAME, true, false, false, WIDTH, HEIGHT, user);

		
		if (user.isAdmin()) {
			UserManagerWidget managerWidget = new UserManagerWidget();		
			setContent(managerWidget);
		} else {
			MessageDialog errDia = new MessageDialog("Admin Only",
					"You must be an administrator to access this feature.");
			errDia.show();
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

}
