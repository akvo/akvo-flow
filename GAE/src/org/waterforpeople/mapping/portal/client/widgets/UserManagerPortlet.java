package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.user.UserDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserService;
import org.waterforpeople.mapping.app.gwt.client.user.UserServiceAsync;
import org.waterforpeople.mapping.portal.client.widgets.component.DataTableBinder;
import org.waterforpeople.mapping.portal.client.widgets.component.DataTableListener;
import org.waterforpeople.mapping.portal.client.widgets.component.PaginatedDataTable;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
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
public class UserManagerPortlet extends Portlet implements
		DataTableListener<UserDto>, DataTableBinder<UserDto>, ClickHandler {

	public static final String NAME = "User Management";
	private static final String DEFAULT_SORT_FIELD = "username";
	private static final String[] GRID_HEADERS = { "User Name",
			"Email Address", "" };
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;

	private VerticalPanel contentPane;
	private PaginatedDataTable<UserDto> dataTable;
	private TextBox usernameField;
	private TextBox emailField;
	private Button searchButton;
	private Button addNewButton;
	private UserServiceAsync userService;

	public UserManagerPortlet() {
		super(NAME, true, false, WIDTH, HEIGHT);

		contentPane = new VerticalPanel();
		contentPane.add(buildSearchHeader());
		dataTable = new PaginatedDataTable<UserDto>(DEFAULT_SORT_FIELD, this,
				this);
		contentPane.add(dataTable);
		setContent(contentPane);

		userService = GWT.create(UserService.class);
		requestData(null);
	}

	private Widget buildSearchHeader() {
		HorizontalPanel hPanel = new HorizontalPanel();
		Label l = new Label("Username: ");
		hPanel.add(l);
		usernameField = new TextBox();
		hPanel.add(usernameField);
		l = new Label("Email: ");
		emailField = new TextBox();
		hPanel.add(emailField);
		searchButton = new Button("Search");
		searchButton.addClickHandler(this);
		hPanel.add(searchButton);
		addNewButton = new Button("Add User");
		addNewButton.addClickHandler(this);
		hPanel.add(addNewButton);

		return hPanel;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void onItemSelected(UserDto item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestData(String cursor) {
		final boolean isNew = (cursor == null);
		userService.listUsers(null, null, null, null, cursor,
				new AsyncCallback<ResponseDto<ArrayList<UserDto>>>() {

					@Override
					public void onSuccess(ResponseDto<ArrayList<UserDto>> result) {
						dataTable.bindData(result.getPayload(), result
								.getCursorString(), isNew);
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}
				});
	}

	@Override
	public void resort(String field, String direction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindRow(Grid grid, final UserDto item, int row) {
		final TextBox uBox = new TextBox();
		uBox.setText(item.getUserName());
		grid.setWidget(row, 0, uBox);
		final TextBox eBox = new TextBox();
		eBox.setText(item.getEmailAddress());
		grid.setWidget(row, 1, eBox);
		HorizontalPanel buttonPanel = new HorizontalPanel();
		Button saveButton = new Button("Save");
		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				item.setUserName(uBox.getText());
				item.setEmailAddress(eBox.getText());
				userService.saveUser(item, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Void result) {
						// TODO Auto-generated method stub

					}
				});
			}
		});
		buttonPanel.add(saveButton);
		Button deleteButton = new Button("Delete");
		deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				item.setUserName(uBox.getText());
				item.setEmailAddress(eBox.getText());
				userService.deleteUser(item.getKeyId(),
						new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								MessageDialog errDia = new MessageDialog(
										"Error",
										"There was an error while attempting to delete the user: "
												+ caught.getMessage());
								errDia.show();

							}

							@Override
							public void onSuccess(Void result) {
								MessageDialog confDia = new MessageDialog(
										"User Deleted", "User has been deleted");
								confDia.show();
								requestData(null);
							}
						});
			}
		});
		buttonPanel.add(deleteButton);
		grid.setWidget(row, 2, buttonPanel);
	}

	@Override
	public String[] getHeaders() {
		return GRID_HEADERS;
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == addNewButton) {
			String userName = usernameField.getText().trim();
			String email = emailField.getText().trim();
			if (userName.length() == 0 || email.length() == 0) {
				MessageDialog errDia = new MessageDialog(
						"Missing Mandatory Data",
						"Please enter both email address and username.");
				errDia.showRelativeTo(addNewButton);
			} else {
				UserDto u = new UserDto();
				u.setUserName(userName);
				u.setEmailAddress(email);
				userService.saveUser(u, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errorDia = new MessageDialog(
								"Could not save user",
								"There was an error while trying to save the user: "
										+ caught.getMessage());
						errorDia.showRelativeTo(addNewButton);
					}

					@Override
					public void onSuccess(Void result) {
						MessageDialog confDia = new MessageDialog("User Saved",
								"User has been saved");
						confDia.showRelativeTo(addNewButton);
						usernameField.setText("");
						emailField.setText("");
						requestData(null);
					}
				});
			}

		} else if (event.getSource() == searchButton) {

		}

	}

}
