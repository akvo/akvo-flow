package org.waterforpeople.mapping.domain;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.waterforpeople.mapping.domain.Status.StatusCode;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DeviceFiles {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id = null;
	@Persistent
	private String URI = null;
	@Persistent
	private Date uploadDateTime = null;
	@Persistent
	private Status.StatusCode processedStatus = null;
	@Persistent
	private Status status = null;
	@Persistent
	private String processDate = null;
	
	public String getURI() {
		return URI;
	}

	public String getProcessDate() {
		return processDate;
	}

	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}

	public void setURI(String uri) {
		URI = uri;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getUploadDateTime() {
		return uploadDateTime;
	}

	public void setUploadDateTime(Date uploadDateTime) {
		this.uploadDateTime = uploadDateTime;
	}

	public Status.StatusCode getProcessedStatus() {
		return processedStatus;
	}

	public void setProcessedStatus(StatusCode statusCode) {
		this.processedStatus = statusCode;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("DeviceFiles: ");
		sb.append("\n   Id: " + id);
		sb.append("\n   URI: " + URI);
		sb.append("\n   ProcessDate: " + this.processDate);
		sb.append("\n   Status: " + status);
		sb.append("\n   ProcessedStatus: "+ this.processedStatus);
		return sb.toString();
	}
}
