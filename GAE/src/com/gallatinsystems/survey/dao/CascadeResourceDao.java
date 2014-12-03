/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.survey.dao;

import java.util.logging.Logger;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.CascadeResource;

/**
 * Dao for manipulating CascadeResources
 */
public class CascadeResourceDao extends BaseDAO<CascadeResource> {
    public CascadeResourceDao() {
		super(CascadeResource.class);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(SurveyGroupDAO.class
            .getName());

    /**
     * deletes a cascade resource, and the nodes therein asynchronously in a task
     * 
     * @param item
     */
    public void delete(CascadeResource item) {    	
        CascadeNodeDao cascadeNodeDao = new CascadeNodeDao();
        item = super.getByKey(item.getKey().getId());
        // TODO - delete cascade nodes in task
        cascadeNodeDao.delete(cascadeNodeDao
                    .listCascadeNodesByResource(item.getKey().getId()));
        super.delete(item);
    }
}