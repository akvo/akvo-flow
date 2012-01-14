package com.gallatinsystems.standards.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.framework.domain.BaseDomain;
@PersistenceCapable
public class Standard extends BaseDomain implements StandardDef{

	public enum StandardScope {
		Global, Local
	};
	
	public enum StandardType {
		WaterPointLevelOfService, WaterPointSustainability
	};
	
	public enum StandardValueType{
		Number, String, Boolean
	}

	public enum StandardComparisons{
		equal, notequal, lessthan, greaterthan, greaterthanorequal, lessthanorequal
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4476247448049079794L;
	private AccessPointType accessPointType = null;
	private StandardScope standardScope = null;
	private String country = null;
	private String standardDescription = null;
	private String accessPointAttribute = null;
	private StandardValueType acessPointAttributeType = null;
	private ArrayList<String> positiveValues = null;
	private StandardComparisons standardComparison = null;
	private StandardType standardType = null;
	private Boolean partOfCompoundRule = false;
	private Date effectiveStartDate = null;
	private Date effectiveEndDate = null;

	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public Date getEffectiveEndDate() {
		return effectiveEndDate;
	}

	public void setEffectiveEndDate(Date effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}

	public Boolean getPartOfCompoundRule() {
		return partOfCompoundRule;
	}

	public void setPartOfCompoundRule(Boolean partOfCompoundRule) {
		this.partOfCompoundRule = partOfCompoundRule;
	}

	public StandardType getStandardType() {
		return standardType;
	}

	public void setStandardType(StandardType standardType) {
		this.standardType = standardType;
	}

	public StandardComparisons getStandardComparison() {
		return standardComparison;
	}

	public void setStandardComparison(StandardComparisons standardComparison) {
		this.standardComparison = standardComparison;
	}

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

	public StandardValueType getAcessPointAttributeType() {
		return acessPointAttributeType;
	}

	public void setAcessPointAttributeType(StandardValueType acessPointAttributeType) {
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

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");

		result.append(this.getClass().getName());
		result.append(" Object {");
		result.append(newLine);

		// determine fields declared in this class only (no fields of
		// superclass)
		Field[] fields = this.getClass().getDeclaredFields();

		// print field names paired with their values
		for (Field field : fields) {
			field.setAccessible(true);
			result.append("  ");
			try {
				result.append(field.getName());
				result.append(": ");
				// requires access to private field:
				result.append(field.get(this));
			} catch (IllegalAccessException ex) {
				System.out.println(ex);
			}
			result.append(newLine);
		}
		result.append("}");

		return result.toString();
	}

	@Override
	public String getCountryCode() {
		
		return this.getCountry();
	}

	@Override
	public void setCountryCode(String countryCode) {
		this.setCountry(countryCode);
	}
}
