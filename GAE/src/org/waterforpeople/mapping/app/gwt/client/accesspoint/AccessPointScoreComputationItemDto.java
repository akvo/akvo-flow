package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.io.Serializable;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class AccessPointScoreComputationItemDto extends BaseDto implements Serializable {

	private static final long serialVersionUID = 6851278647835206723L;

	public AccessPointScoreComputationItemDto() {
	};

	public AccessPointScoreComputationItemDto(Integer score, String item) {
		scoreItem = score;
		scoreDetailMessage = item;
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
