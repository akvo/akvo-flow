package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.messaging.app.gwt.client.MessageDto;
import com.gallatinsystems.messaging.app.gwt.client.MessageService;
import com.gallatinsystems.messaging.app.gwt.client.MessageServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Widget for viewing and managing Message objects. This view will render the
 * messages in the system in descending order. Each action will have a set of 1
 * or more actions based on the message type.
 * 
 * @author Christopher Fagiani
 * 
 */
public class MessageViewWidget extends Composite implements
		DataTableListener<MessageDto>, DataTableBinder<MessageDto>,
		ClickHandler, ChangeHandler {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final String DEFAULT_SORT_FIELD = "lastUpdateDateTime";
	private static final DataTableHeader[] GRID_HEADERS = {
			new DataTableHeader(TEXT_CONSTANTS.id()),
			new DataTableHeader(TEXT_CONSTANTS.lastUpdated()),
			new DataTableHeader(TEXT_CONSTANTS.title()),
			new DataTableHeader(TEXT_CONSTANTS.message()),
			new DataTableHeader("") };
	private static final int PAGE_SIZE = 20;
	private static final String SURVEY_CHANGE = "surveyUpdate";
	private static final String SURVEY_ASSEMBLY = "surveyAssembly";

	private PaginatedDataTable<MessageDto> dataTable;
	private Panel contentPanel;
	private ListBox filterListBox;
	private MessageServiceAsync messageService;
	private SurveyServiceAsync surveyService;

	public MessageViewWidget() {
		messageService = GWT.create(MessageService.class);
		surveyService = GWT.create(SurveyService.class);
		contentPanel = new VerticalPanel();
		filterListBox = new ListBox();
		filterListBox.addItem("", "");
		filterListBox.addItem(TEXT_CONSTANTS.surveyUpdate(), SURVEY_CHANGE);
		filterListBox.addItem(TEXT_CONSTANTS.surveyAssembly(), SURVEY_ASSEMBLY);
		ViewUtil.installFieldRow(contentPanel, TEXT_CONSTANTS.filterResults(),
				filterListBox, null);
		dataTable = new PaginatedDataTable<MessageDto>(DEFAULT_SORT_FIELD,
				this, this, false);
		contentPanel.add(dataTable);
		initWidget(contentPanel);
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public DataTableHeader[] getHeaders() {
		return GRID_HEADERS;
	}

	@Override
	public void bindRow(Grid grid, final MessageDto item, int row) {
		grid.setWidget(row, 0, new Label(item.getKeyId().toString()));
		grid.setWidget(
				row,
				1,
				new Label(DateTimeFormat
						.getFormat(PredefinedFormat.DATE_MEDIUM).format(
								item.getLastUpdateDateTime())));
		grid.setWidget(row, 2,
				new Label(item.getObjectTitle() != null ? item.getObjectTitle()
						: ""));

		String message = item.getShortMessage();
		if (message == null) {
			item.getMessage();
		}
		if (SURVEY_CHANGE.equalsIgnoreCase(item.getActionAbout())) {
			message = TEXT_CONSTANTS.surveyChangeMessage();
		}
		if (message == null) {
			message = "";
		}
		grid.setWidget(row, 3, new Label(message));
		HorizontalPanel hp = new HorizontalPanel();

		final Button publishButton = new Button(TEXT_CONSTANTS.publishSurvey());
		final Button delButton = new Button(TEXT_CONSTANTS.delete());
		ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {			
				if (event.getSource() == publishButton) {
					surveyService.publishSurvey(item.getObjectId(),
							new AsyncCallback<String>() {

								@Override
								public void onFailure(Throwable caught) {
									MessageDialog errDia = new MessageDialog(
											TEXT_CONSTANTS.error(),
											TEXT_CONSTANTS.errorTracePrefix()
													+ " "
													+ caught.getLocalizedMessage());
									errDia.showCentered();
								}

								@Override
								public void onSuccess(String result) {
									deleteItem(item.getKeyId());
								}

							});
				} else if (event.getSource() == delButton) {
					deleteItem(item.getKeyId());
				}
			}
		};

		publishButton.addClickHandler(handler);
		delButton.addClickHandler(handler);
		if (SURVEY_CHANGE.equalsIgnoreCase(item.getActionAbout())) {
			hp.add(publishButton);
		}
		hp.add(delButton);
		grid.setWidget(row, 4, hp);
	}

	private void deleteItem(Long key) {
		messageService.deleteMessage(key, new AsyncCallback<Void>() {

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
				requestData(null, false);
			}
		});
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}

	@Override
	public void onItemSelected(MessageDto item) {
		// no-op

	}

	@Override
	public void requestData(String cursor, final boolean isResort) {
		final boolean isNew = (cursor == null);
		String filter = null;
		if (filterListBox.getSelectedIndex() > 0) {
			filterListBox.getValue(filterListBox.getSelectedIndex());
		}

		messageService.listMessages(filter, null, cursor,
				new AsyncCallback<ResponseDto<ArrayList<MessageDto>>>() {

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<MessageDto>> result) {
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

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == filterListBox) {
			requestData(null, false);
		}

	}
}
