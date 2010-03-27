package org.waterforpeople.mapping.portal.client.widgets;

import java.util.Date;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine.Options;

public class ActivityChartPortlet extends Portlet {
	private static final int WIDTH = 600;
	private static final int HEIGHT = 300;
	private AnnotatedTimeLine timeLine;

	public ActivityChartPortlet() {
		super("Recent Activity", false, WIDTH, HEIGHT);
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

	private Options createOptions() {
		Options options = Options.create();
		return options;
	}
}
