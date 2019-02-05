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
    static final String METADATA_LABEL = "Metadata";

    //Metadata column headers, in canonical order
    static final String IDENTIFIER_LABEL = "Identifier";
    static final String DATA_APPROVAL_STATUS_LABEL = "Data approval status";
    static final String REPEAT_LABEL = "Repeat no";
    static final String DISPLAY_NAME_LABEL = "Display Name";
    static final String DEVICE_IDENTIFIER_LABEL = "Device identifier";
    static final String INSTANCE_LABEL = "Instance";
    static final String SUB_DATE_LABEL = "Submission Date";
    static final String SUBMITTER_LABEL = "Submitter";
    static final String DURATION_LABEL = "Duration";
    static final String FORM_VER_LABEL = "Form version"; //Good name?
    
    //Data column headers
    static final String LAT_LABEL = "Latitude";
    static final String LON_LABEL = "Longitude";
    static final String IMAGE_LABEL = "Image";
    static final String ELEV_LABEL = "Elevation";
    static final String ACC_LABEL = "Accuracy (m)";
    static final String OTHER_SUFFIX = "--OTHER--";
    static final String GEO_PREFIX = "--GEO";
    static final String CADDISFLY_PREFIX = "--CADDISFLY";
}
