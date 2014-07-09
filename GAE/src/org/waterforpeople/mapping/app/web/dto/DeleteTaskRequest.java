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

import com.gallatinsystems.framework.rest.RestRequest;

public class DeleteTaskRequest extends RestRequest {

    private static final long serialVersionUID = 8342489912346343508L;
    public static final String OBJECT_PARAM = "object";
    public static final String KEY_PARAM = "key";
    public static final String TASK_COUNT_PARAM = "taskCount";
    private String objectName = null;
    private String key = null;
    private String cursor = null;
    private String taskCount = null;

    public String getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(String taskCount) {
        this.taskCount = taskCount;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        if (req.getParameter(OBJECT_PARAM) != null) {
            setObjectName(req.getParameter(OBJECT_PARAM));
            if (req.getParameter(KEY_PARAM) != null) {
                setKey(req.getParameter(KEY_PARAM));
            } else {
                throw new Exception("Parameter " + KEY_PARAM + " is mandatory");
            }
            if (req.getParameter(CURSOR_PARAM) != null) {
                setCursor(req.getParameter(CURSOR_PARAM));
            }
            if (req.getParameter(TASK_COUNT_PARAM) != null) {
                setTaskCount(req.getParameter(TASK_COUNT_PARAM));
            }
        } else {
            throw new Exception("Parameter " + OBJECT_PARAM + " is mandatory");
        }
    }

    @Override
    protected void populateErrors() {

    }

}
