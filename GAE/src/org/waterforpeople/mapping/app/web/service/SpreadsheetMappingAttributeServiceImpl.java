package org.waterforpeople.mapping.app.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.waterforpeople.mapping.app.web.client.SpreadsheetMappingAttributeService;
import org.waterforpeople.mapping.app.web.client.dto.MappingSpreadsheetColumnToAttribute;
import org.waterforpeople.mapping.app.web.client.dto.MappingSpreadsheetDefinition;
import org.waterforpeople.mapping.helper.SpreadsheetMappingAttributeHelper;

import com.google.gdata.util.ServiceException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SpreadsheetMappingAttributeServiceImpl extends
		RemoteServiceServlet implements SpreadsheetMappingAttributeService {

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

	}

	private org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition copyToCanonicalObject(
			MappingSpreadsheetDefinition mapDef) {
		org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition canonicalMapDef = new org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition();
		canonicalMapDef.setSpreadsheetURL(mapDef.getSpreadsheetURL());
		for (Map.Entry<String, MappingSpreadsheetColumnToAttribute> entry : mapDef
				.getColumnMap().entrySet()) {
			MappingSpreadsheetColumnToAttribute attribute = entry.getValue();
			org.waterforpeople.mapping.domain.MappingSpreadsheetColumnToAttribute canonicalAttribute = new org.waterforpeople.mapping.domain.MappingSpreadsheetColumnToAttribute();
			canonicalAttribute.setSpreadsheetColumn(attribute
					.getSpreadsheetColumn());
			canonicalAttribute.setObjectAttribute(attribute
					.getObjectAttribute());
			canonicalAttribute.setFormattingRule(attribute.getFormattingRule());
		}
		return canonicalMapDef;
	}
}
