/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.gis.map;

import java.util.HashMap;
import java.util.Map;

import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

import net.sf.jsr107cache.Cache;

import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.surveyal.dao.SurveyedLocaleClusterDao;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.gallatinsystems.surveyal.domain.SurveyedLocaleCluster;

public class MapUtils {

    // used to multiply latitude and longitude values, to fit them in a long
    private static final int MULT = 1000000;
    // used to divide long values by MULT, to go back to double values for
    // latitude / longitude values
    private static final double REVMULT = 0.000001;

    public static void recomputeCluster(Cache cache, SurveyedLocale locale) {

        final SurveyedLocaleClusterDao slcDao = new SurveyedLocaleClusterDao();
        final SurveyInstanceDAO siDao = new SurveyInstanceDAO();
        final SurveyDAO sDao = new SurveyDAO();

        Long latTotal;
        Long lonTotal;
        Double latCenter;
        Double lonCenter;
        Boolean showOnPublicMap = false;
        Long surveyId = null;
        String surveyIdString = "";

        if (locale.getLastSurveyalInstanceId() != null) {
            SurveyInstance si = siDao.getByKey(locale.getLastSurveyalInstanceId());
            if (si != null) {
                surveyId = si.getSurveyId();
                surveyIdString = surveyId.toString();

                // get public status, first try from cache
                String pubKey = surveyIdString + "-publicStatus";
                if (cache.containsKey(pubKey)) {
                    showOnPublicMap = (Boolean) cache.get(pubKey);
                } else {
                    Survey s = sDao.getByKey(surveyId);
                    if (s != null) {
                        showOnPublicMap = showOnPublicMap || s.getPointType().equals("Point")
                                || s.getPointType().equals("PublicInstitution");
                        cache.put(pubKey, showOnPublicMap);
                    }
                }
            }
        }

        for (int i = 1; i <= 4; i++) {

            String cell = locale.getGeocells().get(i) + "-" + showOnPublicMap.toString();

            if (cache.containsKey(cell)) {

                @SuppressWarnings("unchecked")
                final Map<String, Long> cellMap = (Map<String, Long>) cache.get(cell);
                final Long count = (Long) cellMap.get("count");

                latTotal = cellMap.get("lat") + Math.round(locale.getLatitude() * MULT);
                lonTotal = cellMap.get("lon") + Math.round(locale.getLongitude() * MULT);

                addToCache(cache, cell, cellMap.get("id"), count + 1, latTotal, lonTotal);

                SurveyedLocaleCluster clusterInStore = slcDao.getByKey(cellMap.get("id"));

                if (clusterInStore != null) {
                    clusterInStore.setCount(cellMap.get("count").intValue() + 1);
                    clusterInStore.setLatCenter(REVMULT * latTotal / (count + 1));
                    clusterInStore.setLonCenter(REVMULT * lonTotal / (count + 1));
                    slcDao.save(clusterInStore);
                }

            } else {
                // try to get it in the datastore. This can happen when the
                // cache has expired
                final SurveyedLocaleCluster clusterInStore = slcDao.getExistingCluster(locale
                        .getGeocells().get(i), showOnPublicMap);

                if (clusterInStore != null) {
                    final Long count = clusterInStore.getCount().longValue();

                    latCenter = (clusterInStore.getLatCenter() * count + locale.getLatitude())
                            / (count + 1);

                    lonCenter = (clusterInStore.getLonCenter() * count + locale.getLongitude())
                            / (count + 1);

                    addToCache(cache, cell, clusterInStore.getKey().getId(),
                            clusterInStore.getCount() + 1,
                            Math.round(MULT * latCenter * (count + 1)),
                            Math.round(MULT * lonCenter * (count + 1)));

                    clusterInStore.setCount(clusterInStore.getCount() + 1);
                    clusterInStore.setLatCenter(latCenter);
                    clusterInStore.setLonCenter(lonCenter);
                    slcDao.save(clusterInStore);
                } else {
                    // create a new one
                    SurveyedLocaleCluster slcNew = new SurveyedLocaleCluster(locale.getLatitude(),
                            locale.getLongitude(), locale.getGeocells().subList(0, i), locale
                                    .getGeocells().get(i), i + 1, locale.getKey().getId(),
                            showOnPublicMap, locale.getLastSurveyedDate());

                    slcDao.save(slcNew);

                    addToCache(cache, cell, slcNew.getKey().getId(), 1,
                            Math.round(MULT * locale.getLatitude()),
                            Math.round(MULT * locale.getLongitude()));
                }
            }
        }

    }

    private static void addToCache(Cache cache, String cell, Long id, long count, Long latTotal,
            Long lonTotal) {
        final Map<String, Long> v = new HashMap<String, Long>();
        v.put("count", count);
        v.put("id", id);
        // the cache stores lat/lon values as longs. We store the sums over the
        // whole cluster.
        v.put("lat", latTotal);
        v.put("lon", lonTotal);
        cache.put(cell, v);
    }
}
