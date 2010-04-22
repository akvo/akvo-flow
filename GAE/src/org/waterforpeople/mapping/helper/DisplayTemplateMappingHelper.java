package org.waterforpeople.mapping.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.dao.DisplayTemplateMappingDAO;
import org.waterforpeople.mapping.domain.DisplayTemplateMapping;

public class DisplayTemplateMappingHelper {
	private static Logger logger = Logger
			.getLogger(DisplayTemplateMappingHelper.class.getName());
	public List<DisplayTemplateMapping> save(
			List<DisplayTemplateMapping> listDTM) {
		List<DisplayTemplateMapping> returnList = new ArrayList<DisplayTemplateMapping>();
		for (DisplayTemplateMapping item : listDTM) {
			returnList.add((DisplayTemplateMapping) new DisplayTemplateMappingDAO().save(item));
		}
		return returnList;
	}

	public DisplayTemplateMapping save(DisplayTemplateMapping item) {
		return (DisplayTemplateMapping) new DisplayTemplateMappingDAO().save(item);
	}

	public void delete(DisplayTemplateMapping item) {
		new DisplayTemplateMappingDAO().delete(item);
	}

	public List<DisplayTemplateMapping> list(String cursorString) {
		return new DisplayTemplateMappingDAO().list(cursorString);
	}

	public List<DisplayTemplateMapping> listByAccessPointType(
			String propertyValue, String cursorString) {
		return (List<DisplayTemplateMapping>) new DisplayTemplateMappingDAO().listByAccessPointType(
				"AccessPointType", propertyValue);
	}

	public DisplayTemplateMapping get(Long id) {
		return (DisplayTemplateMapping) new DisplayTemplateMappingDAO().getByKey(id);
	}

}
