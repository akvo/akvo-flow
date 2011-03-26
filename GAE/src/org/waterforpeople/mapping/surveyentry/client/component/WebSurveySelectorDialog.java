package org.waterforpeople.mapping.surveyentry.client.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.util.client.CompletionListener;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.framework.gwt.util.client.WidgetDialog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Widget for selecting a survey from a prefetched list
 * 
 * @author Christopher Fagiani
 * 
 */
public class WebSurveySelectorDialog extends WidgetDialog implements
		DataTableBinder<SurveyDto>, DataTableListener<SurveyDto> {

	protected static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);
	public static final String SELECTED_SURVEY_KEY = "SELECTEDSURVEY";
	private static final Integer PAGE_SIZE = 10;
	private static final String TITLE = TEXT_CONSTANTS.selectSurvey();
	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader(TEXT_CONSTANTS.surveyGroup(), "path", false),
			new DataTableHeader(TEXT_CONSTANTS.survey(), "name", false) };
	private static final String DEFAULT_SORT_FIELD = "name";

	private PaginatedDataTable<SurveyDto> surveyTable;
	private Panel mainPanel;
	private SurveyDto selectedSurvey;
	private ArrayList<SurveyDto> surveys;

	public WebSurveySelectorDialog(ArrayList<SurveyDto> surveys,
			CompletionListener listener) {
		super(TITLE, null, true,listener);
		mainPanel = new VerticalPanel();

		this.surveys = surveys;

		surveyTable = new PaginatedDataTable<SurveyDto>(DEFAULT_SORT_FIELD,
				this, this, true);
		mainPanel
				.add(ViewUtil
						.initLabel(TEXT_CONSTANTS.selectSurveyClick()));
		mainPanel.add(surveyTable);
		setContentWidget(mainPanel);
		requestData(null, false);
	}

	@Override
	public void bindRow(final Grid grid, final SurveyDto dto, int row) {

		grid.setWidget(row, 0, ViewUtil.initLabel(dto.getPath()));
		grid.setWidget(row, 1, ViewUtil.initLabel(dto.getName()));

	}

	@Override
	public DataTableHeader[] getHeaders() {
		return HEADERS;
	}

	@Override
	public void onItemSelected(SurveyDto item) {
		selectedSurvey = item;
		Map<String, Object> payload = new HashMap<String, Object>();
		payload.put(SELECTED_SURVEY_KEY, selectedSurvey);
		hide(true);
		notifyListener(true, payload);
	}

	@Override
	public void requestData(String cursor, final boolean isResort) {
		surveyTable.bindData(surveys, null, true, false);
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}

}
