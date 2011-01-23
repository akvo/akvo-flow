package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;

import com.gallatinsystems.framework.gwt.component.ListBasedWidget;
import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.wizard.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Widget for viewing question lists
 * 
 * TODO: handle delete and reorder
 * 
 * @author Christopher Fagiani
 * 
 */
public class QuestionListWidget extends ListBasedWidget implements ContextAware {

	private SurveyServiceAsync surveyService;
	private Map<Widget, QuestionDto> questionMap;
	private QuestionDto selectedQuestion;
	private QuestionGroupDto questionGroup;
	private Map<String, Object> bundle;
	private Grid dataGrid;
	
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

								} else {
									toggleLoading(false);
								}
							}
						});
			}
		}
	}

	private void populateQuestionList(Collection<QuestionDto> questionList) {
		toggleLoading(false);
		if(dataGrid != null){
			dataGrid.removeFromParent();
		}
		dataGrid = new Grid(questionList.size(), 4);
		int i = 0;
		if (questionList != null) {
			for (QuestionDto q : questionList) {
				Label l = createListEntry(q.getText());
				HorizontalPanel bp = new HorizontalPanel();
				Image moveUp = new Image("/images/greenuparrow.png");
				createClickableWidget(ClickMode.MOVE_UP, moveUp);
				Image moveDown = new Image("/images/greendownarrow.png");
				createClickableWidget(ClickMode.MOVE_DOWN, moveDown);
				Button deleteButton = createButton(ClickMode.DELETE, "Delete");
				Button editButton = createButton(ClickMode.EDIT, "Edit");
				bp.add(moveUp);
				bp.add(moveDown);

				questionMap.put(l, q);
				dataGrid.setWidget(i, 0, l);
				dataGrid.setWidget(i, 1, bp);
				dataGrid.setWidget(i, 2, editButton);
				dataGrid.setWidget(i, 3, deleteButton);

				questionMap.put(editButton, q);
				questionMap.put(deleteButton, q);
				questionMap.put(moveUp, q);
				questionMap.put(moveDown, q);

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
			if (ClickMode.EDIT == mode || ClickMode.OPEN == mode) {
				openPage(QuestionEditWidget.class, getContextBundle(true));
			} else if (ClickMode.DELETE == mode) {
				deleteQuestion(selectedQuestion);
			} else if (ClickMode.MOVE_DOWN == mode) {

			} else if (ClickMode.MOVE_UP == mode) {
				
			}		
	}
	
	private void deleteQuestion(QuestionDto question){
		setWorking(true);
		Integer key = null;
		for(Entry<Integer,QuestionDto> entry:questionGroup.getQuestionMap().entrySet()){
			if(entry.getValue().getKeyId().equals(question.getKeyId())){
				key = entry.getKey();
				break;
			}
		}
		if(key != null){
			questionGroup.getQuestionMap().remove(key);
			surveyService.deleteQuestion(question, question.getQuestionGroupId(), new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
					setWorking(false);
					MessageDialog errDia = new MessageDialog("Error","Could not delete question: "+caught.getLocalizedMessage());
					errDia.showCentered();					
				}

				@Override
				public void onSuccess(String result) {
					setWorking(false);
					loadData(questionGroup);
				}
			});
		}
	}

	@Override
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		if (selectedQuestion != null && doPopulation) {
			bundle.put(BundleConstants.QUESTION_KEY, selectedQuestion);
		}
		return bundle;
	}

	@Override
	public void persistContext(CompletionListener listener) {
		if (listener != null) {
			listener.operationComplete(true, getContextBundle(true));
		}
	}
}