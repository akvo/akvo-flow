package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.component.ListBasedWidget;
import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * displays lists of SurveyGroups. Clicking the edit button beside a group will
 * open that group in edit mode.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyGroupListWidget extends ListBasedWidget implements
		ContextAware {

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
								Button b = createButton(ClickMode.EDIT, "Edit");
								groupMap.put(b, result.get(i));
								dataGrid.setWidget(i, 1, b);
							}
							addWidget(dataGrid);
						}
					}
				});
	}

	@Override
	protected void handleItemClick(Object source, ClickMode mode) {
		bundle.put(BundleConstants.SURVEY_GROUP_KEY, groupMap.get(source));
		if (ClickMode.OPEN == mode) {
			openPage(SurveyListWidget.class, bundle);
		} else if (ClickMode.EDIT == mode) {
			openPage(SurveyGroupEditWidget.class, bundle);
		}
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
	}

	@Override
	public Map<String, Object> getContextBundle() {
		return bundle;
	}

	@Override
	public void persistContext(CompletionListener listener) {
		if (listener != null) {
			listener.operationComplete(true, getContextBundle());
		}
	}

}