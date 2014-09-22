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

package com.gallatinsystems.editorial.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

/**
 * persistent class for representing a content snippet for an editorial page. A page can have 0 or
 * more of these EditorialPageContent items. These can be bound into the editorial page by using the
 * type name from this class as a context variable within the template stored in the EditorialPage.
 * 
 * @author Christopher Fagiani
 */
@PersistenceCapable
public class EditorialPageContent extends BaseDomain {

    private static final long serialVersionUID = -4573912898036246617L;
    private Long editorialPageId;
    private String type;
    private String heading;
    private Text text;
    private Long sortOrder;

    public Long getEditorialPageId() {
        return editorialPageId;
    }

    public void setEditorialPageId(Long editorialPageId) {
        this.editorialPageId = editorialPageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }
}
