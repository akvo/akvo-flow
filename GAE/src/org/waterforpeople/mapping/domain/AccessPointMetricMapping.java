package org.waterforpeople.mapping.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * domain for mapping fields within AccessPoint to Metric values
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class AccessPointMetricMapping extends BaseDomain {

	private static final long serialVersionUID = -4247381034949232233L;

	private String organization;
	private String metricName;
	private String metricGroup;
	private String fieldName;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public String getMetricGroup() {
		return metricGroup;
	}

	public void setMetricGroup(String metricGroup) {
		this.metricGroup = metricGroup;
	}
}
