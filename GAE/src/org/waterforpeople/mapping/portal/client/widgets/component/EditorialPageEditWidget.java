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

package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageContentDto;
import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageDto;
import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageService;
import org.waterforpeople.mapping.app.gwt.client.editorial.EditorialPageServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Edits editorial pages and their corresponding EditorialPageContent items.
 * 
 * @author Christopher Fagiani
 * 
 */
public class EditorialPageEditWidget extends Composite implements ContextAware {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final String TEXT_AREA_WIDTH = "600px";
	private static final String TEXT_AREA_HEIGHT = "400px";
	private static final String TOP_ALIGN_LABEL_STYLE = "input-label-topalign";
	private static final String REORDER_BUTTON_CSS = "reorder-button";

	private Map<String, Object> bundle;
	private EditorialPageDto currentPage;
	private TextBox targetFileNameBox;
	private TextArea template;
	private VerticalPanel contentPanel;
	private VerticalPanel itemPanel;
	private FlexTable itemTable;

	private EditorialPageServiceAsync editorialService;

	public EditorialPageEditWidget() {

		editorialService = GWT.create(EditorialPageService.class);

		contentPanel = new VerticalPanel();
		itemPanel = new VerticalPanel();
		itemPanel.setVisible(false);
		itemTable = new FlexTable();
		itemPanel.add(itemTable);
		targetFileNameBox = new TextBox();
		Grid dataGrid = new Grid(2, 2);

		template = new TextArea();
		template.setWidth(TEXT_AREA_WIDTH);
		template.setHeight(TEXT_AREA_HEIGHT);
		ViewUtil.installGridRow(TEXT_CONSTANTS.fileName(), targetFileNameBox,
				dataGrid, 0, 0, TOP_ALIGN_LABEL_STYLE);
		ViewUtil.installGridRow(TEXT_CONSTANTS.templateText(), template,
				dataGrid, 1, 0, TOP_ALIGN_LABEL_STYLE);

		contentPanel.add(dataGrid);
		contentPanel.add(itemPanel);
		initWidget(contentPanel);
	}

	protected void populateFields(EditorialPageDto page) {
		if (page != null) {
			targetFileNameBox.setText(page.getTargetFileName());
			template.setText(page.getTemplate());
			if (page.getContentItems() != null
					&& page.getContentItems().size() > 0) {
				itemPanel.setVisible(true);
				for (EditorialPageContentDto item : page.getContentItems()) {
					installContentItemRow(item);
				}
			}
		}
	}

	@Override
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		if (bundle == null) {
			bundle = new HashMap<String, Object>();
		}
		if (doPopulation) {
			bundle.put(BundleConstants.EDITORIAL_PAGE, currentPage);
		}
		return bundle;
	}

	@Override
	public void flushContext() {
		// no-op
	}

	@Override
	public void persistContext(String buttonText, final CompletionListener listener) {
		currentPage.setTemplate(template.getValue());
		currentPage.setTargetFileName(targetFileNameBox.getValue());
		editorialService.saveEditorialPage(currentPage,
				new AsyncCallback<EditorialPageDto>() {

					@Override
					public void onFailure(Throwable caught) {
						if (listener != null) {
							listener.operationComplete(false, null);
						}

					}

					@Override
					public void onSuccess(EditorialPageDto result) {
						if (listener != null) {
							currentPage = result;
							listener.operationComplete(true,
									getContextBundle(true));
						}

					}
				});

	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		currentPage = (EditorialPageDto) bundle
				.get(BundleConstants.EDITORIAL_PAGE);
		if (currentPage != null) {
			fetchContentItems(currentPage);
		} else {
			currentPage = new EditorialPageDto();
		}
		flushContext();
	}

	/**
	 * loads the content items from the service and then populates the UI with
	 * the results
	 */
	private void fetchContentItems(EditorialPageDto page) {
		editorialService.listContentByPage(page.getKeyId(),
				new AsyncCallback<List<EditorialPageContentDto>>() {

					@Override
					public void onSuccess(List<EditorialPageContentDto> result) {
						currentPage.setContentItems(result);
						populateFields(currentPage);
					}

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						errDia.showCentered();
					}
				});
	}

	/**
	 * adds a row representing a EditorialPageContentDto to the UI. The rows
	 * support deletion as well as reordering.
	 * 
	 * @param opt
	 */
	private void installContentItemRow(EditorialPageContentDto contentItem) {
		int row = itemTable.getRowCount();
		itemTable.insertRow(row);
		TextBox heading = new TextBox();
		itemTable.setWidget(row, 0, heading);
		TextArea text = new TextArea();
		itemTable.setWidget(row, 1, text);
		HorizontalPanel bp = new HorizontalPanel();
		final Image moveUp = new Image("/images/greenuparrow.png");
		final Image moveDown = new Image("/images/greendownarrow.png");
		final Button deleteButton = new Button("Remove");

		ClickHandler optionClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				List<EditorialPageContentDto> itemList = currentPage
						.getContentItems();
				Cell cell = itemTable.getCellForEvent(event);
				if (event.getSource() == deleteButton) {
					itemTable.removeRow(cell.getRowIndex());
					itemList.remove(cell.getRowIndex());
				} else {
					int increment = 0;
					if (event.getSource() == moveUp && cell.getRowIndex() > 0) {
						increment = -1;
					} else if (event.getSource() == moveDown
							&& cell.getRowIndex() < itemList.size() - 1) {
						increment = 1;
					}
					if (increment != 0) {
						EditorialPageContentDto targetItem = itemList.get(cell
								.getRowIndex() + increment);
						EditorialPageContentDto movingItem = itemList.get(cell
								.getRowIndex());
						itemList.set(cell.getRowIndex() + increment, movingItem);
						itemList.set(cell.getRowIndex(), targetItem);
						targetItem.setSortOrder(targetItem.getSortOrder()
								- increment);
						movingItem.setSortOrder(movingItem.getSortOrder()
								+ increment);
						// now update the UI
						((TextBox) (itemTable.getWidget(cell.getRowIndex(), 0)))
								.setText(targetItem.getHeading());
						((TextBox) (itemTable.getWidget(cell.getRowIndex()
								+ increment, 0))).setText(movingItem
								.getHeading());
						((TextArea) (itemTable.getWidget(cell.getRowIndex(), 1)))
								.setText(targetItem.getText());
						((TextArea) (itemTable.getWidget(cell.getRowIndex()
								+ increment, 1))).setText(movingItem.getText());
					}
				}
			}
		};

		moveUp.setStylePrimaryName(REORDER_BUTTON_CSS);
		moveUp.addClickHandler(optionClickHandler);

		moveDown.setStylePrimaryName(REORDER_BUTTON_CSS);
		moveDown.addClickHandler(optionClickHandler);
		bp.add(moveUp);
		bp.add(moveDown);
		itemTable.setWidget(row, 3, bp);

		deleteButton.addClickHandler(optionClickHandler);
		itemTable.setWidget(row, 4, deleteButton);
		if (contentItem != null) {
			heading.setText(contentItem.getHeading());
			text.setText(contentItem.getText());
			if (contentItem.getSortOrder() == null) {
				contentItem.setSortOrder(new Long(row));
			}
		} else {
			if (currentPage.getContentItems() == null) {
				currentPage
						.setContentItems(new ArrayList<EditorialPageContentDto>());
			}
			EditorialPageContentDto dto = new EditorialPageContentDto();
			dto.setSortOrder(new Long(row));
			currentPage.getContentItems().add(dto);
		}
	}
}
