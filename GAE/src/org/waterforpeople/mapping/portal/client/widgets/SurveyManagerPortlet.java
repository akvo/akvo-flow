package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDependencyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.view.SurveyTree;
import org.waterforpeople.mapping.app.gwt.client.survey.view.SurveyTreeListener;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SurveyManagerPortlet extends Portlet implements ClickHandler,
		SurveyTreeListener {

	public static final String NAME = "Survey Manager Portlet";
	public static final String DESCRIPTION = "Manages Create/Edit/Delete of Surveys";
	private static final int MAX_Q_LENGTH = 50;
	private static String title = "";
	private static Boolean scrollable = true;
	private static Boolean configurable = false;
	private static final int HEIGHT = 800;
	private static final int WIDTH = 1080;

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

	private enum ButtonState {
		SURVEYGROUP, SURVEY, QUESTIONGROUP, QUESTION, NONE
	};

	public SurveyManagerPortlet() {
		super(title, scrollable, configurable, WIDTH, HEIGHT);
		svc = GWT.create(SurveyService.class);
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
	}

	/**
	 * toggles visibility of the various "add" buttons based on the state passed
	 * in
	 * 
	 * @param state
	 */
	private void setButtonState(ButtonState state) {
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

	/**
	 * constructs all the buttons and sets their initial visibility
	 */
	private void configureButtonPanel() {
		addSurveyGroupButton = constructAndInstallButton("Add Survey Group",
				true);
		deleteSurveyGroupButton = constructAndInstallButton(
				"Delete Survey Group", false);

		addSurveyButton = constructAndInstallButton("Add Survey", true);
		deleteSurveyButton = constructAndInstallButton("Delete Survey", false);

		addQuestionGroupButton = constructAndInstallButton(
				"Add Question Group", true);
		deleteQuestionGroupButton = constructAndInstallButton(
				"Delete Question Group", false);

		addQuestionButton = constructAndInstallButton("Add Question", true);
		deleteQuestionButton = constructAndInstallButton("Delete Question",
				false);
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
			Window.alert("Clicked Delete Survey Group");
		} else if (event.getSource() == addSurveyButton) {
			loadSurveyDetail(null);
		} else if (event.getSource() == deleteSurveyButton) {

		} else if (event.getSource() == addQuestionGroupButton) {
			loadQuestionGroupDetail(null);
		} else if (event.getSource() == deleteQuestionGroupButton) {

		} else if (event.getSource() == addQuestionButton) {
			loadQuestionDetails(null);
		} else if (event.getSource() == deleteQuestionButton) {

		}
	}

	private void loadQuestionDetails(QuestionDto item) {
		setButtonState(ButtonState.NONE);
		questionOptionDetail.removeAllRows();
		questionDetailPanel.removeAllRows();
		treeContainer.remove(detailContainer);
		detailContainer = new VerticalPanel();
		treeContainer.add(detailContainer);
		TextBox questionId = new TextBox();
		questionId.setVisible(false);
		TextArea questionText = new TextArea();
		questionText.setSize("35em", "5em");
		// questionText.setWidth("40em");
		TextArea tip = new TextArea();
		tip.setSize("35em", "5em");
		TextBox validationRule = new TextBox();
		CheckBox mandatoryQuestion = new CheckBox();
		CheckBox dependentQuestion = new CheckBox();

		if (item != null) {
			questionId.setText(item.getKeyId().toString());
			if (item.getText() != null)
				questionText.setText(item.getText());
			if (item.getTip() != null)
				tip.setText(item.getTip());
			if (item.getValidationRule() != null)
				validationRule.setText(item.getValidationRule());
			if (item.getMandatoryFlag() != null)
				if (item.getMandatoryFlag())
					mandatoryQuestion.setValue(item.getMandatoryFlag());
				else
					mandatoryQuestion.setValue(item.getMandatoryFlag());
		}
		ListBox questionTypeLB = new ListBox();
		// FREE_TEXT, OPTION, NUMBER, GEO, PICTURE, VIDEO
		questionTypeLB.addItem("Free Text");
		questionTypeLB.addItem("Option");
		questionTypeLB.addItem("Number");
		questionTypeLB.addItem("Geo");
		questionTypeLB.addItem("Photo");
		questionTypeLB.addItem("Video");
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
			}
		}

		questionTypeLB.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				if (((ListBox) event.getSource()).getSelectedIndex() == 1) {
					loadQuestionOptionDetail(null);
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

		Button saveQuestionButton = new Button("Save Question");
		Button deleteQuestionButton = new Button("Delete Question");
		questionId.setVisible(false);

		questionDetailPanel.setWidget(0, 0, questionId);
		questionDetailPanel.setWidget(1, 0, new Label("Question Text"));
		questionDetailPanel.setWidget(1, 1, questionText);
		questionDetailPanel.setWidget(2, 0, new Label("Question Type"));
		questionDetailPanel.setWidget(2, 1, questionTypeLB);
		questionDetailPanel.setWidget(3, 0, new Label("Question Tool Tip"));
		questionDetailPanel.setWidget(3, 1, tip);
		questionDetailPanel.setWidget(4, 0, new Label("Validation Rule"));
		questionDetailPanel.setWidget(4, 1, validationRule);
		questionDetailPanel.setWidget(5, 0, new Label("Mandatory Question"));
		questionDetailPanel.setWidget(5, 1, mandatoryQuestion);
		questionDetailPanel.setWidget(7, 0, new Label(
				"Question Dependant On Other Question"));
		questionDetailPanel.setWidget(7, 1, dependentQuestion);

		if (item != null && item.getQuestionDependency() != null) {
			dependentQuestion.setValue(true);
			loadDependencyTable(true);
		}
		saveQuestionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				try {
					saveQuestion();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Window
							.alert("Could not save question no Question Group was selected");
				}
			}

		});
		deleteQuestionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				QuestionDto value = new QuestionDto();
				TextBox questionId = (TextBox) questionDetailPanel.getWidget(0,
						0);
				value.setKeyId(new Long(questionId.getText()));
				deleteQuestion(value, 1L);
			}

		});

		detailContainer.add(questionDetailPanel);
		if (questionOptionDetail != null)
			detailContainer.add(questionOptionDetail);

		buttonHPanel = null;
		buttonHPanel = new HorizontalPanel();

		buttonHPanel.add(saveQuestionButton);
		buttonHPanel.add(deleteQuestionButton);

		detailContainer.add(buttonHPanel);
		// this.removeAllWidgetsLoadThisWidget(questionDetailPanel);

	}

	private HorizontalPanel buttonHPanel = new HorizontalPanel();

	private void loadDependencyTable(Boolean dependentValue) {

		if (dependentValue) {
			QuestionDto qDto = null;
			questionDetailPanel.setWidget(8, 0, new Label(
					"Dependent on Quesiton"));
			ListBox questionLB = new ListBox();
			ListBox answerLB = new ListBox();
			final QuestionGroupDto questionGroup = (QuestionGroupDto) surveyTree
					.getParentUserObject(currentSelection);

			QuestionDependencyDto item = null;
			if (currentSelection instanceof QuestionDto) {
				qDto = (QuestionDto) currentSelection;
				if (qDto != null && qDto.getQuestionDependency() != null)
					item = qDto.getQuestionDependency();
			}
			int selectedIdx = -1;
			if (questionGroup != null) {
				int idx = 0;
				for (Entry<Integer, QuestionDto> entry : questionGroup
						.getQuestionMap().entrySet()) {
					if (QuestionType.OPTION.equals(entry.getValue().getType())) {
						String txt = entry.getValue().getText();
						if (txt != null && txt.trim().length() > MAX_Q_LENGTH) {
							txt = txt.substring(0, MAX_Q_LENGTH);
						}
						questionLB.addItem(txt, entry.getValue().getKeyId()
								.toString());
						if (item != null
								&& item.getQuestionId() == entry.getValue()
										.getKeyId()) {
							selectedIdx = idx;
						}
						idx++;
					}
				}
				if (selectedIdx >= 0) {
					questionLB.setSelectedIndex(selectedIdx);
				}
				questionDetailPanel.setWidget(8, 1, questionLB);
				TextBox dependentQId = new TextBox();

				if (item != null && item.getKeyId() != null)
					dependentQId.setText(item.getKeyId().toString());

				dependentQId.setVisible(false);
				questionDetailPanel.setWidget(8, 2, dependentQId);
				answerLB.setVisible(false);
				questionDetailPanel.setWidget(8, 3, answerLB);

				questionLB.addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {
						ListBox questionLBox = (ListBox) event.getSource();
						loadDepQA(questionLBox, questionGroup);

					}
				});
				TextBox qDepId = new TextBox();
				questionDetailPanel.setWidget(8, 4, qDepId);
				if (qDto != null && qDto.getQuestionDependency() != null) {
					// set existing value
					qDepId.setText(qDto.getQuestionDependency().getQuestionId()
							.toString());
					loadDepQA(questionLB, questionGroup);
					Boolean foundAnswer = false;
					for (int i = 0; i < answerLB.getItemCount(); i++) {
						if (answerLB.getValue(i).equals(
								qDto.getQuestionDependency().getAnswerValue())) {
							answerLB.setSelectedIndex(i);
							foundAnswer = true;
						}
						if (foundAnswer)
							break;
					}
				}
			}
		} else {
			questionDetailPanel.removeRow(8);
		}
	}

	private void loadDepQA(ListBox questionLBox, QuestionGroupDto questionGroup) {
		Integer selectedIndex = questionLBox.getSelectedIndex();
		String value = questionLBox.getValue(selectedIndex);
		if (questionGroup != null) {
			for (QuestionDto qDto : questionGroup.getQuestionMap().values()) {

				if (qDto.getKeyId().toString().equals(value)) {
					if (qDto.getOptionContainerDto() != null) {
						updateDependencyAnswerSelection(qDto
								.getOptionContainerDto().getOptionsList());
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
													.getOptionsList());
										}
									}

									@Override
									public void onFailure(Throwable caught) {
										Window.alert("Could not load answers");

									}
								});
					}
					break;
				}
			}
		}
	}

	private void updateDependencyAnswerSelection(List<QuestionOptionDto> qoList) {
		ListBox answerLB = (ListBox) questionDetailPanel.getWidget(8, 3);
		answerLB.clear();
		if (qoList != null) {
			for (QuestionOptionDto qoDto : qoList) {
				answerLB.addItem(qoDto.getText(), qoDto.getCode());
			}
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
				if (ocDto.getKeyId() != null)
					ocId.setText(ocDto.getKeyId().toString());
				if (ocDto.getAllowOtherFlag() != null)
					allowOther.setValue(ocDto.getAllowOtherFlag());
				if (ocDto.getOptionsList() != null)
					questionOptionList = ocDto.getOptionsList();
			}
		}

		questionOptionDetail.setWidget(row, 0, new Label("Allow Other"));
		questionOptionDetail.setWidget(row, 1, allowOther);
		questionOptionDetail.setWidget(row, 2, new Label("Allow Multiple"));
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

		Button addNewOptionButton = new Button("Add New Option");
		// Button deleteOptionButton = new Button("Delete Option");

		questionOptionDetail.setWidget(row, 2, addNewOptionButton);
		addNewOptionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				loadQuestionOptionRowDetail(null, questionOptionDetail
						.getRowCount());
			}

		});
	}

	private void loadQuestionOptionRowDetail(QuestionOptionDto item, Integer row) {

		TextBox optionValue = new TextBox();
		optionValue.setWidth("3em");
		TextBox optionText = new TextBox();
		optionText.setWidth("30em");
		TextBox optionId = new TextBox();
		// optionId.setVisible(true);
		if (item != null) {
			if (item.getKeyId() != null)
				optionId.setText(item.getKeyId().toString());
			if (item.getCode() != null)
				optionValue.setText(item.getCode());
			if (item.getText() != null)
				optionText.setText(item.getText());
		}
		if (row >= 2)
			row = row - 1;

		questionOptionDetail.insertRow(row);

		questionOptionDetail.setWidget(row, 0, new Label("Option Value"));
		questionOptionDetail.setWidget(row, 1, optionValue);
		questionOptionDetail.setWidget(row, 2, new Label("Option Text"));
		questionOptionDetail.setWidget(row, 3, optionText);
		questionOptionDetail.setWidget(row, 4, optionId);

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
		}

		svc.saveQuestion(dto, parentId, new AsyncCallback<QuestionDto>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not save question");
			}

			@Override
			public void onSuccess(QuestionDto result) {
				if (isNew) {
					surveyTree.addChild(treeParent, result);
				}else{
					surveyTree.replaceUserObject(currentSelection,result);
					currentSelection = result;
				}
				if (result.getQuestionDependency() != null)
					((TextBox) questionDetailPanel.getWidget(8, 4))
							.setText(result.getQuestionDependency()
									.getQuestionId().toString());

				Window.alert("Question Saved");
			}

		});

	}

	private QuestionDto getQuestionDto() {
		QuestionDto value = new QuestionDto();

		TextBox questionId = (TextBox) questionDetailPanel.getWidget(0, 0);
		// TextBox dependencyId = (TextBox) questionDetailPanel.getWidget(0, 0);
		TextArea questionText = (TextArea) questionDetailPanel.getWidget(1, 1);
		ListBox questionTypeLB = (ListBox) questionDetailPanel.getWidget(2, 1);

		TextArea tip = (TextArea) questionDetailPanel.getWidget(3, 1);
		TextBox validationRule = (TextBox) questionDetailPanel.getWidget(4, 1);
		CheckBox mandatoryQuestion = (CheckBox) questionDetailPanel.getWidget(
				5, 1);

		if (questionId.getText().length() > 0)
			value.setKeyId(new Long(questionId.getText()));

		if (questionText.getText().length() > 0)
			value.setText(questionText.getText().trim());

		if (tip.getText().length() > 0)
			value.setTip(tip.getText());
		if (validationRule.getText().length() > 0)
			value.setValidationRule(validationRule.getText());

		value.setMandatoryFlag(mandatoryQuestion.getValue());

		if (questionTypeLB.getSelectedIndex() == 0) {
			value.setType(QuestionType.FREE_TEXT);
		} else if (questionTypeLB.getSelectedIndex() == 1) {
			value.setType(QuestionType.OPTION);
			FlexTable questionOptionTable = (FlexTable) detailContainer
					.getWidget(1);

			CheckBox allowOther = (CheckBox) questionOptionDetail.getWidget(0,
					1);
			CheckBox allowMultiple = (CheckBox) questionOptionDetail.getWidget(
					0, 3);

			TextBox ocId = (TextBox) questionOptionDetail.getWidget(0, 4);

			OptionContainerDto ocDto = new OptionContainerDto();
			if (ocId.getText().length() > 0)
				ocDto.setKeyId(new Long(ocId.getText()));
			ocDto.setAllowMultipleFlag(allowMultiple.getValue());
			ocDto.setAllowOtherFlag(allowOther.getValue());

			for (int row = 1; row < questionOptionTable.getRowCount() - 1; row++) {
				QuestionOptionDto qoDto = new QuestionOptionDto();
				TextBox optionValue = (TextBox) questionOptionDetail.getWidget(
						row, 1);
				TextBox optionText = (TextBox) questionOptionDetail.getWidget(
						row, 3);
				TextBox qoId = (TextBox) questionOptionDetail.getWidget(row, 4);
				qoDto.setCode(optionValue.getText());
				qoDto.setText(optionText.getText());
				if (qoId.getText().length() > 0)
					qoDto.setKeyId(new Long(qoId.getText()));
				qoDto.setOrder(row);
				ocDto.addQuestionOption(qoDto);
			}
			value.setOptionContainerDto(ocDto);

		} else if (questionTypeLB.getSelectedIndex() == 2) {
			value.setType(QuestionType.NUMBER);
		} else if (questionTypeLB.getSelectedIndex() == 3) {
			value.setType(QuestionType.GEO);
		} else if (questionTypeLB.getSelectedIndex() == 4) {
			value.setType(QuestionType.PHOTO);
		} else if (questionTypeLB.getSelectedIndex() == 5) {
			value.setType(QuestionType.VIDEO);
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
		}

		return value;
	}

	private void deleteQuestion(QuestionDto value, Long questionGroupId) {
		svc.deleteQuestion(value, questionGroupId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not delete question");

			}

			@Override
			public void onSuccess(Void result) {
				Window.alert("Question Deleted");
				questionDetailPanel.setVisible(false);
				// TODO implement remove from tree
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
		Button saveSurveyGroupButton = new Button("Save Survey Group");
		Button deleteSurveyGroupButton = new Button("Delete Survey Group");

		surveyGroupDetail.setWidget(0, 1, surveyGroupId);
		surveyGroupDetail.setWidget(1, 0, new Label("Survey Code"));
		surveyGroupDetail.setWidget(1, 1, surveyGroupCode);
		surveyGroupDetail.setWidget(2, 0, new Label("Survey Description"));
		surveyGroupDetail.setWidget(2, 1, surveyGroupDesc);
		surveyGroupDetail.setWidget(3, 0, saveSurveyGroupButton);
		surveyGroupDetail.setWidget(3, 1, deleteSurveyGroupButton);
		saveSurveyGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				saveSurveyGroup();
			}

		});
		deleteSurveyGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub

			}

		});
		removeAllWidgetsLoadThisWidget(surveyGroupDetail);
	}

	private void loadSurveyDetail(SurveyDto item) {
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

		Button saveSurveyButton = new Button("Save");
		Button deleteSurveyButton = new Button("Delete");
		Button publishSurveyButton = new Button("Publish");

		surveyDetail.setWidget(0, 0, surveyId);
		surveyDetail.setWidget(1, 0, new Label("Survey Name"));
		surveyDetail.setWidget(1, 1, surveyname);
		surveyDetail.setWidget(2, 0, new Label("Description"));
		surveyDetail.setWidget(2, 1, surveyDesc);
		surveyDetail.setWidget(3, 0, new Label("Version"));
		surveyDetail.setWidget(3, 1, version);
		surveyDetail.setWidget(4, 0, saveSurveyButton);
		surveyDetail.setWidget(4, 1, deleteSurveyButton);
		surveyDetail.setWidget(4, 2, publishSurveyButton);
		removeAllWidgetsLoadThisWidget(surveyDetail);

		saveSurveyButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				try {
					saveSurvey();
				} catch (Exception e) {
					Window
							.alert("Could not save survey no survey group selected");
					e.printStackTrace();
				}
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
							Window.alert("Could not publish survey");
						}

						@Override
						public void onSuccess(Void result) {
							Window.alert("Survey published");

						}
					});
					/*
					 * svc.publishSurvey(surveyId, new AsyncCallback<String>() {
					 * 
					 * @Override public void onFailure(Throwable caught) { //
					 * TODO Auto-generated method stub
					 * 
					 * }
					 * 
					 * @Override public void onSuccess(String result) {
					 * Window.alert(result);
					 * 
					 * }
					 * 
					 * });
					 */
				} else {
					Window.alert("Please save survey before publishing");
				}
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

		Button saveQuestionGroupButton = new Button("Save Question Group");
		Button deleteQuestionGroupButton = new Button("Delete Question Group");
		questionGroupDetail.setWidget(0, 0, questionGroupId);
		questionGroupDetail.setWidget(1, 0, new Label("Name"));
		questionGroupDetail.setWidget(1, 1, name);
		questionGroupDetail.setWidget(2, 0, new Label("Description"));
		questionGroupDetail.setWidget(2, 1, description);
		questionGroupDetail.setWidget(4, 0, saveQuestionGroupButton);
		questionGroupDetail.setWidget(4, 1, deleteQuestionGroupButton);

		saveQuestionGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				try {
					saveQuestionGroup();
				} catch (Exception ex) {
					Window
							.alert("Cannot Save Question Group Because no parent survey is selected");
				}
			}

		});

		deleteQuestionGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Window
						.confirm("This will remove all question associatons with this Question Group, but will not delete the questions. Not yet implemented");

			}

		});
	}

	private QuestionGroupDto getQuestionGroupDto() {
		QuestionGroupDto qDto = new QuestionGroupDto();

		TextBox questionGroupId = (TextBox) questionGroupDetail.getWidget(0, 0);
		TextBox name = (TextBox) questionGroupDetail.getWidget(1, 1);
		TextBox desc = (TextBox) questionGroupDetail.getWidget(2, 1);

		if (questionGroupId.getText().length() > 0)
			qDto.setKeyId(new Long(questionGroupId.getText()));
		if (name.getText().length() > 0)
			qDto.setCode(name.getText());
		if (desc.getText().length() > 0)
			qDto.setDescription(desc.getText());
		return qDto;
	}

	private void saveQuestionGroup() throws Exception {
		QuestionGroupDto dto = getQuestionGroupDto();
		Long parentId = currentSelection.getKeyId();
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
						Window.alert("Could not save question group");
					}

					@Override
					public void onSuccess(QuestionGroupDto result) {
						TextBox questionGroupId = (TextBox) questionGroupDetail
								.getWidget(0, 0);
						questionGroupId.setText(result.getKeyId().toString());
						if (isNew) {
							surveyTree.addChild(treeParent, result);
						}
						Window.alert("Saved Question Group");
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
				Window.alert("Could not save survey");
			}

			@Override
			public void onSuccess(SurveyDto result) {
				TextBox surveyId = (TextBox) surveyDetail.getWidget(0, 0);
				surveyId.setText(result.getKeyId().toString());
				if (isNew) {
					surveyTree.addChild(treeParent, result);
				}
				Window.alert("Saved Survey");
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
		if (surveyId.getText().length() > 0)
			surveyDto.setKeyId(new Long(surveyId.getText()));
		if (surveyname.getText().length() > 0)
			surveyDto.setName(surveyname.getText());
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
				Window.alert("Could not save survey group");

			}

			@Override
			public void onSuccess(SurveyGroupDto result) {
				if (isNew) {
					surveyTree.addChild(treeParent, result);
				}
				Window.alert("Survey Group Saved");
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
}
