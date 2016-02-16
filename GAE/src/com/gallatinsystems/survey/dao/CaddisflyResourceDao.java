/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.waterforpeople.mapping.domain.CaddisflyResource;


/**
 * Dao for listing Caddisfly resources
 * Note: this class doesn't need to implement baseDAO as it consists of only a single method.
 */
public class CaddisflyResourceDao {

    private static final Logger log = Logger.getLogger(CascadeResourceDao.class.getName());

    /**
     * lists caddisfly resources. Source is the json file caddisfly-tests.json stored in WEB-INF/resources
     * 
     * @param item
     */
    public List<CaddisflyResource> listResources() {
        List<CaddisflyResource> result = new ArrayList<CaddisflyResource>();
    	
        try {
        	InputStream stream = getClass().getClassLoader().getResourceAsStream ("resources/caddisfly/caddisfly-tests.json");
        	String jsonTxt = IOUtils.toString(stream);
        	
            // create a list of caddisflyResource objects
        	ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readValue(jsonTxt, JsonNode.class);
            ArrayNode testsNode = (ArrayNode) rootNode.get("tests");
            
            for (int i = 0; i < testsNode.size(); i++){
            	CaddisflyResource cr = JsonToCaddisflyResource(testsNode.get(i));
            	result.add(cr);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error parsing Caddisfly resource: " + e.getMessage(), e);
        }
    	return result;
    }

	private CaddisflyResource JsonToCaddisflyResource(JsonNode rootNode) {
		ObjectMapper mapper = new ObjectMapper();
		CaddisflyResource cr = null;
		try {
			cr = mapper.readValue(rootNode, CaddisflyResource.class);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Error parsing Caddisfly resource: " + e.getMessage(), e);
		}
		return cr;
	}
}