package com.gallatinsystems.standards.domain;

import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Key;
@PersistenceCapable
public class LevelOfServiceScore extends BaseDomain {
	public enum LevelOfServiceScoreType {
		WaterPointLevelOfService, WaterPointSustainability, PublicInstitutionLevelOfService, PublicInstitutionSustainability
	};
	
	public enum ScoreObject{AccessPoint, SurveyedLocale};

	/**
	 * 
	 */
	private static final long serialVersionUID = 2125766896569232295L;
	private LevelOfServiceScoreType scoreType = null;
	private ArrayList<String> scoreDetails = null;
	private Integer score = 0;
	private Key objectKey = null;
	private ScoreObject scoreObject = null;

	public Key getObjectKey() {
		return objectKey;
	}

	public void setObjectKey(Key objectKey) {
		this.objectKey = objectKey;
	}

	public ScoreObject getScoreObject() {
		return scoreObject;
	}

	public void setScoreObject(ScoreObject scoreObject) {
		this.scoreObject = scoreObject;
	}

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
