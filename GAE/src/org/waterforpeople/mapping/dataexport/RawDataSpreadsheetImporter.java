package org.waterforpeople.mapping.dataexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class RawDataSpreadsheetImporter implements DataImporter {
	private static final String SERVLET_URL = "/rawdatarestapi";

	@Override
	public void executeImport(File file, String serverBase) {
		InputStream inp = null;

		Sheet sheet1 = null;

		try {
			inp = new FileInputStream(file);
			HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
			int i = 0;
			sheet1 = wb.getSheetAt(0);
			HashMap<Integer, String> questionIDColMap = new HashMap<Integer, String>();
			for (Row row : sheet1) {

				StringBuilder sb = new StringBuilder();
				sb.append("?action=saveSurveyInstance&");
				for (Cell cell : row) {
					if (row.getRowNum() == 0 && cell.getColumnIndex() > 1) {
						// load questionIds
						String[] parts = cell.getStringCellValue().split("\\|");
						questionIDColMap.put(cell.getColumnIndex(), parts[0]);
					}
					if (cell.getColumnIndex() == 0 && cell.getRowIndex() > 0) {
						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
							sb.append("surveyInstance="
									+ URLEncoder.encode(new Double(cell
											.getNumericCellValue()).toString(),
											"UTF-8") + "&");
					}
					if (cell.getRowIndex() > 0 && cell.getColumnIndex() > 1) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING)
							sb.append("questionId="
									+ questionIDColMap.get(cell
											.getColumnIndex())
									+ "|value="
									+ URLEncoder.encode(cell
											.getStringCellValue().trim(),
											"UTF-8") + "&");
						else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
							sb.append("questionId="
									+ questionIDColMap.get(cell
											.getColumnIndex())
									+ "|value="
									+ URLEncoder.encode(new Double(cell
											.getNumericCellValue()).toString()
											.trim(), "UTF-8") + "&");
						else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN)
							sb.append("questionId="
									+ questionIDColMap.get(cell
											.getColumnIndex())
									+ "|value="
									+ URLEncoder.encode(new Boolean(cell
											.getBooleanCellValue()).toString()
											.trim(), "UTF-8") + "&");
						else if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
							sb.append("questionId="
									+ questionIDColMap.get(cell
											.getColumnIndex()) + "|value=&");
					}

				}
				// For debugging
				if (row.getRowNum() > 0) {
					System.out.println(sb.toString());
					if (callUrlFlag) {
						URL url = new URL(serverBase + SERVLET_URL
								+ sb.toString());
						System.out.println(i++ + " : " + serverBase
								+ SERVLET_URL + sb.toString());
						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setRequestMethod("GET");
						conn.setDoOutput(true);
						String line;
						BufferedReader reader = new BufferedReader(
								new InputStreamReader(conn.getInputStream()));
						while ((line = reader.readLine()) != null) {
							System.out.println(line);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inp != null) {
				try {
					inp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public Map<Integer, String> validate(File file) {
		// TODO implement validation
		return null;
	}

	private Boolean callUrlFlag = true;

	public static void main(String[] args) {
		File file = new File(args[0].trim());
		String serverBaseArg = args[1].trim();
		RawDataSpreadsheetImporter r = new RawDataSpreadsheetImporter();
		r.callUrlFlag = false;
		r.executeImport(file, serverBaseArg);
	}
}
