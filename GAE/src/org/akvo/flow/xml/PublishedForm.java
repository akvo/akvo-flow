/*
 *  Copyright (C) 2019 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.xml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;


public class PublishedForm {

    // Reads from XML and converts to Java objects
    public static XmlForm parse(String xml) throws IOException {
        return parse(xml, false);
    }

    // Reads from XML and converts to Java objects
    public static XmlForm parse(String xml, boolean strict) throws IOException {

        ObjectMapper objectMapper = new XmlMapper();

        if (!strict) {
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); //For production, ignore unknown stuff
        }
        XmlForm form = objectMapper.readValue(xml, XmlForm.class);

        return form;
    }

    // Generates XML from Java objects
    public static String generate(XmlForm tree) throws IOException {

        ObjectMapper objectMapper = new XmlMapper();

        // Reads from POJO and converts to XML
        String xml = objectMapper.writeValueAsString(tree);
        return xml;
    }

}
