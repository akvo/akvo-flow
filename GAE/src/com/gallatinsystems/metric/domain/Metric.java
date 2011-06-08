package com.gallatinsystems.metric.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Represents a metric about which we want to capture data.
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class Metric extends BaseDomain {

	private static final long serialVersionUID = 2501689702365825679L;
	private String organization;
	private String name;
	private String group;
	private String valueType;

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

}
