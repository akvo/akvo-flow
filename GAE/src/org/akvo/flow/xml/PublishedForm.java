package org.akvo.flow.xml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.survey.QuestionGroupDto;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
/*
 * Class for working with a form XML file like this:
 * <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
 * <survey name="Brand new form four" defaultLanguageCode="en" version='4.0' app="akvoflowsandbox" surveyGroupId="41213002" surveyGroupName="Brand new" surveyId="43993002">
 * <questionGroup><heading>Foo</heading>
 * <question order="1" type="option" mandatory="true" localeNameFlag="false" id="46843002">
 * <options allowOther="false" allowMultiple="false" renderType="radio">
 * <option value="Yes" code="Y"><text>Yes</text></option>
 * <option value="No" code="N"><text>No</text></option>
 * </options>
 * <text>Brand new option question</text></question>
 * <question order="2" type="free" mandatory="true" localeNameFlag="false" id="40533002">
 * <dependency answer-value="Yes" question="46843002"/><text>Name of optimist</text></question>
 * <question order="3" type="free" mandatory="true" localeNameFlag="false" id="45993002">
 * <dependency answer-value="No" question="46843002"/><text>Name of pessimist</text></question>
 * <question order="4" type="free" mandatory="true" localeNameFlag="false" id="47563002"><text>Victim</text></question>
 * <question order="5" type="free" mandatory="true" localeNameFlag="false" id="65813002"><text>New question - please change name</text></question>
 * </questionGroup>
 * <questionGroup><heading>Bar</heading>
 * <question order="1" type="free" mandatory="true" localeNameFlag="false" id="46673002"><text>AA</text></question>
 * <question order="2" type="free" mandatory="true" localeNameFlag="false" id="77813002"><text>BB</text></question>
 * <question order="3" type="free" mandatory="true" localeNameFlag="false" id="48523002"><text>CC</text></question>
 * <question order="4" type="free" mandatory="true" localeNameFlag="false" id="56093002"><text>DD</text></question>
 * </questionGroup>
 * </survey>
 */


public class PublishedForm {

    public static XmlForm parse(String xml) throws IOException {

        ObjectMapper objectMapper = new XmlMapper();
//        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        // Reads from XML and converts to POJO
        XmlForm form = objectMapper.readValue(xml, XmlForm.class);

        System.out.println(form);

        return form;

    }


    public static String generate(XmlForm tree) throws IOException {

        ObjectMapper objectMapper = new XmlMapper();

        // Reads from POJO and converts to XML
        String xml = objectMapper.writeValueAsString(tree);
        return xml;

        //      objectMapper.writeValue(new File("/tmp/fieldsets.xml"), fieldSet);

    }





}
