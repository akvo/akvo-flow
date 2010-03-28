package org.waterforpeople.mapping.app.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
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

	private HorizontalPanel mainHPanel = new HorizontalPanel();
	private VerticalPanel mainVRightPanel = new VerticalPanel();
	private VerticalPanel mainVLeftPanel = new VerticalPanel();
	private VerticalPanel buttonVPanel = new VerticalPanel();
	private VerticalPanel mapVPanel = new VerticalPanel();
	private HorizontalPanel colMapHPanel = new HorizontalPanel();

	public void onModuleLoad() {
		loadSpreadsheetTree();
		spreadSheetTypeListBox.addItem("Google Spreadsheet");
		spreadSheetTypeListBox.addItem("Excel Spreadsheet");

		buttonVPanel.add(spreadsheetMapAddButton);
		buttonVPanel.add(spreadsheetMapDeleteButton);

		mainVLeftPanel.add(spreadsheetMappingTree);
		mainVLeftPanel.add(buttonVPanel);

		mainVRightPanel.add(spreadSheetTextBox);
		mainVRightPanel.add(spreadSheetTypeListBox);

		colMapHPanel.add(addColMapButton);
		colMapHPanel.add(deleteColMapButton);

		colMapTable.setText(0, 0, "Spreadsheet Columns");
		colMapTable.setText(0, 1, "Attribute List");

		for (int i = 1; i < 15; i++) {
			ListBox objectAttributeList = new ListBox();
			ListBox spreadsheetColumnList = new ListBox();

			spreadsheetColumnList.addItem("Date of Visit", "dateOfVisit");
			spreadsheetColumnList.addItem("Latitude", "latitude");
			spreadsheetColumnList.addItem("Longitude", "longitude");
			spreadsheetColumnList.addItem("Community Code", "communityCode");
			spreadsheetColumnList.setSelectedIndex(0);

			objectAttributeList.addItem("latitude", "latitude");
			objectAttributeList.addItem("longitude", "longitude");
			objectAttributeList.addItem("collection date", "collectionDate");
			objectAttributeList.addItem("photo url", "photoUrl");
			objectAttributeList.addItem("technology", "technology");
			objectAttributeList.addItem("photo caption", "photoCaption");
			objectAttributeList.setSelectedIndex(0);
			colMapTable.setWidget(i, 0, spreadsheetColumnList);
			colMapTable.setWidget(i, 1, objectAttributeList);
		}

		mapVPanel.add(colMapTable);
		mapVPanel.add(colMapHPanel);

		mainVRightPanel.add(mapVPanel);
		mainVRightPanel.add(saveSpreadsheetMapButton);

		saveSpreadsheetMapButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				StringBuilder sb = new StringBuilder();
				for (int i = 1; i < colMapTable.getRowCount(); i++) {
					ListBox colList = (ListBox) colMapTable.getWidget(i, 0);
					Integer spreadsheetColIndex = (colList).getSelectedIndex();
					
					String colValue = colList.getItemText(spreadsheetColIndex);

					ListBox attrList = (ListBox) colMapTable.getWidget(i, 1);
					Integer attrColIndex = (attrList).getSelectedIndex();
					String attrValue = colList.getItemText(attrColIndex);
					sb.append("row: " + i + " columnValue: " + colValue + " attributeValue: " + attrValue+"\n");
					
				}
				Window.alert(sb.toString());
			}

		});

		mainHPanel.add(mainVLeftPanel);
		mainHPanel.add(mainVRightPanel);

		RootPanel.get("content").add(mainHPanel);
		spreadsheetMappingTree
				.addSelectionHandler(new SelectionHandler<TreeItem>() {
					public void onSelection(SelectionEvent event) {
						Window.alert(((TreeItem) event.getSelectedItem())
								.getText());
					}
				});
	}

	private void loadSpreadsheetTree() {
		spreadsheetMappingTree.addItem("Spreadsheet 1");
		TreeItem outerRoot = new TreeItem("Item 1");
		outerRoot.addItem("Item 1-1");
		outerRoot.addItem("Item 1-2");
		outerRoot.addItem("Item 1-3");
		outerRoot.addItem(new CheckBox("Item 1-4"));

		TreeItem innerRoot = new TreeItem("Item 1-5");
		innerRoot.addItem("Item 1-5-1");
		innerRoot.addItem("Item 1-5-2");
		innerRoot.addItem("Item 1-5-3");
		innerRoot.addItem("Item 1-5-4");
		innerRoot.addItem(new CheckBox("Item 1-5-5"));

		outerRoot.addItem(innerRoot);
		spreadsheetMappingTree.addItem(outerRoot);
	}

}
