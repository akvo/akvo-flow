/*
 *  Copyright (C) 2014,2019 Stichting Akvo (Akvo Foundation)
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

import org.waterforpeople.mapping.app.web.DataProcessorRestServlet;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.CascadeResource;

/**
 * Dao for manipulating CascadeResources
 */
public class CascadeResourceDao extends BaseDAO<CascadeResource> {

    private static final Logger log = Logger.getLogger(CascadeResourceDao.class.getName());

    public CascadeResourceDao() {
		super(CascadeResource.class);
	}


    /**
     * deletes a cascade resource, and the nodes therein asynchronously in a task
     * 
     * @param resource
     */
    public void delete(CascadeResource resource) {
        if (resource == null) {
            return;
        }

        super.delete(resource);

        DataProcessorRestServlet.scheduleCascadeResourceDeletion(resource.getKey().getId());
    }
}