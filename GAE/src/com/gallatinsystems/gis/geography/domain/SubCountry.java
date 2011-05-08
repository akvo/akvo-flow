package com.gallatinsystems.gis.geography.domain;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * structure to hold sub-country geographical regions. These should be
 * auto-created on ingest of shape files.
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SubCountry extends BaseDomain {

	private static final long serialVersionUID = 5331278163302041952L;
	private String name;
	private Integer level;
	private Long parentKey;
	private String parentName;
	private String countryCode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Long getParentKey() {
		return parentKey;
	}

	public void setParentKey(Long parentKey) {
		this.parentKey = parentKey;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(key.getId()).append(": ").append(countryCode).append(",")
				.append(level).append(",").append(name);
		return b.toString();
	}

}
