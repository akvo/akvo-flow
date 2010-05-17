package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
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
	private static final int WIDTH = 800;

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
	VerticalPanel questionDetailPanel = new VerticalPanel();

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

	@Override
	protected boolean getReadyForRemove() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void handleConfigClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(PortletEvent e) {
		// TODO Auto-generated method stub

	}

	private VerticalPanel contentPane = null;

	private void buildContentPanel() {
		contentPane = new VerticalPanel();
		setContent(contentPane);

		loadTree();
		treeContainer.add(surveyTree);
		treeContainer.add(detailContainer);
		contentPane.add(treeContainer);
		contentPane.add(buttonPanel);
		configureButtonPanel();

	}
	private VerticalPanel detailContainer = new VerticalPanel();

	private TreeItem selectedItem = null;
	
	private void removeAllWidgetsLoadThisWidget(Widget w){
		for(int i=0;i<detailContainer.getWidgetCount();i++){
			detailContainer.remove(i);
		}
		detailContainer.add(w);
	}

	private void loadTree() {
		surveyTree = new Tree();
		try {
			svc.listSurveyGroups("all", false, false, false,
					new AsyncCallback<ArrayList<SurveyGroupDto>>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}

						@SuppressWarnings("deprecation")
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
											if (item
													.getUserObject()
													.getClass()
													.getName()
													.equals(
															"org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto")) {
												SurveyGroupDto sg = (SurveyGroupDto) item
														.getUserObject();
												loadSurveyGroupDetail(sg);
												
												
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
												loadQuestionGroupDetail(qgDto);
												
												svc
														.listQuestionsByQuestionGroup(
																qgDto
																		.getKeyId()
																		.toString(),
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
												loadQuestionDetails(questionDto);
												

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
		TreeItem surveyGroupItem = new TreeItem();

		surveyGroupItem.setUserObject(item);
		surveyGroupItem.setText(item.getCode());
		TreeItem parentItem = surveyTree.getItem(0);
		parentItem.addItem(surveyGroupItem);
	}

	private void bindSurvey(SurveyDto item) {
		TreeItem parentItem = surveyTree.getSelectedItem();
		TreeItem surveyItem = new TreeItem();
		surveyItem.setText(item.getName());
		surveyItem.setUserObject(item);
		parentItem.addItem(surveyItem);
	}

	private void bindQuestionGroup(QuestionGroupDto item) {
		TreeItem parentItem = surveyTree.getSelectedItem();
		TreeItem questionGroupItem = new TreeItem();
		questionGroupItem.setText(item.getCode());
		questionGroupItem.setUserObject(item);
		parentItem.addItem(questionGroupItem);
	}

	private void bindQuestion(QuestionDto item) {
		TreeItem parentItem = surveyTree.getSelectedItem();
		TreeItem questionGroupItem = new TreeItem();
		questionGroupItem.setText(item.getText());
		questionGroupItem.setUserObject(item);
		parentItem.addItem(questionGroupItem);
	}

	private void configureButtonPanel() {
		buttonPanel.add(addSurveyGroupButton);
		buttonPanel.add(deleteSurveyGroupButton);
		buttonPanel.add(addSurveyButton);
		buttonPanel.add(deleteSurveyButton);
		buttonPanel.add(addQuestionGroupButton);
		buttonPanel.add(deleteQuestionGroupButton);
		buttonPanel.add(addQuestionButton);
		buttonPanel.add(deleteQuestionButton);
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

	private void loadQuestionDetails(QuestionDto item) {
		removeAllWidgetsLoadThisWidget(questionDetailPanel);
		TextBox questionId = new TextBox();
		TextBox questionText = new TextBox();
		if (item != null) {
			questionId.setText(item.getKeyId().toString());
			questionId.setVisible(false);
			questionText.setText(item.getText());
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
		Button saveQuestionButton = new Button("Save Question");
		Button deleteQuestionButton = new Button("Delete Question");
		questionDetailPanel.add(questionId);
		questionDetailPanel.add(questionText);
		questionDetailPanel.add(questionTypeLB);
		questionDetailPanel.add(saveQuestionButton);
		questionDetailPanel.add(deleteQuestionButton);

		saveQuestionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				QuestionDto value = new QuestionDto();
				TextBox questionId = (TextBox) questionDetailPanel.getWidget(0);
				TextBox questionText = (TextBox) questionDetailPanel.getWidget(1);
				ListBox questionTypeLB = (ListBox) questionDetailPanel.getWidget(2);
				value.setKeyId(new Long(questionId.getText()));
				value.setText(questionText.getText());
				if (questionTypeLB.getSelectedIndex() == 0) {
					value.setType(QuestionType.FREE_TEXT);
				} else if (questionTypeLB.getSelectedIndex() == 1) {
					value.setType(QuestionType.OPTION);
				} else if (questionTypeLB.getSelectedIndex() == 2) {
					value.setType(QuestionType.NUMBER);
				} else if (questionTypeLB.getSelectedIndex() == 3) {
					value.setType(QuestionType.GEO);
				} else if (questionTypeLB.getSelectedIndex() == 4) {
					value.setType(QuestionType.PHOTO);
				} else if (questionTypeLB.getSelectedIndex() == 5) {
					value.setType(QuestionType.VIDEO);
				}
				TreeItem item = surveyTree.getSelectedItem();
				if (item
						.getUserObject()
						.equals(
								"org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto"))
					saveQuestion(value, ((QuestionGroupDto) item
							.getUserObject()).getKeyId());
			}

		});
		deleteQuestionButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				QuestionDto value = new QuestionDto();
				TextBox questionId = (TextBox) questionDetailPanel.getWidget(0);
				value.setKeyId(new Long(questionId.getText()));
				deleteQuestion(value, 1L);
			}

		});

	}

	private void saveQuestion(QuestionDto value, Long questionGroupId) {
		svc.saveQuestion(value, questionGroupId,
				new AsyncCallback<QuestionDto>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(QuestionDto result) {
						Window.alert("Question Saved");
					}

				});
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
	
	private void loadSurveyGroupDetail(SurveyGroupDto item){
		TextBox surveyGroupId = new TextBox();
		TextBox surveyGroupCode = new TextBox();
		TextBox surveyGroupDesc = new TextBox();
		
		if(item!=null){
			surveyGroupId.setText(item.getKeyId().toString());
			surveyGroupCode.setText(item.getCode());
			surveyGroupDesc.setText(item.getDescription());
		}
		
		surveyGroupId.setVisible(false);
		Button saveSurveyGroupButton = new Button("Save Survey Group");
		Button deleteSurveyGroupButton = new Button("Delete Survey Group");
		surveyGroupDetail.add(surveyGroupId);
		surveyGroupDetail.add(surveyGroupCode);
		surveyGroupDetail.add(surveyGroupDesc);
		surveyGroupDetail.add(saveSurveyGroupButton);
		surveyGroupDetail.add(deleteSurveyGroupButton);
		saveSurveyGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				SurveyGroupDto dto = new SurveyGroupDto();
				TextBox surveyGroupId =(TextBox) surveyGroupDetail.getWidget(0);
				if(surveyGroupId.getText().length()>0){
					dto.setKeyId(new Long(surveyGroupId.getText()));
				}
				TextBox groupCode = (TextBox) surveyGroupDetail.getWidget(1);
				if(groupCode.getText().length()>0){
					dto.setCode(groupCode.getText());
				}
				TextBox desc = (TextBox)surveyGroupDetail.getWidget(2);
				if(desc.getText().length()>0){
					dto.setDescription(desc.getText());
				}
				svc.saveSurveyGroup(dto, new AsyncCallback<SurveyGroupDto>(){

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(SurveyGroupDto result) {
						Window.alert("Survey Group Saved");
					}
					
				});
			}
			
		});
		deleteSurveyGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
		removeAllWidgetsLoadThisWidget(surveyGroupDetail);
	}
	
	private VerticalPanel surveyGroupDetail = new VerticalPanel();
	private FlexTable surveyDetail = new FlexTable();
	private FlexTable questionGroupDetail = new FlexTable();
	
	
	private void loadSurveyDetail(SurveyDto item){
		TextBox surveyId = new TextBox();
		TextBox surveyname = new TextBox();
		TextBox surveyDesc = new TextBox();
		TextBox version = new TextBox();
		
		if(item!=null){
			surveyId.setText(item.getKeyId().toString());
			surveyname.setText(item.getName());
			surveyDesc.setText(item.getDescription());
			version.setText(item.getVersion());
		}
		
		Button saveSurveyButton = new Button("Save");
		Button deleteSurveyButton = new Button("Delete");
		
		surveyDetail.setWidget(0,0,surveyId);
		surveyDetail.setWidget(1, 0, new Label("Survey Name"));
		surveyDetail.setWidget(1,1,surveyname);
		surveyDetail.setWidget(2,0,new Label("Description"));
		surveyDetail.setWidget(2,1,surveyDesc);
		surveyDetail.setWidget(3, 0,new Label("Version"));
		surveyDetail.setWidget(3,1,version);
		surveyDetail.setWidget(4,0,saveSurveyButton);
		surveyDetail.setWidget(4,1,deleteSurveyButton);
		removeAllWidgetsLoadThisWidget(surveyDetail);
		
		
		saveSurveyButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				SurveyDto surveyDto = new SurveyDto();
				TreeItem parentItem = surveyTree.getSelectedItem();
				SurveyGroupDto sgDto = (SurveyGroupDto)parentItem.getUserObject();
				
				Long surveyGroupId = sgDto.getKeyId();
				
				TextBox surveyId = (TextBox)surveyDetail.getWidget(0,0);
				if(surveyId.getText().length()>0)
					surveyDto.setKeyId(new Long(surveyId.getText()));
				
				TextBox name =(TextBox)	surveyDetail.getWidget(1,1);
				if(name.getText().length()>0)
					surveyDto.setName(name.getText());
				
				TextBox desc = (TextBox)surveyDetail.getWidget(2,1);
				if(desc.getText().length()>0)
					surveyDto.setDescription(desc.getText());
				
				svc.saveSurvey(surveyDto, surveyGroupId, new AsyncCallback<SurveyDto>(){

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(SurveyDto result) {
						Window.alert("Survey saved");
						
					}
					
				});
			}
			
		});
		
	}
	
	private void loadQuestionGroupDetail(QuestionGroupDto item){
		removeAllWidgetsLoadThisWidget(questionGroupDetail);
		TextBox questionGroupId = new TextBox();
		TextBox name = new TextBox();
		TextBox description = new TextBox();
		
		if(item!=null){
			questionGroupId.setText(item.getKeyId().toString());
			name.setText(item.getCode());
			description.setText(item.getDescription());
		}
		
		Button saveQuestionGroupButton = new Button();
		Button deleteQuestionGroupButton = new Button();
		questionGroupDetail.setWidget(0,0,questionGroupId);
		questionGroupDetail.setWidget(1, 0, new Label("Name"));
		questionGroupDetail.setWidget(1,1,name);
		questionGroupDetail.setWidget(2,0,new Label("Description"));
		questionGroupDetail.setWidget(2,1,description);
		questionGroupDetail.setWidget(4,0,saveQuestionGroupButton);
		questionGroupDetail.setWidget(4,1,deleteQuestionGroupButton);
	}
	
}
