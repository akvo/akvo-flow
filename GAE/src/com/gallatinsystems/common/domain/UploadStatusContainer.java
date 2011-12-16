package com.gallatinsystems.common.domain;

/**
 * Simple data structure class to encapsulate responses from file upload.
 * 
 *
 */
public class UploadStatusContainer {
	private Boolean uploadedFile = null;
	private Boolean uploadedZip = null;
	private String message = null;

	public Boolean getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(Boolean uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public Boolean getUploadedZip() {
		return uploadedZip;
	}

	public void setUploadedZip(Boolean uploadedZip) {
		this.uploadedZip = uploadedZip;
	}

	private String url = null;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
