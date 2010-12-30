package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
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

public class QuestionGroupListWidget extends ListBasedWidget implements
		WorkflowParticipant {

	private SurveyServiceAsync surveyService;
	private Map<Widget, QuestionGroupDto> questionGroupMap;
	Map<String, Object> bundle;
	

	public QuestionGroupListWidget(PageController controller) {
		super(controller);
		bundle = new HashMap<String, Object>();
		surveyService = GWT.create(SurveyService.class);
		questionGroupMap = new HashMap<Widget, QuestionGroupDto>();
	}

	public void loadData(String surveyId) {
		surveyService.listQuestionGroupsBySurvey(surveyId,
				new AsyncCallback<ArrayList<QuestionGroupDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						toggleLoading(false);
					}

					@Override
					public void onSuccess(ArrayList<QuestionGroupDto> result) {
						toggleLoading(false);
						if (result != null && result.size() > 0) {
							Grid dataGrid = new Grid(result.size(), 2);
							for (int i = 0; i < result.size(); i++) {
								Label l = createListEntry(result.get(i)
										.getName());
								dataGrid.setWidget(i, 0, l);
								questionGroupMap.put(l, result.get(i));
							}
							addWidget(dataGrid);
						}
					}
				});
	}

	@Override
	public void setBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		loadData((String) bundle.get(BundleConstants.SURVEY_KEY).toString());
	}

	@Override
	protected void handleItemClick(Object source) {
		bundle.put(BundleConstants.QUESTION_GROUP_KEY,
				questionGroupMap.get(source).getKeyId());
		openPage(QuestionListWidget.class, bundle);
	}

	@Override
	public Map<String, Object> getBundle() {
		return bundle;
	}
}