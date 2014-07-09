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

/**
 * Factory responsible for returning a DataExporter instance that is identified by the key passed
 * in.
 * 
 * @author Christopher Fagiani
 */
public interface DataImportExportFactory {
    /**
     * gets a DataExporter corresponding to the type passed in
     * 
     * @param type
     * @return - DataExporter instance or null if not found
     */
    public DataExporter getExporter(String type);

    /**
     * gets a DataImporter corresponding to the type passed in
     * 
     * @param type
     * @return - DataImporter instance or null if not found
     */
    public DataImporter getImporter(String type);
}
