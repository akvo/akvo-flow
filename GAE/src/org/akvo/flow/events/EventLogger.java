/*
 *  Copyright (C) 2015 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.flow.events;

import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.appengine.api.datastore.DeleteContext;
import com.google.appengine.api.datastore.PostDelete;
import com.google.appengine.api.datastore.PostPut;
import com.google.appengine.api.datastore.PutContext;

public class EventLogger {
    private static Logger logger = Logger.getLogger(EventLogger.class.getName());
    
    @PostPut(kinds = {
            "SurveyGroup", "Survey", "QuestionGroup", "Question", "QuestionOption"
    })
    void logPut(PutContext context) {
        logger.log(Level.INFO, "Finished putting " + context.getCurrentElement().getKey().getId());
        try {
            ObjectMapper m = new ObjectMapper();
            StringWriter w = new StringWriter();
            // getCurrentElement returns an Entity
            m.writeValue(w, context.getCurrentElement());
            logger.log(Level.INFO, w.toString());
        } catch (Exception ex) {
        }

    }

    @PostDelete(kinds = {
            "SurveyGroup", "Survey", "QuestionGroup", "Question", "QuestionOption"
    })
    void logDelete(DeleteContext context) {
        // getCurrentElement returns a Key
        logger.log(Level.INFO, "Finished deleting " + context.getCurrentElement().getKind() + " "
                + context.getCurrentElement().getId());
    }
}
