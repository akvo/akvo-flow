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

package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;

/**
 * transfer object for transfering Rest status messages
 * 
 * @author Mark Tiele Westra
 */
public class RestStatusDto implements Serializable {

    private static final long serialVersionUID = -5905705837362187943L;

    private String status = "";
    private String message = "";
    private String since = "";
    private Integer num;
    private Long keyId;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setSince(String since) {
        this.since = since;
    }

    public String getSince() {
        return since;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getnum() {
        return num;
    }

    public void setKeyId(Long keyId) {
        this.keyId = keyId;
    }

    public Long getKeyId() {
        return keyId;
    }

}
