package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.ArrayList;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * service for manipulating metrics
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("metricrpcservice")
public interface MetricService extends RemoteService {

	/**
	 * lists all metrics, optionally filtered by the parameters
	 * 
	 * @param name
	 * @param group
	 * @param valueType
	 * @param organizationName
	 * @param cursor
	 * @return
	 */
	public ResponseDto<ArrayList<MetricDto>> listMetrics(String name,
			String group, String valueType, String organizationName,
			String cursor);

	/**
	 * deletes the metric with the given ID.
	 * 
	 * @param id
	 */
	public void deleteMetric(Long id);

	/**
	 * saves/updates a metric. If it is a create (no key is set), the system
	 * will check for duplicates prior to saving. If a duplicate exists, the object returned will be the already present metric
	 * 
	 * @param metric
	 */
	public MetricDto saveMetric(MetricDto metric);
}
