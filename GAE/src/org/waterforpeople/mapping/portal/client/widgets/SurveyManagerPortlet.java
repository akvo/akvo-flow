package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDependencyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.view.SurveyTree;
import org.waterforpeople.mapping.app.gwt.client.survey.view.SurveyTreeListener;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.SurveyQuestionTranslationDialog;
import org.waterforpeople.mapping.portal.client.widgets.component.TranslationChangeListener;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SurveyManagerPortlet extends UserAwarePortlet implements
		ClickHandler, SurveyTreeListener, TranslationChangeListener {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String NAME = TEXT_CONSTANTS
			.surveyManagerPortletTitle();
	public static final String DESCRIPTION = TEXT_CONSTANTS
			.surveyManagerPortletDescription();
	private static final int MAX_Q_LENGTH = 50;
	private static String title = "";
	private static Boolean scrollable = true;
	private static Boolean configurable = false;
	private static final int HEIGHT = 800;
	private static final int WIDTH = 1080;

	private HorizontalPanel buttonHPanel = new HorizontalPanel();
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private Button addSurveyGroupButton;
	private Button addSurveyButton;
	private Button addQuestionGroupButton;
	private Button addQuestionButton;
	private Button deleteSurveyGroupButton;
	private Button deleteSurveyButton;
	private Button deleteQuestionGroupButton;
	private Button deleteQuestionButton;

	private HorizontalPanel treeContainer = new HorizontalPanel();
	private Tree surveyRoot;
	private SurveyTree surveyTree;

	private VerticalPanel contentPane = null;
	private VerticalPanel detailContainer = new VerticalPanel();
	private FlexTable surveyGroupDetail = new FlexTable();
	private FlexTable surveyDetail = new FlexTable();
	private FlexTable questionGroupDetail = new FlexTable();
	private FlexTable questionOptionDetail = new FlexTable();
	private FlexTable questionDetailPanel = new FlexTable();

	private SurveyServiceAsync svc = null;
	private BaseDto currentSelection;
	private Map<Long, QuestionDto[]> surveyOptionQuestionMap;

	private enum ButtonState {
		SURVEYGROUP, SURVEY, QUESTIONGROUP, QUESTION, NONE
	};

	public SurveyManagerPortlet(UserDto user) {
		super(title, scrollable, configurable, false, WIDTH, HEIGHT, user);
		svc = GWT.create(SurveyService.class);
		surveyOptionQuestionMap = new HashMap<Long, QuestionDto[]>();
		buildContentPanel();
	}

	@Override
	public String getName() {
		return NAME;
	}

	private void buildContentPanel() {
		contentPane = new VerticalPanel();
		setContent(contentPane);
		configureButtonPanel();
		surveyRoot = new Tree();
		surveyTree = new SurveyTree(surveyRoot, null, true);
		surveyTree.addSurveyListener(this);
		treeContainer.add(surveyRoot);
		treeContainer.add(detailContainer);
		contentPane.add(buttonPanel);
		contentPane.add(treeContainer);
		setButtonState(ButtonState.SURVEYGROUP);
	}

	private void removeAllWidgetsLoadThisWidget(Widget w) {
		// for (int i = 0; i < detailContainer.getWidgetCount(); i++) {
		// detailContainer.remove(i);
		// }
		treeContainer.remove(detailContainer);
		detailContainer = new VerticalPanel();
		treeContainer.add(detailContainer);
		detailContainer.add(w);
		w.setVisible(true);
	}

	/**
	 * toggles visibility of the various "add" buttons based on the state passed
	 * in
	 * 
	 * @param state
	 */
	private void setButtonState(ButtonState state) {
		if (getCurrentUser().hasPermission(PermissionConstants.EDIT_SURVEY)) {
			switch (state) {
			case SURVEYGROUP:
				addSurveyGroupButton.setVisible(true);
				addSurveyButton.setVisible(false);
				addQuestionGroupButton.setVisible(false);
				addQuestionButton.setVisible(false);
				break;
			case SURVEY:
				addSurveyGroupButton.setVisible(false);
				addSurveyButton.setVisible(true);
				addQuestionGroupButton.setVisible(false);
				addQuestionButton.setVisible(false);
				break;
			case QUESTIONGROUP:
				addSurveyGroupButton.setVisible(false);
				addSurveyButton.setVisible(false);
				addQuestionGroupButton.setVisible(true);
				addQuestionButton.setVisible(false);
				break;
			case QUESTION:
				addSurveyGroupButton.setVisible(false);
				addSurveyButton.setVisible(false);
				addQuestionGroupButton.setVisible(false);
				addQuestionButton.setVisible(true);
				break;
			case NONE:
				addSurveyGroupButton.setVisible(false);
				addSurveyButton.setVisible(false);
				addQuestionGroupButton.setVisible(false);
				addQuestionButton.setVisible(false);
				break;
			}
		}
	}

	/**
	 * constructs all the buttons and sets their initial visibility
	 */
	private void configureButtonPanel() {
		addSurveyGroupButton = constructAndInstallButton(
				TEXT_CONSTANTS.createSurveyGroup(), true);
		deleteSurveyGroupButton = constructAndInstallButton(
				"Delete Survey Group", false);

		addSurveyButton = constructAndInstallButton(
				TEXT_CONSTANTS.createSurvey(), true);
		deleteSurveyButton = constructAndInstallButton(
				TEXT_CONSTANTS.deleteSurvey(), false);

		addQuestionGroupButton = constructAndInstallButton(
				"Add Question Group", true);
		deleteQuestionGroupButton = constructAndInstallButton(
				"Delete Question Group", false);

		addQuestionButton = constructAndInstallButton(
				TEXT_CONSTANTS.createQuestion(), true);
		deleteQuestionButton = constructAndInstallButton(
				TEXT_CONSTANTS.deleteQuestion(), false);

		if (!getCurrentUser().hasPermission(PermissionConstants.EDIT_SURVEY)) {
			addSurveyGroupButton.setEnabled(false);
			deleteSurveyGroupButton.setEnabled(false);
			addSurveyButton.setEnabled(false);
			deleteSurveyButton.setEnabled(false);
			addQuestionGroupButton.setEnabled(false);
			deleteQuestionGroupButton.setEnabled(false);
			addQuestionButton.setEnabled(false);
			deleteQuestionButton.setEnabled(false);

			addSurveyGroupButton.setVisible(false);
			deleteSurveyGroupButton.setVisible(false);
			addSurveyButton.setVisible(false);
			deleteSurveyButton.setVisible(false);
			addQuestionGroupButton.setVisible(false);
			deleteQuestionGroupButton.setVisible(false);
			addQuestionButton.setVisible(false);
			deleteQuestionButton.setVisible(false);
		}

	}

	/**
	 * constructs a button with the text passed in and installs it in the
	 * buttonPanel member variable setting this class as a click handler.
	 * 
	 * @param text
	 * @param isVisible
	 * @return
	 */
	private Button constructAndInstallButton(String text, boolean isVisible) {
		Button b = new Button(text);
		b.setVisible(isVisible);
		b.addClickHandler(this);
		buttonPanel.add(b);
		return b;
	}

	/**
	 * handles all button clicks for the portlet
	 */
	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == addSurveyGroupButton) {
			loadSurveyGroupDetail(null);
		} else if (event.getSource() == deleteSurveyGroupButton) {
			deleteSurveyGroup(event);
		} else if (event.getSource() == addSurveyButton) {
			loadSurveyDetail(null);
		} else if (event.getSource() == deleteSurveyButton) {
			deleteSurvey(event);
		} else if (event.getSource() == addQuestionGroupButton) {
			loadQuestionGroupDetail(null);
		} else if (event.getSource() == deleteQuestionGroupButton) {
		} else if (event.getSource() == addQuestionButton) {
			loadQuestionDetails(null);
		} else if (event.getSource() == deleteQuestionButton) {
			deleteQuestion(getQuestionDto(), 1L);
		}
	}

	private void deleteSurvey(ClickEvent event) {
		final TreeItem item = surveyTree.getCurrentlySelectedItem();
		svc.deleteSurvey(getSurveyDtoFromPanel(event), 1L,
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
								.error(), TEXT_CONSTANTS.errorTracePrefix()
								+ " " + caught.getLocalizedMessage());
						errDia.showCentered();

					}

					@Override
					public void onSuccess(String result) {
						surveyDetail.setVisible(false);
						surveyTree.removeItem(item);
					}

				});

	}

	private void deleteSurveyGroup(ClickEvent event) {
		final TreeItem item = surveyTree.getCurrentlySelectedItem();
		svc.deleteSurveyGroup(getSurveyGroupDto(), new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.error(), TEXT_CONSTANTS
								.errorTracePrefix()
								+ " "
								+ caught.getLocalizedMessage());
				errDia.showCentered();
			}

			@Override
			public void onSuccess(String result) {
				if (result == null) {
					Window.alert(TEXT_CONSTANTS.deleteComplete());
					surveyTree.removeItem(item);
					surveyGroupDetail.setVisible(false);

				} else {
					MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS
							.error(), TEXT_CONSTANTS.error());
					errDia.showCentered();
				}
			}
		});
	}

	private SurveyDto getSurveyDtoFromPanel(ClickEvent event) {
		return getSurveyDto();
	}

	private void loadQuestionDetails(final QuestionDto item) {
		Window.scrollTo(0, 0);
		setButtonState(ButtonState.NONE);
		questionOptionDetail.removeAllRows();
		questionDetailPanel.removeAllRows();
		treeContainer.remove(detailContainer);
		questionDetailPanel.setVisible(true);

		detailContainer = new VerticalPanel();
		treeContainer.add(detailContainer);
		TextBox questionId = new TextBox();
		questionId.setVisible(false);
		TextArea questionText = new TextArea();
		questionText.setSize("30em", "5em");
		// questionText.setWidth("40em");
		TextArea tip = new TextArea();
		tip.setSize("30em", "5em");
		TextBox validationRule = new TextBox();
		ListBox lbOrder = new ListBox();
		for (Integer i = 1; i < 100; i++) {
			lbOrder.addItem(i.toString());
		}

		CheckBox mandatoryQuestion = new CheckBox();
		CheckBox dependentQuestion = new CheckBox();

		if (item != null) {
			if (item.getOrder() != null) {
				lbOrder.setItemSelected(item.getOrder() - 1, true);
			} else {
				Integer order = getMaxOrder((QuestionGroupDto) surveyTree
						.getCurrentlySelectedItem().getUserObject());
				lbOrder.setSelectedIndex(order + 1);
			}
			questionId.setText(item.getKeyId().toString());
			if (item.getText() != null)
				questionText.setText(item.getText());
			if (item.getTip() != null
					&& !"null".equalsIgnoreCase(item.getTip().trim())) {
				tip.setText(item.getTip());
			}
			if (item.getValidationRule() != null
					&& !"null".equalsIgnoreCase(item.getTip().trim())) {
				validationRule.setText(item.getValidationRule());
			}
			if (item.getMandatoryFlag() != null)
				if (item.getMandatoryFlag())
					mandatoryQuestion.setValue(true);
				else
					mandatoryQuestion.setValue(false);
		} else {
			Integer order = getMaxOrder((QuestionGroupDto) surveyTree
					.getCurrentlySelectedItem().getUserObject());
			lbOrder.setSelectedIndex(order + 1);
		}
		ListBox questionTypeLB = new ListBox();
		// FREE_TEXT, OPTION, NUMBER, GEO, PICTURE, VIDEO, STRENGTH
		questionTypeLB.addItem(TEXT_CONSTANTS.freeText());
		questionTypeLB.addItem(TEXT_CONSTANTS.option());
		questionTypeLB.addItem(TEXT_CONSTANTS.number());
		questionTypeLB.addItem(TEXT_CONSTANTS.geo());
		questionTypeLB.addItem(TEXT_CONSTANTS.photo());
		questionTypeLB.addItem(TEXT_CONSTANTS.video());
		questionTypeLB.addItem(TEXT_CONSTANTS.strength());
		if (item != null) {
			QuestionDto.QuestionType qType = item.getType();
			if (qType.equals(QuestionType.FREE_TEXT)) {
				questionTypeLB.setSelectedIndex(0);
			} else if (qType.equals(QuestionType.OPTION)) {
				questionTypeLB.setSelectedIndex(1);
				loadQuestionOptionDetail(item);
			} else if (qType.equals(QuestionType.NUMBER)) {
				questionTypeLB.setSelectedIndex(2);
			} else if (qType.equals(QuestionType.GEO)) {
				questionTypeLB.setSelectedIndex(3);
			} else if (qType.equals(QuestionType.PHOTO)) {
				questionTypeLB.setSelectedIndex(4);
			} else if (qType.equals(QuestionType.VIDEO)) {
				questionTypeLB.setSelectedIndex(5);
			} else if (qType.equals(QuestionType.STRENGTH)) {
				questionTypeLB.setSelectedIndex(6);
				loadQuestionOptionDetail(item);
			}
		}

		questionTypeLB.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int idx = ((ListBox) event.getSource()).getSelectedIndex();
				if (idx == 1 || idx == 6) {
					questionOptionDetail.clear(true);
					loadQuestionOptionDetail(item);
					if (detailContainer.getWidget(1) instanceof FlexTable)
						questionOptionDetail.setVisible(true);
					if (questionOptionDetail != null
							&& detailContainer.getWidget(1) instanceof HorizontalPanel)
						detailContainer.insert(questionOptionDetail, 1);
					// Fix
					// questionDetailPanel.setWidget(6, 1,
					// questionOptionDetail);
					// questionDetailPanel.getWidget(6, 1).setVisible(true);

				} else {
					if (detailContainer.getWidget(1) instanceof FlexTable)
						detailContainer.remove(1);
				}
			}

		});

		dependentQuestion.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				CheckBox dependentCB = (CheckBox) event.getSource();

				loadDependencyTable(dependentCB.getValue());

			}

		});

		// Fix this section so that multiple buttons aren't created

		Button saveQuestionButton = new Button(TEXT_CONSTANTS.saveQuestion());
		Button deleteQuestionButton = new Button(
				TEXT_CONSTANTS.deleteQuestion());
		Button viewResponsesButton = new Button(TEXT_CONSTANTS.viewResponses());
		Button editTranslationButton = new Button(
				TEXT_CONSTANTS.editTranslations());
		if (!getCurrentUser().hasPermission(PermissionConstants.EDIT_SURVEY)) {
			saveQuestionButton.setVisible(false);
			deleteQuestionButton.setVisible(false);
			editTranslationButton.setVisible(false);
		}

		questionId.setVisible(false);

		questionDetailPanel.setWidget(0, 0, questionId);
		questionDetailPanel.setWidget(1, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.questionText()));
		questionDetailPanel.setWidget(1, 1, questionText);
		questionDetailPanel.setWidget(2, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.questionType()));
		questionDetailPanel.setWidget(2, 1, questionTypeLB);
		questionDetailPanel.setWidget(3, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.questionToolTip()));
		questionDetailPanel.setWidget(3, 1, tip);
		questionDetailPanel.setWidget(4, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.validationRule()));
		questionDetailPanel.setWidget(4, 1, validationRule);
		questionDetailPanel.setWidget(5, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.mandatory()));
		questionDetailPanel.setWidget(5, 1, mandatoryQuestion);
		questionDetailPanel.setWidget(6, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.order()));
		questionDetailPanel.setWidget(6, 1, lbOrder);
		questionDetailPanel.setWidget(7, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.dependent()));
		questionDetailPanel.setWidget(7, 1, dependentQuestion);

		if (item != null && item.getQuestionDependency() != null
				&& item.getQuestionDependency().getQuestionId() != null) {
			dependentQuestion.setValue(true);
			loadDependencyTable(true);
		}
		saveQuestionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				try {
					String message = validateQuestion();
					if (message == null) {
						saveQuestion();
					} else {
						Window.alert(TEXT_CONSTANTS.pleaseCorrect() + " "
								+ message);
					}
				} catch (Exception e) {
					Window.alert(TEXT_CONSTANTS.noQuestionGroupSelected());
				}
			}

			private String validateQuestion() {
				String message = null;
				QuestionDto qDto = getQuestionDto();
				if (qDto.getText() == null)
					message = TEXT_CONSTANTS.questionTextMandatory() + "\n";
				if (qDto.getType().equals(QuestionType.OPTION)) {
					ArrayList<QuestionOptionDto> qoList = qDto
							.getOptionContainerDto().getOptionsList();
					if (qoList == null) {
						message = message + " "
								+ TEXT_CONSTANTS.optionMandatory() + "\n";
					} else {
						for (QuestionOptionDto qoDto : qoList) {
							if (qoDto.getText() == null
									|| qoDto.getText().trim().equals("")) {
								message = message + " "
										+ TEXT_CONSTANTS.optionNotBlank()
										+ "\n";
							}
						}
					}
				}
				return message;
			}

		});
		deleteQuestionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				deleteQuestion(getQuestionDto(), 1L);
			}

		});

		detailContainer.add(questionDetailPanel);
		if (questionOptionDetail != null)
			detailContainer.add(questionOptionDetail);

		buttonHPanel = null;
		buttonHPanel = new HorizontalPanel();

		buttonHPanel.add(saveQuestionButton);
		buttonHPanel.add(deleteQuestionButton);
		buttonHPanel.add(editTranslationButton);
		buttonHPanel.add(viewResponsesButton);

		detailContainer.add(buttonHPanel);

		viewResponsesButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				QuestionResponseDialog dia = new QuestionResponseDialog(
						currentSelection.getKeyId());
				dia.show();
			}
		});

		editTranslationButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (currentSelection instanceof QuestionDto) {
					SurveyQuestionTranslationDialog dia = new SurveyQuestionTranslationDialog(
							(QuestionDto) currentSelection,
							SurveyManagerPortlet.this);
					dia.show();
				} else {
					// Likely have the QG selected because this is a new
					// question need to figure out how to get QuestionDto
					Window.alert(TEXT_CONSTANTS.saveQuestionFirst());
				}
			}
		});

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

	private void loadDependencyTable(Boolean dependentValue) {
		if (dependentValue) {
			QuestionDto tempQuestion = null;
			if (currentSelection instanceof QuestionDto) {
				tempQuestion = (QuestionDto) currentSelection;
			} else {
				tempQuestion = new QuestionDto();
				tempQuestion.setSurveyId(((QuestionGroupDto) currentSelection)
						.getSurveyId());
			}
			final QuestionDto currentQuestion = tempQuestion;

			final MessageDialog dia = new MessageDialog(
					TEXT_CONSTANTS.loading(), TEXT_CONSTANTS.pleaseWait(), true);
			dia.showCentered();
			if (surveyOptionQuestionMap.get(currentQuestion.getSurveyId()) == null) {
				// if we haven't loaded the Option questions for this survey, do
				// it now
				svc.listSurveyQuestionByType(currentQuestion.getSurveyId(),
						QuestionType.OPTION,
						new AsyncCallback<QuestionDto[]>() {

							@Override
							public void onFailure(Throwable caught) {
								dia.hide();
								MessageDialog errDia = new MessageDialog(
										TEXT_CONSTANTS.error(), TEXT_CONSTANTS
												.errorTracePrefix()
												+ " "
												+ caught.getLocalizedMessage());
								errDia.showCentered();
							}

							@Override
							public void onSuccess(QuestionDto[] result) {
								surveyOptionQuestionMap.put(
										currentQuestion.getSurveyId(), result);
								populateDependencySelection(currentQuestion,
										result);
								dia.hide();
							}
						});
			} else {
				populateDependencySelection(currentQuestion,
						surveyOptionQuestionMap.get(currentQuestion
								.getSurveyId()));
				dia.hide();
			}
		} else {
			questionDetailPanel.removeRow(8);
		}
	}

	private void populateDependencySelection(QuestionDto currentQuestion,
			final QuestionDto[] optionQuestions) {
		questionDetailPanel.setWidget(8, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.dependentOnQuestion()));
		ListBox questionLB = new ListBox();
		ListBox answerLB = new ListBox();
		QuestionDependencyDto item = null;

		if (currentQuestion != null) {
			item = currentQuestion.getQuestionDependency();

			for (int i = 0; i < optionQuestions.length; i++) {
				if (currentQuestion.getKeyId() == null
						|| !currentQuestion.getKeyId().equals(
								optionQuestions[i].getKeyId())) {
					String txt = optionQuestions[i].getText();
					if (txt != null && txt.trim().length() > MAX_Q_LENGTH) {
						txt = txt.substring(0, MAX_Q_LENGTH);
					}
					questionLB.addItem(txt, optionQuestions[i].getKeyId()
							.toString());
					if (item != null
							&& item.getQuestionId().equals(
									optionQuestions[i].getKeyId())) {
						questionLB
								.setSelectedIndex(questionLB.getItemCount() - 1);
					}
				}
			}
			questionDetailPanel.setWidget(8, 1, questionLB);
			TextBox dependentQId = new TextBox();

			if (item != null && item.getKeyId() != null)
				dependentQId.setText(item.getKeyId().toString());

			dependentQId.setVisible(false);
			questionDetailPanel.setWidget(8, 2, dependentQId);
			answerLB.setVisible(false);
			questionDetailPanel.setWidget(8, 3, answerLB);

			if (questionLB.getItemCount() == 1 && optionQuestions != null
					&& optionQuestions.length > 0) {
				loadDepQA(optionQuestions[0], null);
			}

			TextBox qDepId = new TextBox();
			qDepId.setVisible(false);
			questionDetailPanel.setWidget(8, 4, qDepId);

			if (currentQuestion != null
					&& currentQuestion.getQuestionDependency() != null) {
				// set existing value
				qDepId.setText(currentQuestion.getQuestionDependency()
						.getQuestionId().toString());
				loadDepQA(optionQuestions[questionLB.getSelectedIndex()],
						currentQuestion.getQuestionDependency()
								.getAnswerValue());

			}
			// don't install the change handler until the end so adding values
			// to the list box doesn't trigger the handler
			questionLB.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					ListBox questionLBox = (ListBox) event.getSource();
					loadDepQA(optionQuestions[questionLBox.getSelectedIndex()],
							null);

				}
			});
		}
	}

	private void loadDepQA(QuestionDto qDto, final String selectedAnswer) {
		if (qDto != null) {
			if (qDto.getOptionContainerDto() != null) {
				updateDependencyAnswerSelection(qDto.getOptionContainerDto()
						.getOptionsList(), selectedAnswer);
			} else {
				// if the option container is null, we probably have not
				// yet loaded the question details. so do it now
				svc.loadQuestionDetails(qDto.getKeyId(),
						new AsyncCallback<QuestionDto>() {

							@Override
							public void onSuccess(QuestionDto result) {
								if (result.getOptionContainerDto() != null) {
									updateDependencyAnswerSelection(result
											.getOptionContainerDto()
											.getOptionsList(), selectedAnswer);
								}
							}

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(TEXT_CONSTANTS.errorTracePrefix()
										+ " " + caught.getLocalizedMessage());
							}
						});
			}
		}
	}

	private void updateDependencyAnswerSelection(
			List<QuestionOptionDto> qoList, String selectedAnswer) {
		ListBox answerLB = (ListBox) questionDetailPanel.getWidget(8, 3);
		answerLB.clear();
		int answerIndex = -1;
		if (qoList != null) {
			int i = 0;
			for (QuestionOptionDto qoDto : qoList) {
				answerLB.addItem(qoDto.getText(), qoDto.getText());
				if (selectedAnswer != null
						&& selectedAnswer.equals(qoDto.getText())) {
					answerIndex = i;
				}
				i++;
			}
		}
		if (answerIndex > -1) {
			answerLB.setSelectedIndex(answerIndex);
		}
		answerLB.setVisible(true);
	}

	private void loadQuestionOptionDetail(QuestionDto item) {
		// questionOptionDetail.removeAllRows();
		Integer row = 0;
		OptionContainerDto ocDto = null;
		ArrayList<QuestionOptionDto> questionOptionList = null;
		CheckBox allowOther = new CheckBox();
		CheckBox allowMultiple = new CheckBox();
		TextBox ocId = new TextBox();
		ocId.setVisible(false);

		if (item != null) {
			ocDto = item.getOptionContainerDto();
			if (ocDto != null) {
				if (ocDto.getAllowMultipleFlag() != null) {
					allowMultiple.setValue(ocDto.getAllowMultipleFlag());
				}
				if (ocDto.getKeyId() != null) {
					ocId.setText(ocDto.getKeyId().toString());
					ocId.setVisible(false);
				}
				if (ocDto.getAllowOtherFlag() != null)
					allowOther.setValue(ocDto.getAllowOtherFlag());
				if (ocDto.getOptionsList() != null)
					questionOptionList = ocDto.getOptionsList();
			}
		}

		questionOptionDetail.setWidget(row, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.allowOther()));
		questionOptionDetail.setWidget(row, 1, allowOther);
		questionOptionDetail.setWidget(row, 2,
				ViewUtil.initLabel(TEXT_CONSTANTS.allowMultiple()));
		questionOptionDetail.setWidget(row, 3, allowMultiple);
		questionOptionDetail.setWidget(row, 4, ocId);

		row++;

		if (ocDto != null) {
			if (ocDto.getAllowOtherFlag() != null)
				allowOther.setValue(ocDto.getAllowOtherFlag());
			if (ocDto.getAllowMultipleFlag() != null)
				allowMultiple.setValue(ocDto.getAllowMultipleFlag());
		}

		if (questionOptionList != null) {
			for (QuestionOptionDto qoDto : questionOptionList) {
				loadQuestionOptionRowDetail(qoDto, row++);
			}
		}

		Button addNewOptionButton = new Button(TEXT_CONSTANTS.addOption());
		if (!getCurrentUser().hasPermission(PermissionConstants.EDIT_SURVEY)) {
			addNewOptionButton.setVisible(false);
		}
		// Button deleteOptionButton = new Button("Delete Option");

		questionOptionDetail.setWidget(row, 2, addNewOptionButton);
		addNewOptionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				loadQuestionOptionRowDetail(null,
						questionOptionDetail.getRowCount() - 1);
			}

		});
	}

	private void loadQuestionOptionRowDetail(QuestionOptionDto item, Integer row) {

		TextBox optionValue = new TextBox();
		optionValue.setWidth("3em");
		ListBox lbOptOrder = new ListBox();
		for (Integer i = 0; i < 50; i++) {
			lbOptOrder.addItem(i.toString());
		}
		TextBox optionText = new TextBox();
		optionText.setWidth("30em");
		final TextBox optionId = new TextBox();
		optionId.setVisible(false);
		if (item != null) {
			if (item.getKeyId() != null) {
				optionId.setText(item.getKeyId().toString());
				optionId.setVisible(false);
			}
			if (item.getCode() != null) {
				optionValue.setText(item.getCode());
			}
			if (item.getText() != null) {
				optionText.setText(item.getText());
			}
			if (item.getOrder() != null) {
				lbOptOrder.setSelectedIndex(item.getOrder());
			} else
				lbOptOrder.setSelectedIndex(questionOptionDetail.getRowCount());
		} else {
			lbOptOrder.setSelectedIndex(row);
		}
		Button deleteOptionButton = new Button(TEXT_CONSTANTS.delete());
		deleteOptionButton.setTitle(row.toString());

		questionOptionDetail.insertRow(row);

		questionOptionDetail.setWidget(row, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.order()));
		questionOptionDetail.setWidget(row, 1, lbOptOrder);
		questionOptionDetail.setWidget(row, 2,
				ViewUtil.initLabel(TEXT_CONSTANTS.optionText()));
		questionOptionDetail.setWidget(row, 3, optionText);
		questionOptionDetail.setWidget(row, 4, optionId);
		questionOptionDetail.setWidget(row, 5, deleteOptionButton);

		deleteOptionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Button b = (Button) event.getSource();
				Integer row = Integer.parseInt(b.getTitle());
				questionOptionDetail.removeRow(row);

				for (Integer i = row; i < questionOptionDetail.getRowCount() - 1; i++) {
					ListBox lbOptOrder = (ListBox) questionOptionDetail
							.getWidget(i, 1);
					lbOptOrder.setSelectedIndex(i);
					Button bDelete = (Button) questionOptionDetail.getWidget(i,
							5);
					bDelete.setTitle(i.toString());
				}
			}

		});

	}

	private void saveQuestion() throws Exception {
		QuestionDto dto = getQuestionDto();
		Long parentId = null;
		if (currentSelection instanceof QuestionDto) {
			parentId = ((QuestionDto) currentSelection).getQuestionGroupId();
		} else if (currentSelection instanceof QuestionGroupDto) {
			parentId = currentSelection.getKeyId();
		}
		final BaseDto treeParent = currentSelection;
		final boolean isNew;
		if (dto.getKeyId() != null) {
			isNew = false;
		} else {
			isNew = true;
			QuestionGroupDto parentQGDto = (QuestionGroupDto) surveyTree
					.getCurrentlySelectedItem().getUserObject();
			dto.setPath(parentQGDto.getPath() + "/" + parentQGDto.getCode());
			dto.setQuestionGroupId(parentQGDto.getKeyId());
		}

		svc.saveQuestion(dto, parentId, new AsyncCallback<QuestionDto>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(TEXT_CONSTANTS.errorTracePrefix() + " "
						+ caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(QuestionDto result) {
				if (isNew) {
					surveyTree.addChild(treeParent, result);
				} else {
					surveyTree.replaceUserObject(currentSelection, result);
					currentSelection = result;
				}
				if (result.getQuestionDependency() != null)
					((TextBox) questionDetailPanel.getWidget(8, 4))
							.setText(result.getQuestionDependency()
									.getQuestionId().toString());

				Window.alert(TEXT_CONSTANTS.saveComplete());
			}

		});

	}

	private QuestionDto getQuestionDto() {
		QuestionDto value = new QuestionDto();
		if (currentSelection instanceof QuestionDto) {
			value = (QuestionDto) currentSelection;
		}

		TextBox questionId = (TextBox) questionDetailPanel.getWidget(0, 0);
		// TextBox dependencyId = (TextBox) questionDetailPanel.getWidget(0, 0);
		TextArea questionText = (TextArea) questionDetailPanel.getWidget(1, 1);
		ListBox questionTypeLB = (ListBox) questionDetailPanel.getWidget(2, 1);

		TextArea tip = (TextArea) questionDetailPanel.getWidget(3, 1);
		TextBox validationRule = (TextBox) questionDetailPanel.getWidget(4, 1);
		CheckBox mandatoryQuestion = (CheckBox) questionDetailPanel.getWidget(
				5, 1);

		ListBox lbOrder = (ListBox) questionDetailPanel.getWidget(6, 1);

		if (questionId.getText().length() > 0) {
			value.setKeyId(new Long(questionId.getText()));
		}

		if (questionText.getText().length() > 0) {
			value.setText(questionText.getText().trim());
		}

		if (lbOrder.getSelectedIndex() > -1) {
			value.setOrder(Integer.parseInt(lbOrder.getItemText(lbOrder
					.getSelectedIndex())));
		}

		if (ViewUtil.isTextPopulated(tip)) {
			value.setTip(tip.getText());
		}
		if (ViewUtil.isTextPopulated(validationRule)) {
			value.setValidationRule(validationRule.getText());
		}
		value.setMandatoryFlag(mandatoryQuestion.getValue());

		if (questionTypeLB.getSelectedIndex() == 0) {
			value.setType(QuestionType.FREE_TEXT);
		} else if (questionTypeLB.getSelectedIndex() == 1) {
			value.setType(QuestionType.OPTION);
			configureOptionPanel(value);
		} else if (questionTypeLB.getSelectedIndex() == 2) {
			value.setType(QuestionType.NUMBER);
		} else if (questionTypeLB.getSelectedIndex() == 3) {
			value.setType(QuestionType.GEO);
		} else if (questionTypeLB.getSelectedIndex() == 4) {
			value.setType(QuestionType.PHOTO);
		} else if (questionTypeLB.getSelectedIndex() == 5) {
			value.setType(QuestionType.VIDEO);
		} else if (questionTypeLB.getSelectedIndex() == 6) {
			value.setType(QuestionType.STRENGTH);
			configureOptionPanel(value);
		}

		CheckBox dependentQuestionFlag = (CheckBox) questionDetailPanel
				.getWidget(7, 1);
		if (dependentQuestionFlag.getValue()) {
			ListBox questionLB = (ListBox) questionDetailPanel.getWidget(8, 1);
			String selectedValue = questionLB.getValue(questionLB
					.getSelectedIndex());
			QuestionDependencyDto qdDto = new QuestionDependencyDto();
			qdDto.setQuestionId(new Long(selectedValue));
			value.setQuestionDependency(qdDto);
			TextBox dependentQId = (TextBox) questionDetailPanel
					.getWidget(8, 2);
			if (dependentQId.getText().length() > 0)
				qdDto.setKeyId(new Long(dependentQId.getText()));
			ListBox answerLB = (ListBox) questionDetailPanel.getWidget(8, 3);
			String selectedAnswerValue = answerLB.getItemText(answerLB
					.getSelectedIndex());
			qdDto.setAnswerValue(selectedAnswerValue);
			TextBox qDepId = (TextBox) questionDetailPanel.getWidget(8, 4);
			if (qDepId.getText().length() > 0)
				qdDto.setKeyId(new Long(qDepId.getText()));
		} else {
			value.setQuestionDependency(null);
		}

		return value;
	}

	private void configureOptionPanel(QuestionDto value) {
		FlexTable questionOptionTable = (FlexTable) detailContainer
				.getWidget(1);

		CheckBox allowOther = (CheckBox) questionOptionDetail.getWidget(0, 1);
		CheckBox allowMultiple = (CheckBox) questionOptionDetail
				.getWidget(0, 3);

		TextBox ocId = (TextBox) questionOptionDetail.getWidget(0, 4);

		OptionContainerDto ocDto = new OptionContainerDto();
		if (ocId.getText().length() > 0)
			ocDto.setKeyId(new Long(ocId.getText()));
		ocDto.setAllowMultipleFlag(allowMultiple.getValue());
		ocDto.setAllowOtherFlag(allowOther.getValue());

		for (int row = 1; row < questionOptionTable.getRowCount() - 1; row++) {
			QuestionOptionDto qoDto = new QuestionOptionDto();
			// TextBox optionValue = (TextBox)
			// questionOptionDetail.getWidget(row,
			// 1);
			ListBox lbOptOrder = (ListBox) questionOptionDetail.getWidget(row,
					1);
			if (lbOptOrder.getSelectedIndex() > 0) {
				qoDto.setOrder(lbOptOrder.getSelectedIndex());
			}
			TextBox optionText = (TextBox) questionOptionDetail.getWidget(row,
					3);
			TextBox qoId = (TextBox) questionOptionDetail.getWidget(row, 4);
			// qoDto.setCode(optionValue.getText());
			if (optionText.getText() != null) {
				String sanitizedOption = optionText.getText().replaceAll(",",
						" ");
				qoDto.setText(sanitizedOption);
				qoDto.setCode(sanitizedOption);
				optionText.setText(sanitizedOption);
			}
			if (qoId.getText().length() > 0)
				qoDto.setKeyId(new Long(qoId.getText()));
			qoDto.setOrder(lbOptOrder.getSelectedIndex());
			ocDto.addQuestionOption(qoDto);
		}
		value.setOptionContainerDto(ocDto);
	}

	private void deleteQuestion(QuestionDto value, Long questionGroupId) {
		final TreeItem item = surveyTree.getCurrentlySelectedItem();
		svc.deleteQuestion(value, questionGroupId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(TEXT_CONSTANTS.errorTracePrefix() + " "
						+ caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(String result) {
				if (result == null) {
					Window.alert(TEXT_CONSTANTS.deleteComplete());
					questionDetailPanel.setVisible(false);
					surveyTree.removeItem(item);
				} else {
					Window.alert(result);
				}

			}
		});
	}

	private void loadSurveyGroupDetail(SurveyGroupDto item) {
		setButtonState(ButtonState.SURVEY);
		TextBox surveyGroupId = new TextBox();
		TextBox surveyGroupCode = new TextBox();
		TextBox surveyGroupDesc = new TextBox();

		if (item != null) {
			surveyGroupId.setText(item.getKeyId().toString());
			surveyGroupCode.setText(item.getCode());
			surveyGroupDesc.setText(item.getDescription());
		}

		surveyGroupId.setVisible(false);
		Button saveSurveyGroupButton = new Button(
				TEXT_CONSTANTS.saveSurveyGroup());
		Button deleteSurveyGroupButton = new Button(
				TEXT_CONSTANTS.deleteSurveyGroup());

		surveyGroupDetail.setWidget(0, 1, surveyGroupId);
		surveyGroupDetail.setWidget(1, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.code()));
		surveyGroupDetail.setWidget(1, 1, surveyGroupCode);
		surveyGroupDetail.setWidget(2, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.description()));
		surveyGroupDetail.setWidget(2, 1, surveyGroupDesc);
		surveyGroupDetail.setWidget(3, 0, saveSurveyGroupButton);
		surveyGroupDetail.setWidget(3, 1, deleteSurveyGroupButton);
		saveSurveyGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (validateSurveyGroup())
					saveSurveyGroup();
				else
					Window.alert(TEXT_CONSTANTS.cannotSaveSurveyGroup());
			}

		});
		deleteSurveyGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				deleteSurveyGroup(event);
			}

		});
		removeAllWidgetsLoadThisWidget(surveyGroupDetail);
	}

	private Boolean validateSurveyGroup() {
		SurveyGroupDto surveyGroupDto = getSurveyGroupDto();
		if (surveyGroupDto.getCode() == null) {
			return false;
		}
		return true;
	}

	private void loadSurveyDetail(final SurveyDto item) {
		setButtonState(ButtonState.QUESTIONGROUP);
		TextBox surveyId = new TextBox();
		surveyId.setVisible(false);
		TextBox surveyname = new TextBox();
		TextBox surveyDesc = new TextBox();
		TextBox version = new TextBox();

		if (item != null) {
			surveyId.setText(item.getKeyId().toString());
			if (item.getName() != null)
				surveyname.setText(item.getName());
			if (item.getDescription() != null)
				surveyDesc.setText(item.getDescription());
			if (item.getVersion() != null)
				version.setText(item.getVersion());

		}

		Button saveSurveyButton = new Button(TEXT_CONSTANTS.save());
		Button deleteSurveyButton = new Button(TEXT_CONSTANTS.delete());
		Button publishSurveyButton = new Button(TEXT_CONSTANTS.publishSurvey());
		Button exportSummaryButton = new Button(TEXT_CONSTANTS.exportSummary());
		Button exportRawDataButton = new Button(TEXT_CONSTANTS.exportRawData());
		Button exportFormButton = new Button(TEXT_CONSTANTS.exportSurveyForm());
		Button remapSurveyFormButton = new Button(
				TEXT_CONSTANTS.remapToAccessPoint());
		Button importRawDataButton = new Button(TEXT_CONSTANTS.importRawData());
		Button genKMLButton = new Button(TEXT_CONSTANTS.generateKml());

		if (!getCurrentUser().hasPermission(PermissionConstants.EDIT_SURVEY)) {
			saveSurveyButton.setVisible(false);
			deleteSurveyButton.setVisible(false);						
			remapSurveyFormButton.setVisible(false);
		}
		if (getCurrentUser().hasPermission(PermissionConstants.PUBLISH_SURVEY)) {
			publishSurveyButton.setVisible(true);
		}else{
			publishSurveyButton.setVisible(false);
		}
		if (!getCurrentUser().hasPermission(
				PermissionConstants.IMPORT_SURVEY_DATA)) {
			importRawDataButton.setVisible(false);
		}

		surveyDetail.setWidget(0, 0, surveyId);
		surveyDetail.setWidget(1, 0, new Label(TEXT_CONSTANTS.name()));
		surveyDetail.setWidget(1, 1, surveyname);
		surveyDetail.setWidget(2, 0, new Label(TEXT_CONSTANTS.description()));
		surveyDetail.setWidget(2, 1, surveyDesc);
		surveyDetail.setWidget(3, 0, new Label(TEXT_CONSTANTS.version()));
		surveyDetail.setWidget(3, 1, version);
		surveyDetail.setWidget(4, 0, saveSurveyButton);
		surveyDetail.setWidget(4, 1, deleteSurveyButton);
		surveyDetail.setWidget(4, 2, publishSurveyButton);
		surveyDetail.setWidget(4, 3, exportSummaryButton);
		surveyDetail.setWidget(4, 4, exportRawDataButton);
		surveyDetail.setWidget(4, 5, exportFormButton);
		surveyDetail.setWidget(5, 1, remapSurveyFormButton);
		surveyDetail.setWidget(5, 2, importRawDataButton);
		surveyDetail.setWidget(5, 3, genKMLButton);

		removeAllWidgetsLoadThisWidget(surveyDetail);

		remapSurveyFormButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Long surveyId = new Long(((TextBox) surveyDetail
						.getWidget(0, 0)).getText());
				svc.rerunAPMappings(surveyId, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(TEXT_CONSTANTS.couldNotProcessRemap()
								+ " " + caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(Void result) {
						Window.alert(TEXT_CONSTANTS.remapSubmitted());
					}

				});
			}

		});

		deleteSurveyButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				deleteSurvey(event);
			}
		});

		saveSurveyButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				try {
					if (validateSurvey()) {
						saveSurvey();
					} else {
						Window.alert(TEXT_CONSTANTS.cannotSaveSurvey());
					}
				} catch (Exception e) {
					Window.alert(TEXT_CONSTANTS.noSurveyGroupSelected());
				}
			}

			private boolean validateSurvey() {
				SurveyDto surveyDto = getSurveyDto();
				if (surveyDto.getCode() == null) {
					return false;
				}
				return true;
			}

		});

		publishSurveyButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Long surveyId = 0L;
				TextBox surveyIdTB = (TextBox) surveyDetail.getWidget(0, 0);
				if (surveyIdTB.getText().length() > 0) {
					surveyId = new Long(surveyIdTB.getText());
					svc.publishSurveyAsync(surveyId, new AsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(TEXT_CONSTANTS.couldNotPublish());
						}

						@Override
						public void onSuccess(Void result) {
							Window.alert(TEXT_CONSTANTS.surveyPublished());

						}
					});
				} else {
					Window.alert(TEXT_CONSTANTS.saveBeforePublish());
				}
			}
		});

		genKMLButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				String appletString = "<applet width='100' height='30' code=org.waterforpeople.mapping.dataexport.KMLApplet width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar,velocity-1.6.2-dep.jar'>";
				appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar, velocity-1.6.2-dep.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
				appletString += "<PARAM name='exportType' value='SURVEY_FORM'>";
				appletString += "<PARAM name='surveyId' value='"
						+ item.getKeyId() + "'>";
				appletString += "</applet>";
				HTML html = new HTML();
				html.setHTML(appletString);
				surveyDetail.setWidget(5, 0, html);
			}

		});

		exportSummaryButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar'>";
				appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
				appletString += "<PARAM name='exportType' value='SURVEY_SUMMARY'>";
				appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
				appletString += "<PARAM name='criteria' value=surveyId="
						+ item.getKeyId() + ">";
				appletString += "</applet>";
				HTML html = new HTML();
				html.setHTML(appletString);
				surveyDetail.setWidget(5, 0, html);
			}
		});

		exportRawDataButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar'>";
				appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
				appletString += "<PARAM name='exportType' value='RAW_DATA'>";
				appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
				appletString += "<PARAM name='criteria' value=surveyId="
						+ item.getKeyId() + ">";
				appletString += "</applet>";
				HTML html = new HTML();
				html.setHTML(appletString);
				surveyDetail.setWidget(5, 0, html);
			}
		});

		exportFormButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar'>";
				appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
				appletString += "<PARAM name='exportType' value='SURVEY_FORM'>";
				appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
				appletString += "<PARAM name='criteria' value=surveyId="
						+ item.getKeyId() + ">";
				appletString += "</applet>";
				HTML html = new HTML();
				html.setHTML(appletString);
				surveyDetail.setWidget(5, 0, html);
			}
		});
		importRawDataButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String appletString = "<applet width='100' height='30' code=org.waterforpeople.mapping.dataexport.RawDataSpreadsheetImportApplet width=256 height=256 archive='exporterapplet.jar,json.jar,poi-3.5-signed.jar'>";
				appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar, poi-3.5-signed.jar'><PARAM name='cache-version' value'1.3, 1.0, 3.5'>";
				appletString += "<PARAM name='exportType' value='SURVEY_FORM'>";
				appletString += "<PARAM name='surveyId' value='"
						+ item.getKeyId() + "'>";
				appletString += "</applet>";
				HTML html = new HTML();
				html.setHTML(appletString);
				surveyDetail.setWidget(5, 0, html);

			}

		});

	}

	private void loadQuestionGroupDetail(QuestionGroupDto item) {
		setButtonState(ButtonState.QUESTION);
		removeAllWidgetsLoadThisWidget(questionGroupDetail);
		TextBox questionGroupId = new TextBox();
		questionGroupId.setVisible(false);
		TextBox name = new TextBox();
		TextBox description = new TextBox();

		if (item != null) {
			questionGroupId.setText(item.getKeyId().toString());
			name.setText(item.getCode());
			description.setText(item.getDescription());
		}
		ListBox order = new ListBox();
		for (Integer i = 1; i < 50; i++) {
			order.addItem(i.toString());
			if (item != null) {
				if (item.getOrder().equals(i)) {
					order.setSelectedIndex(i - 1);
				}
			}
		}
		if (item == null) {
			if (surveyTree.getCurrentlySelectedItem() != null) {
				int countOfQG = surveyTree.getCurrentlySelectedItem()
						.getChildCount();
				order.setSelectedIndex(countOfQG);
			} else {
				// find currently selected item

			}
		}

		Button saveQuestionGroupButton = new Button(TEXT_CONSTANTS.save());
		Button deleteQuestionGroupButton = new Button(TEXT_CONSTANTS.delete());
		if (!getCurrentUser().hasPermission(PermissionConstants.EDIT_SURVEY)) {
			saveQuestionGroupButton.setVisible(false);
			deleteQuestionGroupButton.setVisible(false);
		}

		questionGroupDetail.setWidget(0, 0, questionGroupId);

		questionGroupDetail.setWidget(1, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.name()));
		questionGroupDetail.setWidget(1, 1, name);
		questionGroupDetail.setWidget(2, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.description()));
		questionGroupDetail.setWidget(2, 1, description);
		questionGroupDetail.setWidget(3, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.order()));
		questionGroupDetail.setWidget(3, 1, order);
		questionGroupDetail.setWidget(4, 0, saveQuestionGroupButton);
		questionGroupDetail.setWidget(4, 1, deleteQuestionGroupButton);

		saveQuestionGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				try {
					if (validateQuestionGroup())
						saveQuestionGroup();
					else
						Window.alert(TEXT_CONSTANTS.cannotSaveQuestionGroup());
				} catch (Exception ex) {
					Window.alert(TEXT_CONSTANTS.noParentSurvey());
				}
			}

			private boolean validateQuestionGroup() {
				QuestionGroupDto qgDto = getQuestionGroupDto();
				if (qgDto.getCode() == null)
					return false;
				return true;
			}

		});

		deleteQuestionGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm(TEXT_CONSTANTS.deleteQuestionGroupWarning())) {
					final TreeItem selectedItem = surveyTree
							.getCurrentlySelectedItem();
					QuestionGroupDto dto = getQuestionGroupDto();
					if (dto != null && dto.getKeyId() != null) {
						svc.deleteQuestionGroup(dto, dto.getSurveyId(),
								new AsyncCallback<String>() {

									@Override
									public void onFailure(Throwable caught) {
										MessageDialog errDia = new MessageDialog(
												TEXT_CONSTANTS.error(),
												TEXT_CONSTANTS
														.errorTracePrefix()
														+ " "
														+ caught.getLocalizedMessage());
										errDia.showCentered();
									}

									@Override
									public void onSuccess(String result) {
										surveyTree.removeItem(selectedItem);
										questionGroupDetail.setVisible(false);
									}
								});
					}
				}

			}

		});

	}

	private QuestionGroupDto getQuestionGroupDto() {
		QuestionGroupDto qDto = new QuestionGroupDto();

		TextBox questionGroupId = (TextBox) questionGroupDetail.getWidget(0, 0);
		TextBox name = (TextBox) questionGroupDetail.getWidget(1, 1);
		TextBox desc = (TextBox) questionGroupDetail.getWidget(2, 1);
		ListBox order = (ListBox) questionGroupDetail.getWidget(3, 1);

		if (questionGroupId.getText().length() > 0) {
			qDto.setKeyId(new Long(questionGroupId.getText()));
			String path = ((QuestionGroupDto) currentSelection).getPath();
			qDto.setPath(path);
		} else {
			SurveyDto parentSDto = (SurveyDto) surveyTree
					.getCurrentlySelectedItem().getUserObject();
			SurveyGroupDto sGroup = (SurveyGroupDto) surveyTree
					.getParentUserObject(parentSDto);
			qDto.setPath(sGroup.getCode() + "/" + parentSDto.getName());
			qDto.setSurveyId(parentSDto.getKeyId());
		}
		if (name.getText().length() > 0)
			qDto.setCode(name.getText());
		if (desc.getText().length() > 0)
			qDto.setDescription(desc.getText());
		if (order.getSelectedIndex() != -1)
			qDto.setOrder(new Integer(order.getItemText(order
					.getSelectedIndex())));

		return qDto;
	}

	private void saveQuestionGroup() throws Exception {
		QuestionGroupDto dto = getQuestionGroupDto();
		Long parentId = null;
		if (currentSelection instanceof QuestionGroupDto) {
			// Get Survey Id
			parentId = ((QuestionGroupDto) currentSelection).getSurveyId();
			if (parentId == 0) {
				// TODO: HACK Not sure why this is 0 clearly there is a bug
				// elsewhere in the creation
				parentId = ((SurveyDto) surveyTree
						.getParentUserObject((QuestionGroupDto) currentSelection))
						.getKeyId();
			}
		} else {
			parentId = currentSelection.getKeyId();
		}
		// = currentSelection.getKeyId();

		final BaseDto treeParent = currentSelection;
		final boolean isNew;
		if (dto.getKeyId() != null) {
			isNew = false;
		} else {
			isNew = true;
		}

		svc.saveQuestionGroup(dto, parentId,
				new AsyncCallback<QuestionGroupDto>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(TEXT_CONSTANTS.errorTracePrefix() + " "
								+ caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(QuestionGroupDto result) {
						TextBox questionGroupId = (TextBox) questionGroupDetail
								.getWidget(0, 0);
						questionGroupId.setText(result.getKeyId().toString());
						if (isNew) {
							TreeItem item = surveyTree.addChild(treeParent,
									result);
							if (item != null) {
								surveyTree.setCurrentlySelectedItem(item);
								currentSelection = (BaseDto) item
										.getUserObject();
							}
						} else {
							// TODO: Problem the result is different from the
							// user object in the tree so I think we need to
							// find userobject by id and type?
							surveyTree.replaceUserObject(currentSelection,
									result);
							currentSelection = result;
							TreeItem item = surveyTree
									.getCurrentlySelectedItem();
							item.setText(result.getCode());
						}
						Window.alert(TEXT_CONSTANTS.saveComplete());
					}

				});
	}

	private void saveSurvey() throws Exception {
		SurveyDto dto = getSurveyDto();
		Long parentId = currentSelection.getKeyId();
		final BaseDto treeParent = currentSelection;
		final boolean isNew;
		if (dto.getKeyId() != null) {
			isNew = false;
		} else {
			isNew = true;
		}
		svc.saveSurvey(dto, parentId, new AsyncCallback<SurveyDto>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(TEXT_CONSTANTS.errorTracePrefix() + " "
						+ caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(SurveyDto result) {
				TextBox surveyId = (TextBox) surveyDetail.getWidget(0, 0);
				surveyId.setText(result.getKeyId().toString());
				if (isNew) {
					TreeItem item = surveyTree.addChild(treeParent, result);
					currentSelection = result;
					if (item != null) {
						surveyTree.setCurrentlySelectedItem(item);
						currentSelection = (BaseDto) item.getUserObject();
					}
				}
				Window.alert(TEXT_CONSTANTS.saveComplete());
			}

		});
	}

	private SurveyGroupDto getSurveyGroupDto() {
		SurveyGroupDto dto = new SurveyGroupDto();
		TextBox surveyGroupId = (TextBox) surveyGroupDetail.getWidget(0, 1);
		if (surveyGroupId.getText().length() > 0) {
			dto.setKeyId(new Long(surveyGroupId.getText()));
		}
		TextBox groupCode = (TextBox) surveyGroupDetail.getWidget(1, 1);
		if (groupCode.getText().length() > 0) {
			dto.setCode(groupCode.getText());
		}
		TextBox desc = (TextBox) surveyGroupDetail.getWidget(2, 1);
		if (desc.getText().length() > 0) {
			dto.setDescription(desc.getText());
		}
		return dto;
	}

	private SurveyDto getSurveyDto() {
		SurveyDto surveyDto = new SurveyDto();

		TextBox surveyId = (TextBox) surveyDetail.getWidget(0, 0);
		TextBox surveyname = (TextBox) surveyDetail.getWidget(1, 1);
		TextBox surveyDesc = (TextBox) surveyDetail.getWidget(2, 1);
		TextBox version = (TextBox) surveyDetail.getWidget(3, 1);
		if (surveyId.getText().length() > 0) {
			SurveyDto originalSurveyDto = (SurveyDto) surveyTree
					.getCurrentlySelectedItem().getUserObject();
			Long sKeyId = originalSurveyDto.getKeyId();
			surveyDto.setKeyId(sKeyId);
			Long sgKeyId = originalSurveyDto.getSurveyGroupId();
			surveyDto.setSurveyGroupId(sgKeyId);

			surveyDto.setSurveyGroupId(originalSurveyDto.getSurveyGroupId());
		} else {
			SurveyGroupDto parentSGDto = (SurveyGroupDto) surveyTree
					.getCurrentlySelectedItem().getUserObject();
			surveyDto.setPath(parentSGDto.getCode());
			surveyDto.setSurveyGroupId(parentSGDto.getKeyId());
		}
		if (surveyname.getText().length() > 0) {
			surveyDto.setName(surveyname.getText());
			surveyDto.setCode(surveyname.getText());
		}
		if (surveyDesc.getText().length() > 0)
			surveyDto.setDescription(surveyDesc.getText());
		if (version.getText().length() > 0)
			surveyDto.setVersion(version.getText());

		return surveyDto;
	}

	private void saveSurveyGroup() {
		SurveyGroupDto dto = getSurveyGroupDto();
		final BaseDto treeParent = currentSelection;
		final boolean isNew;
		if (dto.getKeyId() != null) {
			isNew = false;
		} else {
			isNew = true;
		}
		svc.saveSurveyGroup(dto, new AsyncCallback<SurveyGroupDto>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(TEXT_CONSTANTS.errorTracePrefix() + " "
						+ caught.getLocalizedMessage());

			}

			@Override
			public void onSuccess(SurveyGroupDto result) {
				if (isNew) {
					((TextBox) surveyGroupDetail.getWidget(0, 1))
							.setText(result.getKeyId().toString());
					TreeItem item = surveyTree.addChild(treeParent, result);
					if (item != null) {
						surveyTree.setCurrentlySelectedItem(item);
						currentSelection = (BaseDto) item.getUserObject();
					}
				}
				Window.alert(TEXT_CONSTANTS.saveComplete());
			}
		});
	}

	@Override
	public void onSurveyTreeSelection(BaseDto dto) {
		if (dto != null) {
			currentSelection = dto;
			if (dto instanceof SurveyGroupDto) {
				loadSurveyGroupDetail((SurveyGroupDto) dto);
			} else if (dto instanceof SurveyDto) {
				loadSurveyDetail((SurveyDto) dto);
			} else if (dto instanceof QuestionGroupDto) {
				loadQuestionGroupDetail((QuestionGroupDto) dto);
			} else if (dto instanceof QuestionDto) {
				loadQuestionDetails((QuestionDto) dto);
			}
		}
	}

	/**
	 * updates the translationMap in currentSelection to the new translation set
	 * so if the user clicks the edit button again without reloading the
	 * portlet, the updates actually show up
	 */
	@Override
	public void translationsUpdated(List<TranslationDto> translationList) {
		if (currentSelection instanceof QuestionDto) {
			QuestionDto question = (QuestionDto) currentSelection;
			if (translationList != null) {
				for (TranslationDto trans : translationList) {
					if ("QUESTION_TYPE".equals(trans.getParentType())) {
						question.addTranslation(trans);
					} else if ("QUESTION_OPTION".equals(trans.getParentType())) {
						// need to find the right option
						if (question.getOptionContainerDto() != null) {
							for (QuestionOptionDto opt : question
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
	}
}
