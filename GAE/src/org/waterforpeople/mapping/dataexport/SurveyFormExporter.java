package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;

import com.gallatinsystems.framework.dataexport.applet.DataExporter;

/**
 * Data exporter to write excel files containing the survey questions
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyFormExporter implements DataExporter {

	private static final String SURVEY_ID_KEY = "surveyId";
	private List<QuestionGroupDto> groupList;
	private Map<QuestionGroupDto, List<QuestionDto>> questionMap;
	
	@Override
	public void export(Map<String, String> criteria, File fileName,
			String serverBase) {
		try{
		populateQuestionMap(criteria.get(SURVEY_ID_KEY),serverBase);
		writeSurvey(fileName,groupList,questionMap);
		}catch(Exception e){
			System.out.println("Could not write survey");
			e.printStackTrace();
		}
	}
	
	private void populateQuestionMap(String surveyId, String serverBase){
		//TODO: call server
		
	}
	
	private void writeSurvey(File fileName, List<QuestionGroupDto> groupList,Map<QuestionGroupDto, List<QuestionDto>> questions) throws Exception{
		HSSFWorkbook wb          = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		if(questions!= null){
			for(QuestionGroupDto g: groupList){
				for(QuestionDto q: questions.get(g)){
					HSSFRow row     = sheet.createRow((short)0); 
					HSSFCell cell   = row.createCell((short)0); 
					/*cell.setCellValue(1); 
					row.createCell((short)1).setCellValue(1.2); 
					row.createCell((short)2).setCellValue("This is a string");
					row.createCell((short)3).setCellValue(true);*/
				}
			}
		}
		
		FileOutputStream fileOut = new FileOutputStream(fileName.getName());
		wb.write(fileOut);
		fileOut.close();	
	}
}
