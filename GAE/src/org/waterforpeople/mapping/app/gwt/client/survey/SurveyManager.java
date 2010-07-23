package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SurveyManager implements EntryPoint {
	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private HorizontalPanel hPanel = new HorizontalPanel();
	private VerticalPanel vPanel = new VerticalPanel();
	private Tree surveyTree = new Tree();
	private Button addItem = new Button("+");
	private Button deleteItem = new Button("-");

	private TextBox nameBox = new TextBox();
	private Button saveButton = new Button("save");
	private VerticalPanel detailVPanel = new VerticalPanel();
	private HorizontalPanel detailHPanel = new HorizontalPanel();
	SurveyGroupServiceAsync svc;
	ServiceDefTarget endpoint;
	@SuppressWarnings("unchecked")
	AsyncCallback callback;

	@SuppressWarnings("unchecked")
	public void onModuleLoad() {
		actionType = "load";

		svc = (SurveyGroupServiceAsync) GWT.create(SurveyGroupService.class);
		endpoint = (ServiceDefTarget) svc;
		endpoint.setServiceEntryPoint("/surveygroup");
		callback = new AsyncCallback() {

			public void onFailure(Throwable caught) {

			}

			public void onSuccess(Object result) {
				if (actionType.equals("createSurveyGroup")) {
					TreeItem outerRoot = new TreeItem(((SurveyGroupDto) result)
							.getCode());
					surveyTree.addItem(outerRoot);
				} else if (actionType.equals("load")) {
					loadSurveyTree((List<SurveyGroupDto>) result);
				}
			}

		};
		loadElements();
		addItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addItem(event);
			}
		});
		saveButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				saveDetailItem(event);
			}

		});
	}

	private void loadElements() {
		addToPanel();
	}

	@SuppressWarnings("unchecked")
	private void addToPanel() {
		if (actionType.equals("load")) {
			svc.listSurveyGroups("desc", callback);
		}
		hPanel.add(surveyTree);
		nameBox.setVisible(false);
		detailHPanel.add(nameBox);
		saveButton.setVisible(false);
		detailVPanel.add(saveButton);
		detailVPanel.add(detailHPanel);
		hPanel.add(detailVPanel);
		addPanel.add(addItem);
		addPanel.add(deleteItem);
		vPanel.add(hPanel);
		vPanel.add(addPanel);
		mainPanel.add(vPanel);
		// RootPanel.get("survey").add(mainPanel);
	}

	private void loadSurveyTree(List<SurveyGroupDto> surveyGroupList) {
		for (SurveyGroupDto sg : surveyGroupList) {
			surveyTree.addItem(sg.getCode());
		}
	}

	String actionType = new String();

	private void addItem(ClickEvent event) {
		if (surveyTree.getItemCount() == 0) {
			actionType = "createSurveyGroup";
		} else {
			surveyTree.getSelectedItem();
		}
		nameBox.setVisible(true);
		saveButton.setVisible(true);

	}

	@SuppressWarnings("unchecked")
	private void saveDetailItem(ClickEvent event) {
		actionType = "createSurveyGroup";
		nameBox.setVisible(true);
		saveButton.setVisible(true);
		SurveyGroupDto sg = new SurveyGroupDto();
		sg.setCode(nameBox.getText());
		sg.setDescription(nameBox.getSelectedText());
		svc.saveSurveyGroup(sg, callback);
	}

}
