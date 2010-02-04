package org.waterforpeople.mapping.domain;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class CaptionDefinition {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	private String captionVariableName = null;
	private String captionValue = null;
	private Date captionEffectiveStartDate = null;
	private Date captionEffectiveEndDate = null;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCaptionVariableName() {
		return captionVariableName;
	}
	public void setCaptionVariableName(String captionVariableName) {
		this.captionVariableName = captionVariableName;
	}
	public String getCaptionValue() {
		return captionValue;
	}
	public void setCaptionValue(String captionValue) {
		this.captionValue = captionValue;
	}
	public Date getCaptionEffectiveStartDate() {
		return captionEffectiveStartDate;
	}
	public void setCaptionEffectiveStartDate(Date captionEffectiveStartDate) {
		this.captionEffectiveStartDate = captionEffectiveStartDate;
	}
	public Date getCaptionEffectiveEndDate() {
		return captionEffectiveEndDate;
	}
	public void setCaptionEffectiveEndDate(Date captionEffectiveEndDate) {
		this.captionEffectiveEndDate = captionEffectiveEndDate;
	}
	
	
}
