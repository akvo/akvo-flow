package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDependencyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.Cell;

/**
 * Widget for creating and editing a Survey Question.
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionEditWidget extends Composite implements ContextAware,
		ChangeHandler, ClickHandler, TranslationChangeListener,
		CompletionListener {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static final int MAX_LEN = 500;
	private static final String REORDER_BUTTON_CSS = "reorder-button";
	private static final String DEFAULT_BOX_WIDTH = "300px";
	private static final String SELECT_TXT = TEXT_CONSTANTS.select() + "...";
	private static final String EDIT_TRANS_OP = "Edit Translation";
	private static final String EDIT_HELP_OP = "Edit Help";
	private VerticalPanel panel;
	private CaptionPanel basePanel;
	private TextArea questionTextArea;
	private ListBox questionTypeSelector;
	private TextArea tooltipArea;
	private TextBox validationRuleBox;
	private CheckBox mandatoryBox;
	private CheckBox dependentBox;
	private ListBox dependentQuestionSelector;
	private ListBox dependentAnswerSelector;
	private CaptionPanel dependencyPanel;
	private Grid dependencyGrid;

	private CheckBox allowOtherBox;
	private CheckBox allowMultipleBox;
	private Button addOptionButton;
	private CaptionPanel optionPanel;
	private VerticalPanel optionContent;
	private FlexTable optionTable;
	private SurveyServiceAsync surveyService;
	private Map<String, Object> bundle;
	private QuestionDto currentQuestion;
	private Map<Long, List<QuestionDto>> optionQuestions;
	private Button editTranslationButton;
	private Button editHelpButton;

	private QuestionGroupDto questionGroup;
	private String operation;

	public QuestionEditWidget() {
		surveyService = GWT.create(SurveyService.class);
		optionQuestions = new HashMap<Long, List<QuestionDto>>();
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
		validationRuleBox = new TextBox();
		mandatoryBox = new CheckBox();
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
		questionTypeSelector.addChangeHandler(this);
		basePanel = new CaptionPanel(TEXT_CONSTANTS.questionBasics());

		Grid grid = new Grid(7, 2);
		basePanel.add(grid);

		ViewUtil.installGridRow(TEXT_CONSTANTS.questionText(),
				questionTextArea, grid, 0);
		ViewUtil.installGridRow(TEXT_CONSTANTS.questionType(),
				questionTypeSelector, grid, 1);
		ViewUtil.installGridRow(TEXT_CONSTANTS.tooltip(), tooltipArea, grid, 2);
		ViewUtil.installGridRow(TEXT_CONSTANTS.validationRule(),
				validationRuleBox, grid, 3);
		ViewUtil.installGridRow(TEXT_CONSTANTS.mandatory(), mandatoryBox, grid,
				4);
		ViewUtil.installGridRow(TEXT_CONSTANTS.dependent(), dependentBox, grid,
				5);

		dependencyPanel = new CaptionPanel(TEXT_CONSTANTS.dependencyDetails());
		dependentQuestionSelector = new ListBox();
		dependentQuestionSelector.setWidth(DEFAULT_BOX_WIDTH);
		dependentQuestionSelector.addChangeHandler(this);
		dependentQuestionSelector.addItem(SELECT_TXT);

		dependentAnswerSelector = new ListBox();
		dependentAnswerSelector.addItem(SELECT_TXT);
		dependentAnswerSelector.setWidth(DEFAULT_BOX_WIDTH);

		dependencyGrid = new Grid(2, 2);
		dependencyPanel.add(dependencyGrid);
		ViewUtil.installGridRow(null, dependencyPanel, grid, 6, 1, null);
		dependencyPanel.setVisible(false);
		ViewUtil.installGridRow(TEXT_CONSTANTS.question(),
				dependentQuestionSelector, dependencyGrid, 0);
		ViewUtil.installGridRow(TEXT_CONSTANTS.response(),
				dependentAnswerSelector, dependencyGrid, 1);

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
		questionTextArea.setText(currentQuestion.getText());
		if (currentQuestion.getTip() != null
				&& currentQuestion.getTip().trim().length() > 0
				&& !"null".equals(currentQuestion.getTip())) {
			tooltipArea.setText(currentQuestion.getTip());
		}
		if (currentQuestion.getType() != null) {
			for (int i = 0; i < questionTypeSelector.getItemCount(); i++) {
				if (currentQuestion.getType().toString()
						.equals(questionTypeSelector.getValue(i))) {
					questionTypeSelector.setSelectedIndex(i);
					break;
				}
			}
		}
		if (currentQuestion.getMandatoryFlag() != null) {
			mandatoryBox.setValue(currentQuestion.getMandatoryFlag());
		}
		if (currentQuestion.getQuestionDependency() != null
				&& currentQuestion.getQuestionDependency().getQuestionId() != null) {
			dependentBox.setValue(true);
			loadDependencyList();
		}
		if (QuestionDto.QuestionType.OPTION == currentQuestion.getType()) {
			loadOptions();
		}
	}

	/**
	 * fetches the QuestionOptions from the server if they haven't already been
	 * retrieved
	 */
	private void loadOptions() {
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
							populateOptions(currentQuestion
									.getOptionContainerDto());
						}
					});
		} else {
			populateOptions(currentQuestion.getOptionContainerDto());
		}
	}

	/**
	 * populates the UI with data from all the QuestionOptionDto objects in the
	 * question.
	 * 
	 * @param optionContainer
	 */
	private void populateOptions(OptionContainerDto optionContainer) {
		if (optionContainer != null) {
			optionPanel.setVisible(true);
			// wipe out any old values
			optionTable.clear(true);
			allowMultipleBox.setValue(optionContainer.getAllowMultipleFlag());
			allowOtherBox.setValue(optionContainer.getAllowOtherFlag());
			if (optionContainer != null
					&& optionContainer.getOptionsList() != null) {
				for (QuestionOptionDto opt : optionContainer.getOptionsList()) {
					installOptionRow(opt);
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
	private void installOptionRow(QuestionOptionDto opt) {
		int row = optionTable.getRowCount();
		optionTable.insertRow(row);
		TextBox optText = new TextBox();
		optText.setMaxLength(MAX_LEN);
		optionTable.setWidget(row, 0, optText);
		HorizontalPanel bp = new HorizontalPanel();
		final Image moveUp = new Image("/images/greenuparrow.png");
		final Image moveDown = new Image("/images/greendownarrow.png");
		final Button deleteButton = new Button(TEXT_CONSTANTS.remove());

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
		bp.add(moveUp);
		bp.add(moveDown);
		optionTable.setWidget(row, 1, bp);

		deleteButton.addClickHandler(optionClickHandler);
		optionTable.setWidget(row, 2, deleteButton);
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
	 * this has already been loaded it may be returend from cache.
	 */
	private void loadDependencyList() {
		dependencyPanel.setVisible(true);
		if (optionQuestions != null
				&& optionQuestions.get(currentQuestion.getSurveyId()) != null) {
			populateDependencySelection(currentQuestion,
					optionQuestions.get(currentQuestion.getSurveyId()));
		} else {
			showLoading(dependencyPanel, TEXT_CONSTANTS.loading());
			surveyService.listSurveyQuestionByType(
					currentQuestion.getSurveyId(), QuestionType.OPTION,
					new AsyncCallback<QuestionDto[]>() {

						@Override
						public void onFailure(Throwable caught) {
							showContent(dependencyPanel, new Label(
									TEXT_CONSTANTS.error()));
						}

						@Override
						public void onSuccess(QuestionDto[] result) {
							if (result != null) {
								List<QuestionDto> questionList = Arrays
										.asList(result);
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
					dependentQuestionSelector.addItem(q.getText(), q.getKeyId()
							.toString());
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
			for (int i = 0; i < options.size(); i++) {
				dependentAnswerSelector.addItem(options.get(i).getText(),
						options.get(i).getText());
				if (currentQuestion != null
						&& currentQuestion.getQuestionDependency() != null
						&& options
								.get(i)
								.getText()
								.equals(currentQuestion.getQuestionDependency()
										.getAnswerValue())) {
					dependentAnswerSelector.setSelectedIndex(i + 1);
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
		}
		return bundle;
	}

	@Override
	public void flushContext() {
		bundle.remove(BundleConstants.QUESTION_KEY);
	}

	/**
	 * validates and saves the question. If the question does not validate, then
	 * a failure message will be sent to the CompletionListener containing the
	 * list of errors.
	 */
	@Override
	public void persistContext(final CompletionListener listener) {
		List<String> validationErrors = updateCurrentQuestion();
		if (validationErrors == null || validationErrors.size() == 0) {
			surveyService.saveQuestion(currentQuestion,
					currentQuestion.getQuestionGroupId(),
					new AsyncCallback<QuestionDto>() {

						@Override
						public void onSuccess(QuestionDto result) {
							currentQuestion = result;
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
											found = true;
										}
									}
									if (!found) {
										optionQuestions.get(
												currentQuestion.getSurveyId())
												.add(currentQuestion);
									}
								}else {
									List<QuestionDto> qList = new ArrayList<QuestionDto>();
									qList.add(currentQuestion);
									optionQuestions.put(currentQuestion.getSurveyId(), qList);
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
		} else {
			StringBuilder builder = new StringBuilder("<br><ul>");
			for (String err : validationErrors) {
				builder.append("<li>").append(err).append("</li>");
			}
			builder.append("</ul>");
			MessageDialog errorDialog = new MessageDialog(
					TEXT_CONSTANTS.inputError(), TEXT_CONSTANTS.pleaseCorrect()
							+ builder.toString());
			errorDialog.showCentered();
			listener.operationComplete(false, getContextBundle(true));
		}
	}

	/**
	 * updates the cached questionDto using the values currently present in the
	 * UI
	 * 
	 * @return
	 */
	private List<String> updateCurrentQuestion() {
		List<String> validationMessages = new ArrayList<String>();
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
		currentQuestion.setValidationRule(validationRuleBox.getText());
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
				depDto.setQuestionId(Long.parseLong(dependentQuestionSelector
						.getValue(dependentQuestionSelector.getSelectedIndex())));
				if (dependentAnswerSelector.getSelectedIndex() == 0) {
					validationMessages.add(TEXT_CONSTANTS
							.dependentResponseMandatory());
				} else {
					depDto.setAnswerValue(dependentAnswerSelector
							.getValue(dependentAnswerSelector
									.getSelectedIndex()));
				}
			}
		} else {
			currentQuestion.setQuestionDependency(null);
		}
		if (QuestionDto.QuestionType.OPTION == currentQuestion.getType()) {
			currentQuestion.setAllowMultipleFlag(allowMultipleBox.getValue());
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
							container.getOptionsList().get(i).setOrder(i + 1);
						}
					} else {
						validationMessages.add(TEXT_CONSTANTS.optionNotBlank()
								+ " " + i);
					}
				}
			}
		}
		return validationMessages;
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
		questionGroup = (QuestionGroupDto) bundle
				.get(BundleConstants.QUESTION_GROUP_KEY);
		optionQuestions = (Map<Long, List<QuestionDto>>) bundle
				.get(BundleConstants.OPTION_QUESTION_LIST_KEY);
		if (optionQuestions == null) {
			optionQuestions = new HashMap<Long, List<QuestionDto>>();
		}
		if (currentQuestion != null) {
			populateFields();
		} else {
			currentQuestion = new QuestionDto();
			QuestionGroupDto currentGroup = (QuestionGroupDto) bundle
					.get(BundleConstants.QUESTION_GROUP_KEY);
			currentQuestion.setSurveyId(currentGroup.getSurveyId());
			currentQuestion.setPath(currentGroup.getPath() + "/"
					+ currentGroup.getCode());
			currentQuestion.setQuestionGroupId(currentGroup.getKeyId());
			if (currentGroup.getQuestionMap() != null) {
				currentQuestion.setOrder(getMaxOrder(currentGroup) + 1);
			} else {
				currentQuestion.setOrder(1);
			}
		}
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
				loadDependencyList();
			} else {
				dependencyPanel.setVisible(false);
			}
		} else if (event.getSource() == addOptionButton) {
			installOptionRow(null);
		} else if (event.getSource() == editTranslationButton) {
			operation = EDIT_TRANS_OP;
			persistContext(this);
		} else if (event.getSource() == editHelpButton) {
			operation = EDIT_HELP_OP;
			persistContext(this);
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
						(QuestionDto) currentQuestion, this);
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