package com.gallatinsystems.editorial.dao;

import java.util.List;

import com.gallatinsystems.editorial.domain.MapBalloonItemDefinition;
import com.gallatinsystems.editorial.domain.MapBalloonRowDefinition;
import com.gallatinsystems.framework.dao.BaseDAO;

/**
 * 
 * dao for mapBaloonItemDefinition objects
 * 
 * @deprecated
 */
public class MapBalloonItemDefinitionDao extends
		BaseDAO<MapBalloonItemDefinition> {
	public MapBalloonItemDefinitionDao() {
		super(MapBalloonItemDefinition.class);
	}

	/**
	 * loads the item along with any rows that descend from it
	 */
	public MapBalloonItemDefinition getByKey(Long id) {
		MapBalloonItemDefinition item = super.getByKey(id);
		List<MapBalloonRowDefinition> rowDefList = new MapBalloonRowDefinitionDao()
				.listByParentId(id);
		if (rowDefList != null)
			item.setRows(rowDefList);
		return item;
	}

	/**
	 * saves the mapBaloonItemDefintion and then iterates over any child rows and saves those.
	 * @param item
	 * @return
	 */
	public MapBalloonItemDefinition save(MapBalloonItemDefinition item) {
		super.save(item);
		if (item.getRows() != null) {
			MapBalloonRowDefinitionDao mapRowDefDao = new MapBalloonRowDefinitionDao();
			for (MapBalloonRowDefinition rowItem : item.getRows()) {
				if (rowItem != null) {
					rowItem.setParentId(item.getKey().getId());
					mapRowDefDao.save(rowItem);
				}
			}
		}
		return item;
	}

	public List<MapBalloonItemDefinition> listByParentId(Long id) {
		List<MapBalloonItemDefinition> mapIdList = super.listByProperty(
				"parentId", id, "Long");
		MapBalloonRowDefinitionDao mapRowDefDao = new MapBalloonRowDefinitionDao();
		for (MapBalloonItemDefinition item : mapIdList) {
			List<MapBalloonRowDefinition> mapRowDefList = mapRowDefDao
					.listByParentId(item.getKey().getId());
			item.setRows(mapRowDefList);
		}
		return mapIdList;
	}

}
