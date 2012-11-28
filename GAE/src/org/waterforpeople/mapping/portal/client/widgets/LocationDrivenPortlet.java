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

package org.waterforpeople.mapping.portal.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.community.CommunityDto;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityService;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;
import org.waterforpeople.mapping.app.gwt.client.community.SubCountryDto;
import org.waterforpeople.mapping.app.gwt.client.util.TextConstants;

import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * base class for portlets that need/want to use community/country drop downs.
 * On load, it will load the list of countries into a ListBox widget (accessible
 * via getCountryControl()). If useCommunities (set via the constructor) is
 * true, then on selection of the country, the corresponding communities will be
 * loaded.
 * 
 * When the user selects a country/community, this portlet will call the
 * countrySelected or communitySelected (respectively). That is where one can
 * implement portlet-specific behavior.
 * 
 * @author Christopher Fagiani
 * 
 */
public abstract class LocationDrivenPortlet extends UserAwarePortlet {

	private boolean useCommunity;
	private static TextConstants TEXT_CONSTANTS = GWT
			.create(TextConstants.class);
	public static final String ANY_OPT = TEXT_CONSTANTS.any();
	public static final String ALL_OPT = TEXT_CONSTANTS.all();
	private ListBox countryListbox;
	private ListBox communityListbox;
	private List<ListBox> subLevelListboxes;
	private String specialOption;
	private int maxSubLevel;
	private CommunityServiceAsync communityService;

	/**
	 * constructs a new portlet
	 * 
	 * @param title
	 * @param scrollable
	 * @param configurable
	 * @param width
	 * @param height
	 * @param useCommunity
	 *            - flag indicating whether or not this portlet will make use of
	 *            communities
	 * @param specialOption
	 *            - either ALL_OPT or ANY_OPT (or null). If not null, then this
	 *            will appear as the first value in the Country list. Selection
	 *            of that value will mean that the call to getSelectedCountry
	 *            will return null.
	 */
	public LocationDrivenPortlet(String title, boolean scrollable,
			boolean configurable, boolean snapable, int width, int height,
			UserDto user, boolean useCommunity, String specialOption) {
		this(title, scrollable, configurable, snapable, width, height, user,
				useCommunity, 0, specialOption);
	}

	public LocationDrivenPortlet(String title, boolean scrollable,
			boolean configurable, boolean snapable, int width, int height,
			UserDto user, boolean useCommunity, int maxSubLevel,
			String specialOption) {
		super(title, scrollable, configurable, snapable, width, height, user);
		communityListbox = new ListBox();
		countryListbox = new ListBox();
		this.specialOption = specialOption;
		this.useCommunity = useCommunity;
		if (maxSubLevel > 0) {
			this.maxSubLevel = maxSubLevel;
			subLevelListboxes = new ArrayList<ListBox>();
			for (int i = 0; i < maxSubLevel; i++) {
				subLevelListboxes.add(new ListBox());
			}
		}
		communityService = GWT.create(CommunityService.class);
		loadCountries();
		installChangeHandlers();
	}

	/**
	 * returns the community selection control
	 * 
	 * @return
	 */
	protected Widget getCommunityControl() {
		return communityListbox;
	}

	/**
	 * returns the country selection control
	 * 
	 * @return
	 */
	protected Widget getCountryControl() {
		return countryListbox;
	}

	protected List<ListBox> getSubLevelControls() {
		return subLevelListboxes;
	}

	/**
	 * sets up anonymous callbacks for both country and community listboxes (so
	 * we don't have to worry about the subclasses being sure to call our
	 * onChange event (which would be the case if this class implemented
	 * OnChangeHandler)
	 */
	private void installChangeHandlers() {
		countryListbox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				String country = getSelectedValue(countryListbox);
				if (useCommunity) {
					communityListbox.clear();
					// only proceed if they didn't select the "specialOption"
					if (country != null) {
						loadCommunities(country);
					} else {
						if (specialOption != null) {
							communityListbox.addItem(specialOption,
									specialOption);
						}
					}
				} else if (maxSubLevel > 0) {
					for (ListBox box : subLevelListboxes) {
						box.clear();
					}
					if (country != null) {
						loadSubLevel(1, country, null);
					}
				}
				// now notify subclasses
				countrySelected(country);
			}
		});

		communityListbox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				String commmunity = getSelectedValue(communityListbox);
				// notify subclasses
				communitySelected(commmunity);
			}
		});

		if (maxSubLevel > 0) {
			for (int i = 0; i < subLevelListboxes.size(); i++) {
				final int level = i + 1;
				subLevelListboxes.get(i).addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						String selectedSubLevel = getSelectedValue(subLevelListboxes
								.get(level - 1));
						if (selectedSubLevel == null) {
							// if they selected the "special option", clear the
							// subsequent sub level boxes
							for (int j = level; j < subLevelListboxes.size(); j++) {
								subLevelListboxes.get(j).clear();
							}
							subLevelSelected(level, null);
						} else {
							if (level < maxSubLevel) {
								// if this isn't the terminal level, load the
								// next set
								loadSubLevel(level + 1, null, new Long(
										selectedSubLevel));

							}
							subLevelSelected(level, new Long(selectedSubLevel));
						}
					}
				});
			}
		}
	}

	/**
	 * loads the SubCountry box identified by the level
	 * 
	 * @param level
	 * @param key
	 */
	private void loadSubLevel(int level, String country, Long parentId) {
		if (level <= subLevelListboxes.size()) {
			final ListBox boxToLoad = subLevelListboxes.get(level - 1);
			AsyncCallback<List<SubCountryDto>> sublevelCallback = new AsyncCallback<List<SubCountryDto>>() {
				@Override
				public void onFailure(Throwable caught) {
					// no-op
				}

				@Override
				public void onSuccess(List<SubCountryDto> result) {
					if (specialOption != null) {
						boxToLoad.addItem(specialOption, specialOption);
					}
					if (result != null) {
						for (SubCountryDto dto : result) {
							boxToLoad.addItem(dto.getName(), dto.getKeyId()
									.toString());
						}
					}
				}
			};

			communityService.listChildSubCountries(country, parentId,
					sublevelCallback);

		}
	}

	/**
	 * loads the countries into the control
	 */
	private void loadCountries() {
		if (specialOption != null) {
			countryListbox.addItem(specialOption, specialOption);
			if (useCommunity) {
				communityListbox.addItem(specialOption, specialOption);
			}
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
				initialLoadComplete();
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
					if (result.length == 0) {
						communitySelected(null);
					} else {
						communitySelected(result[0].getCommunityCode());
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
	protected String getSelectedValue(ListBox lb) {
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
	 * returns a list of selected sub-levels. The list may contain nulls if
	 * there are gaps in the selection. There will be no more than maxSubLevel
	 * items in the list returned. If sublevel selection is not enabled on this
	 * instance, or if no sub levels are selected, the list will be empty.
	 * 
	 * @return
	 */
	protected List<String> getSelectedSubLevels() {
		List<String> levelList = new ArrayList<String>();
		if (maxSubLevel > 0) {
			for (int i = 0; i < subLevelListboxes.size(); i++) {
				levelList.add(getSelectedValue(subLevelListboxes.get(i)));
			}
		}
		return levelList;
	}

	/**
	 * returns selected community value
	 * 
	 * @return
	 */
	protected String getSelectedCommunity() {
		if (useCommunity) {
			return getSelectedValue(communityListbox);
		} else {
			return null;
		}
	}

	/**
	 * method called after the user selects a country. Subclasses should
	 * override this if they want to handle this event.
	 * 
	 * @param countryCode
	 */
	protected void countrySelected(String countryCode) {
	}

	/**
	 * method called after the user selects a community. Subclasses should
	 * override this if they want to handle this event.
	 * 
	 * @param communityCode
	 */
	protected void communitySelected(String communityCode) {
	}

	/**
	 * called when the initial load of countries is completed. This can be
	 * overriden by sub classes that want to take some action once data has been
	 * loaded.
	 */
	protected void initialLoadComplete() {
	}

	/**
	 * called when a sub level selection is made. Subclasses should override
	 * this if they want to handle this event.
	 * 
	 * @param level
	 * @param keyId
	 */
	protected void subLevelSelected(int level, Long keyId) {

	}
}
