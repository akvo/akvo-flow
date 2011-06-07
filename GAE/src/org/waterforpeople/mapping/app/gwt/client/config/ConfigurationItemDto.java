package org.waterforpeople.mapping.app.gwt.client.config;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * transfer object for configuration values
 * 
 * @author Christopher Fagiani
 * 
 */
public class ConfigurationItemDto extends BaseDto {
		
	private static final long serialVersionUID = -5865337450952618714L;
	private String name;
	private String value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
