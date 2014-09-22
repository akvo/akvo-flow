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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.DisplayTemplateMapping;

import com.gallatinsystems.framework.dao.BaseDAO;

public class DisplayTemplateMappingDAO extends BaseDAO<DisplayTemplateMapping> {
    @SuppressWarnings("unused")
    private static Logger logger = Logger

            .getLogger(DisplayTemplateMappingDAO.class.getName());

    public DisplayTemplateMappingDAO() {
        super(DisplayTemplateMapping.class);
    }

    public List<DisplayTemplateMapping> save(
            List<DisplayTemplateMapping> listDTM) {
        List<DisplayTemplateMapping> returnList = new ArrayList<DisplayTemplateMapping>();
        for (DisplayTemplateMapping item : listDTM) {
            returnList.add((DisplayTemplateMapping) super.save(item));
        }
        return returnList;
    }

    public List<DisplayTemplateMapping> listByAccessPointType(
            String propertyValue, String cursorString) {
        return listByProperty("AccessPointType", propertyValue, "String");
    }

    public DisplayTemplateMapping get(Long id) {
        return (DisplayTemplateMapping) getByKey(id);
    }

}
