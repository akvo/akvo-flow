package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.component.ListBasedWidget;
import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.wizard.client.WorkflowParticipant;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SurveyListWidget extends ListBasedWidget implements WorkflowParticipant  {

	private SurveyServiceAsync surveyService;
	private Map<Widget, SurveyDto> surveyMap;
	public static final String SURVEY_GROUP_KEY = "SURVEY_GROUP_ID";

	public SurveyListWidget(PageController controller) {
		super(controller);
		surveyService = GWT.create(SurveyService.class);
		surveyMap = new HashMap<Widget, SurveyDto>();
	}

	public void loadData(String groupId) {
		surveyService.listSurveysByGroup(groupId,
				new AsyncCallback<ArrayList<SurveyDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						toggleLoading(false);
					}

					@Override
					public void onSuccess(ArrayList<SurveyDto> result) {
						toggleLoading(false);
						if (result != null && result.size() > 0) {
							Grid dataGrid = new Grid(result.size(), 2);
							for (int i = 0; i < result.size(); i++) {
								Label l = createListEntry(result.get(i)
										.getName());
								dataGrid.setWidget(i, 0, l);
								surveyMap.put(l, result.get(i));
							}
							addWidget(dataGrid);
						}
					}
				});
	}

	@Override
	public void setBundle(Map<String, Object> bundle) {
		loadData((String) bundle.get(SURVEY_GROUP_KEY).toString());		
	}

	@Override
	protected void handleItemClick(Object source) {
		Map<String,Object> bundle = new HashMap<String,Object>();
		bundle.put(QuestionGroupListWidget.SURVEY_KEY, surveyMap.get(source).getKeyId());
		openPage(QuestionGroupListWidget.class, bundle);
	}

}