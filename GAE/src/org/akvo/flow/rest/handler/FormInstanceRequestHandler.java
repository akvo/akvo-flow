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
        SurveyedLocale dataPoint = surveyedLocaleDao.getById(surveyedLocaleId);
        List<SurveyInstance> relatedSurveyInstances = siDao.listInstancesByLocale(surveyedLocaleId, null, null, null);
        if (relatedSurveyInstances.size() == 1
                && relatedSurveyInstances.get(0).getKey().getId() == formInstance.getKey().getId()) {
            // we need to consider what happens when a registration form is deleted even when there are
            // other existing monitoring forms. Should we delete the registration form *and* the monitoring forms
            // i.e. all the data?
            surveyedLocaleDao.delete(dataPoint);
        }
        siDao.delete(formInstance);
    }
}
