package org.waterforpeople.mapping.domain;

import java.util.List;

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

	public static final String UNKOWN_BUCKET = "UNKNOWN";
	public static final String POSITIVE_BUCKET = "POSITIVE";
	public static final String NEGATIVE_BUCKET = "NEGATIVE";
	public static final String NEUTRAL_BUCKET = "NEUTRAL";

	private String organization;
	private String metricName;
	private String metricGroup;
	private String fieldName;
	private List<String> positiveValues;
	private List<String> neutralValues;
	private List<String> negativeValues;

	public List<String> getPositiveValues() {
		return positiveValues;
	}

	public void setPositiveValues(List<String> positiveValues) {
		this.positiveValues = positiveValues;
	}

	public List<String> getNeutralValues() {
		return neutralValues;
	}

	public void setNeutralValues(List<String> neutralValues) {
		this.neutralValues = neutralValues;
	}

	public List<String> getNegativeValues() {
		return negativeValues;
	}

	public void setNegativeValues(List<String> negativeValues) {
		this.negativeValues = negativeValues;
	}

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
