package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.portal.client.widgets.component.WidgetDialog;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.ImagePieChart;
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
	private static final int MAX_LEN = 40;

	private SurveyServiceAsync surveyService;

	private VerticalPanel contentPane;
	private PieChart pieChart;
	private ListBox questionListbox;
	private ListBox surveyGroupListbox;
	private ListBox surveyListbox;
	private AbstractDataTable currentTable;

	public SurveyQuestionPortlet() {
		super(NAME, false, false, true, WIDTH, HEIGHT);
		contentPane = new VerticalPanel();
		questionListbox = new ListBox();
		surveyGroupListbox = new ListBox();
		surveyListbox = new ListBox();

		VerticalPanel header = new VerticalPanel();
		HorizontalPanel line = new HorizontalPanel();
		line.add(new Label("Survey Group: "));
		line.add(surveyGroupListbox);
		line.add(new Label("Survey: "));
		line.add(surveyListbox);
		header.add(line);
		line = new HorizontalPanel();
		line.add(new Label("Survey Question: "));
		line.add(questionListbox);
		header.add(line);

		contentPane.add(header);
		setContent(contentPane);
		surveyService = GWT.create(SurveyService.class);
		loadSurveyGroups();

	}

	private void loadSurveyGroups() {

		// Set up the callback object.
		AsyncCallback<ArrayList<SurveyGroupDto>> surveyGroupCallback = new AsyncCallback<ArrayList<SurveyGroupDto>>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(ArrayList<SurveyGroupDto> result) {
				surveyGroupListbox.addItem("", "");
				if (result != null) {
					for (int i = 0; i < result.size(); i++) {
						surveyGroupListbox.addItem(result.get(i)
								.getDisplayName(), result.get(i).getKeyId()
								.toString());

					}
					surveyGroupListbox.setVisibleItemCount(1);
					surveyGroupListbox.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							ListBox lb = (ListBox) event.getSource();
							if (lb.getSelectedIndex() == 0) {
								surveyListbox.clear();
							} else {
								loadSurveys(lb.getValue(lb.getSelectedIndex()));
							}
						}
					});
				}
			}
		};
		surveyService.listSurveyGroups(null, false, false, false,
				surveyGroupCallback);
	}

	private void loadSurveys(String groupId) {
		surveyListbox.clear();
		// Set up the callback object.
		AsyncCallback<ArrayList<SurveyDto>> surveyCallback = new AsyncCallback<ArrayList<SurveyDto>>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(ArrayList<SurveyDto> result) {
				surveyListbox.addItem("", "");
				if (result != null) {
					for (int i = 0; i < result.size(); i++) {
						surveyListbox.addItem(result.get(i).getDisplayName(),
								result.get(i).getKeyId().toString());

					}
					surveyListbox.setVisibleItemCount(1);
					surveyListbox.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							ListBox lb = (ListBox) event.getSource();
							if (lb.getSelectedIndex() > 0) {
								loadSurveyQuestion(Long.parseLong(lb
										.getValue(lb.getSelectedIndex())));
							} else {
								questionListbox.clear();
							}
						}
					});
				}
			}
		};
		surveyService.listSurveysByGroup(groupId, surveyCallback);
	}

	private void loadSurveyQuestion(Long surveyId) {
		questionListbox.clear();
		// Set up the callback object.
		AsyncCallback<QuestionDto[]> surveyQuestionCallback = new AsyncCallback<QuestionDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(QuestionDto[] result) {
				if (result != null) {
					for (int i = 0; i < result.length; i++) {
						String text = result[i].getText();
						if (text != null && text.length() > MAX_LEN) {
							text = text.substring(0, MAX_LEN) + "...";
						}
						questionListbox.addItem(text, result[i].getKeyId()
								.toString());

					}
					questionListbox.setVisibleItemCount(1);
					if (result.length > 0) {
						buildChart(result[0].getKeyId().toString());
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
		surveyService.listSurveyQuestionByType(surveyId, QuestionType.OPTION,
				surveyQuestionCallback);
	}

	/**
	 * configures the Options to initialize the visualization
	 * 
	 * @return
	 */
	private Options createOptions() {
		Options options = Options.create();
		// this is needed so we can display html pop-ups over the flash content
		options.setHeight(HEIGHT - 60);
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
								dataTable.setValue(i, 0, result[i]
										.getResponseText());
								dataTable.setValue(i, 1, result[i].getCount());
							}
							pieChart = new PieChart(dataTable, createOptions());
							contentPane.add(pieChart);
							currentTable = dataTable;

						}
					};
					VisualizationUtils.loadVisualizationApi(onLoadCallback,
							"corechart");
				}
			}
		};
		surveyService.listResponses(question, surveyCallback);
	}

	public String getName() {
		return NAME;
	}

	@Override
	public void handleExportClick() {
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				ImagePieChart.Options options = ImagePieChart.Options.create();
				options.setHeight(HEIGHT - 60);
				options.setWidth(WIDTH);
				ImagePieChart ipc = new ImagePieChart(currentTable, options);
				WidgetDialog dia = new WidgetDialog(NAME, ipc);
				dia.showRelativeTo(getHeaderWidget());
			}
		};
		if (currentTable != null) {
			VisualizationUtils.loadVisualizationApi(onLoadCallback,
					ImagePieChart.PACKAGE);
		}
	}

}
