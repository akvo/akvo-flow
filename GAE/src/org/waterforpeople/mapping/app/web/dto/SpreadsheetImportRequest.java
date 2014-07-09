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

package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestError;
import com.gallatinsystems.framework.rest.RestRequest;

public class SpreadsheetImportRequest extends RestRequest {

    private static final long serialVersionUID = 8472898722818764987L;

    public static final String PROCESS_FILE_ACTION = "processFile";

    private static final String ID_PARAM = "identifier";
    private static final String TYPE_PARAM = "type";
    private static final String START_ROW_PARAM = "startRow";
    private static final String GROUP_ID_PARAM = "questionGroupId";
    private static final String ST_PARAM = "sessionToken";

    private String identifier;
    private String type;
    private int startRow = 0;
    private long groupId;
    private byte[] key = null;
    private String keySpec = null;

    private String sessionToken;

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        identifier = req.getParameter(ID_PARAM);
        type = req.getParameter(TYPE_PARAM);
        sessionToken = req.getParameter(ST_PARAM);
        key = req.getParameter("privateKey").getBytes();
        keySpec = req.getParameter("keySpec");
        String start = req.getParameter(START_ROW_PARAM);
        String groupId = req.getParameter(GROUP_ID_PARAM);

        if (start != null) {
            try {
                startRow = Integer.parseInt(start);
            } catch (NumberFormatException e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE,
                        "Start row must be an integer"));
            }
        }
        if (groupId != null) {
            try {
                this.groupId = Long.parseLong(groupId);
            } catch (NumberFormatException e) {
                addError(new RestError(RestError.BAD_DATATYPE_CODE,
                        RestError.BAD_DATATYPE_MESSAGE,
                        "GroupId must be an integer"));
            }
        }
    }

    @Override
    public void populateErrors() {
        if (getAction() == null) {
            String errorMsg = ACTION_PARAM + " is mandatory";
            addError(new RestError(RestError.MISSING_PARAM_ERROR_CODE,
                    RestError.MISSING_PARAM_ERROR_MESSAGE, errorMsg));
        }
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKeySpec(String keySpec) {
        this.keySpec = keySpec;
    }

    public String getKeySpec() {
        return keySpec;
    }

}
