package org.waterforpeople.mapping.app.gwt.client.community;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommunityServiceAsync {

	void listCommunities(String countryCode,
			AsyncCallback<CommunityDto[]> callback);

	void listCountries(AsyncCallback<CountryDto[]> callback);

}
