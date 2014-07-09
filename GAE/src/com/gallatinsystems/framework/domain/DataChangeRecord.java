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

package com.gallatinsystems.framework.domain;

/**
 * this is a non-persistent class used to represent logical change records. It will encapsulate an
 * object type, a field value and an old/new value.
 * 
 * @author Christopher Fagiani
 */
public class DataChangeRecord {

    private static final String DELIMITER = "#~#";
    private String type;
    private String oldVal;
    private String newVal;
    private String id;

    public DataChangeRecord(String t, String i, String o, String n) {
        type = t;
        id = i;
        oldVal = o;
        newVal = n;
    }

    /**
     * parses a packedString to hydrate a new instance. The input to this class should only ever be
     * strings that were initially generated via DataChangeRecord.packString();
     * 
     * @param packedString
     */
    public DataChangeRecord(String packedString) {
        String[] parts = packedString.split(DELIMITER);
        if (parts.length < 3) {
            throw new RuntimeException("Packed string in invalid format: "
                    + packedString);
        } else {
            type = parts[0];
            id = parts[1];
            oldVal = parts[2];
            if (parts.length > 3) {
                newVal = parts[3];
            } else {
                newVal = "";
            }
        }
    }

    /**
     * forms a string representation of this object.
     * 
     * @return
     */
    public String packString() {
        return type + DELIMITER + id + DELIMITER + oldVal + DELIMITER + newVal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOldVal() {
        return oldVal;
    }

    public void setOldVal(String oldVal) {
        this.oldVal = oldVal;
    }

    public String getNewVal() {
        return newVal;
    }

    public void setNewVal(String newVal) {
        this.newVal = newVal;
    }

}
