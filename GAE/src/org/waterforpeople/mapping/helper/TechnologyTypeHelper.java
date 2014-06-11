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

package org.waterforpeople.mapping.helper;

import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.TechnologyType;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.dao.BaseDAO;

public class TechnologyTypeHelper {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(TechnologyTypeHelper.class
            .getName());

    public List<TechnologyType> listTechnologyTypes() {
        BaseDAO<TechnologyType> baseDAO = new BaseDAO<TechnologyType>(TechnologyType.class);
        return baseDAO.list(Constants.ALL_RESULTS);
    }

    public TechnologyType save(TechnologyType techType) {
        BaseDAO<TechnologyType> baseDAO = new BaseDAO<TechnologyType>(TechnologyType.class);
        return baseDAO.save(techType);
    }

    public void delete(TechnologyType techType) {
        BaseDAO<TechnologyType> baseDAO = new BaseDAO<TechnologyType>(TechnologyType.class);
        baseDAO.delete(techType);
    }

    public TechnologyType getTechnologyType(Long id) {
        BaseDAO<TechnologyType> baseDAO = new BaseDAO<TechnologyType>(TechnologyType.class);
        return baseDAO.getByKey(id);
    }

    public void deleteAll() {
        BaseDAO<TechnologyType> baseDAO = new BaseDAO<TechnologyType>(TechnologyType.class);
        for (TechnologyType techType : baseDAO.list(Constants.ALL_RESULTS)) {
            baseDAO.delete(techType);
        }
    }
}
