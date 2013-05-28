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
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyedLocaleServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.AccessPointSearchControl.Mode;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.user.app.gwt.client.PermissionConstants;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * UI component for searching/editing/creating SurveyedLocale objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyedLocaleManager extends Composite implements
		DataTableBinder<SurveyedLocaleDto>,
		DataTableListener<SurveyedLocaleDto>, ClickHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private static final String DEFAULT_SORT_FIELD = "key";
	private static final Integer PAGE_SIZE = 20;
	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader(TEXT_CONSTANTS.id(), "key", true),
			new DataTableHeader(TEXT_CONSTANTS.communityCode(), "identifier",
					true),
			new DataTableHeader(TEXT_CONSTANTS.latitude(), "latitude", true),
			new DataTableHeader(TEXT_CONSTANTS.longitude(), "longitude", true),
			new DataTableHeader(TEXT_CONSTANTS.pointType(), "localeType", true),
			new DataTableHeader(TEXT_CONSTANTS.lastUpdated(),
					"lastSurveyedDate", true),
			new DataTableHeader(TEXT_CONSTANTS.editDelete()) };

	private Panel contentPanel;
	private PaginatedDataTable<SurveyedLocaleDto> dataTable;
	private AccessPointSearchControl searchControl;
	private Button searchButton;
	private Button createButton;
	private DateTimeFormat dateFormat;
	private UserDto currentUser;
	private SurveyedLocaleServiceAsync surveyedLocaleService;

	public SurveyedLocaleManager(UserDto user) {
		surveyedLocaleService = GWT.create(SurveyedLocaleService.class);
		currentUser = user;
		contentPanel = new VerticalPanel();
		contentPanel.add(constructSearchPanel());
		dataTable = new PaginatedDataTable<SurveyedLocaleDto>(
				DEFAULT_SORT_FIELD, this, this, false,false);
		contentPanel.add(dataTable);
		dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
		initWidget(contentPanel);
	}

	/**
	 * constructs the search control and binds the button listeners to the
	 * search button.
	 * 
	 * @return
	 */
	private Composite constructSearchPanel() {
		CaptionPanel cap = new CaptionPanel(TEXT_CONSTANTS.searchCriteria());
		Panel content = new VerticalPanel();
		searchControl = new AccessPointSearchControl(Mode.LOCALE);
		searchButton = new Button(TEXT_CONSTANTS.search());
		createButton = new Button(TEXT_CONSTANTS.createNew());
		Panel buttonPanel = new HorizontalPanel();
		buttonPanel.add(searchButton);
		if(currentUser.hasPermission(PermissionConstants.EDIT_AP)){
			buttonPanel.add(createButton);
		}
		content.add(searchControl);
		content.add(buttonPanel);
		cap.add(content);
		searchButton.addClickHandler(this);
		createButton.addClickHandler(this);
		return cap;
	}

	@Override
	public void onItemSelected(SurveyedLocaleDto item) {
		// TODO Auto-generated method stub

	}

	/**
	 * constructs a search criteria object using values from the form
	 * 
	 * @return
	 */
	private AccessPointSearchCriteriaDto formSearchCriteria() {
		AccessPointSearchCriteriaDto dto = searchControl.getSearchCriteria();
		dto.setOrderBy(dataTable.getCurrentSortField());
		dto.setOrderByDir(dataTable.getCurrentSortDirection());
		return dto;
	}

	@Override
	public void requestData(String cursor, final boolean isResort) {
		final boolean isNew = (cursor == null);
		final AccessPointSearchCriteriaDto searchDto = formSearchCriteria();

		AsyncCallback<ResponseDto<ArrayList<SurveyedLocaleDto>>> dataCallback = new AsyncCallback<ResponseDto<ArrayList<SurveyedLocaleDto>>>() {
			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.error(),
						TEXT_CONSTANTS.errorTracePrefix() + " "
								+ caught.getLocalizedMessage());
				errDia.showCentered();

			}

			@Override
			public void onSuccess(
					ResponseDto<ArrayList<SurveyedLocaleDto>> result) {
				dataTable.bindData(result.getPayload(),
						result.getCursorString(), isNew, isResort);

				if (result.getPayload() != null
						&& result.getPayload().size() > 0) {
					dataTable.setVisible(true);

				}
			}
		};
		surveyedLocaleService.listLocales(searchDto, cursor, dataCallback);
	}

	@Override
	public DataTableHeader[] getHeaders() {
		return HEADERS;
	}

	@Override
	public void bindRow(final Grid grid, final SurveyedLocaleDto item,final int row) {
		Label keyIdLabel = new Label(item.getKeyId().toString());
		grid.setWidget(row, 0, keyIdLabel);
		if (item.getIdentifier() != null) {
			String communityCode = item.getIdentifier();
			if (communityCode.length() > 10)
				communityCode = communityCode.substring(0, 10);
			grid.setWidget(row, 1, new Label(communityCode));
		}

		if (item.getLatitude() != null && item.getLongitude() != null) {
			grid.setWidget(row, 2, new Label(item.getLatitude().toString()));
			grid.setWidget(row, 3, new Label(item.getLongitude().toString()));
		}
		if (item.getLocaleType() != null) {
			grid.setWidget(row, 4, new Label(item.getLocaleType()));
		}
		if (item.getLastSurveyedDate() != null) {
			grid.setWidget(row, 5,
					new Label(dateFormat.format(item.getLastSurveyedDate())));
		}

		Button editLocale = new Button(TEXT_CONSTANTS.edit());		
		Button deleteLocale = new Button(TEXT_CONSTANTS.delete());	
		deleteLocale.setTitle(row+"|"+item.getKeyId());
		HorizontalPanel buttonHPanel = new HorizontalPanel();
		buttonHPanel.add(editLocale);
		buttonHPanel.add(deleteLocale);
		if (!currentUser.hasPermission(PermissionConstants.EDIT_AP)) {
			buttonHPanel.setVisible(false);
		}

		editLocale.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {				
				SurveyedLocaleEditorDialog dia = new SurveyedLocaleEditorDialog(new CompletionListener() {
					
					@Override
					public void operationComplete(boolean wasSuccessful,
							Map<String, Object> payload) {					
						if(payload != null && payload.containsKey(SurveyedLocaleEditorWidget.LOCALE_KEY)){
							SurveyedLocaleDto dto = (SurveyedLocaleDto)payload.get(SurveyedLocaleEditorWidget.LOCALE_KEY);
							bindRow(grid,dto,row);							
						}
					}
				},item, currentUser.hasPermission(PermissionConstants.EDIT_AP));
				dia.show();
			}

		});

		deleteLocale.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final Button pressedButton = (Button) event.getSource();
				String[] titleParts = pressedButton.getTitle().split("\\|");
				final Integer row = Integer.parseInt(titleParts[0]);
				final Long itemId = Long.parseLong(titleParts[1]);

				surveyedLocaleService.deleteLocale(itemId,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(TEXT_CONSTANTS.errorTracePrefix()
										+ " " + caught.getLocalizedMessage());
							}

							@Override
							public void onSuccess(Void result) {
								int rowSelected = row;
								dataTable.removeRow(rowSelected);
								Grid grid = dataTable.getGrid();
								for (int i = rowSelected; i < grid
										.getRowCount() - 1; i++) {
									HorizontalPanel hPanel = (HorizontalPanel) grid
											.getWidget(i, 6);
									Button deleteButton = (Button) hPanel
											.getWidget(1);
									String[] buttonTitleParts = deleteButton
											.getTitle().split("\\|");
									Integer newRowNum = Integer
											.parseInt(buttonTitleParts[0]);
									newRowNum = newRowNum - 1;
									deleteButton.setTitle(newRowNum + "|"
											+ buttonTitleParts[1]);
								}
								Window.alert(TEXT_CONSTANTS.deleteComplete());
							}

						});

			}

		});
		grid.setWidget(row, 6, buttonHPanel);

	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == searchButton) {
			requestData(null, false);
		}else if (event.getSource() == createButton){
			SurveyedLocaleEditorDialog dia = new SurveyedLocaleEditorDialog(new CompletionListener() {				
				@Override
				public void operationComplete(boolean wasSuccessful,
						Map<String, Object> payload) {					
					if(payload != null && payload.containsKey(SurveyedLocaleEditorWidget.LOCALE_KEY)){
						SurveyedLocaleDto dto = (SurveyedLocaleDto)payload.get(SurveyedLocaleEditorWidget.LOCALE_KEY);
						dataTable.addNewRow(dto);											
					}
				}
			},null, currentUser.hasPermission(PermissionConstants.EDIT_AP));
			dia.show();
		}

	}

}
