package org.waterforpeople.mapping.portal.client.widgets;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityDto;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityService;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
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
public class AccessPointPerformancePortlet extends Portlet implements
		ChangeHandler, ClickHandler, ValueChangeHandler<Boolean> {
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

	protected CountryDto[] countries;

	private RadioButton wpTypeButton;
	private RadioButton spTypeButton;

	private ListBox metricListbox;
	private Button addLocationButton;
	private Button resetButton;

	private LocationDialog locationDialog;
	private Map<String, Map<Long, AccessPointSummaryDto>> summaryMap;

	public AccessPointPerformancePortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		contentPane.add(header);
		setContent(contentPane);
		summaryMap = new HashMap<String, Map<Long, AccessPointSummaryDto>>();

		CommunityServiceAsync communityService = GWT
				.create(CommunityService.class);
		// Set up the callback object.
		AsyncCallback<CountryDto[]> countryCallback = new AsyncCallback<CountryDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(CountryDto[] result) {
				if (result != null) {
					countries = result;
					locationDialog = new LocationDialog();
					addLocationButton.setEnabled(true);
				}
			}
		};
		communityService.listCountries(countryCallback);
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
		super.onClick(event);
		if (event.getSource() == addLocationButton) {
			locationDialog.show();
		} else if (event.getSource() == resetButton) {
			summaryMap.clear();
			renderChart();
		}
	}

	/**
	 * gets the currently selected values from the list box and then updates the
	 * chart
	 */
	private void updateChart() {
		buildChart(locationDialog.getSelectedCountry(), locationDialog
				.getSelectedCommunity(), getSelectedMetric(), wpTypeButton
				.getValue() ? WATER_TYPE.toString() : SANITATION_TYPE
				.toString());
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
		wpTypeButton = new RadioButton("typeGroup", "Waterpoint");
		spTypeButton = new RadioButton("typeGroup", "Sanitation");
		controlPanel.add(wpTypeButton);
		controlPanel.add(spTypeButton);
		wpTypeButton.setValue(true);

		wpTypeButton.addValueChangeHandler(this);
		spTypeButton.addValueChangeHandler(this);

		controlPanel.add(new Label("Metric: "));
		controlPanel.add(metricListbox);

		HorizontalPanel buttonPanel = new HorizontalPanel();
		addLocationButton = new Button("Add Location");
		addLocationButton.addClickHandler(this);
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
		options.setHeight(HEIGHT);
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
		AccessPointSummaryServiceAsync apService = GWT
				.create(AccessPointSummaryService.class);

		// Set up the callback object.
		AsyncCallback<AccessPointSummaryDto[]> apCallback = new AsyncCallback<AccessPointSummaryDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(final AccessPointSummaryDto[] result) {

				Runnable onLoadCallback = new Runnable() {
					public void run() {

						if (result != null) {
							Map<Long, AccessPointSummaryDto> locationMap = new HashMap<Long, AccessPointSummaryDto>();
							for (int i = 0; i < result.length; i++) {
								if (result[i].getYear() != null) {
									try {
										locationMap.put(new Long(result[i]
												.getYear().trim()), result[i]);
									} catch (NumberFormatException e) {
										// no-op
									}
								}
							}
							summaryMap.put(countryCode + " - " + communityCode,
									locationMap);
							renderChart();
						}
					}
				};
				VisualizationUtils.loadVisualizationApi(onLoadCallback,
						LineChart.PACKAGE);
			}
		};
		apService.listAccessPointStatusSummaryWithoutRollup(countryCode,
				communityCode, type, null, null, apCallback);
	}

	/**
	 * Renders the line chart for all loaded locations
	 */
	private void renderChart() {
		if (lineChart != null) {
			// remove the old chart
			lineChart.removeFromParent();
		}

		if (summaryMap.keySet().size() > 0) {
			DataTable dataTable = DataTable.create();
			String metric = getSelectedMetric();
			SortedSet<Long> years = new TreeSet<Long>();
			// get the union of all years sorted in ascending order
			for (Map<Long, AccessPointSummaryDto> apList : summaryMap.values()) {
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
					AccessPointSummaryDto curItem = summaryMap.get(location) != null ? summaryMap
							.get(location).get(year)
							: null;
					if (curItem != null) {
						if (STATUS_METRIC.equals(metric)) {
							dataTable.setValue(i, j, curItem.getStatus());
						} else if (COST_METRIC.equals(metric)) {
							dataTable.setValue(i, j, curItem.getCost());
						} else {
							dataTable.setValue(i, j, curItem
									.getHouseholdsServed());
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
	private class LocationDialog extends DialogBox implements ClickHandler,
			ChangeHandler {

		private ListBox countryListbox;
		private ListBox communityListbox;
		private Button okButton;
		private Button cancelButton;

		public LocationDialog() {
			// Set the dialog box's caption.
			setText("Add Items to Dashboard");
			setAnimationEnabled(true);
			setGlassEnabled(true);
			setWidth("80%");
			VerticalPanel contentPane = new VerticalPanel();
			contentPane.add(new Label("Add Community to Chart"));
			setPopupPosition(Window.getClientWidth() / 3, Window
					.getClientHeight() / 3);

			HorizontalPanel countryPanel = new HorizontalPanel();
			countryPanel.add(new Label("Country: "));
			countryListbox = new ListBox();
			for (int i = 0; i < countries.length; i++) {
				countryListbox.addItem(countries[i].getName(), countries[i]
						.getIsoAlpha2Code());
			}

			countryListbox.setVisibleItemCount(1);
			countryPanel.add(countryListbox);
			contentPane.add(countryPanel);

			countryListbox.addChangeHandler(this);

			HorizontalPanel commPanel = new HorizontalPanel();
			commPanel.add(new Label("Community: "));
			communityListbox = new ListBox();
			commPanel.add(communityListbox);
			contentPane.add(commPanel);
			communityListbox.addChangeHandler(this);

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
			loadCommunities();
		}

		/**
		 * checks if the country was changed and, if so, loads that country's
		 * communities. This will also trigger a reload of the pie chart
		 */
		@Override
		public void onChange(ChangeEvent event) {
			if (event.getSource() == countryListbox) {
				loadCommunities();
			}
		}

		private void loadCommunities() {
			String selectedCountry = countryListbox.getValue(countryListbox
					.getSelectedIndex());
			if (selectedCountry != null) {
				// empty the old communities
				communityListbox.clear();
				// if country changed, load the communities
				CommunityServiceAsync communityService = GWT
						.create(CommunityService.class);
				// Set up the callback object.
				AsyncCallback<CommunityDto[]> communityCallback = new AsyncCallback<CommunityDto[]>() {
					public void onFailure(Throwable caught) {
						// no-op
					}

					public void onSuccess(CommunityDto[] result) {
						if (result != null) {
							for (int i = 0; i < result.length; i++) {
								communityListbox.addItem(result[i]
										.getCommunityCode(), result[i]
										.getCommunityCode());

							}
							communityListbox.setVisibleItemCount(1);
						}
					}
				};
				communityService.listCommunities(selectedCountry,
						communityCallback);
			}
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

		/**
		 * returns the currently selected country from the listbox
		 */
		public String getSelectedCountry() {
			if (countryListbox.getSelectedIndex() >= 0) {
				String val = countryListbox.getValue(countryListbox
						.getSelectedIndex());
				return val;
			} else {
				return null;
			}
		}

		/**
		 * returns the currently selected community from the listbox
		 */
		public String getSelectedCommunity() {
			if (communityListbox.getSelectedIndex() >= 0) {
				String val = communityListbox.getValue(communityListbox
						.getSelectedIndex());
				return val;
			} else {
				return null;
			}
		}
	}

}
