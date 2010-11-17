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
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;

import com.gallatinsystems.framework.dataexport.applet.DataImporter;

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
		InputStream inp = null;

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
							break;
						case 12:
							sb.append("scoring=" + cell.getStringCellValue());
							break;
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
		InputStream inp = null;
		Sheet sheet1 = null;
		Map<Integer, String> errorMap = new TreeMap<Integer, String>();

		try {
			inp = new FileInputStream(file);
			HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(inp));
			sheet1 = wb.getSheetAt(0);
			for (Row row : sheet1) {
				StringBuffer rowError = new StringBuffer();
				if (row.getRowNum() >= 1) {
					String type = null;
					for (Cell cell : row) {
						try {
							switch (cell.getColumnIndex()) {
							case 0:
								if (cell.getStringCellValue().trim().length() == 0) {
									rowError
											.append("Survey Group Name is missing\n");
								}
								break;
							case 1:
								if (cell.getStringCellValue().trim().length() == 0) {
									rowError.append("Survey Name is missing\n");
								}
								break;
							case 2:
								try {
									if (cell.getNumericCellValue() < 0) {
										rowError
												.append("Question Group Order must be a positive integer\n");
									}
								} catch (Exception e) {
									rowError
											.append("Question group order must be a number\n");
								}
								break;
							case 3:
								if (cell.getStringCellValue().trim().length() == 0) {
									rowError
											.append("Question Group Name is missing\n");
								}
								break;
							case 4:
								try {
									if (cell.getNumericCellValue() < 0) {
										rowError
												.append("Question Id Order must be a positive integer\n");
									}
								} catch (Exception e) {
									rowError
											.append("Question Id order must be a number\n");
								}
								break;
							case 5:
								if (cell.getStringCellValue().trim().length() == 0) {
									rowError
											.append("Question Text is missing\n");
								}
								break;
							case 6:
								type = cell.getStringCellValue().trim();
								if (type.length() == 0) {
									rowError
											.append("Question Type is missing\n");
								} else {
									if (!(type
											.equals(QuestionDto.QuestionType.FREE_TEXT
													.toString())
											|| type
													.equals(QuestionDto.QuestionType.PHOTO
															.toString())
											|| type
													.equals(QuestionDto.QuestionType.VIDEO
															.toString())
											|| type
													.equals(QuestionDto.QuestionType.GEO
															.toString())
											|| type
													.equals(QuestionDto.QuestionType.SCAN
															.toString())
											|| type
													.equals(QuestionDto.QuestionType.TRACK
															.toString())
											|| type
													.equals(QuestionDto.QuestionType.NAME
															.toString())
											|| type
													.equals(QuestionDto.QuestionType.NUMBER
															.toString()) || type
											.equals(QuestionDto.QuestionType.OPTION
													.toString()))
											|| type
													.equals(QuestionDto.QuestionType.STRENGTH
															.toString())) {
										rowError
												.append("Invalid question type. Must be either: FREE_TEXT, PHOTO, VIDEO, GEO, NUMBER, OPTION, SCAN, TRACK, NAME, STRENGTH\n");
									}
								}
								break;
							case 7:
								if (QuestionType.OPTION.toString().equals(type)
										|| QuestionType.STRENGTH.toString()
												.equals(type)) {
									if (cell.getStringCellValue().trim()
											.length() == 0) {
										rowError
												.append("Options are missing\n");
									}
								}
								// TODO: validate language codes
								break;
							case 8:
								// TODO: validate dependency
								break;
							case 9:
								if (!validateBooleanField(cell)) {
									rowError
											.append("Allow Other must be either TRUE or FALSE\n");
								}
								break;
							case 10:
								if (!validateBooleanField(cell)) {
									rowError
											.append("Allow Multiple must be either TRUE or FALSE\n");
								}
								break;
							case 11:
								if (!validateBooleanField(cell)) {
									rowError
											.append("Manditory must be either TRUE or FALSE\n");
								}
								break;
							}
						} catch (Exception e) {
							rowError.append(e.toString());
						} finally {
							if (rowError.toString().trim().length() > 0) {
								errorMap.put(row.getRowNum() + 1, rowError
										.toString().trim());
							}
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
		return errorMap;
	}

	/**
	 * validates a boolean field. We have to try reading it as both a boolean
	 * and a string column because once we encounter 1 non-boolean, it changes
	 * the underlying model for the remainder of the spreadsheet.
	 * 
	 * @param cell
	 * @return
	 */
	private boolean validateBooleanField(Cell cell) {
		try {
			cell.getBooleanCellValue();
		} catch (Exception e) {
			try {
				if (cell.getStringCellValue().trim().length() > 0) {
					if (!("TRUE".equalsIgnoreCase(cell.getStringCellValue()
							.trim()) || "FALSE".equalsIgnoreCase(cell
							.getStringCellValue().trim()))) {
						return false;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return true;
	}

	@Override
	public void executeImport(String sourceBase, String serverBase) {
		// TODO Auto-generated method stub
		
	}

}
