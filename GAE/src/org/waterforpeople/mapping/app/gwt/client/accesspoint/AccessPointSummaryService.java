package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * lists access point summary info
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("apsummaryrpcservice")
public interface AccessPointSummaryService extends RemoteService {

	public AccessPointSummaryDto[] listAccessPointStatusSummary(String country,
			String community, String type, String year, String status);
	
	public AccessPointSummaryDto[] listAccessPointStatusSummaryWithoutRollup(String country,
	String community, String type, String year, String status);
}
