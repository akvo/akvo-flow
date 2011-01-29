package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.displaytemplate.DisplayTemplateManagerService;
import org.waterforpeople.mapping.app.gwt.client.displaytemplate.DisplayTemplateManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.displaytemplate.DisplayTemplateMappingDto;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DisplayContentManager extends LocationDrivenPortlet {
	public static final String DESCRIPTION = "Create/Edit/Delete Display Mapping";
	public static final String NAME = "Display Mapping Manager";

	private static final int WIDTH = 1600;
	private static final int HEIGHT = 800;
	private VerticalPanel contentPane;
	private static final String title = "Display Template Content Manager";
	private static final Boolean scrollable = true;
	private static final Boolean configurable = false;

	private DisplayTemplateManagerServiceAsync svc;

	// Search UI Elements
	private VerticalPanel mainVPanel = new VerticalPanel();
	FlexTable dspEntryTable = new FlexTable();

	public DisplayContentManager() {
		super(title, scrollable, configurable, false, WIDTH, HEIGHT, null,
				false, "");
	}

	public DisplayContentManager(String title, boolean scrollable,
			boolean configurable, int width, int height, UserDto user,
			boolean useCommunity, String specialOption) {
		super(title, scrollable, configurable, false, width, height, user,
				useCommunity, specialOption);
	}

	public DisplayContentManager(UserDto user) {
		super(NAME, true, false, false, WIDTH, HEIGHT, user, true,
				LocationDrivenPortlet.ANY_OPT);
		loadAttributes();
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		contentPane.add(header);
		setContent(contentPane);
		svc = GWT.create(DisplayTemplateManagerService.class);
	}

	private Widget buildHeader() {
		contentPane = new VerticalPanel();

		buildEntryTable();
		return contentPane;
	}

	@Override
	public String getName() {
		return NAME;
	}

	private void buildEntryTable() {
		buildColumnHeaders();
	}

	private void buildColumnHeaders() {
		svc.getLabels(new AsyncCallback<ArrayList<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDialog = new MessageDialog(
						"Error while fetching data",
						"Could not list labels. Please try again. If the problem persists, contact an administrator");
				errDialog.showRelativeTo(mainVPanel);
			}

			@Override
			public void onSuccess(ArrayList<String> result) {
				int row = 0;
				int column = 0;
				for (String item : result) {
					dspEntryTable.setWidget(row, column++, new Label(item));
				}
				buildTableDetails();
			}

		});
	}

	private void buildTableDetails() {
		svc.getRows(new AsyncCallback<ArrayList<DisplayTemplateMappingDto>>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDialog = new MessageDialog(
						"Error while fetching data",
						"Could not list rows. Please try again. If the problem persists, contact an administrator");
				errDialog.showRelativeTo(mainVPanel);
			}

			@Override
			public void onSuccess(ArrayList<DisplayTemplateMappingDto> result) {
				Integer row = 1;
				for (DisplayTemplateMappingDto rowItem : result) {
					addDetailRow(rowItem, row);
				}
				Button addRow = new Button("Add New");
				dspEntryTable.setWidget(row, 1, addRow);
				mainVPanel.add(dspEntryTable);
			}
		});
	}

	private Integer savedRow = null;

	private void addDetailRow(DisplayTemplateMappingDto item, Integer row) {
		TextBox displayOrderTB = new TextBox();
		dspEntryTable.setWidget(row, 0, displayOrderTB);
		TextBox descTB = new TextBox();
		dspEntryTable.setWidget(row, 1, descTB);
		ListBox attributesLB = new ListBox();
		for (String itemAttr : objectAttributesList) {
			attributesLB.addItem(itemAttr);
		}
		dspEntryTable.setWidget(row, 2, attributesLB);
		Button saveButton = new Button("+");
		saveButton.setTitle(row.toString());
		dspEntryTable.setWidget(row, 3, saveButton);
		Button deleteButton = new Button("-");
		dspEntryTable.setWidget(row, 4, deleteButton);
		Label idLabel = new Label();
		idLabel.setVisible(false);
		dspEntryTable.setWidget(row, 5, idLabel);

		if (item != null) {
			idLabel.setText(item.getKeyId().toString());
			displayOrderTB.setText(item.getDisplayOrder().toString());
			descTB.setText(item.getRowDescription());
			for (int i = 0; i < attributesLB.getItemCount(); i++) {
				String itemText = attributesLB.getItemText(i);
				if (itemText.equals(item.getAttributeName()))
					attributesLB.setSelectedIndex(i);
			}
		}

		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Button calledButton = (Button) event.getSource();
				Integer iRow = new Integer(calledButton.getTitle());
				savedRow = iRow;
				DisplayTemplateMappingDto item = null;
				svc.save(item, new AsyncCallback<DisplayTemplateMappingDto>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								"Error while saving",
								"Could not save. Please try again. If the problem persists, contact an administrator");
						errDialog.showRelativeTo(mainVPanel);
					}

					@Override
					public void onSuccess(DisplayTemplateMappingDto result) {
						Label idLabel = (Label) dspEntryTable.getWidget(
								savedRow, 5);
						idLabel.setText(result.getKeyId().toString());
					}

				});
			}

		});

		deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Button calledButton = (Button) event.getSource();
				Integer iRow = new Integer(calledButton.getTitle());
				savedRow = iRow;
				Long keyId = new Long(
						((Label) dspEntryTable.getWidget(iRow, 5)).getText());
				svc.delete(keyId, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								"Error while deleting",
								"Could not delete. Please try again. If the problem persists, contact an administrator");
						errDialog.showRelativeTo(mainVPanel);
					}

					@Override
					public void onSuccess(Void result) {
						dspEntryTable.removeRow(savedRow);

					}

				});
			}

		});

		row++;
	}

	private ArrayList<String> objectAttributesList = null;

	private void loadAttributes() {
		svc.listObjectAttributes("AccessPoint",
				new AsyncCallback<ArrayList<String>>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								"Error while fetching data",
								"Could not list access point annotations. Please try again. If the problem persists, contact an administrator");
						errDialog.showRelativeTo(mainVPanel);

					}

					@Override
					public void onSuccess(ArrayList<String> result) {
						objectAttributesList = result;
						objectAttributesList.add(" ");
					}

				});
	}
}
