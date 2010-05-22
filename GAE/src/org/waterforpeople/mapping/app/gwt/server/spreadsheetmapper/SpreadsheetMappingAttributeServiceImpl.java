package org.waterforpeople.mapping.app.gwt.server.spreadsheetmapper;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.waterforpeople.mapping.adapter.SpreadsheetAccessPointAdapter;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingDefinitionColumnContainer;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingSpreadsheetColumnToAttribute;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.MappingSpreadsheetDefinition;
import org.waterforpeople.mapping.app.gwt.client.spreadsheetmapper.SpreadsheetMappingAttributeService;
import org.waterforpeople.mapping.helper.SpreadsheetMappingAttributeHelper;

import com.gallatinsystems.common.data.spreadsheet.GoogleSpreadsheetAdapter;
import com.gallatinsystems.common.data.spreadsheet.domain.RowContainer;
import com.gallatinsystems.common.data.spreadsheet.domain.SpreadsheetContainer;
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
		log.info("listingSpreadsheets");
		SpreadsheetMappingAttributeHelper helper = new SpreadsheetMappingAttributeHelper(
				sessionToken, privateKey);
		return helper.listSpreadsheets();
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
					.processSpreadsheetOfAccessPoints(mapDef.getSpreadsheetURL());
			return new String("Processed Successfully");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			String message =new String("Could not save spreadsheet : ");
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
	public void processSurveySpreadsheet(String spreadsheetName)  {
		setCreds();
			
		try {
			GoogleSpreadsheetAdapter gsa = new GoogleSpreadsheetAdapter(
					sessionToken, privateKey);	
			SpreadsheetContainer sc = gsa
					.getSpreadsheetContents(spreadsheetName);
			for (RowContainer row : sc.getRowContainerList()) {
				log.info(row.toString());
				
				
			}
		} catch (IOException e) {
			e.printStackTrace();
			
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	}
}
