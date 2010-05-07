package org.waterforpeople.mapping.portal.client.widgets;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;

import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.LineChart;
import com.google.gwt.visualization.client.visualizations.LineChart.Options;

/**
 * This portlet shows a line graph that lets users compare the performance of
 * access points in different communities over time.
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointPerformancePortlet extends LocationDrivenPortlet
		implements ChangeHandler, ClickHandler, ValueChangeHandler<Boolean> {
	public static final String DESCRIPTION = "Compare Access Point performance over time across communities";
	public static final String NAME = "Access Point Performance";
	private static final String WATER_TYPE = "WATER_POINT";
	private static final String SANITATION_TYPE = "SANITATION_POINT";

	private static final String STATUS_METRIC = "Status";
	private static final String COST_METRIC = "Cost";
	private static final String COUNT_METRIC = "Households Served";

	private static final int WIDTH = 400;
	private static final int HEIGHT = 400;
	private VerticalPanel contentPane;
	private LineChart lineChart;

	private RadioButton wpTypeButton;
	private RadioButton spTypeButton;

	private ListBox metricListbox;
	private Button addLocationButton;
	private Button resetButton;

	private LocationDialog locationDialog;
	private Map<String, Map<Long, AccessPointDto>> wpSummaryMap;
	private Map<String, Map<Long, AccessPointDto>> spSummaryMap;

	public AccessPointPerformancePortlet(UserDto user) {
		super(NAME, false, false, WIDTH, HEIGHT, user, true, null);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		contentPane.add(header);
		setContent(contentPane);
		wpSummaryMap = new HashMap<String, Map<Long, AccessPointDto>>();
		spSummaryMap = new HashMap<String, Map<Long, AccessPointDto>>();
	}

	@Override
	protected void initialLoadComplete() {
		locationDialog = new LocationDialog();
		addLocationButton.setEnabled(true);
	}

	/**
	 * triggers reload of chart when the user changes the type of AP from
	 * sanitation to waterpoint or vice versa
	 */
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		updateChart();
	}

	/**
	 * opens the "add location" dialog box or resets the widget
	 */
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == addLocationButton) {
			locationDialog.showRelativeTo(this);
		} else if (event.getSource() == resetButton) {
			wpSummaryMap.clear();
			spSummaryMap.clear();
			renderChart();
		}
	}

	/**
	 * gets the currently selected values from the list box and then updates the
	 * chart
	 */
	private void updateChart() {
		buildChart(getSelectedCountry(), getSelectedCommunity(),
				getSelectedMetric(), wpTypeButton.getValue() ? WATER_TYPE
						.toString() : SANITATION_TYPE.toString());
	}

	/**
	 * when the community is selected, enable the OK button on the dialog
	 */
	protected void communitySelected(String community) {
		locationDialog.enableOk();
	}

	/**
	 * constructs and installs the menu for this portlet. Also wires in the
	 * event handlers so we can update on menu value change
	 * 
	 * @return
	 */
	private Widget buildHeader() {

		metricListbox = new ListBox();
		metricListbox.addItem("Cost", COST_METRIC);
		metricListbox.addItem("Households Served", COUNT_METRIC);
		// metricListbox.addItem("Status", STATUS_METRIC);
		metricListbox.addChangeHandler(this);

		VerticalPanel headerPanel = new VerticalPanel();

		HorizontalPanel controlPanel = new HorizontalPanel();

		controlPanel.add(new Label("Type: "));
		wpTypeButton = new RadioButton("APperfTypeGroup", "Waterpoint");
		spTypeButton = new RadioButton("APperfTypeGroup", "Sanitation");

		wpTypeButton.addValueChangeHandler(this);
		spTypeButton.addValueChangeHandler(this);
		wpTypeButton.setValue(true);

		controlPanel.add(wpTypeButton);
		controlPanel.add(spTypeButton);

		controlPanel.add(new Label("Metric: "));
		controlPanel.add(metricListbox);

		HorizontalPanel buttonPanel = new HorizontalPanel();
		addLocationButton = new Button("Add Location");
		addLocationButton.addClickHandler(this);
		addLocationButton.setEnabled(false);
		buttonPanel.add(addLocationButton);
		resetButton = new Button("Reset");
		resetButton.addClickHandler(this);
		buttonPanel.add(resetButton);

		headerPanel.add(controlPanel);
		headerPanel.add(buttonPanel);
		return headerPanel;
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

	/**
	 * constructs a data table using the results of the service call and
	 * installs a new LineChart with those values
	 * 
	 * @param countryCode
	 * @param communityCode
	 * @param valueType
	 * @param type
	 */
	private void buildChart(final String countryCode,
			final String communityCode, String valueType, String type) {

		// fetch list of responses for a question
		AccessPointManagerServiceAsync apService = GWT
				.create(AccessPointManagerService.class);

		// Set up the callback object.
		AsyncCallback<AccessPointDto[]> apCallback = new AsyncCallback<AccessPointDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(final AccessPointDto[] result) {

				Runnable onLoadCallback = new Runnable() {
					public void run() {

						if (result != null) {
							Map<Long, AccessPointDto> locationMap = new HashMap<Long, AccessPointDto>();
							for (int i = 0; i < result.length; i++) {
								if (result[i].getCollectionDate() != null) {
									try {
										locationMap.put(result[i].getYear(),
												result[i]);
									} catch (NumberFormatException e) {
										// no-op
									}
								}
							}
							if (spTypeButton.getValue()) {
								spSummaryMap.put(countryCode + " - "
										+ communityCode, locationMap);
							} else {
								wpSummaryMap.put(countryCode + " - "
										+ communityCode, locationMap);
							}
							renderChart();
						}
					}
				};
				VisualizationUtils.loadVisualizationApi(onLoadCallback,
						LineChart.PACKAGE);
			}
		};
		apService.listAccessPointByLocation(countryCode, communityCode, type,
				apCallback);
	}

	/**
	 * Renders the line chart for all loaded locations
	 */
	private void renderChart() {
		if (lineChart != null) {
			// remove the old chart
			lineChart.removeFromParent();
		}
		Map<String, Map<Long, AccessPointDto>> summaryMap = null;
		if (spTypeButton.getValue()) {
			summaryMap = spSummaryMap;
		} else {
			summaryMap = wpSummaryMap;
		}

		if (summaryMap.keySet().size() > 0) {
			DataTable dataTable = DataTable.create();
			String metric = getSelectedMetric();
			SortedSet<Long> years = new TreeSet<Long>();
			// get the union of all years sorted in ascending order
			for (Map<Long, AccessPointDto> apList : summaryMap.values()) {
				years.addAll(apList.keySet());
			}

			dataTable.addColumn(ColumnType.STRING, "Year");
			// add a column for each location
			for (String location : summaryMap.keySet()) {
				if (STATUS_METRIC.equals(metric)) {
					dataTable.addColumn(ColumnType.STRING, location);
				} else {
					dataTable.addColumn(ColumnType.NUMBER, location);
				}
			}

			// add a row for each year
			int i = 0;
			for (Long year : years) {
				dataTable.addRow();
				dataTable.setValue(i, 0, year.toString());
				int j = 1;
				for (String location : summaryMap.keySet()) {
					AccessPointDto curItem = summaryMap.get(location) != null ? summaryMap
							.get(location).get(year)
							: null;
					if (curItem != null) {
						if (STATUS_METRIC.equals(metric)) {
							dataTable.setValue(i, j, curItem.getPointStatus()
									.toString());
						} else if (COST_METRIC.equals(metric)) {
							dataTable.setValue(i, j, curItem.getCostPer());
						} else {
							dataTable.setValue(i, j, curItem
									.getNumberOfHouseholdsUsingPoint());
						}
					} else {
						dataTable.setValue(i, j, 0);
					}
					j++;
				}
				i++;
			}

			if (lineChart != null) {
				// remove the old chart
				lineChart.removeFromParent();
			}
			lineChart = new LineChart(dataTable, createOptions());
			contentPane.add(lineChart);
		}

	}

	@Override
	public void handleEvent(PortletEvent e) {
		// no-op
	}

	@Override
	protected boolean getReadyForRemove() {
		// no-op. nothing to do before remove
		return true;
	}

	@Override
	protected void handleConfigClick() {
		// no-op. this portlet does not support config
	}

	public String getName() {
		return NAME;
	}

	/**
	 * returns the value currently selected in the metric listbox
	 */
	public String getSelectedMetric() {
		if (metricListbox.getSelectedIndex() >= 0) {
			String val = metricListbox.getValue(metricListbox
					.getSelectedIndex());
			return val;
		} else {
			return null;
		}
	}

	/**
	 * triggers a chart update whenever the user changes the value of the metric
	 * listbox
	 */
	@Override
	public void onChange(ChangeEvent event) {
		updateChart();
	}

	/**
	 * Renders the country/region selection window,
	 * 
	 * @author Christopher Fagiani
	 * 
	 */
	private class LocationDialog extends DialogBox implements ClickHandler {

		private Button okButton;
		private Button cancelButton;

		public LocationDialog() {
			// Set the dialog box's caption.
			setText("Add Items to Dashboard");
			setAnimationEnabled(true);
			setGlassEnabled(true);
			VerticalPanel contentPane = new VerticalPanel();
			contentPane.add(new Label("Add Community to Chart"));

			HorizontalPanel countryPanel = new HorizontalPanel();
			countryPanel.add(new Label("Country: "));
			countryPanel.add(AccessPointPerformancePortlet.this
					.getCountryControl());
			contentPane.add(countryPanel);

			HorizontalPanel commPanel = new HorizontalPanel();
			commPanel.add(new Label("Community: "));
			commPanel.add(AccessPointPerformancePortlet.this
					.getCommunityControl());
			contentPane.add(commPanel);

			HorizontalPanel buttonPanel = new HorizontalPanel();
			okButton = new Button("Ok");
			okButton.setEnabled(false);
			okButton.addClickHandler(this);
			buttonPanel.add(okButton);
			cancelButton = new Button("Cancel");
			cancelButton.addClickHandler(this);
			buttonPanel.add(cancelButton);
			setWidget(contentPane);
			contentPane.add(buttonPanel);
		}

		public void enableOk() {
			okButton.setEnabled(true);
		}

		/**
		 * Closes the dialog box and, if "ok" is the source, tells the portlet
		 * to update the chart
		 */
		@Override
		public void onClick(ClickEvent event) {
			if (event.getSource() == cancelButton) {
				LocationDialog.this.hide();
			} else if (event.getSource() == okButton) {
				updateChart();
				LocationDialog.this.hide();
			}
		}
	}
}
