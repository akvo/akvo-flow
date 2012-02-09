package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.survey.MetricDto;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricService;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDependencyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyMetricMappingDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyMetricMappingService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyMetricMappingServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyNavigationWidget.MODE;

import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.gallatinsystems.user.app.gwt.client.PermissionConstants;
import com.gallatinsystems.user.app.gwt.client.UserDto;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget for creating and editing a Survey Question.
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionEditWidget extends Composite implements ContextAware,
		ChangeHandler, ClickHandler, TranslationChangeListener,
		CompletionListener {
	private static Logger logger = Logger.getLogger("");

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final int MAX_LEN = 500;
	private static final String REORDER_BUTTON_CSS = "reorder-button";
	private static final String DEFAULT_BOX_WIDTH = "300px";
	private static final String SELECT_TXT = TEXT_CONSTANTS.select() + "...";
	private static final String EDIT_TRANS_OP = "Edit Translation";
	private static final String EDIT_HELP_OP = "Edit Help";
	private static final String FWD = "FWD";
	private VerticalPanel panel;
	private CaptionPanel basePanel;
	private TextArea questionTextArea;
	private ListBox questionTypeSelector;
	private TextArea tooltipArea;
	private CheckBox allowDecimal;
	private CheckBox allowSign;
	private TextBox minVal;
	private TextBox maxVal;
	private CheckBox isName;
	private CheckBox mandatoryBox;
	private CheckBox dependentBox;
	private CheckBox collapseableBox;
	private CheckBox immutableBox;
	private ListBox dependentQuestionSelector;
	private ListBox dependentAnswerSelector;
	private ListBox metricSelector;
	private CaptionPanel dependencyPanel;
	private Button viewTreeButton;
	private CaptionPanel navPanel;
	private Grid dependencyGrid;
	private Panel numericValidationPanel;
	private Panel textValidationPanel;
	private Panel validationPanel;

	private CheckBox allowOtherBox;
	private CheckBox allowMultipleBox;
	private Button addOptionButton;
	private CaptionPanel optionPanel;
	private VerticalPanel optionContent;
	private FlexTable optionTable;
	private Label orderLabel;
	private SurveyServiceAsync surveyService;
	private MetricServiceAsync metricService;
	private SurveyMetricMappingServiceAsync metricMappingService;
	private Map<String, Object> bundle;
	private QuestionDto currentQuestion;
	private QuestionDto insertAboveQuestion;
	private SurveyDto currentSurvey;

	private Map<Long, List<QuestionDto>> optionQuestions;
	private Button editTranslationButton;
	private Button editHelpButton;

	private QuestionGroupDto questionGroup;
	private String operation;
	private boolean needTranslations;
	private String locale;
	private UserDto currentUser;
	private PageController controller;
	private List<MetricDto> metricList;

	public QuestionEditWidget(UserDto user, PageController controller) {
		surveyService = GWT.create(SurveyService.class);
		metricService = GWT.create(MetricService.class);
		metricMappingService = GWT.create(SurveyMetricMappingService.class);
		currentUser = user;
		this.controller = controller;
		optionQuestions = new HashMap<Long, List<QuestionDto>>();
		locale = com.google.gwt.i18n.client.LocaleInfo.getCurrentLocale()
				.getLocaleName();
		if ("Default".equalsIgnoreCase(locale) || "en".equalsIgnoreCase(locale)) {
			needTranslations = false;
		} else {
			needTranslations = true;
		}
		installWidgets();
		initWidget(panel);
	}

	/**
	 * installs all the UI elements in their respective panels (even invisible
	 * items).
	 */
	private void installWidgets() {
		panel = new VerticalPanel();
		questionTextArea = new TextArea();
		questionTextArea.setWidth(DEFAULT_BOX_WIDTH);
		tooltipArea = new TextArea();
		tooltipArea.setWidth(DEFAULT_BOX_WIDTH);

		orderLabel = ViewUtil.initLabel("");
		mandatoryBox = new CheckBox();
		collapseableBox = new CheckBox();
		immutableBox = new CheckBox();

		isName = new CheckBox();
		allowDecimal = new CheckBox();
		allowSign = new CheckBox();
		minVal = ViewUtil.constructNumericTextBox();
		maxVal = ViewUtil.constructNumericTextBox();

		viewTreeButton = new Button(TEXT_CONSTANTS.viewDependencyTree());
		viewTreeButton.setVisible(false);
		viewTreeButton.addClickHandler(this);
		dependentBox = new CheckBox();
		dependentBox.addClickHandler(this);
		questionTypeSelector = new ListBox();
		questionTypeSelector.addItem(TEXT_CONSTANTS.freeText(),
				QuestionDto.QuestionType.FREE_TEXT.toString());
		questionTypeSelector.addItem(TEXT_CONSTANTS.option(),
				QuestionDto.QuestionType.OPTION.toString());
		questionTypeSelector.addItem(TEXT_CONSTANTS.number(),
				QuestionDto.QuestionType.NUMBER.toString());
		questionTypeSelector.addItem(TEXT_CONSTANTS.geo(),
				QuestionDto.QuestionType.GEO.toString());
		questionTypeSelector.addItem(TEXT_CONSTANTS.photo(),
				QuestionDto.QuestionType.PHOTO.toString());
		questionTypeSelector.addItem(TEXT_CONSTANTS.video(),
				QuestionDto.QuestionType.VIDEO.toString());
		questionTypeSelector.addItem(TEXT_CONSTANTS.strength(),
				QuestionDto.QuestionType.STRENGTH.toString());
		questionTypeSelector.addItem(TEXT_CONSTANTS.date(),
				QuestionDto.QuestionType.DATE.toString());
		questionTypeSelector.addChangeHandler(this);

		metricSelector = new ListBox();
		metricSelector.addItem("", "");
		navPanel = new CaptionPanel(TEXT_CONSTANTS.surveyNavigation());
		panel.add(navPanel);
		basePanel = new CaptionPanel(TEXT_CONSTANTS.questionBasics());

		int row = 0;
		Grid grid = new Grid(11, 2);
		basePanel.add(grid);

		ViewUtil.installGridRow(TEXT_CONSTANTS.questionText(),
				questionTextArea, grid, row++);
		ViewUtil.installGridRow(TEXT_CONSTANTS.questionType(),
				questionTypeSelector, grid, row++);
		ViewUtil.installGridRow(TEXT_CONSTANTS.order(), orderLabel, grid, row++);
		ViewUtil.installGridRow(TEXT_CONSTANTS.tooltip(), tooltipArea, grid,
				row++);
		numericValidationPanel = new VerticalPanel();
		textValidationPanel = new VerticalPanel();
		validationPanel = new VerticalPanel();

		ViewUtil.installFieldRow(textValidationPanel, TEXT_CONSTANTS.isName(),
				isName, ViewUtil.DEFAULT_INPUT_LABEL_CSS);
		Grid validationGrid = new Grid(3, 2);

		validationGrid
				.setWidget(0, 0, ViewUtil.formFieldPair(TEXT_CONSTANTS.min(),
						minVal, ViewUtil.DEFAULT_INPUT_LABEL_CSS));
		validationGrid
				.setWidget(0, 1, ViewUtil.formFieldPair(TEXT_CONSTANTS.max(),
						maxVal, ViewUtil.DEFAULT_INPUT_LABEL_CSS));
		ViewUtil.installGridRow(TEXT_CONSTANTS.isSigned(), allowSign,
				validationGrid, 1);
		ViewUtil.installGridRow(TEXT_CONSTANTS.isDecimal(), allowDecimal,
				validationGrid, 2);
		numericValidationPanel.add(validationGrid);

		ViewUtil.installGridRow(TEXT_CONSTANTS.validationRules(),
				validationPanel, grid, row++);

		ViewUtil.installGridRow(TEXT_CONSTANTS.metric(), metricSelector, grid,
				row++);
		ViewUtil.installGridRow(TEXT_CONSTANTS.collapseable(), collapseableBox,
				grid, row++);
		ViewUtil.installGridRow(TEXT_CONSTANTS.immutable(), immutableBox, grid,
				row++);
		ViewUtil.installGridRow(TEXT_CONSTANTS.mandatory(), mandatoryBox, grid,
				row++);
		ViewUtil.installGridRow(TEXT_CONSTANTS.dependent(), dependentBox, grid,
				row++);

		dependencyPanel = new CaptionPanel(TEXT_CONSTANTS.dependencyDetails());
		dependentQuestionSelector = new ListBox();
		dependentQuestionSelector.setWidth(DEFAULT_BOX_WIDTH);
		dependentQuestionSelector.addChangeHandler(this);
		dependentQuestionSelector.addItem(SELECT_TXT);

		dependentAnswerSelector = new ListBox(true);
		dependentAnswerSelector.addChangeHandler(this);
		dependentAnswerSelector.addItem(SELECT_TXT);
		dependentAnswerSelector.setWidth(DEFAULT_BOX_WIDTH);
		dependentAnswerSelector.setVisibleItemCount(4);

		dependencyGrid = new Grid(3, 2);
		dependencyPanel.add(dependencyGrid);
		ViewUtil.installGridRow(null, dependencyPanel, grid, row++, 1, null);
		dependencyPanel.setVisible(false);
		ViewUtil.installGridRow(TEXT_CONSTANTS.question(),
				dependentQuestionSelector, dependencyGrid, 0);
		ViewUtil.installGridRow(TEXT_CONSTANTS.response(),
				dependentAnswerSelector, dependencyGrid, 1);
		dependencyGrid.setWidget(2, 1, viewTreeButton);
		panel.add(basePanel);

		allowMultipleBox = new CheckBox();
		allowOtherBox = new CheckBox();
		addOptionButton = new Button(TEXT_CONSTANTS.addOption());
		addOptionButton.addClickHandler(this);

		optionPanel = new CaptionPanel(TEXT_CONSTANTS.optionDetails());
		optionContent = new VerticalPanel();
		optionPanel.add(optionContent);
		optionTable = new FlexTable();
		Grid optGrid = new Grid(2, 4);
		ViewUtil.installGridRow(TEXT_CONSTANTS.allowMultiple(),
				allowMultipleBox, optGrid, 0, 0, null);
		ViewUtil.installGridRow(TEXT_CONSTANTS.allowOther(), allowOtherBox,
				optGrid, 0, 2, null);

		optionContent.add(optGrid);
		optionContent.add(optionTable);
		optionContent.add(addOptionButton);
		optionPanel.setVisible(false);
		panel.add(optionPanel);

		HorizontalPanel tempPanel = new HorizontalPanel();
		editTranslationButton = new Button(TEXT_CONSTANTS.editTranslations());
		editTranslationButton.addClickHandler(this);
		tempPanel.add(editTranslationButton);

		editHelpButton = new Button(TEXT_CONSTANTS.editHelpMedia());
		editHelpButton.addClickHandler(this);
		tempPanel.add(editHelpButton);
		panel.add(tempPanel);
	}

	/**
	 * populates the UI based on the values in the loaded QuestionDto
	 */
	private void populateFields() {
		boolean isEditable = currentQuestion.getImmutable() == null
				|| (!currentQuestion.getImmutable() || currentUser
						.hasPermission(PermissionConstants.EDIT_IMMUTABLITY));
		navPanel.add(new SurveyNavigationWidget(currentSurvey, questionGroup,
				currentQuestion.getOrder(), (insertAboveQuestion != null),
				MODE.QUESTION, controller, this));
		addOptionButton.setEnabled(isEditable);
		editTranslationButton.setEnabled(isEditable);
		editHelpButton.setEnabled(isEditable);
		questionTextArea.setText(currentQuestion.getText());
		questionTextArea.setEnabled(isEditable);
		if (currentQuestion.getTip() != null
				&& currentQuestion.getTip().trim().length() > 0
				&& !"null".equals(currentQuestion.getTip())) {
			tooltipArea.setText(currentQuestion.getTip());
			tooltipArea.setEnabled(isEditable);
		}
		if (currentQuestion.getOrder() != null) {
			orderLabel.setText(currentQuestion.getOrder().toString());
		}
		if (currentQuestion.getType() != null) {
			for (int i = 0; i < questionTypeSelector.getItemCount(); i++) {
				if (currentQuestion.getType().toString()
						.equals(questionTypeSelector.getValue(i))) {
					questionTypeSelector.setSelectedIndex(i);
					break;
				}
			}
			questionTypeSelector.setEnabled(isEditable);
		}
		if (QuestionDto.QuestionType.NUMBER == currentQuestion.getType()) {
			validationPanel.clear();
			validationPanel.add(numericValidationPanel);
			allowDecimal.setValue(currentQuestion.getAllowDecimal());
			allowDecimal.setEnabled(isEditable);
			allowSign.setValue(currentQuestion.getAllowSign());
			allowSign.setEnabled(isEditable);
			if (currentQuestion.getMaxVal() != null) {
				maxVal.setText(currentQuestion.getMaxVal().toString());
			}
			maxVal.setEnabled(isEditable);
			if (currentQuestion.getMinVal() != null) {
				minVal.setText(currentQuestion.getMinVal().toString());
			}
			minVal.setEnabled(isEditable);
			validationPanel.setVisible(true);
		} else if (QuestionDto.QuestionType.FREE_TEXT == currentQuestion
				.getType()) {
			validationPanel.clear();
			validationPanel.add(textValidationPanel);
			validationPanel.setVisible(true);
			isName.setValue(currentQuestion.getIsName());
			isName.setEnabled(isEditable);
		} else {
			validationPanel.setVisible(false);
		}
		if (currentQuestion.getCollapseable() != null) {
			collapseableBox.setValue(currentQuestion.getCollapseable());
		} else {
			collapseableBox.setValue(false);
		}
		collapseableBox.setEnabled(isEditable);

		if (currentQuestion.getImmutable() != null) {
			immutableBox.setValue(currentQuestion.getImmutable());
		} else {
			immutableBox.setValue(false);
		}
		if (!currentUser.hasPermission(PermissionConstants.EDIT_IMMUTABLITY)) {
			immutableBox.setEnabled(false);
		}
		if (currentQuestion.getMandatoryFlag() != null) {
			mandatoryBox.setValue(currentQuestion.getMandatoryFlag());
		}
		mandatoryBox.setEnabled(isEditable);
		if (currentQuestion.getQuestionDependency() != null
				&& currentQuestion.getQuestionDependency().getQuestionId() != null) {
			dependentBox.setValue(true);
			viewTreeButton.setVisible(true);
			loadDependencyList(isEditable);
		}
		dependentBox.setEnabled(isEditable);
		if (QuestionDto.QuestionType.OPTION == currentQuestion.getType()) {
			loadOptions(isEditable);
		}
	}

	/**
	 * fetches the QuestionOptions from the server if they haven't already been
	 * retrieved
	 */
	private void loadOptions(final boolean isEditable) {
		if (QuestionDto.QuestionType.OPTION == currentQuestion.getType()
				&& (currentQuestion.getOptionContainerDto() == null || currentQuestion
						.getOptionContainerDto().getOptionsList() == null)) {
			optionPanel.setVisible(true);
			showLoading(optionPanel, TEXT_CONSTANTS.loading());
			surveyService.loadQuestionDetails(currentQuestion.getKeyId(),
					new AsyncCallback<QuestionDto>() {

						@Override
						public void onFailure(Throwable caught) {
							showContent(optionPanel,
									new Label(TEXT_CONSTANTS.error()));
						}

						@Override
						public void onSuccess(QuestionDto result) {
							showContent(optionPanel, optionContent);
							currentQuestion = result;
							populateOptions(
									currentQuestion.getOptionContainerDto(),
									isEditable);
						}
					});
		} else {
			populateOptions(currentQuestion.getOptionContainerDto(), isEditable);
		}
	}

	/**
	 * populates the UI with data from all the QuestionOptionDto objects in the
	 * question.
	 * 
	 * @param optionContainer
	 */
	private void populateOptions(OptionContainerDto optionContainer,
			boolean isEditable) {
		if (optionContainer != null) {
			optionPanel.setVisible(true);
			// wipe out any old values
			optionTable.clear(true);
			allowMultipleBox.setValue(optionContainer.getAllowMultipleFlag());
			allowMultipleBox.setEnabled(isEditable);
			allowOtherBox.setValue(optionContainer.getAllowOtherFlag());
			allowOtherBox.setEnabled(isEditable);
			if (optionContainer != null
					&& optionContainer.getOptionsList() != null) {
				for (QuestionOptionDto opt : optionContainer.getOptionsList()) {
					installOptionRow(opt, isEditable);
				}
			}
		}
	}

	/**
	 * adds a row representing a Question Option to the UI. The rows support
	 * deletion as well as reordering.
	 * 
	 * @param opt
	 */
	private void installOptionRow(QuestionOptionDto opt, boolean isEditable) {
		int row = optionTable.getRowCount();
		optionTable.insertRow(row);
		TextBox optText = new TextBox();
		optText.setMaxLength(MAX_LEN);
		optText.setEnabled(isEditable);
		optionTable.setWidget(row, 0, optText);
		HorizontalPanel bp = new HorizontalPanel();
		final Image moveUp = new Image("/images/greenuparrow.png");
		final Image moveDown = new Image("/images/greendownarrow.png");
		final Button deleteButton = new Button(TEXT_CONSTANTS.remove());
		if (isEditable) {
			optText.setFocus(true);
		}
		ClickHandler optionClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				updateCurrentQuestion();
				ArrayList<QuestionOptionDto> optList = currentQuestion
						.getOptionContainerDto().getOptionsList();
				Cell cell = optionTable.getCellForEvent(event);
				if (event.getSource() == deleteButton) {
					optionTable.removeRow(cell.getRowIndex());
					optList.remove(cell.getRowIndex());
				} else {
					int increment = 0;
					if (event.getSource() == moveUp && cell.getRowIndex() > 0) {
						increment = -1;
					} else if (event.getSource() == moveDown
							&& cell.getRowIndex() < optList.size() - 1) {
						increment = 1;
					}
					if (increment != 0) {
						QuestionOptionDto targetOpt = optList.get(cell
								.getRowIndex() + increment);
						QuestionOptionDto movingOpt = optList.get(cell
								.getRowIndex());
						optList.set(cell.getRowIndex() + increment, movingOpt);
						optList.set(cell.getRowIndex(), targetOpt);
						targetOpt.setOrder(targetOpt.getOrder() - increment);
						movingOpt.setOrder(movingOpt.getOrder() + increment);
						// now update the UI
						((TextBox) (optionTable
								.getWidget(cell.getRowIndex(), 0)))
								.setText(targetOpt.getText());
						((TextBox) (optionTable.getWidget(cell.getRowIndex()
								+ increment, 0))).setText(movingOpt.getText());
					}
				}
			}
		};

		moveUp.setStylePrimaryName(REORDER_BUTTON_CSS);
		moveUp.addClickHandler(optionClickHandler);

		moveDown.setStylePrimaryName(REORDER_BUTTON_CSS);
		moveDown.addClickHandler(optionClickHandler);
		if (isEditable) {
			bp.add(moveUp);
			bp.add(moveDown);
			optionTable.setWidget(row, 2, deleteButton);
		}
		optionTable.setWidget(row, 1, bp);

		deleteButton.addClickHandler(optionClickHandler);
		if (opt != null) {
			optText.setText(opt.getText());
			if (opt.getOrder() == null) {
				opt.setOrder(row);
			}
		} else {
			if (currentQuestion.getOptionContainerDto() == null) {
				currentQuestion.setOptionContainerDto(new OptionContainerDto());
			}
			if (currentQuestion.getOptionContainerDto().getOptionsList() == null) {
				currentQuestion.getOptionContainerDto().setOptionsList(
						new ArrayList<QuestionOptionDto>());
			}
			QuestionOptionDto dto = new QuestionOptionDto();
			dto.setOrder(row);
			currentQuestion.getOptionContainerDto().getOptionsList().add(dto);

		}
	}

	/**
	 * displays a message in the loading label
	 * 
	 * @param container
	 * @param labelText
	 */
	private void showLoading(HasWidgets container, String labelText) {
		Label l = new Label(labelText);
		container.clear();
		container.add(l);
	}

	/**
	 * clears the container and adds the widget in content as the new child of
	 * container
	 * 
	 * @param container
	 * @param content
	 */
	private void showContent(HasWidgets container, Widget content) {
		container.clear();
		container.add(content);
	}

	/**
	 * loads the list of possible values for the dependent question list box. If
	 * this has already been loaded it may be returned from cache.
	 */
	private void loadDependencyList(final boolean isEditable) {
		dependencyPanel.setVisible(true);
		dependentQuestionSelector.setEnabled(isEditable);
		dependentAnswerSelector.setEnabled(isEditable);
		if (optionQuestions != null
				&& optionQuestions.get(currentQuestion.getSurveyId()) != null) {
			populateDependencySelection(currentQuestion,
					optionQuestions.get(currentQuestion.getSurveyId()));
		} else {
			showLoading(dependencyPanel, TEXT_CONSTANTS.loading());
			surveyService.listSurveyQuestionByType(
					currentQuestion.getSurveyId(), QuestionType.OPTION,
					needTranslations, new AsyncCallback<QuestionDto[]>() {

						@Override
						public void onFailure(Throwable caught) {
							showContent(dependencyPanel, new Label(
									TEXT_CONSTANTS.error()));
						}

						@Override
						public void onSuccess(QuestionDto[] result) {
							if (result != null) {
								List<QuestionDto> questionList = new ArrayList<QuestionDto>(
										Arrays.asList(result));
								optionQuestions.put(
										currentQuestion.getSurveyId(),
										questionList);
								getContextBundle(true)
										.put(BundleConstants.OPTION_QUESTION_LIST_KEY,
												optionQuestions);

								populateDependencySelection(currentQuestion,
										questionList);
							}
							showContent(dependencyPanel, dependencyGrid);
						}
					});
		}
	}

	/**
	 * populates the question dependency selector drop down and ensures the
	 * correct value is selected (if the currentQuestion's dependency is
	 * non-null).
	 * 
	 * @param currentQuestion
	 * @param questionList
	 */
	private void populateDependencySelection(QuestionDto currentQuestion,
			List<QuestionDto> questionList) {
		dependencyPanel.setVisible(true);
		if (questionList != null) {
			String selectedQId = null;
			for (int i = 0; i < questionList.size(); i++) {
				QuestionDto q = questionList.get(i);
				if (currentQuestion == null
						|| (currentQuestion.getKeyId() == null || !currentQuestion
								.getKeyId().equals(q.getKeyId()))) {
					dependentQuestionSelector.addItem(
							(q.getOrder() != null ? q.getOrder() + ": " : "")
									+ q.getText(), q.getKeyId().toString());
					if (currentQuestion != null
							&& currentQuestion.getQuestionDependency() != null
							&& currentQuestion.getQuestionDependency()
									.getQuestionId().equals(q.getKeyId())) {
						dependentQuestionSelector
								.setSelectedIndex(dependentQuestionSelector
										.getItemCount() - 1);
						selectedQId = q.getKeyId().toString();
					}
				}
			}
			if (selectedQId != null) {
				loadDependentQuestionAnswers(selectedQId);
			}
		}
	}

	/**
	 * loads the list of potential responses for the questionId passed in.
	 * 
	 * @param questionId
	 */
	private void loadDependentQuestionAnswers(String questionId) {
		final List<QuestionDto> questionList = optionQuestions
				.get(currentQuestion.getSurveyId());
		QuestionDto question = null;
		if (questionList != null) {
			for (QuestionDto q : questionList) {
				if (q.getKeyId().toString().equals(questionId)) {
					question = q;
				}
			}
		}
		if (question != null) {
			if (question.getOptionContainerDto() != null
					&& question.getOptionContainerDto().getOptionsList() != null) {
				populateDependencyAnswers(currentQuestion, question
						.getOptionContainerDto().getOptionsList());
			} else {
				showLoading(dependencyPanel, TEXT_CONSTANTS.loading());
				// if the option container is null, we probably have not
				// yet loaded the question details. so do it now
				surveyService.loadQuestionDetails(question.getKeyId(),
						new AsyncCallback<QuestionDto>() {
							@Override
							public void onSuccess(QuestionDto result) {

								if (questionList != null) {
									// update the option container of the cached
									// result so we have it for next time
									int idx = questionList.indexOf(result);
									if (idx >= 0) {
										questionList
												.get(idx)
												.setOptionContainerDto(
														result.getOptionContainerDto());
									}
								}
								if (result.getOptionContainerDto() != null) {
									populateDependencyAnswers(currentQuestion,
											result.getOptionContainerDto()
													.getOptionsList());
								}
								showContent(dependencyPanel, dependencyGrid);
							}

							@Override
							public void onFailure(Throwable caught) {
								showContent(dependencyPanel, dependencyGrid);
								Window.alert(TEXT_CONSTANTS.errorTracePrefix()
										+ " " + caught.getLocalizedMessage());
							}
						});
			}
		}
	}

	/**
	 * populates the dependent question answer selector and selects the correct
	 * value
	 * 
	 * @param currentQuestion
	 * @param options
	 */
	private void populateDependencyAnswers(QuestionDto currentQuestion,
			List<QuestionOptionDto> options) {
		// first, clear out the existing data
		dependentAnswerSelector.clear();
		// now add the "select" item
		dependentAnswerSelector.addItem(SELECT_TXT);
		if (options != null) {
			String[] answers = null;
			if (currentQuestion != null) {
				if (currentQuestion.getQuestionDependency() != null
						&& currentQuestion.getQuestionDependency()
								.getAnswerValue() != null) {
					answers = currentQuestion.getQuestionDependency()
							.getAnswerValue()
							.split(QuestionDto.ANS_DELIM_REGEX);
				}
			}
			for (int i = 0; i < options.size(); i++) {
				dependentAnswerSelector.addItem(options.get(i).getText(),
						options.get(i).getText());
				if (answers != null) {
					for (int j = 0; j < answers.length; j++) {
						if (options.get(i).getText().equals(answers[j])) {
							dependentAnswerSelector
									.setItemSelected(i + 1, true);
						}
					}
				}
			}
		}
	}

	/**
	 * returns the current context bundle
	 */
	@Override
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		if (bundle == null) {
			bundle = new HashMap<String, Object>();
		}
		if (doPopulation) {
			bundle.put(BundleConstants.QUESTION_KEY, currentQuestion);
			if (questionGroup != null) {
				bundle.put(BundleConstants.QUESTION_GROUP_KEY, questionGroup);
			}
		}
		return bundle;
	}

	@Override
	public void flushContext() {
		bundle.remove(BundleConstants.QUESTION_KEY);
		bundle.remove(BundleConstants.INSERT_ABOVE_QUESTION);
	}

	/**
	 * validates and saves the question. If the question does not validate, then
	 * a failure message will be sent to the CompletionListener containing the
	 * list of errors.
	 */
	@Override
	public void persistContext(final String buttonText,
			final CompletionListener listener) {
		if (currentQuestion.getImmutable() == null
				|| (!currentQuestion.getImmutable() || currentUser
						.hasPermission(PermissionConstants.EDIT_IMMUTABLITY))) {
			List<String> validationErrors = updateCurrentQuestion();
			if (validationErrors == null || validationErrors.size() == 0) {
				if (currentQuestion.getKeyId() == null
						&& insertAboveQuestion == null) {
					// reload list and update order
					logger.log(
							Level.WARNING,
							"QuestionEditWidget: Question is new so looking up question group size. Prior to lookup, order is "
									+ currentQuestion.getOrder()
									+ " and group has "
									+ (questionGroup.getQuestionMap() != null ? questionGroup
											.getQuestionMap().size() : "0")
									+ "items");
					surveyService.listQuestionsByQuestionGroup(currentQuestion
							.getQuestionGroupId().toString(), false, false,
							new AsyncCallback<ArrayList<QuestionDto>>() {

								@Override
								public void onFailure(Throwable caught) {
									MessageDialog dia = new MessageDialog(
											TEXT_CONSTANTS.error(),
											TEXT_CONSTANTS.errorTracePrefix()
													+ " "
													+ caught.getLocalizedMessage());
									dia.showCentered();
								}

								@Override
								public void onSuccess(
										ArrayList<QuestionDto> result) {
									if (result != null && result.size() > 0) {

										TreeMap<Integer, QuestionDto> questionTree = new TreeMap<Integer, QuestionDto>();
										int maxOrder = 0;
										for (int i = 0; i < result.size(); i++) {
											questionTree.put(result.get(i)
													.getOrder(), result.get(i));
											if (result.get(i).getOrder() != null
													&& maxOrder < result.get(i)
															.getOrder()) {
												maxOrder = i;
											}
										}
										if (maxOrder < result.size()) {
											maxOrder = result.size();
										}
										currentQuestion.setOrder(maxOrder + 1);
										logger.log(Level.WARNING,
												"QuestionEditWidget: Setting order to "
														+ (maxOrder + 1));
										if (questionGroup != null) {
											questionGroup
													.setQuestionMap(questionTree);
										}
									}
									performSave(listener);
								}
							});
				} else {
					logger.log(
							Level.WARNING,
							"QuestionEditWidget: Question has id: "
									+ currentQuestion.getKeyId()
									+ " and order "
									+ currentQuestion.getOrder());
					if (TEXT_CONSTANTS.saveGotoNext().equals(buttonText)) {
						getContextBundle(false).put(
								BundleConstants.LAST_QUESTION_ORDER,
								currentQuestion.getOrder());
						getContextBundle(false).put(BundleConstants.DIRECTION,
								FWD);
					} else if (TEXT_CONSTANTS.saveGotoPrev().equals(buttonText)) {
						getContextBundle(false).put(
								BundleConstants.LAST_QUESTION_ORDER,
								currentQuestion.getOrder());
					}
					performSave(listener);
				}
			} else {
				StringBuilder builder = new StringBuilder("<br><ul>");
				for (String err : validationErrors) {
					builder.append("<li>").append(err).append("</li>");
				}
				builder.append("</ul>");
				MessageDialog errorDialog = new MessageDialog(
						TEXT_CONSTANTS.inputError(),
						TEXT_CONSTANTS.pleaseCorrect() + builder.toString());
				errorDialog.showCentered();
				listener.operationComplete(false, getContextBundle(true));
			}
		} else {
			if (listener != null) {
				listener.operationComplete(true, getContextBundle(true));
			}
		}
	}

	private void performSave(final CompletionListener listener) {

		surveyService.saveQuestion(currentQuestion,
				currentQuestion.getQuestionGroupId(),
				(insertAboveQuestion != null),
				new AsyncCallback<QuestionDto>() {

					@Override
					public void onSuccess(QuestionDto result) {
						currentQuestion = result;
						if (metricSelector.getSelectedIndex() > 0) {
							SurveyMetricMappingDto mapping = new SurveyMetricMappingDto();
							mapping.setSurveyQuestionId(currentQuestion
									.getKeyId());
							mapping.setMetricId(new Long(ViewUtil
									.getListBoxSelection(metricSelector, true)));
							mapping.setQuestionGroupId(currentQuestion
									.getQuestionGroupId());
							mapping.setSurveyId(currentQuestion.getSurveyId());
							metricMappingService
									.saveMapping(
											mapping,
											new AsyncCallback<SurveyMetricMappingDto>() {

												@Override
												public void onFailure(
														Throwable caught) {
													MessageDialog dia = new MessageDialog(
															TEXT_CONSTANTS
																	.error(),
															TEXT_CONSTANTS
																	.errorTracePrefix()
																	+ " "
																	+ caught.getLocalizedMessage());
													dia.showCentered();

												}

												@Override
												public void onSuccess(
														SurveyMetricMappingDto result) {
													// no-op
												}
											});
						} else {
							metricMappingService.deleteMetricMapping(
									currentQuestion.getKeyId(),
									new AsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											// no-op
										}

										@Override
										public void onSuccess(Void result) {
											// no-op
										}
									});
						}
						if (insertAboveQuestion != null) {
							// set to null so we don't keep bumping it up on
							// subsequent saves if the user doesn't navigate off
							// the page
							insertAboveQuestion = null;
							TreeMap<Integer, QuestionDto> movedQuestions = new TreeMap<Integer, QuestionDto>();
							TreeMap<Integer, QuestionDto> origQuestionMap = questionGroup
									.getQuestionMap();
							for (QuestionDto q : origQuestionMap.values()) {
								if (q.getOrder() >= currentQuestion.getOrder()) {
									q.setOrder(q.getOrder() + 1);
									movedQuestions.put(q.getOrder(), q);
								}
							}
							// now update the orders.
							for (Entry<Integer, QuestionDto> entry : movedQuestions
									.entrySet()) {
								origQuestionMap.put(entry.getKey(),
										entry.getValue());
							}
						}
						questionGroup.addQuestion(currentQuestion,
								currentQuestion.getOrder());
						if (currentQuestion.getType() == QuestionDto.QuestionType.OPTION
								&& optionQuestions != null) {
							if (optionQuestions.containsKey(currentQuestion
									.getSurveyId())) {
								boolean found = false;
								for (QuestionDto q : optionQuestions
										.get(currentQuestion.getSurveyId())) {
									if (q.getKeyId().equals(
											currentQuestion.getKeyId())) {
										// update the text in case it was
										// changed
										q.setText(currentQuestion.getText());
										q.setTranslationMap(currentQuestion
												.getTranslationMap());
										// also update the options in case they
										// added/removed any
										q.setOptionContainerDto(currentQuestion
												.getOptionContainerDto());
										found = true;
									}
								}
								if (!found) {
									optionQuestions.get(
											currentQuestion.getSurveyId()).add(
											currentQuestion);
								}
							} else {
								List<QuestionDto> qList = new ArrayList<QuestionDto>();
								qList.add(currentQuestion);
								optionQuestions.put(
										currentQuestion.getSurveyId(), qList);
							}
						}
						if (listener != null) {
							listener.operationComplete(true,
									getContextBundle(true));
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						if (listener != null) {
							listener.operationComplete(false,
									getContextBundle(true));
						}
					}
				});
	}

	/**
	 * updates the cached questionDto using the values currently present in the
	 * UI
	 * 
	 * @return
	 */
	private List<String> updateCurrentQuestion() {
		List<String> validationMessages = new ArrayList<String>();
		if (currentQuestion.getImmutable() == null
				|| (!currentQuestion.getImmutable() || currentUser
						.hasPermission(PermissionConstants.EDIT_IMMUTABLITY))) {
			if (ViewUtil.isTextPopulated(questionTextArea)) {
				if (questionTextArea.getText().trim().length() > MAX_LEN) {
					validationMessages.add(TEXT_CONSTANTS.question() + ": "
							+ TEXT_CONSTANTS.textMustBeLessThan500Chars());
				} else {
					currentQuestion.setText(questionTextArea.getText().trim());
				}
			} else {
				validationMessages.add(TEXT_CONSTANTS.questionTextMandatory());
			}
			currentQuestion.setTip(tooltipArea.getText());
			currentQuestion.setCollapseable(collapseableBox.getValue());
			currentQuestion.setImmutable(immutableBox.getValue());
			if (tooltipArea.getText() != null) {
				if (tooltipArea.getText().length() > MAX_LEN) {
					validationMessages.add(TEXT_CONSTANTS.tooltip() + ": "
							+ TEXT_CONSTANTS.textMustBeLessThan500Chars());
				}
			}
			if (mandatoryBox.getValue()) {
				currentQuestion.setMandatoryFlag(true);
			} else {
				currentQuestion.setMandatoryFlag(false);
			}
			currentQuestion.setType(QuestionDto.QuestionType
					.valueOf(questionTypeSelector.getValue(questionTypeSelector
							.getSelectedIndex())));
			if (QuestionType.NUMBER == currentQuestion.getType()) {
				currentQuestion.setIsName(false);
				currentQuestion.setAllowDecimal(allowDecimal.getValue());
				currentQuestion.setAllowSign(allowSign.getValue());
				String val = ViewUtil.getNonBlankValue(minVal);
				if (val != null) {
					currentQuestion.setMinVal(new Double(val));
				} else {
					currentQuestion.setMinVal(null);
				}
				val = ViewUtil.getNonBlankValue(maxVal);
				if (val != null) {
					currentQuestion.setMaxVal(new Double(val));
				} else {
					currentQuestion.setMaxVal(null);
				}
			} else if (QuestionType.FREE_TEXT == currentQuestion.getType()) {
				currentQuestion.setIsName(isName.getValue());
				currentQuestion.setAllowDecimal(false);
				currentQuestion.setAllowSign(false);
				currentQuestion.setMinVal(null);
				currentQuestion.setMaxVal(null);
			} else {
				currentQuestion.setIsName(false);
				currentQuestion.setAllowDecimal(false);
				currentQuestion.setAllowSign(false);
				currentQuestion.setMinVal(null);
				currentQuestion.setMaxVal(null);
			}
			if (dependentBox.getValue()) {
				QuestionDependencyDto depDto = currentQuestion
						.getQuestionDependency();
				if (depDto == null) {
					depDto = new QuestionDependencyDto();
					currentQuestion.setQuestionDependency(depDto);
				}
				if (dependentQuestionSelector.getSelectedIndex() == 0) {
					validationMessages.add(TEXT_CONSTANTS.dependentMandatory());
				} else {
					depDto.setQuestionId(Long
							.parseLong(dependentQuestionSelector
									.getValue(dependentQuestionSelector
											.getSelectedIndex())));
					if (dependentAnswerSelector.getSelectedIndex() <= 0) {
						validationMessages.add(TEXT_CONSTANTS
								.dependentResponseMandatory());
					} else {
						// start at 1 since 0 is the "SELECT" text
						StringBuilder builder = new StringBuilder();
						for (int i = 1; i < dependentAnswerSelector
								.getItemCount(); i++) {
							if (dependentAnswerSelector.isItemSelected(i)) {
								if (builder.length() > 0) {
									builder.append(QuestionDto.ANS_DELIM);
								}
								builder.append(dependentAnswerSelector
										.getValue(i));
							}
						}
						depDto.setAnswerValue(builder.toString().trim());
					}
				}
			} else {
				currentQuestion.setQuestionDependency(null);
			}
			if (QuestionDto.QuestionType.OPTION == currentQuestion.getType()) {
				currentQuestion.setAllowMultipleFlag(allowMultipleBox
						.getValue());
				currentQuestion.setAllowOtherFlag(allowOtherBox.getValue());
				OptionContainerDto container = currentQuestion
						.getOptionContainerDto();
				if (container == null) {
					container = new OptionContainerDto();
					currentQuestion.setOptionContainerDto(container);
				}
				container.setAllowMultipleFlag(allowMultipleBox.getValue());
				container.setAllowOtherFlag(allowOtherBox.getValue());
				if (container.getOptionsList() == null
						|| container.getOptionsList().size() == 0) {
					validationMessages.add(TEXT_CONSTANTS.optionMandatory());
				} else {
					for (int i = 0; i < container.getOptionsList().size(); i++) {
						TextBox box = (TextBox) optionTable.getWidget(i, 0);
						if (ViewUtil.isTextPopulated(box)) {
							if (box.getText().trim().length() > MAX_LEN) {
								validationMessages.add(TEXT_CONSTANTS.option()
										+ " "
										+ i
										+ ": "
										+ TEXT_CONSTANTS
												.textMustBeLessThan500Chars());
							} else {
								container.getOptionsList().get(i)
										.setText(box.getText().trim());
								container.getOptionsList().get(i)
										.setOrder(i + 1);
							}
						} else {
							validationMessages.add(TEXT_CONSTANTS
									.optionNotBlank() + " " + i);
						}
					}
				}
			}
		}
		return validationMessages;
	}

	/**
	 * populates the metric selector AND sets the list in the context bundle so
	 * we don't need to hit the server all the time
	 */
	private void loadMetrics(String cursor) {
		metricService.listMetrics(null, null, null, null, cursor,
				new AsyncCallback<ResponseDto<ArrayList<MetricDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog dia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						dia.showCentered();
					}

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<MetricDto>> result) {
						if (result != null && result.getPayload() != null) {
							populateMetricControl(result.getPayload());
							metricList.addAll(result.getPayload());
							if (result.getCursorString() != null
									&& result.getPayload().size() == ResponseDto.DEFAULT_PAGE_SIZE) {
								loadMetrics(result.getCursorString());
							}
						} else {
							loadMetricMapping();
						}
					}
				});
	}

	/**
	 * installs metrics into the selection dialog
	 * 
	 * @param metrics
	 */
	private void populateMetricControl(List<MetricDto> metrics) {
		if (metrics != null) {
			for (MetricDto dto : metrics) {
				metricSelector
						.addItem(dto.getName(), dto.getKeyId().toString());
			}
		}
	}

	/**
	 * loads the mapping (if any) for this question if it's saved TODO: see if
	 * we need to handle multiple mappings per question
	 */
	private void loadMetricMapping() {
		if (currentQuestion != null && currentQuestion.getKeyId() != null) {
			metricMappingService.listMappingsByQuestion(
					currentQuestion.getKeyId(),
					new AsyncCallback<List<SurveyMetricMappingDto>>() {

						@Override
						public void onFailure(Throwable caught) {
							MessageDialog dia = new MessageDialog(
									TEXT_CONSTANTS.error(), TEXT_CONSTANTS
											.errorTracePrefix()
											+ " "
											+ caught.getLocalizedMessage());
							dia.showCentered();
						}

						@Override
						public void onSuccess(
								List<SurveyMetricMappingDto> result) {
							if (result != null) {
								for (SurveyMetricMappingDto mapping : result) {
									for (int i = 0; i < metricSelector
											.getItemCount(); i++) {
										if (mapping
												.getMetricId()
												.toString()
												.equals(metricSelector
														.getValue(i))) {
											metricSelector.setSelectedIndex(i);
											return;
										}
									}
								}
							}
						}
					});
		}
	}

	/**
	 * installs the contextBundle and triggers invocation of the populateFields
	 * method (if there is a questionDto in the bundle).
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		currentQuestion = (QuestionDto) bundle
				.get(BundleConstants.QUESTION_KEY);

		if (bundle.get(BundleConstants.METRIC_LIST) != null) {
			metricList = (List<MetricDto>) bundle
					.get(BundleConstants.METRIC_LIST);
			populateMetricControl(metricList);
			loadMetricMapping();
		} else {
			metricList = new ArrayList<MetricDto>();
			loadMetrics(null);
			if (this.bundle == null) {
				this.bundle = new HashMap<String, Object>();
			}
			this.bundle.put(BundleConstants.METRIC_LIST, metricList);
		}
		insertAboveQuestion = (QuestionDto) bundle
				.get(BundleConstants.INSERT_ABOVE_QUESTION);
		bundle.remove(BundleConstants.INSERT_ABOVE_QUESTION);
		currentSurvey = (SurveyDto) bundle.get(BundleConstants.SURVEY_KEY);
		questionGroup = (QuestionGroupDto) bundle
				.get(BundleConstants.QUESTION_GROUP_KEY);
		if (currentQuestion == null
				&& bundle.get(BundleConstants.LAST_QUESTION_ORDER) != null) {

			Integer order = (Integer) bundle
					.remove(BundleConstants.LAST_QUESTION_ORDER);
			String dir = (String) bundle.remove(BundleConstants.DIRECTION);
			// find the next question the hard way since we may not have
			// continuous ordering
			if (questionGroup.getQuestionMap() != null) {
				QuestionDto candidate = null;
				for (QuestionDto q : questionGroup.getQuestionMap().values()) {
					if (FWD.equals(dir)) {
						if (q.getOrder() > order) {
							if (candidate == null) {
								candidate = q;
							} else if (candidate.getOrder() > q.getOrder()) {
								candidate = q;
							}
						}
					} else {
						if (q.getOrder() < order) {
							if (candidate == null) {
								candidate = q;
							} else if (candidate.getOrder() < q.getOrder()) {
								candidate = q;
							}
						}
					}
				}
				if (candidate != null) {
					currentQuestion = candidate;
					bundle.put(BundleConstants.QUESTION_KEY, candidate);
				}
			}
		}
		optionQuestions = (Map<Long, List<QuestionDto>>) bundle
				.get(BundleConstants.OPTION_QUESTION_LIST_KEY);
		if (optionQuestions == null) {
			optionQuestions = new HashMap<Long, List<QuestionDto>>();
		}
		if (currentQuestion == null) {

			currentQuestion = new QuestionDto();

			currentQuestion.setSurveyId(questionGroup.getSurveyId());
			currentQuestion.setPath(questionGroup.getPath() + "/"
					+ questionGroup.getCode());
			currentQuestion.setQuestionGroupId(questionGroup.getKeyId());
			if (insertAboveQuestion != null) {
				currentQuestion.setOrder(insertAboveQuestion.getOrder());
			} else {
				if (questionGroup.getQuestionMap() != null) {
					currentQuestion.setOrder(getMaxOrder(questionGroup) + 1);
				} else {
					currentQuestion.setOrder(1);
				}
			}
		}
		populateFields();
	}

	/**
	 * finds the highest Order value amongst all the questions in a group
	 * 
	 * @param group
	 * @return
	 */
	private Integer getMaxOrder(QuestionGroupDto group) {
		Integer max = 0;
		if (group != null && group.getQuestionMap() != null) {

			for (QuestionDto q : group.getQuestionMap().values()) {
				if (q.getOrder() > max) {
					max = q.getOrder();
				}
			}
		}
		return max;
	}

	/**
	 * react to changes in the drop-downs (usually by hiding/showing panels)
	 */
	@Override
	public void onChange(ChangeEvent event) {
		if (event.getSource() == questionTypeSelector) {
			if (QuestionDto.QuestionType.OPTION.toString().equals(
					questionTypeSelector.getValue(questionTypeSelector
							.getSelectedIndex()))) {
				optionPanel.setVisible(true);
				validationPanel.setVisible(false);
				loadOptions(true);
			} else {
				optionPanel.setVisible(false);
				if (QuestionDto.QuestionType.NUMBER.toString().equals(
						questionTypeSelector.getValue(questionTypeSelector
								.getSelectedIndex()))) {
					validationPanel.clear();
					validationPanel.add(numericValidationPanel);
					validationPanel.setVisible(true);
				} else if (QuestionDto.QuestionType.FREE_TEXT.toString()
						.equals(questionTypeSelector
								.getValue(questionTypeSelector
										.getSelectedIndex()))) {
					validationPanel.clear();
					validationPanel.add(textValidationPanel);
					validationPanel.setVisible(true);
				}
			}
		} else if (event.getSource() == dependentQuestionSelector) {
			int index = dependentQuestionSelector.getSelectedIndex();
			if (index > 0) {
				loadDependentQuestionAnswers(dependentQuestionSelector
						.getValue(index));
			} else {
				populateDependencyAnswers(currentQuestion, null);
			}
		} else if (event.getSource() == dependentAnswerSelector) {
			viewTreeButton.setVisible(true);
		}
	}

	/**
	 * react to click events. Usually triggers loading of dependent data. In the
	 * case of the translations button: the question will first be validated and
	 * saved before the translations dialog will be opened
	 */
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == dependentBox) {
			if (dependentBox.getValue()) {
				dependencyPanel.setVisible(true);
				loadDependencyList(true);
			} else {
				dependencyPanel.setVisible(false);
			}
		} else if (event.getSource() == addOptionButton) {
			installOptionRow(null, true);
		} else if (event.getSource() == editTranslationButton) {
			operation = EDIT_TRANS_OP;
			persistContext(null, this);
		} else if (event.getSource() == editHelpButton) {
			operation = EDIT_HELP_OP;
			persistContext(null, this);
		} else if (event.getSource() == viewTreeButton) {
			// don't mess with currentDto since the changes may not be saved yet
			QuestionDto temp = new QuestionDto();
			QuestionDependencyDto depDto = new QuestionDependencyDto();
			String val = ViewUtil.getListBoxSelection(
					dependentQuestionSelector, true);
			if (val != null) {
				depDto.setQuestionId(new Long(val));
				String valString = ViewUtil.getListBoxSelection(
						dependentAnswerSelector, true);
				if (valString != null) {
					valString = valString.trim();
				}
				depDto.setAnswerValue(valString);
				if (depDto.getAnswerValue() != null) {
					temp.setQuestionDependency(depDto);
					WidgetDialog dia = new WidgetDialog(
							TEXT_CONSTANTS.viewDependencyTree(),
							new DependencyTreeViewerWidget(temp,
									optionQuestions));
					dia.showCentered();
				}
			}
		}
	}

	/**
	 * receives notification when the translations dialog has been closed so the
	 * translations can be added to the cached question dto.
	 */
	@Override
	public void translationsUpdated(List<TranslationDto> translationList) {
		if (translationList != null) {
			for (TranslationDto trans : translationList) {
				if ("QUESTION_TYPE".equals(trans.getParentType())) {
					currentQuestion.addTranslation(trans);
				} else if ("QUESTION_OPTION".equals(trans.getParentType())) {
					// need to find the right option
					if (currentQuestion.getOptionContainerDto() != null) {
						for (QuestionOptionDto opt : currentQuestion
								.getOptionContainerDto().getOptionsList()) {
							if (opt.getKeyId().equals(trans.getParentId())) {
								opt.addTranslation(trans);
								break;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * allows this widget to act as a CompletionListener so we can display the
	 * translation dialog after the async save if the current operation was set
	 * to EDIT_TRANS_OP
	 */
	@Override
	public void operationComplete(boolean wasSuccessful,
			Map<String, Object> payload) {
		if (wasSuccessful) {
			if (EDIT_TRANS_OP.equals(operation)) {
				SurveyQuestionTranslationDialog dia = new SurveyQuestionTranslationDialog(
						(QuestionDto) currentQuestion,
						currentSurvey.getDefaultLanguageCode() != null ? currentSurvey
								.getDefaultLanguageCode() : "en", this);
				dia.show();
			} else if (EDIT_HELP_OP.equals(operation)) {
				operation = null;
				QuestionHelpDialog dia = new QuestionHelpDialog(
						(QuestionDto) currentQuestion, this);
				dia.show();
			} else if (operation == null) {
			}
		}
		operation = null;
	}
}