package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.SurveyInstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.gwt.client.SurveyInstance.SurveyInstanceService;
import org.waterforpeople.mapping.app.gwt.client.SurveyInstance.SurveyInstanceServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RawDataViewPortlet extends LocationDrivenPortlet {
	private static String title;
	private static String description;
	private static Boolean scrollable;
	private static Boolean configurable;
	private static Integer width;
	private static Integer height;
	private static Boolean useCommunity;
	private static String specialOption;
	private static UserDto user;

	private Tree surveyImportedTree = new Tree();

	public RawDataViewPortlet(String title, boolean scrollable,
			boolean configurable, int width, int height, UserDto user,
			boolean useCommunity, String specialOption) {
		super(title, scrollable, configurable, width, height, user,
				useCommunity, specialOption);
		setupPortlet();
	}

	public RawDataViewPortlet() {
		super(title, scrollable, configurable, width, height, user,
				useCommunity, specialOption);
		setupPortlet();
	}

	private SurveyInstanceServiceAsync svc = null;
	private ServiceDefTarget endpoint = null;

	private void setupPortlet() {
		bindSvc();
		loadContentPanel();
		
	}
	
	
	
	public String getDescription(){
		return description;
	}

	private void bindSvc() {
		svc = GWT.create(SurveyInstanceService.class);
		endpoint = (ServiceDefTarget) svc;
		endpoint
				.setServiceEntryPoint("/org.waterforpeople.mapping.portal.portal/technologytype");
	}

	private void loadContentPanel() {
		loadSurveyImportTree();
		contentPanel.add(mainHPanel);
		setWidget(contentPanel);
	}

	@Override
	public String getName() {
		return title;
	}

	private void loadSurveyImportTree() {
		svc.listSurveyInstance(null,
				new AsyncCallback<ArrayList<SurveyInstanceDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(ArrayList<SurveyInstanceDto> result) {
						for (SurveyInstanceDto item : result)
							bindItemToTree(item);
						mainHPanel.add(surveyImportedTree);
					}

				});
	}
	private HorizontalPanel mainHPanel = new HorizontalPanel();
	private VerticalPanel contentPanel = new VerticalPanel();
	

	private void bindItemToTree(SurveyInstanceDto item) {
		TreeItem treeItem= new TreeItem();
		treeItem.setText(item.getKeyId() + ":" + item.getCollectionDate());
		treeItem.setUserObject(item);
		surveyImportedTree.addItem(treeItem);
	}

}
