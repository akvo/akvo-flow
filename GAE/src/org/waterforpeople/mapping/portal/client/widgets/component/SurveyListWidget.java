package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.ListBasedWidget;
import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
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

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private SurveyServiceAsync surveyService;
	private Map<Widget, SurveyDto> surveyMap;
	private Map<String, Object> bundle;
	private SurveyGroupDto surveyGroup;
	private Grid dataGrid;

	public SurveyListWidget(PageController controller) {
		super(controller);
		bundle = new HashMap<String, Object>();

		surveyService = GWT.create(SurveyService.class);
		surveyMap = new HashMap<Widget, SurveyDto>();
	}

	public void loadData(SurveyGroupDto groupDto) {
		surveyGroup = groupDto;
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
							surveyGroup.setSurveyList(result);
							if (dataGrid != null) {
								dataGrid.removeFromParent();
							}
							if (result != null && result.size() > 0) {
								dataGrid = new Grid(result.size(), 4);
								for (int i = 0; i < result.size(); i++) {
									Label l = createListEntry(result.get(i)
											.getName());
									dataGrid.setWidget(i, 0, l);
									surveyMap.put(l, result.get(i));
									Button edit = createButton(ClickMode.EDIT,
											TEXT_CONSTANTS.edit());
									surveyMap.put(edit, result.get(i));
									dataGrid.setWidget(i, 1, edit);
									Button copy = createButton(ClickMode.COPY,
											TEXT_CONSTANTS.copy());
									dataGrid.setWidget(i, 2, copy);
									surveyMap.put(copy, result.get(i));
									Button del = createButton(ClickMode.DELETE,
											TEXT_CONSTANTS.delete());
									dataGrid.setWidget(i, 3, del);
									surveyMap.put(del, result.get(i));
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
		flushContext();
		loadData((SurveyGroupDto) bundle.get(BundleConstants.SURVEY_GROUP_KEY));
	}

	@Override
	protected void handleItemClick(Object source, ClickMode mode) {
		bundle.put(BundleConstants.SURVEY_KEY, surveyMap.get(source));
		if (ClickMode.OPEN == mode) {
			openPage(QuestionGroupListWidget.class, bundle);
		} else if (ClickMode.EDIT == mode) {
			openPage(SurveyEditWidget.class, bundle);
		} else if (ClickMode.COPY == mode) {
			SurveyCopyDialog copyDialog = new SurveyCopyDialog(
					surveyMap.get(source), new CompletionListener() {

						@Override
						public void operationComplete(boolean wasSuccessful,
								Map<String, Object> payload) {
							MessageDialog dia = new MessageDialog(
									TEXT_CONSTANTS.copyComplete(),
									TEXT_CONSTANTS.copyCompleteMessage());
							dia.showCentered();
						}
					});
			copyDialog.show();
		} else if (ClickMode.DELETE == mode) {
			deleteSurvey(surveyMap.get(source));
		}
	}

	private void deleteSurvey(SurveyDto survey) {
		setWorking(true);
		Integer idx = null;
		for (int i = 0; i < surveyGroup.getSurveyList().size(); i++) {
			if (surveyGroup.getSurveyList().get(i).getKeyId()
					.equals(survey.getKeyId())) {
				idx = i;
			}
		}
		if (idx != null && idx >= 0) {
			surveyGroup.getSurveyList().remove(idx);
			final MessageDialog dia = new MessageDialog(
					TEXT_CONSTANTS.deleting(), TEXT_CONSTANTS.pleaseWait(),
					true);
			dia.showCentered();
			surveyService.deleteSurvey(survey, surveyGroup.getKeyId(),
					new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							dia.hide(true);
							setWorking(false);
							MessageDialog errDia = new MessageDialog(
									TEXT_CONSTANTS.error(), TEXT_CONSTANTS
											.errorTracePrefix()
											+ " "
											+ caught.getLocalizedMessage());
							errDia.showCentered();
						}

						@Override
						public void onSuccess(String result) {
							dia.hide(true);
							setWorking(false);
							loadData(surveyGroup);
						}
					});
		}
	}

	@Override
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		return bundle;
	}

	@Override
	public void flushContext() {
		if (bundle != null) {
			bundle.remove(BundleConstants.SURVEY_KEY);
		}
	}

	@Override
	public void persistContext(CompletionListener listener) {
		if (listener != null) {
			listener.operationComplete(true, getContextBundle(true));
		}
	}

}