package org.waterforpeople.mapping.app.gwt.client.community;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * dto for sub-countries (regions within a country at various "levels")
 * 
 * @author Christopher Fagiani
 * 
 */
public class SubCountryDto extends BaseDto {

	private static final long serialVersionUID = 528193417185492492L;
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

}
