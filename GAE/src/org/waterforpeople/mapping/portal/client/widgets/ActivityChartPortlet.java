package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.community.CommunityDto;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityService;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
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
public class ActivityChartPortlet extends Portlet implements ChangeHandler {
	public static final String DESCRIPTION = "Displays survey response activity over a configurable timeframe";
	public static final String NAME = "Survey Activity Over Time";
	private static final String ALL_OPT = "All";
	private static final int WIDTH = 600;
	private static final int HEIGHT = 300;
	private AnnotatedTimeLine timeLine;
	private ListBox countryListbox;
	private ListBox communityListbox;
	private VerticalPanel contentPane;

	public ActivityChartPortlet() {
		super(NAME, false, true, WIDTH, HEIGHT);
		contentPane = new VerticalPanel();
		contentPane.add(buildHeader());
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
						buildChart(null, null);
					}
				}
			}
		};
		communityService.listCountries(countryCallback);
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
							dataTable.addColumn(ColumnType.DATE, "Date");
							dataTable.addColumn(ColumnType.NUMBER, "Count");
							for (int i = 0; i < result.length; i++) {
								dataTable.addRow();
								dataTable.setValue(i, 0, result[i].getDate());
								dataTable.setValue(i, 1, result[i].getCount());
							}
							if (timeLine != null) {
								// remove the old chart
								timeLine.removeFromParent();
							}
							timeLine = new AnnotatedTimeLine(dataTable,
									createOptions(), WIDTH + "px",
									(HEIGHT - 60) + "px");
							contentPane.add(timeLine);
						}
					}
				};
				VisualizationUtils.loadVisualizationApi(onLoadCallback,
						AnnotatedTimeLine.PACKAGE);
			}
		};
		siService.listInstanceSummaryByLocation(countryCode, communityCode,
				siCallback);
	}

	/**
	 * checks if the country was changed and, if so, loads that country's
	 * communities. This will also trigger a reload of the pie chart
	 */
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
		buildChart(getSelectedValue(countryListbox),
				getSelectedValue(communityListbox));
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
		countryPanel.add(new Label("Country: "));
		countryListbox = new ListBox();
		countryPanel.add(countryListbox);
		grid.setWidget(0, 0, countryPanel);

		countryListbox.addChangeHandler(this);

		HorizontalPanel commPanel = new HorizontalPanel();
		commPanel.add(new Label("Community: "));
		communityListbox = new ListBox();
		commPanel.add(communityListbox);
		grid.setWidget(0, 1, commPanel);
		communityListbox.addChangeHandler(this);

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

	@Override
	public void handleEvent(PortletEvent e) {
	}

	@Override
	protected boolean getReadyForRemove() {
		return true;
	}

	@Override
	protected void handleConfigClick() {
		// TODO: handle configuration
	}

	public String getName() {
		return NAME;
	}
}
