/*
 * Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Flow.
 *
 * Akvo Flow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Flow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Flow.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.akvo.flow.rest.form;

public class FormUploadXml {
    private final String formIdFilename;
    private final String formIdVersionFilename;
    private final String xmlContent;

    public FormUploadXml(String formIdFilename, String formIdVersionFilename, String xmlContent) {
        this.formIdFilename = formIdFilename;
        this.formIdVersionFilename = formIdVersionFilename;
        this.xmlContent = xmlContent;
    }

    public String getFormIdFilename() {
        return formIdFilename;
    }

    public String getFormIdVersionFilename() {
        return formIdVersionFilename;
    }

    public String getXmlContent() {
        return xmlContent;
    }
}
