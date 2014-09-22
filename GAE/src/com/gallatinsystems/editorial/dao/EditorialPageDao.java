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

package com.gallatinsystems.editorial.dao;

import java.util.List;

import com.gallatinsystems.editorial.domain.EditorialPage;
import com.gallatinsystems.editorial.domain.EditorialPageContent;
import com.gallatinsystems.framework.dao.BaseDAO;

/**
 * data access object for persisting EditorialPage objects.
 * 
 * @author Christopher Fagiani
 */
public class EditorialPageDao extends BaseDAO<EditorialPage> {

    public EditorialPageDao() {
        super(EditorialPage.class);
    }

    /**
     * returns a sorted list of all EditorialPageContent objects by pageId.
     * 
     * @param pageId
     * @return
     */
    public List<EditorialPageContent> listContentByPage(Long pageId) {
        return listByProperty("editorialPageId", pageId, "Long", "sortOrder",
                EditorialPageContent.class);
    }

    /**
     * finds the EditorialPage that matched the targetFileName passed in.
     * 
     * @param name
     * @return
     */
    public EditorialPage findByTargetPage(String name) {
        return findByProperty("targetFileName", name, "String");
    }

}
