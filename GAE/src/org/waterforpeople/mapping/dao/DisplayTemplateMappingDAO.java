package org.waterforpeople.mapping.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.DisplayTemplateMapping;
import org.waterforpeople.mapping.helper.DisplayTemplateMappingHelper;

import com.gallatinsystems.framework.dao.BaseDAO;

public class DisplayTemplateMappingDAO extends BaseDAO {
	private static Logger logger = Logger
			.getLogger(DisplayTemplateMappingDAO.class.getName());

	public DisplayTemplateMappingDAO() {
		super(DisplayTemplateMapping.class);
	}

	public DisplayTemplateMappingDAO(Class e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

	public List<DisplayTemplateMapping> save(
			List<DisplayTemplateMapping> listDTM) {
		List<DisplayTemplateMapping> returnList = new ArrayList<DisplayTemplateMapping>();
		for (DisplayTemplateMapping item : listDTM) {
			returnList.add((DisplayTemplateMapping) super.save(item));
		}
		return returnList;
	}

	public DisplayTemplateMapping save(DisplayTemplateMapping item) {
		return (DisplayTemplateMapping) super.save(item);
	}

	public void delete(DisplayTemplateMapping item) {
		super.delete(item);
	}

	public List<DisplayTemplateMapping> list(String cursorString) {
		return super.list(cursorString);
	}

	public List<DisplayTemplateMapping> listByAccessPointType(
			String propertyValue, String cursorString) {
		return (List<DisplayTemplateMapping>) super.listByProperty(
				"AccessPointType", propertyValue, "String");
	}

	public DisplayTemplateMapping get(Long id) {
		return (DisplayTemplateMapping) super.getByKey(id);
	}

}
