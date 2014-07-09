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

package com.gallatinsystems.operations.dao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.operations.domain.ProcessingStatus;

/**
 * data access object for the processing status domain
 * 
 * @author Christopher Fagiani
 */
public class ProcessingStatusDao extends BaseDAO<ProcessingStatus> {

    public ProcessingStatusDao() {
        super(ProcessingStatus.class);
    }

    /**
     * returns a single ProcessingStatus object using its code
     * 
     * @param code
     * @return
     */
    public ProcessingStatus getStatusByCode(String code) {
        return findByProperty("code", code, "String");
    }

}
