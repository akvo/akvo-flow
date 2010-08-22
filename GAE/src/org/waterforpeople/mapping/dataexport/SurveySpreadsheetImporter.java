package org.waterforpeople.mapping.dataexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * this data importer will read a local excel spreadsheet file using the POI
 * library and will then
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveySpreadsheetImporter implements DataImporter {

	private static final String SERVLET_URL = "/surveyrestapi";

	@Override
	public void executeImport(File file, String serverBase) {
		InputStream inp;

		Sheet sheet1 = null;

		try {
			inp = new FileInputStream(file);
			HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
			int i = 0;
			sheet1 = wb.getSheetAt(0);
			for (Row row : sheet1) {
				if (row.getRowNum() >= 1) {
					StringBuilder sb = new StringBuilder();
					sb.append("?action=saveQuestion&");
					for (Cell cell : row) {
						switch (cell.getColumnIndex()) {
						case 0:
							sb.append("surveyGroupName="
									+ URLEncoder.encode(cell
											.getStringCellValue().trim(),
											"UTF-8") + "&");
							break;
						case 1:
							sb.append("surveyName="
									+ URLEncoder.encode(cell
											.getStringCellValue().trim(),
											"UTF-8") + "&");
							break;
						case 2:
							sb.append("questionGroupOrder="
									+ new Double(cell.getNumericCellValue())
											.intValue() + "&");
							break;

						case 3:
							sb.append("questionGroupName="
									+ URLEncoder.encode(cell
											.getStringCellValue().trim(),
											"UTF-8") + "&");
							break;

						case 4:
							sb.append("questionID="
									+ new Double(cell.getNumericCellValue())
											.intValue() + "&");
							break;
						case 5:
							sb.append("questionText="
									+ URLEncoder.encode(cell
											.getStringCellValue().trim(),
											"UTF-8") + "&");
							break;
						case 6:
							sb.append("questionType="
									+ URLEncoder.encode(cell
											.getStringCellValue().trim(),
											"UTF-8") + "&");
							break;
						case 7:
							sb.append("options="
									+ URLEncoder.encode(cell
											.getStringCellValue().trim(),
											"UTF-8") + "&");
							break;
						case 8:
							sb.append("dependQuestion="
									+ URLEncoder.encode(cell
											.getStringCellValue().trim(),
											"UTF-8") + "&");
							break;
						case 9:
							sb.append("allowOther="
									+ cell.getBooleanCellValue() + "&");
							break;
						case 10:
							sb.append("allowMultiple="
									+ cell.getBooleanCellValue() + "&");
							break;
						case 11:
							sb.append("mandatory=" + cell.getBooleanCellValue()
									+ "&");
						}
					}

					URL url = new URL(serverBase + SERVLET_URL + sb.toString());
					System.out.println(i++ + " : " + serverBase + SERVLET_URL
							+ sb.toString());
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
					reader.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean validate(File file) {
		// TODO Auto-generated method stub
		return true;
	}

}
