package org.waterforpeople.mapping.portal.client.widgets;

import java.util.Date;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.visualization.client.AbstractDataTable;
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
public class ActivityChartPortlet extends Portlet {
	public static final String DESCRIPTION = "Displays survey response activity over a configurable timeframe";
	public static final String NAME = "Survey Activity Over Time";
	private static final int WIDTH = 600;
	private static final int HEIGHT = 300;
	private AnnotatedTimeLine timeLine;

	public ActivityChartPortlet() {
		super(NAME, false, true, WIDTH, HEIGHT);
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				timeLine = new AnnotatedTimeLine(createTable(),
						createOptions(), WIDTH + "", "" + HEIGHT);
				setContent(timeLine);

			}
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				AnnotatedTimeLine.PACKAGE);
	}

	// TODO: get from DB
	private AbstractDataTable createTable() {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.DATE, "Day");
		data.addColumn(ColumnType.NUMBER, "Responses");
		data.addRows(5);
		data.setValue(0, 0, new Date(2010, 3, 1));
		data.setValue(0, 1, 10);
		data.setValue(1, 0, new Date(2010, 3, 2));
		data.setValue(1, 1, 6);
		data.setValue(2, 0, new Date(2010, 3, 3));
		data.setValue(2, 1, 33);
		data.setValue(3, 0, new Date(2010, 3, 4));
		data.setValue(3, 1, 2);
		data.setValue(4, 0, new Date(2010, 3, 5));
		data.setValue(4, 1, 18);
		return data;
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

	public String getName(){
		return NAME;
	}
}
