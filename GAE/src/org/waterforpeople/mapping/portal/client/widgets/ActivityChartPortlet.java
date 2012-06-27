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

import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
import com.google.gwt.visualization.client.visualizations.ImageLineChart;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.Options;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.WindowMode;

/**
 * Portlet that displays the current system activity over a period of time using
 * the AnnotatedTimeLine visualization.
 * 
 * This portlet supports configuration - users can specify the timeframe and
 * filters for the activity
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class ActivityChartPortlet extends LocationDrivenPortlet {
	private static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);
	public static final String DESCRIPTION = TEXT_CONSTANTS.activityChartPortletDescription();
	public static final String NAME = TEXT_CONSTANTS.activityChartPortletTitle();
	private static final String CONFIG_NAME = "ActivityChart";
	private static final int WIDTH = 600;
	private static final int HEIGHT = 300;
	private AnnotatedTimeLine timeLine;
	private VerticalPanel contentPane;
	private AbstractDataTable currentTable;

	public ActivityChartPortlet(UserDto u) {
		super(NAME, false, true, true, WIDTH, HEIGHT, u, true,
				LocationDrivenPortlet.ALL_OPT);
		contentPane = new VerticalPanel();
		contentPane.add(buildHeader());
		setContent(contentPane);
	}

	protected void countrySelected(String country) {
		setConfig(getSelectedCountry() + "," + getSelectedCommunity());
		buildChart(getSelectedCountry(), getSelectedCommunity());
	}

	protected void initialLoadComplete() {
		doInitialLoad();
	}

	/**
	 * parses config and, if present, sets the control values and builds the
	 * chart.
	 */
	private void doInitialLoad() {
		String conf = getConfig();
		if (conf != null) {
			String[] vals = conf.split(",");
			if (vals.length >= 2) {
				setSelectedValue(vals[0], getCountryControl());
				if (getSelectedCountry() != null) {
					loadCommunities(vals[0]);
					setSelectedValue(vals[1], getCommunityControl());
				}
				buildChart(vals[0], vals[1]);
			}
		} else {
			buildChart(ALL_OPT, ALL_OPT);
		}
	}

	/**
	 * constructs a data table using the results of the service call and
	 * installs a new AnnotatedTimeLine with those values
	 * 
	 * @param countryCode
	 * @param communityCode
	 * 
	 */
	private void buildChart(String countryCode, String communityCode) {
		// fetch list of responses for a question
		SurveySummaryServiceAsync siService = GWT
				.create(SurveySummaryService.class);
		// Set up the callback object.
		AsyncCallback<SurveySummaryDto[]> siCallback = new AsyncCallback<SurveySummaryDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(final SurveySummaryDto[] result) {

				Runnable onLoadCallback = new Runnable() {
					public void run() {
						if (result != null) {
							DataTable dataTable = DataTable.create();
							dataTable.addColumn(ColumnType.DATE, TEXT_CONSTANTS.date());
							dataTable.addColumn(ColumnType.NUMBER, TEXT_CONSTANTS.count());
							for (int i = 0; i < result.length; i++) {
								if (result[i] != null
										&& result[i].getDate() != null) {
									dataTable.addRow();
									dataTable.setValue(i, 0, result[i]
											.getDate());
									dataTable.setValue(i, 1, result[i]
											.getCount() != null ? result[i]
											.getCount() : 0);
								}
							}
							if (timeLine != null) {
								// remove the old chart
								timeLine.removeFromParent();
							}
							timeLine = new AnnotatedTimeLine(dataTable,
									createOptions(), WIDTH + "px",
									(HEIGHT - 60) + "px");
							currentTable = dataTable;
							contentPane.add(timeLine);
						}
					}
				};
				VisualizationUtils.loadVisualizationApi(onLoadCallback,
						AnnotatedTimeLine./*
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

package);
			}
		};
		siService.listInstanceSummaryByLocation(
				ALL_OPT.equals(countryCode) ? null : countryCode, ALL_OPT
						.equals(communityCode) ? null : communityCode,
				siCallback);
	}

	protected void communitySelected(String community) {
		setConfig(getSelectedCountry() + "," + getSelectedCommunity());
		buildChart(getSelectedCountry(), getSelectedCommunity());
	}

	/**
	 * constructs and installs the menu for this portlet. Also wires in the
	 * event handlers so we can update on menu value change
	 * 
	 * @return
	 */
	private Widget buildHeader() {
		Grid grid = new Grid(1, 2);

		HorizontalPanel countryPanel = new HorizontalPanel();
		countryPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.country()));
		countryPanel.add(getCountryControl());
		grid.setWidget(0, 0, countryPanel);

		HorizontalPanel commPanel = new HorizontalPanel();
		commPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.community()));
		commPanel.add(getCommunityControl());
		grid.setWidget(0, 1, commPanel);

		return grid;
	}

	/**
	 * configures the Options to initialize the visualization
	 * 
	 * @return
	 */
	private Options createOptions() {
		Options options = Options.create();
		// this is needed so we can display html pop-ups over the flash content
		options.setWindowMode(WindowMode.TRANSPARENT);
		return options;
	}

	public String getName() {
		return NAME;
	}

	@Override
	protected String getConfigItemName() {
		return CONFIG_NAME;
	}

	@Override
	protected void handleExportClick() {
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				ImageLineChart.Options options = ImageLineChart.Options
						.create();
				options.setHeight(HEIGHT - 60);
				options.setWidth(WIDTH + 60);
				options.setLegend(LegendPosition.RIGHT);
				ImageLineChart ilc = new ImageLineChart(currentTable, options);
				WidgetDialog dia = new WidgetDialog(NAME, ilc);
				dia.showRelativeTo(getHeaderWidget());
			}
		};
		if (currentTable != null) {
			VisualizationUtils.loadVisualizationApi(onLoadCallback,
					ImageLineChart./*
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

package);
		}
	}

}
