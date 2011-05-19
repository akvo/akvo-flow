package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeService;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerService;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class StandardScoringManagerPortlet extends UserAwarePortlet implements
		DataTableBinder<StandardScoringDto>,
		DataTableListener<StandardScoringDto> {

	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	private static String title = "Scoring Manager";
	private static Boolean scrollable = true;
	private static Boolean configurable = false;
	private static Boolean snapable = true;
	private static Integer width = 1024;
	private static Integer height = 768;
	private DateTimeFormat dateFormat = null;
	private static Boolean errorMode = null;
	private StandardScoringManagerServiceAsync svc;
	private static final Integer PAGE_SIZE = 40;
	private ArrayList<String> objectAttributes = new ArrayList<String>();
	PaginatedDataTable<StandardScoringDto> scoringTable;
	private VerticalPanel mainVPanel = new VerticalPanel();
	SpreadsheetMappingAttributeServiceAsync svcAP;
	ScrollPanel scrollP = new ScrollPanel();

	private static final DataTableHeader HEADERS[] = {
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
		init();
	}

	private Widget buildHeader() {
		Grid grid = new Grid(2, 2);
		// configureSearchRibbon();
		grid.setWidget(0, 0, mainVPanel);
		return grid;
	}

	public StandardScoringManagerPortlet(UserDto user) {
		super(title, scrollable, configurable, snapable, width, height, user);
		init();
	}

	private void init() {
		svcAP = (SpreadsheetMappingAttributeServiceAsync) GWT
				.create(SpreadsheetMappingAttributeService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) svcAP;
		endpoint.setServiceEntryPoint("/org.waterforpeople.mapping.portal.portal/spreadsheetattributemapperrpc");
		loadAttributes();
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
		scrollP.add(scoringTable);
		scrollP.setAlwaysShowScrollBars(true);
		scrollP.setWidth("1800px");
		mainVPanel.add(scrollP);
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
		currentItem = item;
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
		if (item.getSubValue() != null) {
			subValue.setText(item.getSubValue());
		}
		grid.setWidget(row, 2, subValue);

		ListBox pointType = new ListBox();
		pointType.addItem("Water Point");
		pointType.addItem("Sanitation");
		pointType.addItem("House Hold");
		pointType.addItem("Public Institution");
		if (item.getPointType() != null) {
			if (item.getPointType().equals("WATER_POINT")) {
				pointType.setSelectedIndex(0);
			}
			// ToDo complete
		}
		grid.setWidget(row, 3, pointType);
		// evalField
		ListBox fields = new ListBox();
		if (objectAttributes.size() > 0) {
			for (String field : objectAttributes) {
				fields.addItem(field);
			}
		}
		grid.setWidget(row, 4, fields);
		// criteriatype

		configureCriteriaTypeBox(item, grid, row, 5);

		// positivecriteria
		TextBox positiveCriteria = new TextBox();
		if (item.getPositiveCriteria() != null) {
			positiveCriteria.setText(item.getPositiveCriteria());
		}
		grid.setWidget(row, 6, positiveCriteria);
		// positiveoperator
		// negativecriteria
		TextBox negativeCriteria = new TextBox();
		if (item.getNegativeCriteria() != null) {
			negativeCriteria.setText(item.getNegativeCriteria());
		}
		grid.setWidget(row, 8, negativeCriteria);

		// effectivestartdate
		DatePicker effectiveStartDate = new DatePicker();
		if (item.getEffectiveStartDate() != null) {
			// set date
		}
		grid.setWidget(row, 10, effectiveStartDate);
		// effectiveenddate
		DatePicker effectiveEndDate = new DatePicker();
		if (item.getEffectiveEndDate() != null) {
			// set date
		}
		grid.setWidget(row, 11, effectiveEndDate);
	}

	private void loadCriteriaOperators(Grid grid, Integer row, Integer column,
			String type, String currentSelection) {
		ListBox operators = new ListBox();
		if (type.equals("Number")) {
			operators.addItem("<=");
			operators.addItem("<");
			operators.addItem("!=");
			operators.addItem("==");
			operators.addItem(">=");
			operators.addItem(">");
		} else {
			operators.addItem("!=");
			operators.addItem("==");
		}
		if (currentSelection != null) {
			for (int i = 0; i < operators.getItemCount(); i++) {
				if (operators.getItemText(i).equals(currentSelection)) {
					operators.setSelectedIndex(i);
					break;
				}
			}
		}
		grid.setWidget(row, column, operators);
		scrollP.setWidth("1600px");
	}

	@Override
	public Integer getPageSize() {
		// TODO Auto-generated method stub
		return PAGE_SIZE;
	}

	private void configureCriteriaTypeBox(StandardScoringDto item, Grid grid,
			Integer row, Integer col) {
		ListBox criteriaType = new ListBox();
		criteriaType.addItem("Text");
		criteriaType.addItem("Number");
		criteriaType.addItem("True/False");
		criteriaType.setTitle(row + "|" + 5);
		if (item.getCriteriaType() != null) {
			if (item.getCriteriaType().equals("String")) {
				criteriaType.setSelectedIndex(0);
			} else if (item.getCriteriaType().equals("Number")) {
				criteriaType.setSelectedIndex(1);
			} else if (item.getCriteriaType().equals("Boolean")) {
				criteriaType.setSelectedIndex(2);
			}
			
			
		}

		criteriaType.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				// set operator choices
				ListBox target = ((ListBox) event.getSource());
				String title = target.getTitle();
				Grid grid = (Grid) target.getParent();
				String[] pos = title.split("\\|");
				Integer row = Integer.parseInt(pos[0]);
				Integer column = Integer.parseInt(pos[1]);

				if (target.getSelectedIndex() > -1) {
					if (target.getSelectedIndex() == 1) {
						loadCriteriaOperators(grid, row, column + 2, "Number",
								currentItem.getPositiveOperator());
						loadCriteriaOperators(grid, row, column + 4, "Number",
								currentItem.getNegativeOperator());
					} else {
						loadCriteriaOperators(grid, row, column + 2,
								"NotNumber", currentItem.getPositiveOperator());
						loadCriteriaOperators(grid, row, column + 4,
								"NotNumber", currentItem.getNegativeOperator());
					}
				}
			}

		});
		grid.setWidget(row, col, criteriaType);
	}

	private static StandardScoringDto currentItem;

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

	private ArrayList<String> loadAttributes() {
		svcAP.listObjectAttributes(null,
				new AsyncCallback<ArrayList<String>>() {
					@Override
					public void onFailure(Throwable caught) {
						MessageDialog errDialog = new MessageDialog(
								TEXT_CONSTANTS.error(), TEXT_CONSTANTS
										.errorTracePrefix()
										+ " "
										+ caught.getLocalizedMessage());
						errDialog.showCentered();
					}

					@Override
					public void onSuccess(ArrayList<String> result) {
						objectAttributes = result;

					}

				});
		return null;
	}

}
