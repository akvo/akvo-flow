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

package org.waterforpeople.mapping.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.domain.GeoRegion;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.servlet.PersistenceFilter;

@SuppressWarnings("unchecked")
public class GeoRegionDAO extends BaseDAO<GeoRegion> {

    public List<GeoRegion> list() {
        PersistenceManager pm = PersistenceFilter.getManager();
        List<GeoRegion> region = null;
        javax.jdo.Query query = pm.newQuery(GeoRegion.class);
        query.setOrdering("uuid, order asc");
        region = (List<GeoRegion>) query.execute();
        return region;
    }

    public GeoRegionDAO() {
        super(GeoRegion.class);
    }
}
