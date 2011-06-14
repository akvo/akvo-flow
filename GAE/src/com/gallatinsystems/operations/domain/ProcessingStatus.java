package com.gallatinsystems.operations.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Domain that can be used to track processing status for use in computing
 * last-run times and progress of backend jobs.
 * 
 * @author Christopher Fagiani
 * 
 */
@PersistenceCapable
public class ProcessingStatus extends BaseDomain {
	private static final long serialVersionUID = -5624416550678028364L;

	private String code;
	private String value;
	private String guid;
	private Boolean inError;

	public Boolean getInError() {
		return inError;
	}

	public void setInError(Boolean inError) {
		this.inError = inError;
	}

	public Date getLastEventDate() {
		return lastEventDate;
	}

	public void setLastEventDate(Date lastEventDate) {
		this.lastEventDate = lastEventDate;
	}

	private Date lastEventDate;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
