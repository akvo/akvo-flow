
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
import org.apache.poi.ss.usermodel.Row;

import com.gallatinsystems.common.util.StringUtil;

public class ExportUtils {
    private static final Logger log = Logger.getLogger(ExportUtils.class);
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
        String val = "";
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    val = cell.getBooleanCellValue() + "";
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    val = cell.getNumericCellValue() + "";
                    break;
                default:
                    val = cell.getStringCellValue();
                    break;
            }
        }
        return val;
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
        try {
            return DATE_FORMAT.get().parse(dateString);
        } catch (ParseException e) {
            log.error("bad date format: " + dateString + "\n" + e.getMessage(), e);
            return null;
        }
    }

}
