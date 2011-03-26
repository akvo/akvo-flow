package org.waterforpeople.mapping.surveyentry.client.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
 * widget for finding an existing AccessPoint
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointLocatorDialog extends WidgetDialog implements
		DataTableBinder<AccessPointDto>, DataTableListener<AccessPointDto>,
		ClickHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	public static final String SELECTED_AP_KEY = "SELECTEDAP";
	private static final Integer PAGE_SIZE = 10;
	private static final String TITLE =TEXT_CONSTANTS.findAccessPoint();
	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader(TEXT_CONSTANTS.id(), "key", true),
			new DataTableHeader(TEXT_CONSTANTS.communityCode(), "communityCode", true),
			new DataTableHeader(TEXT_CONSTANTS.latitude(), "latitude", true),
			new DataTableHeader(TEXT_CONSTANTS.longitude(), "longitude", true),
			new DataTableHeader(TEXT_CONSTANTS.pointType(), "pointType", true) };
	private static final String DEFAULT_SORT_FIELD = "key";

	private AccessPointManagerServiceAsync apmService;
	private PaginatedDataTable<AccessPointDto> apTable;
	private Panel mainPanel;
	private Composite searchPanel;
	private ListBox pointTypeBox;
	private TextBox commCodeBox;
	private Label statusLabel;
	private Button searchButton;
	private Button selectButton;
	private AccessPointDto selectedAP;

	public AccessPointLocatorDialog(CompletionListener listener) {
		super(TITLE, null, listener);
		mainPanel = new VerticalPanel();
		apmService = GWT.create(AccessPointManagerService.class);
		apTable = new PaginatedDataTable<AccessPointDto>(DEFAULT_SORT_FIELD,
				this, this, true);
		apTable.setVisible(false);
		initSearchPanel();
		mainPanel.add(searchPanel);
		mainPanel.add(apTable);
		selectButton = new Button(TEXT_CONSTANTS.selectAndClose());
		selectButton.setEnabled(false);
		selectButton.addClickHandler(this);
		mainPanel.add(selectButton);
		setContentWidget(mainPanel);
	}

	private void initSearchPanel() {
		searchPanel = new CaptionPanel(TEXT_CONSTANTS.searchCriteria());
		VerticalPanel controlsPanel = new VerticalPanel();
		Panel temp = new HorizontalPanel();
		pointTypeBox = new ListBox();
		for (int i = 0; i < AccessPointDto.AccessPointType.values().length; i++) {
			pointTypeBox.addItem(AccessPointDto.AccessPointType.values()[i]
					.toString(), AccessPointDto.AccessPointType.values()[i]
					.toString());
		}
		commCodeBox = new TextBox();
		searchButton = new Button(TEXT_CONSTANTS.search());
		searchButton.addClickHandler(this);
		temp.add(ViewUtil.initLabel(TEXT_CONSTANTS.communityCode()));
		temp.add(commCodeBox);
		// temp.add(ViewUtil.initLabel("Point Type"));
		// temp.add(pointTypeBox);
		temp.add(searchButton);
		controlsPanel.add(temp);
		statusLabel = ViewUtil.initLabel(TEXT_CONSTANTS.loading());
		statusLabel.setVisible(false);
		controlsPanel.add(statusLabel);
		((CaptionPanel) searchPanel).add(controlsPanel);
	}

	@Override
	public void bindRow(final Grid grid, AccessPointDto apDto, int row) {
		Label keyIdLabel = new Label(apDto.getKeyId().toString());
		grid.setWidget(row, 0, keyIdLabel);
		if (apDto.getCommunityCode() != null) {
			String communityCode = apDto.getCommunityCode();
			if (communityCode.length() > 10)
				communityCode = communityCode.substring(0, 10);
			grid.setWidget(row, 1, new Label(communityCode));
		}

		if (apDto.getLatitude() != null && apDto.getLongitude() != null) {
			grid.setWidget(row, 2, new Label(apDto.getLatitude().toString()));
			grid.setWidget(row, 3, new Label(apDto.getLongitude().toString()));
		}
		if (apDto.getPointType() != null) {
			grid.setWidget(row, 4, new Label(apDto.getPointType().name()));
		}
	}

	@Override
	public DataTableHeader[] getHeaders() {
		return HEADERS;
	}

	@Override
	public void onItemSelected(AccessPointDto item) {
		selectedAP = item;
		if (selectedAP != null) {
			selectButton.setEnabled(true);
		}
	}

	@Override
	public void requestData(String cursor, final boolean isResort) {
		statusLabel.setVisible(true);
		final boolean isNew = (cursor == null);
		final AccessPointSearchCriteriaDto searchDto = formSearchCriteria();
		boolean isOkay = true;
		AsyncCallback<ResponseDto<ArrayList<AccessPointDto>>> dataCallback = new AsyncCallback<ResponseDto<ArrayList<AccessPointDto>>>() {
			@Override
			public void onFailure(Throwable caught) {
				statusLabel.setVisible(false);
				MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS.error(),TEXT_CONSTANTS.errorTracePrefix()+" "+caught.getLocalizedMessage());
				errDia.showCentered();

			}

			@Override
			public void onSuccess(ResponseDto<ArrayList<AccessPointDto>> result) {
				apTable.bindData(result.getPayload(), result.getCursorString(),
						isNew, isResort);
				if (result.getPayload() != null
						&& result.getPayload().size() > 0) {
					apTable.setVisible(true);
				}
				statusLabel.setVisible(false);
			}
		};

		if (searchDto != null) {
			if (searchDto.getCollectionDateFrom() != null
					|| searchDto.getCollectionDateTo() != null) {
				if (isOkay) {
					searchDto.setOrderBy(apTable.getCurrentSortField());
					searchDto.setOrderByDir(apTable.getCurrentSortDirection());
				}
			}
		}
		if (isOkay) {
			apmService.listAccessPoints(searchDto, cursor, dataCallback);
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == searchButton) {
			requestData(null, false);
		} else if (event.getSource() == selectButton) {
			Map<String, Object> payload = new HashMap<String, Object>();
			payload.put(SELECTED_AP_KEY, selectedAP);
			hide(true);
			notifyListener(true, payload);
		}
	}

	private AccessPointSearchCriteriaDto formSearchCriteria() {
		AccessPointSearchCriteriaDto dto = new AccessPointSearchCriteriaDto();
		if (ViewUtil.isTextPopulated(commCodeBox)) {
			dto.setCommunityCode(commCodeBox.getText());
		}
		// TODO: put back once this is parameterized
		/*
		 * dto .setPointType(pointTypeBox.getValue(pointTypeBox
		 * .getSelectedIndex()));
		 */
		dto.setPointType(AccessPointDto.AccessPointType.WATER_POINT.toString());
		dto.setPageSize(PAGE_SIZE);
		dto.setOrderBy(apTable.getCurrentSortField());
		dto.setOrderByDir(apTable.getCurrentSortDirection());
		return dto;
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}
}
