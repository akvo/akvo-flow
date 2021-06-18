/*
 * Copyright 2010 Alexandre Gellibert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.beoui.geocell;

import com.beoui.geocell.model.Point;

/**
 * Utils class to compute geocells.
 *
 * @author api.roman.public@gmail.com (Roman Nurik)
 * @author (java portage) Alexandre Gellibert
 *
 */
public final class GeocellUtils {

    public static final float MIN_LONGITUDE = -180.0f;
    public static final float MAX_LONGITUDE = 180.0f;
    public static final float MIN_LATITUDE = -90.0f;
    public static final float MAX_LATITUDE = 90.0f;
	// Geocell algorithm constants.
    public static final int GEOCELL_GRID_SIZE = 4;
    private static final String GEOCELL_ALPHABET = "0123456789abcdef";

    private GeocellUtils() {
        // no instantiation allowed
    }

    /**
     * Computes the geocell containing the given point to the given resolution.

      This is a simple 16-tree lookup to an arbitrary depth (resolution).
     *
     * @param point: The geotypes.Point to compute the cell for.
     * @param resolution: An int indicating the resolution of the cell to compute.
     * @return The geocell string containing the given point, of length resolution.
     */
    public static String compute(Point point, int resolution) {
        float north = MAX_LATITUDE;
        float south = MIN_LATITUDE;
        float east = MAX_LONGITUDE;
        float west = MIN_LONGITUDE;

        StringBuilder cell = new StringBuilder();
        while(cell.length() < resolution) {
            float subcellLonSpan = (east - west) / GEOCELL_GRID_SIZE;
            float subcellLatSpan = (north - south) / GEOCELL_GRID_SIZE;

            int x = Math.min((int)(GEOCELL_GRID_SIZE * (point.getLon() - west) / (east - west)),
                    GEOCELL_GRID_SIZE - 1);
            int y = Math.min((int)(GEOCELL_GRID_SIZE * (point.getLat() - south) / (north - south)),
                    GEOCELL_GRID_SIZE - 1);

            int l[] = {x,y};
            cell.append(subdivChar(l));

            south += subcellLatSpan * y;
            north = south + subcellLatSpan;

            west += subcellLonSpan * x;
            east = west + subcellLonSpan;
        }
        return cell.toString();
    }

    /**
     * Returns the geocell character in the 4x4 alphabet grid at pos. (x, y).
     * @param pos
     * @return Returns the geocell character in the 4x4 alphabet grid at pos. (x, y).
     */
    public static char subdivChar(int[] pos) {
        // NOTE: This only works for grid size 4.
        return GEOCELL_ALPHABET.charAt(
                (pos[1] & 2) << 2 |
                (pos[0] & 2) << 1 |
                (pos[1] & 1) << 1 |
                (pos[0] & 1) << 0);
    }
}
