package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;


public class SurveyManagerPortlet extends Portlet {

	private VerticalPanel contentPanel = null;
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
	private Button deleteQuestionGroupButton = new Button("Delete Question Group");
	private Button deleteQuestionButton = new Button("Delete Question");
	
	HorizontalPanel buttonPanel = new HorizontalPanel();

	public SurveyManagerPortlet(String title, boolean scrollable,
			boolean configurable, int width, int height) {
		super(title, scrollable, configurable, WIDTH, HEIGHT);
		svc = GWT.create(SurveyService.class);
	}

	public SurveyManagerPortlet(){
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
		contentPane.add(surveyTree);
		contentPane.add(buttonPanel);
		configureButtonPanel();

	}

	private void loadTree() {
		surveyTree = new Tree();
		try{
		svc.listSurveyGroups("all", false, false,false,
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
							bindSurveyGroup(item, outerRoot);
						}
						
						surveyTree.addTreeListener(new TreeListener(){

							@Override
							public void onTreeItemSelected(TreeItem item) {
								TreeItem parentItem = item.getParentItem();
								if(parentItem.getText().toLowerCase().equals("survey groups")){
									String surveyGroupCode = item.getText().split(":")[0];
									
									
								}else{
									TreeItem grandparentItem = new TreeItem();
									grandparentItem = parentItem.getParentItem();
									if(grandparentItem.getText().toLowerCase().equals("survey groups")){
										String surveyCode= item.getText();
										svc.loadFullSurvey(surveyCode, new AsyncCallback<SurveyDto>(){

											@Override
											public void onFailure(
													Throwable caught) {
												// TODO Auto-generated method stub
												
											}

											@Override
											public void onSuccess(
													SurveyDto result) {
												// TODO Auto-generated method stub
												
											}
											
										});
									}else{
										TreeItem greatgrandparentItem = grandparentItem.getParentItem();
										if(greatgrandparentItem.getText().toLowerCase().equals("survey groups")){
											String questionGroupCode = item.getText();
										}
									}
								}
							}

							@Override
							public void onTreeItemStateChanged(TreeItem item) {
								// TODO Auto-generated method stub
								
							}
							
						});
						surveyTree.addOpenHandler(new OpenHandler<TreeItem>() {

							@Override
							public void onOpen(OpenEvent<TreeItem> event) {
								TreeItem selectedItem = new TreeItem();
								String surveyGroupCode = selectedItem.getText();
								svc
										.getSurveyGroup(
												surveyGroupCode,
												new AsyncCallback<ArrayList<SurveyDto>>() {

													@Override
													public void onFailure(
															Throwable caught) {
														// TODO Auto-generated
														// method stub

													}

													@Override
													public void onSuccess(
															ArrayList<SurveyDto> result) {
														TreeItem selectedItem = surveyTree
																.getSelectedItem();

													}

												});
							}

						});
						
					}

				});}catch(NullPointerException ex){
					System.out.print(ex);
				}

	}

	private void bindSurveyGroup(SurveyGroupDto item, TreeItem parentItem) {
		TreeItem surveyGroupItem = new TreeItem();
		surveyGroupItem.setText(item.getCode());
		parentItem.addItem(surveyGroupItem);
	}

	private void bindSurvey(SurveyDto item, TreeItem parentItem) {
		TreeItem surveyItem = new TreeItem();
		surveyItem.setText(item.getName());
		parentItem.addItem(surveyItem);
	}
	
	private void configureButtonPanel(){
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
	
	private void configureButtonHandlers(){
		addSurveyGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				Window.alert("Clicked Add Survey Group");
			}
			
		});
		
		deleteSurveyGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				Window.alert("Clicked Delete Survey Group");	
			}
			
		});
		
		addSurveyButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		deleteSurveyButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		addQuestionGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		deleteQuestionGroupButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		addQuestionButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		deleteQuestionButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
}
