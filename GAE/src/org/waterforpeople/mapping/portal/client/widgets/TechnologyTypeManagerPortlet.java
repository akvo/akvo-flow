package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.TechnologyTypeDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.TechnologyTypeService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.TechnologyTypeServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TechnologyTypeManagerPortlet extends Portlet {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String DESCRIPTION = TEXT_CONSTANTS
			.technologyTypeManagerPortletDescription();
	public static final String NAME = TEXT_CONSTANTS
			.technologyTypeManagerPortletTitle();

	private static final int WIDTH = 600;
	private static final int HEIGHT = 800;
	private VerticalPanel contentPane;
	private TechnologyTypeServiceAsync svc;
	// Search UI Elements
	private VerticalPanel mainVPanel = new VerticalPanel();
	private FlexTable entryTable = new FlexTable();
	private int savedRowId = 0;
	private int deleteRowId = 0;

	public TechnologyTypeManagerPortlet(String title, boolean scrollable,
			boolean configurable, int width, int height) {
		super(title, scrollable, configurable, width, height);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		contentPane.add(header);
		setContent(contentPane);
		svc = GWT.create(TechnologyTypeService.class);
		endpoint = (ServiceDefTarget) svc;
		endpoint
				.setServiceEntryPoint("/org.waterforpeople.mapping.portal.portal/technologytype");

	}

	private static final Boolean SCROLLABLE = true;
	private static final Boolean CONFIGURABLE = false;

	public TechnologyTypeManagerPortlet() {
		super(NAME, SCROLLABLE, CONFIGURABLE, WIDTH, HEIGHT);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		contentPane.add(header);
		setContent(contentPane);
		svc = GWT.create(TechnologyTypeService.class);
		endpoint = (ServiceDefTarget) svc;
		endpoint
				.setServiceEntryPoint("/org.waterforpeople.mapping.portal.portal/technologytype");

	}

	private ServiceDefTarget endpoint;

	@Override
	public String getName() {
		return NAME;
	}

	private Widget buildHeader() {
		svc = GWT.create(TechnologyTypeService.class);
		endpoint = (ServiceDefTarget) svc;
		endpoint
				.setServiceEntryPoint("/org.waterforpeople.mapping.portal.portal/technologytype");
		Grid grid = new Grid(2, 2);
		buildEntryScreen();
		grid.setWidget(0, 0, mainVPanel);
		mainVPanel.add(entryTable);

		return grid;
	}

	private void buildEntryScreen() {
		createHeaderRow();
		svc.list(new AsyncCallback<ArrayList<TechnologyTypeDto>>() {

			@Override
			public void onFailure(Throwable caught) {
				addControlButtons();
			}

			@Override
			public void onSuccess(ArrayList<TechnologyTypeDto> result) {
				int row = 1;
				for (TechnologyTypeDto item : result) {
					createDetailRow(item, row);
					row++;
				}
				addControlButtons();

			}

		});

	}

	private void addControlButtons() {
		Integer numRows = entryTable.getRowCount();
		Button addNewTechType = new Button(TEXT_CONSTANTS.add());

		addNewTechType.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// Add new row for data entry
				Integer numRows = entryTable.getRowCount();
				entryTable.insertRow(numRows - 1);
				createDetailRow(null, numRows - 1);
			}

		});
		entryTable.setWidget(numRows, 1, addNewTechType);
	}

	private void createHeaderRow() {
		entryTable.setWidget(0, 0, ViewUtil.initLabel(TEXT_CONSTANTS.code()));
		entryTable.setWidget(0, 1, ViewUtil.initLabel(TEXT_CONSTANTS.name()));
		entryTable.setWidget(0, 2, ViewUtil.initLabel(TEXT_CONSTANTS
				.description()));
	}

	private void createDetailRow(TechnologyTypeDto item, Integer row) {
		TextBox codeTB = new TextBox();
		if (item != null)
			codeTB.setText(item.getCode());
		entryTable.setWidget(row, 0, codeTB);

		TextBox nameTB = new TextBox();
		if (item != null)
			nameTB.setText(item.getName());
		entryTable.setWidget(row, 1, nameTB);

		TextBox descriptionTB = new TextBox();
		if (item != null)
			descriptionTB.setText(item.getDescription());
		entryTable.setWidget(row, 2, descriptionTB);

		Button saveButton = new Button("+");
		entryTable.setWidget(row, 3, saveButton);
		saveButton.setTitle(row.toString());

		Button deleteButton = new Button("-");
		entryTable.setWidget(row, 4, deleteButton);
		saveButton.setTitle(row.toString());
		deleteButton.setTitle(row.toString());
		Label idLabel = new Label();
		idLabel.setVisible(false);
		if (item != null) {
			idLabel.setText(item.getKeyId().toString());
		} else {
			idLabel.setText("-1");
		}
		entryTable.setWidget(row, 5, idLabel);

		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Button calledButton = (Button) event.getSource();
				Integer iRow = new Integer(calledButton.getTitle());
				savedRowId = iRow;
				TechnologyTypeDto techTypeDto = getObjectFromRow(iRow);
				svc.save(techTypeDto, new AsyncCallback<TechnologyTypeDto>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								TEXT_CONSTANTS.error(), TEXT_CONSTANTS
										.errorTracePrefix()
										+ " " + caught.getLocalizedMessage());
						errDialog.showRelativeTo(mainVPanel);
					}

					@Override
					public void onSuccess(TechnologyTypeDto result) {
						Label idLabel = (Label) entryTable.getWidget(
								savedRowId, 5);
						idLabel.setText(result.getKeyId().toString());
					}

				});
			}

		});

		deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Long id = 0l;
				Button calledButton = (Button) event.getSource();
				Integer iRow = new Integer(calledButton.getTitle());
				deleteRowId = iRow;
				id = new Long(((Label) entryTable.getWidget(iRow, 5)).getText());
				svc.delete(id, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								TEXT_CONSTANTS.error(), TEXT_CONSTANTS
										.errorTracePrefix()
										+ " " + caught.getLocalizedMessage());
						errDialog.showRelativeTo(mainVPanel);
					}

					@Override
					public void onSuccess(Void result) {
						entryTable.removeRow(deleteRowId);
					}

				});

			}

		});

	}

	private TechnologyTypeDto getObjectFromRow(Integer row) {
		Label idLabel = (Label) entryTable.getWidget(row, 5);
		TextBox codeTB = (TextBox) entryTable.getWidget(row, 0);
		TextBox nameTB = (TextBox) entryTable.getWidget(row, 1);
		TextBox descriptionTB = (TextBox) entryTable.getWidget(row, 2);
		Long id = new Long(idLabel.getText());
		TechnologyTypeDto techType = new TechnologyTypeDto();
		if (id > -1) {
			techType.setKeyId(id);
		}
		techType.setCode(codeTB.getText());
		techType.setName(nameTB.getText());
		techType.setDescription(descriptionTB.getText());
		return techType;
	}

}
