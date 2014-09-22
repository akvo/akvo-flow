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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.JApplet;

/**
 * Base class that provides common functionality used by the data import/export applets that make
 * use of the DataImporter and DataExporter framework
 * 
 * @author Christopher Fagiani
 */
public class AbstractDataImportExportApplet extends JApplet {

    private static final long serialVersionUID = 2018425813551644493L;
    private static final String FACTORY_PARAM = "factoryClass";
    private static final String SERVER_BASE_OVERRIDE_PARAM = "serverOverride";
    private static final String CRITERIA_PARAM = "criteria";

    /**
     * uses the factoryClass applet parameter to instantiate a factory class. This parameter MUST be
     * set in the applet tag
     * 
     * @return
     */
    protected DataImportExportFactory getDataImportExportFactory() {
        String factoryClass = getParameter(FACTORY_PARAM);
        DataImportExportFactory dataImporterFactory = null;
        if (factoryClass != null) {
            try {
                dataImporterFactory = (DataImportExportFactory) Class.forName(
                        factoryClass).newInstance();
            } catch (Exception e) {
                System.err.println("Could not instantiate factory: "
                        + factoryClass);
                e.printStackTrace(System.err);
            }
        } else {
            System.err.println("Factory must be specified");
        }
        return dataImporterFactory;
    }

    /**
     * parses configuration criteria set in the applet tag
     * 
     * @param source
     * @return
     */
    protected Map<String, String> parseCriteria(String source) {
        String delimiter = ":=";
        if (source != null && !source.contains(":=")) {
            delimiter = "=";
        }
        Map<String, String> crit = new HashMap<String, String>();
        if (source != null) {
            StringTokenizer strTok = new StringTokenizer(source, ";");
            while (strTok.hasMoreTokens()) {
                String[] parts = strTok.nextToken().split(delimiter);
                if (parts.length == 2) {
                    crit.put(parts[0], parts[1]);
                }
            }
        }
        return crit;
    }

    /**
     * returns the server base that hosts this applet (unless it has been overridden in the applet
     * configuration)
     * 
     * @return
     */
    protected String getServerBase() {
        String serverBase = getParameter(SERVER_BASE_OVERRIDE_PARAM);
        if (serverBase == null || serverBase.trim().length() == 0) {
            serverBase = getCodeBase().toString();
        }
        return serverBase;
    }

    /**
     * reads the criteria applet config parameter and returns a map of key/values
     * 
     * @return
     */
    protected Map<String, String> getConfigCriteria() {
        final String criteria = getParameter(CRITERIA_PARAM);

        if (criteria != null && !"".equals(criteria)) {
            return parseCriteria(criteria);
        }

        System.out.println("Loading configuration from UploadConstants.properties");

        final Properties uploadProperties = new Properties();
        final InputStream is = AbstractDataImportExportApplet.class
                .getResourceAsStream("/UploadConstants.properties");
        try {
            uploadProperties.load(is);
        } catch (IOException ioe) {
            System.err.println("Error loading upload constants");
            return null;
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                // no-op
            }
        }

        @SuppressWarnings({
                "unchecked", "rawtypes"
        })
        Map<String, String> config = new HashMap(System.getProperties());
        return config;
    }
}
