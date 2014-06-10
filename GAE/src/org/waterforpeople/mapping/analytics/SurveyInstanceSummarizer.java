/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.analytics;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.dao.SurveyInstanceSummaryDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.GeoCoordinates;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyInstance;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.gis.location.GeoLocationService;
import com.gallatinsystems.gis.location.GeoLocationServiceGeonamesImpl;
import com.gallatinsystems.gis.location.GeoPlace;

/**
 * Populates SurveyInstanceSummary objects (a roll-up that aggregates survey instances by
 * country/region/day)
 * 
 * @author Christopher Fagiani
 */
public class SurveyInstanceSummarizer implements DataSummarizer {

    private static Logger logger = Logger
            .getLogger(SurveyInstanceSummarizer.class.getName());

    private static final String GEO_TYPE = "GEO";
    private SurveyInstanceDAO instanceDao;

    public SurveyInstanceSummarizer() {
        instanceDao = new SurveyInstanceDAO();
    }

    /**
     * looks up a survey instance then finds it's corresponding country and (if possible) sublevel1
     * using the GIS serviceF A second version of this function is present in
     * dataProcessorRestService.
     */
    @Override
    public boolean performSummarization(String key, String type, String value,
            Integer offset, String cursor) {
        boolean success = false;
        if (key != null) {
            SurveyInstance instance = instanceDao.getByKey(new Long(key));
            List<QuestionAnswerStore> qasList = instanceDao
                    .listQuestionAnswerStoreByType(new Long(key), GEO_TYPE);
            if (qasList != null) {
                GeoCoordinates geoC = null;
                for (QuestionAnswerStore q : qasList) {
                    if (q.getValue() != null
                            && q.getValue().trim().length() > 0) {
                        geoC = GeoCoordinates
                                .extractGeoCoordinate(q.getValue());
                        if (geoC != null) {
                            break;
                        }
                    }
                }
                if (geoC != null) {
                    GeoLocationService gisService = new GeoLocationServiceGeonamesImpl();
                    GeoPlace gp = gisService.findDetailedGeoPlace(geoC
                            .getLatitude().toString(), geoC.getLongitude()
                            .toString());
                    if (gp != null) {
                        SurveyInstanceSummaryDao.incrementCount(gp.getSub1(),
                                gp.getCountryCode(),
                                instance.getCollectionDate(), 1);
                        success = true;
                    }
                } else {
                    logger.log(Level.INFO,
                            "Instance does not have a geo question: "
                                    + instance.getKey().getId());
                    success = true;
                }
            }
            if (!success) {
                logger.log(
                        Level.SEVERE,
                        "Couldn't find community for instance. Was the community saved correctly? Instance id: "
                                + key);
            }
        }

        return true;
    }

    @Override
    public String getCursor() {
        return null;
    }
}
