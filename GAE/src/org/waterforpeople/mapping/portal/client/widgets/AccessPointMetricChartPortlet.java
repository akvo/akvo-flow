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

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointMetricSummaryDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointMetricSummaryService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointMetricSummaryServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.ImagePieChart;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;

/**
 * Portlet for displaying Access Point Metric values for a selected region as a
 * pie chart.
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointMetricChartPortlet extends LocationDrivenPortlet
		implements ChangeHandler {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	public static final String DESCRIPTION = TEXT_CONSTANTS
			.accessPointMetricChartDescription();
	public static final String NAME = TEXT_CONSTANTS
			.accessPointMetricChartTitle();
	private static final String CONFIG_ITEM_NAME = "APMetricChart";

	private static final String TECH_METRIC = "technologyTypeString";
	private static final String TYPE_METRIC = "pointType";
	private static final int WIDTH = 400;
	private static final int HEIGHT = 400;
	private ListBox metricListbox;
	private Panel contentPane;
	private PieChart pieChart;
	private Label noDataLabel;
	private AccessPointMetricSummaryServiceAsync apMetricService;

	private AbstractDataTable currentTable;
	private String selectedMetric;

	public AccessPointMetricChartPortlet(UserDto user) {
		super(NAME, false, false, false, WIDTH, HEIGHT, user, false, 3,
				LocationDrivenPortlet.ALL_OPT);
		apMetricService = GWT.create(AccessPointMetricSummaryService.class);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		contentPane.add(header);
		setContent(contentPane);
	}

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * constructs and installs the menu for this portlet. Also wires in the
	 * event handlers so we can update on menu value change
	 * 
	 * @return
	 */
	private Widget buildHeader() {

		metricListbox = new ListBox();
		metricListbox.addItem(TEXT_CONSTANTS.select(), TEXT_CONSTANTS.select());
		metricListbox.addItem(TEXT_CONSTANTS.technologyTypeMetric(),
				TECH_METRIC);
		metricListbox.addItem(TEXT_CONSTANTS.pointTypeMetric(), TYPE_METRIC);

		metricListbox.addChangeHandler(this);

		VerticalPanel headerPanel = new VerticalPanel();

		HorizontalPanel topRow = new HorizontalPanel();
		topRow.add(ViewUtil.initLabel(TEXT_CONSTANTS.country()));
		topRow.add(getCountryControl());
		topRow.add(ViewUtil.initLabel(TEXT_CONSTANTS.metric()));
		topRow.add(metricListbox);

		headerPanel.add(topRow);
		List<ListBox> boxes = getSubLevelControls();
		if (boxes != null && boxes.size() > 0) {
			headerPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.subdivision()));
			for (ListBox box : boxes) {
				headerPanel.add(box);
			}
		}

		return headerPanel;
	}

	@Override
	public void onChange(ChangeEvent event) {
		selectedMetric = getSelectedValue(metricListbox);
		if (selectedMetric != null
				&& !TEXT_CONSTANTS.select().equals(selectedMetric)) {
			loadData();
		}
	}

	private void loadData() {
		List<String> subLevels = getSelectedSubLevels();
		int maxLevel = 0;
		String maxLevelName = null;
		for (int i = 0; i < subLevels.size(); i++) {
			if (subLevels.get(i) != null) {
				maxLevel = i + 1;
				maxLevelName = subLevels.get(i);
			}
		}
		apMetricService.listAccessPointMetricSummary(selectedMetric,
				getSelectedCountry(), maxLevelName, maxLevel,
				new AsyncCallback<List<AccessPointMetricSummaryDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog dia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ ": " + caught.getLocalizedMessage());
						dia.showCentered();
					}

					@Override
					public void onSuccess(
							final List<AccessPointMetricSummaryDto> result) {

						Runnable onLoadCallback = new Runnable() {
							public void run() {

								if (result != null) {
									final DataTable dataTable = DataTable
											.create();
									dataTable.addColumn(ColumnType.STRING,
											TEXT_CONSTANTS.status());
									dataTable.addColumn(ColumnType.NUMBER,
											TEXT_CONSTANTS.count());
									for (int i = 0; i < result.size(); i++) {
										dataTable.addRow();
										dataTable.setValue(i, 0, result.get(i)
												.getMetricValue());
										dataTable.setValue(i, 1, result.get(i)
												.getCount() != null ? result
												.get(i).getCount() : 0);
									}
									if (pieChart != null) {
										// remove the old chart
										pieChart.removeFromParent();
									}
									if (noDataLabel != null) {
										noDataLabel.removeFromParent();
									}
									if (result.size() > 0) {
										pieChart = new PieChart(dataTable,
												createOptions());
										currentTable = dataTable;
										contentPane.add(pieChart);
									} else {
										noDataLabel = new Label(TEXT_CONSTANTS
												.noData());
										contentPane.add(noDataLabel);
									}
								}
							}
						};
						VisualizationUtils.loadVisualizationApi(onLoadCallback,
								"corechart");

					}
				});
	}

	@Override
	public void handleExportClick() {
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				ImagePieChart.Options options = ImagePieChart.Options.create();
				options.setHeight(HEIGHT - 60);
				options.setWidth(WIDTH + 60);
				options.setLabels("value");
				options.setLegend(LegendPosition.RIGHT);
				ImagePieChart ipc = new ImagePieChart(currentTable, options);
				WidgetDialog dia = new WidgetDialog(NAME, ipc);
				dia.showRelativeTo(getHeaderWidget());
			}
		};
		if (currentTable != null) {
			VisualizationUtils.loadVisualizationApi(onLoadCallback,
					ImagePieChart.PACKAGE);
		}
	}

	/**
	 * configures the Options to initialize the visualization
	 * 
	 * @return
	 */
	private Options createOptions() {
		Options options = Options.create();
		// this is needed so we can display html pop-ups over the flash content
		options.setHeight(HEIGHT - 60);
		options.setWidth(WIDTH);
		return options;
	}

	@Override
	protected String getConfigItemName() {
		return CONFIG_ITEM_NAME;
	}

}
