package org.waterforpeople.mapping.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.DisplayTemplateMapping;

import com.gallatinsystems.framework.dao.BaseDAO;

public class DisplayTemplateMappingHelper {
	private static Logger logger = Logger
			.getLogger(DisplayTemplateMappingHelper.class.getName());

	public List<DisplayTemplateMapping> save(
			List<DisplayTemplateMapping> listDTM) {
		BaseDAO<DisplayTemplateMapping> baseDAO = new BaseDAO<DisplayTemplateMapping>(
				DisplayTemplateMapping.class);
		List<DisplayTemplateMapping> returnList = new ArrayList<DisplayTemplateMapping>();
		for (DisplayTemplateMapping item : listDTM) {
			returnList.add(baseDAO.save(item));
		}
		return returnList;
	}

	public DisplayTemplateMapping save(DisplayTemplateMapping item) {
		BaseDAO<DisplayTemplateMapping> baseDAO = new BaseDAO<DisplayTemplateMapping>(
				DisplayTemplateMapping.class);
		return baseDAO.save(item);
	}

	public void delete(DisplayTemplateMapping item) {
		BaseDAO<DisplayTemplateMapping> baseDAO = new BaseDAO<DisplayTemplateMapping>(
				DisplayTemplateMapping.class);
		baseDAO.delete(item);
	}

	public List<DisplayTemplateMapping> list(String cursorString) {
		BaseDAO<DisplayTemplateMapping> baseDAO = new BaseDAO<DisplayTemplateMapping>(
				DisplayTemplateMapping.class);
		return baseDAO.list(cursorString);
	}

	public DisplayTemplateMapping get(Long id) {
		BaseDAO<DisplayTemplateMapping> baseDAO = new BaseDAO<DisplayTemplateMapping>(
				DisplayTemplateMapping.class);
		return baseDAO.getByKey(id);
	}
}
