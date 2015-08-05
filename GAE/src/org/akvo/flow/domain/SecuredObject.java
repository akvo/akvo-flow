/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.domain;

import java.util.List;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Interface to define methods that should be overridden by objects to which access is restricted
 * via user permissions
 */
public interface SecuredObject {
    public SecuredObject getParentObject();

    /**
     * Update the ancestor ids for all child entities. If cascade is true, then cascade ancestor ids
     * updates to include all the subsequent child entities. Return a list of all the updated child
     * entities
     *
     * @param cascade
     * @return
     */
    public List<BaseDomain> updateAncestorIds(boolean cascade);

    public Long getObjectId();

    public List<Long> listAncestorIds();
}
