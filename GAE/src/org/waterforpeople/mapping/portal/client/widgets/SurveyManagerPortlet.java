package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.List;

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

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SurveyManagerPortlet extends Portlet {

	private Tree surveyTree = null;
	private SurveyServiceAsync svc = null;
	public static final String NAME = "Survey Manager Portlet";
	public static final String DESCRIPTION = "Manages Create/Edit/Delete of Surveys";
	private static String title = "";
	private static Boolean scrollable = true;
	private static Boolean configurable = false;
	private static final int HEIGHT = 800;
	private static final int WIDTH = 1080;

	private Button addSurveyGroupButton = new Button("Add Survey Group");
	private Button addSurveyButton = new Button("Add Survey");
	private Button addQuestionGroupButton = new Button("Add Question Group");
	private Button addQuestionButton = new Button("Add Question");
	private Button deleteSurveyGroupButton = new Button("Delete Survey Group");
	private Button deleteSurveyButton = new Button("Delete Survey");
	private Button deleteQuestionGroupButton = new Button(
			"Delete Question Group");
	private Button deleteQuestionButton = new Button("Delete Question");
	HorizontalPanel treeContainer = new HorizontalPanel();
	FlexTable questionDetailPanel = new FlexTable();

	HorizontalPanel buttonPanel = new HorizontalPanel();

	public SurveyManagerPortlet(String title, boolean scrollable,
			boolean configurable, int width, int height) {
		super(title, scrollable, configurable, WIDTH, HEIGHT);
		svc = GWT.create(SurveyService.class);
	}

	public SurveyManagerPortlet() {
		super(title, scrollable, configurable, WIDTH, HEIGHT);
		svc = GWT.create(SurveyService.class);
		buildContentPanel();
	}

	@Override
	public String getName() {
		return NAME;
	}

	private VerticalPanel contentPane = null;

	private void buildContentPanel() {
		contentPane = new VerticalPanel();
		setContent(contentPane);

		loadTree();
		treeContainer.add(surveyTree);
		treeContainer.add(detailContainer);
		contentPane.add(buttonPanel);
		contentPane.add(treeContainer);
		configureButtonPanel();

	}

	private VerticalPanel detailContainer = new VerticalPanel();

	private TreeItem selectedItem = null;

	private void removeAllWidgetsLoadThisWidget(Widget w) {
		for (int i = 0; i < detailContainer.getWidgetCount(); i++) {
			detailContainer.remove(i);
		}
		detailContainer.add(w);
	}

	private void loadTree() {
		addSurveyGroupButton.setVisible(true);
		addSurveyButton.setVisible(false);
		addQuestionGroupButton.setVisible(false);
		addQuestionButton.setVisible(false);
		surveyTree = new Tree();
		try {
			svc.listSurveyGroups("all", false, false, false,
					new AsyncCallback<ArrayList<SurveyGroupDto>>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(ArrayList<SurveyGroupDto> result) {
							TreeItem outerRoot = new TreeItem();
							outerRoot.setText("Survey Groups");
							surveyTree.addItem(outerRoot);
							for (SurveyGroupDto item : result) {
								bindSurveyGroup(item);
							}

							surveyTree
									.addSelectionHandler(new SelectionHandler<TreeItem>() {

										@Override
										public void onSelection(
												SelectionEvent<TreeItem> event) {
											TreeItem item = event
													.getSelectedItem();
											if (item.getParentItem() == null) {
												loadTree();
												addSurveyGroupButton
														.setVisible(true);
												addSurveyButton
														.setVisible(false);
												addQuestionGroupButton
														.setVisible(false);
												addQuestionButton
														.setVisible(false);

											} else if (item
													.getUserObject()
													.getClass()
													.getName()
													.equals(
															"org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto")) {
												SurveyGroupDto sg = (SurveyGroupDto) item
														.getUserObject();
												loadSurveyGroupDetail(sg);
												addSurveyGroupButton
														.setVisible(false);
												addSurveyButton
														.setVisible(true);
												addQuestionGroupButton
														.setVisible(false);
												addQuestionButton
														.setVisible(false);

												svc
														.listSurveysByGroup(
																sg
																		.getKeyId()
																		.toString(),
																new AsyncCallback<ArrayList<SurveyDto>>() {

																	@Override
																	public void onFailure(
																			Throwable caught) {
																		// TODO
																		// Auto-
																		// generated
																		// method
																		// stub

																	}

																	@Override
																	public void onSuccess(
																			ArrayList<SurveyDto> result) {
																		for (SurveyDto surveyDto : result) {
																			bindSurvey(surveyDto);
																		}

																	}

																});
											} else if (item
													.getUserObject()
													.getClass()
													.getName()
													.equals(
															"org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto")) {
												SurveyDto s = (SurveyDto) item
														.getUserObject();
												addSurveyGroupButton
														.setVisible(false);
												addSurveyButton
														.setVisible(false);
												addQuestionGroupButton
														.setVisible(true);
												addQuestionButton
														.setVisible(false);
												loadSurveyDetail(s);

												String surveyId = s.getKeyId()
														.toString();
												svc
														.listQuestionGroupsBySurvey(
																surveyId,
																new AsyncCallback<ArrayList<QuestionGroupDto>>() {

																	@Override
																	public void onFailure(
																			Throwable caught) {
																		// TODO
																		// Auto-
																		// generated
																		// method
																		// stub

																	}

																	@Override
																	public void onSuccess(
																			ArrayList<QuestionGroupDto> result) {
																		for (QuestionGroupDto qgDto : result) {
																			bindQuestionGroup(qgDto);
																		}

																	}

																});
											} else if (item
													.getUserObject()
													.getClass()
													.getName()
													.equals(
															"org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto")) {
												QuestionGroupDto qgDto = (QuestionGroupDto) item
														.getUserObject();
												addSurveyGroupButton
														.setVisible(false);
												addSurveyButton
														.setVisible(false);
												addQuestionGroupButton
														.setVisible(false);
												addQuestionButton
														.setVisible(true);
												loadQuestionGroupDetail(qgDto);

												svc
														.listQuestionsByQuestionGroup(
																qgDto
																		.getKeyId()
																		.toString(),
																false,
																new AsyncCallback<ArrayList<QuestionDto>>() {

																	@Override
																	public void onFailure(
																			Throwable caught) {
																		// TODO
																		// Auto-
																		// generated
																		// method
																		// stub

																	}

																	@Override
																	public void onSuccess(
																			ArrayList<QuestionDto> result) {
																		for (QuestionDto qDto : result) {
																			bindQuestion(qDto);
																		}
																	}

																});

											} else if (item
													.getUserObject()
													.getClass()
													.getName()
													.equals(
															"org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto")) {
												QuestionDto questionDto = (QuestionDto) item
														.getUserObject();
												addSurveyGroupButton
														.setVisible(false);
												addSurveyButton
														.setVisible(false);
												addQuestionGroupButton
														.setVisible(false);
												addQuestionButton
														.setVisible(false);
												svc
														.loadQuestionDetails(
																questionDto
																		.getKeyId(),
																new AsyncCallback<QuestionDto>() {

																	@Override
																	public void onFailure(
																			Throwable caught) {
																		// no-op

																	}

																	@Override
																	public void onSuccess(
																			QuestionDto result) {
																		loadQuestionDetails(result);

																	}
																});

											}

										}

									});
						}

					});
		} catch (NullPointerException ex) {
			System.out.print(ex);
		}

	}

	private void bindSurveyGroup(SurveyGroupDto item) {

		Boolean foundSurveyGroupFlag = false;
		TreeItem rootItem = surveyTree.getItem(0);
		for (int i = 0; i < rootItem.getChildCount(); i++) {
			SurveyGroupDto sgDto = (SurveyGroupDto) rootItem.getChild(i)
					.getUserObject();
			if (sgDto.getKeyId().equals(item.getKeyId())) {
				rootItem.getChild(i).setUserObject(item);
				rootItem.getChild(i).setText(item.getCode());
				break;
			}
		}
		if (!foundSurveyGroupFlag) {
			TreeItem surveyGroupItem = new TreeItem();
			surveyGroupItem.setUserObject(item);
			surveyGroupItem.setText(item.getCode());
			TreeItem parentItem = surveyTree.getItem(0);
			parentItem.addItem(surveyGroupItem);
		}
	}

	private void bindSurvey(SurveyDto item) {
		TreeItem parentItem = surveyTree.getSelectedItem();
		if (parentItem != null) {
			Boolean foundSurveyFlag = false;
			for (int i = 0; i < parentItem.getChildCount(); i++) {
				SurveyDto surveyDto = (SurveyDto) parentItem.getChild(i)
						.getUserObject();
				if (surveyDto.getKeyId().equals(item.getKeyId())) {
					parentItem.getChild(i).setUserObject(item);
					parentItem.getChild(i).setText(item.getName());
					foundSurveyFlag = true;
					break;

				}
			}
			if (!foundSurveyFlag) {
				TreeItem surveyItem = new TreeItem();
				surveyItem.setText(item.getName());
				surveyItem.setUserObject(item);
				parentItem.addItem(surveyItem);
			}
		}
	}

	private void bindQuestionGroup(QuestionGroupDto item) {
		Boolean foundQuestionGroupFlag = false;

		TreeItem parentItem = surveyTree.getSelectedItem();
		TreeItem questionGroupItem = new TreeItem();
		for (int i = 0; i < parentItem.getChildCount(); i++) {
			QuestionGroupDto qgDto = (QuestionGroupDto) parentItem.getChild(i)
					.getUserObject();
			if (qgDto.getKeyId().equals(item.getKeyId())) {
				questionGroupItem = parentItem.getChild(i);
				questionGroupItem.setText(item.getCode());
				questionGroupItem.setUserObject(item);
				foundQuestionGroupFlag = true;
				break;
			}
		}
		if (!foundQuestionGroupFlag) {
			questionGroupItem.setText(item.getCode());
			questionGroupItem.setUserObject(item);
			parentItem.addItem(questionGroupItem);
		}
	}

	private void bindQuestion(QuestionDto item) {
		TreeItem parentItem = surveyTree.getSelectedItem();
		TreeItem questionItem = new TreeItem();
		Boolean foundQuestionFlag = false;
		Integer count = parentItem.getChildCount() + 1;

		for (int i = 0; i < parentItem.getChildCount(); i++) {
			QuestionDto qDto = (QuestionDto) parentItem.getChild(i)
					.getUserObject();
			if (qDto.getKeyId().equals(item.getKeyId())) {
				questionItem = parentItem.getChild(i);
				if (item.getText().trim().length() > 15)
					questionItem.setText(count + ":"
							+ item.getText().substring(0, 15));
				else
					questionItem.setText(item.getText().trim());
				questionItem.setUserObject(count + ":" + item);
				foundQuestionFlag = true;
				break;
			}
		}

		if (!foundQuestionFlag) {

			if (item.getText().trim().length() > 15)
				questionItem.setText(count + ":"
						+ item.getText().substring(0, 15));
			else
				questionItem.setText(count + ":" + item.getText().trim());
			questionItem.setUserObject(item);
			parentItem.addItem(questionItem);
		}
	}

	private void configureButtonPanel() {
		buttonPanel.add(addSurveyGroupButton);
		buttonPanel.add(deleteSurveyGroupButton);
		deleteSurveyGroupButton.setVisible(false);
		buttonPanel.add(addSurveyButton);
		buttonPanel.add(deleteSurveyButton);
		deleteSurveyButton.setVisible(false);
		buttonPanel.add(addQuestionGroupButton);
		buttonPanel.add(deleteQuestionGroupButton);
		deleteQuestionGroupButton.setVisible(false);
		buttonPanel.add(addQuestionButton);
		buttonPanel.add(deleteQuestionButton);
		deleteQuestionButton.setVisible(false);
		configureButtonHandlers();
	}

	private void configureButtonHandlers() {
		addSurveyGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				loadSurveyGroupDetail(null);
			}

		});

		deleteSurveyGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Window.alert("Clicked Delete Survey Group");
			}

		});

		addSurveyButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				loadSurveyDetail(null);

			}

		});

		deleteSurveyButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub

			}

		});

		addQuestionGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				loadQuestionGroupDetail(null);

			}

		});

		deleteQuestionGroupButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

			}

		});

		addQuestionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				loadQuestionDetails(null);
			}

		});

		deleteQuestionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub

			}

		});
	}

	private FlexTable questionOptionDetail = new FlexTable();

	private void loadQuestionDetails(QuestionDto item) {

		questionOptionDetail.removeAllRows();
		questionDetailPanel.removeAllRows();

		TextBox questionId = new TextBox();
		questionId.setVisible(false);
		TextBox questionText = new TextBox();
		TextBox tip = new TextBox();
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
				questionDetailPanel.setWidget(6, 2, questionOptionDetail);
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
					questionDetailPanel.setWidget(6, 2, questionOptionDetail);
					questionDetailPanel.getWidget(6, 2).setVisible(true);
				} else {
					if (questionDetailPanel.getCellCount(6) >= 2)
						questionDetailPanel.getWidget(6, 2).setVisible(false);

					// questionDetailPanel.removeRow(6);
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
		questionDetailPanel.setWidget(9, 0, saveQuestionButton);
		questionDetailPanel.setWidget(9, 1, deleteQuestionButton);
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
		if (item != null && item.getQuestionDependency() != null) {
			loadDependencyTable(true);
		}
		this.removeAllWidgetsLoadThisWidget(questionDetailPanel);

	}

	private void loadDependencyTable(Boolean dependentValue) {

		if (dependentValue) {
			QuestionDto qDto = null;
			questionDetailPanel.setWidget(8, 0, new Label(
					"Dependent on Quesiton"));
			ListBox questionLB = new ListBox();
			ListBox answerLB = new ListBox();
			TreeItem questionGroup = surveyTree.getSelectedItem();
			QuestionDependencyDto item = null;
			if (questionGroup != null
					&& questionGroup
							.getUserObject()
							.getClass()
							.getName()
							.equals(
									"org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto")) {
				qDto = (QuestionDto) questionGroup.getUserObject();
				if (qDto != null && qDto.getQuestionDependency() != null)
					item = qDto.getQuestionDependency();

				questionGroup = questionGroup.getParentItem();
			}
			if (questionGroup != null
					&& questionGroup
							.getUserObject()
							.getClass()
							.getName()
							.equals(
									"org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto")) {
				for (int i = 0; i < questionGroup.getChildCount(); i++) {
					TextBox questId = (TextBox) questionDetailPanel.getWidget(
							0, 0);

					TreeItem questionItem = questionGroup.getChild(i);
					if (!((QuestionDto) questionItem.getUserObject())
							.getKeyId().toString().equals(questId.getText())
							&& ((QuestionDto) questionItem.getUserObject())
									.getType().equals(QuestionType.OPTION)) {
						String question = ((QuestionDto) questionItem
								.getUserObject()).getText();
						String id = ((QuestionDto) questionItem.getUserObject())
								.getKeyId().toString();
						questionLB.addItem(question, id);
						String questDepId = null;
						if (item != null)
							questDepId = item.getQuestionId().toString();
						if (questDepId != null && questDepId.equals(id)) {
							questionLB.setSelectedIndex(i);
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

				questionLB.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						ListBox questionLBox = (ListBox) event.getSource();
						loadDepQA(questionLBox);
					}
				});
				TextBox qDepId = new TextBox();
				questionDetailPanel.setWidget(8, 4, qDepId);
				if (qDto != null && qDto.getQuestionDependency() != null) {
					// set existing value
					qDepId.setText(qDto.getQuestionDependency().getKeyId()
							.toString());
					loadDepQA(questionLB);
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

	private void loadDepQA(ListBox questionLBox) {
		Integer selectedIndex = questionLBox.getSelectedIndex();
		String value = questionLBox.getValue(selectedIndex);
		TreeItem questionGroup = surveyTree.getSelectedItem();

		if (questionGroup != null
				&& questionGroup
						.getUserObject()
						.getClass()
						.getName()
						.equals(
								"org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto")) {
			questionGroup = questionGroup.getParentItem();
		}
		if (questionGroup != null
				&& questionGroup
						.getUserObject()
						.getClass()
						.getName()
						.equals(
								"org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto")) {
			Boolean foundQuestion = false;
			for (int i = 0; i < questionGroup.getChildCount(); i++) {
				QuestionDto qDto = (QuestionDto) questionGroup.getChild(i)
						.getUserObject();
				if (qDto.getKeyId().toString().equals(value)) {
					ListBox answerLB = (ListBox) questionDetailPanel.getWidget(
							8, 3);
					List<QuestionOptionDto> qoList = qDto
							.getOptionContainerDto().getOptionsList();
					if (qoList != null) {
						for (QuestionOptionDto qoDto : qoList) {
							answerLB.addItem(qoDto.getText(), qoDto.getCode());
						}
					}
					TextBox qDepAnsId = new TextBox();

					foundQuestion = true;
					answerLB.setVisible(true);
				}
				if (foundQuestion)
					break;
			}
		}
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
		Button deleteOptionButton = new Button("Delete Option");

		questionDetailPanel.setWidget(7, 2, addNewOptionButton);
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
		TextBox optionText = new TextBox();
		TextBox optionId = new TextBox();
		optionId.setVisible(false);
		if (item != null) {
			if (item.getKeyId() != null)
				optionId.setText(item.getKeyId().toString());
			if (item.getCode() != null)
				optionValue.setText(item.getCode());
			if (item.getText() != null)
				optionText.setText(item.getText());
		}

		questionOptionDetail.setWidget(row, 0, new Label("Option Value"));
		questionOptionDetail.setWidget(row, 1, optionValue);
		questionOptionDetail.setWidget(row, 2, new Label("Option Text"));
		questionOptionDetail.setWidget(row, 3, optionText);
		questionOptionDetail.setWidget(row, 4, optionId);

	}

	private void saveQuestion() throws Exception {
		QuestionDto dto = getQuestionDto();
		Long parentId = null;
		if (dto.getKeyId() == null) {
			parentId = getCurrentId();
		} else {
			parentId = getParentId(QuestionGroupDto.class);
		}

		svc.saveQuestion(dto, parentId, new AsyncCallback<QuestionDto>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(QuestionDto result) {
				bindQuestion(result);
				if (result.getQuestionDependency() != null)
					((TextBox) questionDetailPanel.getWidget(8, 4))
							.setText(result.getQuestionDependency().getKeyId()
									.toString());

				Window.alert("Question Saved");
			}

		});

	}

	private QuestionDto getQuestionDto() {
		QuestionDto value = new QuestionDto();
		TextBox questionId = (TextBox) questionDetailPanel.getWidget(0, 0);
		TextBox questionText = (TextBox) questionDetailPanel.getWidget(1, 1);
		ListBox questionTypeLB = (ListBox) questionDetailPanel.getWidget(2, 1);

		TextBox tip = (TextBox) questionDetailPanel.getWidget(3, 1);
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
			FlexTable questionOptionTable = (FlexTable) questionDetailPanel
					.getWidget(6, 2);

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

			for (int row = 1; row < questionOptionTable.getRowCount(); row++) {
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
		svc.deleteQuestion(value, questionGroupId, new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Object result) {
				Window.alert("Question Deleted");
				questionDetailPanel.setVisible(false);
				// todo implement remove from tree

			}

		});
	}

	private void loadSurveyGroupDetail(SurveyGroupDto item) {
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

	private FlexTable surveyGroupDetail = new FlexTable();
	private FlexTable surveyDetail = new FlexTable();
	private FlexTable questionGroupDetail = new FlexTable();

	private void loadSurveyDetail(SurveyDto item) {
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
					svc.publishSurvey(surveyId, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(String result) {
							Window.alert(result);

						}

					});
				} else {
					Window.alert("Please save survey before publishing");
				}
			}

		});

	}

	private void loadQuestionGroupDetail(QuestionGroupDto item) {
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

	private Long getParentId(Class<? extends BaseDto> clazz) throws Exception {
		TreeItem parentItem = surveyTree.getSelectedItem().getParentItem();
		Long parentId = null;
		String className = clazz.getName();
		if (clazz.getName().equals(SurveyGroupDto.class.getName())
				&& parentItem.getUserObject().getClass().getName().equals(
						clazz.getName()))
			parentId = ((SurveyGroupDto) (parentItem.getUserObject()))
					.getKeyId();
		else if (clazz.getName().equals(SurveyDto.class.getName())
				&& parentItem.getUserObject().getClass().getName().equals(
						clazz.getName()))
			parentId = ((SurveyDto) (parentItem.getUserObject())).getKeyId();
		else if (clazz.getName().equals(QuestionGroupDto.class.getName())
				&& parentItem.getUserObject().getClass().getName().equals(
						clazz.getName()))
			parentId = ((QuestionGroupDto) (parentItem.getUserObject()))
					.getKeyId();
		else if (clazz.getName().equals(QuestionDto.class.getName())
				&& parentItem.getUserObject().getClass().getName().equals(
						clazz.getName()))
			parentId = ((QuestionDto) (parentItem.getUserObject())).getKeyId();
		else
			throw new Exception(
					"Cannot save item because parent item is of wrong type");

		return parentId;

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
		Long parentId = null;
		if (dto.getKeyId() == null)
			parentId = getCurrentId();
		else
			parentId = getParentId(SurveyDto.class);

		svc.saveQuestionGroup(dto, parentId,
				new AsyncCallback<QuestionGroupDto>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(QuestionGroupDto result) {
						TextBox questionGroupId = (TextBox) questionGroupDetail
								.getWidget(0, 0);
						questionGroupId.setText(result.getKeyId().toString());
						Window.alert("Saved Question Group");

					}

				});
	}

	private Long getCurrentId() {
		TreeItem item = surveyTree.getSelectedItem();
		return ((BaseDto) item.getUserObject()).getKeyId();
	}

	private TreeItem surveyParentItem = null;
	private Boolean isNewSurveyItem = false;

	private void saveSurvey() throws Exception {
		SurveyDto dto = getSurveyDto();
		Long parentId = null;
		if (dto.getKeyId() == null) {
			parentId = getCurrentId();
			isNewSurveyItem = false;
			surveyParentItem = surveyTree.getSelectedItem();
		} else
			parentId = getParentId(SurveyGroupDto.class);

		svc.saveSurvey(dto, parentId, new AsyncCallback<SurveyDto>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(SurveyDto result) {
				TextBox surveyId = (TextBox) surveyDetail.getWidget(0, 0);
				surveyId.setText(result.getKeyId().toString());
				Window.alert("Saved Survey");
				bindSurvey(result);
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
		svc.saveSurveyGroup(getSurveyGroupDto(),
				new AsyncCallback<SurveyGroupDto>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(SurveyGroupDto result) {
						bindSurveyGroup(result);
						Window.alert("Survey Group Saved");
					}

				});
	}

}
