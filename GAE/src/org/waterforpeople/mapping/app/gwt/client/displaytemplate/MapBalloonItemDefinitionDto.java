package org.waterforpeople.mapping.app.gwt.client.displaytemplate;

import java.io.Serializable;
import java.util.List;
public class MapBalloonItemDefinitionDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7213375365770068948L;
	/**
	 * 
	 */
	private Long keyId = null;
	private List<MapBalloonRowDefinitionDto> rows = null;
	private Integer displayOrder = null;
	private Long parentId = null;
	
	public List<MapBalloonRowDefinitionDto> getRows() {
		return rows;
	}
	public void setRows(List<MapBalloonRowDefinitionDto> row) {
		this.rows = row;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setKeyId(Long keyId) {
		this.keyId = keyId;
	}
	public Long getKeyId() {
		return keyId;
	}

}
