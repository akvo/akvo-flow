package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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
	private QuestionGroupDto questionGroup;
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
			if (questionGroupDto.getQuestionMap() != null
					&& questionGroupDto.getQuestionMap().size() > 0) {
				populateQuestionList(questionGroupDto.getQuestionMap().values());
			} else {
				surveyService.listQuestionsByQuestionGroup(questionGroupDto
						.getKeyId().toString(), false,
						new AsyncCallback<ArrayList<QuestionDto>>() {

							@Override
							public void onFailure(Throwable caught) {
								toggleLoading(false);
							}

							@Override
							public void onSuccess(ArrayList<QuestionDto> result) {								
								if (result != null && result.size() > 0) {
									TreeMap<Integer, QuestionDto> questionTree = new TreeMap<Integer, QuestionDto>();

									for (int i = 0; i < result.size(); i++) {
										questionTree.put(result.get(i)
												.getOrder(), result.get(i));
									}
									populateQuestionList(result);
									questionGroup.setQuestionMap(questionTree);
									bundle.put(
											BundleConstants.QUESTION_GROUP_KEY,
											questionGroup);

								}else{
									toggleLoading(false);
								}
							}
						});
			}
		}
	}

	private void populateQuestionList(Collection<QuestionDto> questionList) {
		toggleLoading(false);
		Grid dataGrid = new Grid(questionList.size(), 2);
		int i = 0;
		if (questionList != null) {
			for (QuestionDto q : questionList) {
				Label l = createListEntry(q.getText());
				questionMap.put(l, q);
				Button b = createButton(ClickMode.EDIT, "Edit");
				dataGrid.setWidget(i, 1, b);
				questionMap.put(b, q);
				dataGrid.setWidget(i, 0, l);
				i++;
			}
		}
		addWidget(dataGrid);
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		// remove the bundle item that this widget populates
		bundle.remove(BundleConstants.QUESTION_KEY);
		questionGroup = (QuestionGroupDto) bundle
				.get(BundleConstants.QUESTION_GROUP_KEY);
		loadData(questionGroup);
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