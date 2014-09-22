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

import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.KML;

import com.gallatinsystems.framework.dao.BaseDAO;

public class KMLDAO extends BaseDAO<KML> {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(KMLDAO.class.getName());

    public KMLDAO() {
        super(KML.class);
    }

    public String saveKML(String kmlText) {
        KML kml = new KML();
        // kml.setKmlText(new Text(kmlText));
        return save(kml).getKey().getId() + "";
    }

    public String getKML(Long id) {
        KML kml = getByKey(id);
        if (kml != null) {
            return kml.getKmlText().toString();
        } else {
            return "";
        }
    }
}
