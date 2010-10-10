package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSummaryServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;
import org.waterforpeople.mapping.portal.client.widgets.component.WidgetDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.IntensityMap;
import com.google.gwt.visualization.client.visualizations.IntensityMap.Options;
import com.google.gwt.visualization.client.visualizations.IntensityMap.Region;

/**
 * Portlet that displays the current access point status activity over a period
 * of time using the IntensityMap visualization.
 * 
 * This portlet supports configuration - users can specify the timeframe and
 * filters for the activity. Alternatively, they should also be able to choose
 * an option to "bind" the timeframe used by this portlet to the timeframe used
 * in other portlets
 * 
 * @author Christopher Fagiani
 * 
 */
public class ActivityMapPortlet extends UserAwarePortlet implements
		ChangeHandler, ValueChangeHandler<Boolean> {
	public static final String DESCRIPTION = "Displays access points by status by region on a map";
	public static final String NAME = "Access Point Status by Country";
	private static final String CONFIG_NAME = "ActivityMap";
	private static final String WATER_TYPE = "WATER_POINT";
	private static final String SANITATION_TYPE = "SANITATION_POINT";
	private static final String URL_START = "url(";

	private static final int WIDTH = 400;
	private static final int HEIGHT = 300;
	private IntensityMap map;
	private VerticalPanel contentPane;
	private ListBox statusListbox;
	private ListBox regionListbox;
	private RadioButton wpTypeButton;
	private RadioButton spTypeButton;

	public ActivityMapPortlet(UserDto user) {
		super(NAME, false, true, true, WIDTH, HEIGHT, user);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		contentPane.add(header);
		setContent(contentPane);
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
				for (int i = 0; i < statusListbox.getItemCount(); i++) {
					if (statusListbox.getValue(i).equals(vals[0])) {
						statusListbox.setSelectedIndex(i);
						break;
					}
				}
				if (vals[1].equals(spTypeButton.getText())) {
					spTypeButton.setValue(true);
				} else {
					wpTypeButton.setValue(true);
				}
				if (vals.length > 2) {
					for (int i = 0; i < regionListbox.getItemCount(); i++) {
						if (regionListbox.getValue(i).equals(vals[2])) {
							regionListbox.setSelectedIndex(i);
							break;
						}
					}
				}
				buildChart(vals[0], vals[1]);

			}
		} else {
			buildChart("FUNCTIONING_HIGH", WATER_TYPE);
		}
	}

	private Options createOptions() {
		Options options = Options.create();
		String region = regionListbox
				.getValue(regionListbox.getSelectedIndex());
		if (region.equalsIgnoreCase(Region.AFRICA.toString())) {
			options.setRegion(Region.AFRICA);
		} else if (region.equalsIgnoreCase(Region.ASIA.toString())) {
			options.setRegion(Region.ASIA);
		} else if (region.equalsIgnoreCase(Region.ASIA.toString())) {
			options.setRegion(Region.ASIA);
		} else if (region.equalsIgnoreCase(Region.EUROPE.toString())) {
			options.setRegion(Region.EUROPE);
		} else if (region.equalsIgnoreCase(Region.MIDDLE_EAST.toString())) {
			options.setRegion(Region.MIDDLE_EAST);
		} else if (region.equalsIgnoreCase(Region.SOUTH_AMERICA.toString())) {
			options.setRegion(Region.SOUTH_AMERICA);
		}
		options.setWidth(WIDTH);
		options.setHeight(HEIGHT - 60);
		return options;
	}

	/**
	 * gets the values from the menus and calls buildChart
	 */
	private void updateChart() {
		String typeVal = wpTypeButton.getValue() ? WATER_TYPE.toString()
				: SANITATION_TYPE.toString();
		setConfig(getSelectedValue(statusListbox) + "," + typeVal);
		buildChart(getSelectedValue(statusListbox), typeVal);
	}

	/**
	 * constructs a data table using the results of the service call and
	 * installs a new Intensity Map with those values
	 * 
	 * @param status
	 * @param type
	 */
	private void buildChart(String status, String type) {
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
							dataTable.addColumn(ColumnType.STRING, "Country");
							dataTable.addColumn(ColumnType.NUMBER, "Count");
							for (int i = 0; i < result.length; i++) {
								dataTable.addRow();
								dataTable.setValue(i, 0, result[i]
										.getCountryCode());
								dataTable
										.setValue(
												i,
												1,
												result[i].getCount() != null ? result[i]
														.getCount()
														: 0);
							}
							if (map != null) {
								// remove the old chart
								map.removeFromParent();
							}
							map = new IntensityMap(dataTable, createOptions());
							contentPane.add(map);
						}
					}
				};
				VisualizationUtils.loadVisualizationApi(onLoadCallback,
						IntensityMap.PACKAGE);
			}
		};
		apService.listAccessPointStatusSummary(null, null, type, null, status,
				apCallback);
	}

	/**
	 * builds the menus for this portlet
	 * 
	 * @return
	 */
	private Widget buildHeader() {
		VerticalPanel headerPanel = new VerticalPanel();
		Grid grid = new Grid(1, 2);

		HorizontalPanel statusPanel = new HorizontalPanel();
		statusPanel.add(new Label("Status: "));
		statusListbox = new ListBox();
		statusListbox.addItem("High", "FUNCTIONING_HIGH");
		statusListbox.addItem("Ok", "FUNCTIONING_OK");
		statusListbox.addItem("Poor", "FUNCTIONING_WITH_PROBLEMS");
		statusListbox.addItem("Other", "OTHER");
		statusListbox.setVisibleItemCount(1);
		statusPanel.add(statusListbox);
		grid.setWidget(0, 0, statusPanel);
		statusListbox.addChangeHandler(this);

		HorizontalPanel typePanel = new HorizontalPanel();
		typePanel.add(new Label("Type: "));
		wpTypeButton = new RadioButton("ActMapTypeGroup", "Waterpoint");
		spTypeButton = new RadioButton("ActMapTypeGroup", "Sanitation");
		typePanel.add(wpTypeButton);
		typePanel.add(spTypeButton);
		wpTypeButton.addValueChangeHandler(this);
		spTypeButton.addValueChangeHandler(this);
		wpTypeButton.setValue(true);
		grid.setWidget(0, 1, typePanel);

		headerPanel.add(grid);

		HorizontalPanel regionPanel = new HorizontalPanel();
		regionPanel.add(new Label("Region: "));
		regionListbox = new ListBox();
		regionListbox.addItem("World", "world");
		regionListbox.addItem("Africa", "africa");
		regionListbox.addItem("Asia", "asia");
		regionListbox.addItem("Europe", "europe");
		regionListbox.addItem("Middle East", "middle_east");
		regionListbox.addItem("South America", "south_america");
		regionListbox.addChangeHandler(this);
		regionPanel.add(regionListbox);
		headerPanel.add(regionPanel);

		return headerPanel;
	}

	public String getName() {
		return NAME;
	}

	/**
	 * calls the update method to update the map's content
	 */
	@Override
	public void onChange(ChangeEvent event) {
		updateChart();
	}

	/**
	 * calls the update method to update the map's content
	 */
	@Override
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
			return val;
		} else {
			return null;
		}
	}

	@Override
	protected String getConfigItemName() {
		return CONFIG_NAME;
	}

	@Override
	protected void handleExportClick() {
		if (map != null) {
			// this is less than ideal, but it's the only way to get the image
			// URL used to produce the chart
			String htmlString = map.toString();
			if (htmlString != null && htmlString.contains(URL_START)) {
				htmlString = htmlString.substring(htmlString.indexOf(URL_START)
						+ URL_START.length());
				// strip off the closing paren
				htmlString = htmlString.substring(0, htmlString.indexOf(")"));
				// undo the HTML encoding of the content
				htmlString = htmlString.replaceAll("&amp;", "&");
				htmlString = htmlString.replaceAll("&quot;", "\"");
				// the url is surrounded by quotes, so strip those too
				htmlString = htmlString.substring(1, htmlString.length() - 1);
			}
			WidgetDialog dia = new WidgetDialog(NAME, new Image(htmlString));
			dia.showRelativeTo(getHeaderWidget());
		}
	}
}
