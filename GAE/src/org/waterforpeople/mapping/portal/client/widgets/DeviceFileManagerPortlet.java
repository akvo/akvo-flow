package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesDto;
import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesService;
import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesServiceAsync;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DeviceFileManagerPortlet extends LocationDrivenPortlet implements
		DataTableBinder<DeviceFilesDto>, DataTableListener<DeviceFilesDto> {
	public static final String DESCRIPTION = "View and Reprocess Device Files";
	public static final String NAME = "Device File Manager Portlet";

	private static final String DEFAULT_SORT_FIELD = "createdDateTime";
	private PaginatedDataTable<DeviceFilesDto> dfTable;
	private Integer PAGE_SIZE = 20;
	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader("Id", "key", true),
			new DataTableHeader("Device Identifier", "devicePhoneNumber", true),
			new DataTableHeader("Uri", "uri", true),
			new DataTableHeader("Status", "processedStatus", true),
			new DataTableHeader("Created Date Time", "createDateTime", true),
			new DataTableHeader("Action") };

	private static final String ANY_OPT = "Any";
	private static final int WIDTH = 1600;
	private static final int HEIGHT = 800;
	private VerticalPanel contentPane;
	VerticalPanel mainVPanel = new VerticalPanel();
	DeviceFilesServiceAsync svc;
	ListBox processStatus = new ListBox();

	public DeviceFileManagerPortlet(String title, boolean scrollable,
			boolean configurable, boolean snapable, int width, int height,
			UserDto user, boolean useCommunity, String specialOption) {
		super(title, scrollable, configurable, snapable, width, height, user,
				useCommunity, specialOption);
		// TODO Auto-generated constructor stub
	}
	
	public DeviceFileManagerPortlet(UserDto user) {
		super(NAME, true, false, false, WIDTH, HEIGHT, user, true,
				LocationDrivenPortlet.ANY_OPT);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		dfTable = new PaginatedDataTable<DeviceFilesDto>(DEFAULT_SORT_FIELD,
				this, this, true);
		contentPane.add(header);
		setContent(contentPane);
		svc = GWT.create(DeviceFilesService.class);
		prepareProcessStatusLB();
		mainVPanel.add(processStatus);
		dfTable.setVisible(false);
		requestData(null, false);
		mainVPanel.add(dfTable);

	}
	
	private void prepareProcessStatusLB(){
		processStatus.addItem("Error Inflating Zip", "ERROR_INFLATING_ZIP");
		processStatus.addItem("In Progress","IN_PROGRESS");
		processStatus.addItem("Processed No Errors","PROCESSED_NO_ERRORS");
		processStatus.addItem("Processed With Errors","PROCESSED_WITH_ERRORS");
		processStatus.addItem("Reprocessing","REPROCESSING");
		processStatus.setSelectedIndex(3);
		processStatus.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent event) {
				requestData(null, false);
			}
			
		});
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	private Widget buildHeader() {
		Grid grid = new Grid(2, 2);

		grid.setWidget(0, 0, mainVPanel);
		return grid;
	}

	@Override
	public void onItemSelected(DeviceFilesDto item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestData(String cursor, final boolean isResort) {
		final boolean isNew = (cursor == null);
		boolean isOkay = true;
		final String statusCode = processStatus.getValue(processStatus.getSelectedIndex()); 
		AsyncCallback<ResponseDto<ArrayList<DeviceFilesDto>>> dataCallback = new AsyncCallback<ResponseDto<ArrayList<DeviceFilesDto>>>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDia = new MessageDialog("Application Error",
						"Cannot search");
				errDia.showRelativeTo(dfTable);

			}

			@Override
			public void onSuccess(ResponseDto<ArrayList<DeviceFilesDto>> result) {
				dfTable.bindData(result.getPayload(), result.getCursorString(),
						isNew, isResort);

				if (result.getPayload() != null
						&& result.getPayload().size() > 0) {
					dfTable.setVisible(true);
				}
			}

		};
		svc.listDeviceFiles(statusCode, cursor, dataCallback);
	}

	@Override
	public DataTableHeader[] getHeaders() {
		// TODO Auto-generated method stub
		return HEADERS;
	}

	@Override
	public void bindRow(final Grid grid, DeviceFilesDto item, final int row) {
		grid.setWidget(row, 0, new Label(item.getKeyId().toString()));
		grid.setWidget(row, 1, new Label(item.getPhoneNumber()));
		String[] formattedFileNameParts = item.getURI().split("/");
		grid.setWidget(row, 2, new Hyperlink(
				formattedFileNameParts[formattedFileNameParts.length - 1],item.getURI()));
		
		grid.setWidget(row, 3, new Label(item.getProcessedStatus()));
		grid.setWidget(row, 4, new Label(item.getProcessDate()));
		/*String abbrvProcessingMessage = "";
		if (item.getProcessingMessage() != null) {
			if (item.getProcessingMessage().length() > 25) {
				abbrvProcessingMessage = item.getProcessingMessage().substring(
						0, 25);
			}else{
				abbrvProcessingMessage = item.getProcessingMessage();
			}
		}
		grid.setWidget(row, 5, new Label(abbrvProcessingMessage));*/
		Button reprocessButton = new Button("Reprocess");
		
		reprocessButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Hyperlink uri = (Hyperlink)grid.getWidget(row, 2);
				svc.reprocessDeviceFile(uri.getText(), new AsyncCallback<String>(){

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(String result) {
						Label status = (Label)grid.getWidget(row, 3);
						status.setText("REPROCESSING");
						
					}
					
				});

			}

		});
		grid.setWidget(row, 5, reprocessButton);
	};

	@Override
	public Integer getPageSize(){
		return PAGE_SIZE;
	}
}
