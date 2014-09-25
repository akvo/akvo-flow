package org.waterforpeople.mapping.app.web.rest.dto;

import java.io.Serializable;

public class ProjectFolderPayload implements Serializable {

    private String name;
    private Long parentId;

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Long getParentId() {
	return parentId;
    }

    public void setParentId(Long parentId) {
	this.parentId = parentId;
    }
}
