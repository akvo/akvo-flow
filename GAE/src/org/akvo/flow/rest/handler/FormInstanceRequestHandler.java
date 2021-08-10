/*
 *  Copyright (C) 2021 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow.rest.handler;

import com.gallatinsystems.surveyal.dao.SurveyedLocaleDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import java.util.List;
import java.util.logging.Logger;

public class FormInstanceRequestHandler {
    private static final Logger log = Logger.getLogger(FormInstanceRequestHandler.class.getSimpleName());

    private SurveyInstanceDAO siDao = new SurveyInstanceDAO();

    public void deleteFormInstance(SurveyInstance formInstance) {
        SurveyedLocaleDao surveyedLocaleDao = new SurveyedLocaleDao();
        Long surveyedLocaleId = formInstance.getSurveyedLocaleId();

        if (surveyedLocaleId == null) {
            // we add this to catch the orphaned form
            // instances that are not related to any
            // datapoint. We simply delete them
            log.warning("Deleting a form instance that has no associated datapoint: " + formInstance.getKey().getId());
            siDao.delete(formInstance);
            return;
        }

        SurveyedLocale dataPoint = surveyedLocaleDao.getById(surveyedLocaleId);
        if (isRegistrationForm(dataPoint, formInstance)) {
            List<SurveyInstance> relatedSurveyInstances = siDao.listInstancesByLocale(dataPoint.getKey().getId(), null, null, null);
            surveyedLocaleDao.delete(dataPoint);
            siDao.delete(relatedSurveyInstances);
        } else {
            siDao.delete(formInstance);
        }
    }

    private boolean isRegistrationForm(SurveyedLocale dataPoint, SurveyInstance formInstance) {
        return dataPoint != null
                && dataPoint.getCreationSurveyId() != null
                && formInstance.getSurveyId() != null
                && dataPoint.getCreationSurveyId().equals(formInstance.getSurveyId());
    }
}
