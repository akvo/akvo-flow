package org.waterforpeople.mapping.app.gwt.client.community;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Lists community and country information
 * 
 * @author Christopher Fagiani
 *
 */
@RemoteServiceRelativePath("communityrpcservice")
public interface CommunityService extends RemoteService {

	public CommunityDto[] listCommunities(String countryCode);

	public CountryDto[] listCountries();
}
