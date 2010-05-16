package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeService;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeServiceAsync;
import org.waterforpeople.mapping.app.web.client.dto.MappingDefinitionColumnContainer;
import org.waterforpeople.mapping.app.web.client.dto.MappingSpreadsheetColumnToAttribute;
import org.waterforpeople.mapping.app.web.client.dto.MappingSpreadsheetDefinition;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
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

public class MappingAttributeManager extends Portlet {
	public MappingAttributeManager(String title, boolean scrollable,
			boolean configurable, int width, int height) {
		super(title, scrollable, configurable, width, height);
		// TODO Auto-generated constructor stub
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

		RootPanel.get("content").add(mainHPanel);
		spreadsheetMappingTree
				.addSelectionHandler(new SelectionHandler<TreeItem>() {
					public void onSelection(SelectionEvent event) {
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
				svc.processSpreadsheet(mapDef, new AsyncCallback() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Object result) {
						colMapTable.setVisible(true);
						colMapStatusLabel.setVisible(false);
						colMapStatusLabel
								.setText("Please wait loading columns");

						Window.alert((String) result);

					}

				});

			}

		});

	}

	private void clearColumnMapTable() {
		colMapTable.removeAllRows();
	}

	private void setSpreadsheetCols() {
		svc.listSpreadsheetColumns(spreadSheetTextBox.getText().trim(),
				new AsyncCallback() {

					@Override
					public void onSuccess(Object result) {
						spreadsheetCols = (ArrayList<String>) result;

					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

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
						// TODO Auto-generated method stub

					}
				});
	}

	private ArrayList<String> spreadsheetCols = null;

	private void existingMapAyncCalls(String spreadsheetName) {
		svc.getMappingSpreadsheetDefinition(spreadsheetName,
				new AsyncCallback() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Object result) {
						if (result != null) {
							MappingDefinitionColumnContainer existingMapDef = (MappingDefinitionColumnContainer) result;
							loadColumnsAndAttributes(existingMapDef
									.getSpreadsheetColsList(), existingMapDef
									.getMapDef());
						}else{
							
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

	private MappingSpreadsheetDefinition existingMapDef = null;

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

		svc.saveSpreadsheetMapping(mapDef, new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Object result) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean getReadyForRemove() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void handleConfigClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleEvent(PortletEvent e) {
		// TODO Auto-generated method stub
		
	}
}
