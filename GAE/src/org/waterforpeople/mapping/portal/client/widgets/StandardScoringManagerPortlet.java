package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoreBucketDto;
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
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

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
	private TreeMap<String, String> objectAttributes = null;
	PaginatedDataTable<StandardScoringDto> scoringTable;
	private VerticalPanel mainVPanel = new VerticalPanel();
	ScrollPanel scrollP = new ScrollPanel();
	private AccessPointManagerServiceAsync apSvc = null;
	private ArrayList<String> countryCodesList = null;
	private ListBox scoreBucketsBox = new ListBox();

	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader(TEXT_CONSTANTS.globalStandard(),
					"globalStandard", true),
			new DataTableHeader(TEXT_CONSTANTS.countryCode(), "countryCode",
					true),
			new DataTableHeader(TEXT_CONSTANTS.subValue(), "subValue", true),
			new DataTableHeader(TEXT_CONSTANTS.pointType(), "pointType", true),
			new DataTableHeader(TEXT_CONSTANTS.description(), "displayName",
					true),
			new DataTableHeader(TEXT_CONSTANTS.evaluateField(),
					"evaluateField", true),
			new DataTableHeader(TEXT_CONSTANTS.criteriaType(), "criteriaType",
					true),
			new DataTableHeader(TEXT_CONSTANTS.positiveCriteria(),
					"positiveCriteria", true),
			new DataTableHeader(TEXT_CONSTANTS.positiveOperator(),
					"positiveOperator", true),
			new DataTableHeader(TEXT_CONSTANTS.positiveScore(),
					"positiveCriteria", true),
			new DataTableHeader(TEXT_CONSTANTS.negativeCriteria(),
					"negativeCriteria", true),
			new DataTableHeader(TEXT_CONSTANTS.negativeOperator(),
					"negativeOperator", true),
			new DataTableHeader(TEXT_CONSTANTS.negativeScore(),
					"negativeScore", true),
			new DataTableHeader(TEXT_CONSTANTS.negativeOverride(),
					"negativeOverride", true),
			new DataTableHeader(TEXT_CONSTANTS.effectiveStartDate(),
					"effectiveStartDate", true),
			new DataTableHeader(TEXT_CONSTANTS.effectiveEndDate(),
					"effectiveEndDate", true),
			new DataTableHeader("ID", "key", false),
			new DataTableHeader(TEXT_CONSTANTS.editDelete()) };
	private static final String DEFAULT_SORT_FIELD = "globalStandard";

	private VerticalPanel contentPane;
	private Button addNewButton = new Button(TEXT_CONSTANTS.add());
	private VerticalPanel tablePanel = new VerticalPanel();

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
		svc = GWT.create(StandardScoringManagerService.class);
		apSvc = GWT.create(AccessPointManagerService.class);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		scoringTable = new PaginatedDataTable<StandardScoringDto>(
				DEFAULT_SORT_FIELD, this, this, true);
		dateFormat = DateTimeFormat.getShortDateFormat();
		contentPane.add(header);
		setContent(contentPane);
		errorMode = false;
		loadAttributes();
		loadStandardScoreBuckets();
		loadCountries();
		scoringTable.setVisible(false);
		tablePanel.add(scoreBucketsBox);
		tablePanel.add(scoringTable);
		tablePanel.add(addNewButton);
		addNewButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				scoringTable.addNewRow();
			}
		});
		scrollP.add(tablePanel);
		scrollP.setAlwaysShowScrollBars(true);
		scrollP.setWidth("1800px");
		mainVPanel.add(scrollP);

	}

	private void loadStandardScoreBuckets() {
		svc.listStandardScoreBuckets(new AsyncCallback<ArrayList<StandardScoreBucketDto>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ArrayList<StandardScoreBucketDto> result) {
				if (result != null) {
					for (StandardScoreBucketDto item : result) {
						scoreBucketsBox.addItem(item.getName(), item.getKeyId()
								.toString());
					}
					scoreBucketsBox.setSelectedIndex(0);
					Long scoreBucketKey = Long.parseLong(scoreBucketsBox
							.getValue(scoreBucketsBox.getSelectedIndex()));
					requestScoringData(scoreBucketKey);
				}
			}
		});
		scoreBucketsBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				ListBox scoreBuckets = (ListBox) event.getSource();
				Long scoreBucketKey = Long.parseLong(scoreBuckets
						.getValue(scoreBuckets.getSelectedIndex()));
				requestScoringData(scoreBucketKey);

			}
		});

	}

	private void loadCountries() {
		apSvc.listCountryCodes(new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(List<String> result) {
				countryCodesList = (ArrayList) result;

			}
		});
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
		if (item != null) {
			currentItem = item;
		} else {
			currentItem = new StandardScoringDto();
		}
		ListBox global = new ListBox();
		global.addItem("Global");
		global.addItem("Local");
		if (item != null && item.getGlobalStandard()) {
			global.setSelectedIndex(0);
		} else {
			global.setSelectedIndex(1);
		}
		grid.setWidget(row, 0, global);

		ListBox country = new ListBox();
		int i = 1;
		country.addItem(" ");
		country.setSelectedIndex(0);
		for (String countryCode : countryCodesList) {
			country.addItem(countryCode);
			if (item != null && item.getCountryCode() != null) {
				if (countryCode.equals(item.getCountryCode())) {
					country.setSelectedIndex(i);
				}
			}
			i++;
		}

		grid.setWidget(row, 1, country);

		TextBox subValue = new TextBox();
		if (item != null && item.getSubValue() != null) {
			subValue.setText(item.getSubValue());
		}
		grid.setWidget(row, 2, subValue);

		ListBox pointType = new ListBox();
		pointType.addItem(" ");
		pointType.addItem("Water Point");
		pointType.addItem("Sanitation");
		pointType.addItem("Household");
		pointType.addItem("Public Institution");
		pointType.setSelectedIndex(0);
		if (item != null && item.getPointType() != null) {
			if (item.getPointType().equals("WATER_POINT")) {
				pointType.setSelectedIndex(1);
			} else if (item.getPointType().equals("SANITATION")) {
				pointType.setSelectedIndex(2);
			} else if (item.getPointType().equals("HOUSEHOLD")) {
				pointType.setSelectedIndex(3);
			} else {
				pointType.setSelectedIndex(4);
			}
			// ToDo complete
		}
		grid.setWidget(row, 3, pointType);

		// DisplayName
		TextBox displayName = new TextBox();
		if (item != null && item.getDisplayName() != null) {
			displayName.setText(item.getDisplayName());
		}
		grid.setWidget(row, 4, displayName);

		// evalField
		ListBox fields = new ListBox();
		fields.addItem(" ");
		fields.setSelectedIndex(0);
		int ifield = 1;
		if (objectAttributes.size() > 0) {
			for (Entry<String, String> field : objectAttributes.entrySet()) {
				fields.addItem(field.getKey(), field.getValue());
				if (item != null && item.getEvaluateField() != null) {
					if (item.getEvaluateField().toLowerCase().trim()
							.equals(field.getKey().toLowerCase())) {
						fields.setSelectedIndex(i);
					}
				}
				++ifield;
			}
		}
		grid.setWidget(row, 5, fields);
		// criteriatype

		configureCriteriaTypeBox(item, grid, row, 6);

		// positivecriteria
		TextBox positiveCriteria = new TextBox();
		if (item != null && item.getPositiveCriteria() != null) {
			positiveCriteria.setText(item.getPositiveCriteria());
		}
		grid.setWidget(row, 7, positiveCriteria);
		// positiveoperator

		TextBox positiveScore = new TextBox();
		if (item != null && item.getPositiveScore() != null) {
			positiveScore.setText(item.getPositiveScore().toString());
		}
		grid.setWidget(row, 9, positiveScore);

		// negativecriteria
		TextBox negativeCriteria = new TextBox();
		if (item != null && item.getNegativeCriteria() != null) {
			negativeCriteria.setText(item.getNegativeCriteria());
		}
		grid.setWidget(row, 10, negativeCriteria);

		TextBox negativeScore = new TextBox();
		if (item != null && item.getNegativeScore() != null) {
			negativeScore.setText(item.getNegativeScore().toString());
		}
		grid.setWidget(row, 12, negativeScore);

		// negativeOverride
		ListBox negativeOverride = new ListBox();
		negativeOverride.addItem("No");
		negativeOverride.addItem("Yes");
		negativeOverride.setSelectedIndex(0);
		if (item != null && item.getNegativeOverride() != null) {
			if (item.getNegativeOverride()) {
				negativeOverride.setSelectedIndex(1);
			}
		}
		grid.setWidget(row, 13, negativeOverride);

		// effectivestartdate
		DateBox effectiveStartDate = new DateBox();
		if (item != null && item.getEffectiveStartDate() != null) {
			effectiveStartDate.setValue(item.getEffectiveStartDate());
		}
		grid.setWidget(row, 14, effectiveStartDate);
		// effectiveenddate
		DateBox effectiveEndDate = new DateBox();
		if (item != null && item.getEffectiveEndDate() != null) {
			effectiveEndDate.setValue(item.getEffectiveEndDate());
		}
		
		grid.setWidget(row, 15, effectiveEndDate);
		
		TextBox keyBox = new TextBox();
		if(item!=null&&item.getKeyId()!=null){
			keyBox.setText(item.getKeyId().toString());
		}
		grid.setWidget(row, 16, keyBox);
		HorizontalPanel hpanel = new HorizontalPanel();
		Button saveButton = new Button();
		saveButton.setText(TEXT_CONSTANTS.save());
		saveButton.setTitle(String.valueOf(row));
		Button deleteButton = new Button();
		deleteButton.setText(TEXT_CONSTANTS.delete());

		Long buttonKey = null;
		if (item != null) {
			buttonKey = item.getKeyId();
		} else {
			buttonKey = -1L;
		}

		deleteButton.setTitle(String.valueOf(row) + "|" + buttonKey);
		hpanel.add(saveButton);
		hpanel.add(deleteButton);
		grid.setWidget(row, 17, hpanel);

		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Integer row = Integer.parseInt(((Button) event.getSource())
						.getTitle());
				StandardScoringDto ssDto = formStandardScoringDto(row);
				svc.save(ssDto, new AsyncCallback<StandardScoringDto>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(TEXT_CONSTANTS.saveFailed());
					}

					@Override
					public void onSuccess(StandardScoringDto result) {
						
					}
				});
			}

		});

		deleteButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Button deleteButton = (Button) event.getSource();
				String title = deleteButton.getTitle();
				Integer row = Integer.parseInt(title.split("\\|")[0]);
				Long keyId = Long.parseLong(title.split("\\|")[1]);
				selectedRow = row;
				svc.delete(keyId, new AsyncCallback() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Object result) {
						Grid grid = scoringTable.getGrid();
						grid.removeRow(selectedRow);
						selectedRow = null;
					}
				});
			}
		});
	}

	private static Integer selectedRow = null;

	private StandardScoringDto formStandardScoringDto(Integer row) {
		StandardScoringDto item = new StandardScoringDto();
		Grid grid = scoringTable.getGrid();

		Long scoreBucketKey = Long.parseLong(scoreBucketsBox
				.getValue(scoreBucketsBox.getSelectedIndex()));
		if (scoreBucketKey != null) {
			item.setScoreBucketId(scoreBucketKey);
		}

		ListBox global = (ListBox) grid.getWidget(row, 0);
		if (global.getSelectedIndex() > 0) {
			item.setGlobalStandard(false);
		} else {
			item.setGlobalStandard(true);
		}

		ListBox country = (ListBox) grid.getWidget(row, 1);
		if (country.getSelectedIndex() > 0) {
			item.setCountryCode(country.getItemText(country.getSelectedIndex()));
		}

		TextBox subValue = (TextBox) grid.getWidget(row, 2);
		if (subValue.getText().trim().length() > 0) {
			item.setSubValue(subValue.getText());
		}
		ListBox pointType = (ListBox) grid.getWidget(row, 3);
		if (pointType.getSelectedIndex() == 1) {
			item.setPointType("WATER_POINT");
		}

		TextBox displayName = (TextBox) grid.getWidget(row, 4);
		if (displayName.getText().trim().length() > 0) {
			item.setDisplayName(displayName.getText().trim());
		}

		ListBox fields = (ListBox) grid.getWidget(row, 5);
		if (fields.getSelectedIndex() > 0) {
			item.setEvaluateField(fields.getValue(fields.getSelectedIndex()));
		}
		ListBox criteriaType = (ListBox) grid.getWidget(row, 6);
		if (criteriaType.getSelectedIndex() > 0) {
			if (criteriaType.getSelectedIndex() == 1) {
				// text
				item.setCriteriaType("String");
			} else if (criteriaType.getSelectedIndex() == 2) {
				// number
				item.setCriteriaType("Number");
			} else if (criteriaType.getSelectedIndex() == 3) {
				// true/false
				item.setCriteriaType("Boolean");
			}
		}
		TextBox positiveCriteria = (TextBox) grid.getWidget(row, 7);
		if (positiveCriteria.getText().trim().length() > 0) {
			item.setPositiveCriteria(positiveCriteria.getText().trim());
		}
		ListBox positiveOp = (ListBox) grid.getWidget(row, 8);
		if (positiveOp.getSelectedIndex() > 0) {
			String op = positiveOp.getItemText(positiveOp.getSelectedIndex());
			item.setPositiveOperator(op);
		}
		TextBox positiveScore = (TextBox) grid.getWidget(row, 9);
		if (positiveScore.getText().trim().length() > 0) {
			Integer score = Integer.parseInt(positiveScore.getText().trim());
			item.setPositiveScore(score);
		}
		TextBox negativeCriteria = (TextBox) grid.getWidget(row, 10);
		if (negativeCriteria.getText().trim().length() > 0) {
			item.setNegativeCriteria(negativeCriteria.getText());
		}
		ListBox negativeOp = (ListBox) grid.getWidget(row, 11);
		if (negativeOp.getSelectedIndex() > 0) {
			item.setNegativeOperator(negativeOp.getItemText(negativeOp
					.getSelectedIndex()));
		}

		TextBox negativeScore = (TextBox) grid.getWidget(row, 12);
		if (negativeScore.getText().trim().length() > 0) {
			Integer score = Integer.parseInt(negativeScore.getText().trim());
			item.setNegativeScore(score);
		}

		ListBox negativeOverride = (ListBox) grid.getWidget(row, 13);
		if (negativeOverride.getSelectedIndex() == 0) {
			item.setNegativeOverride(false);
		} else {
			item.setNegativeOverride(true);
		}

		// effectivestartdate
		DateBox effectiveStartDate = (DateBox) grid.getWidget(row, 14);
		if (effectiveStartDate.getValue() != null) {
			item.setEffectiveStartDate(effectiveStartDate.getValue());
		}
		// effectiveenddate
		DateBox effectiveEndDate = (DateBox) grid.getWidget(row, 15);
		if (effectiveEndDate.getValue() != null) {
			item.setEffectiveEndDate(effectiveEndDate.getValue());
		}
		TextBox keyBox = (TextBox)grid.getWidget(row,16);
		if(keyBox.getValue()!=null){
			item.setKeyId(Long.parseLong(keyBox.getText()));
		}
		return item;
	}

	private void loadCriteriaOperators(Grid grid, Integer row, Integer column,
			String type, String currentSelection) {
		ListBox operators = new ListBox();
		operators.addItem(" ");
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
		operators.setSelectedIndex(0);
		if (currentSelection != null) {
			for (int i = 1; i < operators.getItemCount(); i++) {
				if (operators.getItemText(i).equals(currentSelection)) {
					operators.setSelectedIndex(i);
					break;
				}
			}
		}
		grid.setWidget(row, column, operators);
	}

	@Override
	public Integer getPageSize() {
		// TODO Auto-generated method stub
		return PAGE_SIZE;
	}

	private void configureCriteriaTypeBox(StandardScoringDto item, Grid grid,
			Integer row, Integer col) {
		ListBox criteriaType = new ListBox();
		criteriaType.addItem(" ");
		criteriaType.addItem("Text", "String");
		criteriaType.addItem("Number", "Number");
		criteriaType.addItem("True/False", "Boolean");
		criteriaType.setSelectedIndex(0);
		criteriaType.setTitle(row + "|" + 6);
		if (item != null && item.getCriteriaType() != null) {
			ViewUtil.setListboxSelection(criteriaType, item.getCriteriaType());
			setupOperatorListBox(item, grid, row, col);
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
						loadCriteriaOperators(grid, row, column + 2, "String",
								currentItem.getPositiveOperator());
						loadCriteriaOperators(grid, row, column + 5, "String",
								currentItem.getNegativeOperator());
					} else if (target.getSelectedIndex() == 2) {
						loadCriteriaOperators(grid, row, column + 2, "Number",
								currentItem.getPositiveOperator());
						loadCriteriaOperators(grid, row, column + 5, "Number",
								currentItem.getNegativeOperator());
					} else {
						loadCriteriaOperators(grid, row, column + 2,
								"NotNumber", currentItem.getPositiveOperator());
						loadCriteriaOperators(grid, row, column + 5,
								"NotNumber", currentItem.getNegativeOperator());
					}
				}
			}

		});
		grid.setWidget(row, col, criteriaType);
	}

	private void setupOperatorListBox(StandardScoringDto item, Grid grid,
			Integer row, Integer column) {
		if (item.getCriteriaType() != null) {
			if (item.getCriteriaType().equals("Number")) {
				loadCriteriaOperators(grid, row, column + 2, "Number",
						currentItem.getPositiveOperator());
				loadCriteriaOperators(grid, row, column + 5, "Number",
						currentItem.getNegativeOperator());
			} else {
				loadCriteriaOperators(grid, row, column + 2, "NotNumber",
						currentItem.getPositiveOperator());
				loadCriteriaOperators(grid, row, column + 5, "NotNumber",
						currentItem.getNegativeOperator());
			}
		}
	}

	private static StandardScoringDto currentItem;

	private String cursorString = "all";

	private void requestScoringData(Long scoreBucketKey) {
		svc.listStandardScoring(
				scoreBucketKey,
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
		svc.listObjectAttributes(
				"org.waterforpeople.mapping.domain.AccessPoint",
				new AsyncCallback<TreeMap<String, String>>() {

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
					public void onSuccess(TreeMap<String, String> result) {
						objectAttributes = result;

					}
				});

		return null;
	}

}
