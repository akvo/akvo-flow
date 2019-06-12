package org.akvo.flow.xml;

import java.util.Arrays;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionDto;
import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class XmlQuestionGroup {

    @JacksonXmlElementWrapper(useWrapping = false)
    private XmlQuestion[] question;

    @JacksonXmlProperty(localName = "heading", isAttribute = false)
    private String heading;

    @JacksonXmlProperty(localName = "repeatable", isAttribute = true)
    private boolean repeatable;

    private int order;

    public XmlQuestionGroup() {
    }

    //Create a form XML object from a DTO
    public XmlQuestionGroup(QuestionGroupDto dto) {
        heading = dto.getCode();
        if (heading == null){
            heading = dto.getName();
        }
        repeatable = dto.getRepeatable();
        //Now copy the q tree
        question = new XmlQuestion[dto.getQuestionMap().size()];
        int i = 0;
        for (QuestionDto q: dto.getQuestionMap().values()) {
            question[i++] = new XmlQuestion(q);
        }
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String h) {
        this.heading = h;
    }

    public XmlQuestion[] getQuestion() {
        return question;
    }

    public void setQuestion(XmlQuestion[] qs) {
        this.question = qs;
    }

    public boolean getRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    /**
     * @return a Dto object with relevant fields copied
     */
    public QuestionGroupDto toDto() {
        QuestionGroupDto dto = new QuestionGroupDto();
        //TODO: lots
        dto.setName(heading);
        dto.setCode(heading);
        dto.setOrder(order);
        TreeMap<Integer,QuestionDto> qMap = new TreeMap<>();
        for (XmlQuestion q : question) {
            qMap.put(q.getOrder(), q.toDto());
        }
        dto.setQuestionMap(qMap);

        return dto;
    }

    @Override public String toString() {

        return "questionGroup{" +
                "order='" + order + '\'' +
                "heading='" + heading + '\'' +
                "questions=" + Arrays.toString(question) +
                '}';
    }
}
