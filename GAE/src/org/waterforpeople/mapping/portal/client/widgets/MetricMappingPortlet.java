package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.MetricMappingWidget;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.google.gwt.core.client.GWT;

/**
 * portlet wrapper for the MetricMapping widget
 * 
 * @author Christopher Fagiani
 * 
 */
public class MetricMappingPortlet extends Portlet {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String NAME = TEXT_CONSTANTS.metricMapping();

	private static final int WIDTH = 1600;
	private static final int HEIGHT = 800;

	public MetricMappingPortlet() {
		super(NAME, true, false, false, WIDTH, HEIGHT);
		MetricMappingWidget mappingWidget = new MetricMappingWidget();
		setContent(mappingWidget);
		mappingWidget.renderInitialState();
	}

	@Override
	public String getName() {
		return NAME;
	}
}
