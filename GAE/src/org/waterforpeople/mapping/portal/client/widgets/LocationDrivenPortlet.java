package org.waterforpeople.mapping.portal.client.widgets;

import org.waterforpeople.mapping.app.gwt.client.community.CommunityDto;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityService;
import org.waterforpeople.mapping.app.gwt.client.community.CommunityServiceAsync;
import org.waterforpeople.mapping.app.gwt.client.community.CountryDto;

import com.gallatinsystems.framework.gwt.portlet.client.Portlet;
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
public abstract class LocationDrivenPortlet extends Portlet {

	private boolean useCommunity;

	public static final String ANY_OPT = "Any";
	public static final String ALL_OPT = "All";
	private ListBox countryListbox;
	private ListBox communityListbox;
	private String specialOption;
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
			boolean configurable, int width, int height, boolean useCommunity,
			String specialOption) {
		super(title, scrollable, configurable, width, height);
		communityListbox = new ListBox();
		countryListbox = new ListBox();
		this.specialOption = specialOption;
		this.useCommunity = useCommunity;
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
						countryListbox.addItem(result[i].getName(), result[i]
								.getIsoAlpha2Code());
					}
				}
				initialLoadComplete();
			}
		};
		communityService.listCountries(countryCallback);
	}

	private void loadCommunities(String country) {
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
}
