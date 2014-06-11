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

package com.gallatinsystems.common.domain;

/**
 * Simple data structure class to encapsulate responses from file upload.
 */
public class UploadStatusContainer {
    private Boolean uploadedFile = null;
    private Boolean uploadedZip = null;
    private String message = null;

    public Boolean getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Boolean uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public Boolean getUploadedZip() {
        return uploadedZip;
    }

    public void setUploadedZip(Boolean uploadedZip) {
        this.uploadedZip = uploadedZip;
    }

    private String url = null;

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
