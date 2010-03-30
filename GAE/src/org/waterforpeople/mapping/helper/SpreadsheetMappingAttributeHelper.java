package org.waterforpeople.mapping.helper;

import java.io.IOException;
import java.util.ArrayList;

import org.waterforpeople.mapping.adapter.SpreadsheetAccessPointAdapter;
import org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.google.gdata.util.ServiceException;

public class SpreadsheetMappingAttributeHelper {

	public MappingSpreadsheetDefinition saveSpreadsheetMappingAttribute(
			MappingSpreadsheetDefinition mapDef) {
		return null;
	}

	public ArrayList<String> processSpreadsheet(
			MappingSpreadsheetDefinition mapDef) {
		String spreadsheetName = mapDef.getSpreadsheetURL();
		if (!spreadsheetName.trim().isEmpty()) {
			SpreadsheetAccessPointAdapter sapa = new SpreadsheetAccessPointAdapter();
			sapa.processSpreadsheetOfAccessPoints(spreadsheetName);
			// ToDo: need to decide how to let them know this is finished since
			// it could take a while
			// need to think about how to manage errors as well.
		}

		return null;
	}

	public ArrayList<String> listObjectAttributes(String objectNames) {
		ArrayList<String> attributesList = new ArrayList<String>();
		attributesList.add("altitude");
		attributesList.add("collectionDate");
		attributesList.add("communityCode");
		attributesList.add("constructionDate");
		attributesList.add("costPer");
		attributesList.add("currentManagementStructurePoint");
		attributesList.add("description");
		attributesList.add("farthestHouseholdfromPoint");
		attributesList.add("latitude");
		attributesList.add("longitude");
		attributesList.add("numberOfHouseholdsUsingPoint");
		attributesList.add("photoURL");
		attributesList.add("pointPhotoCaption");
		attributesList.add("pointStatus");
		attributesList.add("pointType");
		attributesList.add("typeTechnology");
		return attributesList;
	}

	public ArrayList<String> listSpreadsheetColumns(String spreadsheetName) throws IOException, ServiceException {
		if (!spreadsheetName.trim().isEmpty()) {
			SpreadsheetAccessPointAdapter sapa = new SpreadsheetAccessPointAdapter();
			return sapa.listColumns(spreadsheetName);
		}
		return null;
	}

	public ArrayList<String> listSpreadsheets() {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveSpreadsheetMapping(MappingSpreadsheetDefinition mapDef) {
		BaseDAO<MappingSpreadsheetDefinition> baseDAO  = new BaseDAO<MappingSpreadsheetDefinition>((Class<MappingSpreadsheetDefinition>) mapDef.getClass());
		baseDAO.save(mapDef);
	}
	
	public ArrayList<String> listSpreadsheets(String feedURL) throws IOException, ServiceException{
		return new SpreadsheetAccessPointAdapter().listSpreadsheets(feedURL);
	}
}
