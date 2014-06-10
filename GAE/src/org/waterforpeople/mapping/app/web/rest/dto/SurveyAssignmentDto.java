
package org.waterforpeople.mapping.app.web.rest.dto;

import java.util.Date;
import java.util.List;

import com.gallatinsystems.framework.gwt.dto.client.BaseDto;

public class SurveyAssignmentDto extends BaseDto {

    private static final long serialVersionUID = -2656272454003993589L;

    private Date startDate;
    private Date endDate;
    private String name;
    private String language;
    private List<Long> surveys;
    private List<Long> devices;

    public List<Long> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<Long> surveyIds) {
        this.surveys = surveyIds;
    }

    public List<Long> getDevices() {
        return devices;
    }

    public void setDevices(List<Long> deviceIds) {
        this.devices = deviceIds;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}
