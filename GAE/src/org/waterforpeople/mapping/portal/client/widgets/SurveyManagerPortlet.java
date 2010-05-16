package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
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

	public SurveyManagerPortlet(String title, boolean scrollable,
			boolean configurable, int width, int height) {
		super(title, scrollable, configurable, WIDTH, HEIGHT);
		GWT.create(SurveyService.class);
	}

	public SurveyManagerPortlet(){
		super(title, scrollable, configurable, WIDTH, HEIGHT);
		GWT.create(SurveyService.class);
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

	private void buildContentPanel() {
		loadTree();

	}

	private void loadTree() {
		surveyTree = new Tree();
		svc.listSurveyGroups("all",
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
							bindSurveyGroup(item, outerRoot);
						}
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
						contentPanel.add(surveyTree);
					}

				});

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
}
