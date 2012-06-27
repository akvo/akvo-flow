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

package com.gallatinsystems.gis.app.gwt.client;

import java.util.ArrayList;
import java.util.TreeMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * Interface for GISSupportServices. This service supoorts listing country
 * codes, coordinatetype, feature types and utm zones.
 * 
 */
@RemoteServiceRelativePath("gissupportrpcservice")
public interface GISSupportService extends RemoteService {
	TreeMap<String, String> listCountryCodes();

	TreeMap<String, String> listCoordinateTypes();

	ArrayList<Integer> listUTMZones();

	TreeMap<String, String> listFeatureTypes();
}
