package com.gallatinsystems.editorial.dao;

import java.util.List;

import com.gallatinsystems.editorial.domain.EditorialPage;
import com.gallatinsystems.editorial.domain.EditorialPageContent;
import com.gallatinsystems.framework.dao.BaseDAO;

public class EditorialPageDao extends BaseDAO<EditorialPage> {

	public EditorialPageDao() {
		super(EditorialPage.class);
	}

	public List<EditorialPageContent> listContentByPage(Long pageId) {
		return listByProperty("editorialPageId", pageId, "Long","sortOrder",
				EditorialPageContent.class);
	}
	
	public EditorialPage findByTargetPage(String name){
		return findByProperty("targetFileName", name, "String");
	}

}
