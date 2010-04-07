package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.Date;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerService;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointManagerServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointTechnologyTypeDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto.AccessPointType;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
import com.gallatinsystems.framework.gwt.portlet.client.PortletEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class AccessPointManagerPortlet extends Portlet {
	public static final String DESCRIPTION = "Create/Edit/Delete Access Points";
	public static final String NAME = "Access Point Manager";

	private static final String ALL_OPT = "All";
	private static final int WIDTH = 600;
	private static final int HEIGHT = 800;
	private VerticalPanel contentPane;

	// Search UI Elements
	private VerticalPanel mainVPanel = new VerticalPanel();
	private FlexTable searchTable = new FlexTable();

	private Label accessPointTypeLabel = new Label("Access Point Type");
	private ListBox accessPointTypeListBox = new ListBox();

	private Label technologyTypeLabel = new Label("Technology Type");
	private ListBox techTypeListBox = new ListBox();

	private Button searchButton = new Button("Search");

	private FlexTable accessPointFT = new FlexTable();

	private AccessPointManagerServiceAsync svc;

	private Label statusLabel = new Label();

	private ListBox communityLB = new ListBox();
	private ListBox countryLB = new ListBox();
	private DatePicker collectionDateDPLower = new DatePicker();
	private DatePicker collectionDateDPUpper = new DatePicker();
	private DatePicker constructionDateDPLower = new DatePicker();
	private DatePicker constructionDateDPUpper = new DatePicker();

	private ListBox statusLB = new ListBox();

	public AccessPointManagerPortlet() {
		super(NAME, false, false, WIDTH, HEIGHT);
		contentPane = new VerticalPanel();
		Widget header = buildHeader();
		contentPane.add(header);
		setContent(contentPane);
		svc = GWT.create(AccessPointManagerService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) svc;
		endpoint
				.setServiceEntryPoint("/org.waterforpeople.mapping.portal.portal/apmanagerrpcservice");
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected boolean getReadyForRemove() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void handleConfigClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(PortletEvent e) {
		// TODO Auto-generated method stub

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
				processClickEvent();
			}
		});

		accessPointTypeListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				configureTechTypeListBox(event);
			}

		});

		createNewAccessPoint.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AccessPointDto nullItem = null;
				loadAccessPointDetailTable(nullItem);
			}

		});

		return grid;
	}

	private Button createNewAccessPoint = new Button("Create New Access Point");

	private void configureSearchRibbon() {
		configureDependantControls();
		searchTable.setWidget(0, 0, new Label("Country"));
		searchTable.setWidget(0, 1, countryLB);
		searchTable.setWidget(0, 2, new Label("Community"));
		searchTable.setWidget(0, 3, communityLB);
		searchTable.setWidget(1, 0, new Label("Collection Date from: "));
		searchTable.setWidget(1, 1, collectionDateDPLower);
		searchTable.setWidget(1, 2, new Label("to"));
		searchTable.setWidget(1, 3, collectionDateDPUpper);
		searchTable.setWidget(2, 0, accessPointTypeLabel);
		searchTable.setWidget(2, 1, accessPointTypeListBox);
		searchTable.setWidget(2, 2, technologyTypeLabel);
		searchTable.setWidget(2, 3, techTypeListBox);
		searchTable.setWidget(3, 0, searchButton);
		searchTable.setWidget(3, 1, createNewAccessPoint);
		searchTable.setWidget(4, 0, new Label("Construction Date From: "));
		searchTable.setWidget(4, 1, constructionDateDPLower);
		searchTable.setWidget(4, 2, constructionDateDPUpper);

		mainVPanel.add(searchTable);
	}

	private void configureDependantControls() {
		configureAccessPointListBox();
		configureTechnologyType();
	}

	private void configureTechnologyType() {

	}

	private void configureAccessPointListBox() {
		accessPointTypeListBox.addItem("Water Point");
		accessPointTypeListBox.addItem("Sanitation Point");
	}

	private void configureTechTypeListBox(ChangeEvent event) {
		AccessPointTechnologyTypeDto techType;
	}

	private void processClickEvent() {
		statusLabel.setText("Please wait loading access points");
		statusLabel.setVisible(true);
		mainVPanel.add(statusLabel);

		svc.listAllAccessPoints(0, 0, new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Object result) {
				loadAccessPoint((ArrayList<AccessPointDto>) result);
			}

		});

	}

	private void loadAccessPoint(ArrayList<AccessPointDto> apDtoList) {

		loadAccessPointHeaderRow();
		int i = 1;

		for (AccessPointDto apDto : apDtoList) {
			Label keyIdLabel = new Label(apDto.getKeyId().toString());
			// keyIdLabel.setVisible(false);
			accessPointFT.setWidget(i, 0, keyIdLabel);
			accessPointFT.setWidget(i, 1, new Label(apDto.getCommunityCode()));
			accessPointFT.setWidget(i, 2, new Label(apDto.getLatitude()
					.toString()));
			accessPointFT.setWidget(i, 3, new Label(apDto.getLongitude()
					.toString()));
			Button editAccessPoint = new Button("edit");
			editAccessPoint.setTitle(keyIdLabel.getText());
			Button deleteAccessPoint = new Button("delete");
			deleteAccessPoint.setTitle(keyIdLabel.getText());
			HorizontalPanel buttonHPanel = new HorizontalPanel();
			buttonHPanel.add(editAccessPoint);
			buttonHPanel.add(deleteAccessPoint);

			editAccessPoint.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Button pressedButton = (Button) event.getSource();
					Long itemId = new Long(pressedButton.getTitle());
					loadAccessPointDetailTable(itemId);
				}

			});

			deleteAccessPoint.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Button pressedButton = (Button) event.getSource();
					Window.alert("delete key id: " + pressedButton.getTitle());
				}

			});
			accessPointFT.setWidget(i, 4, buttonHPanel);
			i++;
			statusLabel
					.setText("loading row: " + i + " of " + apDtoList.size());

		}

		statusLabel.setText("Done loading access points");
		statusLabel.setVisible(false);
		mainVPanel.remove(statusLabel);

		mainVPanel.add(accessPointFT);

	}

	private void loadAccessPointHeaderRow() {
		accessPointFT.setWidget(0, 0, new Label("Id"));
		accessPointFT.setWidget(0, 1, new Label("Community Code"));
		accessPointFT.setWidget(0, 2, new Label("Latitude"));
		accessPointFT.setWidget(0, 3, new Label("Longitude"));
		accessPointFT.setWidget(0, 4, new Label("Edit/Delete"));
	}

	private FlexTable accessPointDetail = new FlexTable();

	private void loadAccessPointDetailTable(Long id) {
		accessPointFT.setVisible(false);
		statusLabel.setText("Please wait loading access point for edit");
		statusLabel.setVisible(true);
		mainVPanel.add(statusLabel);
		svc.getAccessPoint(id, new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Object result) {
				AccessPointDto item = (AccessPointDto) result;
				loadAccessPointDetailTable(item);
			}

		});
	}

	private void loadAccessPointDetailTable(AccessPointDto accessPointDto) {

		accessPointDetail.setWidget(0, 0, new Label("Community Code: "));
		TextBox communityCodeTB = new TextBox();
		if (accessPointDto != null)
			communityCodeTB.setText(accessPointDto.getCommunityCode());
		accessPointDetail.setWidget(0, 1, communityCodeTB);

		accessPointDetail.setWidget(1, 0, new Label("Latititude: "));
		TextBox latitudeTB = new TextBox();
		if (accessPointDto != null)
			latitudeTB.setText(accessPointDto.getLatitude().toString());
		accessPointDetail.setWidget(1, 1, latitudeTB);

		accessPointDetail.setWidget(2, 0, new Label("Longitude: "));
		TextBox longitudeTB = new TextBox();
		if (accessPointDto != null)
			longitudeTB.setText(accessPointDto.getLongitude().toString());
		accessPointDetail.setWidget(2, 1, longitudeTB);

		// accessPointDetail.setWidget(3, 0, new Label("Collection Date: "));
		// TextBox collectionDateTB = new TextBox();
		// collectionDateTB.setText(accessPointDto.getCollectionDate()
		// .toLocaleString());
		// accessPointDetail.setWidget(3, 1, collectionDateTB);
		accessPointDetail.setWidget(3, 0, new Label("Collection Date: "));
		DatePicker pickerCollectionDate = new DatePicker();
		if (accessPointDto != null)
			pickerCollectionDate.setValue(accessPointDto.getConstructionDate());
		accessPointDetail.setWidget(3, 1, pickerCollectionDate);

		// accessPointDetail.setWidget(4, 0, new
		// Label("Point Construction Date"));
		// TextBox constructionDateTB = new TextBox();
		// constructionDateTB.setText(accessPointDto.getConstructionDate());
		// accessPointDetail.setWidget(4, 1, constructionDateTB);

		accessPointDetail.setWidget(4, 0, new Label("Point Construction Date"));
		DatePicker pickerConstructionDate = new DatePicker();
		if (accessPointDto != null)
			pickerConstructionDate.setValue(accessPointDto
					.getConstructionDate());
		accessPointDetail.setWidget(4, 1, pickerConstructionDate);

		accessPointDetail.setWidget(5, 0, new Label("Cost Per: "));
		TextBox costPerTB = new TextBox();
		if (accessPointDto != null)
			costPerTB.setText(accessPointDto.getCostPer());
		accessPointDetail.setWidget(5, 1, costPerTB);

		accessPointDetail.setWidget(6, 0, new Label(
				"Current Management Structure: "));
		TextBox currentMgmtStructureTB = new TextBox();
		if (accessPointDto != null)
			currentMgmtStructureTB.setText(accessPointDto
					.getCurrentManagementStructurePoint());
		accessPointDetail.setWidget(6, 1, currentMgmtStructureTB);

		accessPointDetail.setWidget(7, 0, new Label("Description: "));
		TextBox descTB = new TextBox();
		if (accessPointDto != null)
			descTB.setText(accessPointDto.getDescription());
		accessPointDetail.setWidget(7, 1, descTB);

		accessPointDetail.setWidget(8, 0, new Label(
				"Number of Households using Point: "));
		TextBox numHouseholdsTB = new TextBox();
		if (accessPointDto != null)
			numHouseholdsTB.setText(accessPointDto
					.getNumberOfHouseholdsUsingPoint());
		accessPointDetail.setWidget(8, 1, numHouseholdsTB);

		accessPointDetail.setWidget(9, 0, new Label("Photo Url: "));
		TextBox photoURLTB = new TextBox();
		if (accessPointDto != null)
			photoURLTB.setText(accessPointDto.getPhotoURL());
		accessPointDetail.setWidget(9, 1, photoURLTB);

		accessPointDetail.setWidget(10, 0, new Label("Photo Caption: "));
		TextBox captionTB = new TextBox();
		if (accessPointDto != null)
			captionTB.setText(accessPointDto.getPointPhotoCaption());
		accessPointDetail.setWidget(10, 1, captionTB);

		accessPointDetail.setWidget(11, 0, new Label("Point Status: "));
		TextBox statusTB = new TextBox();
		if (accessPointDto != null)
			statusTB.setText(accessPointDto.getPointStatus());
		accessPointDetail.setWidget(11, 1, statusTB);

		accessPointDetail.setWidget(12, 0, new Label("Point Type: "));

		ListBox pointType = new ListBox();
		pointType.addItem("Water Point");
		pointType.addItem("Sanitation Type");
		if (accessPointDto != null) {
			AccessPointType apType = accessPointDto.getPointType();
			if (apType.equals(AccessPointType.WATER_POINT)) {
				pointType.setSelectedIndex(0);
			} else {
				pointType.setSelectedIndex(1);
			}
		} else {
			pointType.setSelectedIndex(0);
		}
		accessPointDetail.setWidget(12, 1, pointType);

		accessPointDetail.setWidget(13, 0, new Label("Farthest Point From"));
		TextBox farthestPointFromTB = new TextBox();
		if (accessPointDto != null)
			farthestPointFromTB.setText(accessPointDto
					.getFarthestHouseholdfromPoint());
		accessPointDetail.setWidget(13, 1, farthestPointFromTB);
		Label apId;
		if (accessPointDto != null) {
			apId = new Label(accessPointDto.getKeyId().toString());
		} else {
			apId = new Label("-1");
		}
		apId.setVisible(false);
		accessPointDetail.setWidget(14, 1, apId);

		// if(accessPointDto.getPointType().toString()!=null){
		// type = accessPointDto.getPointType().toString();
		// pointTypeTB.setText(type);
		// accessPointDetail.setWidget(12, 1, pointTypeTB);
		// }

		statusLabel.setText("Done loading access point");
		statusLabel.setVisible(false);
		mainVPanel.remove(statusLabel);

		Button saveButton = new Button("Save");

		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (validateAccessPointDetail()) {
					accessPointDetail.setVisible(false);
					statusLabel.setVisible(true);
					statusLabel.setText("Please wait saving access point");
					mainVPanel.add(statusLabel);
					AccessPointDto apDto = buildAccessPointDto();
					svc.saveAccessPoint(apDto, new AsyncCallback() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(Object result) {
							Window.alert("Access Point successfully updated");
						}

					});
					statusLabel.setVisible(false);
					mainVPanel.remove(statusLabel);
					accessPointFT.setVisible(true);
				}
			}

		});
		Button cancelButton = new Button("Cancel");
		accessPointDetail.setWidget(15, 0, saveButton);
		accessPointDetail.setWidget(15, 1, cancelButton);
		accessPointDetail.setVisible(true);
		mainVPanel.add(accessPointDetail);
	}

	private AccessPointDto buildAccessPointDto() {
		AccessPointDto apDto = new AccessPointDto();

		Label apId = (Label) accessPointDetail.getWidget(14, 1);
		Long id = new Long(apId.getText());
		if (id > -1) {
			apDto.setKeyId(id);
		} else {
			apDto.setKeyId(null);
		}
		TextBox communityCodeTB = (TextBox) accessPointDetail.getWidget(0, 1);
		String communityCode = communityCodeTB.getText();
		apDto.setCommunityCode(communityCode);

		TextBox latitudeTB = (TextBox) accessPointDetail.getWidget(1, 1);
		Double latitude = new Double(latitudeTB.getText());
		apDto.setLatitude(latitude);

		TextBox longitudeTB = (TextBox) accessPointDetail.getWidget(2, 1);
		Double longitude = new Double(longitudeTB.getText());
		apDto.setLongitude(longitude);

		DatePicker collectionDateTB = (DatePicker) accessPointDetail.getWidget(
				3, 1);
		apDto.setCollectionDate(collectionDateTB.getValue());

		DatePicker constructionDateTB = (DatePicker) accessPointDetail
				.getWidget(4, 1);
		apDto.setConstructionDate(constructionDateTB.getValue());

		TextBox costPerTB = (TextBox) accessPointDetail.getWidget(5, 1);
		String costPer = costPerTB.getText();
		apDto.setCostPer(costPer);

		TextBox currentMgmtStructureTB = (TextBox) accessPointDetail.getWidget(
				6, 1);
		String currentMgmtStructure = currentMgmtStructureTB.getText();
		apDto.setCurrentManagementStructurePoint(currentMgmtStructure);

		TextBox descTB = (TextBox) accessPointDetail.getWidget(7, 1);
		String desc = descTB.getText();
		apDto.setDescription(desc);

		TextBox numHouseholdsTB = (TextBox) accessPointDetail.getWidget(8, 1);
		String numHouseholds = numHouseholdsTB.getText();
		apDto.setNumberOfHouseholdsUsingPoint(numHouseholds);

		TextBox photoURLTB = (TextBox) accessPointDetail.getWidget(9, 1);
		String photoUrl = photoURLTB.getText();
		apDto.setPhotoURL(photoUrl);

		TextBox captionTB = (TextBox) accessPointDetail.getWidget(10, 1);
		String caption = captionTB.getText();
		apDto.setPointPhotoCaption(caption);

		TextBox statusTB = (TextBox) accessPointDetail.getWidget(11, 1);
		String status = statusTB.getText();
		apDto.setPointStatus(status);

		ListBox pointTypeLB = (ListBox) accessPointDetail.getWidget(12, 1);
		Integer selectedIndex = pointTypeLB.getSelectedIndex();
		String type = pointTypeLB.getItemText(selectedIndex);
		if (type.equals("Water Point")) {
			apDto.setPointType(AccessPointType.WATER_POINT);
		} else {
			apDto.setPointType(AccessPointType.SANITATION_POINT);
		}

		TextBox farthestPointFromTB = (TextBox) accessPointDetail.getWidget(13,
				1);
		String farthestPointFrom = farthestPointFromTB.getText();
		apDto.setFarthestHouseholdfromPoint(farthestPointFrom);

		return apDto;
	}

	public Boolean validateAccessPointDetail() {
		return true;
	}
}
