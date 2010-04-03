package org.waterforpeople.mapping.portal.client.widgets;

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
 * Displays summary information for access points either globablly, for a
 * country, or a community
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointStatusPortlet extends Portlet implements ChangeHandler,
		ValueChangeHandler<Boolean> {
	public static final String DESCRIPTION = "Access Point status as a Pie Chart";
	public static final String NAME = "Access Point Status";
	private static final String WATER_TYPE = "WATER_POINT";
	private static final String SANITATION_TYPE = "SANITATION_POINT";

	private static final String ALL_OPT = "All";
	private static final int WIDTH = 400;
	private static final int HEIGHT = 400;
	private VerticalPanel contentPane;
	private PieChart pieChart;

	private ListBox countryListbox;
	private ListBox communityListbox;
	private ListBox yearListbox;

	private RadioButton wpTypeButton;
	private RadioButton spTypeButton;

	public AccessPointStatusPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		contentPane.add(header);
		setContent(contentPane);
		CommunityServiceAsync communityService = GWT
				.create(CommunityService.class);
		// Set up the callback object.
		AsyncCallback<CountryDto[]> countryCallback = new AsyncCallback<CountryDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(CountryDto[] result) {
				if (result != null) {
					countryListbox.addItem(ALL_OPT, ALL_OPT);
					for (int i = 0; i < result.length; i++) {
						countryListbox.addItem(result[i].getName(), result[i]
								.getCountryCode());

					}
					countryListbox.setVisibleItemCount(1);
					if (result.length > 0) {
						buildChart(null, null, null, WATER_TYPE);
					}
				}
			}
		};
		communityService.listCountries(countryCallback);

		

	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == countryListbox) {
			String selectedCountry = countryListbox.getValue(countryListbox
					.getSelectedIndex());

			if (selectedCountry != null) {
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
							communityListbox.addItem(ALL_OPT, ALL_OPT);
							for (int i = 0; i < result.length; i++) {
								communityListbox.addItem(result[i]
										.getCommunityCode(), result[i]
										.getCommunityCode());

							}
							communityListbox.setVisibleItemCount(1);
							updateChart();
						}
					}
				};
				communityService.listCommunities(
						getSelectedValue(countryListbox), communityCallback);
			} else {
				// if country is ALL_OPT then we need to clear the communities
				// box
				communityListbox.clear();
				updateChart();
			}
		} else {
			updateChart();
		}

	}

	public void onValueChange(ValueChangeEvent<Boolean> event) {
		updateChart();
	}

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

	private void updateChart() {
		buildChart(getSelectedValue(countryListbox),
				getSelectedValue(communityListbox),
				getSelectedValue(yearListbox),
				wpTypeButton.getValue() ? WATER_TYPE.toString()
						: SANITATION_TYPE.toString());
	}

	private Widget buildHeader() {
		Grid grid = new Grid(2, 2);

		HorizontalPanel countryPanel = new HorizontalPanel();
		countryPanel.add(new Label("Country: "));
		countryListbox = new ListBox();
		countryPanel.add(countryListbox);
		grid.setWidget(0, 0, countryPanel);

		countryListbox.addChangeHandler(this);

		HorizontalPanel commPanel = new HorizontalPanel();
		commPanel.add(new Label("Community: "));
		communityListbox = new ListBox();
		commPanel.add(communityListbox);
		grid.setWidget(1, 0, commPanel);
		communityListbox.addChangeHandler(this);

		HorizontalPanel yearPanel = new HorizontalPanel();
		yearPanel.add(new Label("Year: "));
		yearListbox = new ListBox();
		yearPanel.add(yearListbox);
		grid.setWidget(0, 1, yearPanel);
		yearListbox.addChangeHandler(this);

		HorizontalPanel typePanel = new HorizontalPanel();
		typePanel.add(new Label("Type: "));
		wpTypeButton = new RadioButton("typeGroup", "Waterpoint");
		spTypeButton = new RadioButton("typeGroup", "Sanitation");
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
		options.setHeight(HEIGHT);
		options.setWidth(WIDTH);
		return options;
	}

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

			public void onSuccess(AccessPointSummaryDto[] result) {

				if (result != null) {
					final DataTable dataTable = DataTable.create();
					dataTable.addColumn(ColumnType.STRING, "Status");
					dataTable.addColumn(ColumnType.NUMBER, "Count");
					for (int i = 0; i < result.length; i++) {
						dataTable.addRow();
						dataTable.setValue(i, 0, result[i].getStatus());
						dataTable.setValue(i, 1, result[i].getCount());
					}
					Runnable onLoadCallback = new Runnable() {
						public void run() {
							if (pieChart != null) {
								// remove the old chart
								pieChart.removeFromParent();
							}
							pieChart = new PieChart(dataTable, createOptions());
							contentPane.add(pieChart);
						}
					};
					VisualizationUtils.loadVisualizationApi(onLoadCallback,
							PieChart.PACKAGE);

				}
			}
		};
		apService.listAccessPointStatusSummary(countryCode, communityCode,
				year, type, apCallback);
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

}
