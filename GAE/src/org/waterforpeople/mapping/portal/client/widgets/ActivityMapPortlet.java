package org.waterforpeople.mapping.portal.client.widgets;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.IntensityMap;
import com.google.gwt.visualization.client.visualizations.IntensityMap.Options;

public class ActivityMapPortlet extends Portlet {

	private static final int WIDTH = 600;
	private static final int HEIGHT= 300;
	private IntensityMap map;

	public ActivityMapPortlet() {
		super("Recent Activity", false, WIDTH, HEIGHT);
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				map = new IntensityMap(createTable(), createOptions());
				setContent(map);
			}
		};
		VisualizationUtils.loadVisualizationApi(onLoadCallback,
				IntensityMap.PACKAGE);
	}

	// TODO: get from DB
	private AbstractDataTable createTable() {
		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Country Code");
		data.addColumn(ColumnType.NUMBER, "Responses");
		data.addRows(5);
		data.setValue(0, 0, "MW");
		data.setValue(0, 1, 10);
		data.setValue(1, 0, "GT");
		data.setValue(1, 1, 6);
		data.setValue(2, 0, "PE");
		data.setValue(2, 1, 33);
		data.setValue(3, 0, "HN" );
		data.setValue(3, 1, 2);
		data.setValue(4, 0,"IN");
		data.setValue(4, 1, 18);
		return data;
	}

	private Options createOptions() {
		Options options = Options.create();
		options.setWidth(WIDTH);
		options.setHeight(HEIGHT);
		return options;
	}

	@Override
	public void handleEvent(PortletEvent e) {
		// TODO Auto-generated method stub
		
	}
}
