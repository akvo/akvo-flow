package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyAssignmentServiceAsync;
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
 * lists existing surveyAssignment objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyAssignmentListWidget extends ListBasedWidget implements
		ContextAware {

	private static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);
	private Map<String, Object> bundle;
	private Map<Widget, SurveyAssignmentDto> assignmentMap;
	private SurveyAssignmentServiceAsync surveyAssignmentService;
	private Grid dataGrid;

	public SurveyAssignmentListWidget(PageController controller) {
		super(controller);
		surveyAssignmentService = GWT.create(SurveyAssignmentService.class);
		assignmentMap = new HashMap<Widget, SurveyAssignmentDto>();

	}

	private void loadData() {
		if(dataGrid != null){
			dataGrid.removeFromParent();
		}
		surveyAssignmentService
				.listSurveyAssignments(new AsyncCallback<SurveyAssignmentDto[]>() {

					@Override
					public void onSuccess(SurveyAssignmentDto[] result) {
						toggleLoading(false);
						if (result != null && result.length > 0) {							
							dataGrid = new Grid(result.length, 3);
							for (int i = 0; i < result.length; i++) {
								Label l = createListEntry(result[i].getName());
								dataGrid.setWidget(i, 0, l);
								assignmentMap.put(l, result[i]);
								Button b = createButton(ClickMode.EDIT, TEXT_CONSTANTS.edit());
								assignmentMap.put(b, result[i]);
								dataGrid.setWidget(i, 1, b);
								Button e = createButton(ClickMode.DELETE,
										TEXT_CONSTANTS.delete());
								dataGrid.setWidget(i, 2, e);
								assignmentMap.put(e, result[i]);
							}
							addWidget(dataGrid);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						toggleLoading(false);
						MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS.error(),TEXT_CONSTANTS.errorTracePrefix()+" "+caught.getLocalizedMessage());							
						errDia.showCentered();
					}
				});
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		flushContext();
		loadData();
	}

	@Override
	protected void handleItemClick(Object source, ClickMode mode) {
		bundle
				.put(BundleConstants.SURVEY_ASSIGNMENT, assignmentMap
						.get(source));
		if (ClickMode.EDIT == mode || ClickMode.OPEN == mode) {
			openPage(SurveyAssignmentEditWidget.class, bundle);
		} else if (ClickMode.DELETE == mode) {
			setWorking(true);
			surveyAssignmentService.deleteSurveyAssignment(assignmentMap
					.get(source), new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					setWorking(false);
					flushContext();
					MessageDialog errDia = new MessageDialog(TEXT_CONSTANTS.error(),TEXT_CONSTANTS.errorTracePrefix()+" "+caught.getLocalizedMessage());
					errDia.showCentered();
				}

				@Override
				public void onSuccess(Void result) {
					setWorking(false);
					flushContext();
					loadData();

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
			bundle.remove(BundleConstants.SURVEY_ASSIGNMENT);
		}
	}

	@Override
	public void persistContext(CompletionListener listener) {
		if (listener != null) {
			listener.operationComplete(true, getContextBundle(true));
		}
	}

}
