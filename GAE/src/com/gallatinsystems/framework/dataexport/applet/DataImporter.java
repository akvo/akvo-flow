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

package com.gallatinsystems.framework.dataexport.applet;

import java.io.File;
import java.util.Map;

/**
 * interface for any importer to be run via the import applet
 * 
 * @author Christopher Fagiani
 */
public interface DataImporter {

    /**
     * validates the format of the import file
     * 
     * @param file
     * @return - map of error messages
     */
    public Map<Integer, String> validate(File file);

    /**
     * executes the import by reading from file and calling methods on the server.
     * 
     * @param file
     * @param serverBase
     * @param criteria
     */
    public void executeImport(File file, String serverBase,
            Map<String, String> criteria);

}
