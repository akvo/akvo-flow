package com.gallatinsystems.editorial.dao;

import java.util.List;

import com.gallatinsystems.editorial.domain.MapBalloonDefinition;
import com.gallatinsystems.editorial.domain.MapBalloonItemDefinition;
import com.gallatinsystems.framework.dao.BaseDAO;

/**
 * dao for map ballon definitions
 * 
 * @deprecated
 */
public class MapBalloonDefinitionDao extends BaseDAO<MapBalloonDefinition> {

	public MapBalloonDefinitionDao() {
		super(MapBalloonDefinition.class);
	}

	/**
	 * gets a definition by Id along with all the items that descend from it.
	 * @param id
	 * @return
	 */
	public MapBalloonDefinition getById(Long id) {
		MapBalloonDefinition item = super.getByKey(id);
		List<MapBalloonItemDefinition> itemList = new MapBalloonItemDefinitionDao()
				.listByParentId(id);
		if (itemList != null && itemList.size() > 0)
			item.setMapBalloonItemList(itemList);
		return item;
	}

	/**
	 * saves the definition and then iterates over the baloonItemList and saves
	 * each item contained therein.
	 * 
	 * @param item
	 * @return
	 */
	public MapBalloonDefinition save(MapBalloonDefinition item) {
		super.save(item);
		MapBalloonItemDefinitionDao mapItemDao = new MapBalloonItemDefinitionDao();
		if (item.getMapBalloonItemList() != null)
			for (MapBalloonItemDefinition mapItemDef : item
					.getMapBalloonItemList()) {
				mapItemDef.setParentId(item.getKey().getId());
				mapItemDao.save(mapItemDef);
			}
		return item;
	}
}
