package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.portal.client.widgets.component.RunReportWidget;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;

/**
 * Portlet that allows the user to select a report to run.
 * 
 * @author Christopher Fagiani
 * 
 */
public class RunReportsPortlet extends Portlet {

	public static final String NAME = "Run Reports";
	private static final int WIDTH = 800;
	private static final int HEIGHT = 800;
	private RunReportWidget reportWidget;

	public RunReportsPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		reportWidget = new RunReportWidget();
		setContent(reportWidget);
	}

	@Override
	public String getName() {
		return NAME;
	}

}
