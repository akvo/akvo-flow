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
package org.waterforpeople.mapping.dataexport;

public final class ExportImportConstants {

    //Output column group header, indicates modern data-cleaning format
    protected static final String METADATA_LABEL = "Metadata";

    //Metadata column headers, in canonical order
    protected static final String IDENTIFIER_LABEL = "Identifier";
    protected static final String DATA_APPROVAL_STATUS_LABEL = "Data approval status";
    protected static final String REPEAT_LABEL = "Repeat no";
    protected static final String DISPLAY_NAME_LABEL = "Display Name";
    protected static final String DEVICE_IDENTIFIER_LABEL = "Device identifier";
    protected static final String INSTANCE_LABEL = "Instance";
    protected static final String SUB_DATE_LABEL = "Submission Date";
    protected static final String SUBMITTER_LABEL = "Submitter";
    protected static final String DURATION_LABEL = "Duration";
    protected static final String FORM_VER_LABEL = "Form version"; //Good name?

    //Data column headers
    protected static final String LAT_LABEL = "Latitude";
    protected static final String LON_LABEL = "Longitude";
    protected static final String IMAGE_LABEL = "Image";
    protected static final String ELEV_LABEL = "Elevation";
    protected static final String ACC_LABEL = "Accuracy (m)";
    protected static final String OTHER_SUFFIX = "--OTHER--";
    protected static final String GEO_PREFIX = "--GEO";
    protected static final String CADDISFLY_PREFIX = "--CADDISFLY";
}
