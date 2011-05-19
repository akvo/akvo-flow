package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerService;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class StandardScoringManagerPortlet extends UserAwarePortlet implements
		DataTableBinder<StandardScoringDto>,
		DataTableListener<StandardScoringDto> {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static String title = "Scoring Manager";
	private static Boolean scrollable = true;
	private static Boolean configurable = false;
	private static Boolean snapable = false;
	private static Integer width = 800;
	private static Integer height = 1024;
	private DateTimeFormat dateFormat = null;
	private static Boolean errorMode = null;
	private StandardScoringManagerServiceAsync svc;
	private static final Integer PAGE_SIZE = 40;

	PaginatedDataTable<StandardScoringDto> scoringTable;
	private VerticalPanel mainVPanel = new VerticalPanel();

	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader(TEXT_CONSTANTS.id(), "key", true),
			new DataTableHeader(TEXT_CONSTANTS.globalStandard(),
					"globalStandard", true),
			new DataTableHeader(TEXT_CONSTANTS.countryCode(), "countryCode",
					true),
			new DataTableHeader(TEXT_CONSTANTS.subValue(), "subValue", true),
			new DataTableHeader(TEXT_CONSTANTS.pointType(), "pointType", true),
			new DataTableHeader(TEXT_CONSTANTS.evaluateField(),
					"evaluateField", true),
			new DataTableHeader(TEXT_CONSTANTS.criteriaType(), "criteriaType",
					true),
			new DataTableHeader(TEXT_CONSTANTS.positiveCriteria(),
					"positiveCriteria", true),
			new DataTableHeader(TEXT_CONSTANTS.positiveOperator(),
					"positiveOperator", true),
			new DataTableHeader(TEXT_CONSTANTS.negativeCriteria(),
					"negativeCriteria", true),
			new DataTableHeader(TEXT_CONSTANTS.negativeOperator(),
					"negativeOperator", true),
			new DataTableHeader(TEXT_CONSTANTS.effectiveStartDate(),
					"effectiveStartDate", true),
			new DataTableHeader(TEXT_CONSTANTS.effectiveEndDate(),
					"effectiveEndDate", true),

			new DataTableHeader(TEXT_CONSTANTS.editDelete()) };
	private static final String DEFAULT_SORT_FIELD = "globalStandard";

	private VerticalPanel contentPane;

	@Override
	public DataTableHeader[] getHeaders() {
		return HEADERS;
	}

	public StandardScoringManagerPortlet(String title, boolean scrollable,
			boolean configurable, boolean snapable, int width, int height,
			UserDto user) {
		super(title, scrollable, configurable, snapable, width, height, user);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		scoringTable = new PaginatedDataTable<StandardScoringDto>(
				DEFAULT_SORT_FIELD, this, this, true);
		dateFormat = DateTimeFormat.getShortDateFormat();
		contentPane.add(header);
		setContent(contentPane);
		errorMode = false;
		svc = GWT.create(StandardScoringManagerService.class);
		scoringTable.setVisible(false);
		mainVPanel.add(scoringTable);
		requestScoringData();
	}

	private Widget buildHeader() {
		Grid grid = new Grid(2, 2);
		// configureSearchRibbon();
		grid.setWidget(0, 0, mainVPanel);
		return grid;
	}

	public StandardScoringManagerPortlet(UserDto user) {
		super(title, scrollable, configurable, snapable, width, height, user);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		scoringTable = new PaginatedDataTable<StandardScoringDto>(
				DEFAULT_SORT_FIELD, this, this, true);
		dateFormat = DateTimeFormat.getShortDateFormat();
		contentPane.add(header);
		setContent(contentPane);
		errorMode = false;
		svc = GWT.create(StandardScoringManagerService.class);
		scoringTable.setVisible(false);
		mainVPanel.add(scoringTable);
		requestScoringData();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onItemSelected(StandardScoringDto item) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestData(String cursor, boolean isResort) {
		// TODO Auto-generated method stub

	}

	@Override
	public void bindRow(Grid grid, StandardScoringDto item, int row) {
		ListBox global = new ListBox();
		global.addItem("Global");
		global.addItem("Local");
		if (item.getGlobalStandard()) {
			global.setSelectedIndex(0);
		} else {
			global.setSelectedIndex(1);
		}
		grid.setWidget(row, 0, global);

		TextBox country = new TextBox();
		if (item.getCountryCode() != null) {
			country.setText(item.getCountryCode());
		}
		grid.setWidget(row, 1, country);

		TextBox subValue = new TextBox();
		if(item.getSubValue()!=null){
			subValue.setText(item.getSubValue());
		}
		grid.setWidget(row, 2, subValue);
		
		ListBox pointType = new ListBox();
		pointType.addItem("Water Point");
		pointType.addItem("Sanitation");
		pointType.addItem("House Hold");
		pointType.addItem("Public Institution");
		if(item.getPointType()!=null){
			if(item.getPointType().equals("WATER_POINT")){
				pointType.setSelectedIndex(0);
			}
			//ToDo complete
		}
		
		ListBox fields = new ListBox();
		
		
	}

	@Override
	public Integer getPageSize() {
		// TODO Auto-generated method stub
		return PAGE_SIZE;
	}

	private String cursorString = "all";

	private void requestScoringData() {
		svc.listStandardScoring(
				cursorString,
				new AsyncCallback<ResponseDto<ArrayList<StandardScoringDto>>>() {
					Boolean isNew = false;
					Boolean isResort = false;

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(
							ResponseDto<ArrayList<StandardScoringDto>> result) {
						scoringTable.bindData(result.getPayload(),
								result.getCursorString(), isNew, isResort);
						scoringTable.setVisible(true);
					}
				});

	}
}
