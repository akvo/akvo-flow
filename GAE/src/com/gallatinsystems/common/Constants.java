/*
 *  Copyright (C) 2010-2017 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.common;

public class Constants {

    public final static String ALL_RESULTS = "all";
    public final static String BUILD_COUNTRY_TECH_TYPE_FRAGMENTS = "buildCountryTechTypeFragments";
    public final static String BUILD_COUNTRY_FRAGMENTS = "buildCountry";
    public static final String[] EXCLUDED_PROPERTIES = {
            "key",
            "createdDateTime", "lastUpdateDateTime", "lastUpdateUserId",
            "createUserId"
    };
    public static final int MAX_LENGTH = 500;
    public static final int CONNECTION_TIMEOUT = 60 * 1000; // 1min
    public static final int READ_TIMEOUT = 2 * 60 * 1000; // 2min
    public static final int TASK_RETRY_INTERVAL = 2 * 10 * 1000; // 2 mins
    public static final int TASK_DELAY = 2 * 60 * 1000;
    public static final int MAX_TASK_RETRIES = 7;
    public static final String AWS_ACCESS_ID = "aws_identifier";
    public static final String AWS_SECRET_KEY = "aws_secret_key";
    public static final String ANCESTOR_IDS_FIELD = "ancestorIds";
    public static final String API_V1_PREFIX = "/api/v1";
    public static final Long ROOT_FOLDER_ID = 0L;

    // caddisfly constants
    public static final String CADDISFLY_UUID = "uuid";
    public static final String CADDISFLY_IMAGE = "image";
    public static final String CADDISFLY_RESULT = "result";
    public static final String CADDISFLY_RESULT_VALUE = "value";
    public static final String CADDISFLY_RESULT_ID = "id";

    public static final String DEFAULT_SURVEY_FILE_NAME = "survey";

}
