package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;

/**
 * This importer can be used to import spreadsheets where each row is a new
 * record and the columns correspond to the questions in a specific survey
 * (matched by column index). The first column must always be the
 * SurveyedLocaleId and the second column should be the collection date
 * 
 * @author Christopher Fagiani
 * 
 */
public class FixedFormatRawDataImporter extends RawDataSpreadsheetImporter {
	
	@Override
	public void executeImport(File file, String serverBase,
			Map<String, String> criteria) {
		try {
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
			setSurveyId(criteria);
			Sheet sheet1 = getDataSheet(file);
			for (Row row : sheet1) {
				String localeId = null;
				String dateString = null;
				StringBuilder sb = new StringBuilder();
				StringBuilder valueBuilder = new StringBuilder();
				int valueCount = 0;
				sb.append("action="
						+ RawDataImportRequest.SAVE_FIXED_FIELD_SURVEY_INSTANCE_ACTION
						+ "&" + RawDataImportRequest.SURVEY_ID_PARAM + "="
						+ getSurveyId() + "&");
				for (Cell cell : row) {

					if (cell.getColumnIndex() == 0 && cell.getRowIndex() > 0) {
						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							localeId = new Double(cell.getNumericCellValue())
									.intValue() + "";
							sb.append(RawDataImportRequest.LOCALE_ID_PARAM
									+ "=" + localeId + "&");
						}
					}
					if (cell.getColumnIndex() == 1 && cell.getRowIndex() > 0) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							dateString = cell.getStringCellValue();							
						}else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							Date date=HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
							dateString = df.format(date);
						}
						if (dateString != null) {
							sb.append(RawDataImportRequest.COLLECTION_DATE_PARAM
									+ "="
									+ URLEncoder
											.encode(dateString, "UTF-8")
									+ "&");
						}
					}
					String value = null;
					boolean hasValue = false;

					if (cell.getRowIndex() > 0 && cell.getColumnIndex() > 1) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							value = cell.getStringCellValue().trim();
							if (value.contains("|")) {
								value = value.replaceAll("\\|", "^^");
							}
							sb.append(URLEncoder.encode(value, "UTF-8"));
							hasValue = true;
						} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							value = new Double(cell.getNumericCellValue())
									.toString().trim();
							hasValue = true;
						} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
							value = new Boolean(cell.getBooleanCellValue())
									.toString().trim();
							hasValue = true;
						}
					}
					if (hasValue) {
						if (valueCount > 0) {
							valueBuilder
									.append(RawDataImportRequest.FIELD_VAL_DELIMITER);
						}
						valueBuilder.append(value);
						valueCount++;
					}
				}
				if (valueCount > 0) {
					sb.append(RawDataImportRequest.FIXED_FIELD_VALUE_PARAM
							+ "=" + valueBuilder.toString());
					invokeUrl(serverBase, sb.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cleanup();
		}
	}
}
