package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingDefinitionColumnContainer;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingSpreadsheetColumnToAttribute;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingSpreadsheetDefinition;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeService;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MappingAttributeManager extends Portlet {

	public static final String DESCRIPTION = "Import Access Points from Google Doc";
	public static final String NAME = "Google Doc Access Point Importer";
	public static final String TITLE = "Import Access Points from Google Docs";
	private static final int WIDTH = 1600;
	private static final int HEIGHT = 800;
	private VerticalPanel contentPane = new VerticalPanel();
	@SuppressWarnings("unused")
	private ArrayList<String> spreadsheetCols = null;
	private ArrayList<String> objectAttributes = new ArrayList<String>();

	private Widget buildHeader() {

		onModuleLoad();

		return contentPane;
	}

	public MappingAttributeManager(String title, boolean scrollable,
			boolean configurable, int width, int height) {
		super(title, scrollable, configurable, width, height);
		setContent(buildHeader());
	}

	public MappingAttributeManager() {
		super(TITLE, true, false, WIDTH, HEIGHT);
		setContent(buildHeader());
	}

	private TextBox spreadSheetTextBox = new TextBox();
	private ListBox spreadSheetTypeListBox = new ListBox();
	private Tree spreadsheetMappingTree = new Tree();
	private FlexTable colMapTable = new FlexTable();
	private Button saveSpreadsheetMapButton = new Button("save");
	private Button getSpreadsheetColumnsButton = new Button(
			"Retrieve Spreadsheet Info");
	private Label spreadsheetNameLabel = new Label("Spreadsheet Name");
	private Button processSpreadsheetButton = new Button("Process Spreadsheet");
	private Label treeStatusLabel = new Label(
			"Please wait loading spreadsheets");
	private Label colMapStatusLabel = new Label("Please wait loading columns");

	private HorizontalPanel mainHPanel = new HorizontalPanel();
	private VerticalPanel mainVRightPanel = new VerticalPanel();
	private VerticalPanel mainVLeftPanel = new VerticalPanel();
	private VerticalPanel buttonVPanel = new VerticalPanel();
	private VerticalPanel mapVPanel = new VerticalPanel();
	private HorizontalPanel colMapHPanel = new HorizontalPanel();
	private HorizontalPanel spreadsheetNameHPanel = new HorizontalPanel();
	SpreadsheetMappingAttributeServiceAsync svc;

	public void onModuleLoad() {
		svc = (SpreadsheetMappingAttributeServiceAsync) GWT
				.create(SpreadsheetMappingAttributeService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) svc;
		endpoint
				.setServiceEntryPoint("/org.waterforpeople.mapping.portal.portal/spreadsheetattributemapperrpc");
		loadAttributes();
		svc.listSpreadsheetsFromFeed(null,
				new AsyncCallback<ArrayList<String>>() {
					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog("Error",
								"Cannot list spreadsheets. Will reauth with Google");
						errDia.showRelativeTo(processSpreadsheetButton);
						Window.open("/authsub", "_self", "");
					}

					@Override
					public void onSuccess(ArrayList<String> result) {
						loadSpreadsheetTree(result);
						treeStatusLabel.setVisible(false);
						spreadsheetMappingTree.setVisible(true);
					}
				});

		spreadSheetTypeListBox.addItem("Google Spreadsheet");
		spreadSheetTypeListBox.addItem("Excel Spreadsheet");

		colMapTable.setVisible(false);
		mainVLeftPanel.add(new Label("Available Spreadsheets"));
		mainVLeftPanel.add(treeStatusLabel);
		spreadsheetMappingTree.setVisible(false);
		mainVLeftPanel.add(spreadsheetMappingTree);

		mainVLeftPanel.add(buttonVPanel);

		spreadsheetNameHPanel.setVisible(false);
		spreadsheetNameHPanel.add(spreadsheetNameLabel);
		spreadsheetNameHPanel.add(spreadSheetTextBox);
		spreadsheetNameHPanel.add(spreadSheetTypeListBox);
		spreadsheetNameHPanel.add(getSpreadsheetColumnsButton);
		mainVRightPanel.add(spreadsheetNameHPanel);

		colMapTable.setText(0, 0, "Spreadsheet Columns");
		colMapTable.setText(0, 1, "Attribute List");
		colMapTable.setVisible(false);
		colMapStatusLabel.setVisible(false);
		mapVPanel.add(colMapStatusLabel);
		mapVPanel.add(colMapTable);
		mapVPanel.add(colMapHPanel);

		mainVRightPanel.add(mapVPanel);
		saveSpreadsheetMapButton.setVisible(false);
		mainVRightPanel.add(saveSpreadsheetMapButton);
		processSpreadsheetButton.setVisible(false);
		mainVRightPanel.add(processSpreadsheetButton);

		saveSpreadsheetMapButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				colMapTable.setVisible(false);
				colMapStatusLabel.setText("Please wait saving columns");
				colMapStatusLabel.setVisible(true);
				saveSpreadsheetMapping();
			}

		});

		mainHPanel.add(mainVLeftPanel);
		mainHPanel.add(mainVRightPanel);

		// RootPanel.get("content").add(mainHPanel);
		spreadsheetMappingTree
				.addSelectionHandler(new SelectionHandler<TreeItem>() {
					public void onSelection(SelectionEvent<TreeItem> event) {
						spreadsheetNameHPanel.setVisible(true);
						colMapStatusLabel.setVisible(true);
						colMapTable.setVisible(false);
						clearColumnMapTable();

						String spreadsheetName = ((TreeItem) event
								.getSelectedItem()).getText();
						spreadSheetTextBox.setText(spreadsheetName);
						// check and see if there is a map in the database
						// already for it
						loadExistingMap(spreadsheetName);
					}
				});

		getSpreadsheetColumnsButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (spreadSheetTextBox.getText() != null
						&& !spreadSheetTextBox.getText().trim().equals("")) {
					retrieveSpreadsheetCols();
				}

			}

		});

		processSpreadsheetButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				colMapTable.setVisible(false);
				colMapStatusLabel
						.setText("Please wait ingesting data from spreadsheet");
				colMapStatusLabel.setVisible(true);
				MappingSpreadsheetDefinition mapDef = new MappingSpreadsheetDefinition();
				mapDef.setSpreadsheetURL(spreadSheetTextBox.getText().trim());
				svc.processSpreadsheet(mapDef, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								"Error while processing sheet",
								"Could not process shpreadsheet. Please try again. If the problem persists, contact an administrator");
						errDialog.showRelativeTo(spreadSheetTextBox);
					}

					@Override
					public void onSuccess(String result) {
						colMapTable.setVisible(true);
						colMapStatusLabel.setVisible(false);
						colMapStatusLabel
								.setText("Please wait loading columns");

						Window.alert(result);

					}

				});

			}

		});
		contentPane.add(mainHPanel);
	}

	private void clearColumnMapTable() {
		colMapTable.removeAllRows();
	}

	@SuppressWarnings("unused")
	private void setSpreadsheetCols() {
		svc.listSpreadsheetColumns(spreadSheetTextBox.getText().trim(),
				new AsyncCallback<ArrayList<String>>() {

					@Override
					public void onSuccess(ArrayList<String> result) {
						spreadsheetCols = result;

					}

					@Override
					public void onFailure(Throwable caught) {

					}
				});
	}

	@SuppressWarnings("unchecked")
	private void retrieveSpreadsheetCols() {
		svc.listSpreadsheetColumns(spreadSheetTextBox.getText().trim(),
				new AsyncCallback() {

					@Override
					public void onSuccess(Object result) {
						spreadsheetCols = (ArrayList<String>) result;
						loadColumnsAndAttributes((ArrayList<String>) result,
								null);
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								"Error while loading data",
								"Could not load columns. Please try again. If the problem persists, contact an administrator");
						errDialog.showRelativeTo(spreadSheetTextBox);

					}
				});
	}

	private void existingMapAyncCalls(String spreadsheetName) {
		svc.getMappingSpreadsheetDefinition(spreadsheetName,
				new AsyncCallback<MappingDefinitionColumnContainer>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								"Error while fetching data",
								"Could not get spreadsheet mapping. Please try again. If the problem persists, contact an administrator");
						errDialog.showRelativeTo(mainHPanel);
					}

					@Override
					public void onSuccess(
							MappingDefinitionColumnContainer result) {
						if (result != null) {
							MappingDefinitionColumnContainer existingMapDef = result;
							loadColumnsAndAttributes(existingMapDef
									.getSpreadsheetColsList(), existingMapDef
									.getMapDef());
						} else {

							colMapStatusLabel.setText("No Existing Map Found");
							colMapStatusLabel.setVisible(false);
							retrieveSpreadsheetCols();
						}

					}

				});

	}

	private void loadExistingMap(String spreadsheetName) {
		existingMapAyncCalls(spreadsheetName);

	}

	private void loadSpreadsheetTree(ArrayList<String> spreadsheetList) {
		TreeItem outerRoot = new TreeItem("Google Docs Spreadsheets");
		spreadsheetMappingTree.addItem(outerRoot);
		for (String item : spreadsheetList) {
			outerRoot.addItem(item);
		}
	}

	private void loadColumnsAndAttributes(ArrayList<String> cols,
			MappingSpreadsheetDefinition existingMap) {
		colMapTable.setWidget(0, 0, new Label("Spreadsheet Columns"));
		colMapTable.setWidget(0, 1, new Label("Database Column"));

		for (int i = 0; i < cols.size(); i++) {
			ListBox objectAttributeList = new ListBox();
			ListBox spreadsheetColumnList = new ListBox();
			String columnName = null;
			for (String colItem : cols) {
				spreadsheetColumnList.addItem(colItem);
			}
			spreadsheetColumnList.setSelectedIndex(i);
			columnName = spreadsheetColumnList.getItemText(i);
			int j = 0;
			int iMatchCol = 0;
			for (String item : objectAttributes) {
				objectAttributeList.addItem(item, item);
				if (item.trim().toLowerCase().equals(
						columnName.trim().toLowerCase())) {
					iMatchCol = j;
				}

				if (existingMap != null) {
					for (MappingSpreadsheetColumnToAttribute itemExisting : existingMap
							.getColumnMap()) {
						if (columnName.trim().toLowerCase().equals(
								itemExisting.getSpreadsheetColumn().trim()
										.toLowerCase())
								&& item.trim().toLowerCase().equals(
										itemExisting.getObjectAttribute()
												.trim().toLowerCase())) {
							iMatchCol = j;
						}
					}
				}
				// } else if ((Integer iMatchCol=patternMatch(columnName))>=0) {
				//					
				// }

				j++;
			}
			objectAttributeList.setSelectedIndex(iMatchCol);
			colMapTable.setWidget(i + 1, 0, spreadsheetColumnList);
			colMapTable.setWidget(i + 1, 1, objectAttributeList);
		}
		colMapStatusLabel.setVisible(false);
		colMapTable.setVisible(true);
		saveSpreadsheetMapButton.setVisible(true);
	}

	private ArrayList<String> loadAttributes() {
		svc.listObjectAttributes(null, new AsyncCallback<ArrayList<String>>() {
			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDialog = new MessageDialog(
						"Error while fetching data",
						"Could not list annotations. Please try again. If the problem persists, contact an administrator");
				errDialog.showRelativeTo(mainHPanel);
			}

			@Override
			public void onSuccess(ArrayList<String> result) {
				objectAttributes = result;

			}

		});
		return null;
	}

	private void saveSpreadsheetMapping() {

		MappingSpreadsheetDefinition mapDef = new MappingSpreadsheetDefinition();
		mapDef.setSpreadsheetURL(spreadSheetTextBox.getText().trim());
		ArrayList<MappingSpreadsheetColumnToAttribute> columnMap = new ArrayList<MappingSpreadsheetColumnToAttribute>();

		for (int i = 1; i < colMapTable.getRowCount(); i++) {
			MappingSpreadsheetColumnToAttribute item = new MappingSpreadsheetColumnToAttribute();
			ListBox colList = (ListBox) colMapTable.getWidget(i, 0);
			Integer spreadsheetColIndex = (colList).getSelectedIndex();

			String colValue = colList.getItemText(spreadsheetColIndex);

			ListBox attrList = (ListBox) colMapTable.getWidget(i, 1);
			Integer attrColIndex = (attrList).getSelectedIndex();
			String attrValue = attrList.getItemText(attrColIndex);
			item.setSpreadsheetColumn(colValue);
			item.setObjectAttribute(attrValue);

			columnMap.add(item);

		}
		mapDef.setColumnMap(columnMap);

		svc.saveSpreadsheetMapping(mapDef, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDialog = new MessageDialog(
						"Error while saving",
						"Could not save. Please try again. If the problem persists, contact an administrator");
				errDialog.showRelativeTo(mainHPanel);
			}

			@Override
			public void onSuccess(Void result) {
				colMapTable.setVisible(true);
				colMapStatusLabel.setVisible(false);
				colMapStatusLabel.setText("Please wait loading columns");

				Window.alert("Spreadsheet Mapping successfully saved");
				processSpreadsheetButton.setVisible(true);
			}

		});

	}

	@Override
	public String getName() {
		return NAME;
	}

}
