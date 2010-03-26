package org.waterforpeople.mapping.app.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;


public class MappingAttributeManager implements EntryPoint {
	private TextBox spreadSheetTextBox = new TextBox();
	private ListBox spreadSheetTypeListBox = new ListBox();
	private Tree spreadSheetMappingTree = new Tree();
	private FlexTable colMapTable = new FlexTable();
	private Button addColMapButton = new Button("+");
	private Button deleteColMapButton = new Button("-");
	private Button spreadsheetMapAddButton = new Button("+");
	private Button spreadsheetMapDeleteButton = new Button("-");
	private Button saveSpreadsheetMapButtion =new Button("save");
	
	private HorizontalPanel mainHPanel = new HorizontalPanel();
	private VerticalPanel mainVRightPanel = new VerticalPanel();
	private VerticalPanel mainVLeftPanel = new VerticalPanel();
	private VerticalPanel buttonVPanel = new VerticalPanel();
	private VerticalPanel mapVPanel = new VerticalPanel();
	private HorizontalPanel colMapHPanel =new HorizontalPanel();
	
	public void onModuleLoad() {
		
		spreadSheetTypeListBox.addItem("Google Spreadsheet");
		spreadSheetTypeListBox.addItem("Excel Spreadsheet");
		
		buttonVPanel.add(spreadsheetMapAddButton);
		buttonVPanel.add(spreadsheetMapDeleteButton);
		
		mainVLeftPanel.add(spreadSheetMappingTree);
		mainVLeftPanel.add(buttonVPanel);
		
		mainVRightPanel.add(spreadSheetTextBox);
		mainVRightPanel.add(spreadSheetTypeListBox);
		
		colMapHPanel.add(addColMapButton);
		colMapHPanel.add(deleteColMapButton);
		
		mapVPanel.add(colMapTable);
		mapVPanel.add(colMapHPanel);
		
		mainVRightPanel.add(mapVPanel);
		mainVRightPanel.add(saveSpreadsheetMapButtion);
		
		mainHPanel.add(mainVLeftPanel);
		mainHPanel.add(mainVRightPanel);
		
		RootPanel.get("content").add(mainHPanel);
		
	}

}
