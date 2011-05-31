package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveySummaryServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.LegendPosition;
import com.google.gwt.visualization.client.VisualizationUtils;
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
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	public static final String DESCRIPTION = TEXT_CONSTANTS
			.surveyQuestionPortletDescription();;
	public static final String NAME = TEXT_CONSTANTS
			.surveyQuestionPortletTitle();
	private static final int WIDTH = 400;
	private static final int HEIGHT = 400;
	private static final int MAX_LEN = 40;

	private SurveyServiceAsync surveyService;

	private VerticalPanel contentPane;
	private PieChart pieChart;
	private ListBox questionListbox;
	private Map<Long, String> fullQuestionMap;
	private Map<String, QuestionDto> questionsForTrans;
	private ListBox surveyGroupListbox;
	private ListBox surveyListbox;
	private AbstractDataTable currentTable;
	private Label noDataLabel;
	private boolean needTranslations;
	private String locale;

	public SurveyQuestionPortlet() {
		super(NAME, false, false, true, WIDTH, HEIGHT);
		contentPane = new VerticalPanel();
		questionListbox = new ListBox();
		surveyGroupListbox = new ListBox();
		surveyListbox = new ListBox();
		fullQuestionMap = new HashMap<Long, String>();
		questionsForTrans = new HashMap<String, QuestionDto>();
		locale = com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale()
				.getLocaleName();
		if ("Default".equalsIgnoreCase(locale) || "en".equalsIgnoreCase(locale)) {
			needTranslations = false;
		} else {
			needTranslations = true;
		}

		VerticalPanel header = new VerticalPanel();
		HorizontalPanel line = new HorizontalPanel();
		line.add(ViewUtil.initLabel(TEXT_CONSTANTS.surveyGroup()));
		line.add(surveyGroupListbox);
		line.add(ViewUtil.initLabel(TEXT_CONSTANTS.survey()));
		line.add(surveyListbox);
		header.add(line);
		line = new HorizontalPanel();
		line.add(ViewUtil.initLabel(TEXT_CONSTANTS.question()));
		line.add(questionListbox);
		header.add(line);

		contentPane.add(header);
		setContent(contentPane);
		surveyService = GWT.create(SurveyService.class);
		loadSurveyGroups();

	}

	private void loadSurveyGroups() {

		// Set up the callback object.
		AsyncCallback<ResponseDto<ArrayList<SurveyGroupDto>>> surveyGroupCallback = new AsyncCallback<ResponseDto<ArrayList<SurveyGroupDto>>>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(
					ResponseDto<ArrayList<SurveyGroupDto>> response) {
				ArrayList<SurveyGroupDto> result = response.getPayload();
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
		surveyService.listSurveyGroups("all", false, false, false,
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
		fullQuestionMap.clear();
		// Set up the callback object.
		AsyncCallback<QuestionDto[]> surveyQuestionCallback = new AsyncCallback<QuestionDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(QuestionDto[] result) {
				if (result != null) {
					for (int i = 0; i < result.length; i++) {
						String textToUse = getLocalizedText(
								result[i].getText(),
								result[i].getTranslationMap());
						fullQuestionMap.put(result[i].getKeyId(), textToUse);
						if (textToUse != null && textToUse.length() > MAX_LEN) {
							textToUse = textToUse.substring(0, MAX_LEN) + "...";
						}
						questionListbox.addItem(textToUse, result[i].getKeyId()
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
				needTranslations, surveyQuestionCallback);
	}

	/**
	 * configures the Options to initialize the visualization
	 * 
	 * @return
	 */
	private Options createOptions(String title) {
		Options options = Options.create();
		// this is needed so we can display html pop-ups over the flash content
		options.setHeight(HEIGHT - 60);
		options.setWidth(WIDTH);
		if (title != null) {
			options.setTitle(title);
		}
		return options;
	}

	private void buildChart(final String question) {
		// fetch list of responses for a question
		if (needTranslations && questionsForTrans.get(question) == null) {
			surveyService.loadQuestionDetails(Long.parseLong(question),
					new AsyncCallback<QuestionDto>() {

						@Override
						public void onFailure(Throwable caught) {
							noDataLabel.setVisible(true);
							noDataLabel.setText(TEXT_CONSTANTS.error());
						}

						@Override
						public void onSuccess(QuestionDto result) {
							questionsForTrans.put(question, result);
							// call build chart, but this time, the check for
							// the details will not be null so we won't hit the
							// service call again
							buildChart(question);

						}
					});
		} else {
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
								if (noDataLabel != null) {
									noDataLabel.removeFromParent();
								}
								DataTable dataTable = DataTable.create();
								dataTable.addColumn(ColumnType.STRING,
										TEXT_CONSTANTS.response());
								dataTable.addColumn(ColumnType.NUMBER,
										TEXT_CONSTANTS.count());
								for (int i = 0; i < result.length; i++) {
									dataTable.addRow();
									dataTable
											.setValue(
													i,
													0,
													handleResponseText(
															result[i]
																	.getResponseText(),
															question));
									dataTable.setValue(i, 1,
											result[i].getCount());
								}
								if (result.length > 0) {
									pieChart = new PieChart(dataTable,
											createOptions(fullQuestionMap
													.get(new Long(question))));
									contentPane.add(pieChart);
									currentTable = dataTable;
								} else {
									noDataLabel = new Label(
											TEXT_CONSTANTS.noData());
									contentPane.add(noDataLabel);
								}

							}
						};
						VisualizationUtils.loadVisualizationApi(onLoadCallback,
								"corechart");
					}
				}
			};
			surveyService.listResponses(question, surveyCallback);
		}
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
				options.setWidth(WIDTH + 60);
				options.setLabels("value");
				options.setLegend(LegendPosition.RIGHT);
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

	/**
	 * translates the option text if translations are present for the current
	 * locale. This method will also handle translating "multi-selected"
	 * options.
	 * 
	 * @param text
	 * @param question
	 * @return
	 */
	private String handleResponseText(String text, String question) {
		QuestionDto q = questionsForTrans.get(question);
		String processedText = text;
		if (text != null && text.trim().length() > 0 && q != null
				&& q.getOptionContainerDto() != null
				&& q.getOptionContainerDto().getOptionsList() != null) {
			StringBuilder builder = new StringBuilder();
			String[] tokens = text.split("\\|");
			// see if we have a translation for this option
			for (int i = 0; i < tokens.length; i++) {
				if (i > 0) {
					builder.append("|");
				}
				boolean found = false;
				for (QuestionOptionDto opt : q.getOptionContainerDto()
						.getOptionsList()) {
					if (opt.getText() != null
							&& opt.getText().trim()
									.equalsIgnoreCase(tokens[i].trim())) {
						builder.append(getLocalizedText(tokens[i],
								opt.getTranslationMap()));
						found = true;
						break;
					}
				}
				if (!found) {
					builder.append(tokens[i]);
				}
			}
			processedText = builder.toString();
		}
		return processedText;
	}

	/**
	 * uses the locale and the translation map passed in to determine what value
	 * to use for the string TODO: relocate this method to a shared util
	 * 
	 * @param text
	 * @param translationMap
	 * @return
	 */
	private String getLocalizedText(String text,
			Map<String, TranslationDto> translationMap) {
		TranslationDto trans = null;
		if (translationMap != null) {
			trans = translationMap.get(locale);
		}
		if (trans != null && trans.getText() != null
				&& trans.getText().trim().length() > 0) {
			return trans.getText();
		} else {
			return text;

		}
	}
}
