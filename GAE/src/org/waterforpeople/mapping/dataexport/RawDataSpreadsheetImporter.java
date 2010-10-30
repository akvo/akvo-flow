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
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;

import com.gallatinsystems.framework.dataexport.applet.DataImporter;

public class RawDataSpreadsheetImporter implements DataImporter {
	private static final String SERVLET_URL = "/rawdatarestapi";
	private Long surveyId;

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
			Map<String, String> typeMap = new HashMap<String, String>();
			for (Row row : sheet1) {
				String instanceId = null;
				String dateString = null;
				StringBuilder sb = new StringBuilder();
				sb.append("?action="
						+ RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION
						+ "&" + RawDataImportRequest.SURVEY_ID_PARAM + "="
						+ surveyId + "&");				
				for (Cell cell : row) {
					String type = null;
					if (row.getRowNum() == 0 && cell.getColumnIndex() > 1) {
						// load questionIds
						String[] parts = cell.getStringCellValue().split("\\|");
						questionIDColMap.put(cell.getColumnIndex(), parts[0]);
						if (parts.length > 1) {
							if ("lat/lon".equalsIgnoreCase(parts[1].trim())) {
								typeMap.put(parts[0], "GEO");
							}
						}
					}
					if (cell.getColumnIndex() == 0 && cell.getRowIndex() > 0) {
						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							instanceId = new Double(cell.getNumericCellValue())
									.intValue()
									+ "";
							sb.append(RawDataImportRequest.SURVEY_INSTANCE_ID_PARAM+"="
									+ URLEncoder.encode(instanceId, "UTF-8")
									+ "&");
						}
					}
					if (cell.getColumnIndex() == 1 && cell.getRowIndex() > 0) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							dateString = cell.getStringCellValue();
						}
					}
					String value = null;
					boolean hasValue = false;
					if (cell.getRowIndex() > 0 && cell.getColumnIndex() > 1) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							sb.append("questionId="
									+ questionIDColMap.get(cell
											.getColumnIndex()) + "|value=");

							value = cell.getStringCellValue().trim();
							if (value.endsWith(".jpg")) {
								type = "PHOTO";
								value = value.substring(value.lastIndexOf("/"));
								value = "/sdcard" + value;
							}
							sb.append(URLEncoder.encode(value, "UTF-8"));
							hasValue = true;

						} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							sb.append("questionId="
									+ questionIDColMap.get(cell
											.getColumnIndex())
									+ "|value="
									+ URLEncoder.encode(new Double(cell
											.getNumericCellValue()).toString()
											.trim(), "UTF-8"));
							hasValue = true;
						} else if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
							sb.append("questionId="
									+ questionIDColMap.get(cell
											.getColumnIndex())
									+ "|value="
									+ URLEncoder.encode(new Boolean(cell
											.getBooleanCellValue()).toString()
											.trim(), "UTF-8"));
							hasValue = true;
						}
						if (type == null && value != null) {
							type = typeMap.get(questionIDColMap.get(cell
									.getColumnIndex()));
						}
						if (type == null) {
							type = "VALUE";
						}
						if(hasValue){
							sb.append("|type=").append(type).append("&");
						}
					}

				}
				if (row.getRowNum() > 0) {
					if (instanceId != null) {
						invokeUrl(
								serverBase,
								"?action="
										+ RawDataImportRequest.RESET_SURVEY_INSTANCE_ACTION
										+ "&"
										+ RawDataImportRequest.SURVEY_INSTANCE_ID_PARAM
										+ "="
										+ instanceId
										+ "&"
										+ RawDataImportRequest.SURVEY_ID_PARAM
										+ "="
										+ surveyId
										+ "&"
										+ RawDataImportRequest.COLLECTION_DATE_PARAM
										+ "="
										+ URLEncoder
												.encode(dateString, "UTF-8"));
						System.out.print(i++ + " : ");
						invokeUrl(serverBase, sb.toString());
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

	private void invokeUrl(String serverBase, String urlString)
			throws Exception {
		URL url = new URL(serverBase + SERVLET_URL + urlString);
		System.out.println(serverBase + SERVLET_URL + urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn
				.getInputStream()));
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
	}

	@Override
	public Map<Integer, String> validate(File file) {
		// TODO implement validation
		return null;
	}

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out
					.println("Error.\nUsage:\n\tjava org.waterforpeople.mapping.dataexport.RawDataSpreadsheetImporter <file> <serverBase> <surveyId>");
			System.exit(1);
		}
		File file = new File(args[0].trim());
		String serverBaseArg = args[1].trim();
		RawDataSpreadsheetImporter r = new RawDataSpreadsheetImporter();
		r.surveyId = Long.parseLong(args[2].trim());
		r.executeImport(file, serverBaseArg);
	}
}
