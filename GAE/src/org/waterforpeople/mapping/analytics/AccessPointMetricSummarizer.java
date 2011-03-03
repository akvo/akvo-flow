package org.waterforpeople.mapping.analytics;

import java.util.List;

import org.waterforpeople.mapping.analytics.dao.AccessPointMetricSummaryDao;
import org.waterforpeople.mapping.analytics.domain.AccessPointMetricSummary;
import org.waterforpeople.mapping.dao.AccessPointMetricMappingDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPointMetricMapping;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;

/**
 * Summarizer for populating AccessPointMetricSummary objects based on values in
 * AccessPoint
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointMetricSummarizer implements DataSummarizer {

	@Override
	public boolean performSummarization(String key, String type, String value,
			Integer offset, String cursor) {
		AccessPointMetricSummaryDao summaryDao = new AccessPointMetricSummaryDao();
		AccessPointMetricMappingDao mappingDao = new AccessPointMetricMappingDao();
		if (key != null) {
			AccessPoint ap = summaryDao.getByKey(Long.parseLong(key),
					AccessPoint.class);
			if (ap != null) {
				List<AccessPointMetricMapping> mappingList = mappingDao
						.findMappings(ap.getOrganization(), null, null);
				if (mappingList != null) {
					for (AccessPointMetricMapping mapping : mappingList) {
						String fieldValue = AccessPointHelper
								.getAccessPointFieldAsString(ap, mapping
										.getFieldName());
						if (fieldValue != null
								&& fieldValue.trim().length() > 0) {
							AccessPointMetricSummary metricSummary = new AccessPointMetricSummary();
							metricSummary.setMetricValue(fieldValue);
							metricSummary.setMetricGroup(mapping
									.getMetricGroup());
							metricSummary
									.setMetricName(mapping.getMetricName());
							metricSummary.setOrganization(ap.getOrganization());
							metricSummary.setCountry(ap.getCountryCode());
							//metricSummary.setDistrict(ap.getDistrict());
							metricSummary.setValueBucket(bucketizeValue(
									mapping, fieldValue));
							AccessPointMetricSummaryDao.incrementCount(
									metricSummary, 1);
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * converts a raw value to a "bucketized" value (i.e. "Positive", "Neutral",
	 * "Negative", "Unknown"
	 * 
	 * @param mapping
	 * @param value
	 * @return
	 */
	private String bucketizeValue(AccessPointMetricMapping mapping, String value) {
		String bucket = AccessPointMetricMapping.UNKOWN_BUCKET;
		if (mapping.getPositiveValues() != null) {
			if (mapping.getPositiveValues().contains(value)) {
				bucket = AccessPointMetricMapping.POSITIVE_BUCKET;
			} else if (mapping.getNeutralValues().contains(value)) {
				bucket = AccessPointMetricMapping.NEUTRAL_BUCKET;
			} else if (mapping.getNegativeValues().contains(value)) {
				bucket = AccessPointMetricMapping.NEGATIVE_BUCKET;
			}
		}
		return bucket;
	}

	@Override
	public String getCursor() {
		return null;
	}

}
