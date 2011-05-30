package org.waterforpeople.mapping.domain;

import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class AccessPointScoreDetail extends BaseDomain {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8688043975799269589L;
	private Long accessPointId = null;
	private Integer score = null;
	private ArrayList<AccessPointScoreComputationItem> scoreComputationItems = null;
	private String status = null;
	private Date computationDate = null;
	private String scoreBucket = null;
	private Long scoreBucketId = null;

	public Long getAccessPointId() {
		return accessPointId;
	}

	public void setAccessPointId(Long accessPointId) {
		this.accessPointId = accessPointId;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public ArrayList<AccessPointScoreComputationItem> getScoreComputationItems() {
		return scoreComputationItems;
	}

	public void setScoreComputationItems(
			ArrayList<AccessPointScoreComputationItem> scoreComputationItems) {
		this.scoreComputationItems = scoreComputationItems;
	}

	public void addScoreComputationItem(Integer score, String item) {
		if (scoreComputationItems == null) {
			scoreComputationItems = new ArrayList<AccessPointScoreComputationItem>();
		}
		AccessPointScoreComputationItem apsi =new AccessPointScoreComputationItem(score,item);
		scoreComputationItems.add(apsi);

	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setComputationDate(Date computationDate) {
		this.computationDate = computationDate;
	}

	public Date getComputationDate() {
		return computationDate;
	}

	public void setScoreBucket(String scoreBucket) {
		this.scoreBucket = scoreBucket;
	}

	public String getScoreBucket() {
		return scoreBucket;
	}

	public void setScoreBucketId(Long scoreBucketId) {
		this.scoreBucketId = scoreBucketId;
	}

	public Long getScoreBucketId() {
		return scoreBucketId;
	}

}
