package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityService;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardContainerDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerService;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
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
	private DateTimeFormat dateFormat = null;
	private static Boolean errorMode = null;
	private StandardScoringManagerServiceAsync svc;
	private CommunityServiceAsync communitySvc;
	private static final Integer PAGE_SIZE = 40;
	private TreeMap<String, String> objectAttributes = null;
	private VerticalPanel mainVPanel = new VerticalPanel();
	ScrollPanel scrollP = new ScrollPanel();
	private AccessPointManagerServiceAsync apSvc = null;
	private ArrayList<CountryDto> countryCodesList = null;
	private ListBox scoreBucketsBox = new ListBox();

	private static final String DEFAULT_SORT_FIELD = "globalStandard";

	private VerticalPanel contentPane;
	private Button addNewButton = new Button(TEXT_CONSTANTS.add());
	private VerticalPanel tablePanel = new VerticalPanel();
	private HorizontalPanel bucketsHPanel = new HorizontalPanel();
	private Button addScoringBucket = new Button();
	private Label bucketsBoxLbl = new Label("Enter new bucket name");
	private TextBox bucketsEntryBox = new TextBox();
	private Button saveBucket = new Button("Save New Bucket");
	private FlexTable standardTable = new FlexTable();

	@Override
	public int getFullscreenWidth() {
		return 1900;
	}

	private Widget buildHeader() {
		Grid grid = new Grid(2, 2);
		// configureSearchRibbon();
		grid.setWidget(0, 0, mainVPanel);
		return grid;
	}

	public StandardScoringManagerPortlet(UserDto user) {
		super(title, true, false, false, 1900, FULLSCREEN_HEIGHT, user);
		init();
	}

	private void init() {
		svc = GWT.create(StandardScoringManagerService.class);
		apSvc = GWT.create(AccessPointManagerService.class);
		communitySvc = GWT.create(CommunityService.class);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();

		dateFormat = DateTimeFormat.getShortDateFormat();
		contentPane.add(header);
		setContent(contentPane);
		errorMode = false;

		loadCountries();

		bucketsHPanel.add(scoreBucketsBox);
		addScoringBucket.setText("Add Bucket");
		addScoringBucket.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				scoreBucketsBox.setEnabled(false);
				addScoringBucket.setEnabled(false);

				bucketsEntryBox.addKeyPressHandler(new KeyPressHandler() {
					@Override
					public void onKeyPress(KeyPressEvent event) {
						TextBox bucketEntryBox = (TextBox) event.getSource();
						if (bucketEntryBox.getText().trim().length() > 0) {
							saveBucket.setEnabled(true);
						} else {
							saveBucket.setEnabled(false);
						}
					}
				});

				bucketsHPanel.add(bucketsBoxLbl);
				bucketsHPanel.add(bucketsEntryBox);
			}
		});
		bucketsHPanel.add(addScoringBucket);
		tablePanel.add(bucketsHPanel);

		tablePanel.add(standardTable);
		tablePanel.add(addNewButton);

	}

	private void loadStandardTable() {
		String currSelectedStandard = scoreBucketsBox.getValue(scoreBucketsBox
				.getSelectedIndex());
		if (currSelectedStandard != null) {
			svc.listStandardContainer(currSelectedStandard,
					new AsyncCallback<ArrayList<StandardContainerDto>>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(
								ArrayList<StandardContainerDto> result) {
							addHeaderRow();
							Integer row = 1;
							for (StandardContainerDto item : result) {
								
								row++;
							}
						}
					});
		}
	}

	private void addHeaderRow() {
		// TODO Auto-generated method stub

	}

	private void loadStandardScoreBuckets() {
		this.scoreBucketsBox.addItem("WaterPointLevelOfService",
				"Water Point Level Of Service");
		this.scoreBucketsBox.addItem("WaterPointSustainability",
				"Water Point Sustainability");
		this.scoreBucketsBox.addItem("PublicInstitutionLevelOfService",
				"Public Institution Level Of Service");
		this.scoreBucketsBox.addItem("PublicInstitutionSustainability",
				"Public Institution Sustainability");
		this.scoreBucketsBox.setSelectedIndex(0);
	}

	private void loadCountries() {
		communitySvc.listCountries(new AsyncCallback<CountryDto[]>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(CountryDto[] result) {
				if (countryCodesList == null)
					countryCodesList = new ArrayList<CountryDto>();
				for (CountryDto item : result) {
					countryCodesList.add(item);
				}
				loadAttributes();

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

	private static Integer selectedRow = null;

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
		} else if (type.equals("Distance")) {
			operators.addItem("<=");
			operators.addItem("<");
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
		criteriaType.addItem("Distance", "Distance");
		criteriaType.setSelectedIndex(0);
		criteriaType.setTitle(row + "|" + col);
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

				if (target.getSelectedIndex() > 0) {
					String posOper = null;
					String negOper = null;
					if (currentItem != null) {
						if (currentItem.getPositiveOperator() != null)
							posOper = currentItem.getPositiveOperator();
						if (currentItem.getNegativeOperator() != null)
							negOper = currentItem.getNegativeOperator();
					}
					if (target.getSelectedIndex() == 1) {
						loadCriteriaOperators(grid, row, column + 2, "String",
								posOper);
						loadCriteriaOperators(grid, row, column + 6, "String",
								negOper);
					} else if (target.getSelectedIndex() == 2) {
						loadCriteriaOperators(grid, row, column + 2, "Number",
								posOper);
						loadCriteriaOperators(grid, row, column + 6, "Number",
								negOper);
					} else if (target.getSelectedIndex() == 3) {
						loadCriteriaOperators(grid, row, column + 2,
								"NotNumber", posOper);
						loadCriteriaOperators(grid, row, column + 6,
								"NotNumber", negOper);
					} else if (target.getSelectedIndex() == 4) {
						loadCriteriaOperators(grid, row, column + 2,
								"Distance", posOper);
						loadCriteriaOperators(grid, row, column + 6,
								"Distance", negOper);
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
				loadCriteriaOperators(grid, row, column + 6, "Number",
						currentItem.getNegativeOperator());
			} else {
				loadCriteriaOperators(grid, row, column + 2, "NotNumber",
						currentItem.getPositiveOperator());
				loadCriteriaOperators(grid, row, column + 6, "NotNumber",
						currentItem.getNegativeOperator());
			}
		}
	}

	private static StandardScoringDto currentItem;

	private String cursorString = "all";

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
						loadStandardScoreBuckets();
					}
				});

		return null;
	}

	@Override
	public DataTableHeader[] getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bindRow(Grid grid, StandardScoringDto item, int row) {
		// TODO Auto-generated method stub

	}

}
