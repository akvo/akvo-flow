package org.waterforpeople.mapping.app.gwt.client.displaytemplate;

import java.io.Serializable;
import java.util.List;

public class MapBalloonDefinitionDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4377814137657027551L;
	/**
	 * 
	 */
	private Long keyId = null;
	private Long parentId = null;
	private String name = null;
	
	private BalloonType balloonType = null;
	private String styleData = null;
	private String headerText = null;
	private List<MapBalloonItemDefinitionDto> mapBalloonItemList = null;
	private String footerText = null;
	public enum BalloonType{  KML_FOLDER, KML_WATER_POINT, KML_COMMUNITY_SUMMARY}
	
	public String getStyleData() {
		return styleData;
	}
	public void setStyleData(String styleData) {
		this.styleData = styleData;
	}
	public String getHeaderText() {
		return headerText;
	}
	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BalloonType getBalloonType() {
		return balloonType;
	}
	public void setBalloonType(BalloonType balloonType) {
		this.balloonType = balloonType;
	}
	
	public String getFooterText() {
		return footerText;
	}
	public void setFooterText(String footerText) {
		this.footerText = footerText;
	}
	public void setMapBalloonItemList(List<MapBalloonItemDefinitionDto> mapBalloonItemList) {
		this.mapBalloonItemList = mapBalloonItemList;
	}
	public List<MapBalloonItemDefinitionDto> getMapBalloonItemList() {
		return mapBalloonItemList;
	}
	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}
	public Long getKeyId() {
		return keyId;
	}
}
