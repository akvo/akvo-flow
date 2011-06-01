package org.waterforpeople.mapping.app.gwt.client.survey;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * RPC service for mapping survey questions to Metric objects
 * 
 * @author Christopher Fagiani
 * 
 */
@RemoteServiceRelativePath("surveymetricmaprpcservice")
public interface SurveyMetricMappingService extends RemoteService {

	/**
	 * lists all mappings for a single survey
	 */
	public List<SurveyMetricMappingDto> listMappingsBySurvey(Long surveyId);

	/**
	 * lists all metrics, optionally filtered by organization
	 * 
	 * @param organizationName
	 * @return
	 */
	public List<MetricDto> listMetrics(String organizationName);

	/**
	 * saves all mappings within a single quesiton group. Existing mappings for
	 * the same question group will be deleted prior to saving.
	 * 
	 * @param questionGroupId
	 * @param mappings
	 * @return
	 */
	public List<SurveyMetricMappingDto> saveMappings(Long questionGroupId,
			List<SurveyMetricMappingDto> mappings);

}
