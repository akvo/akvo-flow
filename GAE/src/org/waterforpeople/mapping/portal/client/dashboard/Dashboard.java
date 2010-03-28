package org.waterforpeople.mapping.portal.client.dashboard;

import org.waterforpeople.mapping.portal.client.widgets.ActivityChartPortlet;
import org.waterforpeople.mapping.portal.client.widgets.ActivityMapPortlet;
import org.waterforpeople.mapping.portal.client.widgets.SummaryPortlet;

import com.gallatinsystems.framework.gwt.portlet.client.PortalContainer;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * Main container for the dashboard.
 * 
 * TODO: make this load the list of widgets and their relative position/order
 * from the datastore on a per-user basis
 * 
 * 
 * @author Christopher Fagiani
 * 
 */
public class Dashboard extends PortalContainer implements EntryPoint {
	private static final int COLUMNS = 3;

	public Dashboard() {
		super(COLUMNS);
	}

	public void onModuleLoad() {
		RootPanel.get().setPixelSize(1024, 768);
		RootPanel.get().getElement().getStyle().setProperty("position",
				"relative");

		VerticalPanel containerPanel = new VerticalPanel();
		containerPanel.add(new Image("images/WFP_Logo.png"));
		RootPanel.get().add(containerPanel);

		addPortlet(new SummaryPortlet(), 0, true);
		addPortlet(new ActivityChartPortlet(), 1, true);
		addPortlet(new ActivityMapPortlet(), 1, true);
		// now add the portal container to the vertical panel
		containerPanel.add(this);
	}

	@Override
	public Class<?>[] getInvolvedClasses() {
		return new Class[] { this.getClass(), SummaryPortlet.class,
				ActivityChartPortlet.class, ActivityMapPortlet.class };
	}
}
