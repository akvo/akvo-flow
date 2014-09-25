package com.gallatinsystems.survey.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable
public class ProjectFolder extends BaseDomain {

    private static final long serialVersionUID = 6484797146698705117L;

    private Long parentId;
    private String name;

    public ProjectFolder(String name, Long parentId) {
	this.name = name;
	this.parentId = parentId;
    }

    public Long getParentId() {
	return parentId;
    }

    public void setParentId(Long parentId) {
	this.parentId = parentId;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}
