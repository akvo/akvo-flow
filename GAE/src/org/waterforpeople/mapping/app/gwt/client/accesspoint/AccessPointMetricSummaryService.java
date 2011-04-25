package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * service for retrieval of AccessPointMetricSummary objects
 * 
 * @author Christopher Fagiani
 *
 */
@RemoteServiceRelativePath("apmetricsummaryrpcservice")
public interface AccessPointMetricSummaryService extends RemoteService{
	
	public List<AccessPointMetricSummaryDto> listAccessPointMetricSummary(
			String metricType, String countryCode, String name, Integer level);

}
