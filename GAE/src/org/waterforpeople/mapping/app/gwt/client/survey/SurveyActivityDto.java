package org.waterforpeople.mapping.app.gwt.client.survey;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO that can hold summarized information (i.e. counts) about survey activity.
 * 
 * @author Christopher Fagiani
 * 
 */
public class SurveyActivityDto implements Serializable {

	private static final long serialVersionUID = -912714008261009995L;

	private Date date;
	private String location;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	private Long count;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

}
