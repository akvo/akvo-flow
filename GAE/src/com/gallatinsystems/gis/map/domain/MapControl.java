package com.gallatinsystems.gis.map.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class MapControl extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4966511822743910055L;

	private Date startDate = null;
	private Date endDate = null;
	private Status status = null;
	public enum Status{SUCCESS,FAILURE,INPROCESS,STARTING}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
}
