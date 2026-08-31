/*
 *  Copyright (C) 2026 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.waterforpeople.mapping.domain.response.value.Location;
import org.waterforpeople.mapping.domain.response.value.Media;
import org.waterforpeople.mapping.serialization.response.MediaResponse;

/**
 * Inputs come from the real serialisers: the two "no location" cases differ only in null
 * handling, which a hand-written JSON literal would paper over.
 */
class CronCommanderServletTest {

    private static Media media(String filename) {
        Media media = new Media();
        media.setFilename(filename);
        return media;
    }

    @Test
    void answerWhoseImageHasNoGpsTagIsNotReadAgain() {
        // What the scan stores once it has read the file and found no GPS tag.
        Media media = media("photo.jpg");
        String stored = MediaResponse.formatWithGeotag(media);

        assertTrue(CronCommanderServlet.isKnownToHaveNoGeotag(stored), stored);
    }

    @Test
    void answerWhoseImageHasNotArrivedYetIsRetried() {
        // What the scan stores when the file is not in S3. It may still turn up.
        String stored = MediaResponse.formatWithoutGeotag(media("missing.jpg"));

        assertFalse(CronCommanderServlet.isKnownToHaveNoGeotag(stored), stored);
    }

    @Test
    void answerWithARealLocationIsNotMistakenForAnUntaggedOne() {
        Location location = new Location();
        location.setLatitude(59.29);
        location.setLongitude(17.95);
        Media media = media("tagged.jpg");
        media.setLocation(location);
        String stored = MediaResponse.formatWithGeotag(media);

        assertFalse(CronCommanderServlet.isKnownToHaveNoGeotag(stored), stored);
    }
}
