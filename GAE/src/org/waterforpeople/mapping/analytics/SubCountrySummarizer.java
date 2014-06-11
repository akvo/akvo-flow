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
import java.util.Map;
import java.util.logging.Logger;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;
import com.gallatinsystems.gis.geography.dao.SubCountryDao;
import com.gallatinsystems.gis.geography.domain.SubCountry;
import com.gallatinsystems.gis.map.dao.OGRFeatureDao;
import com.gallatinsystems.gis.map.domain.OGRFeature;
import com.gallatinsystems.gis.map.domain.OGRFeature.FeatureType;
import com.google.common.collect.MapMaker;

/**
 * Summarizer that handles creation of SubCountry domain objects in response to persistence of a
 * OGRFeature. As with all DataSummarizers, this class is NOT thread-safe.
 * 
 * @author Christopher Fagiani
 */
public class SubCountrySummarizer implements DataSummarizer {
    @SuppressWarnings("unused")
    private Logger logger = Logger.getLogger(SubCountrySummarizer.class
            .getName());
    private SubCountryDao subCountryDao;
    private OGRFeatureDao featureDao;
    private String currentCursor;
    private Map<String, SubCountry> countryCache;

    public SubCountrySummarizer() {
        subCountryDao = new SubCountryDao();
        featureDao = new OGRFeatureDao();
        // use a soft map so we don't end up running out of memory
        countryCache = new MapMaker().softValues().softKeys().makeMap();
    }

    /**
     * returns the last cursor generated. NOTE: this class is not thread-safe.
     */
    @Override
    public String getCursor() {
        return currentCursor;
    }

    /**
     * creates SubCountry domain objects in response to persistence of a OGRFeature.
     */
    @Override
    public boolean performSummarization(String key, String type, String value,
            Integer offset, String cursor) {
        if (value != null) {
            findOrCreateSubCountry(key, value);
        } else {
            // if value is null, then we need to summarize a whole country. This
            // only usually occurs when catching up to previously loaded data.
            List<OGRFeature> features = featureDao.listByCountryAndType(key,
                    FeatureType.SUB_COUNTRY_OTHER, cursor);
            if (features != null) {
                for (OGRFeature f : features) {
                    findOrCreateSubCountry(f.getCountryCode(),
                            f.packSublevelString("|"));
                }
                currentCursor = OGRFeatureDao.getCursor(features);
            } else {
                currentCursor = null;
                return true;
            }
            if (currentCursor != null) {
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

    /**
     * finds or creates a subCountry record with the values passed in
     * 
     * @param countryCode
     * @param value - packed string of sub level values
     * @return
     */
    private SubCountry[] findOrCreateSubCountry(String countryCode, String value) {
        String[] values = value.split("\\|");
        SubCountry[] subLevels = new SubCountry[6];
        if (values != null) {

            for (int i = 0; i < values.length; i++) {
                if (values[i] != null
                        && !"null".equalsIgnoreCase(values[i].trim())) {
                    boolean cacheHit = true;
                    SubCountry sub = countryCache.get(countryCode + "|"
                            + values[i] + "" + (i + 1));
                    if (sub == null) {
                        sub = subCountryDao.findSubCountry(countryCode,
                                values[i].trim(), i + 1);
                        cacheHit = false;
                    }
                    if (sub == null) {
                        sub = new SubCountry();
                        sub.setCountryCode(countryCode);
                        sub.setName(values[i]);
                        sub.setLevel(i + 1);
                        if (i > 0) {
                            sub.setParentKey(subLevels[i - 1].getKey().getId());
                            sub.setParentName(subLevels[i - 1].getName());
                        }
                        subCountryDao.save(sub);
                        subLevels[i] = sub;
                    } else {
                        subLevels[i] = sub;
                    }
                    // if it was a cache miss or we just created the object,
                    // stick it in the cache
                    if (!cacheHit) {
                        countryCache.put(countryCode + "|" + values[i] + "|"
                                + (i + 1), sub);
                    }
                }
            }
        }
        return subLevels;
    }
}
