package org.waterforpeople.mapping.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.DisplayTemplateMapping;

import com.gallatinsystems.framework.dao.BaseDAO;

public class DisplayTemplateMappingDAO extends BaseDAO<DisplayTemplateMapping> {
	@SuppressWarnings("unused")
	private static Logger logger = Logger

	.getLogger(DisplayTemplateMappingDAO.class.getName());

	public DisplayTemplateMappingDAO() {
		super(DisplayTemplateMapping.class);
	}

	public List<DisplayTemplateMapping> save(
			List<DisplayTemplateMapping> listDTM) {
		List<DisplayTemplateMapping> returnList = new ArrayList<DisplayTemplateMapping>();
		for (DisplayTemplateMapping item : listDTM) {
			returnList.add((DisplayTemplateMapping) super.save(item));
		}
		return returnList;
	}

	public List<DisplayTemplateMapping> listByAccessPointType(
			String propertyValue, String cursorString) {
		return listByProperty("AccessPointType", propertyValue, "String");
	}

	public DisplayTemplateMapping get(Long id) {
		return (DisplayTemplateMapping) getByKey(id);
	}

}
