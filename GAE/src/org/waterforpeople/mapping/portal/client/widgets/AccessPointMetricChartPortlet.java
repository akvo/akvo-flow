package org.waterforpeople.mapping.portal.client.widgets;

import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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

	private static final int WIDTH = 400;
	private static final int HEIGHT = 400;
	private ListBox metricListbox;
	private Panel contentPane;

	public AccessPointMetricChartPortlet(UserDto user) {
		super(NAME, false, false, true, WIDTH, HEIGHT, user, false, 3,
				LocationDrivenPortlet.ALL_OPT);
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
		// metricListbox.addItem(TEXT_CONSTANTS.cost(), COST_METRIC);
		// metricListbox.addItem(TEXT_CONSTANTS.householdsServed(),
		// COUNT_METRIC);
		// metricListbox.addItem("Status", STATUS_METRIC);
		metricListbox.addChangeHandler(this);

		VerticalPanel headerPanel = new VerticalPanel();

		HorizontalPanel controlPanel = new HorizontalPanel();

		controlPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.type()));

		controlPanel.add(ViewUtil.initLabel(TEXT_CONSTANTS.metric()));
		controlPanel.add(metricListbox);

		headerPanel.add(getCountryControl());
		List<ListBox> boxes = getSubLevelControls();
		if (boxes != null) {
			for (ListBox box : boxes) {
				headerPanel.add(box);
			}
		}

		headerPanel.add(controlPanel);

		return headerPanel;
	}

	@Override
	public void onChange(ChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
