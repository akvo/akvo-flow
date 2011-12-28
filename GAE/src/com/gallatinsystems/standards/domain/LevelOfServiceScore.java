package com.gallatinsystems.standards.domain;

import java.util.ArrayList;

import com.gallatinsystems.framework.domain.BaseDomain;

public class LevelOfServiceScore extends BaseDomain {
	public enum LevelOfServiceScoreType {
		WaterPointLevelOfService, WaterPointSustainability, PublicInstitutionLevelOfService, PublicInstitutionSustainability
	};

	/**
	 * 
	 */
	private static final long serialVersionUID = 2125766896569232295L;
	private LevelOfServiceScoreType scoreType = null;
	private ArrayList<String> scoreDetails = null;
	private Integer score = 0;

	public LevelOfServiceScoreType getScoreType() {
		return scoreType;
	}

	public void setScoreType(LevelOfServiceScoreType scoreType) {
		this.scoreType = scoreType;
	}

	public ArrayList<String> getScoreDetails() {
		return scoreDetails;
	}

	public void setScoreDetails(ArrayList<String> scoreDetails) {
		this.scoreDetails = scoreDetails;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}
}
