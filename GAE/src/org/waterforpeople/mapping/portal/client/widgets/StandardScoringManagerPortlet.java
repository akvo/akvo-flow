package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityService;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;
import org.waterforpeople.mapping.app.gwt.client.community.SubCountryDto;
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
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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
	private DateTimeFormat dateFormat = null;
	private static Boolean errorMode = null;
	private StandardScoringManagerServiceAsync svc;
	private CommunityServiceAsync communitySvc;
	private static final Integer PAGE_SIZE = 40;
	private TreeMap<String, String> objectAttributes = null;
	PaginatedDataTable<StandardScoringDto> scoringTable;
	private VerticalPanel mainVPanel = new VerticalPanel();
	ScrollPanel scrollP = new ScrollPanel();
	private AccessPointManagerServiceAsync apSvc = null;
	private ArrayList<CountryDto> countryCodesList = null;
	private ListBox scoreBucketsBox = new ListBox();

	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader(TEXT_CONSTANTS.globalStandard(),
					"globalStandard", true),
			new DataTableHeader(TEXT_CONSTANTS.countryCode(), "countryCode",
					true),
			new DataTableHeader(TEXT_CONSTANTS.pointType(), "pointType", true),
			new DataTableHeader(TEXT_CONSTANTS.criteriaType(), "criteriaType",
					true),
			new DataTableHeader(TEXT_CONSTANTS.description(), "displayName",
					true),
			new DataTableHeader(TEXT_CONSTANTS.evaluateField(),
					"evaluateField", true),
			new DataTableHeader(TEXT_CONSTANTS.positiveCriteria(),
					"positiveCriteria", true),
			new DataTableHeader(TEXT_CONSTANTS.positiveOperator(),
					"positiveOperator", true),
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
	private HorizontalPanel bucketsHPanel = new HorizontalPanel();
	private Button addScoringBucket = new Button();
	private Label bucketsBoxLbl = new Label("Enter new bucket name");
	private TextBox bucketsEntryBox = new TextBox();
	private Button saveBucket = new Button("Save New Bucket");

	@Override
	public int getFullscreenWidth() {
		return 1900;
	}

	@Override
	public DataTableHeader[] getHeaders() {
		return HEADERS;
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
		scoringTable = new PaginatedDataTable<StandardScoringDto>(
				DEFAULT_SORT_FIELD, this, this, true);
		dateFormat = DateTimeFormat.getShortDateFormat();
		contentPane.add(header);
		setContent(contentPane);
		errorMode = false;
		loadAttributes();
		loadCountries();
		scoringTable.setVisible(false);
		bucketsHPanel.add(scoreBucketsBox);
		// loadStandardScoreBuckets();
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
				saveBucket.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						String newBucketName = bucketsEntryBox.getText().trim();
						Boolean didnotfindinexisting = true;
						for (int i = 0; i < scoreBucketsBox.getItemCount(); i++) {
							if (scoreBucketsBox.getItemText(i).trim()
									.toLowerCase()
									.equals(newBucketName.toLowerCase())) {
								didnotfindinexisting = false;
							}
						}
						if (didnotfindinexisting)
							saveScoreBucket(newBucketName);
						else
							Window.alert("Could not add bucket as a bucket with that name already exists");
					}

				});
				bucketsHPanel.add(bucketsBoxLbl);
				bucketsHPanel.add(bucketsEntryBox);
				bucketsHPanel.add(saveBucket);
			}
		});
		bucketsHPanel.add(addScoringBucket);
		tablePanel.add(bucketsHPanel);

		tablePanel.add(scoringTable);
		tablePanel.add(addNewButton);
		addNewButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				scoringTable.addNewRow(null);
			}
		});
		scrollP.add(tablePanel);
		scrollP.setAlwaysShowScrollBars(true);
		scrollP.setWidth("1900px");
		mainVPanel.add(scrollP);

	}

	private void saveScoreBucket(String trim) {
		StandardScoreBucketDto ssbDto = new StandardScoreBucketDto();
		ssbDto.setName(bucketsEntryBox.getText().trim());
		svc.save(ssbDto, new AsyncCallback<StandardScoreBucketDto>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(StandardScoreBucketDto result) {
				scoreBucketsBox.setEnabled(true);
				scoreBucketsBox.addItem(result.getName(), result.getKeyId()
						.toString());
				bucketsBoxLbl.setVisible(false);
				bucketsEntryBox.setVisible(false);
				saveBucket.setVisible(false);
			}
		});

	}

	private void loadStandardScoreBuckets() {
		// svc.listStandardScoreBuckets(new
		// AsyncCallback<ArrayList<StandardScoreBucketDto>>() {
		//
		// @Override
		// public void onFailure(Throwable caught) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public void onSuccess(ArrayList<StandardScoreBucketDto> result) {
		// if (result != null && !result.isEmpty()) {
		// for (StandardScoreBucketDto item : result) {
		// scoreBucketsBox.addItem(item.getName(), item.getKeyId()
		// .toString());
		// }
		// scoreBucketsBox.setSelectedIndex(0);
		// Long scoreBucketKey = Long.parseLong(scoreBucketsBox
		// .getValue(scoreBucketsBox.getSelectedIndex()));
		// requestScoringData(scoreBucketKey);
		// }
		// }
		// });

		scoreBucketsBox.addItem("WaterPointLevelOfService");
		scoreBucketsBox.setSelectedIndex(0);
		requestScoringData(0L);
		scoreBucketsBox.addItem("WaterPointSustainability");
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
	public void bindRow(final Grid grid, StandardScoringDto item, final int row) {
		if (item != null) {
			currentItem = item;
		} else {
			currentItem = null;
		}
		ListBox global = new ListBox();
		global.addItem("Global");
		global.addItem("Local");
		if (item != null && item.getGlobalStandard() != null
				&& item.getGlobalStandard()) {
			global.setSelectedIndex(0);
		} else {
			global.setSelectedIndex(1);
			String selectedCountry = null;

			if (item != null && item.getCountryCode() != null)
				selectedCountry = item.getCountryCode();
			populateCountryCodeControl(grid, selectedCountry, row);
			// ListBox subValue = new ListBox();
			// if (item != null && item.getSubValue() != null) {
			// fetchSubCountries(item.getSubValue(), row, item.getSubValue());
			// }
			// grid.setWidget(row, 1, subValue);
		}
		grid.setWidget(row, 0, global);

		global.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				ListBox global = (ListBox) event.getSource();
				if (global.getSelectedIndex() == 1) {
					populateCountryCodeControl(scoringTable.getGrid(), null,
							row);
				} else {
					Grid grid = scoringTable.getGrid();
					ListBox country = (ListBox) grid.getWidget(row, 1);
					if (country != null)
						grid.remove(country);
					// ListBox subValue = (ListBox) grid.getWidget(row, 2);
					// if (subValue != null)
					// grid.remove(subValue);
				}
			}
		});

		ListBox pointType = new ListBox();
		pointType.addItem(" ");
		pointType.addItem("Water Point");
		pointType.addItem("Sanitation");
		pointType.addItem("Household");
		pointType.addItem("Public Institution");
		pointType.setSelectedIndex(0);
		if (item != null && item.getPointType() != null) {
			if (item.getPointType().equals("WaterPoint")) {
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
		grid.setWidget(row, 2, pointType);
		// Handle Distance
		configureCriteriaTypeBox(item, grid, row, 3);

		if (((ListBox) grid.getWidget(row, 3)).getSelectedIndex() == 4) {
			ListBox locationType = new ListBox();
			locationType.addItem("Urban");
			locationType.addItem("Peri-Urban");
			locationType.addItem("Rural");
			locationType.addItem("Other");
			if (item != null && item.getDisplayName() != null) {
				if (item.getDisplayName().equalsIgnoreCase("URBAN")) {
					locationType.setSelectedIndex(0);
				} else if (item.getDisplayName().equalsIgnoreCase("PERIURBAN")) {
					locationType.setSelectedIndex(1);
				} else if (item.getDisplayName().equals("RURAL")) {
					locationType.setSelectedIndex(2);
				} else if (item.getDisplayName().equals("OTHER")) {
					locationType.setSelectedIndex(3);
				}
			}
			grid.setWidget(row, 4, locationType);
			grid.setWidget(row, 5, new Label("Distance"));
		} else {

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
			int i = 0;
			if (item.getEvaluateField() == null) {
				TextBox distanceTB = new TextBox();
				distanceTB.setText("Distance Rule");
				grid.setWidget(row, 5, distanceTB);
			} else if (objectAttributes.size() > 0) {
				for (Entry<String, String> field : objectAttributes.entrySet()) {
					if (field.getValue() != null)
						fields.addItem(field.getValue(), field.getKey());
					else
						fields.addItem(field.getKey(), field.getKey());
					if (item != null && item.getEvaluateField() != null) {
						if (item.getEvaluateField().toLowerCase().trim()
								.equals(field.getKey().toLowerCase())) {
							fields.setSelectedIndex(ifield);
						}
					}
					++ifield;
				}
				grid.setWidget(row, 5, fields);
			}
		}
		// criteriatype

		// positivecriteria
		VerticalPanel critPanel = new VerticalPanel();

		if (item != null && item.getPositiveCriteria() != null) {
			for (String posCrit : item.getPositiveCriteria()) {
				HorizontalPanel hcritPanel = new HorizontalPanel();
				TextBox positiveCriteria = new TextBox();
				positiveCriteria.setText(posCrit);
				hcritPanel.add(positiveCriteria);
				Button delete = new Button("-");
				hcritPanel.add(delete);
				critPanel.add(hcritPanel);
			}
			Button add = new Button("+");
			critPanel.add(add);
			HandlerRegistration addClickHandler = add
					.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							VerticalPanel critVertPanel = (VerticalPanel) grid
									.getWidget(row, 6);
							HorizontalPanel hpanel = new HorizontalPanel();
							hpanel.add(new TextBox());
							hpanel.add(new Button("-"));
							//critVertPanel.add(hpanel);
							critVertPanel.insert(hpanel, critVertPanel.getWidgetCount()-2);
						}
					});
		}
		grid.setWidget(row, 6, critPanel);
		// loadCriteriaOperators(grid, row, 8,
		// item.getCriteriaType(), item.getPositiveOperator());
		// positiveoperator 8

		// TextBox positiveScore = new TextBox();
		// if (item != null && item.getPositiveScore() != null) {
		// positiveScore.setText(item.getPositiveScore().toString());
		// }
		// grid.setWidget(row, 9, positiveScore);

		// TextBox positiveMessage = new TextBox();
		// if (item != null && item.getPositiveMessage() != null) {
		// positiveMessage.setText(item.getPositiveMessage());
		// }
		// grid.setWidget(row, 10, positiveMessage);

		// effectivestartdate
		DateBox effectiveStartDate = new DateBox();
		if (item != null && item.getEffectiveStartDate() != null) {
			effectiveStartDate.setValue(item.getEffectiveStartDate());
		}
		grid.setWidget(row, 8, effectiveStartDate);
		// effectiveenddate
		DateBox effectiveEndDate = new DateBox();
		if (item != null && item.getEffectiveEndDate() != null) {
			effectiveEndDate.setValue(item.getEffectiveEndDate());
		}

		grid.setWidget(row, 9, effectiveEndDate);

		TextBox keyBox = new TextBox();
		if (item != null && item.getKeyId() != null) {
			keyBox.setText(item.getKeyId().toString());
		}
		grid.setWidget(row, 10, keyBox);
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
		grid.setWidget(row, 11, hpanel);

		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final Integer row = Integer.parseInt(((Button) event
						.getSource()).getTitle());
				StandardScoringDto ssDto = formStandardScoringDto(row);
				svc.save(ssDto, new AsyncCallback<StandardScoringDto>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(TEXT_CONSTANTS.saveFailed());
					}

					@Override
					public void onSuccess(StandardScoringDto result) {
						Grid grid = scoringTable.getGrid();
						TextBox id = (TextBox) grid.getWidget(row, 18);
						id.setText(result.getKeyId().toString());
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
					}

					@Override
					public void onSuccess(Object result) {
						Grid grid = scoringTable.getGrid();
						grid.removeRow(selectedRow);

						for (int i = selectedRow; i < grid.getRowCount(); i++) {
							HorizontalPanel buttonPanel = (HorizontalPanel) grid
									.getWidget(i, 19);
							Button deleteButton = (Button) buttonPanel
									.getWidget(1);
							Long keyId = 0L;
							TextBox keyIdBox = (TextBox) grid.getWidget(i, 18);
							if (keyIdBox != null
									&& keyIdBox.getText().trim().length() > 0) {
								keyId = Long.parseLong(keyIdBox.getText()
										.trim());
							}
							deleteButton.setTitle(String.valueOf(i) + "|"
									+ keyId);
						}
						selectedRow = null;
					}
				});
			}
		});
	}

	private void populateCountryCodeControl(Grid grid,
			final String selectedCountry, final Integer row) {
		ListBox country = new ListBox();
		country.addItem(selectedCountry);
		// int i = 1;
		// country.addItem(" ");
		// country.setSelectedIndex(0);
		// for (CountryDto countryCode : countryCodesList) {
		// country.addItem(countryCode.getDisplayName(),
		// countryCode.getIsoAlpha2Code());
		// if (selectedCountry != null) {
		// if (countryCode.getIsoAlpha2Code().equals(selectedCountry)) {
		// country.setSelectedIndex(i);
		// }
		// }
		// i++;
		// }
		//
		grid.setWidget(row, 1, country);
		// country.addChangeHandler(new ChangeHandler() {
		//
		// @Override
		// public void onChange(ChangeEvent event) {
		// fetchSubCountries(selectedCountry, row, null);
		// }
		// });
	}

	private void fetchSubCountries(String selectedCountry, final Integer row,
			final String selectedSub) {
		ListBox country = (ListBox) scoringTable.getGrid().getWidget(row, 1);
		String countryCode = country.getValue(country.getSelectedIndex());
		Long id = null;
		communitySvc.listChildSubCountries(countryCode, id,
				new AsyncCallback<List<SubCountryDto>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(List<SubCountryDto> result) {

						populateSubLevelControl(scoringTable.getGrid(),
								selectedSub, row, result);
					}
				});

	}

	private void populateSubLevelControl(Grid grid, String selectedCountry,
			final Integer row, List<SubCountryDto> subCountryDtoList) {
		ListBox country = new ListBox();
		int i = 1;
		country.addItem(" ");
		country.setSelectedIndex(0);
		for (SubCountryDto countryCode : subCountryDtoList) {
			country.addItem(countryCode.getName(), countryCode.getKeyId()
					.toString());
			if (selectedCountry != null) {
				if (countryCode.getName().equals(selectedCountry)) {
					country.setSelectedIndex(i);
				}
			}
			i++;
		}

		grid.setWidget(row, 2, country);
	}

	private static Integer selectedRow = null;

	private StandardScoringDto formStandardScoringDto(Integer row) {
		StandardScoringDto item = new StandardScoringDto();
		Grid grid = scoringTable.getGrid();

		Long scoreBucketKey = Long.parseLong(scoreBucketsBox
				.getValue(scoreBucketsBox.getSelectedIndex()));
		if (scoreBucketKey != null) {
			item.setScoreBucketId(scoreBucketKey);
			item.setScoreBucket(scoreBucketsBox.getItemText(scoreBucketsBox
					.getSelectedIndex()));
		}

		ListBox global = (ListBox) grid.getWidget(row, 0);
		if (global.getSelectedIndex() == 0) {
			item.setGlobalStandard(true);
		} else {
			item.setGlobalStandard(false);
		}

		ListBox country = (ListBox) grid.getWidget(row, 1);
		if (country != null) {
			if (country.getSelectedIndex() > 0) {
				item.setCountryCode(country.getValue(country.getSelectedIndex()));
			}
		}

		ListBox subValue = (ListBox) grid.getWidget(row, 2);
		if (subValue != null) {
			if (subValue.getSelectedIndex() > 0) {
				item.setSubValue(subValue.getItemText(subValue
						.getSelectedIndex()));
			}
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
			} else if (criteriaType.getSelectedIndex() == 4) {
				item.setCriteriaType("Distance");
			}
		}
		TextBox positiveCriteria = (TextBox) grid.getWidget(row, 7);
		// if (positiveCriteria.getText().trim().length() > 0) {
		// item.setPositiveCriteria(positiveCriteria.getText().trim());
		// }
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
		TextBox positiveMessage = (TextBox) grid.getWidget(row, 10);
		if (positiveMessage.getText().trim().length() > 0) {
			if (positiveMessage.getText().trim().length() < 500)
				item.setPositiveMessage(positiveMessage.getText().trim());
			else
				item.setPositiveMessage(positiveMessage.getText().trim()
						.substring(0, 498));
		}
		TextBox negativeCriteria = (TextBox) grid.getWidget(row, 11);
		if (negativeCriteria.getText().trim().length() > 0) {
			item.setNegativeCriteria(negativeCriteria.getText());
		}
		ListBox negativeOp = (ListBox) grid.getWidget(row, 12);
		if (negativeOp.getSelectedIndex() > 0) {
			item.setNegativeOperator(negativeOp.getItemText(negativeOp
					.getSelectedIndex()));
		}

		TextBox negativeScore = (TextBox) grid.getWidget(row, 13);
		if (negativeScore.getText().trim().length() > 0) {
			Integer score = Integer.parseInt(negativeScore.getText().trim());
			item.setNegativeScore(score);
		}

		ListBox negativeOverride = (ListBox) grid.getWidget(row, 14);
		if (negativeOverride.getSelectedIndex() == 0) {
			item.setNegativeOverride(false);
		} else {
			item.setNegativeOverride(true);
		}

		TextBox negativeMessage = (TextBox) grid.getWidget(row, 15);
		if (negativeMessage.getText().trim().length() > 0) {
			if (negativeMessage.getText().trim().length() < 500) {
				item.setNegativeMessage(negativeMessage.getText().trim());
			} else {
				item.setNegativeMessage(negativeMessage.getText().trim()
						.substring(0, 498));
			}
		}

		// effectivestartdate
		DateBox effectiveStartDate = (DateBox) grid.getWidget(row, 16);
		if (effectiveStartDate.getValue() != null) {
			item.setEffectiveStartDate(effectiveStartDate.getValue());
		}
		// effectiveenddate
		DateBox effectiveEndDate = (DateBox) grid.getWidget(row, 17);
		if (effectiveEndDate.getValue() != null) {
			item.setEffectiveEndDate(effectiveEndDate.getValue());
		}
		TextBox keyBox = (TextBox) grid.getWidget(row, 18);
		if (keyBox.getValue().trim() != "" && keyBox.getValue() != null) {
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
						// Add urban or periurban

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

			loadCriteriaOperators(grid, row, column + 4,
					item.getCriteriaType(), currentItem.getPositiveOperator());

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
						loadStandardScoreBuckets();
					}
				});

		return null;
	}

}
