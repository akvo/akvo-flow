package org.waterforpeople.mapping.app.web.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.rest.dto.ProjectFolderPayload;

import com.gallatinsystems.survey.dao.ProjectFolderDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.ProjectFolder;
import com.gallatinsystems.survey.domain.SurveyGroup;

@Controller
@RequestMapping("/project_folders")
public class ProjectFolderRestService {

    @Inject
    private SurveyGroupDAO surveyGroupDao;

    @Inject
    private ProjectFolderDAO projectFolderDao;

    // list children by projectFolderId
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listChildren(
	    @RequestParam(value = "projectFolderId", defaultValue="-1")
	    Long projectFolderId) {

	List<ProjectFolder> projectFolders = projectFolderDao.listByProjectFolderId(projectFolderId);
	List<SurveyGroup> projects = surveyGroupDao.listByProjectFolderId(projectFolderId);

	Map<String, Object> result = new HashMap<String, Object>();

	result.put("project_folders", projectFolders);
	result.put("projects", projects);

	return result;
    }

    // Create new survey group
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Map<String, ProjectFolder> saveNewProjectFolder(
	    @RequestBody
	    ProjectFolderPayload projectFolderPayload) {

	ProjectFolder projectFolder = new ProjectFolder(projectFolderPayload.getName(), projectFolderPayload.getParentId());
	ProjectFolder savedProjectFolder = projectFolderDao.save(projectFolder);
	Map<String, ProjectFolder> result = new HashMap<String, ProjectFolder>();
	result.put("project_folder", savedProjectFolder);
	return result;
    }
}
