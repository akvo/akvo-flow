package com.gallatinsystems.standards.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.standards.domain.LevelOfServiceScore.LevelOfServiceScoreType;

@PersistenceCapable
public class LOSScoreToStatusMapping extends BaseDomain {

	/**
	 * 
	 */
	public enum LOSColor {Black,Red, Yellow, Green};
	
	private static final long serialVersionUID = -5962041217191538747L;
	private LevelOfServiceScoreType levelOfServiceScoreType = null;
	private Integer floor = null;
	private Integer ceiling = null;
	private LOSColor color = null;
	public LevelOfServiceScoreType getLevelOfServiceScoreType() {
		return levelOfServiceScoreType;
	}
	public void setLevelOfServiceScoreType(
			LevelOfServiceScoreType levelOfServiceScoreType) {
		this.levelOfServiceScoreType = levelOfServiceScoreType;
	}
	public Integer getFloor() {
		return floor;
	}
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public Integer getCeiling() {
		return ceiling;
	}
	public void setCeiling(Integer ceiling) {
		this.ceiling = ceiling;
	}
	public void setColor(LOSColor color) {
		this.color = color;
	}
	public LOSColor getColor() {
		return color;
	}
}
