/*
 *  Copyright (C) 2018-2019 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

public class FlowJsonObjectWriter extends ObjectMapper {

    public FlowJsonObjectWriter withExcludeNullValues() {
        this.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return this;
    }

    public void writeValue(OutputStream outStream, Object value) throws IOException {
        super.writeValue(outStream, value);
    }

    public String writeAsString(Object value) throws IOException {
        try {
            return super.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IOException(e);
        }
    }
}
