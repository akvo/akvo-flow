package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;

/**
 * Displays summary information for access points either globally, for a
 * country, or a community
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointStatusPortlet extends LocationDrivenPortlet implements
		ChangeHandler, ValueChangeHandler<Boolean> {
	public static final String DESCRIPTION = "Access Point status as a Pie Chart";
	public static final String NAME = "Access Point Status";
	private static final String CONFIG_ITEM_NAME = "APStatus";
	private static final String WATER_TYPE = "WATER_POINT";
	private static final String SANITATION_TYPE = "SANITATION_POINT";

	private static final int WIDTH = 400;
	private static final int HEIGHT = 400;
	private VerticalPanel contentPane;
	private PieChart pieChart;

	private ListBox yearListbox;

	private RadioButton wpTypeButton;
	private RadioButton spTypeButton;

	public AccessPointStatusPortlet(UserDto user) {
		super(NAME, false, true, WIDTH, HEIGHT, user, true,
				LocationDrivenPortlet.ALL_OPT);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		contentPane.add(header);
		setContent(contentPane);
	}

	public void initialLoadComplete() {
		String conf = getConfig();
		if (conf != null) {
			String[] vals = conf.split(",");
			if (vals.length >= 4) {
				setSelectedValue(vals[0], getCountryControl());
				if (getSelectedCountry() != null) {
					loadCommunities(vals[0]);
					setSelectedValue(vals[1], getCommunityControl());
				}
				setSelectedValue(vals[2], yearListbox);
				if (WATER_TYPE.equals(vals[3])) {
					wpTypeButton.setValue(true);
				} else {
					spTypeButton.setValue(true);
				}
				buildChart(vals[0], vals[1], vals[2], vals[3]);
			}
		} else {
			buildChart(null, null, null, WATER_TYPE);
		}
	}

	public void countrySelected(String country) {
		updateChart();
	}

	public void communitySelected(String community) {
		updateChart();
	}

	/**
	 * triggers reload of chart
	 */
	public void onValueChange(ValueChangeEvent<Boolean> event) {
		updateChart();
	}

	/**
	 * helper method to get value out of a listbox. If "All" is selected, it's
	 * translated to null since the service expects null to be passed in rather
	 * than "all" if you don't want to filter by that param
	 * 
	 * @param lb
	 * @return
	 */
	private String getSelectedValue(ListBox lb) {
		if (lb.getSelectedIndex() >= 0) {
			String val = lb.getValue(lb.getSelectedIndex());
			if (ALL_OPT.equals(val)) {
				return null;
			} else {
				return val;
			}
		} else {
			return null;
		}
	}

	/**
	 * gets the currently selected values from the list box and then updates the
	 * chart
	 */
	private void updateChart() {
		setConfig(getSelectedCountry()
				+ ","
				+ getSelectedCommunity()
				+ ","
				+ getSelectedValue(yearListbox)
				+ ","
				+ (wpTypeButton.getValue() ? WATER_TYPE.toString()
						: SANITATION_TYPE.toString()));
		buildChart(getSelectedCountry(), getSelectedCommunity(),
				getSelectedValue(yearListbox),
				wpTypeButton.getValue() ? WATER_TYPE.toString()
						: SANITATION_TYPE.toString());
	}

	/**
	 * constructs and installs the menu for this portlet. Also wires in the
	 * event handlers so we can update on menu value change
	 * 
	 * @return
	 */
	private Widget buildHeader() {
		Grid grid = new Grid(2, 2);

		HorizontalPanel countryPanel = new HorizontalPanel();
		countryPanel.add(new Label("Country: "));
		countryPanel.add(getCountryControl());
		grid.setWidget(0, 0, countryPanel);

		HorizontalPanel commPanel = new HorizontalPanel();
		commPanel.add(new Label("Community: "));
		commPanel.add(getCommunityControl());
		grid.setWidget(1, 0, commPanel);

		HorizontalPanel yearPanel = new HorizontalPanel();
		yearPanel.add(new Label("Year: "));
		yearListbox = new ListBox();
		yearPanel.add(yearListbox);
		grid.setWidget(0, 1, yearPanel);
		yearListbox.addChangeHandler(this);

		HorizontalPanel typePanel = new HorizontalPanel();
		typePanel.add(new Label("Type: "));
		wpTypeButton = new RadioButton("APStatusTypeGroup", "Waterpoint");
		spTypeButton = new RadioButton("APStatusTypeGroup", "Sanitation");
		typePanel.add(wpTypeButton);
		typePanel.add(spTypeButton);
		wpTypeButton.addValueChangeHandler(this);
		spTypeButton.addValueChangeHandler(this);
		wpTypeButton.setValue(true);
		grid.setWidget(1, 1, typePanel);

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
		options.setHeight(HEIGHT - 60);
		options.setWidth(WIDTH);
		return options;
	}

	/**
	 * constructs a data table using the results of the service call and
	 * installs a new Pie Chart with those values
	 * 
	 * @param countryCode
	 * @param communityCode
	 * @param year
	 * @param type
	 */
	private void buildChart(String countryCode, String communityCode,
			String year, String type) {
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
							final DataTable dataTable = DataTable.create();
							dataTable.addColumn(ColumnType.STRING, "Status");
							dataTable.addColumn(ColumnType.NUMBER, "Count");
							for (int i = 0; i < result.length; i++) {
								dataTable.addRow();
								dataTable.setValue(i, 0, result[i].getStatus());
								dataTable
										.setValue(
												i,
												1,
												result[i].getCount() != null ? result[i]
														.getCount()
														: 0);
							}
							if (pieChart != null) {
								// remove the old chart
								pieChart.removeFromParent();
							}
							pieChart = new PieChart(dataTable, createOptions());
							contentPane.add(pieChart);
						}
					}
				};
				VisualizationUtils.loadVisualizationApi(onLoadCallback,
						PieChart.PACKAGE);
			}
		};
		apService.listAccessPointStatusSummary(countryCode, communityCode,
				type, year, null, apCallback);
	}

	public String getName() {
		return NAME;
	}

	@Override
	public void onChange(ChangeEvent event) {
		updateChart();
	}

	@Override
	protected String getConfigItemName() {
		return CONFIG_ITEM_NAME;
	}
}
