package org.waterforpeople.mapping.app.gwt.server.spreadsheetmapper;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

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
import com.gallatinsystems.survey.dao.QuestionGroupDao;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.OptionContainer;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.QuestionDependency;
import com.gallatinsystems.survey.domain.QuestionGroup;
import com.gallatinsystems.survey.domain.QuestionOption;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.gdata.util.ServiceException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SpreadsheetMappingAttributeServiceImpl extends
		RemoteServiceServlet implements SpreadsheetMappingAttributeService {
	private static final Logger log = Logger
			.getLogger(SpreadsheetMappingAttributeServiceImpl.class.getName());
	private QuestionGroupDao questionGroupDao;

	private static final long serialVersionUID = 7708378583408245812L;

	public SpreadsheetMappingAttributeServiceImpl() {
		questionGroupDao = new QuestionGroupDao();
	}

	private String getSessionTokenFromSession() throws Exception {
		HttpSession session = this.getThreadLocalRequest().getSession();

		String token = (String) session.getAttribute("sessionToken");
		if (token != null)
			return token;
		else
			throw new Exception(
					"Invalid or missing Google Spreadsheet Session Token");
	}

	private PrivateKey getPrivateKeyFromSession() throws Exception {
		HttpSession session = this.getThreadLocalRequest().getSession();
		PrivateKey key = (PrivateKey) session.getAttribute("privateKey");
		if (key != null)
			return key;
		else
			throw new Exception("Invalid or missing Private Key");
	}

	@Override
	public ArrayList<String> listObjectAttributes(String objectNames) {
		return SpreadsheetMappingAttributeHelper.listObjectAttributes();
	}

	@Override
	public ArrayList<String> listSpreadsheetColumns(String spreadsheetName) {

		log.info("listingSpreadsheetColumns");

		try {
			SpreadsheetMappingAttributeHelper helper = new SpreadsheetMappingAttributeHelper(
					getSessionTokenFromSession(), getPrivateKeyFromSession());
			return helper.listSpreadsheetColumns(spreadsheetName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// need to reauth
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ArrayList<String> listSpreadsheets() throws Exception {

		return this.listSpreadsheetsFromFeed(null);
	}

	@Override
	public void saveSpreadsheetMapping(MappingSpreadsheetDefinition mapDef) {

		// TODO change to return status of save or errors if there are any
		SpreadsheetMappingAttributeHelper helper;
		try {
			helper = new SpreadsheetMappingAttributeHelper(
					getSessionTokenFromSession(), getPrivateKeyFromSession());
			helper.saveSpreadsheetMapping(copyToCanonicalObject(mapDef));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// convert to domain object from dto

	}

	@Override
	public String processSpreadsheet(MappingSpreadsheetDefinition mapDef) {
		try {
			new SpreadsheetAccessPointAdapter(getSessionTokenFromSession(),
					getPrivateKeyFromSession())
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public ArrayList<String> listSpreadsheetsFromFeed(String feedURL)
			throws Exception {

		if (feedURL == null) {
			try {
				try {

					return new SpreadsheetMappingAttributeHelper(
							getSessionTokenFromSession(),
							getPrivateKeyFromSession())
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
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}

		}
		return null;
	}

	@Override
	public MappingDefinitionColumnContainer getMappingSpreadsheetDefinition(
			String spreadsheetName) {
		// TODO Auto-generated method stub
		MappingDefinitionColumnContainer mapdefCC = new MappingDefinitionColumnContainer();
		org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition mapDef;
		try {
			mapDef = new SpreadsheetMappingAttributeHelper(
					getSessionTokenFromSession(), getPrivateKeyFromSession())
					.getMappingSpreadsheetDefinition(spreadsheetName);
			if (mapDef != null) {
				mapdefCC.setMapDef(copyToDTOObject(mapDef));
				mapdefCC
						.setSpreadsheetColsList(listSpreadsheetColumns(spreadsheetName));
				return mapdefCC;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void processSurveySpreadsheetAsync(String tokenString,
			PrivateKey key, String spreadsheetName, int startRow, Long groupId) {

		try {
			org.apache.commons.codec.binary.Base64 b64encoder = new org.apache.commons.codec.binary.Base64();
			byte[] encodedKey = b64encoder.encode(key.getEncoded());
			GoogleSpreadsheetAdapter gsa = new GoogleSpreadsheetAdapter(
					tokenString, key);
			SpreadsheetContainer sc = gsa
					.getSpreadsheetContents(spreadsheetName);
			sc.setSpreadsheetName(spreadsheetName);
			RowContainer rowTitle = sc.getRowContainerList().get(1);
			String sgName = rowTitle.getColumnContainersList().get(0)
					.getColContents();
			if (sgName == null) {
				sgName = "Default";
			}

			if (startRow == -1) {
				setDependencies(sc);
			} else if (startRow == -2) {
				// create the survey and all groups. We'll take care of the
				// questions in another iteration
				SurveyGroupDAO sgDao = new SurveyGroupDAO();
				SurveyGroup sg = null;
				SurveyGroup sgFound = sgDao.findBySurveyGroupName(sgName);
				HashMap<String, QuestionGroup> groupMap = new HashMap<String, QuestionGroup>();
				if (sgFound != null) {
					sg = sgFound;
				} else {
					sg = new SurveyGroup();
				}

				sg.setCode(sgName);
				Survey survey = new Survey();
				sg.addSurvey(survey);
				// iterate over entire sheet to get the group names
				for (int i = 0; i < sc.getRowContainerList().size(); i++) {
					RowContainer row = sc.getRowContainerList().get(i);
					ArrayList<ColumnContainer> ccl = row
							.getColumnContainersList();
					for (ColumnContainer cc : ccl) {
						String colName = cc.getColName();
						String colContents = cc.getColContents();
						if (colContents != null) {
							if (colName.toLowerCase().equals("survey")) {
								survey.setName(colContents.trim());
							} else if (colName.toLowerCase().equals(
									"questiongroup")) {

								QuestionGroup group = groupMap.get(colContents
										.trim());
								if (group == null) {
									group = new QuestionGroup();
									group.setCode(colContents.trim());
									survey.addQuestionGroup(group);
									groupMap.put(colContents.trim(), group);
								}
							}
						}
					}
				}
				sgDao.save(sg);
				// send the message to start question processing
				sendSurveyProcessingMessage(spreadsheetName, 0, tokenString,
						encodedKey, key.getAlgorithm());
			} else {
				// now process the questions
				String currentPath = null;
				HashMap<String, QuestionGroup> groupMap = new HashMap<String, QuestionGroup>();

				int count = 0;
				int i = 0;

				for (count = startRow; count < sc.getRowContainerList().size()
						&& i < 10; count++) {
					i++;
					RowContainer row = sc.getRowContainerList().get(count);
					ArrayList<QuestionOption> qoList = new ArrayList<QuestionOption>();
					QuestionGroup targetQG = null;
					ArrayList<ColumnContainer> ccl = row
							.getColumnContainersList();
					Question q = new Question();
					OptionContainer oc = new OptionContainer();
					for (ColumnContainer cc : ccl) {
						String colName = cc.getColName();
						String colContents = cc.getColContents();
						if (colContents != null) {
							if (colName.toLowerCase().equals("survey")) {
								if (currentPath == null) {
									currentPath = sgName + "/" + colContents;
								}
							} else if (colName.toLowerCase().equals(
									"questiongroup")) {
								String groupName = colContents.trim();
								targetQG = groupMap.get(groupName);
								if (targetQG == null) {
									targetQG = questionGroupDao.getByPath(
											groupName, currentPath);
									if (targetQG != null) {
										groupMap.put(groupName, targetQG);
									}
								}
							} else if (colName.toLowerCase().equals("question")) {
								if (colContents.trim().length() > 500)
									q.setText(colContents.trim().substring(0,
											500));
								else
									q.setText(colContents.trim());
							} else if (colName.toLowerCase().equals(
									"questiontype")) {
								if (colContents.toLowerCase().equals(
										"FREE".toLowerCase())) {
									q.setType(QuestionType.FREE_TEXT);
								} else if (colContents.toLowerCase().equals(
										"GEO".toLowerCase())) {
									q.setType(QuestionType.GEO);
								} else if (colContents.toLowerCase().equals(
										"NUMBER".toLowerCase())) {
									q.setType(QuestionType.NUMBER);
								} else if (colContents.toLowerCase().equals(
										"OPTION".toLowerCase())) {
									q.setType(QuestionType.OPTION);
								} else if (colContents.toLowerCase().equals(
										"PHOTO".toLowerCase())) {
									q.setType(QuestionType.PHOTO);
								} else if (colContents.toLowerCase().equals(
										"SCAN".toLowerCase())) {
									q.setType(QuestionType.SCAN);
								} else if (colContents.toLowerCase().equals(
										"VIDEO".toLowerCase())) {
									q.setType(QuestionType.VIDEO);
								}
							} else if (colName.toLowerCase().equals(
									"Options".toLowerCase())
									&& q.getType().equals(QuestionType.OPTION)) {
								String[] splitColContents = colContents.trim()
										.split(";");
								for (String item : splitColContents) {
									String[] optionParts = item.trim().split(
											"\\|");
									if (optionParts.length == 2) {
										String optionVal = optionParts[0].trim();										
										String text = optionParts[1].trim();
										text.replaceAll("\\n", " ");
										QuestionOption qo = new QuestionOption();
										qo.setCode(optionVal);
										qo.setText(text);
										qoList.add(qo);
									}
								}
							} else if ((colName.equals("AllowOther") || colName
									.equals("AllowMultiple"))
									&& q.getType().equals(QuestionType.OPTION)) {
								if (colName.equals("AllowOther")) {
									oc.setAllowOtherFlag(new Boolean(
											colContents.toLowerCase()));
								}
								if (colName.equals("AllowMultiple")) {
									oc.setAllowMultipleFlag(new Boolean(
											colContents.toLowerCase()));
								}
							} else if (colName.equalsIgnoreCase("QuestionID")) {
								q.setReferenceIndex(colContents.trim());
							}
						}
					}
					if (q.getType().equals(QuestionType.OPTION)) {
						oc.setOptionsList(qoList);
						q.setOptionContainer(oc);
					}
					// TODO: fix this once we allow different groups
					// add new param "question offset" to subtract?
					q.setOrder(count);
					targetQG.addQuestion(q, count);
				}

				QuestionDao qDao = new QuestionDao();
				for (QuestionGroup group : groupMap.values()) {
					Long curGroupId = group.getKey().getId();
					for (Entry<Integer, Question> qEntry : group
							.getQuestionMap().entrySet()) {
						qDao.save(qEntry.getValue(), curGroupId);
					}
				}

				if (count < sc.getRowContainerList().size()) {

					sendSurveyProcessingMessage(sc.getSpreadsheetName(), count,
							tokenString, encodedKey, key.getAlgorithm());
				} else {
					sendSurveyProcessingMessage(sc.getSpreadsheetName(), -1,
							tokenString, encodedKey, key.getAlgorithm());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * sends a survey processing message to the task queue
	 * 
	 */
	private void sendSurveyProcessingMessage(String spreadsheetName,
			int startRow, String token, byte[] key, String keySpec) {
		Queue importQueue = QueueFactory.getQueue("spreadsheetImport");
		importQueue.add(url("/app_worker/sheetimport").param("identifier",
				spreadsheetName).param("type", "Survey").param("action",
				"processFile").param("startRow", startRow + "").param(
				"sessionToken", token).param("privateKey", key).param(
				"keySpec", keySpec));
	}

	@Override
	public void processSurveySpreadsheet(String spreadsheetName, int startRow,
			Long groupId) {
		try {
			processSurveySpreadsheetAsync(getSessionTokenFromSession(),
					getPrivateKeyFromSession(), spreadsheetName, startRow,
					groupId);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not process sheet: " + e, e);
		}
	}

	private void setDependencies(SpreadsheetContainer sc) {
		HashMap<Question, QuestionDependency> dependencyMap = new HashMap<Question, QuestionDependency>();
		ArrayList<Question> spreadsheetQuestions = new ArrayList<Question>();
		int rowIdx = 0;
		for (RowContainer row : sc.getRowContainerList()) {
			Question q = new Question();
			for (ColumnContainer cc : row.getColumnContainersList()) {
				String colName = cc.getColName();
				String colContents = cc.getColContents();
				if (colContents != null) {
					if (colName.toLowerCase().equals("question")) {
						if (colContents.trim().length() > 500)
							q.setText(colContents.trim().substring(0, 500));
						else
							q.setText(colContents.trim());
					} else if ("QuestionID".equalsIgnoreCase(colName)) {
						q.setReferenceIndex(colContents);
					} else if (colName.equalsIgnoreCase("DependQuestion")) {
						if (colContents != null
								&& colContents.trim().length() > 0) {
							String[] parts = colContents.trim().split("\\|");
							if (parts != null && parts.length >= 2) {
								try {
									QuestionDependency dependency = new QuestionDependency();
									dependency.setAnswerValue(parts[1]);
									dependency.setQuestionId(Long
											.parseLong(parts[0].trim()));
									dependencyMap.put(q, dependency);
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
			spreadsheetQuestions.add(q);
			rowIdx++;
		}
		if (dependencyMap.size() > 0) {

			QuestionDao qDao = new QuestionDao();

			for (Entry<Question, QuestionDependency> entry : dependencyMap
					.entrySet()) {
				Question q = entry.getKey();
				QuestionDependency dep = entry.getValue();
				/*
				 * Question parent =
				 * spreadsheetQuestions.get(dep.getQuestionId() .intValue() -
				 * 1);
				 */
				Question savedParent = qDao.findByReferenceId(dep
						.getQuestionId().toString());
				Question savedChild = qDao.findByReferenceId(q
						.getReferenceIndex());
				if (savedParent != null) {

					if (savedParent != null && savedChild != null) {
						dep.setQuestionId(savedParent.getKey().getId());
						savedChild.setDependQuestion(dep);
						qDao.save(savedChild);
					}
				} else {
					log.log(Level.SEVERE,
							"Couldn't find the parent question for the dependency: "
									+ q.getText());
				}
			}
		}
	}
}
