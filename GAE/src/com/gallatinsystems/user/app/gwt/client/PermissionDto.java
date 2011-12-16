package com.gallatinsystems.user.app.gwt.client;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * dto for returning permission objects to the client
 * 
 * @author Christopher Fagiani
 * 
 */
public class PermissionDto extends BaseDto {

	private static final long serialVersionUID = 1590514521529275030L;
	private String code;
	private String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
