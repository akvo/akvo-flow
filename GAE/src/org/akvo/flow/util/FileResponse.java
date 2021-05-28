package org.akvo.flow.util;

import org.waterforpeople.mapping.domain.response.value.Location;

public class FileResponse {
    private final String filename;
    private final Location location;

    public FileResponse(String filename, Location location) {
        this.filename = filename;
        this.location = location;
    }

    public String getFilename() {
        return filename;
    }

    public Location getLocation() {
        return location;
    }
}
