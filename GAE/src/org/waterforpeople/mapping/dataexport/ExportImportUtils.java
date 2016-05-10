/*  Copyright (C) 2015-2016 Stichting Akvo (Akvo Foundation)
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.waterforpeople.mapping.serialization.response.MediaResponse;

import com.gallatinsystems.common.util.StringUtil;

public class ExportImportUtils {
    private static final Logger log = Logger.getLogger(ExportImportUtils.class);

    private static final ThreadLocal<DataFormatter> DATA_FORMATTER = new ThreadLocal<DataFormatter>() {
        @Override
        protected DataFormatter initialValue() {
            return new DataFormatter();
        }
    };

    private static final ThreadLocal<DateFormat> DATE_RESPONSE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(TimeZone.getTimeZone("GMT"));
            return df;
        };
    };

    private static final ThreadLocal<DateFormat> DATE_TIME_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        };
    };

    /**
     * Parse cell as string independent of cell type
     *
     * @param cell
     * @return
     */
    public static String parseCellAsString(Cell cell) {
        return DATA_FORMATTER.get().formatCellValue(cell).trim();
    }

    /**
     * Calculate the md5 digest of each row up to (and including) the lastColumnIndex
     *
     * @param rows
     * @param lastColumnIndex
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String md5Digest(List<Row> rows, int lastColumnIndex)
            throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("MD5");

        for (Row row : rows) {
            for (Cell cell : row) {
                if (cell.getColumnIndex() > lastColumnIndex) {
                    break;
                } else {
                    String val = parseCellAsString(cell);
                    if (val != null && !val.equals("")) {
                        digest.update(val.getBytes());
                    }
                }
            }
        }

        return StringUtil.toHexString(digest.digest());
    }

    /*
     * Format date into a date-time string.
     */
    public static String formatDateTime(Date date) {
        if (date != null) {
            return DATE_TIME_FORMAT.get().format(date);
        } else {
            return "";
        }
    }

    /*
     * Format DateQuestion response in a human-readable way. This date will not contain time details
     * (yyyy-MM-dd)
     */
    public static String formatDateResponse(String value) {
        Date date = parseDatastoreDate(value);
        if (date != null) {
            return DATE_RESPONSE_FORMAT.get().format(date);
        }
        return "";
    }

    /*
     * Convert a DateQuestion response value into a Date. Note: Values may be presented in an
     * inconsistent manner, possibly containing ISO-8601 dates.
     */
    public static Date parseDatastoreDate(String value) {
        try {
            return new Date(Long.valueOf(value));
        } catch (NumberFormatException e) {
            log.error("Value is not a valid timestamp: " + value);
        }

        // Value is not a timestamp. Try to parse as a spreadsheet value
        return parseSpreadsheetDate(value);
    }

    /*
     * Parse a date from the spreadsheet. This includes collection dates (date-time) and
     * DateQuestion responses (ISO8601 or date-time).
     */
    public static Date parseSpreadsheetDate(String dateString) {
        if (dateString == null || dateString.equals("")) {
            return null;
        }

        // Parse date-time format by default (collection dates and old responses)
        try {
            return DATE_TIME_FORMAT.get().parse(dateString);
        } catch (ParseException e) {
            // Date is not date-time formatted
        }

        // Use ISO 8601 otherwise
        try {
            return DATE_RESPONSE_FORMAT.get().parse(dateString);
        } catch (ParseException e) {
            // Date is not ISO 8601
        }

        log.warn("Response doesn't contain a valid format: " + dateString);
        return null;
    }

    public static String formatImage(String value) {
        // Fetch plain image filename
        return MediaResponse.format(value, MediaResponse.VERSION_STRING);
    }

}
