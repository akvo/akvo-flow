package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
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

public class SurveyGroupListWidget extends ListBasedWidget implements
		WorkflowParticipant {

	private SurveyServiceAsync surveyService;
	private Map<Widget, SurveyGroupDto> groupMap;
	Map<String, Object> bundle;

	public SurveyGroupListWidget(PageController controller) {
		super(controller);
		bundle = new HashMap<String, Object>();
		surveyService = GWT.create(SurveyService.class);
		groupMap = new HashMap<Widget, SurveyGroupDto>();
		loadData();
	}

	public void loadData() {
		surveyService.listSurveyGroups(null, false, false, false,
				new AsyncCallback<ArrayList<SurveyGroupDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						toggleLoading(false);
					}

					@Override
					public void onSuccess(ArrayList<SurveyGroupDto> result) {
						toggleLoading(false);
						if (result != null && result.size() > 0) {
							Grid dataGrid = new Grid(result.size(), 2);
							for (int i = 0; i < result.size(); i++) {
								Label l = createListEntry(result.get(i)
										.getDisplayName());
								dataGrid.setWidget(i, 0, l);
								groupMap.put(l, result.get(i));
							}
							addWidget(dataGrid);
						}
					}
				});
	}

	@Override
	protected void handleItemClick(Object source) {
		bundle.put(BundleConstants.SURVEY_GROUP_KEY, groupMap.get(source)
				.getKeyId());
		openPage(SurveyListWidget.class, bundle);
	}

	@Override
	public void setBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
	}

	@Override
	public Map<String, Object> getBundle() {
		return bundle;
	}

}