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

package org.waterforpeople.mapping.helper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.analytics.domain.AccessPointStatusSummary;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.AccessPointScoreDetailDao;
import org.waterforpeople.mapping.dao.SurveyAttributeMappingDao;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPointMappingHistory;
import org.waterforpeople.mapping.domain.AccessPointScoreComputationItem;
import org.waterforpeople.mapping.domain.AccessPointScoreDetail;
import org.waterforpeople.mapping.domain.GeoCoordinates;
import org.waterforpeople.mapping.domain.QuestionAnswerStore;
import org.waterforpeople.mapping.domain.SurveyAttributeMapping;

import com.beoui.geocell.GeocellManager;
import com.beoui.geocell.model.Point;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.common.util.StringUtil;
import com.gallatinsystems.framework.analytics.summarization.DataSummarizationRequest;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.domain.DataChangeRecord;
import com.gallatinsystems.gis.coordinate.utilities.CoordinateUtilities;
import com.gallatinsystems.gis.location.GeoLocationServiceGeonamesImpl;
import com.gallatinsystems.gis.location.GeoPlace;
import com.gallatinsystems.gis.map.domain.OGRFeature;
import com.gallatinsystems.standards.dao.DistanceStandardDao;
import com.gallatinsystems.standards.dao.StandardScoringDao;
import com.gallatinsystems.standards.domain.DistanceStandard;
import com.gallatinsystems.standards.domain.LevelOfServiceScore;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.gallatinsystems.standards.domain.StandardScoring;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.domain.Question;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class AccessPointHelper {

    private static String photo_url_root;
    private static final String GEO_TYPE = "GEO";
    private static final String PHOTO_TYPE = "IMAGE";
    private SurveyAttributeMappingDao mappingDao;
    private static final String SCORE_AP_DYNAMIC_FLAG = "scoreAPDynamicFlag";
    static {
        Properties props = System.getProperties();
        photo_url_root = props.getProperty("photo_url_root");
    }

    private static Logger logger = Logger.getLogger(AccessPointHelper.class
            .getName());

    public AccessPointHelper() {
        mappingDao = new SurveyAttributeMappingDao();
    }

    public AccessPoint getAccessPoint(Long id) {
        BaseDAO<AccessPoint> apDAO = new BaseDAO<AccessPoint>(AccessPoint.class);
        return apDAO.getByKey(id);
    }

    public AccessPoint getAccessPoint(Long id, Boolean needScoreDetail) {
        BaseDAO<AccessPoint> apDAO = new BaseDAO<AccessPoint>(AccessPoint.class);
        AccessPointScoreDetailDao apddao = new AccessPointScoreDetailDao();
        AccessPoint ap = apDAO.getByKey(id);
        List<AccessPointScoreDetail> apScoreSummaryList = apddao
                .listByAccessPointId(id);
        if (apScoreSummaryList != null && !apScoreSummaryList.isEmpty())
            ap.setApScoreDetailList(apScoreSummaryList);
        return ap;
    }

    public void processSurveyInstance(String surveyInstanceId) {
        // Get the survey and QuestionAnswerStore
        // Get the surveyDefinition

        SurveyInstanceDAO sid = new SurveyInstanceDAO();

        List<QuestionAnswerStore> questionAnswerList = sid
                .listQuestionAnswerStore(Long.parseLong(surveyInstanceId), null);

        Collection<AccessPoint> apList = null;
        if (questionAnswerList != null && questionAnswerList.size() > 0) {
            try {
                apList = parseAccessPoint(new Long(questionAnswerList.get(0)
                        .getSurveyId()), questionAnswerList,
                        AccessPoint.AccessPointType.WATER_POINT);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "problem parsing access point.", ex);
            }
            if (apList != null) {
                for (AccessPoint ap : apList) {
                    try {
                        saveAccessPoint(ap);
                    } catch (Exception ex) {
                        logger.log(
                                Level.SEVERE,
                                "Inside processSurveyInstance could not save AP for SurveyInstanceId: "
                                        + surveyInstanceId + ":"
                                        + ap.toString() + " ex: " + ex
                                        + " exMessage: " + ex.getMessage());
                    }
                }
            }
        }
    }

    private Collection<AccessPoint> parseAccessPoint(Long surveyId,
            List<QuestionAnswerStore> questionAnswerList,
            AccessPoint.AccessPointType accessPointType) {
        Collection<AccessPoint> apList = null;
        List<SurveyAttributeMapping> mappings = mappingDao
                .listMappingsBySurvey(surveyId);
        if (mappings != null) {
            apList = parseAccessPoint(surveyId, questionAnswerList, mappings);
        } else {
            logger.log(Level.SEVERE, "NO mappings for survey " + surveyId);
        }
        return apList;
    }

    /**
     * uses the saved mappings for the survey definition to parse values in the questionAnswerStore
     * into attributes of an AccessPoint object TODO: figure out way around known limitation of only
     * having 1 GEO response per survey
     * 
     * @param questionAnswerList
     * @param mappings
     * @return
     */
    private Collection<AccessPoint> parseAccessPoint(Long surveyId,
            List<QuestionAnswerStore> questionAnswerList,
            List<SurveyAttributeMapping> mappings) {
        HashMap<String, AccessPoint> apMap = new HashMap<String, AccessPoint>();
        List<AccessPointMappingHistory> apmhList = new ArrayList<AccessPointMappingHistory>();
        List<Question> questionList = new QuestionDao()
                .listQuestionsBySurvey(surveyId);
        if (questionAnswerList != null) {
            for (QuestionAnswerStore qas : questionAnswerList) {
                SurveyAttributeMapping mapping = getMappingForQuestion(
                        mappings, qas.getQuestionID());
                if (mapping != null) {

                    List<String> types = mapping.getApTypes();
                    if (types == null || types.size() == 0) {
                        // default the list to be access point if nothing is
                        // specified (for backward compatibility)
                        types.add(AccessPointType.WATER_POINT.toString());
                    } else {
                        if (types.contains(AccessPointType.PUBLIC_INSTITUTION
                                .toString())
                                && (types.contains(AccessPointType.HEALTH_POSTS
                                        .toString()) || types
                                        .contains(AccessPointType.SCHOOL
                                                .toString()))) {
                            types.remove(AccessPointType.PUBLIC_INSTITUTION
                                    .toString());
                        }
                    }
                    for (String type : types) {
                        AccessPointMappingHistory apmh = new AccessPointMappingHistory();
                        apmh.setSource(this.getClass().getName());
                        apmh.setSurveyId(surveyId);
                        apmh.setSurveyInstanceId(qas.getSurveyInstanceId());
                        apmh.setQuestionId(Long.parseLong(qas.getQuestionID()));
                        apmh.addAccessPointType(type);
                        try {
                            AccessPoint ap = apMap.get(type);
                            if (ap == null) {
                                ap = new AccessPoint();
                                ap.setPointType(AccessPointType.valueOf(type));
                                // if(AccessPointType.PUBLIC_INSTITUTION.toString().equals(type)){
                                // //get the pointType value from the survey to
                                // properly set it
                                //
                                // }
                                apMap.put(type, ap);
                            }
                            ap.setCollectionDate(qas.getCollectionDate());
                            setAccessPointField(ap, qas, mapping, apmh);

                        } catch (NoSuchFieldException e) {
                            logger.log(
                                    Level.SEVERE,
                                    "Could not map field to access point: "
                                            + mapping.getAttributeName()
                                            + ". Check the surveyAttribueMapping for surveyId "
                                            + surveyId);
                        } catch (IllegalAccessException e) {
                            logger.log(Level.SEVERE,
                                    "Could not set field to access point: "
                                            + mapping.getAttributeName()
                                            + ". Illegal access.");
                        }

                        for (Question q : questionList) {
                            if (q.getKey().getId() == Long.parseLong(qas
                                    .getQuestionID())) {
                                apmh.setQuestionText(q.getText());
                                break;
                            }
                        }
                        apmhList.add(apmh);
                    }
                }
                // if (apmhList.size() > 0) {
                // BaseDAO<AccessPointMappingHistory> apmhDao = new
                // BaseDAO<AccessPointMappingHistory>(
                // AccessPointMappingHistory.class);
                // apmhDao.save(apmhList);
                // }
            }
        }
        return apMap.values();
    }

    /**
     * uses reflection to set the field on access point based on the value in questionAnswerStore
     * and the field name in the mapping
     * 
     * @param ap
     * @param qas
     * @param mapping
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void setAccessPointField(AccessPoint ap,
            QuestionAnswerStore qas, SurveyAttributeMapping mapping,
            AccessPointMappingHistory apmh) throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        apmh.setResponseAnswerType(qas.getType());
        QuestionDao qDao = new QuestionDao();
        ap.setSurveyId(qas.getSurveyId());
        ap.setSurveyInstanceId(qas.getSurveyInstanceId());
        Question q = qDao.getByKey(Long.parseLong(qas.getQuestionID()));
        if (!qas.getType().equals(q.getType().toString())) {
            qas.setType(q.getType().toString());
            logger.log(Level.INFO,
                    "Remapping question type value because QAS version is incorrect");
        }
        // FREE_TEXT, OPTION, NUMBER, GEO, PHOTO, VIDEO, SCAN, TRACK, NAME,
        // STRENGTH

        if (GEO_TYPE.equals(q.getType().toString())) {
            GeoCoordinates geoC = GeoCoordinates.extractGeoCoordinate(qas
                    .getValue());
            if (geoC != null) {
                ap.setLatitude(geoC.getLatitude());
                ap.setLongitude(geoC.getLongitude());
                ap.setAltitude(geoC.getAltitude());
                if (ap.getCommunityCode() == null && geoC.getCode() != null) {
                    ap.setCommunityCode(geoC.getCode());
                }
                apmh.setSurveyResponse(geoC.getLatitude() + "|"
                        + geoC.getLongitude() + "|" + geoC.getAltitude());
                apmh.setQuestionAnswerType("GEO");
                apmh.setAccessPointValue(ap.getLatitude() + "|"
                        + ap.getLongitude() + "|" + ap.getAltitude());
                apmh.setAccessPointField("Latitude,Longitude,Altitude");
            }
        } else {
            apmh.setSurveyResponse(qas.getValue());
            // if it's a value or OTHER type
            Field f = ap.getClass()
                    .getDeclaredField(mapping.getAttributeName());
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            apmh.setAccessPointField(f.getName());
            // TODO: Hack. In the QAS the type is PHOTO, but we were looking for
            // image this is why we were getting /sdcard I think.
            if (PHOTO_TYPE.equals(q.getType().toString())
                    || qas.getType().equals("PHOTO")) {
                String newURL = null;
                String[] photoParts = qas.getValue().split("/");
                if (photoParts.length > 1) {
                    newURL = photo_url_root
                            + photoParts[photoParts.length - 1];
                } else if (photoParts.length == 1) {
                    // handle the case where we only have the filename (no
                    // paths)
                    newURL = photo_url_root + photoParts[0];
                }
                f.set(ap, newURL);
                apmh.setQuestionAnswerType("PHOTO");
                apmh.setAccessPointValue(ap.getPhotoURL());
            } else if (mapping.getAttributeName().equals("pointType")) {
                if (qas.getValue().contains("Health")) {
                    f.set(ap, AccessPointType.HEALTH_POSTS);
                } else {
                    qas.setValue(qas.getValue().replace(" ", "_"));

                    f.set(ap, AccessPointType.valueOf(qas.getValue()
                            .toUpperCase()));
                }
            } else {
                String stringVal = qas.getValue();
                if (stringVal != null && stringVal.trim().length() > 0) {
                    if (f.getType() == String.class) {
                        f.set(ap, qas.getValue());
                        apmh.setQuestionAnswerType("String");
                        apmh.setAccessPointValue(f.get(ap).toString());
                    } else if (f.getType() == AccessPoint.Status.class) {
                        String val = qas.getValue();
                        f.set(ap, encodeStatus(val, ap.getPointType()));
                        apmh.setQuestionAnswerType("STATUS");
                        apmh.setAccessPointValue(f.get(ap).toString());
                    } else if (f.getType() == Double.class) {
                        try {
                            Double val = Double.parseDouble(stringVal.trim());
                            f.set(ap, val);
                            apmh.setQuestionAnswerType("DOUBLE");
                            apmh.setAccessPointValue(f.get(ap).toString());
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Could not parse "
                                    + stringVal + " as double", e);
                            apmh.setMappingMessage("Could not parse "
                                    + stringVal + " as double");
                        }
                    } else if (f.getType() == Long.class) {
                        try {
                            String temp = stringVal.trim();
                            if (temp.contains(".")) {
                                temp = temp.substring(0, temp.indexOf("."));
                            }
                            Long val = Long.parseLong(temp);
                            f.set(ap, val);
                            logger.info("Setting "
                                    + f.getName()
                                    + " to "
                                    + val
                                    + " for ap: "
                                    + (ap.getKey() != null ? ap.getKey()
                                            .getId() : "UNSET"));
                            apmh.setQuestionAnswerType("LONG");
                            apmh.setAccessPointValue(f.get(ap).toString());
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Could not parse "
                                    + stringVal + " as long", e);
                            apmh.setMappingMessage("Could not parse "
                                    + stringVal + " as long");
                        }
                    } else if (f.getType() == Boolean.class) {
                        try {
                            Boolean val = null;
                            if (stringVal.toLowerCase().contains("yes")) {
                                val = true;
                            } else if (stringVal.toLowerCase().contains("no")) {
                                val = false;
                            } else {
                                if (stringVal == null || stringVal.equals("")) {
                                    val = null;
                                }
                                val = Boolean.parseBoolean(stringVal.trim());
                            }
                            f.set(ap, val);
                            apmh.setQuestionAnswerType("BOOLEAN");
                            apmh.setAccessPointValue(f.get(ap).toString());
                        } catch (Exception e) {
                            logger.log(Level.SEVERE, "Could not parse "
                                    + stringVal + " as boolean", e);
                            apmh.setMappingMessage("Could not parse "
                                    + stringVal + " as boolean");
                        }
                    }
                }
            }
        }
    }

    /**
     * reads value of field from AccessPoint via reflection
     * 
     * @param ap
     * @param field
     * @return
     */
    public static String getAccessPointFieldAsString(AccessPoint ap,
            String field) {
        try {
            Field f = ap.getClass().getDeclaredField(field);
            if (!f.isAccessible()) {
                f.setAccessible(true);
            }
            Object val = f.get(ap);
            if (val != null) {
                return val.toString();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not extract field value: " + field,
                    e);
        }
        return null;
    }

    private SurveyAttributeMapping getMappingForQuestion(
            List<SurveyAttributeMapping> mappings, String questionId) {
        if (mappings != null) {
            for (SurveyAttributeMapping mapping : mappings) {
                if (mapping.getSurveyQuestionId().equals(questionId)) {
                    return mapping;
                }
            }
        }
        return null;
    }

    /**
     * generates a unique code based on the lat/lon passed in. Current algorithm returns the
     * concatenation of the integer portion of 1000 times absolute value of lat and lon in base 36
     * 
     * @param lat
     * @param lon
     * @return
     */
    private String generateCode(double lat, double lon) {
        Long code = Long.parseLong((int) ((Math.abs(lat) * 10000d)) + ""

                + (int) ((Math.abs(lon) * 10000d)));
        return Long.toString(code, 36);
    }

    /**
     * saves an access point and fires off a summarization message
     * 
     * @param ap
     * @return
     */
    public AccessPoint saveAccessPoint(AccessPoint ap) {
        AccessPointDao apDao = new AccessPointDao();
        AccessPoint apCurrent = null;
        if (ap != null) {
            if (ap.getPointType() != null && ap.getLatitude() != null
                    && ap.getLongitude() != null) {
                apCurrent = apDao.findAccessPoint(ap.getPointType(),
                        ap.getLatitude(), ap.getLongitude(),
                        ap.getCollectionDate());
                if (apCurrent != null) {
                    // if (!apCurrent.getKey().equals(ap.getKey())) {
                    ap.setKey(apCurrent.getKey());
                    // }
                }

                if (ap.getAccessPointCode() == null) {
                    ap.setAccessPointCode(generateCode(ap.getLatitude(),
                            ap.getLongitude()));
                    logger.log(
                            Level.INFO,
                            "No APCode set in ap so setting to: "
                                    + ap.getAccessPointCode());
                }
                if (ap.getCommunityCode() == null) {
                    if (ap.getAccessPointCode() != null)
                        ap.setCommunityCode(ap.getAccessPointCode());
                    logger.log(
                            Level.INFO,
                            "No Community Code set in ap so setting to: "
                                    + ap.getAccessPointCode());
                }

                if (ap.getKey() != null) {
                    String oldValues = null;
                    if (ap != null && ap.getKey() != null && apCurrent == null) {
                        apCurrent = apDao.getByKey(ap.getKey());
                    }
                    if (apCurrent != null) {
                        oldValues = formChangeRecordString(apCurrent);
                        if (apCurrent != null) {
                            ap.setKey(apCurrent.getKey());
                            apCurrent = ap;
                            logger.log(Level.INFO,
                                    "Found existing point and updating it."
                                            + apCurrent.getKey().getId());
                        }

                        // TODO: Hack since the fileUrl keeps getting set to
                        // incorrect value
                        // Changing from apCurrent to ap
                        ap = apDao.save(ap);

                        String newValues = formChangeRecordString(ap);

                        if (oldValues != null) {
                            DataChangeRecord change = new DataChangeRecord(
                                    AccessPointStatusSummary.class.getName(),
                                    "n/a", oldValues, newValues);
                            Queue queue = QueueFactory.getQueue("dataUpdate");
                            queue.add(TaskOptions.Builder
                                    .withUrl("/app_worker/dataupdate")
                                    .param(DataSummarizationRequest.OBJECT_KEY,
                                            ap.getKey().getId() + "")
                                    .param(DataSummarizationRequest.OBJECT_TYPE,
                                            "AccessPointSummaryChange")
                                    .param(DataSummarizationRequest.VALUE_KEY,
                                            change.packString()));

                        }
                    }
                } else {
                    logger.log(Level.INFO,
                            "Did not find existing point" + ap.toString());
                    if (ap.getGeocells() == null
                            || ap.getGeocells().size() == 0) {
                        if (ap.getLatitude() != null
                                && ap.getLongitude() != null
                                && ap.getLongitude() < 180
                                && ap.getLatitude() < 180) {
                            try {
                                ap.setGeocells(GeocellManager
                                        .generateGeoCell(new Point(ap
                                                .getLatitude(), ap
                                                .getLongitude())));
                            } catch (Exception ex) {
                                logger.log(Level.INFO,
                                        "Could not generate GeoCell for AP: "
                                                + ap.getKey().getId()
                                                + " error: " + ex);
                            }
                        }
                    }
                    try {
                        ap = apDao.save(ap);
                    } catch (Exception ex) {
                        logger.log(Level.INFO, "Could not save point" + ex);
                    }
                    if (ap.getKey() != null) {
                        Queue summQueue = QueueFactory
                                .getQueue("dataSummarization");
                        summQueue.add(TaskOptions.Builder
                                .withUrl("/app_worker/datasummarization")
                                .param("objectKey", ap.getKey().getId() + "")
                                .param("type", "AccessPoint"));
                    } else {
                        logger.log(
                                Level.SEVERE,
                                "After saving could not get key"
                                        + ap.toString());
                    }
                }
            }
        }

        if (ap != null) {
            if (Boolean.parseBoolean(PropertyUtil
                    .getProperty(SCORE_AP_DYNAMIC_FLAG))) {
                AccessPointHelper aph = new AccessPointHelper();
                aph.scoreAccessPointNew(ap);
            }
            return ap;
        } else
            return null;
    }

    private String formChangeRecordString(AccessPoint ap) {
        String changeString = null;
        if (ap != null) {
            changeString = (ap.getCountryCode() != null ? ap.getCountryCode()
                    : "")
                    + "|"
                    + (ap.getCommunityCode() != null ? ap.getCommunityCode()
                            : "")
                    + "|"
                    + (ap.getPointType() != null ? ap.getPointType().toString()
                            : "")
                    + "|"
                    + (ap.getPointStatus() != null ? ap.getPointStatus()
                            .toString() : "")
                    + "|"
                    + StringUtil.getYearString(ap.getCollectionDate());
        }
        return changeString;
    }

    public List<AccessPoint> listAccessPoint(String cursorString) {
        AccessPointDao apDao = new AccessPointDao();

        return apDao.list(cursorString);
    }

    public static AccessPoint.Status encodeStatus(String statusVal,
            AccessPoint.AccessPointType pointType) {
        AccessPoint.Status status = null;
        statusVal = statusVal.toLowerCase().trim();
        if (pointType.equals(AccessPointType.WATER_POINT)) {

            if ("functioning but with problems".equals(statusVal)
                    || "working but with problems".equals(statusVal)) {
                status = AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS;
            } else if ("broken down system".equals(statusVal)
                    || "broken down".equals(statusVal)
                    || statusVal.contains("broken")) {
                status = AccessPoint.Status.BROKEN_DOWN;
            } else if ("no improved system".equals(statusVal)
                    || "not a protected waterpoint".equals(statusVal)) {
                status = AccessPoint.Status.NO_IMPROVED_SYSTEM;
            } else if ("functioning and meets government standards"
                    .equals(statusVal)
                    || "working and protected".equals(statusVal)) {
                status = AccessPoint.Status.FUNCTIONING_HIGH;
            } else if ("high".equalsIgnoreCase(statusVal)
                    || "functioning".equals(statusVal)) {
                status = AccessPoint.Status.FUNCTIONING_HIGH;
            } else if ("ok".equalsIgnoreCase(statusVal)) {
                status = AccessPoint.Status.FUNCTIONING_OK;
            } else {
                status = AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS;
            }
        } else if (pointType.equals(AccessPointType.SANITATION_POINT)) {
            if ("latrine full".equals(statusVal))
                status = AccessPoint.Status.LATRINE_FULL;
            else if ("Latrine used but technical problems evident"
                    .toLowerCase().trim().equals(statusVal))
                status = AccessPoint.Status.LATRINE_USED_TECH_PROBLEMS;
            else if ("Latrine not being used due to structural/technical problems"
                    .toLowerCase().equals(statusVal))
                status = AccessPoint.Status.LATRINE_NOT_USED_TECH_STRUCT_PROBLEMS;
            else if ("Do not Know".toLowerCase().equals(statusVal))
                status = AccessPoint.Status.LATRINE_DO_NOT_KNOW;
            else if ("Functional".toLowerCase().equals(statusVal))
                status = AccessPoint.Status.LATRINE_FUNCTIONAL;
        } else {
            if ("functioning but with problems".equals(statusVal)) {
                status = AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS;
            } else if ("broken down system".equals(statusVal)) {
                status = AccessPoint.Status.BROKEN_DOWN;
            } else if ("no improved system".equals(statusVal))
                status = AccessPoint.Status.NO_IMPROVED_SYSTEM;
            else if ("functioning and meets government standards"
                    .equals(statusVal))
                status = AccessPoint.Status.FUNCTIONING_HIGH;
            else if ("high".equalsIgnoreCase(statusVal)) {
                status = AccessPoint.Status.FUNCTIONING_HIGH;
            } else if ("ok".equalsIgnoreCase(statusVal)) {
                status = AccessPoint.Status.FUNCTIONING_OK;
            } else {
                status = AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS;
            }
        }
        return status;
    }

    public AccessPoint setGeoDetails(AccessPoint point) {
        if (point.getLatitude() != null && point.getLongitude() != null) {
            GeoLocationServiceGeonamesImpl gs = new GeoLocationServiceGeonamesImpl();
            GeoPlace geoPlace = gs.manualLookup(point.getLatitude().toString(),
                    point.getLongitude().toString(),
                    OGRFeature.FeatureType.SUB_COUNTRY_OTHER);
            if (geoPlace != null) {
                point.setCountryCode(geoPlace.getCountryCode());
                point.setSub1(geoPlace.getSub1());
                point.setSub2(geoPlace.getSub2());
                point.setSub3(geoPlace.getSub3());
                point.setSub4(geoPlace.getSub4());
                point.setSub5(geoPlace.getSub5());
                point.setSub6(geoPlace.getSub6());
            } else if (geoPlace == null && point.getCountryCode() == null) {
                GeoPlace geoPlaceCountry = gs.manualLookup(point.getLatitude()
                        .toString(), point.getLongitude().toString(),
                        OGRFeature.FeatureType.COUNTRY);
                if (geoPlaceCountry != null) {
                    point.setCountryCode(geoPlaceCountry.getCountryCode());
                }
            }
        }
        return point;
    }

    public AccessPoint scoreAccessPointDynamic(AccessPoint ap) {
        AccessPointScoreDetail apss = new AccessPointScoreDetail();
        HashMap<Long, Integer> scoreBucketMap = new HashMap<Long, Integer>();

        logger.log(Level.INFO,
                "About to compute score for: " + ap.getCommunityCode());
        StandardScoringDao ssDao = new StandardScoringDao();
        List<StandardScoring> ssList = ssDao.listStandardScoring(ap);
        ArrayList<AccessPointScoreComputationItem> apsciList = new ArrayList<AccessPointScoreComputationItem>();
        if (ssList != null && !ssList.isEmpty()) {
            Integer score = 0;
            for (StandardScoring item : ssList) {

                if (scoreBucketMap.containsKey(item.getScoreBucketId())) {
                    score = scoreBucketMap.get(item.getScoreBucketId());
                } else {
                    scoreBucketMap.put(item.getScoreBucketId(), 0);
                }
                try {
                    AccessPointScoreComputationItem apsi = executeItemScore(ap,
                            score, item);
                    score = apsi.getScoreItem();
                    apsciList.add(apsi);
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                scoreBucketMap.put(item.getScoreBucketId(), score);
            }
            for (Entry<Long, Integer> item : scoreBucketMap.entrySet()) {
                String scoreBucketName = null;
                for (StandardScoring ssitem : ssList) {
                    if (ssitem.getScoreBucketId().equals(item.getKey())) {
                        scoreBucketName = ssitem.getScoreBucket();
                    }
                }
                apss.setScoreBucketId(item.getKey());
                apss.setScoreBucket(scoreBucketName);
                apss.setScore(item.getValue());
                apss.setScoreComputationItems(apsciList);
                ap.setScore(score);
                ap.setScoreComputationDate(new Date());
                apss.setComputationDate(ap.getScoreComputationDate());
                ap.setApScoreDetail(apss);
            }
        }

        return ap;
    }

    private AccessPointScoreComputationItem executeItemScore(AccessPoint ap,
            Integer score, StandardScoring item) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        String criteriaType = item.getCriteriaType();
        String scoreItemMessage = null;

        if (item.getCriteriaType().equals("Distance")) {
            if (ap.getNumberOutsideAcceptableDistance() != null
                    && ap.getNumberOutsideAcceptableDistance() == 0) {
                score = score + item.getPositiveScore();
                scoreItemMessage = item.getPositiveMessage();
            } else {
                scoreItemMessage = item.getNegativeMessage();
            }
        } else {
            item.setEvaluateField(StringUtil
                    .capitalizeFirstCharacterString(item.getEvaluateField()));
            if (criteriaType.equals("String")) {
                Method m = AccessPoint.class.getMethod(
                        "get" + item.getEvaluateField(), (Class<?>[]) null);
                String value = (String) m.invoke(ap, (Object[]) null);
                if (item.getPositiveOperator().equals("==")) {
                    if (item.getPositiveCriteria().equals(value)) {
                        score = score + item.getPositiveScore();
                        scoreItemMessage = item.getPositiveMessage();
                    }
                } else if (item.getPositiveOperator().equals("!=")) {
                    if (!item.getPositiveCriteria().equals(value)) {
                        score = score + item.getPositiveScore();
                        scoreItemMessage = item.getPositiveMessage();
                    }
                } else if (item.getNegativeOperator().equals("==")) {
                    if (item.getNegativeCriteria().equals(value)) {
                        score = score + item.getNegativeScore();
                        scoreItemMessage = item.getNegativeMessage();
                    }
                } else if (item.getNegativeOperator().equals("!=")) {
                    if (!item.getNegativeCriteria().equals(value)) {
                        score = score + item.getNegativeScore();
                        scoreItemMessage = item.getNegativeMessage();
                    }
                }
            } else if (criteriaType.equals("Boolean")) {
                Method m = AccessPoint.class.getMethod(
                        "get" + item.getEvaluateField(), (Class<?>[]) null);
                Boolean value = null;
                String type = m.getReturnType().toString();
                if (type.equals("class java.lang.Boolean")) {
                    value = Boolean.parseBoolean(m.invoke(ap, (Object[]) null).toString());
                    if (item.getPositiveOperator().equals("==")) {
                        if (Boolean.parseBoolean(item.getPositiveCriteria()) == value) {
                            score = score + item.getPositiveScore();
                            scoreItemMessage = item.getPositiveMessage();
                        }
                    } else if (item.getPositiveOperator().equals("!=")) {
                        if (Boolean.parseBoolean(item.getPositiveCriteria()) != value) {
                            score = score + item.getPositiveScore();
                            scoreItemMessage = item.getPositiveMessage();
                        }
                    }
                    if (item.getNegativeOperator() != null
                            && item.getNegativeOperator().equals("==")) {
                        if (Boolean.parseBoolean(item.getNegativeCriteria()) == value) {
                            score = score + item.getNegativeScore();
                            scoreItemMessage = item.getNegativeMessage();
                        }
                    } else if (item.getNegativeOperator() != null
                            && item.getNegativeOperator().equals("!=")) {
                        if (Boolean.parseBoolean(item.getNegativeCriteria()) != value) {
                            score = score + item.getNegativeScore();
                            scoreItemMessage = item.getNegativeMessage();
                        }

                    }
                }
            } else if (criteriaType.equals("Integer")
                    || criteriaType.equals("Number")) {
                Method m = AccessPoint.class.getMethod(
                        "get" + item.getEvaluateField(), (Class<?>[]) null);
                Float value = null;

                if (m.getReturnType().toString().equals("class java.lang.Long"))
                    value = Float.parseFloat(((Long) m.invoke(ap, (Object[]) null))
                            .toString());
                else if (m.getReturnType().toString()
                        .equals("class java.lang.Integer"))
                    value = Float.parseFloat(((Integer) m.invoke(ap, (Object[]) null))
                            .toString());
                else if (m.getReturnType().toString()
                        .equals("class java.lang.Double"))
                    value = Float.parseFloat(((Double) m.invoke(ap, (Object[]) null))
                            .toString());
                if (item.getPositiveOperator().equals("<=")) {
                    if (Integer.parseInt(item.getPositiveCriteria()) <= value) {
                        score = score + item.getPositiveScore();
                        scoreItemMessage = item.getPositiveMessage();
                    }
                } else if (item.getPositiveOperator().equals("<")) {
                    if (Integer.parseInt(item.getPositiveCriteria()) < value) {
                        score = score + item.getPositiveScore();
                        scoreItemMessage = item.getPositiveMessage();
                    }
                } else if (item.getPositiveOperator().equals("==")) {
                    if (Integer.parseInt(item.getPositiveCriteria()) == value) {
                        score = score + item.getPositiveScore();
                        scoreItemMessage = item.getPositiveMessage();
                    }
                } else if (item.getPositiveOperator().equals("!=")) {
                    if (Integer.parseInt(item.getPositiveCriteria()) != value) {
                        score = score + item.getPositiveScore();
                        scoreItemMessage = item.getPositiveMessage();
                    }
                } else if (item.getPositiveOperator().equals(">=")) {
                    if (Integer.parseInt(item.getPositiveCriteria()) >= value) {
                        score = score + item.getPositiveScore();
                        scoreItemMessage = item.getPositiveMessage();
                    }
                } else if (item.getPositiveOperator().equals(">")) {
                    if (Integer.parseInt(item.getPositiveCriteria()) > value) {
                        score = score + item.getPositiveScore();
                        scoreItemMessage = item.getPositiveMessage();
                    }
                } else if (item.getNegativeOperator().equals("<=")) {
                    if (Integer.parseInt(item.getNegativeCriteria()) <= value) {
                        score = score + item.getNegativeScore();
                        scoreItemMessage = item.getNegativeMessage();
                    }
                } else if (item.getNegativeOperator().equals("<")) {
                    if (Integer.parseInt(item.getNegativeCriteria()) < value) {
                        score = score + item.getNegativeScore();
                        scoreItemMessage = item.getNegativeMessage();
                    }
                } else if (item.getNegativeOperator().equals("==")) {
                    if (Integer.parseInt(item.getNegativeCriteria()) == value) {
                        score = score + item.getNegativeScore();
                        scoreItemMessage = item.getNegativeMessage();
                    }
                } else if (item.getNegativeOperator().equals("!=")) {
                    if (Integer.parseInt(item.getNegativeCriteria()) != value) {
                        score = score + item.getNegativeScore();
                        scoreItemMessage = item.getNegativeMessage();
                    }
                } else if (item.getNegativeOperator().equals(">=")) {
                    if (Integer.parseInt(item.getNegativeCriteria()) >= value) {
                        score = score + item.getNegativeScore();
                        scoreItemMessage = item.getNegativeMessage();
                    }
                } else if (item.getNegativeOperator().equals(">")) {
                    if (Integer.parseInt(item.getNegativeCriteria()) > value) {
                        score = score + item.getNegativeScore();
                        scoreItemMessage = item.getNegativeMessage();
                    }
                }
            }
        }
        return new AccessPointScoreComputationItem(score, scoreItemMessage);

    }

    public void scoreAccessPointNew(AccessPoint ap) {
        ScoringHelper sh = new ScoringHelper();
        if (ap.getPointType().equals(AccessPointType.WATER_POINT)) {
            BaseDAO<LevelOfServiceScore> losDao = new BaseDAO<LevelOfServiceScore>(
                    LevelOfServiceScore.class);
            LevelOfServiceScore los = sh.scoreWaterPointByLevelOfService(ap,
                    StandardType.WaterPointLevelOfService);
            ap = computeDistanceRule(ap);
            if (ap.getImprovedWaterPointFlag()) {
                if (ap.getNumberOutsideAcceptableDistance() >= 1) {
                    los.setScore(los.getScore() + 0);
                    los.addScoreDetail("Plus 0 Number of households outside of govt max acceptable distance is"
                            + ap.getNumberOutsideAcceptableDistance());
                    losDao.save(los);
                } else {
                    los.setScore(los.getScore() + 1);
                    los.addScoreDetail("Plus 1 Number of households outside of govt max acceptable distance is"
                            + ap.getNumberOutsideAcceptableDistance());
                    losDao.save(los);
                }
            } else {
                losDao.save(los);
            }
            LevelOfServiceScore losSustain = sh
                    .scoreWaterPointByLevelOfService(ap,
                            StandardType.WaterPointSustainability);
            losDao.save(losSustain);
        }
    }

    public static AccessPoint scoreAccessPoint(AccessPoint ap) {
        // Is there an improved water system no=0, yes=1
        // Provide enough drinking water for community everyday of year no=0,
        // yes=1, don't know=0,
        // Water system been down in 30 days: No=1,yes=0
        // Are there current problems: no=1,yes=0
        // meet govt quantity standards:no=0,yes=1
        // Is there a tarriff or fee no=0,yes=1

        AccessPointScoreDetail apss = new AccessPointScoreDetail();
        logger.log(Level.INFO,
                "About to compute score for: " + ap.getCommunityCode());
        Integer score = 0;
        if (ap.getPointType().equals(AccessPoint.AccessPointType.WATER_POINT)) {
            // added other conditions to guess if it's an improved point or not
            // since the scoring seems like it's flawed
            if ((ap.isImprovedWaterPointFlag() != null && ap
                    .isImprovedWaterPointFlag())
                    || (ap.getConstructionDateYear() != null && !ap
                            .getConstructionDateYear().trim()
                            .equalsIgnoreCase("na"))
                    || (ap.getConstructionDateYear() != null && !ap
                            .getConstructionDateYear().trim()
                            .equalsIgnoreCase("n/a"))) {
                score++;
                apss.addScoreComputationItem(1,
                        "Plus 1 for Improved Water System = true: ");
                if (ap.getProvideAdequateQuantity() != null
                        && ap.getProvideAdequateQuantity().equals(true)) {
                    score++;
                    apss.addScoreComputationItem(1,
                            "Plus 1 for Provide Adequate Quantity = true: ");

                } else {
                    apss.addScoreComputationItem(1,
                            "Plus 0 for Provide Adequate Quantity = false or null: ");
                }
                if (ap.getHasSystemBeenDown1DayFlag() != null
                        && !ap.getHasSystemBeenDown1DayFlag().equals(true)) {
                    score++;
                    apss.addScoreComputationItem(1,
                            "Plus 1 for Has System Been Down 1 Day Flag = false: ");
                } else {
                    apss.addScoreComputationItem(1,
                            "Plus 0 for Has System Been Down 1 Day Flag = true or null: ");
                }
                if (ap.getCurrentProblem() == null) {
                    score++;
                    apss.addScoreComputationItem(1,
                            "Plus 1 for Get Current Problem = null");
                } else {
                    apss.addScoreComputationItem(1,
                            "Plus 0 for Get Current Problem != null value: "
                                    + ap.getCurrentProblem());
                }

                if (ap.isCollectTariffFlag() != null
                        && ap.isCollectTariffFlag()) {
                    score++;
                    apss.addScoreComputationItem(1,
                            "Plus 1 for Collect Tariff Flag = true ");
                } else {
                    apss.addScoreComputationItem(1,
                            "Plus 0 for Collect Tariff Flag = false or null: ");
                }
            } else {
                apss.addScoreComputationItem(1,
                        "Plus 0 for Improved Water System = false or null: ");
            }

            apss.setScore(score);
            ap.setScore(score);
            ap.setScoreComputationDate(new Date());
            apss.setComputationDate(ap.getScoreComputationDate());

            logger.log(Level.INFO,
                    "AP Collected in 2011 so scoring: " + ap.getCommunityCode()
                            + "/" + ap.getCollectionDate() + " score: " + score);
            if (score == 0) {
                ap.setPointStatus(AccessPoint.Status.NO_IMPROVED_SYSTEM);
                apss.setStatus(AccessPoint.Status.NO_IMPROVED_SYSTEM.toString());
            } else if (score >= 1 && score <= 2) {
                ap.setPointStatus(AccessPoint.Status.BROKEN_DOWN);
                apss.setStatus(AccessPoint.Status.BROKEN_DOWN.toString());
            } else if (score >= 3 && score <= 4) {
                ap.setPointStatus(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS);
                apss.setStatus(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS
                        .toString());
            } else if (score >= 5) {
                ap.setPointStatus(AccessPoint.Status.FUNCTIONING_HIGH);
                apss.setStatus(AccessPoint.Status.FUNCTIONING_HIGH.toString());
            } else {
                ap.setPointStatus(AccessPoint.Status.OTHER);
                apss.setStatus(AccessPoint.Status.OTHER.toString());
            }
            ap.setApScoreDetail(apss);
        }
        return ap;
    }

    public AccessPoint computeDistanceRule(AccessPoint ap) {
        AccessPointDao apDao = new AccessPointDao();
        if (ap != null) {
            // StandardScoringDao ssDao = new StandardScoringDao();
            // List<StandardScoring> ssList = ssDao
            // .listLocalDistanceStandardScoringForAccessPoint(ap);
            Integer acceptableDistance = 500;

            DistanceStandardDao distanceStandardDao = new DistanceStandardDao();
            DistanceStandard ds = distanceStandardDao.findDistanceStandard(
                    StandardType.WaterPointLevelOfService, ap.getCountryCode(),
                    ap.getLocationType());
            if (ds != null) {
                acceptableDistance = ds.getMaxDistance();
            }

            // if (ssList != null && !ssList.isEmpty()) {
            // StandardScoring ssItem = ssList.get(0);
            // if (ssItem != null && ssItem.getPositiveCriteria() != null)
            // targetDistance = Integer.parseInt(ssItem
            // .getPositiveCriteria());
            // }
            if (ap.getTypeTechnologyString().equals(
                    "Gravity Fed System with Household Taps")) {
                // ToDo: check against tech type of HH, but need to know which
                // question
                ap.setNumberWithinAcceptableDistance(ap
                        .getNumberWithinAcceptableDistance() + 1);
            } else if (ap.getPointType().equals(
                    AccessPoint.AccessPointType.WATER_POINT)
                    && (ap.getCommunityCode() != null)) {
                List<AccessPoint> apList = apDao.listAccessPointByLocation(
                        ap.getCountryCode(), ap.getCommunityCode(),
                        AccessPointType.HOUSEHOLD.toString(), null, "all");
                if (apList != null && !apList.isEmpty()) {
                    for (AccessPoint hh : apList) {
                        Double distance = CoordinateUtilities.computeDistance(
                                ap.getLatitude(), ap.getLongitude(),
                                hh.getLatitude(), hh.getLongitude());
                        if (distance != null && distance < acceptableDistance) {
                            ap.setNumberWithinAcceptableDistance(ap
                                    .getNumberWithinAcceptableDistance() + 1);
                        } else {
                            ap.setNumberOutsideAcceptableDistance(ap
                                    .getNumberOutsideAcceptableDistance() + 1);
                        }
                    }
                    apDao.save(ap);
                }
            } else if (ap.getPointType().equals(AccessPointType.HOUSEHOLD)
                    && ap.getCommunityCode() != null) {
                List<AccessPoint> apList = apDao.listAccessPointByLocation(
                        ap.getCountryCode(), ap.getCommunityCode(),
                        AccessPointType.WATER_POINT.toString(), null, "all");
                AccessPoint minDistanceWaterPoint = null;
                Double minDistance = null;
                for (AccessPoint wp : apList) {
                    Double distance = CoordinateUtilities.computeDistance(
                            ap.getLatitude(), ap.getLongitude(),
                            wp.getLatitude(), wp.getLongitude());
                    if (minDistance == null || distance < minDistance) {
                        minDistance = CoordinateUtilities.computeDistance(
                                ap.getLatitude(), ap.getLongitude(),
                                wp.getLatitude(), wp.getLongitude());
                        minDistanceWaterPoint = wp;
                    }
                }
                if (minDistance != null && minDistance < acceptableDistance) {
                    minDistanceWaterPoint
                            .setNumberWithinAcceptableDistance(minDistanceWaterPoint
                                    .getNumberWithinAcceptableDistance() + 1);
                } else {
                    minDistanceWaterPoint
                            .setNumberOutsideAcceptableDistance(minDistanceWaterPoint // FIXME: NPE
                                    .getNumberOutsideAcceptableDistance());
                }
                apDao.save(minDistanceWaterPoint);
            }

        }
        return ap;
    }
}
