/*
 *  Copyright (C) 2016-2017 Stichting Akvo (Akvo Foundation)
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

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.waterforpeople.mapping.domain.CaddisflyResource;

/**
 * Dao for listing Caddisfly resources Note: this class doesn't need to implement baseDAO as it
 * consists of only a single method.
 */
public class CaddisflyResourceDao {
    private static ObjectMapper mapper = new ObjectMapper();

    private static final Logger log = Logger.getLogger(CascadeResourceDao.class
            .getName());

    /**
     * lists caddisfly resources. Source is the json file caddisfly-tests.json stored in
     * WEB-INF/resources
     */
    public List<CaddisflyResource> listResources() {
        List<CaddisflyResource> result = null;

        try {
            URL caddisflyFileUrl = new URL(
                    "https://akvoflow-public.s3.amazonaws.com/caddisfly-tests.json");
            InputStream stream = caddisflyFileUrl.openStream();

            // create a list of caddisflyResource objects
            JsonNode rootNode = mapper.readTree(stream);
            result = mapper.readValue(rootNode.get("tests"),
                    new TypeReference<List<CaddisflyResource>>() {
                    });

        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "Error parsing Caddisfly resource: " + e.getMessage(), e);
        }

        if (result != null) {
            return result;
        } else {
            return Collections.emptyList();
        }
    }
}
