package org.waterforpeople.mapping.app.gwt.server.spreadsheetmapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.waterforpeople.mapping.adapter.SpreadsheetAccessPointAdapter;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingDefinitionColumnContainer;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingSpreadsheetColumnToAttribute;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingSpreadsheetDefinition;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeService;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto.QuestionType;
import org.waterforpeople.mapping.helper.SpreadsheetMappingAttributeHelper;

import com.gallatinsystems.common.data.spreadsheet.GoogleSpreadsheetAdapter;
import com.gallatinsystems.common.data.spreadsheet.domain.ColumnContainer;
import com.gallatinsystems.common.data.spreadsheet.domain.RowContainer;
import com.gallatinsystems.common.data.spreadsheet.domain.SpreadsheetContainer;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.OptionContainer;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionDependency;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.google.gdata.util.ServiceException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SpreadsheetMappingAttributeServiceImpl extends
		RemoteServiceServlet implements SpreadsheetMappingAttributeService {
	private static final Logger log = Logger
			.getLogger(SpreadsheetMappingAttributeServiceImpl.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = 7708378583408245812L;
	private String sessionToken = null;
	private PrivateKey privateKey = null;

	public SpreadsheetMappingAttributeServiceImpl() {

	}

	public void setCreds() {
		if (sessionToken == null || privateKey == null) {
			sessionToken = getSessionTokenFromSession();
			privateKey = getPrivateKeyFromSession();
		}
	}

	private String getSessionTokenFromSession() {
		HttpSession session = this.getThreadLocalRequest().getSession();
		String token = (String) session.getValue("sessionToken");

		return token;
	}

	private PrivateKey getPrivateKeyFromSession() {
		HttpSession session = this.getThreadLocalRequest().getSession();
		PrivateKey key = (PrivateKey) session.getValue("privateKey");
		return key;
	}

	@Override
	public ArrayList<String> listObjectAttributes(String objectNames) {
		return SpreadsheetMappingAttributeHelper.listObjectAttributes();
	}

	@Override
	public ArrayList<String> listSpreadsheetColumns(String spreadsheetName) {
		setCreds();
		log.info("listingSpreadsheetColumns");
		SpreadsheetMappingAttributeHelper helper = new SpreadsheetMappingAttributeHelper(
				sessionToken, privateKey);
		try {
			return helper.listSpreadsheetColumns(spreadsheetName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ArrayList<String> listSpreadsheets() {
		setCreds();
		return this.listSpreadsheetsFromFeed(null);
	}

	@Override
	public void saveSpreadsheetMapping(MappingSpreadsheetDefinition mapDef) {
		setCreds();
		// TODO change to return status of save or errors if there are any
		SpreadsheetMappingAttributeHelper helper = new SpreadsheetMappingAttributeHelper(
				sessionToken, privateKey);
		// convert to domain object from dto

		helper.saveSpreadsheetMapping(copyToCanonicalObject(mapDef));
	}

	@Override
	public String processSpreadsheet(MappingSpreadsheetDefinition mapDef) {
		setCreds();
		try {
			new SpreadsheetAccessPointAdapter(sessionToken, privateKey)
					.processSpreadsheetOfAccessPoints(mapDef
							.getSpreadsheetURL());
			return new String("Processed Successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String message = new String("Could not save spreadsheet : ");
			message.concat(e.getMessage());
			return message;
		}
		return null;
	}

	private org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition copyToCanonicalObject(
			MappingSpreadsheetDefinition mapDef) {
		org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition canonicalMapDef = new org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition();
		canonicalMapDef.setSpreadsheetURL(mapDef.getSpreadsheetURL());
		for (MappingSpreadsheetColumnToAttribute entry : mapDef.getColumnMap()) {
			MappingSpreadsheetColumnToAttribute attribute = entry;
			org.waterforpeople.mapping.domain.MappingSpreadsheetColumnToAttribute canonicalAttribute = new org.waterforpeople.mapping.domain.MappingSpreadsheetColumnToAttribute();
			canonicalAttribute.setSpreadsheetColumn(attribute
					.getSpreadsheetColumn());
			canonicalAttribute.setObjectAttribute(attribute
					.getObjectAttribute());
			canonicalAttribute.setFormattingRule(attribute.getFormattingRule());
			canonicalMapDef.addColumnToMap(canonicalAttribute);
		}
		return canonicalMapDef;
	}

	private MappingSpreadsheetDefinition copyToDTOObject(
			org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition canonicalMapDef) {

		MappingSpreadsheetDefinition mapSpreadsheetDTO = new MappingSpreadsheetDefinition();
		if (canonicalMapDef.getKey() != null)
			mapSpreadsheetDTO.setKeyId(canonicalMapDef.getKey().getId());
		mapSpreadsheetDTO
				.setSpreadsheetURL(canonicalMapDef.getSpreadsheetURL());
		if (canonicalMapDef.getColumnMap() != null) {
			for (org.waterforpeople.mapping.domain.MappingSpreadsheetColumnToAttribute entry : canonicalMapDef
					.getColumnMap()) {
				org.waterforpeople.mapping.domain.MappingSpreadsheetColumnToAttribute colAttr = entry;
				MappingSpreadsheetColumnToAttribute colAttrDTO = new MappingSpreadsheetColumnToAttribute();
				// colAttrDTO.setKeyId(colAttr.getKey().getId());
				colAttrDTO.setSpreadsheetColumn(colAttr.getSpreadsheetColumn());
				colAttrDTO.setObjectAttribute(colAttr.getObjectAttribute());
				log.info(colAttr.getSpreadsheetColumn() + "|"
						+ colAttr.getObjectAttribute());
				mapSpreadsheetDTO.addColumnToMap(colAttrDTO);
			}
		}
		return mapSpreadsheetDTO;
	}

	@Override
	public ArrayList<String> listSpreadsheetsFromFeed(String feedURL) {
		setCreds();
		if (feedURL == null) {
			try {
				try {
					return new SpreadsheetMappingAttributeHelper(sessionToken,
							privateKey)
							.listSpreadsheets("http://spreadsheets.google.com/feeds/spreadsheets/private/full");
				} catch (GeneralSecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	@Override
	public MappingDefinitionColumnContainer getMappingSpreadsheetDefinition(
			String spreadsheetName) {
		// TODO Auto-generated method stub
		MappingDefinitionColumnContainer mapdefCC = new MappingDefinitionColumnContainer();
		org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition mapDef = new SpreadsheetMappingAttributeHelper(
				sessionToken, privateKey)
				.getMappingSpreadsheetDefinition(spreadsheetName);
		if (mapDef != null) {
			mapdefCC.setMapDef(copyToDTOObject(mapDef));
			mapdefCC
					.setSpreadsheetColsList(listSpreadsheetColumns(spreadsheetName));
			return mapdefCC;

		}
		return null;
	}

	@Override
	public void processSurveySpreadsheet(String spreadsheetName) {
		setCreds();

		try {
			GoogleSpreadsheetAdapter gsa = new GoogleSpreadsheetAdapter(
					sessionToken, privateKey);
			SpreadsheetContainer sc = gsa
					.getSpreadsheetContents(spreadsheetName);
			SurveyGroup sg = new SurveyGroup();

			sg.setCode("HondurasSurveyLoader");
			Survey surveyCommunityWater = new Survey();
			HashMap<Question, QuestionDependency> dependencyMap = new HashMap<Question, QuestionDependency>();
			ArrayList<Question> questionList = new ArrayList<Question>();
			QuestionGroup qgBase = new QuestionGroup();
			qgBase.setCode("Base");
			QuestionGroup qgWater = new QuestionGroup();
			qgWater.setCode("Water");
			QuestionGroup qgSanitation = new QuestionGroup();
			qgSanitation.setCode("Sanitation");
			ArrayList<QuestionOption> qoList = new ArrayList<QuestionOption>();
			int i = 0;
			for (RowContainer row : sc.getRowContainerList()) {
				Survey targetSurvey = null;
				QuestionGroup targetQG = null;
				ArrayList<ColumnContainer> ccl = row.getColumnContainersList();
				Question q = new Question();
				questionList.add(q);
				OptionContainer oc = new OptionContainer();
				for (ColumnContainer cc : ccl) {
					String colName = cc.getColName();
					String colContents = cc.getColContents();
					log.info("colName: " + colName + " colContents: "
							+ colContents);
					if (colContents != null) {
						if (colName.toLowerCase().equals("survey")) {

							targetSurvey = surveyCommunityWater;
							targetSurvey.setName(colContents);
						} else if (colName.toLowerCase()
								.equals("questiongroup")) {
							if (colContents.toLowerCase().equals(
									"Base".toLowerCase())) {
								targetQG = qgBase;
							}
						} else if (colName.toLowerCase().equals("question")) {
							if (colContents.trim().length() > 500)
								q.setText(colContents.trim().substring(0, 500));
							else
								q.setText(colContents.trim());
						} else if (colName.toLowerCase().equals("questiontype")) {
							if (colContents.toLowerCase().equals(
									"FREE".toLowerCase()))
								q.setType(QuestionType.FREE_TEXT);
							else if (colContents.toLowerCase().equals(
									"GEO".toLowerCase()))
								q.setType(QuestionType.GEO);
							else if (colContents.toLowerCase().equals(
									"NUMBER".toLowerCase()))
								q.setType(QuestionType.NUMBER);
							else if (colContents.toLowerCase().equals(
									"OPTION".toLowerCase()))
								q.setType(QuestionType.OPTION);
							else if (colContents.toLowerCase().equals(
									"PHOTO".toLowerCase()))
								q.setType(QuestionType.PHOTO);
							else if (colContents.toLowerCase().equals(
									"SCAN".toLowerCase()))
								q.setType(QuestionType.SCAN);
							else if (colContents.toLowerCase().equals(
									"VIDEO".toLowerCase()))
								q.setType(QuestionType.VIDEO);
						} else if (colName.toLowerCase().equals(
								"Options".toLowerCase())
								&& q.getType().equals(QuestionType.OPTION)) {
							String[] splitColContents = colContents.trim()
									.split(";");
							for (String item : splitColContents) {
								String[] optionParts = item.trim().split("\\|");
								if (optionParts.length == 2) {
									String optionVal = optionParts[0];
									String text = optionParts[1];
									log.info("Val:" + optionVal + " Text:"
											+ text);
									QuestionOption qo = new QuestionOption();
									qo.setCode(optionVal);
									qo.setText(text);
									qoList.add(qo);
								}

							}
						} else if ((colName.equals("AllowOther") || colName
								.equals("AllowMultiple"))
								&& q.getType().equals(QuestionType.OPTION)) {
							if (colName.equals("AllowOther"))
								oc.setAllowOtherFlag(new Boolean(colContents
										.toLowerCase()));
							if (colName.equals("AllowMultiple"))
								oc.setAllowMultipleFlag(new Boolean(colContents
										.toLowerCase()));
						} else if (colName.equalsIgnoreCase("DependQuestion")) {
							if (colContents != null
									&& colContents.trim().length() > 0) {
								String[] parts = colContents.trim()
										.split("\\|");
								if (parts != null && parts.length >= 2) {
									try {
										QuestionDependency dependency = new QuestionDependency();
										dependency.setAnswerValue(parts[1]);
										dependencyMap.put(questionList
												.get(Integer.parseInt(parts[0]
														.trim())), dependency);
									} catch (Exception e) {
										log
												.log(
														Level.SEVERE,
														"Can't set dependency. The question number in the dependency column isn't an integer");
									}
								}

							}
						}

					}

				}
				if (q.getType().equals(QuestionType.OPTION)) {
					oc.setOptionsList(qoList);
					q.setOptionContainer(oc);
				}
				targetQG.addQuestion(q, i++);
				log.info("added: " + q.toString() + " :" + targetQG.getCode());
			}
			surveyCommunityWater.addQuestionGroup(qgWater);
			surveyCommunityWater.addQuestionGroup(qgBase);
			sg.addSurvey(surveyCommunityWater);
			SurveyGroupDAO sgDao = new SurveyGroupDAO();
			sgDao.save(sg);
			// now go through and re-save the dependencies
			QuestionDao qDao = new QuestionDao();
			for (Entry<Question, QuestionDependency> entry : dependencyMap
					.entrySet()) {
				Question q = entry.getKey();
				QuestionDependency dep = entry.getValue();
				dep.setQuestionId(q.getKey().getId());
				q.setDependQuestion(dep);
				qDao.save(q);
			}

		} catch (IOException e) {
			e.printStackTrace();

		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}
}
