package org.waterforpeople.mapping.app.gwt.client.survey;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * transfer object for use with the Metric domain
 * 
 * @author Christopher Fagiani
 * 
 */
public class MetricDto extends BaseDto {
	private static final long serialVersionUID = 4592344869170645330L;
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
