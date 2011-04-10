package org.waterforpeople.mapping.app.gwt.client.devicefiles;

import java.util.Date;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class DeviceFilesDto extends BaseDto {

	private static final long serialVersionUID = 6922312748824010890L;
	private String URI = null;
	private Date uploadDateTime = null;
	private String processedStatus = null;
	private String processDate = null;
	private String processingMessage = null;
	private String phoneNumber;
	private String checksum;
	
	public String getURI() {
		return URI;
	}
	public void setURI(String uRI) {
		URI = uRI;
	}
	public Date getUploadDateTime() {
		return uploadDateTime;
	}
	public void setUploadDateTime(Date uploadDateTime) {
		this.uploadDateTime = uploadDateTime;
	}
	public String getProcessedStatus() {
		return processedStatus;
	}
	public void setProcessedStatus(String processedStatus) {
		this.processedStatus = processedStatus;
	}
	public String getProcessDate() {
		return processDate;
	}
	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}
	public String getProcessingMessage() {
		return processingMessage;
	}
	public void setProcessingMessage(String processingMessage) {
		this.processingMessage = processingMessage;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getChecksum() {
		return checksum;
	}
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

}
