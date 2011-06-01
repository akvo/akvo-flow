package org.waterforpeople.mapping.app.gwt.server.survey;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.gwt.client.survey.MetricDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyMetricMappingDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyMetricMappingService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.metric.dao.MetricDao;
import com.gallatinsystems.metric.dao.SurveyMetricMappingDao;
import com.gallatinsystems.metric.domain.Metric;
import com.gallatinsystems.metric.domain.SurveyMetricMapping;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * RPC service for saving and listing SurveyMetricMappings
 * 
 * @author Christopher Fagaini
 * 
 */
public class SurveyMetricMappingServiceImpl extends RemoteServiceServlet
		implements SurveyMetricMappingService {

	private static final long serialVersionUID = -7434464050885352388L;
	private static final Logger logger = Logger
			.getLogger(SurveyMetricMappingServiceImpl.class.getName());

	private SurveyMetricMappingDao mappingDao;
	private MetricDao metricDao;

	public SurveyMetricMappingServiceImpl() {
		mappingDao = new SurveyMetricMappingDao();
		metricDao = new MetricDao();
	}

	/**
	 * lists all mappings for a single survey
	 */
	@Override
	public List<SurveyMetricMappingDto> listMappingsBySurvey(Long surveyId) {
		List<SurveyMetricMapping> mappings = mappingDao
				.listMappingsBySurvey(surveyId);
		List<SurveyMetricMappingDto> dtoList = new ArrayList<SurveyMetricMappingDto>();
		if (mappings != null) {
			for (SurveyMetricMapping m : mappings) {
				SurveyMetricMappingDto dto = new SurveyMetricMappingDto();
				DtoMarshaller.copyToDto(m, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	/**
	 * lists all metrics, optionally filtered by organization
	 * 
	 * TODO: if needed, implement pagination and group filter
	 * 
	 * @param organizationName
	 * @return
	 */
	@Override
	public List<MetricDto> listMetrics(String organizationName) {
		List<Metric> metrics = metricDao.listMetricByOrg(organizationName,
				null, BaseDAO.CURSOR_TYPE.all.toString());
		List<MetricDto> dtoList = new ArrayList<MetricDto>();
		if (metrics != null) {
			for (Metric m : metrics) {
				MetricDto dto = new MetricDto();
				DtoMarshaller.copyToDto(m, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	/**
	 * saves all mappings within a single question group. Existing mappings for
	 * the same question group will be deleted prior to saving.
	 * 
	 * @param questionGroupId
	 * @param mappings
	 * @return
	 */
	@Override
	public List<SurveyMetricMappingDto> saveMappings(Long questionGroupId,
			List<SurveyMetricMappingDto> mappings) {
		if (mappings != null && mappings.size() > 0) {
			// first, delete all the old mappings
			mappingDao.deleteMappingsForQuestionGroup(questionGroupId);
			for (SurveyMetricMappingDto dto : mappings) {
				try {
					SurveyMetricMapping domain = new SurveyMetricMapping();
					DtoMarshaller.copyToCanonical(domain, dto);
					domain = mappingDao.save(domain);
					dto.setKeyId(domain.getKey().getId());
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Could not save mapping", e);
				}
			}
		}
		return mappings;
	}
}
