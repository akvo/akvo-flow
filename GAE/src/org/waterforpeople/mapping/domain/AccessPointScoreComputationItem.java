package org.waterforpeople.mapping.domain;

import com.gallatinsystems.framework.domain.BaseDomain;

public class AccessPointScoreComputationItem extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5969112417112456855L;
	public AccessPointScoreComputationItem(Integer score, String item) {
		// TODO Auto-generated constructor stub
	}

	private Integer scoreItem = null;
	private String scoreDetailMessage = null;
	public Integer getScoreItem() {
		return scoreItem;
	}
	public void setScoreItem(Integer scoreItem) {
		this.scoreItem = scoreItem;
	}
	public String getScoreDetailMessage() {
		return scoreDetailMessage;
	}
	public void setScoreDetailMessage(String scoreDetailMessage) {
		this.scoreDetailMessage = scoreDetailMessage;
	}
}
