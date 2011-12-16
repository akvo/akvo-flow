package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.MetricDto;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricService;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.user.app.gwt.client.PermissionConstants;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * widget to be used to administer metrics
 * 
 * @author Christopher Fagiani
 * 
 */
public class MetricManagerWidget extends Composite implements
		DataTableBinder<MetricDto>, DataTableListener<MetricDto>, ClickHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private static final String DEFAULT_SORT_FIELD = "key";
	private static final Integer PAGE_SIZE = 20;
	private static final String STRING_TYPE = "String";
	private static final String DOUBLE_TYPE = "Double";
	private static final String GEO_TYPE = "Geo";
	private static final String MEDIA_TYPE = "Media";
	private static final String IDENTIFIER_TYPE = "Identifier";
	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader(TEXT_CONSTANTS.id(), "key", true),
			new DataTableHeader(TEXT_CONSTANTS.name(), "name", true),
			new DataTableHeader(TEXT_CONSTANTS.group(), "group", true),
			new DataTableHeader(TEXT_CONSTANTS.valueType(), "valueType", true),
			new DataTableHeader(TEXT_CONSTANTS.editDelete()) };

	private Panel contentPanel;
	private PaginatedDataTable<MetricDto> dataTable;
	private Button searchButton;
	private Button createButton;
	private ListBox valueTypeListBox;
	private TextBox nameTextBox;
	private TextBox groupTextBox;

	private UserDto currentUser;
	private MetricServiceAsync metricService;

	public MetricManagerWidget(UserDto user) {
		metricService = GWT.create(MetricService.class);
		currentUser = user;
		contentPanel = new VerticalPanel();
		contentPanel.add(constructSearchPanel());
		dataTable = new PaginatedDataTable<MetricDto>(DEFAULT_SORT_FIELD, this,
				this, false, false);
		contentPanel.add(dataTable);

		initWidget(contentPanel);
		requestData(null, false);
	}

	/**
	 * constructs the search control and binds the button listeners to the
	 * search button.
	 * 
	 * @return
	 */
	private Composite constructSearchPanel() {
		CaptionPanel cap = new CaptionPanel(TEXT_CONSTANTS.filterResults());
		Panel content = new VerticalPanel();
		Panel searchControls = new HorizontalPanel();
		valueTypeListBox = new ListBox();
		valueTypeListBox.addItem("", "");
		valueTypeListBox.addItem(TEXT_CONSTANTS.text(), STRING_TYPE);
		valueTypeListBox.addItem(TEXT_CONSTANTS.number(), DOUBLE_TYPE);
		valueTypeListBox.addItem(TEXT_CONSTANTS.geo(),GEO_TYPE);
		valueTypeListBox.addItem(TEXT_CONSTANTS.media(),MEDIA_TYPE);
		valueTypeListBox.addItem(TEXT_CONSTANTS.identifier(),IDENTIFIER_TYPE);
		nameTextBox = new TextBox();
		groupTextBox = new TextBox();
		searchControls.add(ViewUtil.initLabel(TEXT_CONSTANTS.name()));
		searchControls.add(nameTextBox);
		searchControls.add(ViewUtil.initLabel(TEXT_CONSTANTS.group()));
		searchControls.add(groupTextBox);
		searchControls.add(ViewUtil.initLabel(TEXT_CONSTANTS.valueType()));
		searchControls.add(valueTypeListBox);
		HorizontalPanel buttonPanel = new HorizontalPanel();
		searchButton = new Button(TEXT_CONSTANTS.search());
		content.add(searchControls);
		buttonPanel.add(searchButton);
		cap.add(content);
		searchButton.addClickHandler(this);
		createButton = new Button(TEXT_CONSTANTS.createNew());
		buttonPanel.add(createButton);
		content.add(buttonPanel);
		createButton.addClickHandler(this);
		return cap;
	}

	@Override
	public void onItemSelected(MetricDto item) {
		// no-op
	}

	@Override
	public void requestData(String cursor, final boolean isResort) {
		final boolean isNew = (cursor == null);

		AsyncCallback<ResponseDto<ArrayList<MetricDto>>> dataCallback = new AsyncCallback<ResponseDto<ArrayList<MetricDto>>>() {
			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.error(),
						TEXT_CONSTANTS.errorTracePrefix() + " "
								+ caught.getLocalizedMessage());
				errDia.showCentered();

			}

			@Override
			public void onSuccess(ResponseDto<ArrayList<MetricDto>> result) {
				dataTable.bindData(result.getPayload(),
						result.getCursorString(), isNew, isResort);

				if (result.getPayload() != null
						&& result.getPayload().size() > 0) {
					dataTable.setVisible(true);

				}
			}
		};
		// TODO: handle org
		metricService.listMetrics(ViewUtil.getNonBlankValue(nameTextBox),
				ViewUtil.getNonBlankValue(groupTextBox),
				ViewUtil.getListBoxSelection(valueTypeListBox, true), null,
				cursor, dataCallback);
	}

	@Override
	public DataTableHeader[] getHeaders() {
		return HEADERS;
	}

	@Override
	public void bindRow(final Grid grid, final MetricDto item, final int row) {
		Label keyIdLabel = new Label(item.getKeyId().toString());
		grid.setWidget(row, 0, keyIdLabel);
		grid.setWidget(row, 1, new Label(item.getName()));
		grid.setWidget(row, 2,
				new Label(item.getGroup() != null ? item.getGroup() : ""));

		if (item.getValueType() == null
				|| STRING_TYPE.equals(item.getValueType())) {
			grid.setWidget(row, 3, new Label(TEXT_CONSTANTS.text()));
		} else {
			grid.setWidget(row, 3, new Label(TEXT_CONSTANTS.number()));
		}

		Button editButton = new Button(TEXT_CONSTANTS.edit());
		Button deleteButton = new Button(TEXT_CONSTANTS.delete());
		HorizontalPanel buttonHPanel = new HorizontalPanel();
		buttonHPanel.add(editButton);
		buttonHPanel.add(deleteButton);
		if (!currentUser.hasPermission(PermissionConstants.EDIT_SURVEY)) {
			buttonHPanel.setVisible(false);
		}

		editButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				openEditDialog(item, row);
			}

		});

		deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				metricService.deleteMetric(item.getKeyId(),
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								MessageDialog dia = new MessageDialog(
										TEXT_CONSTANTS.error(), TEXT_CONSTANTS
												.errorTracePrefix()
												+ " "
												+ caught.getLocalizedMessage());
								dia.showCentered();
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
		grid.setWidget(row, 4, buttonHPanel);

	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == searchButton) {
			requestData(null, false);
		} else if (event.getSource() == createButton) {
			MetricDto metric = new MetricDto();
			metric.setName(nameTextBox.getValue());
			metric.setGroup(groupTextBox.getValue());
			metric.setValueType(ViewUtil.getListBoxSelection(valueTypeListBox,
					true));
			openEditDialog(metric, -1);
		}
	}

	/**
	 * displays the editor in a dialog
	 * 
	 * @param metric
	 * @param row
	 */
	private void openEditDialog(MetricDto metric, final int row) {
		MetricEditDialog dia = new MetricEditDialog(new CompletionListener() {
			@Override
			public void operationComplete(boolean wasSuccessful,
					Map<String, Object> payload) {
				if (payload != null
						&& payload.containsKey(MetricEditWidget.METRIC_PAYLOAD_KEY)) {
					MetricDto m = (MetricDto) payload
							.get(MetricEditWidget.METRIC_PAYLOAD_KEY);
					if (row >= 0) {
						bindRow(dataTable.getGrid(), m, row);
					} else {
						dataTable.addNewRow(m);
					}
				}
			}
		}, metric);
		dia.showCentered();
	}
}
