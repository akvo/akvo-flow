package org.akvo.flow.rest;

import java.util.HashMap;
import java.util.Map;

import org.akvo.flow.domain.UserFormSubmissionsCounter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.CurrentUserServlet;

import com.gallatinsystems.user.domain.User;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

@Controller
@RequestMapping("/count_form_submissions")
public class CountFormSubmissionsRestService {

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Integer> getMySubmissionsCount() {
        Map<String, Integer> response = new HashMap<String, Integer>();
        User currentUser = CurrentUserServlet.getCurrentUser();
        UserFormSubmissionsCounter counter = new UserFormSubmissionsCounter(DatastoreServiceFactory.getDatastoreService());

        Integer value = counter.countFor(currentUser);

        response.put("value", value);

        return response;
    }
}
