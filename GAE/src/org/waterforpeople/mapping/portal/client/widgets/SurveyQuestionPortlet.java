package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyQuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.PieChart;
import com.google.gwt.visualization.client.visualizations.PieChart.Options;

/**
 * Displays summary information for a single survey question at a time.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyQuestionPortlet extends Portlet {
	public static final String DESCRIPTION = "Survey Question Responses as a Pie Chart";
	public static final String NAME = "Survey Answer Breakdown";
	private static final int WIDTH = 400;
	private static final int HEIGHT = 400;
	private VerticalPanel contentPane;
	private PieChart pieChart;

	private ListBox questionListbox;

	public SurveyQuestionPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		contentPane = new VerticalPanel();
		HorizontalPanel header = new HorizontalPanel();
		header.add(new Label("Survey Question: "));
		questionListbox = new ListBox();
		header.add(questionListbox);

		contentPane.add(header);
		setContent(contentPane);

		SurveyServiceAsync surveyService = GWT.create(SurveyService.class);
		// Set up the callback object.
		AsyncCallback<SurveyQuestionDto[]> surveyCallback = new AsyncCallback<SurveyQuestionDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(SurveyQuestionDto[] result) {
				if (result != null) {
					for (int i = 0; i < result.length; i++) {
						questionListbox.addItem(result[i].getQuestionText(),
								result[i].getQuestionId());

					}
					questionListbox.setVisibleItemCount(1);
					if (result.length > 0) {
						buildChart(result[0].getQuestionId());
					}
					questionListbox.addChangeHandler(new ChangeHandler() {

						@Override
						public void onChange(ChangeEvent event) {
							ListBox lb = (ListBox) event.getSource();
							buildChart(lb.getValue(lb.getSelectedIndex()));
						}
					});
				}
			}
		};
		surveyService.listSurveyQuestionByType("option", surveyCallback);

		

	}

	/**
	 * configures the Options to initialize the visualization
	 * 
	 * @return
	 */
	private Options createOptions() {
		Options options = Options.create();
		// this is needed so we can display html pop-ups over the flash content
		options.setHeight(HEIGHT-60);
		options.setWidth(WIDTH);
		return options;
	}

	private void buildChart(String question) {
		// fetch list of responses for a question
		SurveySummaryServiceAsync surveyService = GWT
				.create(SurveySummaryService.class);
		// Set up the callback object.
		AsyncCallback<SurveySummaryDto[]> surveyCallback = new AsyncCallback<SurveySummaryDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(final SurveySummaryDto[] result) {

				if (result != null) {					
					Runnable onLoadCallback = new Runnable() {
						public void run() {							
							if (pieChart != null) {
								// remove the old chart
								pieChart.removeFromParent();
							}
							DataTable dataTable = DataTable.create();
							dataTable.addColumn(ColumnType.STRING, "Response");
							dataTable.addColumn(ColumnType.NUMBER, "Count");
							for (int i = 0; i < result.length; i++) {
								dataTable.addRow();
								dataTable.setValue(i, 0, result[i].getResponseText());
								dataTable.setValue(i, 1, result[i].getCount());
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
		surveyService.listResponses(question, surveyCallback);
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
