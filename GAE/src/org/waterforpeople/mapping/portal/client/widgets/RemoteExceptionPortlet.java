package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.device.DeviceDto;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceService;
import org.waterforpeople.mapping.app.gwt.client.device.DeviceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.diagnostics.RemoteExceptionService;
import org.waterforpeople.mapping.app.gwt.client.diagnostics.RemoteExceptionServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.diagnostics.RemoteStacktraceDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.SmallZoomControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * UI widget for viewing, acknowledging and deleting remoteException entities.
 * 
 * @author Christopher Fagiani
 * 
 */
public class RemoteExceptionPortlet extends Portlet implements
		DataTableBinder<RemoteStacktraceDto>,
		DataTableListener<RemoteStacktraceDto>, ClickHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String NAME = TEXT_CONSTANTS
			.remoteExceptionPortletTitle();

	private static final Integer width = 1024;
	private static final Integer height = 768;
	private static final String TRACE_WIDTH = "350px";
	private static final String TRACE_HEIGHT = "400px";
	private static final DataTableHeader TABLE_HEADERS[] = {
			new DataTableHeader(TEXT_CONSTANTS.errorDate()),
			new DataTableHeader(TEXT_CONSTANTS.phoneNumber()),
			new DataTableHeader(TEXT_CONSTANTS.deviceId()),
			new DataTableHeader(TEXT_CONSTANTS.softwareVersion()),
			new DataTableHeader("") };
	private static final Integer PAGE_SIZE = 20;
	private RemoteExceptionServiceAsync remoteExceptionService;
	private DeviceServiceAsync deviceService;
	private PaginatedDataTable<RemoteStacktraceDto> remoteExceptionTable;
	private HorizontalPanel finderPanel;
	private TextBox phoneNumberBox;
	private TextBox deviceIdBox;
	private HorizontalPanel contentPanel;
	private TextArea traceArea;
	private Button findButton;
	private MapWidget mapWidget;
	private Overlay deviceLocOverlay;
	private Button deleteOldButton;

	public RemoteExceptionPortlet() {
		super(NAME, false, false, true, width, height);
		remoteExceptionService = GWT.create(RemoteExceptionService.class);
		deviceService = GWT.create(DeviceService.class);
		loadContentPanel();
	}

	private void loadContentPanel() {
		finderPanel = new HorizontalPanel();
		phoneNumberBox = new TextBox();
		finderPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.phoneNumber()));
		finderPanel.add(phoneNumberBox);
		deviceIdBox = new TextBox();
		finderPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.deviceId()));
		finderPanel.add(deviceIdBox);
		findButton = new Button(TEXT_CONSTANTS.find());
		finderPanel.add(findButton);
		findButton.addClickHandler(this);
		mapWidget = new MapWidget();
		mapWidget.setSize(TRACE_WIDTH, TRACE_WIDTH);
		mapWidget.addControl(new SmallZoomControl());
		mapWidget.setZoomLevel(12);
		mapWidget.setVisible(false);

		remoteExceptionTable = new PaginatedDataTable<RemoteStacktraceDto>(
				"errorDate", this, this, true);

		contentPanel = new HorizontalPanel();
		VerticalPanel rightPanel = new VerticalPanel();
		traceArea = new TextArea();
		traceArea.setWidth(TRACE_WIDTH);
		traceArea.setHeight(TRACE_HEIGHT);
		traceArea.setReadOnly(true);
		rightPanel.add(traceArea);
		rightPanel.add(mapWidget);

		VerticalPanel leftPanel = new VerticalPanel();
		leftPanel.add(finderPanel);
		leftPanel.add(remoteExceptionTable);
		deleteOldButton = new Button(TEXT_CONSTANTS.deleteExceptions());
		deleteOldButton.addClickHandler(this);
		leftPanel.add(deleteOldButton);
		contentPanel.add(leftPanel);
		contentPanel.add(rightPanel);
		requestData(null, false);
		ScrollPanel sp = new ScrollPanel(contentPanel);
		sp.setHeight(height.toString());
		setWidget(sp);
	}

	/**
	 * binds a SurveyInstanceDto to the grid at the row passed in.
	 */
	@Override
	public void bindRow(Grid grid, final RemoteStacktraceDto item, int row) {
		grid.setWidget(
				row,
				0,
				new Label(DateTimeFormat.getFormat(
						DateTimeFormat.PredefinedFormat.DATE_MEDIUM).format(
						item.getErrorDate())));
		grid.setWidget(row, 1,
				new Label(item.getPhoneNumber() != null ? item.getPhoneNumber()
						: ""));
		grid.setWidget(row, 2, new Label(
				item.getDeviceIdentifier() != null ? item.getDeviceIdentifier()
						: ""));
		grid.setWidget(row, 3, new Label(
				item.getSoftwareVersion() != null ? item.getSoftwareVersion()
						: ""));
		HorizontalPanel hp = new HorizontalPanel();

		final Button ackButton = new Button(TEXT_CONSTANTS.acknowledge());
		final Button delButton = new Button(TEXT_CONSTANTS.delete());
		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(
								TEXT_CONSTANTS.error(),
								TEXT_CONSTANTS.errorTracePrefix() + " "
										+ caught.getLocalizedMessage());
						errDia.showRelativeTo(remoteExceptionTable);
					}

					@Override
					public void onSuccess(Boolean result) {
						// reload the table since the item acked/deleted will no
						// longer show up
						requestData(null, false);

					}
				};
				if (event.getSource() == ackButton) {
					remoteExceptionService.acknowledgeRemoteStacktrace(
							item.getKeyId(), callback);
				} else if (event.getSource() == delButton) {
					remoteExceptionService.deleteRemoteStacktrace(
							item.getKeyId(), callback);
				}

			}
		};

		ackButton.addClickHandler(handler);
		delButton.addClickHandler(handler);
		hp.add(ackButton);
		hp.add(delButton);
		grid.setWidget(row, 4, hp);

	}

	@Override
	public DataTableHeader[] getHeaders() {
		return TABLE_HEADERS;
	}

	@Override
	public void onItemSelected(RemoteStacktraceDto item) {
		traceArea.setText(item.getStackTrace());
		// now try to show the last known device location on a map
		if (item.getPhoneNumber() != null) {
			deviceService.findDeviceByPhoneNumber(item.getPhoneNumber(),
					new AsyncCallback<DeviceDto>() {

						@Override
						public void onFailure(Throwable caught) {
							mapWidget.setVisible(false);
						}

						@Override
						public void onSuccess(DeviceDto result) {
							if (result != null
									&& result.getLastKnownLat() != null
									&& result.getLastKnownLon() != null) {
								LatLng point = LatLng.newInstance(
										result.getLastKnownLat(),
										result.getLastKnownLon());
								if (deviceLocOverlay != null) {
									mapWidget.removeOverlay(deviceLocOverlay);
								}
								deviceLocOverlay = new Marker(point);
								mapWidget.addOverlay(deviceLocOverlay);
								mapWidget.setZoomLevel(12);
								mapWidget.panTo(point);
								mapWidget.setVisible(true);
							} else {
								mapWidget.setVisible(false);
							}
						}
					});
		} else {
			mapWidget.setVisible(false);
		}
	}

	/**
	 * call the server to get more data.
	 */
	@Override
	public void requestData(String cursor, final boolean isResort) {

		final boolean isNew = cursor == null ? true : false;
		String phNum = null;
		String devId = null;
		if (phoneNumberBox.getText() != null
				&& phoneNumberBox.getText().trim().length() > 0) {
			phNum = phoneNumberBox.getText().trim();
		}
		if (deviceIdBox.getText() != null
				&& deviceIdBox.getText().trim().length() > 0) {
			devId = deviceIdBox.getText().trim();
		}
		remoteExceptionService
				.listRemoteExceptions(
						phNum,
						devId,
						true,
						cursor,
						new AsyncCallback<ResponseDto<ArrayList<RemoteStacktraceDto>>>() {

							@Override
							public void onSuccess(
									ResponseDto<ArrayList<RemoteStacktraceDto>> result) {
								remoteExceptionTable.bindData(
										result.getPayload(),
										result.getCursorString(), isNew,
										isResort);
							}

							@Override
							public void onFailure(Throwable caught) {
								MessageDialog errDia = new MessageDialog(
										TEXT_CONSTANTS.error(), TEXT_CONSTANTS
												.errorTracePrefix()
												+ " "
												+ caught.getLocalizedMessage());
								errDia.showRelativeTo(remoteExceptionTable);

							}
						});

	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == findButton) {
			requestData(null, false);
		} else if (event.getSource() == deleteOldButton) {
			MessageDialog confDia = new MessageDialog(
					TEXT_CONSTANTS.confirmDelete(),
					TEXT_CONSTANTS.deleteExceptionsWarning(), false,
					new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							final MessageDialog waitDia = new MessageDialog(
									TEXT_CONSTANTS.deleting(), TEXT_CONSTANTS
											.pleaseWait(), true);
							waitDia.showCentered();
							remoteExceptionService
									.deleteOldExceptions(new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											waitDia.hide();
											MessageDialog errDia = new MessageDialog(
													TEXT_CONSTANTS.error(),
													TEXT_CONSTANTS
															.errorTracePrefix()
															+ " "
															+ caught.getLocalizedMessage());
											errDia.showCentered();
										}

										@Override
										public void onSuccess(Void result) {
											waitDia.hide();
											requestData(null, false);
										}
									});
						}
					});
			confDia.showCentered();
		}
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}
}
