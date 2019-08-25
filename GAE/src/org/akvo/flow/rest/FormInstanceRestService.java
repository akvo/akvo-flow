package org.akvo.flow.rest;

import com.gallatinsystems.survey.domain.Survey;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.app.web.rest.dto.SurveyPayload;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;
import org.waterforpeople.mapping.domain.response.FormInstance;
import org.waterforpeople.mapping.serialization.SurveyInstanceHandler;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/form_instances")
public class FormInstanceRestService {
    // create new survey
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewFormInstance(@RequestBody FormInstance formInstance) {
        final Map<String, Object> response = new HashMap<String, Object>();

        // if the POST data contains a valid surveyDto, continue. Otherwise,
        // server will respond with 400 Bad Request
        if (formInstance == null) {
            return response;
        }

        SurveyInstance si = SurveyInstanceHandler.fromFormInstance(formInstance);

        SurveyInstance savedSurveyInstance = new SurveyInstanceDAO().save(si, null);


        final RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("ok");

        response.put("meta", statusDto);
        response.put("surveyInstance", savedSurveyInstance);
        return response;
    }
}
