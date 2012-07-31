package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.community.CommunityService;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CountryEditWidget extends Composite implements
		DataTableListener<CountryDto>, DataTableBinder<CountryDto>,
		ClickHandler {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private CommunityServiceAsync communityService = GWT
			.create(CommunityService.class);

	private static final String DEFAULT_SORT_FIELD = "displayName";
	private static final DataTableHeader[] GRID_HEADERS = {
			new DataTableHeader(TEXT_CONSTANTS.name()),
			new DataTableHeader(TEXT_CONSTANTS.code()),
			new DataTableHeader(TEXT_CONSTANTS.latitude()),
			new DataTableHeader(TEXT_CONSTANTS.longitude()),
			new DataTableHeader(TEXT_CONSTANTS.includeInKmz()),
			new DataTableHeader(TEXT_CONSTANTS.includeInPublic()),
			new DataTableHeader("") };
	private static final Integer PAGE_SIZE = 20;
	private VerticalPanel contentPane;
	private PaginatedDataTable<CountryDto> dataTable;
	private Button createButton;

	public CountryEditWidget() {
		contentPane = new VerticalPanel();
		dataTable = new PaginatedDataTable<CountryDto>(DEFAULT_SORT_FIELD,
				this, this, false);
		contentPane.add(dataTable);
		createButton = new Button(TEXT_CONSTANTS.createNew());
		createButton.addClickHandler(this);
		contentPane.add(createButton);
		requestData(null, false);
		initWidget(contentPane);
	}

	@Override
	public void onItemSelected(CountryDto item) {
		// no-op

	}

	/**
	 * call the server to get more data
	 */
	@Override
	public void requestData(String cursor, final boolean isResort) {
		final boolean isNew = (cursor == null);
		communityService.listCountries(cursor,
				new AsyncCallback<ResponseDto<ArrayList<CountryDto>>>() {

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<CountryDto>> result) {
						dataTable.bindData(result.getPayload(),
								result.getCursorString(), isNew, isResort);
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
	public void bindRow(final Grid grid, final CountryDto item, final int row) {
		if (item != null) {
			grid.setWidget(row, 0, ViewUtil.initLabel(item.getDisplayName()));
			grid.setWidget(row, 1, ViewUtil.initLabel(item.getIsoAlpha2Code()));
			grid.setWidget(row, 2,
					ViewUtil.initLabel(item.getCentroidLat() != null ? item
							.getCentroidLat().toString() : ""));
			grid.setWidget(row, 3,
					ViewUtil.initLabel(item.getCentroidLon() != null ? item
							.getCentroidLon().toString() : ""));
			grid.setWidget(row, 4,
					ViewUtil.initLabel(item.getIncludeInKMZ() != null ? item
							.getIncludeInKMZ().toString() : ""));
			grid.setWidget(row, 5, ViewUtil.initLabel(item
					.getIncludeInExternal() != null ? item
					.getIncludeInExternal().toString() : ""));

			Button editButton = new Button(TEXT_CONSTANTS.edit());
			editButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					for (int i = 0; i < grid.getColumnCount(); i++) {
						grid.clearCell(row, i);
					}
					bindForEdit(item, grid, row);
				}
			});
			grid.setWidget(row, 6, editButton);
		} else {
			bindForEdit(new CountryDto(), grid, row);
		}

	}

	private void bindForEdit(final CountryDto item, final Grid grid,
			final int row) {
		final TextBox nameBox = new TextBox();
		final TextBox codeBox = new TextBox();
		final TextBox latBox = ViewUtil.constructNumericTextBox();
		final TextBox lonBox = ViewUtil.constructNumericTextBox();
		final CheckBox kmzBox = new CheckBox();
		final CheckBox publicBox = new CheckBox();

		grid.setWidget(row, 0, nameBox);
		grid.setWidget(row, 1, codeBox);
		grid.setWidget(row, 2, latBox);
		grid.setWidget(row, 3, lonBox);
		grid.setWidget(row, 4, kmzBox);
		grid.setWidget(row, 5, publicBox);

		if (item != null) {
			if (item.getName() != null) {
				nameBox.setText(item.getName());
			}
			if (item.getIsoAlpha2Code() != null) {
				codeBox.setText(item.getIsoAlpha2Code());
			}
			if (item.getCentroidLat() != null) {
				latBox.setText(item.getCentroidLat().toString());
			}
			if (item.getCentroidLon() != null) {
				lonBox.setText(item.getCentroidLon().toString());
			}
			if (item.getIncludeInKMZ() != null) {
				kmzBox.setValue(item.getIncludeInKMZ());
			}
			if (item.getIncludeInExternal() != null) {
				publicBox.setValue(item.getIncludeInExternal());
			}
		}

		Button saveButton = new Button(TEXT_CONSTANTS.save());
		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				item.setName(nameBox.getText());
				item.setIsoAlpha2Code(codeBox.getText());
				try {
					String val = ViewUtil.getNonBlankValue(latBox);
					if (val != null) {
						item.setCentroidLat(new Double(val));
					}
					val = ViewUtil.getNonBlankValue(lonBox);
					if (val != null) {
						item.setCentroidLon(new Double(val));
					}
				} catch (Exception e) {
					MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
							.error(), TEXT_CONSTANTS.errorTracePrefix() + " "
							+ e.getLocalizedMessage());
					errDia.show();
				}
				item.setIncludeInExternal(publicBox.getValue());
				item.setIncludeInKMZ(kmzBox.getValue());
				communityService.saveCountry(item,
						new AsyncCallback<CountryDto>() {

							@Override
							public void onFailure(Throwable caught) {
								MessageDialog errDia = new MessageDialog(
										TEXT_CONSTANTS.error(), TEXT_CONSTANTS
												.errorTracePrefix()
												+ " "
												+ caught.getLocalizedMessage());
								errDia.show();
							}

							@Override
							public void onSuccess(CountryDto result) {
								for (int i = 0; i < grid.getColumnCount(); i++) {
									grid.clearCell(row, i);
								}
								bindRow(grid, result, row);

							}
						});
			}
		});
		grid.setWidget(row, 6, saveButton);
	}

	@Override
	public DataTableHeader[] getHeaders() {
		return GRID_HEADERS;
	}

	/**
	 * handles the search and add new methods
	 */
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == createButton) {
			dataTable.addNewRow(null);
		}
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}

}
