package com.gallatinsystems.editorial.dao;

import java.util.List;

import com.gallatinsystems.editorial.domain.MapBalloonRowDefinition;
import com.gallatinsystems.framework.dao.BaseDAO;

/**
 * dao for balloon row definitions
 * 
 */
public class MapBalloonRowDefinitionDao extends
		BaseDAO<MapBalloonRowDefinition> {
	public MapBalloonRowDefinitionDao() {
		super(MapBalloonRowDefinition.class);
	}
	
	/**
	 * lists all rows by for a given parent id
	 * @param parentId
	 * @return
	 */
	public List<MapBalloonRowDefinition> listByParentId(Long parentId){
		return super.listByProperty("parentId", parentId, "Long");
	}

}
