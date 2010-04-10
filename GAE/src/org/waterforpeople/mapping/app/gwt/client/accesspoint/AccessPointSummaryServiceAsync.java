package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccessPointSummaryServiceAsync {

	public void listAccessPointStatusSummary(String country, String community,
			String type, String year, String status,
			AsyncCallback<AccessPointSummaryDto[]> callback);

}
