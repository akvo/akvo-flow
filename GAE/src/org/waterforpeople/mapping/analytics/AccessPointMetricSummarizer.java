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
							String valBucket = bucketizeValue(mapping,
									fieldValue);
							String metricName = (mapping.getMetricName() != null ? mapping
									.getMetricName()
									: mapping.getFieldName());
							AccessPointMetricSummary metricSummary = constructBaseSummary(
									fieldValue, mapping.getMetricGroup(),
									metricName, ap.getOrganization(), ap
											.getCountryCode(), valBucket);
							AccessPointMetricSummaryDao.incrementCount(
									metricSummary, 1);
							if (ap.getSub1() != null) {
								metricSummary = constructBaseSummary(
										fieldValue, mapping.getMetricGroup(),
										metricName, ap.getOrganization(), ap
												.getCountryCode(), valBucket);
								metricSummary.setSubLevel(1);
								metricSummary.setSubValue(ap.getSub1());
								AccessPointMetricSummaryDao.incrementCount(
										metricSummary, 1);
							}
							if (ap.getSub1() != null) {
								metricSummary = constructBaseSummary(
										fieldValue, mapping.getMetricGroup(),
										metricName, ap.getOrganization(), ap
												.getCountryCode(), valBucket);
								metricSummary.setSubLevel(1);
								metricSummary.setSubValue(ap.getSub1());
								AccessPointMetricSummaryDao.incrementCount(
										metricSummary, 1);
							}
							if (ap.getSub2() != null) {
								metricSummary = constructBaseSummary(
										fieldValue, mapping.getMetricGroup(),
										metricName, ap.getOrganization(), ap
												.getCountryCode(), valBucket);
								metricSummary.setSubLevel(2);
								metricSummary.setSubValue(ap.getSub2());
								AccessPointMetricSummaryDao.incrementCount(
										metricSummary, 1);
							}
							if (ap.getSub3() != null) {
								metricSummary = constructBaseSummary(
										fieldValue, mapping.getMetricGroup(),
										metricName, ap.getOrganization(), ap
												.getCountryCode(), valBucket);
								metricSummary.setSubLevel(3);
								metricSummary.setSubValue(ap.getSub3());
								AccessPointMetricSummaryDao.incrementCount(
										metricSummary, 1);
							}
							if (ap.getSub4() != null) {
								metricSummary = constructBaseSummary(
										fieldValue, mapping.getMetricGroup(),
										metricName, ap.getOrganization(), ap
												.getCountryCode(), valBucket);
								metricSummary.setSubLevel(4);
								metricSummary.setSubValue(ap.getSub4());
								AccessPointMetricSummaryDao.incrementCount(
										metricSummary, 1);
							}
							if (ap.getSub5() != null) {
								metricSummary = constructBaseSummary(
										fieldValue, mapping.getMetricGroup(),
										metricName, ap.getOrganization(), ap
												.getCountryCode(), valBucket);
								metricSummary.setSubLevel(5);
								metricSummary.setSubValue(ap.getSub5());
								AccessPointMetricSummaryDao.incrementCount(
										metricSummary, 1);
							}
							if (ap.getSub6() != null) {
								metricSummary = constructBaseSummary(
										fieldValue, mapping.getMetricGroup(),
										metricName, ap.getOrganization(), ap
												.getCountryCode(), valBucket);
								metricSummary.setSubLevel(6);
								metricSummary.setSubValue(ap.getSub6());
								AccessPointMetricSummaryDao.incrementCount(
										metricSummary, 1);
							}
						}
					}
				}
			}
		}
		return true;
	}

	private AccessPointMetricSummary constructBaseSummary(String fieldValue,
			String metricGroup, String metricName, String org, String country,
			String valueBucket) {
		AccessPointMetricSummary metricSummary = new AccessPointMetricSummary();
		metricSummary.setMetricValue(fieldValue);
		metricSummary.setMetricGroup(metricGroup);
		metricSummary.setMetricName(metricName);
		metricSummary.setOrganization(org);
		metricSummary.setCountry(country);
		metricSummary.setValueBucket(valueBucket);
		return metricSummary;
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
