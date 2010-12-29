package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SurveyGroupListWidget extends Composite implements ClickHandler, MouseOverHandler, MouseOutHandler{

	private static final String LOADING_CSS = "loading-label";
	private static final String LOADING_TEXT = "Loading. Please wait...";
	private static final String LIST_ITEM_CSS = "clickable-list-item";
	private static final String LIST_ITEM_HOVER_CSS = "red-hover";
	

	private VerticalPanel panel;
	private SurveyServiceAsync surveyService;
	private Label loadingLabel;
	private Map<Widget,SurveyGroupDto> groupMap;

	public SurveyGroupListWidget() {
		panel = new VerticalPanel();
		surveyService = GWT.create(SurveyService.class);
		groupMap = new HashMap<Widget,SurveyGroupDto>();
		loadingLabel = new Label();
		loadingLabel.setText(LOADING_TEXT);
		loadingLabel.setStylePrimaryName(LOADING_CSS);
		panel.add(loadingLabel);
		initWidget(panel);
		loadData();
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
							for(int i=0; i < result.size(); i++){
								Label l = new Label();									
								l.setStylePrimaryName(LIST_ITEM_CSS);
								l.setText(result.get(i).getDisplayName());
								l.addMouseOutHandler(SurveyGroupListWidget.this);
								l.addMouseOverHandler(SurveyGroupListWidget.this);
								l.addClickHandler(SurveyGroupListWidget.this);
								dataGrid.setWidget(i, 0,l);
								groupMap.put(l,result.get(i));
							}
							panel.add(dataGrid);
						}
					}
				});
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		((Label)event.getSource()).removeStyleName(LIST_ITEM_HOVER_CSS);
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		((Label)event.getSource()).addStyleName(LIST_ITEM_HOVER_CSS);
	}

	@Override
	public void onClick(ClickEvent event) {
	//TODO: load next page	
	}
}