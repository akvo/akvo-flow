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
import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.dao.AccessPointMetricSummaryDao;
import org.waterforpeople.mapping.analytics.domain.AccessPointMetricSummary;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.AccessPointMetricMappingDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPointMetricMapping;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.gallatinsystems.framework.analytics.summarization.DataSummarizer;

/**
 * Summarizer for populating AccessPointMetricSummary objects based on values in AccessPoint
 * 
 * @author Christopher Fagiani
 */
public class AccessPointMetricSummarizer implements DataSummarizer {
    Logger logger = Logger.getLogger(AccessPointMetricSummarizer.class
            .getName());

    /**
     * summarizes the access point metrics by rolling them up by geographic region and year. The
     * summaries will be incremented at each geographic level so counts can be provided in a
     * drill-down fashion.
     */
    @Override
    public boolean performSummarization(String key, String type, String value,
            Integer offset, String cursor) {
        AccessPointMetricMappingDao mappingDao = new AccessPointMetricMappingDao();
        AccessPointDao apDao = new AccessPointDao();
        if (key != null) {
            AccessPoint ap = apDao.getByKey(Long.parseLong(key),
                    AccessPoint.class);
            if (ap != null) {
                if (ap.getCountryCode() == null) {
                    logger.log(java.util.logging.Level.INFO,
                            "During APMS Summarization ap was missing countryCode so try GeoLocation");
                    AccessPointHelper aph = new AccessPointHelper();
                    ap = aph.setGeoDetails(ap);
                    apDao.save(ap);
                    if (ap.getCountryCode() != null)
                        logger.log(java.util.logging.Level.INFO,
                                "Mapped AP to " + ap.getCountryCode());
                }
                List<AccessPointMetricMapping> mappingList = mappingDao
                        .findMappings(ap.getOrganization(), null, null);
                if (mappingList != null) {
                    for (AccessPointMetricMapping mapping : mappingList) {
                        String fieldValue = AccessPointHelper
                                .getAccessPointFieldAsString(ap,
                                        mapping.getFieldName());
                        if (fieldValue != null
                                && fieldValue.trim().length() > 0) {
                            String valBucket = bucketizeValue(mapping,
                                    fieldValue);
                            String metricName = (mapping.getMetricName() != null ? mapping
                                    .getMetricName() : mapping.getFieldName());
                            AccessPointMetricSummary metricSummary = constructBaseSummary(
                                    fieldValue, mapping.getMetricGroup(),
                                    metricName, ap.getOrganization(),
                                    ap.getCountryCode(), valBucket);
                            metricSummary.setSubLevel(0);
                            metricSummary.setSubValue(ap.getCountryCode());
                            AccessPointMetricSummaryDao.incrementCount(
                                    metricSummary, 1);
                            if (ap.getSub1() != null) {
                                metricSummary = constructBaseSummary(
                                        fieldValue, mapping.getMetricGroup(),
                                        metricName, ap.getOrganization(),
                                        ap.getCountryCode(), valBucket);
                                metricSummary.setSubLevel(1);
                                metricSummary.setSubValue(ap.getSub1());
                                metricSummary.setParentSubName(ap
                                        .getCountryCode());
                                AccessPointMetricSummaryDao.incrementCount(
                                        metricSummary, 1);
                            }
                            if (ap.getSub2() != null) {
                                metricSummary = constructBaseSummary(
                                        fieldValue, mapping.getMetricGroup(),
                                        metricName, ap.getOrganization(),
                                        ap.getCountryCode(), valBucket);
                                metricSummary.setSubLevel(2);
                                metricSummary.setSubValue(ap.getSub2());
                                metricSummary.setParentSubName(ap
                                        .getCountryCode() + "/" + ap.getSub1());
                                AccessPointMetricSummaryDao.incrementCount(
                                        metricSummary, 1);
                            }
                            if (ap.getSub3() != null) {
                                metricSummary = constructBaseSummary(
                                        fieldValue, mapping.getMetricGroup(),
                                        metricName, ap.getOrganization(),
                                        ap.getCountryCode(), valBucket);
                                metricSummary.setSubLevel(3);
                                metricSummary.setSubValue(ap.getSub3());
                                metricSummary.setParentSubName(ap
                                        .getCountryCode()
                                        + "/"
                                        + ap.getSub1()
                                        + "/" + ap.getSub2());
                                AccessPointMetricSummaryDao.incrementCount(
                                        metricSummary, 1);
                            }
                            if (ap.getSub4() != null) {
                                metricSummary = constructBaseSummary(
                                        fieldValue, mapping.getMetricGroup(),
                                        metricName, ap.getOrganization(),
                                        ap.getCountryCode(), valBucket);
                                metricSummary.setSubLevel(4);
                                metricSummary.setSubValue(ap.getSub4());
                                metricSummary.setParentSubName(ap
                                        .getCountryCode()
                                        + "/"
                                        + ap.getSub1()
                                        + "/"
                                        + ap.getSub2()
                                        + "/"
                                        + ap.getSub3());
                                AccessPointMetricSummaryDao.incrementCount(
                                        metricSummary, 1);
                            }
                            if (ap.getSub5() != null) {
                                metricSummary = constructBaseSummary(
                                        fieldValue, mapping.getMetricGroup(),
                                        metricName, ap.getOrganization(),
                                        ap.getCountryCode(), valBucket);
                                metricSummary.setSubLevel(5);
                                metricSummary.setSubValue(ap.getSub5());
                                metricSummary.setParentSubName(ap
                                        .getCountryCode()
                                        + "/"
                                        + ap.getSub1()
                                        + "/"
                                        + ap.getSub2()
                                        + "/"
                                        + ap.getSub3() + "/" + ap.getSub4());
                                AccessPointMetricSummaryDao.incrementCount(
                                        metricSummary, 1);
                            }
                            if (ap.getSub6() != null) {
                                metricSummary = constructBaseSummary(
                                        fieldValue, mapping.getMetricGroup(),
                                        metricName, ap.getOrganization(),
                                        ap.getCountryCode(), valBucket);
                                metricSummary.setSubLevel(6);
                                metricSummary.setSubValue(ap.getSub6());
                                metricSummary.setParentSubName(ap
                                        .getCountryCode()
                                        + "/"
                                        + ap.getSub1()
                                        + "/"
                                        + ap.getSub2()
                                        + "/"
                                        + ap.getSub3()
                                        + "/"
                                        + ap.getSub4()
                                        + "/" + ap.getSub5());
                                AccessPointMetricSummaryDao.incrementCount(
                                        metricSummary, 1);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * constructs a summary object using the values passed in. Once this is returned, the caller
     * should set the relevant geo and count information prior to saving.
     * 
     * @param fieldValue
     * @param metricGroup
     * @param metricName
     * @param org
     * @param country
     * @param valueBucket
     * @return
     */
    private AccessPointMetricSummary constructBaseSummary(String fieldValue,
            String metricGroup, String metricName, String org, String country,
            String valueBucket) {
        AccessPointMetricSummary metricSummary = new AccessPointMetricSummary();
        metricSummary.setMetricValue(fieldValue);
        metricSummary.setMetricGroup(metricGroup);
        metricSummary.setMetricName(metricName);
        metricSummary.setOrganization(org);
        metricSummary.setCountry(country);
        metricSummary.setValueBucket(valueBucket);
        return metricSummary;
    }

    /**
     * converts a raw value to a "bucketized" value (i.e. "Positive", "Neutral", "Negative",
     * "Unknown"
     * 
     * @param mapping
     * @param value
     * @return
     */
    private String bucketizeValue(AccessPointMetricMapping mapping, String value) {
        String bucket = AccessPointMetricMapping.UNKOWN_BUCKET;
        if (mapping.getPositiveValues() != null) {
            if (mapping.getPositiveValues().contains(value)) {
                bucket = AccessPointMetricMapping.POSITIVE_BUCKET;
            } else if (mapping.getNeutralValues().contains(value)) {
                bucket = AccessPointMetricMapping.NEUTRAL_BUCKET;
            } else if (mapping.getNegativeValues().contains(value)) {
                bucket = AccessPointMetricMapping.NEGATIVE_BUCKET;
            }
        }
        return bucket;
    }

    @Override
    public String getCursor() {
        return null;
    }

}
