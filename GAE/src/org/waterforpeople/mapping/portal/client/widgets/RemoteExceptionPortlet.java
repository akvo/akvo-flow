package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.diagnostics.RemoteExceptionService;
import org.waterforpeople.mapping.app.gwt.client.diagnostics.RemoteExceptionServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.diagnostics.RemoteStacktraceDto;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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
		DataTableListener<RemoteStacktraceDto> {

	public static final String NAME = "Remote Exception Manager";

	private static Integer width = 1024;
	private static Integer height = 768;
	private static final DataTableHeader TABLE_HEADERS[] = {
			new DataTableHeader("Error Date"),
			new DataTableHeader("Phone Number"),
			new DataTableHeader("Device Id"),
			new DataTableHeader("Software Version"), new DataTableHeader("") };

	private RemoteExceptionServiceAsync remoteExceptionService;
	private PaginatedDataTable<RemoteStacktraceDto> remoteExceptionTable;
	private HorizontalPanel finderPanel;
	private TextBox phoneNumberBox;
	private TextBox deviceIdBox;
	private HorizontalPanel contentPanel;
	private TextArea traceArea;

	public RemoteExceptionPortlet() {
		super(NAME, false, false, true, width, height);
		remoteExceptionService = GWT.create(RemoteExceptionService.class);

		loadContentPanel();
	}

	private void loadContentPanel() {
		finderPanel = new HorizontalPanel();
		phoneNumberBox = new TextBox();
		finderPanel.add(new Label("Phone Number: "));
		finderPanel.add(phoneNumberBox);
		deviceIdBox = new TextBox();
		finderPanel.add(new Label("Device Id: "));
		finderPanel.add(deviceIdBox);
		Button findButton = new Button("Find");
		finderPanel.add(findButton);
		findButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				requestData(null, false);
			}
		});
		remoteExceptionTable = new PaginatedDataTable<RemoteStacktraceDto>(
				"Error Date", this, this, true);

		contentPanel = new HorizontalPanel();
		traceArea = new TextArea();
		traceArea.setWidth("350px");
		traceArea.setHeight("400px");
		traceArea.setReadOnly(true);

		VerticalPanel leftPanel = new VerticalPanel();
		leftPanel.add(finderPanel);
		leftPanel.add(remoteExceptionTable);
		contentPanel.add(leftPanel);
		contentPanel.add(traceArea);
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
		grid.setWidget(row, 0, new Label(DateTimeFormat.getMediumDateFormat()
				.format(item.getErrorDate())));
		grid.setWidget(row, 1, new Label(item.getPhoneNumber() != null ? item
				.getPhoneNumber() : ""));
		grid.setWidget(row, 2, new Label(
				item.getDeviceIdentifier() != null ? item.getDeviceIdentifier()
						: ""));
		grid.setWidget(row, 3, new Label(
				item.getSoftwareVersion() != null ? item.getSoftwareVersion()
						: ""));
		HorizontalPanel hp = new HorizontalPanel();

		final Button ackButton = new Button("Acknowledge");
		final Button delButton = new Button("Delete");
		ClickHandler handler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(
								"Could not perform operation",
								"There was an error performing the operation, please try again. If the problem persists, contact an administrator.");
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
					remoteExceptionService.acknowledgeRemoteStacktrace(item
							.getKeyId(), callback);
				} else if (event.getSource() == delButton) {
					remoteExceptionService.deleteRemoteStacktrace(item
							.getKeyId(), callback);
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
								remoteExceptionTable.bindData(result
										.getPayload(),
										result.getCursorString(), isNew,
										isResort);
							}

							@Override
							public void onFailure(Throwable caught) {
								MessageDialog errDia = new MessageDialog(
										"Error listing exceptions",
										"Could not list remote exceptions. Please try again. If the problem persists, contact an administrator");
								errDia.showRelativeTo(remoteExceptionTable);

							}
						});

	}

	@Override
	public String getName() {
		return NAME;
	}

}
