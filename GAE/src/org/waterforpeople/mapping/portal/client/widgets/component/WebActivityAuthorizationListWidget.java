package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationDto;
import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationService;
import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.PermissionConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.Orientation;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveySelectionWidget.TerminalType;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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
 * lists all existing web authorizations for the purpose of editing/deleting
 * them.
 * 
 * @author Christopher Fagiani
 * 
 */
public class WebActivityAuthorizationListWidget extends Composite implements
		DataTableBinder<WebActivityAuthorizationDto>,
		DataTableListener<WebActivityAuthorizationDto>, ContextAware {

	private static Integer PAGE_SIZE = 20;
	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader("Id", "key", false),
			new DataTableHeader("Name", "name", false),
			new DataTableHeader("Token", "token", false),
			new DataTableHeader("Expires", "expiry", false),
			new DataTableHeader("Edit/Delete") };
	private static final String DEFAULT_SORT_FIELD = "key";

	private PaginatedDataTable<WebActivityAuthorizationDto> authTable;
	private WebActivityAuthorizationServiceAsync authService;
	private DateTimeFormat dateFormat;
	private UserDto user;
	private SurveySelectionWidget surveySelector;
	private Panel contentPanel;
	private PageController controller;
	private Map<String, Object> bundle;

	public WebActivityAuthorizationListWidget(PageController controller,
			UserDto user) {
		authTable = new PaginatedDataTable<WebActivityAuthorizationDto>(
				DEFAULT_SORT_FIELD, this, this, false);
		this.user = user;
		this.controller = controller;
		authService = GWT.create(WebActivityAuthorizationService.class);
		dateFormat = DateTimeFormat.getShortDateFormat();
		contentPanel = new VerticalPanel();
		surveySelector = new SurveySelectionWidget(Orientation.HORIZONTAL,
				TerminalType.SURVEY);
		CaptionPanel selectorPanel = new CaptionPanel("Filter Results");
		selectorPanel.add(surveySelector);
		contentPanel.add(selectorPanel);
		contentPanel.add(authTable);
		initWidget(contentPanel);
	}

	@Override
	public void bindRow(final Grid grid,
			final WebActivityAuthorizationDto authDto, int row) {
		Label keyIdLabel = new Label(authDto.getKeyId().toString());
		grid.setWidget(row, 0, keyIdLabel);
		if (authDto.getName() != null && authDto.getName().trim().length() > 0) {
			grid.setWidget(row, 1, new Label(authDto.getName()));
		} else {
			grid.setWidget(row, 1, new Label("UNNAMED"));
		}
		grid.setWidget(row, 2, new Label(authDto.getToken()));
		if (authDto.getExpirationDate() != null) {
			grid.setWidget(row, 3, new Label(dateFormat.format(authDto
					.getExpirationDate())));
		}

		Button editButton = new Button("Edit");
		Button deleteButton = new Button("Delete");
		HorizontalPanel buttonHPanel = new HorizontalPanel();
		buttonHPanel.add(editButton);
		buttonHPanel.add(deleteButton);
		if (!user.hasPermission(PermissionConstants.EDIT_TOKENS)) {
			buttonHPanel.setVisible(false);
		}

		editButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (controller != null) {					
					bundle.put(BundleConstants.WEB_ACTIVITY_AUTH, authDto);
					controller.openPage(
							SurveyWebActivityAuthorizationEditWidget.class,
							bundle);
				}
			}
		});

		deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final MessageDialog waitDialog = new MessageDialog(
						"Deleting...", "Please wait");
				waitDialog.showCentered();
				authService.deleteAuthorization(authDto,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								waitDialog.hide();
								MessageDialog errDia = new MessageDialog(
										"Error", "Could not delete record: "
												+ caught.getLocalizedMessage());
								errDia.showCentered();
							}

							@Override
							public void onSuccess(Void result) {
								waitDialog.hide();
								requestData(null, false);
							}
						});
			}
		});
		grid.setWidget(row, 4, buttonHPanel);
	}

	@Override
	public DataTableHeader[] getHeaders() {
		return HEADERS;
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}

	@Override
	public void onItemSelected(WebActivityAuthorizationDto item) {
		// no-op
	}

	@Override
	public void requestData(String cursor, final boolean isResort) {
		final boolean isNew = (cursor == null);
		authService
				.listAuthorizations(
						cursor,
						new AsyncCallback<ResponseDto<ArrayList<WebActivityAuthorizationDto>>>() {

							@Override
							public void onSuccess(
									ResponseDto<ArrayList<WebActivityAuthorizationDto>> result) {
								if (result != null
										&& result.getPayload() != null
										&& result.getPayload().size() > 0) {
									authTable.bindData(result.getPayload(),
											result.getCursorString(), isNew,
											isResort);
								} else {
									authTable.bindData(null, null, isNew,
											isResort);
								}
							}

							@Override
							public void onFailure(Throwable caught) {

								MessageDialog errDia = new MessageDialog(
										"Error",
										"Could not list authorizations: "
												+ caught.getLocalizedMessage());
								errDia.showCentered();
							}
						});

	}

	@Override
	public void flushContext() {
		if(bundle != null){
			bundle.remove(BundleConstants.WEB_ACTIVITY_AUTH);
		}
	}

	@Override
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		return bundle;
	}

	@Override
	public void persistContext(CompletionListener listener) {
		listener.operationComplete(true, bundle);
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		if(bundle != null){
			this.bundle = bundle;
		}else{
			this.bundle = new HashMap<String,Object>();
		}
		
		requestData(null, false);
	}
}
