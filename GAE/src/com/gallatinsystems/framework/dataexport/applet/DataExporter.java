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
 * Interface all classes that perform an export should implement so it can be used with the
 * DataExporterFactory
 * 
 * @author Christopher Fagiani
 */
public interface DataExporter {

    /**
     * exports the data to a file specified by the fileName parameter.
     * 
     * @param criteria
     * @param fileName
     * @param serverBase
     * @param options
     */
    public void export(Map<String, String> criteria, File fileName, String serverBase,
            Map<String, String> options);
}
