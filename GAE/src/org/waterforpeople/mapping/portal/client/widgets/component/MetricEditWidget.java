package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.MetricDto;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricService;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * widget used to edit/create Metric objects
 * 
 * TODO: handle user organization
 * 
 * @author Christopher Fagiani
 * 
 */
public class MetricEditWidget extends Composite {
	public static final String METRIC_PAYLOAD_KEY = "metric";
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	private static final String STRING_TYPE = "String";
	private static final String DOUBLE_TYPE = "Double";

	private MetricDto metric;
	private ListBox valueTypeListBox;
	private TextBox nameTextBox;
	private TextBox groupTextBox;
	private Grid grid;
	private MetricServiceAsync metricService;
	private CompletionListener completionListener;

	public MetricEditWidget(MetricDto metric, CompletionListener listener) {
		this.metric = metric;
		completionListener = listener;
		metricService = GWT.create(MetricService.class);
		grid = new Grid(3, 2);
		valueTypeListBox = new ListBox();
		valueTypeListBox.addItem(TEXT_CONSTANTS.text(), STRING_TYPE);
		valueTypeListBox.addItem(TEXT_CONSTANTS.number(), DOUBLE_TYPE);
		nameTextBox = new TextBox();
		groupTextBox = new TextBox();
		grid.setWidget(0, 0, ViewUtil.initLabel(TEXT_CONSTANTS.name()));
		grid.setWidget(0, 1, nameTextBox);
		grid.setWidget(1, 0, ViewUtil.initLabel(TEXT_CONSTANTS.group()));
		grid.setWidget(1, 1, groupTextBox);
		grid.setWidget(2, 0, ViewUtil.initLabel(TEXT_CONSTANTS.valueType()));
		grid.setWidget(2, 1, valueTypeListBox);
		initWidget(grid);
		if (metric != null) {
			if (metric.getName() != null) {
				nameTextBox.setText(metric.getName());
			}
			if (metric.getGroup() != null) {
				groupTextBox.setText(metric.getGroup());
			}
			if (metric.getValueType() != null) {
				ViewUtil.setListboxSelection(valueTypeListBox,
						metric.getValueType());
			}
		}
	}

	public void saveMetric() {

		if (metric == null) {
			metric = new MetricDto();
		}
		metric.setName(nameTextBox.getText());
		metric.setGroup(groupTextBox.getText());
		metric.setValueType(ViewUtil.getListBoxSelection(valueTypeListBox,
				false));
		if (metric.getName() == null) {
			MessageDialog dia = new MessageDialog(TEXT_CONSTANTS.inputError(),
					TEXT_CONSTANTS.nameMandatory());
			dia.showCentered();
		} else {
			metricService.saveMetric(metric, new AsyncCallback<MetricDto>() {

				@Override
				public void onFailure(Throwable caught) {
					MessageDialog dia = new MessageDialog(TEXT_CONSTANTS
							.error(), TEXT_CONSTANTS.errorTracePrefix() + " "
							+ caught.getLocalizedMessage());
					dia.showCentered();

				}

				@Override
				public void onSuccess(MetricDto result) {
					if (completionListener != null) {
						Map<String, Object> payload = new HashMap<String, Object>();
						payload.put(METRIC_PAYLOAD_KEY, result);
						completionListener.operationComplete(true, payload);
					}
				}
			});
		}
	}
}
