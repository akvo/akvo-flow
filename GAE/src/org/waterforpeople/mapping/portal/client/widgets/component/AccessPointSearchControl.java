/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.portal.client.widgets.component;

import java.util.ArrayList;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto.AccessPointType;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointSearchCriteriaDto;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityDto;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityService;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;
import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationItemDto;
import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationService;
import org.waterforpeople.mapping.app.gwt.client.config.ConfigurationServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricDto;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricService;
import org.waterforpeople.mapping.app.gwt.client.survey.MetricServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.framework.gwt.util.client.ViewUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
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
	private static final String POINT_TYPES_CONFIG = "pointTypes";
	private static final String WP_TYPE = "WaterPoint";
	private static final String SP_TYPE = "SanitationPoint";
	private static final String PI_TYPE = "PublicInstitution";
	private static final String HH_TYPE = "Household";
	private static final String SCHOOL_TYPE = "School";
	private static final String TRAWLER_TYPE = "Trawler";
	private static final String PROCEDURE_TYPE = "Procedure";

	public enum Mode {
		ACCESS_POINT, LOCALE
	};

	private ListBox apTypeBox;
	public static final String ANY_OPT = TEXT_CONSTANTS.any();
	public static final String ALL_OPT = TEXT_CONSTANTS.all();
	private ListBox countryListbox;
	private ListBox communityListbox;
	private String specialOption;
	private CommunityServiceAsync communityService;
	private MetricServiceAsync metricService;
	private DateBox collectionDateFrom;
	private DateBox collectionDateTo;
	private DateBox constructionDateFrom;
	private DateBox constructionDateTo;
	private TextBox metricValue;
	private ListBox metricListbox;
	private Mode mode;

	public AccessPointSearchControl() {
		this(Mode.ACCESS_POINT);
	}

	public AccessPointSearchControl(Mode m) {
		ConfigurationServiceAsync configService = GWT
				.create(ConfigurationService.class);
		configService.getConfigurationItem(POINT_TYPES_CONFIG,
				new AsyncCallback<ConfigurationItemDto>() {
					@Override
					public void onFailure(Throwable caught) {
						// add default set of points
						installPointTypes(null);
					}

					@Override
					public void onSuccess(ConfigurationItemDto result) {
						if (result != null) {
							installPointTypes(result.getValue().split(","));
						} else {
							installPointTypes(null);
						}
					}
				});
		mode = m;
		countryListbox = new ListBox();
		communityListbox = new ListBox();
		metricListbox = new ListBox();
		apTypeBox = new ListBox();
		collectionDateFrom = new DateBox();
		collectionDateTo = new DateBox();
		constructionDateFrom = new DateBox();
		constructionDateTo = new DateBox();
		metricValue = new TextBox();
		specialOption = ANY_OPT;
		Grid grid = new Grid(4, 4);
		grid.setWidget(0, 0, ViewUtil.initLabel(TEXT_CONSTANTS.country()));
		grid.setWidget(0, 1, countryListbox);
		if (Mode.ACCESS_POINT == mode) {
			grid.setWidget(0, 2, ViewUtil.initLabel(TEXT_CONSTANTS.community()));
			grid.setWidget(0, 3, communityListbox);
		}
		grid.setWidget(1, 0,
				ViewUtil.initLabel(TEXT_CONSTANTS.collectionDateFrom()));
		grid.setWidget(1, 1, collectionDateFrom);
		grid.setWidget(1, 2, ViewUtil.initLabel(TEXT_CONSTANTS.to()));
		grid.setWidget(1, 3, collectionDateTo);
		grid.setWidget(2, 0, ViewUtil.initLabel(TEXT_CONSTANTS.pointType()));
		grid.setWidget(2, 1, apTypeBox);
		if (Mode.ACCESS_POINT == mode) {
			grid.setWidget(3, 0,
					ViewUtil.initLabel(TEXT_CONSTANTS.constructionDateFrom()));
			grid.setWidget(3, 1, constructionDateFrom);
			grid.setWidget(3, 2, ViewUtil.initLabel(TEXT_CONSTANTS.to()));
			grid.setWidget(3, 3, constructionDateTo);
		} else {
			grid.setWidget(3, 0, ViewUtil.initLabel(TEXT_CONSTANTS.metric()));
			grid.setWidget(3, 1, metricListbox);
			grid.setWidget(3, 2, ViewUtil.initLabel(TEXT_CONSTANTS.value()));
			grid.setWidget(3, 3, metricValue);
		}

		communityService = GWT.create(CommunityService.class);
		metricService = GWT.create(MetricService.class);
		loadCountries();
		if (Mode.ACCESS_POINT == m) {
			installChangeHandlers();
		} else {
			loadMetrics();
		}

		initWidget(grid);
	}

	private void installPointTypes(String[] pointTypes) {
		if (Mode.LOCALE == mode) {
			apTypeBox.addItem(ANY_OPT, ANY_OPT);
			if (pointTypes == null || pointTypes.length == 0) {
				apTypeBox.addItem(TEXT_CONSTANTS.waterPoint(), WP_TYPE);
				apTypeBox.addItem(TEXT_CONSTANTS.sanitationPoint(), SP_TYPE);
				apTypeBox.addItem(TEXT_CONSTANTS.publicInst(), PI_TYPE);
				apTypeBox.addItem(TEXT_CONSTANTS.school(), SCHOOL_TYPE);
				apTypeBox.addItem(TEXT_CONSTANTS.household(), HH_TYPE);
			} else {
				for (int i = 0; i < pointTypes.length; i++) {
					if (WP_TYPE.equalsIgnoreCase(pointTypes[i].trim())) {
						apTypeBox.addItem(TEXT_CONSTANTS.waterPoint(), WP_TYPE);
					} else if (SP_TYPE.equalsIgnoreCase(pointTypes[i].trim())) {
						apTypeBox.addItem(TEXT_CONSTANTS.sanitationPoint(),
								SP_TYPE);
					} else if (PI_TYPE.equalsIgnoreCase(pointTypes[i].trim())) {
						apTypeBox.addItem(TEXT_CONSTANTS.publicInst(), PI_TYPE);
					} else if (TRAWLER_TYPE.equalsIgnoreCase(pointTypes[i]
							.trim())) {
						apTypeBox.addItem(TEXT_CONSTANTS.trawler(),
								TRAWLER_TYPE);
					} else if (SCHOOL_TYPE.equalsIgnoreCase(pointTypes[i]
							.trim())) {
						apTypeBox.addItem(TEXT_CONSTANTS.school(), SCHOOL_TYPE);
					} else if (PROCEDURE_TYPE.equalsIgnoreCase(pointTypes[i]
							.trim())) {
						apTypeBox.addItem(TEXT_CONSTANTS.procedure(),
								PROCEDURE_TYPE);
					} else if (HH_TYPE.equalsIgnoreCase(pointTypes[i].trim())) {
						apTypeBox.addItem(TEXT_CONSTANTS.household(), HH_TYPE);
					}
				}
			}
		} else {
			apTypeBox.addItem(TEXT_CONSTANTS.waterPoint(),
					AccessPointType.WATER_POINT.toString());
			apTypeBox.addItem(TEXT_CONSTANTS.sanitationPoint(),
					AccessPointType.SANITATION_POINT.toString());
			apTypeBox.addItem(TEXT_CONSTANTS.publicInst(),
					AccessPointType.PUBLIC_INSTITUTION.toString());
			apTypeBox.addItem(TEXT_CONSTANTS.school(),
					AccessPointType.SCHOOL.toString());
		}
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
		String id = getSelectedValue(metricListbox);
		if (id != null && id.trim().length() > 0) {
			dto.setMetricId(id);
		}
		dto.setMetricValue(metricValue.getText());
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
						countryListbox.addItem(result[i].getName(),
								result[i].getIsoAlpha2Code());
					}
				}
			}
		};
		communityService.listCountries(countryCallback);
	}

	/**
	 * loads the metrics into the control
	 */
	private void loadMetrics() {
		// TODO: parameterize with Organization name
		// TODO: see if we need to progressively load the list or paginate or
		// something if this gets to be too big
		metricService.listMetrics(null, null, null, null, "all",
				new AsyncCallback<ResponseDto<ArrayList<MetricDto>>>() {
					public void onFailure(Throwable caught) {
						// no-op
					}

					public void onSuccess(
							ResponseDto<ArrayList<MetricDto>> result) {
						metricListbox.addItem("", "");
						if (result != null && result.getPayload() != null) {
							for (MetricDto metric : result.getPayload())
								metricListbox.addItem(metric.getName(), metric
										.getKeyId().toString());
						}
					}
				});
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
		if (lb != null && lb.getSelectedIndex() >= 0) {
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
