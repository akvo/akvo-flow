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

package com.gallatinsystems.framework.gwt.dto.client;

import java.io.Serializable;

/**
 * base class to be used as a response from a service call. This wrapper object supports a payload
 * (parameterized type) as well as an optional cursor to facilitate building services that support
 * pagination.
 * 
 * @author Christopher Fagiani
 * @param <T> - payload type
 */
public class ResponseDto<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = -670907947130363885L;
    public static final int DEFAULT_PAGE_SIZE = 20;
    private String cursorString = null;
    private T payload = null;

    public ResponseDto() {
    }

    /**
     * returns the current cursor that corresponds to the payload. Returns null if no cursor is set.
     * 
     * @return
     */
    public String getCursorString() {
        return cursorString;
    }

    public void setCursorString(String cursorString) {
        this.cursorString = cursorString;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

}
