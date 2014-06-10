/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

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
