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

package com.beoui.geocell.model;

public class BoundingBox {

    private final Point northEast;
    private final Point southWest;

    public BoundingBox(double north, double east, double south, double west) {
        double north_,south_;
        if(south > north) {
            south_ = north;
            north_ = south;
        } else {
            south_ = south;
            north_ = north;
        }

        // Don't swap east and west to allow disambiguation of
        // antimeridian crossing.

        northEast = new Point(north_, east);
        southWest = new Point(south_, west);
    }

    public double getNorth() {
        return northEast.getLat();
    }

    public double getSouth() {
        return southWest.getLat();
    }

    public double getWest() {
        return southWest.getLon();
    }

    public double getEast() {
        return northEast.getLon();
    }

    public Point getNorthEast() {
        return northEast;
    }

    public Point getSouthWest() {
        return southWest;
    }

}
