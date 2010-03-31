package org.waterforpeople.mapping.app.web.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.waterforpeople.mapping.app.web.client.dto.MappingSpreadsheetColumnToAttribute;
import org.waterforpeople.mapping.app.web.client.dto.MappingSpreadsheetDefinition;

import com.google.gwt.core.client.EntryPoint;
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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MappingAttributeManager implements EntryPoint {
	private TextBox spreadSheetTextBox = new TextBox();
	private ListBox spreadSheetTypeListBox = new ListBox();
	private Tree spreadsheetMappingTree = new Tree();
	private FlexTable colMapTable = new FlexTable();
	private Button addColMapButton = new Button("+");
	private Button deleteColMapButton = new Button("-");
	private Button spreadsheetMapAddButton = new Button("+");
	private Button spreadsheetMapDeleteButton = new Button("-");
	private Button saveSpreadsheetMapButton = new Button("save");
	private Button getSpreadsheetColumnsButton = new Button(
			"Retrieve Spreadsheet Info");
	private Label spreadsheetNameLabel = new Label("Spreadsheet Name");
	private Button processSpreadsheetButton = new Button("Process Spreadsheet");

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
		endpoint.setServiceEntryPoint("/spreadsheetattributemapper");
		loadAttributes();
		svc.listSpreadsheetsFromFeed(null, new AsyncCallback() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Object result) {
				loadSpreadsheetTree((ArrayList<String>) result);
			}
		});

		spreadSheetTypeListBox.addItem("Google Spreadsheet");
		spreadSheetTypeListBox.addItem("Excel Spreadsheet");

		buttonVPanel.add(spreadsheetMapAddButton);
		buttonVPanel.add(spreadsheetMapDeleteButton);
		colMapTable.setVisible(false);
		mainVLeftPanel.add(new Label("Available Spreadsheets"));
		mainVLeftPanel.add(spreadsheetMappingTree);

		mainVLeftPanel.add(buttonVPanel);

		spreadsheetNameHPanel.setVisible(false);
		spreadsheetNameHPanel.add(spreadsheetNameLabel);
		spreadsheetNameHPanel.add(spreadSheetTextBox);
		spreadsheetNameHPanel.add(spreadSheetTypeListBox);
		spreadsheetNameHPanel.add(getSpreadsheetColumnsButton);
		mainVRightPanel.add(spreadsheetNameHPanel);

		colMapHPanel.add(addColMapButton);
		colMapHPanel.add(deleteColMapButton);

		colMapTable.setText(0, 0, "Spreadsheet Columns");
		colMapTable.setText(0, 1, "Attribute List");

		mapVPanel.add(colMapTable);
		mapVPanel.add(colMapHPanel);

		mainVRightPanel.add(mapVPanel);
		mainVRightPanel.add(saveSpreadsheetMapButton);
		processSpreadsheetButton.setVisible(false);
		mainVRightPanel.add(processSpreadsheetButton);

		saveSpreadsheetMapButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				saveSpreadsheetMapping();
			}

		});

		mainHPanel.add(mainVLeftPanel);
		mainHPanel.add(mainVRightPanel);

		RootPanel.get("content").add(mainHPanel);
		spreadsheetMappingTree
				.addSelectionHandler(new SelectionHandler<TreeItem>() {
					public void onSelection(SelectionEvent event) {
						spreadsheetNameHPanel.setVisible(true);
						spreadSheetTextBox.setText(((TreeItem) event
								.getSelectedItem()).getText());
					}
				});

		spreadsheetMapAddButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				spreadsheetNameHPanel.setVisible(true);

			}

		});

		getSpreadsheetColumnsButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (spreadSheetTextBox.getText() != null
						&& !spreadSheetTextBox.getText().trim().equals("")) {
					svc.listSpreadsheetColumns(spreadSheetTextBox.getText()
							.trim(), new AsyncCallback() {

						@Override
						public void onSuccess(Object result) {
							loadColumnsAndAttributes((ArrayList<String>) result);
						}

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}
					});
				}

			}

		});

		processSpreadsheetButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MappingSpreadsheetDefinition mapDef = new MappingSpreadsheetDefinition();
				mapDef.setSpreadsheetURL(spreadSheetTextBox.getText().trim());
				svc.processSpreadsheet(mapDef, new AsyncCallback() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Object result) {
						Window.alert("Spreadsheet Processed");

					}

				});

			}

		});

	}

	private void loadSpreadsheetTree(ArrayList<String> spreadsheetList) {
		TreeItem outerRoot = new TreeItem("Google Docs Spreadsheets");
		spreadsheetMappingTree.addItem(outerRoot);
		for (String item : spreadsheetList) {
			outerRoot.addItem(item);
		}
	}

	private void loadColumnsAndAttributes(ArrayList<String> cols) {
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
				// } else if ((Integer iMatchCol=patternMatch(columnName))>=0) {
				//					
				// }
				
				j++;
			}
			objectAttributeList.setSelectedIndex(iMatchCol);
			colMapTable.setWidget(i + 1, 0, spreadsheetColumnList);
			colMapTable.setWidget(i + 1, 1, objectAttributeList);
		}
		colMapTable.setVisible(true);
	}

	private ArrayList<String> objectAttributes = new ArrayList<String>();

	private Integer patternMatch(String colItem) {

		return -1;
	}

	private ArrayList<String> loadAttributes() {
		svc.listObjectAttributes(null, new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Object result) {
				objectAttributes = (ArrayList<String>) result;

			}

		});
		return null;
	}

	private void saveSpreadsheetMapping() {

		MappingSpreadsheetDefinition mapDef = new MappingSpreadsheetDefinition();
		mapDef.setSpreadsheetURL(spreadSheetTextBox.getText().trim());
		HashMap<String, MappingSpreadsheetColumnToAttribute> columnMap = new HashMap<String, MappingSpreadsheetColumnToAttribute>();

		for (int i = 1; i < colMapTable.getRowCount(); i++) {
			MappingSpreadsheetColumnToAttribute item = new MappingSpreadsheetColumnToAttribute();
			ListBox colList = (ListBox) colMapTable.getWidget(i, 0);
			Integer spreadsheetColIndex = (colList).getSelectedIndex();

			String colValue = colList.getItemText(spreadsheetColIndex);

			ListBox attrList = (ListBox) colMapTable.getWidget(i, 1);
			Integer attrColIndex = (attrList).getSelectedIndex();
			String attrValue = colList.getItemText(attrColIndex);
			item.setSpreadsheetColumn(colValue);
			item.setObjectAttribute(attrValue);

			columnMap.put(colValue, item);

		}
		mapDef.setColumnMap(columnMap);

		svc.saveSpreadsheetMapping(mapDef, new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Object result) {
				Window.alert("Spreadsheet Mapping successfully saved");
				processSpreadsheetButton.setVisible(true);
			}

		});

	}
}
