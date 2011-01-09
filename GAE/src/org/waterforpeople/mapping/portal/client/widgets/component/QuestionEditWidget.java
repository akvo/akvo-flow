package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;

import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class QuestionEditWidget extends Composite implements ContextAware,
		ChangeHandler, ClickHandler {

	private static final String INPUT_LABEL_CSS = "input-label";
	private static final String DEFAULT_BOX_WIDTH = "300px";
	private static final String SELECT_TXT = "Select...";
	private VerticalPanel panel;
	private TextArea questionTextArea;
	private ListBox questionTypeSelector;
	private TextArea tooltipArea;
	private TextBox validationRuleBox;
	private CheckBox mandatoryBox;
	private CheckBox dependentBox;
	private ListBox dependentQuestionSelector;
	private ListBox dependentAnswerSelector;
	private CaptionPanel dependencyPanel;

	private CheckBox allowOtherBox;
	private CheckBox allowMultipleBox;
	private Button addOptionButton;
	private CaptionPanel optionPanel;
	private SurveyServiceAsync surveyService;
	private Map<String, Object> bundle;
	private QuestionDto currentQuestion;
	private Map<Long, List<QuestionDto>> optionQuestions;

	public QuestionEditWidget() {
		surveyService = GWT.create(SurveyService.class);
		installWidgets();
		initWidget(panel);
	}

	private void installWidgets() {
		panel = new VerticalPanel();
		questionTextArea = new TextArea();
		tooltipArea = new TextArea();
		validationRuleBox = new TextBox();
		mandatoryBox = new CheckBox();
		dependentBox = new CheckBox();
		dependentBox.addClickHandler(this);
		questionTypeSelector = new ListBox();
		questionTypeSelector.addItem("Free Text",
				QuestionDto.QuestionType.FREE_TEXT.toString());
		questionTypeSelector.addItem("Option", QuestionDto.QuestionType.OPTION
				.toString());
		questionTypeSelector.addItem("Number", QuestionDto.QuestionType.NUMBER
				.toString());
		questionTypeSelector.addItem("Geo", QuestionDto.QuestionType.GEO
				.toString());
		questionTypeSelector.addItem("Photo", QuestionDto.QuestionType.PHOTO
				.toString());
		questionTypeSelector.addItem("Video", QuestionDto.QuestionType.VIDEO
				.toString());
		questionTypeSelector.addItem("Strength",
				QuestionDto.QuestionType.STRENGTH.toString());
		questionTypeSelector.addChangeHandler(this);
		CaptionPanel basePanel = new CaptionPanel("Question Basics:");

		Grid grid = new Grid(7, 2);
		basePanel.add(grid);

		installRow("Question Text", questionTextArea, grid, 0);
		installRow("Question Type", questionTypeSelector, grid, 1);
		installRow("Tooltip", tooltipArea, grid, 2);
		installRow("Validation Rule", validationRuleBox, grid, 3);
		installRow("Mandatory", mandatoryBox, grid, 4);
		installRow("Dependent", dependentBox, grid, 5);

		dependencyPanel = new CaptionPanel("Dependency Details:");
		dependentQuestionSelector = new ListBox();
		dependentQuestionSelector.setWidth(DEFAULT_BOX_WIDTH);
		dependentQuestionSelector.addChangeHandler(this);
		dependentQuestionSelector.addItem(SELECT_TXT);

		dependentAnswerSelector = new ListBox();
		dependentAnswerSelector.addItem(SELECT_TXT);
		dependentAnswerSelector.setWidth(DEFAULT_BOX_WIDTH);

		Grid depGrid = new Grid(2, 2);
		dependencyPanel.add(depGrid);
		installRow(null, dependencyPanel, grid, 6, 1);
		dependencyPanel.setVisible(false);
		installRow("Question", dependentQuestionSelector, depGrid, 0);
		installRow("Response", dependentAnswerSelector, depGrid, 1);

		panel.add(basePanel);

		allowMultipleBox = new CheckBox();
		allowOtherBox = new CheckBox();
		addOptionButton = new Button("Add Option");

		optionPanel = new CaptionPanel("Option Details:");
		Grid optGrid = new Grid(2, 4);
		installRow("Allow Multiple", allowMultipleBox, optGrid, 0, 0);
		installRow("Allow 'Other'", allowOtherBox, optGrid, 0, 2);
		installRow(null, addOptionButton, optGrid, 1);

		optionPanel.add(optGrid);
		optionPanel.setVisible(false);
		panel.add(optionPanel);
	}

	private void installRow(String labelText, Widget widget, Grid parent,
			int row) {
		installRow(labelText, widget, parent, row, 0);
	}

	private void installRow(String labelText, Widget widget, Grid parent,
			int row, int colOffset) {
		if (labelText != null) {
			Label label = new Label();
			label.setStylePrimaryName(INPUT_LABEL_CSS);
			label.setText(labelText);
			parent.setWidget(row, colOffset, label);
			parent.setWidget(row, colOffset + 1, widget);

		} else {
			parent.setWidget(row, colOffset, widget);
		}
	}

	private void populateFields() {

	}

	private void loadOptions() {

	}

	private void loadDependencyList() {

		if (optionQuestions != null
				&& optionQuestions.get(currentQuestion.getSurveyId()) != null) {
			populateDependencySelection(currentQuestion, optionQuestions
					.get(currentQuestion.getSurveyId()));
		} else {
			final MessageDialog dia = new MessageDialog("Please wait",
					"Loading question details...", true);
			dia.showRelativeTo(dependencyPanel);
			surveyService.listSurveyQuestionByType(currentQuestion
					.getSurveyId(), QuestionType.OPTION,
					new AsyncCallback<QuestionDto[]>() {

						@Override
						public void onFailure(Throwable caught) {
							dia.hide();
							MessageDialog errDia = new MessageDialog(
									"Error loading questions",
									"Could not load questions for dependency selection: "
											+ caught.getMessage());
							errDia.showRelativeTo(dependencyPanel);
						}

						@Override
						public void onSuccess(QuestionDto[] result) {
							if (optionQuestions == null) {
								optionQuestions = new HashMap<Long, List<QuestionDto>>();
							}
							List<QuestionDto> questionList = Arrays
									.asList(result);
							optionQuestions.put(currentQuestion.getSurveyId(),
									questionList);
							getContextBundle().put(
									BundleConstants.OPTION_QUESTION_LIST_KEY,
									optionQuestions);

							populateDependencySelection(currentQuestion,
									questionList);
							dia.hide();
						}
					});
		}
	}

	private void populateDependencySelection(QuestionDto currentQuestion,
			List<QuestionDto> questionList) {
		if (questionList != null) {
			// for(QuestionDto q: questionList){
			for (int i = 0; i < questionList.size(); i++) {
				QuestionDto q = questionList.get(i);
				dependentQuestionSelector.addItem(q.getText(), q.getKeyId()
						.toString());
				if (currentQuestion != null
						&& currentQuestion.getQuestionDependency() != null
						&& currentQuestion.getQuestionDependency()
								.getQuestionId().equals(q.getKeyId())) {
					dependentQuestionSelector.setSelectedIndex(i + 1);
				}
			}
		}
	}

	private void loadDependentQuestionAnswers(String questionId) {
		List<QuestionDto> questionList = optionQuestions.get(currentQuestion
				.getSurveyId());
		QuestionDto question = null;
		if (questionList != null) {
			for (QuestionDto q : questionList) {
				if (q.getKeyId().toString().equals(questionId)) {
					question = q;
				}
			}
		}
		if (question != null) {
			if (question.getOptionContainerDto() != null) {
				populateDependencyAnswers(currentQuestion, question
						.getOptionContainerDto().getOptionsList());
			} else {
				final MessageDialog dia = new MessageDialog("Please wait",
						"Loading question details...", true);
				dia.showRelativeTo(dependencyPanel);
				// if the option container is null, we probably have not
				// yet loaded the question details. so do it now
				surveyService.loadQuestionDetails(question.getKeyId(),
						new AsyncCallback<QuestionDto>() {
							@Override
							public void onSuccess(QuestionDto result) {
								if (result.getOptionContainerDto() != null) {
									populateDependencyAnswers(currentQuestion,
											result.getOptionContainerDto()
													.getOptionsList());
								}
								dia.hide();
							}

							@Override
							public void onFailure(Throwable caught) {
								dia.hide();
								Window.alert("Could not load answers");
							}
						});
			}
		}
	}

	private void populateDependencyAnswers(QuestionDto currentQuestion,
			List<QuestionOptionDto> options) {
		// first, clear out the existing data
		dependentAnswerSelector.clear();
		// now add the "select" item
		dependentAnswerSelector.addItem(SELECT_TXT);
		if (options != null) {
			for (int i = 0; i < options.size(); i++) {
				dependentAnswerSelector.addItem(options.get(i).getText(),
						options.get(i).getKeyId().toString());
			}
		}
	}

	@Override
	public Map<String, Object> getContextBundle() {
		if (bundle == null) {
			bundle = new HashMap<String, Object>();
		}
		return bundle;
	}

	@Override
	public void persistContext(CompletionListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	@SuppressWarnings("unchecked")
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		currentQuestion = (QuestionDto) bundle
				.get(BundleConstants.QUESTION_KEY);
		optionQuestions = (Map<Long, List<QuestionDto>>) bundle
				.get(BundleConstants.OPTION_QUESTION_LIST_KEY);
		if (currentQuestion != null) {
			populateFields();
		} else {
			currentQuestion = new QuestionDto();
			QuestionGroupDto currentGroup = (QuestionGroupDto) bundle
					.get(BundleConstants.QUESTION_GROUP_KEY);
			currentQuestion.setSurveyId(currentGroup.getSurveyId());
			currentQuestion.setPath(currentGroup.getPath() + "/"
					+ currentGroup.getCode());
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == questionTypeSelector) {
			if (QuestionDto.QuestionType.OPTION.toString().equals(
					questionTypeSelector.getValue(questionTypeSelector
							.getSelectedIndex()))) {
				optionPanel.setVisible(true);
				loadOptions();
			} else {
				optionPanel.setVisible(false);
			}
		} else if (event.getSource() == dependentQuestionSelector) {
			int index = dependentQuestionSelector.getSelectedIndex();
			if (index > 0) {
				loadDependentQuestionAnswers(dependentQuestionSelector
						.getValue(index));
			} else {
				populateDependencyAnswers(currentQuestion, null);
			}
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == dependentBox) {
			if (dependentBox.getValue()) {
				dependencyPanel.setVisible(true);
				loadDependencyList();
			} else {
				dependencyPanel.setVisible(false);
			}
		}
	}
}