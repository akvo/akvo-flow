package com.gallatinsystems.editorial.dao;

import java.util.List;

import com.gallatinsystems.editorial.domain.MapBalloonRowDefinition;
import com.gallatinsystems.framework.dao.BaseDAO;

public class MapBalloonRowDefinitionDao extends
		BaseDAO<MapBalloonRowDefinition> {
	public MapBalloonRowDefinitionDao() {
		super(MapBalloonRowDefinition.class);
	}
	
	public List<MapBalloonRowDefinition> listByParentId(Long parentId){
		return super.listByProperty("parentId", parentId, "Long");
	}

}
