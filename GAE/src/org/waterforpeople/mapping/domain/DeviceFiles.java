package org.waterforpeople.mapping.domain;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.waterforpeople.mapping.domain.Status.StatusCode;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class DeviceFiles {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key = null;
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


	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
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
		sb.append("\n   Key: " + key.toString());
		sb.append("\n   URI: " + URI);
		sb.append("\n   ProcessDate: " + this.processDate);
		sb.append("\n   Status: " + status);
		sb.append("\n   ProcessedStatus: "+ this.processedStatus);
		return sb.toString();
	}
}
