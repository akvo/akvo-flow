/*
 *  Copyright (C) 2010-2012,2019 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.common.domain;

/**
 * Simple data structure class to encapsulate responses from file upload.
 */
public class UploadStatusContainer {
    private Boolean uploadedZip1 = null;
    private Boolean uploadedZip2 = null;
    private String url = null;
    private String message = null;

    public Boolean getUploadedZip1() {
        return uploadedZip1;
    }

    public void setUploadedZip1(Boolean uploadedFile) {
        this.uploadedZip1 = uploadedFile;
    }

    public Boolean getUploadedZip2() {
        return uploadedZip2;
    }

    public void setUploadedZip2(Boolean uploadedZip) {
        this.uploadedZip2 = uploadedZip;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
