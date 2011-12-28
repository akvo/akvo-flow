package com.gallatinsystems.standards.domain;

import java.util.ArrayList;

import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.framework.domain.BaseDomain;

public class Standard extends BaseDomain {

	public enum StandardScope {
		Global, Local
	};
	
	public enum StandardType {
		NumberOfUsers, Downtime, WaterEveryDay, Quality, Quantity
	};

	/**
	 * 
	 */
	private static final long serialVersionUID = -4476247448049079794L;
	private AccessPointType accessPointType = null;
	private StandardScope standardScope = null;
	private String country = null;
	private String standardDescription = null;
	private String accessPointAttribute = null;
	private String acessPointAttributeType = null;
	private ArrayList<String> positiveValues = null;
	
	
	public String getStandardDescription() {
		return standardDescription;
	}

	public void setStandardDescription(String standardDescription) {
		this.standardDescription = standardDescription;
	}

	public String getAccessPointAttribute() {
		return accessPointAttribute;
	}

	public void setAccessPointAttribute(String accessPointAttribute) {
		this.accessPointAttribute = accessPointAttribute;
	}

	public String getAcessPointAttributeType() {
		return acessPointAttributeType;
	}

	public void setAcessPointAttributeType(String acessPointAttributeType) {
		this.acessPointAttributeType = acessPointAttributeType;
	}

	public ArrayList<String> getPositiveValues() {
		return positiveValues;
	}

	public void setPositiveValues(ArrayList<String> positiveValues) {
		this.positiveValues = positiveValues;
	}

	public StandardScope getStandardScope() {
		return standardScope;
	}

	public void setStandardScope(StandardScope standardScope) {
		this.standardScope = standardScope;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public AccessPointType getAccessPointType() {
		return accessPointType;
	}

	public void setAccessPointType(AccessPointType accessPointType) {
		this.accessPointType = accessPointType;
	}

}
