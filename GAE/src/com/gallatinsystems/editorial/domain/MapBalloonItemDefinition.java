package com.gallatinsystems.editorial.domain;

import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
@PersistenceCapable
public class MapBalloonItemDefinition extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3467792586845879653L;
	@NotPersistent
	private List<MapBalloonRowDefinition> rows = null;
	private Integer displayOrder = null;
	private Long parentId = null;
	
	public List<MapBalloonRowDefinition> getRows() {
		return rows;
	}
	public void setRows(List<MapBalloonRowDefinition> row) {
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

}
