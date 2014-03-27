/**
  This is a modified version of geomodel.js by Daniel Kim. The original can be found here:
  https://github.com/danieldkim/geomodel/blob/master/geomodel.js
  This file only retains the functions used by the Akvo FLOW dashboard, and makes
  some javascript style improvements
**/

/**
#
# Copyright 2010 Daniel Kim
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
*/

/**
 * This library is an implementation of the Geomodel/Geocell concept: 
 * http://code.google.com/apis/maps/articles/geospatial.html
 * on the web: https://github.com/datadesk/geomodel-js
 * It is a direct port of the Java and Python versions of the geomodel project:
 *
 * - http://code.google.com/p/javageomodel/
 * - http://code.google.com/p/geomodel/
 *
 * Most of the code for the core Geocell concept was ported from the Java version.
 * The proximity_fetch implementation was ported from the Python version, with 
 * the "entity" store abstracted out to remove the coupling to Google App Engine.
 * Also, this proximity_fetch does not directly support the execution of an  
 * additional query to filter the promixity results with, though this could be 
 * implemented in whatever entity finder function the user passes to it.
 *

	A geocell is a hexadecimal string that defines a two dimensional rectangular
	region inside the [-90,90] x [-180,180] latitude/longitude space. A geocell's
	'resolution' is its length. For most practical purposes, at high resolutions,
	geocells can be treated as single points.

	Much like geohashes (see http://en.wikipedia.org/wiki/Geohash), geocells are
	hierarchical, in that any prefix of a geocell is considered its ancestor, with
	geocell[:-1] being geocell's immediate parent cell.

	To calculate the rectangle of a given geocell string, first divide the
	[-90,90] x [-180,180] latitude/longitude space evenly into a 4x4 grid like so:

	             +---+---+---+---+ (90, 180)
	             | a | b | e | f |
	             +---+---+---+---+
	             | 8 | 9 | c | d |
	             +---+---+---+---+
	             | 2 | 3 | 6 | 7 |
	             +---+---+---+---+
	             | 0 | 1 | 4 | 5 |
	  (-90,-180) +---+---+---+---+

	NOTE: The point (0, 0) is at the intersection of grid cells 3, 6, 9 and c. And,
	      for example, cell 7 should be the sub-rectangle from
	      (-45, 90) to (0, 180).

	Calculate the sub-rectangle for the first character of the geocell string and
	re-divide this sub-rectangle into another 4x4 grid. For example, if the geocell
	string is '78a', we will re-divide the sub-rectangle like so:

	               .                   .
	               .                   .
	           . . +----+----+----+----+ (0, 180)
	               | 7a | 7b | 7e | 7f |
	               +----+----+----+----+
	               | 78 | 79 | 7c | 7d |
	               +----+----+----+----+
	               | 72 | 73 | 76 | 77 |
	               +----+----+----+----+
	               | 70 | 71 | 74 | 75 |
	  . . (-45,90) +----+----+----+----+
	               .                   .
	               .                   .

	Continue to re-divide into sub-rectangles and 4x4 grids until the entire
	geocell string has been exhausted. The final sub-rectangle is the rectangular
	region for the geocell.
 * 
 * 
 * 
 */

if (typeof exports !== 'undefined') exports.create_geomodel = create_geomodel;

function create_geomodel() {
	// Geocell algorithm constants.
  var GEOCELL_GRID_SIZE = 4;
  var GEOCELL_ALPHABET = "0123456789abcdef";

  // The maximum *practical* geocell resolution.
  var MAX_GEOCELL_RESOLUTION = 13;

  // The maximum number of geocells to consider for a bounding box search.
  var MAX_FEASIBLE_BBOX_SEARCH_CELLS = 16;

  // Direction enumerations.
  var NORTHWEST = [-1,1];
  var NORTH = [0,1];
  var NORTHEAST = [1,1];
  var EAST = [1,0];
  var SOUTHEAST = [1,-1];
  var SOUTH = [0,-1];
  var SOUTHWEST = [-1,-1];
  var WEST = [-1,0];

  var RADIUS = 6378135;
  var RADIUS_MI = 3963.2;

  // adding this which is used in the GeoCell.best_bbox_search_cells function
  String.prototype.startsWith = function(str) {
    return (this.match("^"+str)==str);
  };

  // adding the Array and String functions below, which are used by the 
  // interpolate function and others
  function arrayAddAll(target) {
    var a, i;
    for (a = 1;  a < arguments.length;  a++) {
      arr = arguments[a];
      for (i = 0;  i < arr.length;  i++) {
        target.push(arr[i]);
      }
    }
  }

  function arrayGetLast(arr) { return arr[arr.length-1];}
  function arrayGetFirst(arr) { return arr[0];}
  String.prototype.equalsIgnoreCase = function(arg) {
    return (this.toLowerCase() == arg.toLowerCase());
  };

  return {
    create_point: function(lat, lon) {
     // limit point range
     if (lat > 90) {
    	 lat = 90;
     } else if (lat < -90){
    	 lat = -90;
     }
    
     if (lon > 180) {
    	 lon = 180;
     } else if (lon < -180){
    	 lon = -180;
     }
 
      return { lat: lat, lon:lon };
    },

    create_bounding_box: function(north, east, south, west) {
      var north_,south_;

      if (south > north) {
        south_ = north;
        north_ = south;
      } else {
        south_ = south;
        north_ = north;
      }

      return {
      // Don't swap east and west to allow disambiguation of
      // antimeridian crossing.
        northEast: this.create_point(north_, east),
        southWest: this.create_point(south_, west),
        getNorth: function() {
          return this.northEast.lat;
        },
        getSouth: function() {
          return this.southWest.lat;
        },
        getWest: function() {
          return this.southWest.lon;
        },
        getEast: function() {
          return this.northEast.lon;
        }
      };
    },

    /**
     * Returns an efficient set of geocells to search in a bounding box query.

      This method is guaranteed to return a set of geocells having the same
      resolution.

     * @param bbox: A geotypes.Box indicating the bounding box being searched.
     * @param costFunction: A function that accepts two arguments:
            * num_cells: the number of cells to search
            * resolution: the resolution of each cell to search
            and returns the 'cost' of querying against this number of cells
            at the given resolution.)
     * @return A list of geocell strings that contain the given box.
     */
    best_bbox_search_cells: function(bbox) {
      var cell_ne, cell_sw, min_cost, min_cost_cell_set, min_resolution, max_resolution, cur_ne, cur_sw,
      num_cells, cell_set, cost, cur_resolution;

      cell_ne = this.compute(bbox.northEast, MAX_GEOCELL_RESOLUTION);
      cell_sw = this.compute(bbox.southWest, MAX_GEOCELL_RESOLUTION);

      // The current lowest BBOX-search cost found; start with practical infinity.
      min_cost = Number.MAX_VALUE;

      // The set of cells having the lowest calculated BBOX-search cost.
      min_cost_cell_set = [];

      // First find the common prefix, if there is one.. this will be the base
      // resolution.. i.e. we don't have to look at any higher resolution cells.
      min_resolution = 1;
      max_resolution = Math.min(cell_ne.length, cell_sw.length);
      while (min_resolution < max_resolution  &&
            cell_ne.substring(0, min_resolution+1).startsWith(cell_sw.substring(0, min_resolution+1))) {
        min_resolution++;
      }

      // Iteravely calculate all possible sets of cells that wholely contain
      // the requested bounding box.
      for (cur_resolution = min_resolution;
          cur_resolution < MAX_GEOCELL_RESOLUTION + 1;
          cur_resolution++) {
        cur_ne = cell_ne.substring(0, cur_resolution);
        cur_sw = cell_sw.substring(0, cur_resolution);

        num_cells = this.interpolation_count(cur_ne, cur_sw);
        if (num_cells > MAX_FEASIBLE_BBOX_SEARCH_CELLS) {
          continue;
        }

        cell_set = this.interpolate(cur_ne, cur_sw);
        cell_set.sort();

        cost = cell_set.length <= 16 ? 0 : 1000000;

        if (cost <= min_cost) {
          min_cost = cost;
          min_cost_cell_set = cell_set;
        } else {
          if (min_cost_cell_set.length === 0) {
            min_cost_cell_set = cell_set;
          }
          // Once the cost starts rising, we won't be able to do better, so abort.
          break;
        }
      }
      return min_cost_cell_set;
    },

    /**
     * Determines whether the given cells are collinear along a dimension.

        Returns True if the given cells are in the same row (column_test=False)
        or in the same column (column_test=True).

     * @param cell1: The first geocell string.
     * @param cell2: The second geocell string.
     * @param column_test: A boolean, where False invokes a row collinearity test
              and 1 invokes a column collinearity test.
     * @return A bool indicating whether or not the given cells are collinear in the given
          dimension.
     */
    collinear: function(cell1, cell2, column_test) {

      for (var i = 0; i < Math.min(cell1.length, cell2.length); i++) {
        var l1 = this._subdiv_xy(cell1.charAt(i));
        var x1 = l1[0];
        var y1 = l1[1];
        var l2 = this._subdiv_xy(cell2.charAt(i));
        var x2 = l2[0];
        var y2 = l2[1];

        // Check row collinearity (assure y's are always the same).
        if (!column_test && y1 != y2) {
          return false;
        }

        // Check column collinearity (assure x's are always the same).
        if (column_test && x1 != x2) {
          return false;
        }
      }
      return true;
    },

    /**
     * 
     *    Calculates the grid of cells formed between the two given cells.

      Generates the set of cells in the grid created by interpolating from the
      given Northeast geocell to the given Southwest geocell.

      Assumes the Northeast geocell is actually Northeast of Southwest geocell.

     * 
     * @param cell_ne: The Northeast geocell string.
     * @param cell_sw: The Southwest geocell string.
     * @return A list of geocell strings in the interpolation.
     */
    interpolate: function(cell_ne, cell_sw) {
      var cell_set, cell_first, cell_tmp, cell_tmp_row, cell_set_last, i, result;
      // 2D array, will later be flattened.
      cell_set = [];
      cell_first = [];
      cell_first.push(cell_sw);
      cell_set.push(cell_first);

      // First get adjacent geocells across until Southeast--collinearity with
      // Northeast in vertical direction (0) means we're at Southeast.
      while (!this.collinear(arrayGetLast(cell_first), cell_ne, true)) {
        cell_tmp = this.adjacent(arrayGetLast(cell_first), EAST);
        if (cell_tmp === null) {
          break;
        }
        cell_first.push(cell_tmp);
      }

      // Then get adjacent geocells upwards.
      while (!arrayGetLast(arrayGetLast(cell_set)).equalsIgnoreCase(cell_ne)) {

        cell_tmp_row = [];
        cell_set_last = arrayGetLast(cell_set);
        for (i = 0; i < cell_set_last.length; i++) {
          cell_tmp_row.push(this.adjacent(cell_set_last[i], NORTH));
        }
        if ( !arrayGetFirst(cell_tmp_row) ) {
          break;
        }
        cell_set.push(cell_tmp_row);
      }

      // Flatten cell_set, since it's currently a 2D array.
      result = [];
      for (i = 0; i < cell_set.length; i++) {
        arrayAddAll(result, cell_set[i]);
      }
      return result;
    },


    /**
     * Computes the number of cells in the grid formed between two given cells.

      Computes the number of cells in the grid created by interpolating from the
      given Northeast geocell to the given Southwest geocell. Assumes the Northeast
      geocell is actually Northeast of Southwest geocell.

     * @param cell_ne: The Northeast geocell string.
     * @param cell_sw: The Southwest geocell string.
     * @return An int, indicating the number of geocells in the interpolation.
     */
    interpolation_count: function(cell_ne, cell_sw) {
      var bbox_ne, bbox_sw, cell_lat_span, cell_lon_span, num_cols, num_rows;

      bbox_ne = this.compute_box(cell_ne);
      bbox_sw = this.compute_box(cell_sw);

      cell_lat_span = bbox_sw.getNorth() - bbox_sw.getSouth();
      cell_lon_span = bbox_sw.getEast() - bbox_sw.getWest();

      num_cols = Math.floor((bbox_ne.getEast() - bbox_sw.getWest()) / cell_lon_span);
      num_rows = Math.floor((bbox_ne.getNorth() - bbox_sw.getSouth()) / cell_lat_span);

      return num_cols * num_rows;
    },

    /**
     * 
     * Calculates all of the given geocell's adjacent geocells.    
     * 
     * @param cell: The geocell string for which to calculate adjacent/neighboring cells.
     * @return A list of 8 geocell strings and/or None values indicating adjacent cells.
     */

    all_adjacents: function(cell) {
      var result, directions, i;
      result = [];
      directions = [NORTHWEST, NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST];
      for (i = 0; i < directions.length; i++) {
        result.push(this.adjacent(cell, directions[i]));
      }
      return result;
    },

    /**
     * Calculates the geocell adjacent to the given cell in the given direction.
     * 
     * @param cell: The geocell string whose neighbor is being calculated.
     * @param dir: An (x, y) tuple indicating direction, where x and y can be -1, 0, or 1.
            -1 corresponds to West for x and South for y, and
             1 corresponds to East for x and North for y.
            Available helper constants are NORTH, EAST, SOUTH, WEST,
            NORTHEAST, NORTHWEST, SOUTHEAST, and SOUTHWEST.
     * @return The geocell adjacent to the given cell in the given direction, or null if
        there is no such cell.

     */
    adjacent: function(cell, dir) {
      var dx, dy, cell_adj_arr, i, l, x, y, l2;
      if (!cell) {
        return null;
      }
      dx = dir[0];
      dy = dir[1];
      cell_adj_arr = cell.split(""); // Split the geocell string characters into a list.
      i = cell_adj_arr.length - 1;

      while (i >= 0 && (dx !== 0 || dy !== 0)) {
        l = this._subdiv_xy(cell_adj_arr[i]);
        x = l[0];
        y = l[1];

        // Horizontal adjacency.
        if (dx == -1) {  // Asking for left.
          if (x === 0) {  // At left of parent cell.
            x = GEOCELL_GRID_SIZE - 1;  // Becomes right edge of adjacent parent.
          } else {
            x--;  // Adjacent, same parent.
            dx = 0; // Done with x.
          }
        }
        else if (dx == 1) { // Asking for right.
          if (x == GEOCELL_GRID_SIZE - 1) { // At right of parent cell.
            x = 0;  // Becomes left edge of adjacent parent.
          } else {
            x++;  // Adjacent, same parent.
            dx = 0;  // Done with x.
          }
        }

        // Vertical adjacency.
        if (dy == 1) { // Asking for above.
          if (y == GEOCELL_GRID_SIZE - 1) {  // At top of parent cell.
            y = 0;  // Becomes bottom edge of adjacent parent.
          } else {
            y++;  // Adjacent, same parent.
            dy = 0;  // Done with y.
          }
        } else if (dy == -1) {  // Asking for below.
          if (y === 0) { // At bottom of parent cell.
            y = GEOCELL_GRID_SIZE - 1; // Becomes top edge of adjacent parent.
          } else {
            y--;  // Adjacent, same parent.
            dy = 0;  // Done with y.
          }
        }

        l2 = [x,y];
        cell_adj_arr[i] = this._subdiv_char(l2);
        i--;
      }
      // If we're not done with y then it's trying to wrap vertically,
      // which is a failure.
      if (dy !== 0) {
        return null;
      }

      // At this point, horizontal wrapping is done inherently.
      return cell_adj_arr.join("");
    },

    /** 
     * Computes the geocell containing the given point to the given resolution.

      This is a simple 16-tree lookup to an arbitrary depth (resolution).
     * 
     * @param point: The geotypes.Point to compute the cell for.
     * @param resolution: An int indicating the resolution of the cell to compute.
     * @return The geocell string containing the given point, of length resolution.
     */
    compute: function(point, resolution) {
      var north, south, east, west, cell, subcell_lon_span, subcell_lat_span, x, y, l;

      resolution = resolution || MAX_GEOCELL_RESOLUTION;

      north = 90.0;
      south = -90.0;
      east = 180.0;
      west = -180.0;

      cell = "";
      while (cell.length < resolution) {
        subcell_lon_span = (east - west) / GEOCELL_GRID_SIZE;
        subcell_lat_span = (north - south) / GEOCELL_GRID_SIZE;

        x = Math.min(Math.floor(GEOCELL_GRID_SIZE * (point.lon - west) / (east - west)),
                         GEOCELL_GRID_SIZE - 1);
        y = Math.min(Math.floor(GEOCELL_GRID_SIZE * (point.lat - south) / (north - south)),
                         GEOCELL_GRID_SIZE - 1);

        l = [x,y];
        cell += this._subdiv_char(l);

        south += subcell_lat_span * y;
        north = south + subcell_lat_span;

        west += subcell_lon_span * x;
        east = west + subcell_lon_span;
      }
      return cell;
    },

    /**
     * Computes the rectangular boundaries (bounding box) of the given geocell.
     * 
     * @param cell_: The geocell string whose boundaries are to be computed.
     * @return A geotypes.Box corresponding to the rectangular boundaries of the geocell.
     */
    compute_box: function(cell_) {
      var bbox, cell, subcell_lon_span, subcell_lat_span, l, x, y;
      if (!cell_) {
        return null;
      }

      bbox = this.create_bounding_box(90.0, 180.0, -90.0, -180.0);
      cell = cell_;
      while (cell.length > 0) {
        subcell_lon_span = (bbox.getEast() - bbox.getWest()) / GEOCELL_GRID_SIZE;
        subcell_lat_span = (bbox.getNorth() - bbox.getSouth()) / GEOCELL_GRID_SIZE;

        l = this._subdiv_xy(cell.charAt(0));
        x = l[0];
        y = l[1];

        bbox = this.create_bounding_box(bbox.getSouth() + subcell_lat_span * (y + 1),
            bbox.getWest()  + subcell_lon_span * (x + 1),
            bbox.getSouth() + subcell_lat_span * y,
            bbox.getWest()  + subcell_lon_span * x);

        cell = cell.substring(1);
      }

      return bbox;
    },

    /**
     * Returns the (x, y) of the geocell character in the 4x4 alphabet grid.
     * @param char_
     * @return Returns the (x, y) of the geocell character in the 4x4 alphabet grid.
     */
    _subdiv_xy:function(char_) {
      var charI;
      // NOTE: This only works for grid size 4.
      charI = GEOCELL_ALPHABET.indexOf(char_);
      return [(charI & 4) >> 1 | (charI & 1) >> 0,
                (charI & 8) >> 2 | (charI & 2) >> 1];
    },

    /** 
     * Returns the geocell character in the 4x4 alphabet grid at pos. (x, y).
     * @param pos
     * @return Returns the geocell character in the 4x4 alphabet grid at pos. (x, y).
     */
    _subdiv_char: function(pos) {
      // NOTE: This only works for grid size 4.
      return GEOCELL_ALPHABET.charAt(
                                (pos[1] & 2) << 2 |
                                (pos[0] & 2) << 1 |
                                (pos[1] & 1) << 1 |
                                (pos[0] & 1) << 0);
    },
  };
}