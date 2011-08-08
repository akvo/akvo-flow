package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.ListBasedWidget;
import com.gallatinsystems.framework.gwt.component.PageController;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.wizard.client.ContextAware;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
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

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private SurveyServiceAsync surveyService;
	private Map<Widget, SurveyGroupDto> groupMap;
	private Map<String, Object> bundle;
	private String currentCursor;
	private Grid dataGrid;

	public SurveyGroupListWidget(PageController controller) {
		super(controller);
		currentCursor = null;
		bundle = new HashMap<String, Object>();
		surveyService = GWT.create(SurveyService.class);
		groupMap = new HashMap<Widget, SurveyGroupDto>();
		loadData();
	}

	public void loadData() {
		if (dataGrid != null) {
			dataGrid.removeFromParent();
		}
		surveyService.listSurveyGroups(currentCursor, false, false, false,
				new AsyncCallback<ResponseDto<ArrayList<SurveyGroupDto>>>() {

					@Override
					public void onFailure(Throwable caught) {
						toggleLoading(false);
					}

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<SurveyGroupDto>> response) {
						ArrayList<SurveyGroupDto> result = response
								.getPayload();
						setCursor(response.getCursorString());
						toggleLoading(false);
						if (result != null && result.size() > 0) {
							dataGrid = new Grid(result.size() + 1, 2);
							for (int i = 0; i < result.size(); i++) {
								Label l = createListEntry(result.get(i)
										.getDisplayName());
								dataGrid.setWidget(i, 0, l);
								groupMap.put(l, result.get(i));
								Button b = createButton(ClickMode.EDIT,
										TEXT_CONSTANTS.edit());
								groupMap.put(b, result.get(i));
								dataGrid.setWidget(i, 1, b);
							}
							HorizontalPanel navPanel = new HorizontalPanel();
							if (getCurrentPage() > 0) {
								navPanel.add(getPreviousButtion());
							}
							if (result.size() > 0
									&& response.getCursorString() != null) {
								navPanel.add(getNextButton());
							}
							dataGrid.setWidget(result.size(), 0, navPanel);

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
		} else if (ClickMode.NEXT_PAGE == mode) {
			loadDataPage(1);
			currentCursor = getCursor(getCurrentPage() - 1);
			loadData();
		} else if (ClickMode.PREV_PAGE == mode) {
			loadDataPage(-1);
			currentCursor = getCursor(getCurrentPage() - 1);
			loadData();
		}
	}

	@Override
	public void setContextBundle(Map<String, Object> bundle) {
		this.bundle = bundle;
		flushContext();
	}

	@Override
	public Map<String, Object> getContextBundle(boolean doPopulation) {
		return bundle;
	}

	@Override
	public void persistContext(String buttonText,CompletionListener listener) {
		if (listener != null) {
			listener.operationComplete(true, getContextBundle(true));
		}
	}

	@Override
	public void flushContext() {
		if (bundle != null) {
			bundle.remove(BundleConstants.SURVEY_GROUP_KEY);
		}

	}

}