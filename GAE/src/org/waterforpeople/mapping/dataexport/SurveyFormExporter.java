package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.waterforpeople.mapping.app.gwt.client.survey.OptionContainerDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import com.gallatinsystems.framework.dataexport.applet.DataExporter;

/**
 * Data exporter to write excel files containing the survey questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyFormExporter implements DataExporter {

	private static final int COL_WIDTH = 10000;
	private static final String BLANK = "_________________________";
	private static final String SMALL_BLANK = "______";
	private static final String QUESTION_HEADER = "Question";
	private static final String RESPONSE_HEADER = "Response";
	private static final String SURVEY_ID_KEY = "surveyId";
	private List<QuestionGroupDto> groupList;
	private String surveyTitle;
	private Map<QuestionGroupDto, List<QuestionDto>> questionMap;

	@Override
	public void export(Map<String, String> criteria, File fileName,
			String serverBase) {
		try {
			populateQuestionMap(criteria.get(SURVEY_ID_KEY), serverBase);
			writeSurvey(surveyTitle, fileName, groupList, questionMap);
		} catch (Exception e) {
			System.out.println("Could not write survey");
			e.printStackTrace();
		}
	}

	private void populateQuestionMap(String surveyId, String serverBase)
			throws Exception {
		groupList = BulkDataServiceClient.fetchQuestionGroups(serverBase,
				surveyId);
		questionMap = new HashMap<QuestionGroupDto, List<QuestionDto>>();
		if (groupList != null) {
			for (QuestionGroupDto group : groupList) {
				List<QuestionDto> questions = BulkDataServiceClient
						.fetchQuestions(serverBase, group.getKeyId());
				if (questions != null) {
					List<QuestionDto> fullQuestions = new ArrayList<QuestionDto>();
					for (QuestionDto q : questions) {
						QuestionDto fullQ = BulkDataServiceClient
								.loadQuestionDetails(serverBase, q.getKeyId());
						if (fullQ != null) {
							fullQuestions.add(fullQ);
						}
					}
					questionMap.put(group,fullQuestions);
				}
			}
		}
	}

	public static void main(String[] args) {
		SurveyFormExporter blah = new SurveyFormExporter();
		blah.groupList = new ArrayList<QuestionGroupDto>();
		QuestionGroupDto group = new QuestionGroupDto();
		group.setCode("GENERAL");
		blah.groupList.add(group);
		blah.questionMap = new HashMap<QuestionGroupDto, List<QuestionDto>>();
		List<QuestionDto> dtoList = new ArrayList<QuestionDto>();
		QuestionDto qDto = new QuestionDto();
		qDto.setText("This is a blank q");
		dtoList.add(qDto);
		blah.questionMap.put(group, dtoList);
		qDto = new QuestionDto();
		qDto.setText("This is opts");
		OptionContainerDto optCont = new OptionContainerDto();
		QuestionOptionDto opt = new QuestionOptionDto();
		opt.setText("Yes");
		optCont.addQuestionOption(opt);
		opt = new QuestionOptionDto();
		opt.setText("No");
		optCont.addQuestionOption(opt);
		opt = new QuestionOptionDto();
		opt.setText("Don't Know");
		optCont.addQuestionOption(opt);
		qDto.setOptionContainerDto(optCont);
		dtoList.add(qDto);
		try {
			blah.writeSurvey("test", new File("c:\\temp\\test.xls"),
					blah.groupList, blah.questionMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void writeSurvey(String title, File fileName,
			List<QuestionGroupDto> groupList,
			Map<QuestionGroupDto, List<QuestionDto>> questions)
			throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		sheet.setColumnWidth(0, COL_WIDTH);
		sheet.setColumnWidth(1, COL_WIDTH);
		HSSFCellStyle headerStyle = wb.createCellStyle();
		headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFCellStyle questionStyle = wb.createCellStyle();
		questionStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);

		int curRow = 0;
		HSSFRow row = sheet.createRow(curRow++);
		sheet
				.addMergedRegion(new CellRangeAddress(curRow - 1, curRow - 1,
						0, 1));
		HSSFCell cell = row.createCell(0);
		cell.setCellValue(title);
		cell.setCellStyle(headerStyle);

		row = sheet.createRow(curRow++);
		cell = row.createCell(0);
		cell.setCellValue(QUESTION_HEADER);
		cell.setCellStyle(headerStyle);
		cell = row.createCell(1);
		cell.setCellValue(RESPONSE_HEADER);
		cell.setCellStyle(headerStyle);
		if (questions != null) {
			for (int i = 0; i < groupList.size(); i++) {
				HSSFRow groupHeaderRow = sheet.createRow(curRow++);
				sheet.addMergedRegion(new CellRangeAddress(curRow - 1,
						curRow - 1, 0, 1));
				HSSFCell headerCell = groupHeaderRow.createCell(0);
				headerCell.setCellStyle(headerStyle);
				headerCell.setCellValue(groupList.get(i).getDisplayName());
				for (QuestionDto q : questions.get(groupList.get(i))) {
					int questionStartRow = curRow;
					HSSFRow tempRow = sheet.createRow(curRow++);
					HSSFCell tempCell = tempRow.createCell(0);
					tempCell.setCellStyle(questionStyle);
					tempCell.setCellValue(q.getText());
					if (q.getOptionContainerDto() != null
							&& q.getOptionContainerDto().getOptionsList() != null) {
						for (QuestionOptionDto opt : q.getOptionContainerDto()
								.getOptionsList()) {
							tempRow = sheet.createRow(curRow++);
							HSSFCell responseCell = tempRow.createCell(1);
							responseCell.setCellValue(opt.getText()
									+ SMALL_BLANK);
						}
						sheet.addMergedRegion(new CellRangeAddress(
								questionStartRow, curRow - 1, 0, 0));
					} else {
						tempCell = tempRow.createCell(1);
						tempCell.setCellValue(BLANK);
					}
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(fileName);
		wb.write(fileOut);
		fileOut.close();
	}
}
