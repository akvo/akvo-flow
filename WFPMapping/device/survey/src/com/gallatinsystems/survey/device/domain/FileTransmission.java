package com.gallatinsystems.survey.device.domain;

import java.util.Date;

/**
 * data structure representing the files transfered to the server
 * 
 * @author Christopher Fagiani
 * 
 */
public class FileTransmission {

	private Long id;
	private Long respondentId;
	private String fileName;
	private Date startDate;
	private Date endDate;
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRespondentId() {
		return respondentId;
	}

	public void setRespondentId(Long respondentId) {
		this.respondentId = respondentId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

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

}
