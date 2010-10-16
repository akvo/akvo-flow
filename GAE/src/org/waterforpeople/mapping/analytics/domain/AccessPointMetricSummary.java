package org.waterforpeople.mapping.analytics.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * summary to record counts of access point metrics for roll up by multiple
 * dimensions
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class AccessPointMetricSummary extends BaseDomain {

	private static final long serialVersionUID = 5145606518665763854L;
	private String organization;
	private String country;
	private String district;
	private String subgroup1;
	private String subgroup2;
	private String subgroup3;
	private String metricGroup;
	private String metricName;
	private String metricValue;
	private Long count;
	private Long year;
	private String valueBucket;

	public String getMetricGroup() {
		return metricGroup;
	}

	public void setMetricGroup(String metricGroup) {
		this.metricGroup = metricGroup;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public String getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(String metricValue) {
		this.metricValue = metricValue;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		this.year = year;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getSubgroup1() {
		return subgroup1;
	}

	public void setSubgroup1(String subgroup1) {
		this.subgroup1 = subgroup1;
	}

	public String getSubgroup2() {
		return subgroup2;
	}

	public void setSubgroup2(String subgroup2) {
		this.subgroup2 = subgroup2;
	}

	public String getSubgroup3() {
		return subgroup3;
	}

	public void setSubgroup3(String subgroup3) {
		this.subgroup3 = subgroup3;
	}

	public String getValueBucket() {
		return valueBucket;
	}

	public void setValueBucket(String valueBucket) {
		this.valueBucket = valueBucket;
	}

}
