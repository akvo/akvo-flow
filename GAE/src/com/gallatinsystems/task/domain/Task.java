package com.gallatinsystems.task.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * domain object to hold a single task tied to a location. This can be used to
 * create a list of tasks that administrators would like users to complete based
 * on their geographic location.
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class Task extends BaseDomain {

	private static final long serialVersionUID = -8361796525058814795L;
	private TaskStatus status;
	private String headline;
	private String description;
	private Double lat;
	private Double lon;
	private Double maxDistance;
	private String countryCode;

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(Double maxDistance) {
		this.maxDistance = maxDistance;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public enum TaskStatus {
		INCOMPLETE, IN_PROGRESS, COMPLETE
	}

}
