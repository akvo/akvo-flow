package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SurveyGroupListWidget extends Composite {

	private static final String LOADING_CSS = "loading-label";
	private static final String LOADING_TEXT = "Loading. Please wait...";

	private VerticalPanel panel;
	private SurveyServiceAsync surveyService;
	private Label loadingLabel;

	public SurveyGroupListWidget() {
		panel = new VerticalPanel();
		surveyService = GWT.create(SurveyService.class);
		loadingLabel = new Label();
		loadingLabel.setText(LOADING_TEXT);
		loadingLabel.setStylePrimaryName(LOADING_CSS);
		panel.add(loadingLabel);
		initWidget(panel);
	}

	public void loadData() {
		surveyService.listSurveyGroups(null, false, false, false,
				new AsyncCallback<ArrayList<SurveyGroupDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						loadingLabel.setVisible(false);
						
					}

					@Override
					public void onSuccess(ArrayList<SurveyGroupDto> result) {
						loadingLabel.setVisible(false);
						if(result != null && result.size()>0){
							Grid dataGrid = new Grid(result.size(),2);
							
						}
					}
				});
	}

}