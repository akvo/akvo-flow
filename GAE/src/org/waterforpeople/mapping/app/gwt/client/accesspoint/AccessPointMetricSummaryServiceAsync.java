package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AccessPointMetricSummaryServiceAsync {

	void listAccessPointMetricSummary(String metricType, String countryCode,
			String name, Integer level,
			AsyncCallback<List<AccessPointMetricSummaryDto>> callback);

	
}
