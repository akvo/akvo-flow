package org.waterforpeople.mapping.domain;

import java.util.ArrayList;

import com.gallatinsystems.framework.domain.BaseDomain;

public class AccessPointStatus extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5126337862102276916L;
	private String code = null;
	private String name = null;
	private String description = null;
	private String iconUrl = null;
	private ArrayList<String> mapToStrings = null;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<String> getMapToStrings() {
		return mapToStrings;
	}

	public void setMapToStrings(ArrayList<String> mapToStrings) {
		this.mapToStrings = mapToStrings;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getIconUrl() {
		return iconUrl;
	}
}
