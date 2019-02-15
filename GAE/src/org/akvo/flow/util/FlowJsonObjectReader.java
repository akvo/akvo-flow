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

package org.akvo.flow.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class FlowJsonObjectReader extends ObjectMapper {

    public <T> T readObject(String jsonString, TypeReference<T> typeReference) throws IOException {
        return this.readValue(jsonString, typeReference);
    }

    public <T> List<T> readDtoListObject(String dtoListJsonString, TypeReference<T> listItemTypeReference) throws IOException {

        JavaType listItemType = this.getTypeFactory().constructType(listItemTypeReference);
        JavaType type = this.getTypeFactory().constructParametrizedType(List.class,List.class, listItemType);
        ObjectReader reader = this.readerFor(type);

        JsonNode dtoListNode = this.readTree(dtoListJsonString).get("dtoList");
        return reader.readValue(dtoListNode);
    }

    public <T> T readObject(URL url, TypeReference<T> typeReference) throws IOException {
        return this.readValue(url, typeReference);
    }
}
