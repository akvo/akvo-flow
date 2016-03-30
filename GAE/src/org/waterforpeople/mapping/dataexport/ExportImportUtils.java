/*  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
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

    private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
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

    public static String formatDate(Date date) {
        if (date != null) {
            return DATE_FORMAT.get().format(date);
        } else {
            return "";
        }
    }

    public static Date parseDate(String dateString) {
        if (dateString == null || dateString.equals("")) {
            return null;
        }
        try {
            return new Date(Long.parseLong(dateString));
        } catch (NumberFormatException nfe) {
            try {
                return DATE_FORMAT.get().parse(dateString);
            } catch (ParseException pe) {
                log.error("bad date format: " + dateString + "\n" + pe.getMessage(), pe);
                return null;
            }
        }
    }
    
    public static String formatImage(String value) {
        // Fetch plain image filename
        return MediaResponse.format(value, MediaResponse.VERSION_STRING);
    }
}
