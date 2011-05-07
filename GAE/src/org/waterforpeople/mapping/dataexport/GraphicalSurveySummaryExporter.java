package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.web.dto.SurveyRestRequest;

/**
 * Enhancement of the SurveySummaryExporter to support writing to Excel and
 * including chart images.
 * 
 * @author Christopher Fagiani
 * 
 */
public class GraphicalSurveySummaryExporter extends SurveySummaryExporter {
	private static final int COL_WIDTH = 500;
	private static final String REPORT_HEADER = "Survey Summary Report";
	private static final String FREQ_LABEL = "Frequency";
	private static final String PCT_LABEL = "Percent";

	@Override
	public void export(Map<String, String> criteria, File fileName,
			String serverBase) {
		InputDialog dia = new InputDialog();
		PrintWriter pw = null;
		try {
			Map<QuestionGroupDto, List<QuestionDto>> questionMap = loadAllQuestions(
					criteria.get(SurveyRestRequest.SURVEY_ID_PARAM), serverBase);
			if (questionMap.size() > 0) {
				SummaryModel model = buildDataModel(
						criteria.get(SurveyRestRequest.SURVEY_ID_PARAM),
						serverBase);
				writeReport(questionMap, model, dia.getDoRollup(), fileName);
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

	/**
	 * 
	 * Writes the report as an XLS document
	 */
	private void writeReport(
			Map<QuestionGroupDto, List<QuestionDto>> questionMap,
			SummaryModel summaryModel, boolean rollUp, File fileName)
			throws Exception {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		sheet.setColumnWidth(0, COL_WIDTH);
		sheet.setColumnWidth(1, COL_WIDTH);
		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont headerFont = wb.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerStyle.setFont(headerFont);
		int curRow = 0;
		HSSFRow row = getRow(curRow++, sheet);
		createCell(row, 0, REPORT_HEADER, headerStyle);
		for (Entry<QuestionGroupDto, List<QuestionDto>> mapEntry : questionMap
				.entrySet()) {
			if (mapEntry.getValue() != null) {
				for (QuestionDto question : mapEntry.getValue()) {
					// TODO: remove println
					System.out.println("Question type: " + question.getType());
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
					if (stats != null && stats.getSampleCount()>0) {
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
					for (Entry<String, Long> count : counts.entrySet()) {
						row = getRow(curRow++, sheet);
						createCell(row, 0, count.getKey(), null);
						createCell(row, 1, count.getValue().toString(), null);
						sampleTotal += count.getValue();
					}
					row = getRow(curRow++, sheet);
					createCell(row, 0, "Total", null);
					createCell(row, 1, sampleTotal + "", null);

					tableBottomRow = curRow;

					if (stats != null&& stats.getSampleCount()>0) {
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
					// add a blank row between questions
					getRow(curRow++, sheet);

				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(fileName);
		wb.write(fileOut);
		fileOut.close();
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
