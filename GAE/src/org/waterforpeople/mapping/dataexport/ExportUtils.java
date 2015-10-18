
package org.waterforpeople.mapping.dataexport;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.gallatinsystems.common.util.StringUtil;

public class ExportUtils {

    /**
     * Parse cell as string independent of cell type
     * 
     * @param cell
     * @return
     */
    public static String parseCellAsString(Cell cell) {
        String val = null;
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

        String rowString = "";

        for (Row row : rows) {
            for (Cell cell : row) {
                if (cell.getColumnIndex() > lastColumnIndex) {
                    break;
                } else {
                    String val = parseCellAsString(cell);
                    if (val != null && !val.equals("")) {
                        rowString += val + ",";
                        digest.update(val.getBytes());
                    }
                }
            }
            rowString += "\n";
        }

        System.out.println(rowString);
        return StringUtil.toHexString(digest.digest());
    }
}
