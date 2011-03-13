package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

/**
 * represents access point metric objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class AccessPointMetricSummaryDto extends BaseDto {

	private static final long serialVersionUID = 5281957725194653032L;

	private String organization;
	private String country;
	private String district;
	private Integer subLevel;
	private String subLevelName;
	private String subValue;
	private String metricGroup;
	private String metricName;
	private String metricValue;
	private Long count;
	private Long year;
	private Double latitude = null;
	private Double longitude = null;
	private String iconUrl = null;
	private String accessPointType = null;
	private String placemarkContents=null;
	
	public String getPlacemarkContents() {
		return placemarkContents;
	}

	public void setPlacemarkContents(String placemarkContents) {
		this.placemarkContents = placemarkContents;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Integer getSubLevel() {
		return subLevel;
	}

	public void setSubLevel(Integer subLevel) {
		this.subLevel = subLevel;
	}

	public String getSubLevelName() {
		return subLevelName;
	}

	public void setSubLevelName(String subLevelName) {
		this.subLevelName = subLevelName;
	}

	public String getSubValue() {
		return subValue;
	}

	public void setSubValue(String subValue) {
		this.subValue = subValue;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getMetricGroup() {
		return metricGroup;
	}

	public void setMetricGroup(String metricGroup) {
		this.metricGroup = metricGroup;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public String getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(String metricValue) {
		this.metricValue = metricValue;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		this.year = year;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setAccessPointType(String accessPointType) {
		this.accessPointType = accessPointType;
	}

	public String getAccessPointType() {
		return accessPointType;
	}
}
