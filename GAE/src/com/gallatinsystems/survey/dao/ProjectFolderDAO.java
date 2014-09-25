package com.gallatinsystems.survey.dao;

import java.util.List;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.survey.domain.ProjectFolder;

public class ProjectFolderDAO extends BaseDAO<ProjectFolder> {

    public ProjectFolderDAO() {
	super(ProjectFolder.class);
    }

    public List<ProjectFolder> listByProjectFolderId(Long parentId) {
	parentId = parentId < 0 ? null : parentId;

	return super.listByProperty("parentId", parentId, "Long");
    }
}
