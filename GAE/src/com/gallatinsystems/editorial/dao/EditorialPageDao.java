package com.gallatinsystems.editorial.dao;

import java.util.List;

import com.gallatinsystems.editorial.domain.EditorialPage;
import com.gallatinsystems.editorial.domain.EditorialPageContent;
import com.gallatinsystems.framework.dao.BaseDAO;

/**
 * data access object for persisting EditorialPage objects.
 * 
 * @author Christopher Fagiani
 * 
 */
public class EditorialPageDao extends BaseDAO<EditorialPage> {

	public EditorialPageDao() {
		super(EditorialPage.class);
	}

	/**
	 * returns a sorted list of all EditorialPageContent objects by pageId.
	 * 
	 * @param pageId
	 * @return
	 */
	public List<EditorialPageContent> listContentByPage(Long pageId) {
		return listByProperty("editorialPageId", pageId, "Long", "sortOrder",
				EditorialPageContent.class);
	}

	/**
	 * finds the EditorialPage that matched the targetFileName passed in. 
	 * 
	 * @param name
	 * @return
	 */
	public EditorialPage findByTargetPage(String name) {
		return findByProperty("targetFileName", name, "String");
	}

	
}
