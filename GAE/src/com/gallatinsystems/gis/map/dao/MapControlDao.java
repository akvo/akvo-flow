package com.gallatinsystems.gis.map.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;
import com.gallatinsystems.gis.map.domain.MapControl;

public class MapControlDao extends BaseDAO<MapControl> {

	public MapControlDao() {
		super(MapControl.class);
	}

	@SuppressWarnings("unchecked")
	public MapControl getLatestRunTime() {
		PersistenceManager pm = PersistenceFilter.getManager();
		javax.jdo.Query query = pm.newQuery(MapControl.class);

		query.setOrdering("createdDateTime desc");

		prepareCursor(null, query);
		List<MapControl> results = (List<MapControl>) query.execute();
		if (results != null)
			if (results.size() > 0)
				if (results.get(0) != null)
					return results.get(0);
		return null;
	}

}
