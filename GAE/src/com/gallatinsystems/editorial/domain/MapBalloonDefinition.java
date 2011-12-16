package com.gallatinsystems.editorial.domain;

import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

/**
 * 
 * Represents a configuration for Map balloons (pop-ups on google maps/google earth maps).
 *
 *@deprecated
 */
@PersistenceCapable
public class MapBalloonDefinition extends BaseDomain {

	
	private static final long serialVersionUID = 7574762368535643204L;
	
	private Long parentId = null;
	private String name = null;
	
	private BalloonType balloonType = null;
	private Text styleData = null;
	private Text headerText = null;
	@NotPersistent
	private List<MapBalloonItemDefinition> mapBalloonItemList = null;
	private Text footerText = null;
	public enum BalloonType{  KML_FOLDER, KML_WATER_POINT, KML_COMMUNITY_SUMMARY}
	
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
	public Text getHeaderText() {
		return headerText;
	}
	public void setHeaderText(Text headerText) {
		this.headerText = headerText;
	}
	
	public Text getFooterText() {
		return footerText;
	}
	public void setFooterText(Text footerText) {
		this.footerText = footerText;
	}
	public void setStyleData(String styleData) {
		this.styleData = new Text(styleData);
	}
	public String getStyleData() {
		return styleData.getValue();
	}
	public void setMapBalloonItemList(List<MapBalloonItemDefinition> mapBalloonItemList) {
		this.mapBalloonItemList = mapBalloonItemList;
	}
	public List<MapBalloonItemDefinition> getMapBalloonItemList() {
		return mapBalloonItemList;
	}
}
