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
