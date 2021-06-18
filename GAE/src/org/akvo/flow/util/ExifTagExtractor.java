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

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.GpsDirectory;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.waterforpeople.mapping.domain.response.value.Location;

public class ExifTagExtractor {

    private static final Logger log = Logger.getLogger(ExifTagExtractor.class.getName());

    private final DateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");

    public ExifTagInfo fetchExifTags(MultipartFile file) {
        try {
            InputStream s = file.getInputStream();
            Metadata metadata = ImageMetadataReader.readMetadata(s);
            Directory directoryBase = metadata.getFirstDirectoryOfType(ExifDirectoryBase.class);
            Date parsedDate = null;
            if (directoryBase != null) {
                String timeStamp = directoryBase.getString(ExifDirectoryBase.TAG_DATETIME);
                try {
                    parsedDate = dateFormat.parse(timeStamp);
                } catch (ParseException e) {
                    log.log(Level.WARNING, "Error parsing date", e);
                }
                log.finest("Timestamp: " + timeStamp);
            }
            Directory directory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (directory == null) {
                log.warning("No gps directory data found");
                return new ExifTagInfo(parsedDate, null);
            }

            Double lat = getLatitude(directory);
            Double lon = getLongitude(directory);
            Double alt = getAltitude(directory);
            Float acc = getAccuracy(directory);
            Location location = null;
            if (lat != null && lon != null) {
                location = new Location();
                location.setLatitude(lat);
                location.setLongitude(lon);
                location.setAccuracy(acc);
                location.setAltitude(alt);
            }
            return new ExifTagInfo(parsedDate, location);
        } catch (IOException | ImageProcessingException e) {
            log.log(Level.SEVERE, "Error extracting exif information from jpeg file", e);
            return null;
        }
    }

    private Double getLongitude(Directory directory) {
        Rational[] lonTag = directory.getRationalArray(GpsDirectory.TAG_LONGITUDE);
        if (lonTag != null) {
            String lonRefTag = directory.getString(GpsDirectory.TAG_LONGITUDE_REF);
            double longitude = lonTag[0].doubleValue() + lonTag[1].doubleValue() / 60.0 + lonTag[2].doubleValue() / 3600.0;
            if (lonRefTag != null && lonRefTag.contentEquals("W")) {
                longitude = -longitude;
            }
            return longitude;
        }
        return null;
    }

    private Double getLatitude(Directory directory) {
        Rational[] latTag = directory.getRationalArray(GpsDirectory.TAG_LATITUDE);
        if (latTag != null) {
            String latRefTag = directory.getString(GpsDirectory.TAG_LATITUDE_REF);
            double latitude = latTag[0].doubleValue() + latTag[1].doubleValue() / 60.0 + latTag[2].doubleValue() / 3600.0;
            if (latRefTag != null && latRefTag.contentEquals("S")) {
                latitude = -latitude;
            }
            return latitude;
        }
        return null;
    }

    private Float getAccuracy(Directory directory) {
        Rational[] accTag = directory.getRationalArray(GpsDirectory.TAG_H_POSITIONING_ERROR);
        float accuracy;
        if (accTag != null) {
            accuracy = accTag[0].floatValue();
        } else {
            accuracy = 0.0f; //Optional; default to 0
        }
        return accuracy;
    }

    private Double getAltitude(Directory directory) {
        Rational[] altTag = directory.getRationalArray(GpsDirectory.TAG_ALTITUDE);
        Integer altRefTag = directory.getInteger(GpsDirectory.TAG_ALTITUDE_REF);
        double altitude;
        if (altTag != null) {
            altitude = altTag[0].doubleValue();
        } else {
            altitude = 0.0; //Optional; default to 0
        }
        if (altRefTag != null && altRefTag.equals(1)) { //0 = above, 1 below sea level
            altitude = -altitude;
        }
        return altitude;
    }
}
