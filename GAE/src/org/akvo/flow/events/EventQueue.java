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

package org.akvo.flow.events;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

/**
 * used to implement a queue for events
 */
@PersistenceCapable
public class EventQueue extends BaseDomain {

    private static final long serialVersionUID = 89484684617286776L;
    private Text payload;
    private Boolean synced = false;

    public EventQueue(Date timestamp, String eventString) {
        this.payload = new Text(eventString);
        this.setLastUpdateDateTime(timestamp);
        this.setCreatedDateTime(timestamp);
    }

    }


    public Text getPayload() {
        return payload;
    }

    public void setPayload(Text payload) {
        this.payload = payload;
    }

    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }

}
