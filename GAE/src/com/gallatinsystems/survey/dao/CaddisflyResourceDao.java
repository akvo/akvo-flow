/*
 *  Copyright (C) 2016-2018 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.survey.dao;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.akvo.flow.util.FlowJsonObjectReader;
import org.waterforpeople.mapping.domain.CaddisflyResource;

/**
 * Dao for listing Caddisfly resources Note: this class doesn't need to implement baseDAO as it
 * consists of only a single method.
 */
public class CaddisflyResourceDao {

    public static String DEFAULT_CADDISFLY_TESTS_FILE_URL = "https://akvoflow-public.s3.amazonaws.com/caddisfly-tests.json";

    private static final Logger log = Logger.getLogger(CascadeResourceDao.class
            .getName());

    public List<CaddisflyResource> listResources(String caddisflyTestsUrl) {
        Map<String, List<CaddisflyResource>> testsMap = null;
        FlowJsonObjectReader<Map<String, List<CaddisflyResource>>> jsonReader = new FlowJsonObjectReader<>();

        try {
            URL caddisflyFileUrl = new URL(caddisflyTestsUrl);
            testsMap = jsonReader.readObject(caddisflyFileUrl);
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "Error parsing Caddisfly resource: " + e.getMessage(), e);
        }

        if (testsMap != null && testsMap.get("tests") != null) {
            return testsMap.get("tests");
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * lists caddisfly resources using default caddisfly tests definition file URL
     */
    public List<CaddisflyResource> listResources() {
        return listResources(DEFAULT_CADDISFLY_TESTS_FILE_URL);
    }
}
