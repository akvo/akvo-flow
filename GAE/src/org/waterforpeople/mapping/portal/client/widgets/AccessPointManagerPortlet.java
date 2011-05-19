package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointScoreDetailDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.UnitOfMeasureDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto.AccessPointType;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto.Status;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.UnitOfMeasureDto.UnitOfMeasureSystem;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;
import org.waterforpeople.mapping.portal.client.widgets.component.AccessPointSearchControl;

import com.gallatinsystems.framework.gwt.component.DataTableBinder;
import com.gallatinsystems.framework.gwt.component.DataTableHeader;
import com.gallatinsystems.framework.gwt.component.DataTableListener;
import com.gallatinsystems.framework.gwt.component.PaginatedDataTable;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.MessageDialog;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.gallatinsystems.user.app.gwt.client.PermissionConstants;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.SmallZoomControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.datepicker.client.DateBox;

public class AccessPointManagerPortlet extends UserAwarePortlet implements
		DataTableBinder<AccessPointDto>, DataTableListener<AccessPointDto> {
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);

	public static final String NAME = TEXT_CONSTANTS.accessPointManager();

	private static final String DEFAULT_SORT_FIELD = "key";
	private static final Integer PAGE_SIZE = 20;
	private static final DataTableHeader HEADERS[] = {
			new DataTableHeader(TEXT_CONSTANTS.id(), "key", true),
			new DataTableHeader(TEXT_CONSTANTS.communityCode(),
					"communityCode", true),
			new DataTableHeader(TEXT_CONSTANTS.latitude(), "latitude", true),
			new DataTableHeader(TEXT_CONSTANTS.longitude(), "longitude", true),
			new DataTableHeader(TEXT_CONSTANTS.pointType(), "pointType", true),
			new DataTableHeader(TEXT_CONSTANTS.collectionDate(),
					"collectionDate", true),
			new DataTableHeader(TEXT_CONSTANTS.editDelete()) };

	private static final int WIDTH = 1600;
	private static final int HEIGHT = 800;
	private VerticalPanel contentPane;
	private String S3_PATH;

	private boolean errorMode;

	// Search UI Elements
	private VerticalPanel mainVPanel = new VerticalPanel();

	private Button searchButton = new Button(TEXT_CONSTANTS.search());
	private Button errorsButton = new Button(TEXT_CONSTANTS.showErrors());
	private Button deleteAllButton = new Button(TEXT_CONSTANTS.deleteMatches());

	private FlexTable accessPointFT = new FlexTable();
	private AccessPointManagerServiceAsync svc;

	private Label statusLabel = new Label();

	private Button createNewAccessPoint = new Button(TEXT_CONSTANTS.createAP());

	private ListBox statusLB = new ListBox();

	private DateTimeFormat dateFormat;

	private FlexTable accessPointDetail = new FlexTable();

	private PaginatedDataTable<AccessPointDto> apTable;
	private AccessPointSearchControl apSearchControl;

	public AccessPointManagerPortlet(UserDto user) {
		super(NAME, true, false, false, WIDTH, HEIGHT, user);
		contentPane = new VerticalPanel();
		svc = GWT.create(AccessPointManagerService.class);
		configureS3Path();
		Widget header = buildHeader();
		apTable = new PaginatedDataTable<AccessPointDto>(DEFAULT_SORT_FIELD,
				this, this, true);
		dateFormat = DateTimeFormat.getShortDateFormat();
		contentPane.add(header);
		setContent(contentPane);
		errorMode = false;
		apTable.setVisible(false);
		mainVPanel.add(apTable);
	}

	@Override
	public String getName() {
		return TEXT_CONSTANTS.apManagerTitle();
	}

	private void configureS3Path() {
		svc.returnS3Path(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(String result) {
				S3_PATH = result;
			}
		});
	}

	/**
	 * constructs and installs the menu for this portlet. Also wires in the
	 * event handlers so we can update on menu value change
	 * 
	 * @return
	 */
	private Widget buildHeader() {
		Grid grid = new Grid(2, 2);
		configureSearchRibbon();
		grid.setWidget(0, 0, mainVPanel);

		searchButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				errorMode = false;
				requestData(null, false);
			}
		});

		createNewAccessPoint.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AccessPointDto nullItem = null;
				loadAccessPointDetailTable(nullItem);
			}

		});

		this.errorsButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				errorMode = true;
				requestData(null, false);
			}

		});

		deleteAllButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				MessageDialog warning = new MessageDialog(TEXT_CONSTANTS
						.warning(), TEXT_CONSTANTS.thisWillDeleteAllAP(),
						false, new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {

								AccessPointSearchCriteriaDto criteria = formSearchCriteria();
								svc.deleteAccessPoints(criteria,
										new AsyncCallback<Void>() {

											@Override
											public void onSuccess(Void result) {
												requestData(null, false);
											}

											@Override
											public void onFailure(
													Throwable caught) {
												MessageDialog errDia = new MessageDialog(
														TEXT_CONSTANTS.error(),
														TEXT_CONSTANTS
																.errorTracePrefix()
																+ " "
																+ caught.getLocalizedMessage());
												errDia.showCentered();
											}
										});
							}
						});
				warning.showCentered();
			}
		});
		return grid;
	}

	private void configureSearchRibbon() {
		apSearchControl = new AccessPointSearchControl();
		Grid buttonGrid = new Grid(1, 4);
		buttonGrid.setWidget(0, 0, searchButton);
		buttonGrid.setWidget(0, 1, createNewAccessPoint);
		buttonGrid.setWidget(0, 2, errorsButton);
		buttonGrid.setWidget(0, 3, deleteAllButton);

		if (!getCurrentUser().hasPermission(PermissionConstants.EDIT_AP)) {
			createNewAccessPoint.setVisible(false);
			deleteAllButton.setVisible(false);
		}

		mainVPanel.add(apSearchControl);
		mainVPanel.add(buttonGrid);
	}

	/**
	 * constructs a search criteria object using values from the form
	 * 
	 * @return
	 */
	private AccessPointSearchCriteriaDto formSearchCriteria() {
		AccessPointSearchCriteriaDto dto = apSearchControl.getSearchCriteria();
		dto.setOrderBy(apTable.getCurrentSortField());
		dto.setOrderByDir(apTable.getCurrentSortDirection());
		return dto;
	}

	private void loadAccessPointDetailTable(Long id) {
		apTable.setVisible(false);
		statusLabel.setText(TEXT_CONSTANTS.loading());
		statusLabel.setVisible(true);
		mainVPanel.add(statusLabel);
		svc.getAccessPoint(id, new AsyncCallback<AccessPointDto>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.error(), TEXT_CONSTANTS
								.errorTracePrefix()
								+ " "
								+ caught.getLocalizedMessage());
				errDia.showCentered();
			}

			@Override
			public void onSuccess(AccessPointDto result) {
				AccessPointDto item = (AccessPointDto) result;
				loadAccessPointDetailTable(item);
			}

		});
	}

	private TabPanel loadTabs(AccessPointDto accessPointDto) {
		TabPanel tp = new TabPanel();
		tp.add(loadGeneralTab(accessPointDto), TEXT_CONSTANTS.general());
		tp.add(loadMediaTab(accessPointDto), TEXT_CONSTANTS.media());
		tp.add(loadAttributeTab(accessPointDto), TEXT_CONSTANTS.attributes());
		tp.add(loadScoreTab(accessPointDto), TEXT_CONSTANTS.scoreDetails());
		tp.selectTab(0);
		return tp;
	}

	private Widget loadScoreTab(AccessPointDto accessPointDto) {
		final FlexTable accessPointDetail = new FlexTable();
		if (accessPointDto != null
				&& accessPointDto.getApScoreDetailList() != null) {
			for (AccessPointScoreDetailDto item : accessPointDto
					.getApScoreDetailList()) {
				TextBox scoreCompDate = new TextBox();
				TextBox score = new TextBox();
				TextBox status = new TextBox();
				TextArea scoreItems = new TextArea();
				if (item.getComputationDate() != null) {
					scoreCompDate.setText(item.getComputationDate().toString());
				}
				if (item.getScore() != null) {
					score.setText(item.getScore().toString());
				}
				if (item.getStatus() != null) {
					status.setText(item.getStatus());
				}
				if (item.getScoreComputationItems() != null) {
					StringBuilder sb = new StringBuilder();
					for (String scoreitem : item.getScoreComputationItems()) {
						sb.append(scoreitem + "\n");
					}
					scoreItems.setText(sb.toString());
					scoreItems.setWidth("30em");
					scoreItems.setHeight("10em");
				}
				accessPointDetail.setWidget(0, 0,
						ViewUtil.initLabel(TEXT_CONSTANTS.scoreDate()));
				accessPointDetail.setWidget(0, 1, scoreCompDate);
				accessPointDetail.setWidget(1, 0,
						ViewUtil.initLabel(TEXT_CONSTANTS.score()));
				accessPointDetail.setWidget(1, 1, score);
				accessPointDetail.setWidget(2, 0,
						ViewUtil.initLabel(TEXT_CONSTANTS.status()));
				accessPointDetail.setWidget(2, 1, status);
				accessPointDetail.setWidget(3, 0,
						ViewUtil.initLabel(TEXT_CONSTANTS.scoreMakeup()));
				accessPointDetail.setWidget(3, 1, scoreItems);
			}
		}
		return accessPointDetail;
	}

	private FlexTable loadMediaTab(AccessPointDto accessPointDto) {
		final FlexTable accessPointDetail = new FlexTable();
		accessPointDetail.setWidget(10, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.photoUrl()));
		TextBox photoURLTB = new TextBox();
		photoURLTB.setWidth("500px");
		FormPanel form = new FormPanel();
		form.setMethod(FormPanel.METHOD_POST);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setAction("/webapp/photoupload");
		FileUpload upload = new FileUpload();
		form.setWidget(upload);
		accessPointDetail.setWidget(10, 3, form);
		Button submitUpload = new Button(TEXT_CONSTANTS.upload());
		upload.setName("uploadFormElement");
		accessPointDetail.setWidget(10, 4, submitUpload);

		form.addSubmitHandler(new SubmitHandler() {
			@Override
			public void onSubmit(SubmitEvent event) {
				// no-op
			}
		});

		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				Window.alert(TEXT_CONSTANTS.uploadComplete());
				String fileName = ((FileUpload) ((FormPanel) accessPointDetail
						.getWidget(10, 3)).getWidget()).getFilename();

				if (fileName.contains("/")) {
					fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
				}
				if (fileName.contains("\\")) {
					fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
				}

				((TextBox) accessPointDetail.getWidget(10, 1)).setText(S3_PATH
						+ "images/" + fileName);

				Image i = ((Image) accessPointDetail.getWidget(11, 1));
				if (i == null) {
					Image photo = new Image();
					photo.setUrl(S3_PATH + "/images/" + fileName);
					photo.setHeight("200px");
					accessPointDetail.setWidget(11, 1, photo);
				} else {
					i.setHeight("200px");
					i.setUrl(S3_PATH + "/images/" + fileName);
				}
			}
		});

		submitUpload.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				((FormPanel) accessPointDetail.getWidget(10, 3)).submit();

			}

		});

		if (accessPointDto != null) {

			photoURLTB.setText(accessPointDto.getPhotoURL());
			Image photo = new Image(accessPointDto.getPhotoURL() + "?random="
					+ Random.nextInt());
			accessPointDetail.setWidget(11, 1, photo);
			photo.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					((Image) accessPointDetail.getWidget(11, 1))
							.setVisible(false);
					accessPointDetail.setWidget(10, 4,
							new Label(TEXT_CONSTANTS.waitForRotate()));
					svc.rotateImage(((TextBox) accessPointDetail.getWidget(10,
							1)).getText(), new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {
							MessageDialog dia = new MessageDialog(
									TEXT_CONSTANTS.error(), TEXT_CONSTANTS
											.errorTracePrefix()
											+ ": "
											+ caught.getLocalizedMessage());
							dia.showCentered();
						}

						@Override
						public void onSuccess(Void v) {
							Integer random = Random.nextInt();
							Image photo = ((Image) accessPointDetail.getWidget(
									11, 1));
							accessPointDetail.getWidget(10, 4)
									.setVisible(false);
							photo.setUrl(((TextBox) accessPointDetail
									.getWidget(11, 1)).getText()
									+ "?random="
									+ random);
							accessPointDetail.setWidget(11, 1, photo);
							photo.setVisible(true);
						}
					});
				}
			});
		}
		accessPointDetail.setWidget(10, 1, photoURLTB);

		accessPointDetail.setWidget(12, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.photoCaption()));
		TextArea captionTB = new TextArea();
		captionTB.setWidth("500px");
		captionTB.setHeight("200px");
		if (accessPointDto != null)
			captionTB.setText(accessPointDto.getPointPhotoCaption());
		accessPointDetail.setWidget(12, 1, captionTB);
		Label apId;
		if (accessPointDto != null) {
			apId = new Label(accessPointDto.getKeyId().toString());
		} else {
			apId = new Label("-1");
		}
		apId.setVisible(false);
		accessPointDetail.setWidget(15, 1, apId);
		return accessPointDetail;
	}

	private FlexTable loadGeneralTab(AccessPointDto accessPointDto) {
		FlexTable accessPointDetail = new FlexTable();
		accessPointDetail.setWidget(0, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.communityCode()));
		TextBox communityCodeTB = new TextBox();
		if (accessPointDto != null) {
			communityCodeTB.setText(accessPointDto.getCommunityCode());
		}

		accessPointDetail.setWidget(0, 1, communityCodeTB);

		accessPointDetail.setWidget(1, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.countryCode()));
		TextBox countryCodeTB = new TextBox();
		if (accessPointDto != null) {
			countryCodeTB.setText(accessPointDto.getCountryCode());
		}

		accessPointDetail.setWidget(1, 1, countryCodeTB);

		accessPointDetail.setWidget(2, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.latitude()));
		TextBox latitudeTB = new TextBox();
		if (accessPointDto != null)
			latitudeTB.setText(accessPointDto.getLatitude().toString());
		accessPointDetail.setWidget(2, 1, latitudeTB);

		accessPointDetail.setWidget(3, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.longitude()));
		TextBox longitudeTB = new TextBox();
		if (accessPointDto != null)
			longitudeTB.setText(accessPointDto.getLongitude().toString());
		accessPointDetail.setWidget(3, 1, longitudeTB);

		if (accessPointDto != null && accessPointDto.getLatitude() != null
				&& accessPointDto.getLongitude() != null) {
			MapWidget map = new MapWidget();
			map.setSize("180px", "180px");
			map.addControl(new SmallZoomControl());
			try {
				LatLng point = LatLng.newInstance(accessPointDto.getLatitude(),
						accessPointDto.getLongitude());
				map.addOverlay(new Marker(point));
				map.setZoomLevel(12);
				map.setCenter(point);
			} catch (Throwable e) {
				// swallow
			}
			accessPointDetail.setWidget(0, 2, map);
			accessPointDetail.getFlexCellFormatter().setRowSpan(0, 2, 5);
		}

		accessPointDetail.setWidget(4, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.collectionDate()));
		DateBox pickerCollectionDate = new DateBox();
		if (accessPointDto != null)
			pickerCollectionDate.setValue(accessPointDto.getCollectionDate());
		accessPointDetail.setWidget(4, 1, pickerCollectionDate);

		accessPointDetail.setWidget(5, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.constructionDate()));
		DateBox pickerConstructionDate = new DateBox();
		if (accessPointDto != null)
			pickerConstructionDate.setValue(accessPointDto
					.getConstructionDate());
		accessPointDetail.setWidget(5, 1, pickerConstructionDate);

		accessPointDetail.setWidget(6, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 1));
		TextBox sub1 = new TextBox();
		if (accessPointDto != null && accessPointDto.getSub1() != null)
			sub1.setText(accessPointDto.getSub1());
		accessPointDetail.setWidget(6, 1, sub1);

		accessPointDetail.setWidget(7, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 2));
		TextBox sub2 = new TextBox();
		if (accessPointDto != null && accessPointDto.getSub2() != null)
			sub2.setText(accessPointDto.getSub2());
		accessPointDetail.setWidget(7, 1, sub2);

		accessPointDetail.setWidget(8, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 3));
		TextBox sub3 = new TextBox();
		if (accessPointDto != null && accessPointDto.getSub3() != null)
			sub3.setText(accessPointDto.getSub3());
		accessPointDetail.setWidget(8, 1, sub3);

		accessPointDetail.setWidget(9, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 4));
		TextBox sub4 = new TextBox();
		if (accessPointDto != null && accessPointDto.getSub4() != null)
			sub1.setText(accessPointDto.getSub4());
		accessPointDetail.setWidget(9, 1, sub4);

		accessPointDetail.setWidget(10, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 5));
		TextBox sub5 = new TextBox();
		if (accessPointDto != null && accessPointDto.getSub5() != null)
			sub5.setText(accessPointDto.getSub5());
		accessPointDetail.setWidget(10, 1, sub5);

		accessPointDetail.setWidget(11, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.subdivision() + " " + 6));
		TextBox sub6 = new TextBox();
		if (accessPointDto != null && accessPointDto.getSub6() != null)
			sub6.setText(accessPointDto.getSub6());
		accessPointDetail.setWidget(11, 1, sub6);

		// Missing
		// InstitutionName
		Label apId;
		if (accessPointDto != null) {
			apId = new Label(accessPointDto.getKeyId().toString());
		} else {
			apId = new Label("-1");
		}
		apId.setVisible(false);
		accessPointDetail.setWidget(15, 1, apId);
		return accessPointDetail;
	}

	private FlexTable loadAttributeTab(AccessPointDto accessPointDto) {
		FlexTable accessPointDetail = new FlexTable();
		accessPointDetail.setWidget(6, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.costPer()));
		TextBox costPerTB = new TextBox();
		if (accessPointDto != null && accessPointDto.getCostPer() != null)
			costPerTB.setText(accessPointDto.getCostPer().toString());
		accessPointDetail.setWidget(6, 1, costPerTB);

		ListBox unitOfMeasureLB = new ListBox();
		unitOfMeasureLB.addItem(TEXT_CONSTANTS.ml());
		unitOfMeasureLB.addItem(TEXT_CONSTANTS.liters());
		unitOfMeasureLB.addItem(TEXT_CONSTANTS.ounces());
		unitOfMeasureLB.addItem(TEXT_CONSTANTS.gallons());
		accessPointDetail.setWidget(6, 2, unitOfMeasureLB);

		accessPointDetail.setWidget(7, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.currentMgmtStructure()));
		TextBox currentMgmtStructureTB = new TextBox();
		if (accessPointDto != null)
			currentMgmtStructureTB.setText(accessPointDto
					.getCurrentManagementStructurePoint());
		accessPointDetail.setWidget(7, 1, currentMgmtStructureTB);

		accessPointDetail.setWidget(8, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.description()));
		TextBox descTB = new TextBox();
		if (accessPointDto != null)
			descTB.setText(accessPointDto.getDescription());
		accessPointDetail.setWidget(8, 1, descTB);

		accessPointDetail.setWidget(9, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.numHouseholdsUsing()));
		TextBox numHouseholdsTB = new TextBox();
		if (accessPointDto != null
				&& accessPointDto.getNumberOfHouseholdsUsingPoint() != null)
			numHouseholdsTB.setText(accessPointDto
					.getNumberOfHouseholdsUsingPoint().toString());
		accessPointDetail.setWidget(9, 1, numHouseholdsTB);
		accessPointDetail.setWidget(12, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.pointStatus()));
		statusLB = new ListBox();
		statusLB.addItem(TEXT_CONSTANTS.funcHigh());
		statusLB.addItem(TEXT_CONSTANTS.funcOk());
		statusLB.addItem(TEXT_CONSTANTS.funcProb());
		statusLB.addItem(TEXT_CONSTANTS.noImprovedSys());
		statusLB.addItem(TEXT_CONSTANTS.other());

		if (accessPointDto != null) {
			AccessPointDto.Status pointStatus = accessPointDto.getPointStatus();
			if (pointStatus.equals(AccessPointDto.Status.FUNCTIONING_HIGH)) {
				statusLB.setSelectedIndex(0);
			} else if (pointStatus.equals(AccessPointDto.Status.FUNCTIONING_OK)) {
				statusLB.setSelectedIndex(1);
			} else if (pointStatus
					.equals(AccessPointDto.Status.FUNCTIONING_WITH_PROBLEMS)) {
				statusLB.setSelectedIndex(2);
			} else if (pointStatus
					.equals(AccessPointDto.Status.NO_IMPROVED_SYSTEM)) {
				statusLB.setSelectedIndex(3);
			} else {
				statusLB.setSelectedIndex(4);
			}
		}
		accessPointDetail.setWidget(12, 1, statusLB);

		accessPointDetail.setWidget(13, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.pointType()));

		ListBox pointType = new ListBox();
		pointType.addItem(TEXT_CONSTANTS.waterPoint(),
				AccessPointType.WATER_POINT.toString());
		pointType.addItem(TEXT_CONSTANTS.sanitationPoint(),
				AccessPointType.SANITATION_POINT.toString());
		pointType.addItem(TEXT_CONSTANTS.publicInst(),
				AccessPointType.PUBLIC_INSTITUTION.toString());
		pointType.addItem(TEXT_CONSTANTS.school(),
				AccessPointType.SCHOOL.toString());
		if (accessPointDto != null) {
			AccessPointType apType = accessPointDto.getPointType();
			if (apType.equals(AccessPointType.WATER_POINT)) {
				pointType.setSelectedIndex(0);
			} else if (apType.equals(AccessPointType.SANITATION_POINT)) {
				pointType.setSelectedIndex(1);
			} else if (apType.equals(AccessPointType.PUBLIC_INSTITUTION)) {
				pointType.setSelectedIndex(2);
			} else if (apType.equals(AccessPointType.SCHOOL)) {
				pointType.setSelectedIndex(3);
			}

		} else {
			pointType.setSelectedIndex(0);
		}
		accessPointDetail.setWidget(13, 1, pointType);

		accessPointDetail.setWidget(14, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.farthestPointFrom()));
		TextBox farthestPointFromTB = new TextBox();
		if (accessPointDto != null)
			farthestPointFromTB.setText(accessPointDto
					.getFarthestHouseholdfromPoint());
		accessPointDetail.setWidget(14, 1, farthestPointFromTB);
		Label apId;
		if (accessPointDto != null) {
			apId = new Label(accessPointDto.getKeyId().toString());
		} else {
			apId = new Label("-1");
		}
		apId.setVisible(false);
		accessPointDetail.setWidget(15, 1, apId);

		accessPointDetail.setWidget(16, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.smsCode()));
		TextBox smsCode = new TextBox();
		if (accessPointDto != null)
			smsCode.setText(accessPointDto.getSmsCode());
		accessPointDetail.setWidget(16, 1, smsCode);

		// In Screen
		// CostPer
		// CurrentMgmtStructure
		// Desc
		// Number of Households Using Point
		// POint Status
		// PointType
		// Farthest Point From
		// SMS Code

		// Missing
		// balloonTitle
		// FarthestHouseholdFromPoint

		accessPointDetail.setWidget(17, 0, ViewUtil.initLabel(TEXT_CONSTANTS
				.farthestHouseholdAcceptable()));
		accessPointDetail.setWidget(
				17,
				1,
				addListBox(accessPointDto != null ? accessPointDto
						.getFarthestHouseholdfromPoint() : null));

		// footer
		// header
		// meetGovtQualtiyStandardFlag
		accessPointDetail.setWidget(18, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.meetQualityStandards()));
		accessPointDetail
				.setWidget(
						18,
						1,
						addListBox(accessPointDto != null
								&& accessPointDto.getMeetGovtQualityStandards() != null ? accessPointDto
								.getMeetGovtQualityStandards().toString()
								: null));

		// meetGovtQuantityStandardFlag
		accessPointDetail.setWidget(19, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.meetQuantityStandards()));
		accessPointDetail
				.setWidget(
						19,
						1,
						addListBox(accessPointDto != null
								&& accessPointDto
										.getMeetGovtQunatityStandardsFlag() != null ? accessPointDto
								.getMeetGovtQunatityStandardsFlag().toString()
								: null));
		// numberOfHouseholdsUsingPoint
		// provideAdequateQuantity
		accessPointDetail.setWidget(21, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.adequateQuantity()));
		accessPointDetail
				.setWidget(
						21,
						1,
						addListBox(accessPointDto != null
								&& accessPointDto.getProvideAdequateQuantity() != null ? accessPointDto
								.getProvideAdequateQuantity().toString() : null));
		// SecondaryTechnologyString
		// typeTechnologyString
		TextBox techTypeString = new TextBox();
		if (accessPointDto != null
				&& accessPointDto.getTypeTechnologyString() != null)
			techTypeString.setText(accessPointDto.getTypeTechnologyString());
		accessPointDetail.setWidget(22, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.techTypeString()));
		accessPointDetail.setWidget(22, 1, techTypeString);

		TextBox secondaryTechTypeString = new TextBox();
		accessPointDetail.setWidget(23, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.secondaryTechTypeString()));
		if (accessPointDto != null
				&& accessPointDto.getSecondaryTechnologyString() != null)
			secondaryTechTypeString.setText(accessPointDto
					.getSecondaryTechnologyString());
		accessPointDetail.setWidget(23, 1, secondaryTechTypeString);
		// whoRepairsPoint
		TextBox whoRepairsPoint = new TextBox();
		accessPointDetail.setWidget(24, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.whoRepairs()));
		if (accessPointDto != null
				&& accessPointDto.getWhoRepairsPoint() != null)
			whoRepairsPoint.setText(accessPointDto.getWhoRepairsPoint());
		accessPointDetail.setWidget(24, 1, whoRepairsPoint);

		// estimatedHouseholds
		TextBox estHouseholds = new TextBox();
		accessPointDetail.setWidget(25, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.estHouseholdsUsing()));
		if (accessPointDto != null
				&& accessPointDto.getNumberOfHouseholdsUsingPoint() != null)
			estHouseholds.setText(accessPointDto
					.getNumberOfHouseholdsUsingPoint().toString());
		accessPointDetail.setWidget(25, 1, estHouseholds);

		// estimatedPeoplePerHouse
		TextBox estPeoplePerHouse = new TextBox();
		accessPointDetail.setWidget(26, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.estPeoplePerHouse()));
		if (accessPointDto != null
				&& accessPointDto.getEstimatedPeoplePerHouse() != null)
			estPeoplePerHouse.setText(accessPointDto
					.getEstimatedPeoplePerHouse().toString());
		accessPointDetail.setWidget(26, 1, estPeoplePerHouse);

		// extimatedPopulation
		TextBox estimatedPopulation = new TextBox();
		accessPointDetail.setWidget(27, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.estPopulation()));
		if (accessPointDto != null
				&& accessPointDto.getEstimatedPopulation() != null)
			estimatedPopulation.setText(accessPointDto.getEstimatedPopulation()
					.toString());
		accessPointDetail.setWidget(27, 1, estimatedPopulation);

		// hasSystemBeenDown1DayFlag
		accessPointDetail.setWidget(29, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.hasSysBeenDown()));
		accessPointDetail
				.setWidget(
						29,
						1,
						addListBox(accessPointDto != null
								&& accessPointDto
										.getHasSystemBeenDown1DayFlag() != null ? accessPointDto
								.getHasSystemBeenDown1DayFlag().toString()
								: null));

		accessPointDetail.setWidget(30, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.wfpSupported()));
		accessPointDetail
				.setWidget(
						30,
						1,
						addListBox(accessPointDto != null
								&& accessPointDto
										.getWaterForPeopleProjectFlag() != null ? accessPointDto
								.getWaterForPeopleProjectFlag().toString()
								: null));
		accessPointDetail.setWidget(31, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.wfpRole()));
		TextBox roleTextBox = new TextBox();
		if (accessPointDto != null
				&& accessPointDto.getWaterForPeopleRole() != null)
			roleTextBox.setText(accessPointDto.getWaterForPeopleRole());
		accessPointDetail.setWidget(31, 1, roleTextBox);

		TextBox currentScoreCompDate = new TextBox();
		accessPointDetail.setWidget(32, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.scoreDate()));
		if (accessPointDto != null
				&& accessPointDto.getScoreComputationDate() != null) {
			currentScoreCompDate.setText(accessPointDto
					.getScoreComputationDate().toString());
		} else {
			currentScoreCompDate.setText(TEXT_CONSTANTS.unknown());
			accessPointDetail.setWidget(32, 1, currentScoreCompDate);
		}

		TextBox currentScore = new TextBox();
		accessPointDetail.setWidget(33, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.score()));
		if (accessPointDto != null && accessPointDto.getScore() != null) {
			currentScore.setText(accessPointDto.getScore().toString());
		} else {
			currentScore.setText(TEXT_CONSTANTS.noScore());
		}
		accessPointDetail.setWidget(33, 1, currentScore);

		return accessPointDetail;
	}

	private void loadAccessPointDetailTable(AccessPointDto accessPointDto) {

		apTable.setVisible(false);

		// if(accessPointDto.getPointType().toString()!=null){
		// type = accessPointDto.getPointType().toString();
		// pointTypeTB.setText(type);
		// accessPointDetail.setWidget(12, 1, pointTypeTB);
		// }

		statusLabel.setText("");
		statusLabel.setVisible(false);
		mainVPanel.remove(statusLabel);

		Button saveButton = new Button(TEXT_CONSTANTS.save());

		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (validateAccessPointDetail()) {
					statusLabel.setVisible(true);
					statusLabel.setText(TEXT_CONSTANTS.pleaseWait());
					mainVPanel.add(statusLabel);
					AccessPointDto apDto = buildAccessPointDto();
					svc.saveAccessPoint(apDto,
							new AsyncCallback<AccessPointDto>() {
								@Override
								public void onFailure(Throwable caught) {
									MessageDialog errDialog = new MessageDialog(
											TEXT_CONSTANTS.error(),
											TEXT_CONSTANTS.errorTracePrefix()
													+ " "
													+ caught.getLocalizedMessage());
									errDialog.showCentered();
								}

								@Override
								public void onSuccess(AccessPointDto result) {
									Window.alert(TEXT_CONSTANTS.saveComplete());
								}
							});
					accessPointDetail.setVisible(false);
					statusLabel.setVisible(false);
					mainVPanel.remove(statusLabel);
					accessPointFT.setVisible(true);
				}
			}

		});
		Button cancelButton = new Button(TEXT_CONSTANTS.cancel());
		cancelButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Boolean ok = Window.confirm(TEXT_CONSTANTS.changeWillBeLost());
				if (ok) {
					accessPointDetail.setVisible(false);
					statusLabel.setVisible(false);
					mainVPanel.remove(statusLabel);
					accessPointFT.setVisible(true);
				}
			}

		});
		accessPointDetail.setWidget(1, 0, loadTabs(accessPointDto));
		HorizontalPanel hButtonPanel = new HorizontalPanel();
		hButtonPanel.add(saveButton);
		hButtonPanel.add(cancelButton);
		accessPointDetail.setWidget(17, 0, hButtonPanel);
		accessPointDetail.setVisible(true);
		mainVPanel.add(accessPointDetail);
	}

	private AccessPointDto buildAccessPointDto() {
		AccessPointDto apDto = new AccessPointDto();

		TabPanel tp = (TabPanel) accessPointDetail.getWidget(1, 0);
		FlexTable accessPointDetail = (FlexTable) tp.getWidget(0);
		apDto = getGeneralAP(apDto, accessPointDetail);

		accessPointDetail = (FlexTable) tp.getWidget(1);
		apDto = getMediaAP(apDto, accessPointDetail);

		accessPointDetail = (FlexTable) tp.getWidget(2);
		apDto = getAttributeAP(apDto, accessPointDetail);

		return apDto;
	}

	private AccessPointDto getGeneralAP(AccessPointDto apDto,
			FlexTable accessPointDetail) {
		Label apId = (Label) accessPointDetail.getWidget(15, 1);
		Long id = new Long(apId.getText());
		if (id > -1) {
			apDto.setKeyId(id);
		} else {
			apDto.setKeyId(null);
		}
		TextBox communityCodeTB = (TextBox) accessPointDetail.getWidget(0, 1);
		String communityCode = communityCodeTB.getText();
		apDto.setCommunityCode(communityCode);

		TextBox countryCodeTB = (TextBox) accessPointDetail.getWidget(1, 1);
		String countryCode = countryCodeTB.getText();
		apDto.setCountryCode(countryCode);

		TextBox subBox = (TextBox) accessPointDetail.getWidget(6, 1);
		apDto.setSub1(subBox.getText());

		subBox = (TextBox) accessPointDetail.getWidget(7, 1);
		apDto.setSub2(subBox.getText());

		subBox = (TextBox) accessPointDetail.getWidget(8, 1);
		apDto.setSub3(subBox.getText());

		subBox = (TextBox) accessPointDetail.getWidget(9, 1);
		apDto.setSub4(subBox.getText());

		subBox = (TextBox) accessPointDetail.getWidget(10, 1);
		apDto.setSub5(subBox.getText());

		subBox = (TextBox) accessPointDetail.getWidget(11, 1);
		apDto.setSub6(subBox.getText());

		TextBox latitudeTB = (TextBox) accessPointDetail.getWidget(2, 1);
		Double latitude = new Double(latitudeTB.getText());
		apDto.setLatitude(latitude);

		TextBox longitudeTB = (TextBox) accessPointDetail.getWidget(3, 1);
		Double longitude = new Double(longitudeTB.getText());
		apDto.setLongitude(longitude);

		DateBox collectionDateTB = (DateBox) accessPointDetail.getWidget(4, 1);

		apDto.setCollectionDate(collectionDateTB.getValue());

		DateBox constructionDateTB = (DateBox) accessPointDetail
				.getWidget(5, 1);
		apDto.setConstructionDate(constructionDateTB.getValue());

		return apDto;
	}

	private AccessPointDto getMediaAP(AccessPointDto apDto,
			FlexTable accessPointDetail) {
		TextBox photoURLTB = (TextBox) accessPointDetail.getWidget(10, 1);
		String photoUrl = photoURLTB.getText();
		apDto.setPhotoURL(photoUrl);

		TextArea captionTB = (TextArea) accessPointDetail.getWidget(12, 1);
		String caption = captionTB.getText();
		apDto.setPointPhotoCaption(caption);

		return apDto;
	}

	private AccessPointDto getAttributeAP(AccessPointDto apDto,
			FlexTable accessPointDetail) {
		TextBox costPerTB = (TextBox) accessPointDetail.getWidget(6, 1);
		String costPerTemp = costPerTB.getText();

		if (costPerTemp != null && costPerTemp.length() > 0) {
			Double costPer = new Double(costPerTB.getText());
			apDto.setCostPer(costPer);
		}
		ListBox unitOfMeasureLB = (ListBox) accessPointDetail.getWidget(6, 2);
		if (unitOfMeasureLB.getSelectedIndex() == 0) {
			// ml
			UnitOfMeasureDto uom = new UnitOfMeasureDto();
			uom.setSystem(UnitOfMeasureSystem.METRIC);
			uom.setCode("ml");
			apDto.setCostPerUnitOfMeasure(uom);
		} else if (unitOfMeasureLB.getSelectedIndex() == 1) {
			// liters
			UnitOfMeasureDto uom = new UnitOfMeasureDto();
			uom.setSystem(UnitOfMeasureSystem.METRIC);
			uom.setCode("l");
			apDto.setCostPerUnitOfMeasure(uom);

		} else if (unitOfMeasureLB.getSelectedIndex() == 2) {
			UnitOfMeasureDto uom = new UnitOfMeasureDto();
			uom.setSystem(UnitOfMeasureSystem.IMPERIAL);
			uom.setCode("oz");
			apDto.setCostPerUnitOfMeasure(uom);
			// /ounces
		} else {
			// gallons
			UnitOfMeasureDto uom = new UnitOfMeasureDto();
			uom.setSystem(UnitOfMeasureSystem.IMPERIAL);
			uom.setCode("g");
			apDto.setCostPerUnitOfMeasure(uom);

		}

		TextBox currentMgmtStructureTB = (TextBox) accessPointDetail.getWidget(
				7, 1);
		String currentMgmtStructure = currentMgmtStructureTB.getText();
		apDto.setCurrentManagementStructurePoint(currentMgmtStructure);

		TextBox descTB = (TextBox) accessPointDetail.getWidget(8, 1);
		String desc = descTB.getText();
		apDto.setDescription(desc);

		TextBox numHouseholdsTB = (TextBox) accessPointDetail.getWidget(9, 1);
		String numHouseholds = numHouseholdsTB.getText();
		if (numHouseholds != null && numHouseholds.trim().length() > 0) {
			try {
				apDto.setNumberOfHouseholdsUsingPoint(new Long(numHouseholds
						.trim()));
			} catch (NumberFormatException e) {
				// to-do: display validation error
			}
		}

		ListBox statusLB = (ListBox) accessPointDetail.getWidget(12, 1);
		if (statusLB.getSelectedIndex() == 0) {
			apDto.setPointStatus(AccessPointDto.Status.FUNCTIONING_HIGH);
		} else if (statusLB.getSelectedIndex() == 1) {
			apDto.setPointStatus(Status.FUNCTIONING_OK);
		} else if (statusLB.getSelectedIndex() == 2) {
			apDto.setPointStatus(Status.FUNCTIONING_WITH_PROBLEMS);
		} else if (statusLB.getSelectedIndex() == 3) {
			apDto.setPointStatus(Status.NO_IMPROVED_SYSTEM);
		} else {
			apDto.setPointStatus(Status.OTHER);
		}

		ListBox pointTypeLB = (ListBox) accessPointDetail.getWidget(13, 1);
		Integer selectedIndex = pointTypeLB.getSelectedIndex();
		String type = pointTypeLB.getItemText(selectedIndex);
		if (type.equals(TEXT_CONSTANTS.waterPoint())) {
			apDto.setPointType(AccessPointType.WATER_POINT);
		} else if (type.equals(TEXT_CONSTANTS.sanitationPoint())) {
			apDto.setPointType(AccessPointType.SANITATION_POINT);
		} else if (type.equals(TEXT_CONSTANTS.publicInst())) {
			apDto.setPointType(AccessPointType.PUBLIC_INSTITUTION);
		} else if (type.equals(TEXT_CONSTANTS.school())) {
			apDto.setPointType(AccessPointType.SCHOOL);
		}

		TextBox farthestPointFromTB = (TextBox) accessPointDetail.getWidget(14,
				1);
		String farthestPointFrom = farthestPointFromTB.getText();
		apDto.setFarthestHouseholdfromPoint(farthestPointFrom);

		TextBox smsCodeTB = (TextBox) accessPointDetail.getWidget(16, 1);
		String smsCode = smsCodeTB.getText();
		apDto.setSmsCode(smsCode);

		apDto.setFarthestHouseholdfromPoint(getValueFromWidget(
				accessPointDetail, 17, 1));

		apDto.setMeetGovtQualityStandards(getValueFromWidget(accessPointDetail,
				18, 1).equals(TEXT_CONSTANTS.yes()) ? true : false);
		apDto.setMeetGovtQunatityStandardsFlag(getValueFromWidget(
				accessPointDetail, 19, 1).equals(TEXT_CONSTANTS.yes()) ? true
				: false);
		apDto.setProvideAdequateQuantity(getValueFromWidget(accessPointDetail,
				21, 1).equals(TEXT_CONSTANTS.yes()) ? true : false);
		apDto.setTypeTechnologyString(getValueFromWidget(accessPointDetail, 22,
				1));
		apDto.setSecondaryTechnologyString(getValueFromWidget(
				accessPointDetail, 23, 1));
		apDto.setWhoRepairsPoint(getValueFromWidget(accessPointDetail, 24, 1));
		apDto.setNumberOfHouseholdsUsingPoint(getLongValueFromWidget(
				accessPointDetail, 25, 1));
		apDto.setEstimatedPeoplePerHouse(getLongValueFromWidget(
				accessPointDetail, 26, 1));
		apDto.setEstimatedPopulation(getLongValueFromWidget(accessPointDetail,
				27, 1));
		apDto.setHasSystemBeenDown1DayFlag(getValueFromWidget(
				accessPointDetail, 29, 1).equals(TEXT_CONSTANTS.yes()) ? true
				: false);
		apDto.setWaterForPeopleProjectFlag(getValueFromWidget(
				accessPointDetail, 30, 1).equals(TEXT_CONSTANTS.yes()) ? true
				: false);
		apDto.setWaterForPeopleRole(getValueFromWidget(accessPointDetail, 31, 1));
		return apDto;
	}

	public Boolean validateAccessPointDetail() {
		return true;
	}

	@Override
	public void bindRow(final Grid grid, AccessPointDto apDto, int row) {
		Label keyIdLabel = new Label(apDto.getKeyId().toString());
		grid.setWidget(row, 0, keyIdLabel);
		if (apDto.getCommunityCode() != null) {
			String communityCode = apDto.getCommunityCode();
			if (communityCode.length() > 10)
				communityCode = communityCode.substring(0, 10);
			grid.setWidget(row, 1, new Label(communityCode));
		}

		if (apDto.getLatitude() != null && apDto.getLongitude() != null) {
			grid.setWidget(row, 2, new Label(apDto.getLatitude().toString()));
			grid.setWidget(row, 3, new Label(apDto.getLongitude().toString()));
		}
		if (apDto.getPointType() != null) {
			grid.setWidget(row, 4, new Label(apDto.getPointType().name()));
		}
		if (apDto.getCollectionDate() != null) {
			grid.setWidget(row, 5,
					new Label(dateFormat.format(apDto.getCollectionDate())));
		}

		Button editAccessPoint = new Button(TEXT_CONSTANTS.edit());
		editAccessPoint.setTitle(keyIdLabel.getText());
		Button deleteAccessPoint = new Button(TEXT_CONSTANTS.delete());
		deleteAccessPoint.setTitle(new Integer(row).toString() + "|"
				+ keyIdLabel.getText());
		HorizontalPanel buttonHPanel = new HorizontalPanel();
		buttonHPanel.add(editAccessPoint);
		buttonHPanel.add(deleteAccessPoint);
		if (!getCurrentUser().hasPermission(PermissionConstants.EDIT_AP)) {
			buttonHPanel.setVisible(false);
		}

		editAccessPoint.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Button pressedButton = (Button) event.getSource();
				Long itemId = Long.parseLong(pressedButton.getTitle());
				loadAccessPointDetailTable(itemId);
			}

		});

		deleteAccessPoint.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final Button pressedButton = (Button) event.getSource();
				String[] titleParts = pressedButton.getTitle().split("\\|");
				final Integer row = Integer.parseInt(titleParts[0]);
				final Long itemId = Long.parseLong(titleParts[1]);

				svc.deleteAccessPoint(itemId, new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(TEXT_CONSTANTS.errorTracePrefix() + " "
								+ caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(Integer result) {
						int rowSelected = row;
						apTable.removeRow(rowSelected);
						Grid grid = apTable.getGrid();
						for (int i = rowSelected; i < grid.getRowCount() - 1; i++) {
							HorizontalPanel hPanel = (HorizontalPanel) grid
									.getWidget(i, 6);
							Button deleteButton = (Button) hPanel.getWidget(1);
							String[] buttonTitleParts = deleteButton.getTitle()
									.split("\\|");
							Integer newRowNum = Integer
									.parseInt(buttonTitleParts[0]);
							newRowNum = newRowNum - 1;
							deleteButton.setTitle(newRowNum + "|"
									+ buttonTitleParts[1]);

						}
						Window.alert(TEXT_CONSTANTS.deleteComplete());
					}

				});

			}

		});
		grid.setWidget(row, 6, buttonHPanel);
	}

	@Override
	public DataTableHeader[] getHeaders() {
		return HEADERS;
	}

	@Override
	public void onItemSelected(AccessPointDto item) {
		// no-op

	}

	@Override
	public void requestData(String cursor, final boolean isResort) {
		accessPointDetail.setVisible(false);
		final boolean isNew = (cursor == null);
		final AccessPointSearchCriteriaDto searchDto = formSearchCriteria();
		boolean isOkay = true;
		AsyncCallback<ResponseDto<ArrayList<AccessPointDto>>> dataCallback = new AsyncCallback<ResponseDto<ArrayList<AccessPointDto>>>() {
			@Override
			public void onFailure(Throwable caught) {
				MessageDialog errDia = new MessageDialog(
						TEXT_CONSTANTS.error(),
						TEXT_CONSTANTS.errorTracePrefix() + " "
								+ caught.getLocalizedMessage());
				errDia.showCentered();

			}

			@Override
			public void onSuccess(ResponseDto<ArrayList<AccessPointDto>> result) {
				apTable.bindData(result.getPayload(), result.getCursorString(),
						isNew, isResort);

				if (result.getPayload() != null
						&& result.getPayload().size() > 0) {
					apTable.setVisible(true);
					if (!errorMode) {

						Button exportButton = new Button(
								TEXT_CONSTANTS.exportToExcel());
						apTable.appendRow(exportButton);
						exportButton.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								String appletString = "<applet width='100' height='30' code=com.gallatinsystems.framework.dataexport.applet.DataExportAppletImpl width=256 height=256 archive='exporterapplet.jar,json.jar'>";
								appletString += "<PARAM name='cache-archive' value='exporterapplet.jar, json.jar'><PARAM name='cache-version' value'1.3, 1.0'>";
								appletString += "<PARAM name='exportType' value='ACCESS_POINT'>";
								appletString += "<PARAM name='factoryClass' value='org.waterforpeople.mapping.dataexport.SurveyDataImportExportFactory'>";
								AccessPointSearchCriteriaDto crit = formSearchCriteria();
								if (crit != null) {
									appletString += "<PARAM name='criteria' value='"
											+ crit.toDelimitedString() + "'>";
								}
								appletString += "</applet>";
								HTML html = new HTML();
								html.setHTML(appletString);
								apTable.appendRow(html);
							}
						});
					}
				}
			}
		};
		if (!errorMode) {
			if (searchDto != null) {
				if (searchDto.getCollectionDateFrom() != null
						|| searchDto.getCollectionDateTo() != null) {
					if (searchDto.getConstructionDateFrom() != null
							|| searchDto.getConstructionDateTo() != null) {
						MessageDialog errDia = new MessageDialog(
								TEXT_CONSTANTS.inputError(),
								TEXT_CONSTANTS.onlyOneDateRange());
						errDia.showCentered();
						isOkay = false;
					}
					if (isOkay) {
						if (searchDto.getCollectionDateFrom() != null
								|| searchDto.getCollectionDateTo() != null) {
							if (isResort) {
								if (!"collectionDate".equals(apTable
										.getCurrentSortField())) {
									MessageDialog errDia = new MessageDialog(
											TEXT_CONSTANTS.inputError(),
											TEXT_CONSTANTS.dateSortError());
									errDia.showCentered();
									isOkay = false;
								}
							} else {
								apTable.overrideSort("collectionDate",
										PaginatedDataTable.DSC_SORT);
							}

						}
						if (searchDto.getConstructionDateFrom() != null
								|| searchDto.getConstructionDateTo() != null) {
							if (isResort) {
								if (!"constructionDate".equals(apTable
										.getCurrentSortField())) {
									MessageDialog errDia = new MessageDialog(
											TEXT_CONSTANTS.inputError(),
											TEXT_CONSTANTS
													.dateSortErrorConstruction());
									errDia.showCentered();
									isOkay = false;
								}
							} else {
								apTable.overrideSort("constructionDate",
										PaginatedDataTable.DSC_SORT);
							}
						}
						searchDto.setOrderBy(apTable.getCurrentSortField());
						searchDto.setOrderByDir(apTable
								.getCurrentSortDirection());
					}
				}
			}
			if (isOkay) {
				svc.listAccessPoints(searchDto, cursor, dataCallback);
			}
		} else {
			svc.listErrorAccessPoints(cursor, dataCallback);
		}
	}

	private ListBox addListBox(String value) {
		ListBox xLB = new ListBox();
		xLB.addItem(TEXT_CONSTANTS.unknown(), "unknown");
		xLB.addItem(TEXT_CONSTANTS.yes(), "yes");
		xLB.addItem(TEXT_CONSTANTS.no(), "no");
		xLB.addItem(TEXT_CONSTANTS.notApplicable(), "N/A");
		if (value == null)
			xLB.setSelectedIndex(0);
		else if (value.equalsIgnoreCase("true")
				|| value.equalsIgnoreCase("yes"))
			xLB.setSelectedIndex(1);
		else if (value.equalsIgnoreCase("false")
				|| value.equalsIgnoreCase("no"))
			xLB.setSelectedIndex(2);
		else
			xLB.setSelectedIndex(3);

		return xLB;
	}

	private Long getLongValueFromWidget(FlexTable ft, Integer row,
			Integer column) {
		String val = getValueFromWidget(ft, row, column);
		if (val != null && val.trim().length() > 0) {
			return new Long(val);
		} else {
			return null;
		}
	}

	private String getValueFromWidget(FlexTable ft, Integer row, Integer column) {
		if (ft.getWidget(row, column) instanceof ListBox) {
			return ((ListBox) ft.getWidget(row, column))
					.getItemText(((ListBox) ft.getWidget(row, column))
							.getSelectedIndex());
		} else if (ft.getWidget(row, column) instanceof TextBox
				|| ft.getWidget(row, column) instanceof TextArea) {
			return ((TextBox) ft.getWidget(row, column)).getText();
		}

		return null;
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE;
	}
}