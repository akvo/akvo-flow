package org.waterforpeople.mapping.app.gwt.client.editorial;

import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class EditorialPageDto extends BaseDto {

	private static final long serialVersionUID = 4181207832662426026L;

	private String template;
	private String type;
	private String targetFileName;
	private List<EditorialPageContentDto> contentItems;

	public List<EditorialPageContentDto> getContentItems() {
		return contentItems;
	}

	public void setContentItems(List<EditorialPageContentDto> contentItems) {
		this.contentItems = contentItems;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTargetFileName() {
		return targetFileName;
	}

	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

}
