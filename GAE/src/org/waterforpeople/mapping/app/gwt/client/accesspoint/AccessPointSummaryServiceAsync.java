package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccessPointSummaryServiceAsync {

	void listAccessPointStatusSummary(String country, String community,
			String type, String year,
			AsyncCallback<AccessPointSummaryDto[]> callback);

}
