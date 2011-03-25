package org.waterforpeople.mapping.portal.client.widgets.component;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto.AccessPointType;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityDto;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityService;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * control that can be used to build up Access Point search criteria objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointSearchControl extends Composite {
	private static TextConstants TEXT_CONSTANTS = GWT
	.create(TextConstants.class);
	
	private ListBox apTypeBox;
	public static final String ANY_OPT = TEXT_CONSTANTS.any();
	public static final String ALL_OPT = TEXT_CONSTANTS.all();
	private ListBox countryListbox;
	private ListBox communityListbox;
	private String specialOption;
	private CommunityServiceAsync communityService;
	private DateBox collectionDateFrom;
	private DateBox collectionDateTo;
	private DateBox constructionDateFrom;
	private DateBox constructionDateTo;

	public AccessPointSearchControl() {
		countryListbox = new ListBox();
		communityListbox = new ListBox();
		apTypeBox = new ListBox();
		collectionDateFrom = new DateBox();
		collectionDateTo = new DateBox();
		constructionDateFrom = new DateBox();
		constructionDateTo = new DateBox();
		specialOption = ANY_OPT;
		Grid grid = new Grid(4, 4);
		configureAccessPointListBox();
		grid.setWidget(0, 0, ViewUtil.initLabel(TEXT_CONSTANTS.country()));
		grid.setWidget(0, 1, countryListbox);
		grid.setWidget(0, 2, ViewUtil.initLabel(TEXT_CONSTANTS.community()));
		grid.setWidget(0, 3, communityListbox);
		grid.setWidget(1, 0, ViewUtil.initLabel(TEXT_CONSTANTS.collectionDateFrom()));
		grid.setWidget(1, 1, collectionDateFrom);
		grid.setWidget(1, 2, ViewUtil.initLabel(TEXT_CONSTANTS.to()));
		grid.setWidget(1, 3, collectionDateTo);
		grid.setWidget(2, 0, ViewUtil.initLabel(TEXT_CONSTANTS.pointType()));
		grid.setWidget(2, 1, apTypeBox);
		grid.setWidget(3, 0, ViewUtil.initLabel(TEXT_CONSTANTS.constructionDateFrom()));
		grid.setWidget(3, 1, constructionDateFrom);
		grid.setWidget(3, 2, ViewUtil.initLabel(TEXT_CONSTANTS.to()));
		grid.setWidget(3, 3, constructionDateTo);

		communityService = GWT.create(CommunityService.class);
		loadCountries();
		installChangeHandlers();

		initWidget(grid);
	}

	private void configureAccessPointListBox() {
		apTypeBox
				.addItem(TEXT_CONSTANTS.waterPoint(), AccessPointType.WATER_POINT.toString());
		apTypeBox.addItem(TEXT_CONSTANTS.sanitationPoint(), AccessPointType.SANITATION_POINT
				.toString());
		apTypeBox.addItem(TEXT_CONSTANTS.publicInst(),
				AccessPointType.PUBLIC_INSTITUTION.toString());
		apTypeBox.addItem(TEXT_CONSTANTS.school(), AccessPointType.SCHOOL.toString());

	}

	/**
	 * constructs a search criteria object using values from the form
	 * 
	 * @return
	 */
	public AccessPointSearchCriteriaDto getSearchCriteria() {
		AccessPointSearchCriteriaDto dto = new AccessPointSearchCriteriaDto();
		dto.setCommunityCode(getSelectedCommunity());
		dto.setCountryCode(getSelectedCountry());
		dto.setCollectionDateFrom(collectionDateFrom.getValue());
		dto.setCollectionDateTo(collectionDateTo.getValue());
		dto.setConstructionDateFrom(constructionDateFrom.getValue());
		dto.setConstructionDateTo(constructionDateTo.getValue());
		dto.setPointType(getSelectedValue(apTypeBox));
		return dto;
	}

	/**
	 * sets up anonymous callbacks for both country listbox
	 */
	private void installChangeHandlers() {
		countryListbox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String country = getSelectedValue(countryListbox);

				communityListbox.clear();
				// only proceed if they didn't select the "specialOption"
				if (country != null) {
					loadCommunities(country);
				} else {
					if (specialOption != null) {
						communityListbox.addItem(specialOption, specialOption);
					}
				}
			}
		});
	}

	/**
	 * loads the countries into the control
	 */
	private void loadCountries() {
		if (specialOption != null) {
			countryListbox.addItem(specialOption, specialOption);
			communityListbox.addItem(specialOption, specialOption);
		}

		// Set up the callback object.
		AsyncCallback<CountryDto[]> countryCallback = new AsyncCallback<CountryDto[]>() {
			public void onFailure(Throwable caught) {
				// no-op
			}

			public void onSuccess(CountryDto[] result) {
				if (result != null) {
					for (int i = 0; i < result.length; i++) {
						countryListbox.addItem(result[i].getName(), result[i]
								.getIsoAlpha2Code());
					}
				}
			}
		};
		communityService.listCountries(countryCallback);
	}

	protected void loadCommunities(String country) {
		if (specialOption != null) {
			communityListbox.addItem(specialOption, specialOption);
		}
		AsyncCallback<CommunityDto[]> communityCallback = new AsyncCallback<CommunityDto[]>() {
			@Override
			public void onFailure(Throwable caught) {
				// no-op
			}

			@Override
			public void onSuccess(CommunityDto[] result) {
				if (result != null) {
					for (int i = 0; i < result.length; i++) {
						communityListbox.addItem(result[i].getCommunityCode(),
								result[i].getCommunityCode());
					}
				}
			}
		};
		communityService.listCommunities(country, communityCallback);
	}

	/**
	 * helper method to get value out of a listbox. If the "specialOption" is
	 * selected, it's translated to null since we don't want to filter on
	 * country.
	 * 
	 * @param lb
	 * @return
	 */
	private String getSelectedValue(ListBox lb) {
		if (lb.getSelectedIndex() >= 0) {
			String val = lb.getValue(lb.getSelectedIndex());
			if (specialOption != null && specialOption.equals(val)) {
				return null;
			} else {
				return val;
			}
		} else {
			return null;
		}
	}

	/**
	 * sets the value of the location
	 * 
	 * @param val
	 */
	protected void setSelectedValue(String val, Widget widget) {
		ListBox control = (ListBox) widget;
		if ((val == null || "null".equalsIgnoreCase(val))
				&& specialOption != null) {
			control.setSelectedIndex(0);
		}
		for (int i = 0; i < control.getItemCount(); i++) {
			if (control.getValue(i).equals(val)) {
				control.setSelectedIndex(i);
				break;
			}
		}
	}

	/**
	 * returns selected country code
	 * 
	 * @return
	 */
	protected String getSelectedCountry() {
		return getSelectedValue(countryListbox);
	}

	/**
	 * returns selected community value
	 * 
	 * @return
	 */
	protected String getSelectedCommunity() {
		return getSelectedValue(communityListbox);
	}

}
