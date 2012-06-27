/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingDefinitionColumnContainer;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingSpreadsheetColumnToAttribute;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingSpreadsheetDefinition;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeService;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
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

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);	
	private static final int WIDTH = 1600;
	private static final int HEIGHT = 800;
	
	public static final String NAME = TEXT_CONSTANTS.mappingAttributeManagerTitle();
	
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
		super(TEXT_CONSTANTS.mappingAttributeManagerTitle(), true, false,
				WIDTH, HEIGHT);
		setContent(buildHeader());
	}

	private TextBox spreadSheetTextBox = new TextBox();
	private ListBox spreadSheetTypeListBox = new ListBox();
	private Tree spreadsheetMappingTree = new Tree();
	private FlexTable colMapTable = new FlexTable();
	private Button saveSpreadsheetMapButton = new Button(TEXT_CONSTANTS.save());
	private Button getSpreadsheetColumnsButton = new Button(TEXT_CONSTANTS.retrieveSpreadsheetData());			
	private Label spreadsheetNameLabel = ViewUtil.initLabel(TEXT_CONSTANTS
			.spreadsheetName());
	private Button processSpreadsheetButton = new Button(TEXT_CONSTANTS
			.processSpreadsheet());
	private Label treeStatusLabel = ViewUtil.initLabel(TEXT_CONSTANTS
			.pleaseWait());
	private Label colMapStatusLabel = ViewUtil.initLabel(TEXT_CONSTANTS
			.pleaseWait());

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
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						errDia.showCentered();
						Window.open("/authsub", "_self", "");
					}

					@Override
					public void onSuccess(ArrayList<String> result) {
						loadSpreadsheetTree(result);
						treeStatusLabel.setVisible(false);
						spreadsheetMappingTree.setVisible(true);
					}
				});

		spreadSheetTypeListBox.addItem(TEXT_CONSTANTS.googleSpreadsheet());
		spreadSheetTypeListBox.addItem(TEXT_CONSTANTS.excelSpreadsheet());

		colMapTable.setVisible(false);
		mainVLeftPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS
				.avaialbleSpreadsheets()));
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

		colMapTable.setText(0, 0, TEXT_CONSTANTS.spreadsheetColumns());
		colMapTable.setText(0, 1, TEXT_CONSTANTS.attributeList());
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
				colMapStatusLabel.setText(TEXT_CONSTANTS.pleaseWait());
				colMapStatusLabel.setVisible(true);
				saveSpreadsheetMapping();
			}

		});

		mainHPanel.add(mainVLeftPanel);
		mainHPanel.add(mainVRightPanel);

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
				colMapStatusLabel.setText(TEXT_CONSTANTS.pleaseWait());
				colMapStatusLabel.setVisible(true);
				MappingSpreadsheetDefinition mapDef = new MappingSpreadsheetDefinition();
				mapDef.setSpreadsheetURL(spreadSheetTextBox.getText().trim());
				svc.processSpreadsheet(mapDef, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								TEXT_CONSTANTS.error(), TEXT_CONSTANTS
										.errorTracePrefix()
										+ " " + caught.getLocalizedMessage());
						errDialog.showCentered();
					}

					@Override
					public void onSuccess(String result) {
						colMapTable.setVisible(true);
						colMapStatusLabel.setVisible(false);
						colMapStatusLabel.setText(TEXT_CONSTANTS.pleaseWait());
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

	private void retrieveSpreadsheetCols() {
		svc.listSpreadsheetColumns(spreadSheetTextBox.getText().trim(),
				new AsyncCallback<ArrayList<String>>() {

					@Override
					public void onSuccess(ArrayList<String> result) {
						spreadsheetCols = (ArrayList<String>) result;
						loadColumnsAndAttributes((ArrayList<String>) result,
								null);
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								TEXT_CONSTANTS.error(), TEXT_CONSTANTS
										.errorTracePrefix()
										+ " " + caught.getLocalizedMessage());
						errDialog.showCentered();
					}
				});
	}

	private void existingMapAyncCalls(String spreadsheetName) {
		svc.getMappingSpreadsheetDefinition(spreadsheetName,
				new AsyncCallback<MappingDefinitionColumnContainer>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								TEXT_CONSTANTS.error(), TEXT_CONSTANTS
										.errorTracePrefix()
										+ " " + caught.getLocalizedMessage());
						errDialog.showCentered();
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
							colMapStatusLabel.setText(TEXT_CONSTANTS
									.noMapFound());
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
		TreeItem outerRoot = new TreeItem(TEXT_CONSTANTS.googleSpreadsheet());
		spreadsheetMappingTree.addItem(outerRoot);
		for (String item : spreadsheetList) {
			outerRoot.addItem(item);
		}
	}

	private void loadColumnsAndAttributes(ArrayList<String> cols,
			MappingSpreadsheetDefinition existingMap) {
		colMapTable.setWidget(0, 0, ViewUtil.initLabel(TEXT_CONSTANTS.spreadsheetColumns()));
		colMapTable.setWidget(0, 1, ViewUtil.initLabel(TEXT_CONSTANTS.databaseColumn()));

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
						TEXT_CONSTANTS.error(), TEXT_CONSTANTS
								.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
				errDialog.showCentered();
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
						TEXT_CONSTANTS.error(), TEXT_CONSTANTS
								.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
				errDialog.showCentered();
			}

			@Override
			public void onSuccess(Void result) {
				colMapTable.setVisible(true);
				colMapStatusLabel.setVisible(false);
				colMapStatusLabel.setText(TEXT_CONSTANTS.pleaseWait());
				Window.alert(TEXT_CONSTANTS.saveComplete());
				processSpreadsheetButton.setVisible(true);
			}
		});
	}

	@Override
	public String getName() {
		return TEXT_CONSTANTS.mappingAttributeManagerTitle();
	}
}
