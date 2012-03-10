package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.waterforpeople.mapping.app.web.dto.RawDataImportRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.common.util.StringUtil;
import com.gallatinsystems.framework.dataexport.applet.DataImporter;
import com.gallatinsystems.framework.dataexport.applet.ProgressDialog;

public class RawDataSpreadsheetImporter implements DataImporter {
	private static final String SERVLET_URL = "/rawdatarestapi";
	private static final String DEFAULT_LOCALE = "en";
	public static final String SURVEY_CONFIG_KEY = "surveyId";
	protected static final String KEY_PARAM = "k";
	private static final Map<String, String> SAVING_DATA;
	private static final Map<String, String> COMPLETE;
	private Long surveyId;
	private InputStream stream;
	private ProgressDialog progressDialog;
	private String locale = DEFAULT_LOCALE;
	private ThreadPoolExecutor threadPool;
	private BlockingQueue<Runnable> jobQueue;
	private List<String> errorIds;
	private volatile int currentStep;

	static {
		SAVING_DATA = new HashMap<String, String>();
		SAVING_DATA.put("en", "Saving Data");

		COMPLETE = new HashMap<String, String>();
		COMPLETE.put("en", "Complete");
	}

	/**
	 * opens a file input stream using the file passed in and tries to return
	 * the first worksheet in that file
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	protected Sheet getDataSheet(File file) throws Exception {
		stream = new FileInputStream(file);
		HSSFWorkbook wb = new HSSFWorkbook(new POIFSFileSystem(stream));
		return wb.getSheetAt(0);

	}

	/**
	 * closes open input streams
	 */
	protected void cleanup() {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	protected void setSurveyId(Map<String, String> criteria) {
		if (criteria != null && criteria.get(SURVEY_CONFIG_KEY) != null) {
			setSurveyId(new Long(criteria.get(SURVEY_CONFIG_KEY).trim()));
		}
	}

	@Override
	public void executeImport(File file, String serverBase,
			Map<String, String> criteria) {
		try {
			errorIds = new ArrayList<String>();
			jobQueue = new LinkedBlockingQueue<Runnable>();
			threadPool = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS,
					jobQueue);
			DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
			setSurveyId(criteria);

			Sheet sheet1 = getDataSheet(file);
			progressDialog = new ProgressDialog(sheet1.getLastRowNum(), locale);
			progressDialog.setVisible(true);
			HashMap<Integer, String> questionIDColMap = new HashMap<Integer, String>();
			Map<String, String> typeMap = new HashMap<String, String>();
			currentStep = 0;
			MessageDigest digest = MessageDigest.getInstance("MD5");
			for (Row row : sheet1) {
				digest.reset();
				String instanceId = null;
				String dateString = null;
				String submitter = null;
				StringBuilder sb = new StringBuilder();

				sb.append("action="
						+ RawDataImportRequest.SAVE_SURVEY_INSTANCE_ACTION
						+ "&" + RawDataImportRequest.SURVEY_ID_PARAM + "="
						+ getSurveyId() + "&");
				boolean needUpload = true;
				for (Cell cell : row) {
					String type = null;
					if (row.getRowNum() == 0 && cell.getColumnIndex() > 1) {
						// load questionIds
						String[] parts = cell.getStringCellValue().split("\\|");
						questionIDColMap.put(cell.getColumnIndex(), parts[0]);
						if (parts.length > 1) {
							if ("lat/lon".equalsIgnoreCase(parts[1].trim())
									|| "location".equalsIgnoreCase(parts[1]
											.trim())) {
								typeMap.put(parts[0], "GEO");
							}
						}
					}
					if (cell.getColumnIndex() == 0 && cell.getRowIndex() > 0) {
						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							instanceId = new Double(cell.getNumericCellValue())
									.intValue() + "";
						} else if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							instanceId = cell.getStringCellValue();

						}
						if (instanceId != null) {
							sb.append(RawDataImportRequest.SURVEY_INSTANCE_ID_PARAM
									+ "=" + instanceId + "&");
						}
					}
					if (cell.getColumnIndex() == 1 && cell.getRowIndex() > 0) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							dateString = cell.getStringCellValue();
						} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							Date date = HSSFDateUtil.getJavaDate(cell
									.getNumericCellValue());
							dateString = df.format(date);
						}
						if (dateString != null) {
							sb.append(RawDataImportRequest.COLLECTION_DATE_PARAM
									+ "="
									+ URLEncoder.encode(dateString, "UTF-8")
									+ "&");
						}
					}
					if (cell.getColumnIndex() == 2 && cell.getRowIndex() > 0) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
							submitter = cell.getStringCellValue();
							sb.append("submitter="
									+ URLEncoder.encode(submitter, "UTF-8")
									+ "&");
						}
					}
					String value = null;
					boolean hasValue = false;
					if (cell.getRowIndex() > 0
							&& cell.getColumnIndex() > 2
							&& questionIDColMap.get(cell.getColumnIndex()) != null) {
						String cellVal = parseCellAsString(cell);
						if (cellVal != null) {
							cellVal = cellVal.trim();
							// need to update digest before manipulating the
							// data
							digest.update(cellVal.getBytes());
							if (cellVal.contains("|")) {
								cellVal = cellVal.replaceAll("\\|", "^^");
							}
							if (cellVal.endsWith(".jpg")) {
								type = "PHOTO";
								cellVal = cellVal.substring(cellVal
										.lastIndexOf("/"));
								cellVal = "/sdcard" + value;
							}
						}
						if (cellVal != null && cellVal.trim().length() > 0) {
							hasValue = true;
							sb.append(
									"questionId="
											+ questionIDColMap.get(cell
													.getColumnIndex())
											+ "|value=").append(
									cellVal != null ? URLEncoder.encode(
											cellVal, "UTF-8") : "");

						}
						type = typeMap.get(questionIDColMap.get(cell
								.getColumnIndex()));

						if (type == null) {
							type = "VALUE";
						}
						if (hasValue) {
							sb.append("|type=").append(type).append("&");
						}
					} else if (cell.getRowIndex() > 0
							&& cell.getColumnIndex() > 2) {
						// we should only get here if we have a column that
						// isn't in the header
						// as long as the user hasn't messed up the sheet, this
						// is the md5 digest of the original data
						try {
							String md5 = parseCellAsString(cell);
							String digestVal = StringUtil.toHexString(digest
									.digest());
							if (md5 != null && md5.equals(digestVal)) {
								needUpload = false;
							} else if (md5 != null) {
								System.out.println("Row: " + row.getRowNum()
										+ " MD5: " + digestVal + " orig md5: "
										+ md5);
							}
						} catch (Exception e) {
							// if we can't handle the md5, then just assume we
							// need to update the row
							System.err.println("Couldn't process md5 for row: "
									+ row.getRowNum());
						}
					}
				}
				if (row.getRowNum() > 0 && needUpload) {
					sendDataToServer(serverBase, "action="
							+ RawDataImportRequest.RESET_SURVEY_INSTANCE_ACTION
							+ "&"
							+ RawDataImportRequest.SURVEY_INSTANCE_ID_PARAM
							+ "=" + (instanceId != null ? instanceId : "")
							+ "&" + RawDataImportRequest.SURVEY_ID_PARAM + "="
							+ getSurveyId() + "&"
							+ RawDataImportRequest.COLLECTION_DATE_PARAM + "="
							+ URLEncoder.encode(dateString, "UTF-8") + "&"
							+ RawDataImportRequest.SUBMITTER_PARAM + "="
							+ URLEncoder.encode(submitter, "UTF-8"),
							sb.toString(), criteria.get(KEY_PARAM));

				} else if (row.getRowNum() > 0) {
					// if we didn't need to upload, then just increment our
					// progress counter
					SwingUtilities.invokeLater(new StatusUpdater(currentStep++,
							SAVING_DATA.get(locale)));
				}
			}
			while (!jobQueue.isEmpty()) {
				Thread.sleep(5000);
			}
			if (errorIds.size() > 0) {
				System.out.println("There were ERRORS: ");
				for (String line : errorIds) {
					System.out.println(line);
				}
			}
			System.out.println("Updating summaries");
			// now update the summaries
			invokeUrl(serverBase, "action="
					+ RawDataImportRequest.UPDATE_SUMMARIES_ACTION + "&"
					+ RawDataImportRequest.SURVEY_ID_PARAM + "=" + surveyId,
					true, criteria.get(KEY_PARAM));

			SwingUtilities.invokeLater(new StatusUpdater(currentStep++,
					COMPLETE.get(locale), true));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cleanup();
		}
	}

	/**
	 * handles calling invokeURL twice (once to reset the instance and again to
	 * save the new one) as a separate job submitted to the thread pool
	 * 
	 * @param serverBase
	 * @param resetUrlString
	 * @param saveUrlString
	 */
	private void sendDataToServer(final String serverBase,
			final String resetUrlString, final String saveUrlString,
			final String key) {
		threadPool.execute(new Runnable() {

			public void run() {
				try {
					SwingUtilities.invokeLater(new StatusUpdater(currentStep++,
							SAVING_DATA.get(locale)));
					invokeUrl(serverBase, resetUrlString, true, key);
					invokeUrl(serverBase, saveUrlString, true, key);
				} catch (Exception e) {
					errorIds.add(saveUrlString);
					System.err.println("Could not invoke rest services: " + e);
					e.printStackTrace(System.err);
				}
			}
		});
	}

	/**
	 * calls a remote api by posting to the url passed in.
	 * 
	 * @param serverBase
	 * @param urlString
	 * @throws Exception
	 */
	protected void invokeUrl(String serverBase, String urlString,
			boolean shouldSign, String key) throws Exception {

		BulkDataServiceClient.fetchDataFromServer(serverBase + SERVLET_URL,
				urlString, shouldSign, key);
	}

	@Override
	public Map<Integer, String> validate(File file) {
		Map<Integer, String> errorMap = new HashMap<Integer, String>();
		return errorMap;
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
		Map<String, String> configMap = new HashMap<String, String>();
		configMap.put(SURVEY_CONFIG_KEY, args[2].trim());
		r.executeImport(file, serverBaseArg, configMap);
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}

	private String parseCellAsString(Cell cell) {
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
	 * Private class to handle updating of the UI thread from our worker thread
	 */
	private class StatusUpdater implements Runnable {

		private int step;
		private String msg;
		private boolean isComplete;

		public StatusUpdater(int step, String message) {
			this(step, message, false);
		}

		public StatusUpdater(int step, String message, boolean isComplete) {
			msg = message;
			this.step = step;
			this.isComplete = isComplete;
		}

		public void run() {
			progressDialog.update(step, msg, isComplete);
		}
	}
}
