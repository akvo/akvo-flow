package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
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

public class QuestionListWidget extends ListBasedWidget implements ContextAware {

	private SurveyServiceAsync surveyService;
	private Map<Widget, QuestionDto> questionMap;
	private QuestionDto selectedQuestion;
	private Map<String, Object> bundle;

	public QuestionListWidget(PageController controller) {
		super(controller);
		bundle = new HashMap<String, Object>();
		surveyService = GWT.create(SurveyService.class);
		questionMap = new HashMap<Widget, QuestionDto>();
		selectedQuestion = null;
	}

	public void loadData(QuestionGroupDto questionGroupDto) {
		if (questionGroupDto != null) {
			surveyService.listQuestionsByQuestionGroup(questionGroupDto
					.getKeyId().toString(), false,
					new AsyncCallback<ArrayList<QuestionDto>>() {

						@Override
						public void onFailure(Throwable caught) {
							toggleLoading(false);
						}

						@Override
						public void onSuccess(ArrayList<QuestionDto> result) {
							toggleLoading(false);
							if (result != null && result.size() > 0) {
								Grid dataGrid = new Grid(result.size(), 2);
								for (int i = 0; i < result.size(); i++) {
									Label l = createListEntry(result.get(i)
											.getText());
									dataGrid.setWidget(i, 0, l);
									questionMap.put(l, result.get(i));
									Button b = createButton(ClickMode.EDIT,
											"Edit");
									dataGrid.setWidget(i, 1, b);
									questionMap.put(b, result.get(i));
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
		loadData((QuestionGroupDto) bundle
				.get(BundleConstants.QUESTION_GROUP_KEY));
	}

	@Override
	protected void handleItemClick(Object source, ClickMode mode) {

		QuestionDto q = questionMap.get((Widget) source);
		if (q != null) {
			selectedQuestion = q;
		}
		if (ClickMode.DELETE != mode) {
			openPage(QuestionEditWidget.class, getContextBundle());
		}
	}

	@Override
	public Map<String, Object> getContextBundle() {
		if (selectedQuestion != null) {
			bundle.put(BundleConstants.QUESTION_KEY, selectedQuestion);
		}
		return bundle;
	}

	@Override
	public void persistContext(CompletionListener listener) {
		if (listener != null) {
			listener.operationComplete(true, getContextBundle());
		}

	}
}