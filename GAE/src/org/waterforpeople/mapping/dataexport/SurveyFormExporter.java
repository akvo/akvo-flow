package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionOptionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.TranslationDto;
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
	private static final String LANG_DELIM = " / ";
	private static final String DEP_HEAD = "Only answer if you responded ";
	private static final String DEP_HEAD_TO = " to ";
	private static final String BLANK = "_________________________";
	private static final String SMALL_BLANK = "______";
	private static final String QUESTION_HEADER = "Question";
	private static final String RESPONSE_HEADER = "Response";
	private static final String SURVEY_ID_KEY = "surveyId";
	private Map<Long,Long> idToNumberMap;
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

/**
* Calls the server to fetch all question groups for the survey and then will iteratee over all the questions for the group and call loadQuestionDetails for each one to
* get the fully hydrated object. This will populate a number of member variables to store the results.
*
*/
	private void populateQuestionMap(String surveyId, String serverBase)
			throws Exception {
		groupList = BulkDataServiceClient.fetchQuestionGroups(serverBase,
				surveyId);
		questionMap = new HashMap<QuestionGroupDto, List<QuestionDto>>();
		idToNumberMap = new HashMap<Long,Long>();
		if (groupList != null) {
			Long count = new Long(1);
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
							idToNumberMap.put(fullQ.getKeyId(),count++);
						}
					}
					questionMap.put(group,fullQuestions);
				}
			}
		}
	}


/**
*
* Writes the survey as an XLS document
*/
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
		HSSFFont headerFont = wb.createFont();
		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerStyle.setFont(headerFont);
	
		HSSFCellStyle questionStyle = wb.createCellStyle();
		questionStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		questionStyle.setWrapText(true);
		
		HSSFCellStyle depStyle = wb.createCellStyle();
		depStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont depFont = wb.createFont();
		depFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		depFont.setItalic(true);
		depStyle.setFont(depFont);

		int curRow = 0;
		HSSFRow row = sheet.createRow(curRow++);
		sheet
				.addMergedRegion(new CellRangeAddress(curRow - 1, curRow - 1,
						0, 1));
		createCell(row,0,title,headerStyle);
		row = sheet.createRow(curRow++);
		createCell(row,0,QUESTION_HEADER,headerStyle);
		createCell(row,1,RESPONSE_HEADER,headerStyle);

		Long count = new Long(1);
		if (questions != null) {
			for (int i = 0; i < groupList.size(); i++) {
				HSSFRow groupHeaderRow = sheet.createRow(curRow++);
				sheet.addMergedRegion(new CellRangeAddress(curRow - 1,
						curRow - 1, 0, 1));
				createCell(groupHeaderRow,0,groupList.get(i).getDisplayName(),headerStyle);

				for (QuestionDto q : questions.get(groupList.get(i))) {
					int questionStartRow = curRow;
					HSSFRow tempRow = sheet.createRow(curRow++);
					if(q.getQuestionDependency() != null){
						//if there is a dependency, add a row about not answering unless the dependency is satisfied
						sheet.addMergedRegion(new CellRangeAddress(curRow-1,curRow-1,0,1));
						Long qNum = idToNumberMap.get(q.getQuestionDependency().getQuestionId());
						createCell(tempRow,0,DEP_HEAD+q.getQuestionDependency().getAnswerValue()+DEP_HEAD_TO+"Q"+qNum,depStyle);
						tempRow = sheet.createRow(curRow++);
						questionStartRow = curRow;
					}
					createCell(tempRow,0,(count++)+". "+formText(q.getText(),q.getTranslationMap()),questionStyle);
					if (q.getOptionContainerDto() != null
							&& q.getOptionContainerDto().getOptionsList() != null) {
						for (QuestionOptionDto opt : q.getOptionContainerDto()
								.getOptionsList()) {
							tempRow = sheet.createRow(curRow++);
							createCell(tempRow,1,formText(opt.getText(),opt.getTranslationMap())+ SMALL_BLANK,null);
						}
						sheet.addMergedRegion(new CellRangeAddress(
								questionStartRow, curRow - 1, 0, 0));
					} else {
						createCell(tempRow,1,BLANK,null);
					}
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(fileName);
		wb.write(fileOut);
		fileOut.close();
	}

	/**
	* creates a cell in the row passed in and sets the style and value (if non-null)
	*
	*/
	private HSSFCell createCell(HSSFRow row, int col, String value, HSSFCellStyle style){
		HSSFCell cell = row.createCell(col);
		if(style != null){
			cell.setCellStyle(style);
		}
		if(value != null){
			cell.setCellValue(value);
		}
		return cell;
	}


/**
* forms a string that has all languages in the translation map delimited by the LANG_DELIM
*
*/
	private String formText(String text, TreeMap<String,TranslationDto> translationMap){
		StringBuilder buff = new StringBuilder();
		int count = 0;
		if(translationMap != null){
			for(Entry<String,TranslationDto> trans: translationMap.entrySet()){
				if(count > 0){
					buff.append(LANG_DELIM);
				}
				buff.append(trans.getValue());
				count++;
			}
		}else{
			buff.append(text);
		}
		return buff.toString();
	}
}
