package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class TechnologyTypeDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2922589897005551499L;
	private Long keyId = null;
	private String name = null;
	private String code = null;
	private String description = null;
	private Date effectiveStartDate = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public Date getEffectiveEndDate() {
		return effectiveEndDate;
	}

	public void setEffectiveEndDate(Date effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}

	public ArrayList<String> getPhotoUrlList() {
		return photoUrlList;
	}

	public void setPhotoUrlList(ArrayList<String> photoUrlList) {
		this.photoUrlList = photoUrlList;
	}

	public void addPhotoUrl(String photoUrl) {
		if (photoUrlList == null) {
			photoUrlList = new ArrayList<String>();
		}
		photoUrlList.add(photoUrl);
	}

	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}

	public Long getKeyId() {
		return keyId;
	}

	private Date effectiveEndDate = null;
	private ArrayList<String> photoUrlList = null;

}
