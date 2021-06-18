/*
 *  Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
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
package org.akvo.flow.util;

import java.util.Date;
import org.waterforpeople.mapping.domain.response.value.Location;

public class ExifTagInfo {
    private final Date collectionDate;
    private final Location location;

    public ExifTagInfo(Date collectionDate, Location location) {
        this.collectionDate = collectionDate;
        this.location = location;
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public Location getLocation() {
        return location;
    }
}
