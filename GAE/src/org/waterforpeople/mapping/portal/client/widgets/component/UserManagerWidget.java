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

package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.user.app.gwt.client.PermissionDto;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.gallatinsystems.user.app.gwt.client.UserService;
import com.gallatinsystems.user.app.gwt.client.UserServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget used for creating/editing/searching users.
 * 
 * @author Christopher Fagiani
 * 
 */
public class UserManagerWidget extends Composite implements
		DataTableListener<UserDto>, DataTableBinder<UserDto>, ClickHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final String DEFAULT_SORT_FIELD = "username";
	private static final DataTableHeader[] GRID_HEADERS = {
			new DataTableHeader(TEXT_CONSTANTS.userName()),
			new DataTableHeader(TEXT_CONSTANTS.emailAddress()),
			new DataTableHeader(TEXT_CONSTANTS.permissions()),
			new DataTableHeader("") };
	private static final DataTableHeader[] SELECTOR_GRID_HEADERS = {
			new DataTableHeader(TEXT_CONSTANTS.userName()),
			new DataTableHeader(TEXT_CONSTANTS.emailAddress()),
			new DataTableHeader("") };
	private static final Integer PAGE_SIZE = 20;
	private VerticalPanel contentPane;
	private PaginatedDataTable<UserDto> dataTable;
	private TextBox usernameField;
	private TextBox emailField;
	private Button searchButton;
	private Button addNewButton;
	private UserServiceAsync userService;
	private List<PermissionDto> permissionList;
	private boolean isSelector;
	private CompletionListener listener;

	public UserManagerWidget() {
		this(false);
	}

	public UserManagerWidget(boolean isSelector) {
		this.isSelector = isSelector;
		contentPane = new VerticalPanel();
		contentPane.add(buildSearchHeader());
		dataTable = new PaginatedDataTable<UserDto>(DEFAULT_SORT_FIELD, this,
				this, false);
		contentPane.add(dataTable);
		initWidget(contentPane);

		userService = GWT.create(UserService.class);
		userService.listPermissions(new AsyncCallback<List<PermissionDto>>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.error(), TEXT_CONSTANTS
								.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
				errDia.showCentered();
			}

			@Override
			public void onSuccess(List<PermissionDto> result) {
				permissionList = result;
				requestData(null, false);
			}
		});
	}

	/**
	 * builds the controls at the top of the portlet used for searching
	 * 
	 * @return
	 */
	private Widget buildSearchHeader() {
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.userName()));
		usernameField = new TextBox();
		hPanel.add(usernameField);
		hPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.emailAddress()));
		emailField = new TextBox();
		hPanel.add(emailField);
		searchButton = new Button(TEXT_CONSTANTS.search());
		searchButton.addClickHandler(this);
		hPanel.add(searchButton);
		addNewButton = new Button(TEXT_CONSTANTS.addUser());
		addNewButton.addClickHandler(this);
		hPanel.add(addNewButton);

		return hPanel;
	}

	@Override
	public void onItemSelected(UserDto item) {
		// no-op

	}

	/**
	 * call the server to get more data
	 */
	@Override
	public void requestData(String cursor, final boolean isResort) {
		final boolean isNew = (cursor == null);
		userService.listUsers(null, null, null, null, cursor,
				new AsyncCallback<ResponseDto<ArrayList<UserDto>>>() {

					@Override
					public void onSuccess(ResponseDto<ArrayList<UserDto>> result) {
						dataTable.bindData(result.getPayload(), result
								.getCursorString(), isNew, isResort);
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						errDia.show();
					}
				});
	}

	/**
	 * installs the data into the row on the data grid
	 */
	@Override
	public void bindRow(Grid grid, final UserDto item, int row) {
		final TextBox uBox = new TextBox();
		uBox.setText(item.getUserName());
		uBox.setReadOnly(isSelector);
		grid.setWidget(row, 0, uBox);
		final TextBox eBox = new TextBox();
		eBox.setReadOnly(isSelector);
		eBox.setText(item.getEmailAddress());
		grid.setWidget(row, 1, eBox);

		if (!isSelector) {
			final ListBox permBox = constructPermissionBox(item);
			grid.setWidget(row, 2, permBox);

			HorizontalPanel buttonPanel = new HorizontalPanel();
			Button saveButton = new Button(TEXT_CONSTANTS.save());
			saveButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					item.setUserName(uBox.getText());
					item.setEmailAddress(eBox.getText());
					item.setPermissionList(formPermissionString(permBox));
					userService.saveUser(item, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							MessageDialog errDia = new MessageDialog(
									TEXT_CONSTANTS.error(), TEXT_CONSTANTS
											.errorTracePrefix()
											+ " "
											+ caught.getLocalizedMessage());
							errDia.showCentered();
						}

						@Override
						public void onSuccess(Void result) {
							MessageDialog confDia = new MessageDialog(
									TEXT_CONSTANTS.saveComplete(),
									TEXT_CONSTANTS.userUpdated());
							confDia.showCentered();

						}
					});
				}
			});
			buttonPanel.add(saveButton);
			Button deleteButton = new Button(TEXT_CONSTANTS.delete());
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
											TEXT_CONSTANTS.error(),
											TEXT_CONSTANTS.errorTracePrefix()
													+ " "
													+ caught
															.getLocalizedMessage());
									errDia.showCentered();
								}

								@Override
								public void onSuccess(Void result) {
									MessageDialog confDia = new MessageDialog(
											TEXT_CONSTANTS.deleteComplete(),
											TEXT_CONSTANTS.userDeleted());
									confDia.showCentered();
									requestData(null, false);
								}
							});
				}
			});
			buttonPanel.add(deleteButton);
			grid.setWidget(row, 3, buttonPanel);
		} else {
			Button selectButton = new Button(TEXT_CONSTANTS.select());
			selectButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Map<String, Object> payload = new HashMap<String, Object>();
					payload.put(BundleConstants.USER, item);
					if (listener != null) {
						listener.operationComplete(true, payload);
					}
				}
			});
			grid.setWidget(row, 2, selectButton);
		}

	}

	private ListBox constructPermissionBox(UserDto u) {
		ListBox lb = new ListBox(true);
		if (permissionList != null) {
			for (int i = 0; i < permissionList.size(); i++) {
				lb.addItem(permissionList.get(i).getName(), permissionList.get(
						i).getCode());
				if (u != null
						&& u.hasPermission(permissionList.get(i).getCode())) {
					lb.setItemSelected(i, true);
				}
			}
		}
		return lb;
	}

	/**
	 * forms a comma delimited string of permission codes using what is selected
	 * in the box
	 * 
	 * @param box
	 * @return
	 */
	private String formPermissionString(ListBox box) {
		StringBuilder buf = new StringBuilder();
		if (box != null) {
			int count = 0;
			for (int i = 0; i < box.getItemCount(); i++) {
				if (box.isItemSelected(i)) {
					if (count > 0) {
						buf.append(",");
					}
					buf.append(box.getValue(i));
					count++;
				}
			}
		}
		return buf.toString();
	}

	@Override
	public DataTableHeader[] getHeaders() {
		if (isSelector) {
			return SELECTOR_GRID_HEADERS;
		} else {
			return GRID_HEADERS;
		}
	}

	/**
	 * handles the search and add new methods
	 */
	@Override
	public void onClick(ClickEvent event) {
		String userName = usernameField.getText().trim();
		String email = emailField.getText().trim();
		if (event.getSource() == addNewButton) {
			if (userName.length() == 0 || email.length() == 0) {
				MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
						.inputError(), TEXT_CONSTANTS.emailUsernameMandatory());
				errDia.showCentered();
			} else {
				UserDto u = new UserDto();
				u.setUserName(userName);
				u.setEmailAddress(email);
				userService.saveUser(u, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errorDia = new MessageDialog(
								TEXT_CONSTANTS.error(), TEXT_CONSTANTS
										.errorTracePrefix()
										+ " " + caught.getLocalizedMessage());
						errorDia.showCentered();
					}

					@Override
					public void onSuccess(Void result) {
						MessageDialog confDia = new MessageDialog(
								TEXT_CONSTANTS.saveComplete(), TEXT_CONSTANTS
										.saveComplete());
						confDia.showCentered();
						usernameField.setText("");
						emailField.setText("");
						requestData(null, false);
					}
				});
			}

		} else if (event.getSource() == searchButton) {
			userService.listUsers(userName.length() > 0 ? userName : null,
					email.length() > 0 ? email : null, null, null, null,
					new AsyncCallback<ResponseDto<ArrayList<UserDto>>>() {

						@Override
						public void onFailure(Throwable caught) {
							MessageDialog errorDia = new MessageDialog(
									TEXT_CONSTANTS.error(), TEXT_CONSTANTS
											.errorTracePrefix()
											+ " "
											+ caught.getLocalizedMessage());
							errorDia.showCentered();
						}

						@Override
						public void onSuccess(
								ResponseDto<ArrayList<UserDto>> result) {
							dataTable.bindData(result.getPayload(), result
									.getCursorString(), true, false);
						}
					});
		}
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}

	public void setCompletionListener(CompletionListener l) {
		listener = l;
	}
}
