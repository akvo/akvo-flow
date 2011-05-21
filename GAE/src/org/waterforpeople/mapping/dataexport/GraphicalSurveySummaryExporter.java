package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.surveyinstance.SurveyInstanceDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.common.util.JFreechartChartUtil;

/**
 * Enhancement of the SurveySummaryExporter to support writing to Excel and
 * including chart images.
 * 
 * @author Christopher Fagiani
 * 
 */
public class GraphicalSurveySummaryExporter extends SurveySummaryExporter {
	private static final String IMAGE_PREFIX = "http://waterforpeople.s3.amazonaws.com/images/";
	private static final String SDCARD_PREFIX = "/sdcard/";

	private static final String REPORT_HEADER = "Survey Summary Report";
	private static final String FREQ_LABEL = "Frequency";
	private static final String PCT_LABEL = "Percent";
	private static final String SUMMARY_LABEL = "Summary";
	private static final String RAW_DATA_LABEL = "Raw Data";
	private static final String INSTANCE_LABEL = "Instance";
	private static final String SUB_DATE_LABEL = "Submission Date";
	private static final String SUBMITTER_LABEL = "Submitter";
	private static final int CHART_WIDTH = 600;
	private static final int CHART_HEIGHT = 400;
	private static final int CHART_CELL_WIDTH = 10;
	private static final int CHART_CELL_HEIGHT = 22;

	@Override
	public void export(Map<String, String> criteria, File fileName,
			String serverBase) {
		InputDialog dia = new InputDialog();
		PrintWriter pw = null;
		try {
			Map<QuestionGroupDto, List<QuestionDto>> questionMap = loadAllQuestions(
					criteria.get(SurveyRestRequest.SURVEY_ID_PARAM), serverBase);
			if (questionMap.size() > 0) {
				HSSFWorkbook wb = new HSSFWorkbook();
				SummaryModel model = fetchAndWriteRawData(
						criteria.get(SurveyRestRequest.SURVEY_ID_PARAM),
						serverBase, questionMap, wb);
				writeSummaryReport(questionMap, model, dia.getDoRollup(), wb);
				FileOutputStream fileOut = new FileOutputStream(fileName);
				wb.setActiveSheet(1);
				wb.write(fileOut);
				fileOut.close();
			} else {
				System.out.println("No questions for survey");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected SummaryModel fetchAndWriteRawData(String surveyId,
			String serverBase,
			Map<QuestionGroupDto, List<QuestionDto>> questionMap,
			HSSFWorkbook wb) throws Exception {
		SummaryModel model = new SummaryModel();

		HSSFSheet sheet = wb.createSheet(RAW_DATA_LABEL);
		Object[] results = createRawDataHeader(wb, sheet, questionMap);
		List<String> questionIdList = (List<String>) results[0];
		List<String> unsummarizable = (List<String>) results[1];
		int curRow = 1;

		Map<String, String> instanceMap = BulkDataServiceClient
				.fetchInstanceIds(surveyId, serverBase);

		for (Entry<String, String> instanceEntry : instanceMap.entrySet()) {
			String instanceId = instanceEntry.getKey();
			String dateString = instanceEntry.getValue();
			HSSFRow row = getRow(curRow++, sheet);
			Map<String, String> responseMap = BulkDataServiceClient
					.fetchQuestionResponses(instanceId, serverBase);
			int col = 0;

			if (responseMap != null && responseMap.size() > 0) {
				createCell(row, col++, instanceId, null);
				createCell(row, col++, dateString, null);
				SurveyInstanceDto dto = BulkDataServiceClient
						.findSurveyInstance(Long.parseLong(instanceId.trim()),
								serverBase);
				if (dto != null) {
					String name = dto.getSubmitterName();
					if (name != null) {
						createCell(row, col++, dto.getSubmitterName()
								.replaceAll("\n", " ").trim(), null);
					} else {
						createCell(row, col++, " ", null);
					}
				}
				for (String q : questionIdList) {
					String val = responseMap.get(q);
					if (val != null) {
						if (val.contains(SDCARD_PREFIX)) {
							val = IMAGE_PREFIX
									+ val.substring(val.indexOf(SDCARD_PREFIX)
											+ SDCARD_PREFIX.length());
						}
						createCell(row, col++,
								val.replaceAll("\n", " ").trim(), null);
					} else {
						createCell(row, col++, "", null);
					}
				}

				String sector = "";
				if (sectorQuestion != null) {
					sector = responseMap.get(sectorQuestion.getKeyId()
							.toString());
				}
				for (Entry<String, String> entry : responseMap.entrySet()) {
					if (!unsummarizable.contains(entry.getKey())) {
						model.tallyResponse(entry.getKey(), sector,
								entry.getValue());
					}
				}
			}
		}
		return model;
	}

	/**
	 * creates the header for the raw data tab
	 * 
	 * @param row
	 * @param questionMap
	 * @return - returns a 2 element array. The first element is a List of
	 *         String objects representing all the question Ids. The second
	 *         element is a List of Strings representing all the non-sumarizable
	 *         question Ids (i.e. those that aren't OPTION or NUMBER questions)
	 */
	private Object[] createRawDataHeader(HSSFWorkbook wb, HSSFSheet sheet,
			Map<QuestionGroupDto, List<QuestionDto>> questionMap) {
		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont headerFont = wb.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerStyle.setFont(headerFont);
		HSSFRow row = getRow(0, sheet);
		createCell(row, 0, INSTANCE_LABEL, headerStyle);
		createCell(row, 1, SUB_DATE_LABEL, headerStyle);
		createCell(row, 2, SUBMITTER_LABEL, headerStyle);
		List<String> questionIdList = new ArrayList<String>();
		List<String> nonSummarizableList = new ArrayList<String>();
		if (questionMap != null) {
			int offset = 3;
			for (Entry<QuestionGroupDto, List<QuestionDto>> entry : questionMap
					.entrySet()) {
				if (entry.getValue() != null) {
					for (QuestionDto q : entry.getValue()) {
						questionIdList.add(q.getKeyId().toString());
						createCell(row, offset++, q.getKeyId().toString() + "|"
								+ q.getText().replaceAll("\n", "").trim(),
								headerStyle);
						if (!(QuestionType.NUMBER == q.getType() || QuestionType.OPTION == q
								.getType())) {
							nonSummarizableList.add(q.getKeyId().toString());
						}
					}
				}
			}
		}
		Object[] temp = new Object[2];
		temp[0] = questionIdList;
		temp[1] = nonSummarizableList;
		return temp;
	}

	/**
	 * 
	 * Writes the report as an XLS document
	 */
	private void writeSummaryReport(
			Map<QuestionGroupDto, List<QuestionDto>> questionMap,
			SummaryModel summaryModel, boolean rollUp, HSSFWorkbook wb)
			throws Exception {

		HSSFSheet sheet = wb.createSheet(SUMMARY_LABEL);
		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont headerFont = wb.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerStyle.setFont(headerFont);

		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

		int curRow = 0;
		HSSFRow row = getRow(curRow++, sheet);
		createCell(row, 0, REPORT_HEADER, headerStyle);
		for (Entry<QuestionGroupDto, List<QuestionDto>> mapEntry : questionMap
				.entrySet()) {
			if (mapEntry.getValue() != null) {
				for (QuestionDto question : mapEntry.getValue()) {
					if(!(QuestionType.OPTION == question.getType() || QuestionType.NUMBER == question.getType())){
						continue;
					}
					// for both options and numeric, we want a pie chart and
					// data table for numeric, we also want descriptive
					// statistics
					int tableTopRow = curRow++;
					int tableBottomRow = curRow;
					row = getRow(tableTopRow, sheet);
					// span the question heading over the data table
					sheet.addMergedRegion(new CellRangeAddress(curRow - 1,
							curRow - 1, 0, 2));
					createCell(row, 0, question.getText(), headerStyle);
					DescriptiveStats stats = summaryModel
							.getDescriptiveStatsForQuestion(question.getKeyId());
					if (stats != null && stats.getSampleCount() > 0) {
						sheet.addMergedRegion(new CellRangeAddress(curRow - 1,
								curRow - 1, 4, 5));
						createCell(row, 4, question.getText(), headerStyle);
						createCell(row, 5, question.getText(), headerStyle);
					}
					row = getRow(curRow++, sheet);
					createCell(row, 1, FREQ_LABEL, headerStyle);
					createCell(row, 2, PCT_LABEL, headerStyle);

					// now create the data table for the option count
					Map<String, Long> counts = summaryModel
							.getResponseCountsForQuestion(question.getKeyId());
					int sampleTotal = 0;
					List<String> labels = new ArrayList<String>();
					List<String> values = new ArrayList<String>();
					for (Entry<String, Long> count : counts.entrySet()) {
						row = getRow(curRow++, sheet);
						createCell(row, 0, count.getKey(), null);
						createCell(row, 1, count.getValue().toString(), null);
						labels.add(count.getKey());
						values.add(count.getValue().toString());
						sampleTotal += count.getValue();
					}
					row = getRow(curRow++, sheet);
					createCell(row, 0, "Total", null);
					createCell(row, 1, sampleTotal + "", null);

					tableBottomRow = curRow;

					if (stats != null && stats.getSampleCount() > 0) {
						int tempRow = tableTopRow;
						row = getRow(tempRow++, sheet);
						createCell(row, 4, "N", null);
						createCell(row, 5, sampleTotal + "", null);
						row = getRow(tempRow++, sheet);
						createCell(row, 4, "Mean", null);
						createCell(row, 5, stats.getMean() + "", null);
						row = getRow(tempRow++, sheet);
						createCell(row, 4, "Std Err", null);
						createCell(row, 5, stats.getStandardError() + "", null);
						row = getRow(tempRow++, sheet);
						createCell(row, 4, "Median", null);
						createCell(row, 5, stats.getMedian() + "", null);
						row = getRow(tempRow++, sheet);
						createCell(row, 4, "Mode", null);
						createCell(row, 5, stats.getMode() + "", null);
						row = getRow(tempRow++, sheet);
						createCell(row, 4, "Std Deviation", null);
						createCell(row, 5, stats.getStandardDeviation() + "",
								null);
						row = getRow(tempRow++, sheet);
						createCell(row, 4, "Variance", null);
						createCell(row, 5, stats.getVariance() + "", null);
						row = getRow(tempRow++, sheet);
						createCell(row, 4, "Range", null);
						createCell(row, 5, stats.getRange() + "", null);
						row = getRow(tempRow++, sheet);
						createCell(row, 4, "Min", null);
						createCell(row, 5, stats.getMin() + "", null);
						row = getRow(tempRow++, sheet);
						createCell(row, 4, "Max", null);
						createCell(row, 5, stats.getMax() + "", null);
						if (tableBottomRow < tempRow) {
							tableBottomRow = tempRow;
						}
					}
					curRow = tableBottomRow;
					if (labels.size() > 0) {
						// now insert the graph
						// int indx =
						// wb.addPicture(ImageChartUtil.getPieChart(labels,values,"",
						// CHART_WIDTH,
						// CHART_HEIGHT),HSSFWorkbook.PICTURE_TYPE_PNG);
						int indx = wb.addPicture(JFreechartChartUtil
								.getPieChart(labels, values, "", CHART_WIDTH,
										CHART_HEIGHT),
								HSSFWorkbook.PICTURE_TYPE_PNG);
						HSSFClientAnchor anchor;
						anchor = new HSSFClientAnchor(0, 0, 0, 255, (short) 6,
								tableTopRow, (short) (6 + CHART_CELL_WIDTH),
								tableTopRow + CHART_CELL_HEIGHT);
						anchor.setAnchorType(2);
						patriarch.createPicture(anchor, indx);
						if (tableTopRow + CHART_CELL_HEIGHT > tableBottomRow) {
							curRow = tableTopRow + CHART_CELL_HEIGHT;
						}
					}

					// add a blank row between questions
					getRow(curRow++, sheet);

				}
			}
		}
	}

	/**
	 * creates a cell in the row passed in and sets the style and value (if
	 * non-null)
	 * 
	 */
	private HSSFCell createCell(HSSFRow row, int col, String value,
			HSSFCellStyle style) {
		HSSFCell cell = row.createCell(col);
		if (style != null) {
			cell.setCellStyle(style);
		}
		if (value != null) {
			cell.setCellValue(value);
		}

		return cell;
	}

	/**
	 * finds or creates the row at the given index
	 * 
	 * @param index
	 * @param rowLocalMax
	 * @param sheet
	 * @return
	 */
	private HSSFRow getRow(int index, HSSFSheet sheet) {
		HSSFRow row = null;
		if (index < sheet.getLastRowNum()) {
			row = sheet.getRow(index);
		} else {
			row = sheet.createRow(index);
		}
		return row;

	}
}
