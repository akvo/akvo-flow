package org.waterforpeople.mapping.app.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.waterforpeople.mapping.adapter.SpreadsheetAccessPointAdapter;
import org.waterforpeople.mapping.app.web.client.SpreadsheetMappingAttributeService;
import org.waterforpeople.mapping.app.web.client.dto.MappingDefinitionColumnContainer;
import org.waterforpeople.mapping.app.web.client.dto.MappingSpreadsheetColumnToAttribute;
import org.waterforpeople.mapping.app.web.client.dto.MappingSpreadsheetDefinition;
import org.waterforpeople.mapping.helper.SpreadsheetMappingAttributeHelper;

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

	@Override
	public ArrayList<String> listObjectAttributes(String objectNames) {
		SpreadsheetMappingAttributeHelper helper = new SpreadsheetMappingAttributeHelper();
		return helper.listObjectAttributes(objectNames);
	}

	@Override
	public ArrayList<String> listSpreadsheetColumns(String spreadsheetName) {
		SpreadsheetMappingAttributeHelper helper = new SpreadsheetMappingAttributeHelper();
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
		SpreadsheetMappingAttributeHelper helper = new SpreadsheetMappingAttributeHelper();
		return helper.listSpreadsheets();
	}

	@Override
	public void saveSpreadsheetMapping(MappingSpreadsheetDefinition mapDef) {
		// TODO change to return status of save or errors if there are any
		SpreadsheetMappingAttributeHelper helper = new SpreadsheetMappingAttributeHelper();
		// convert to domain object from dto

		helper.saveSpreadsheetMapping(copyToCanonicalObject(mapDef));
	}

	@Override
	public void processSpreadsheet(MappingSpreadsheetDefinition mapDef) {
		new SpreadsheetAccessPointAdapter()
				.processSpreadsheetOfAccessPoints(mapDef.getSpreadsheetURL());
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
		if (feedURL == null) {
			try {
				return new SpreadsheetMappingAttributeHelper()
						.listSpreadsheets("http://spreadsheets.google.com/feeds/spreadsheets/private/full");
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
		org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition mapDef = new SpreadsheetMappingAttributeHelper()
				.getMappingSpreadsheetDefinition(spreadsheetName);
		if (mapDef != null) {
			mapdefCC.setMapDef(copyToDTOObject(mapDef));
			mapdefCC
					.setSpreadsheetColsList(listSpreadsheetColumns(spreadsheetName));
			return mapdefCC;

		}
		return null;
	}
}
