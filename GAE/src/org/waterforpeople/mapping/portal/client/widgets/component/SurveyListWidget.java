package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
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
 * Displays lists of surveys. Clicking the edit button beside a survey will open
 * that survey in edit mode.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyListWidget extends ListBasedWidget implements ContextAware {

	private SurveyServiceAsync surveyService;
	private Map<Widget, SurveyDto> surveyMap;
	private Map<String, Object> bundle;

	public SurveyListWidget(PageController controller) {
		super(controller);
		bundle = new HashMap<String, Object>();

		surveyService = GWT.create(SurveyService.class);
		surveyMap = new HashMap<Widget, SurveyDto>();
	}

	public void loadData(SurveyGroupDto groupDto) {
		if (groupDto != null) {
			surveyService.listSurveysByGroup(groupDto.getKeyId().toString(),
					new AsyncCallback<ArrayList<SurveyDto>>() {

						@Override
						public void onFailure(Throwable caught) {
							toggleLoading(false);
						}

						@Override
						public void onSuccess(ArrayList<SurveyDto> result) {
							toggleLoading(false);
							if (result != null && result.size() > 0) {
								Grid dataGrid = new Grid(result.size(), 3);
								for (int i = 0; i < result.size(); i++) {
									Label l = createListEntry(result.get(i)
											.getName());
									dataGrid.setWidget(i, 0, l);
									surveyMap.put(l, result.get(i));
									Button b = createButton(ClickMode.EDIT,
											"Edit");
									dataGrid.setWidget(i, 1, b);
									Button e = createButton(ClickMode.COPY,
									"Copy");
									dataGrid.setWidget(i, 2, e);
									surveyMap.put(e, result.get(i));
								}
								addWidget(dataGrid);
							}
						}
					});
		}
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		bundle.remove(BundleConstants.SURVEY_KEY);
		loadData((SurveyGroupDto) bundle.get(BundleConstants.SURVEY_GROUP_KEY));
	}

	@Override
	protected void handleItemClick(Object source, ClickMode mode) {
		bundle.put(BundleConstants.SURVEY_KEY, surveyMap.get(source));
		if (ClickMode.OPEN == mode) {
			openPage(QuestionGroupListWidget.class, bundle);
		} else if (ClickMode.EDIT == mode) {
			openPage(SurveyEditWidget.class, bundle);
		}else if (ClickMode.COPY == mode){
			SurveyCopyDialog copyDialog = new SurveyCopyDialog(surveyMap.get(source),new CompletionListener() {
				
				@Override
				public void operationComplete(boolean wasSuccessful,
						Map<String, Object> payload) {
					// TODO Auto-generated method stub
					
				}
			});
			copyDialog.show();
		}
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