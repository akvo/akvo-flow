package com.gallatinsystems.editorial.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class EditorialPageContent extends BaseDomain {

	private static final long serialVersionUID = -4573912898036246617L;
	private Long editorialPageId;
	private String type;
	private String heading;
	private Text text;
	private Long sortOrder;

	public Long getEditorialPageId() {
		return editorialPageId;
	}

	public void setEditorialPageId(Long editorialPageId) {
		this.editorialPageId = editorialPageId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

	public Long getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Long sortOrder) {
		this.sortOrder = sortOrder;
	}
}
