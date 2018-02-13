/*
 *  Copyright (C) 2010-2012, 2018 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.velocity.VelocityContext;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.GeoRegionDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.GeoRegion;
import org.waterforpeople.mapping.domain.TechnologyType;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.common.util.VelocityUtil;
import com.gallatinsystems.editorial.dao.EditorialPageDao;
import com.gallatinsystems.editorial.domain.EditorialPage;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.dao.LOSScoreToStatusMappingDao;
import com.gallatinsystems.standards.dao.LevelOfServiceScoreDao;
import com.gallatinsystems.standards.domain.LOSScoreToStatusMapping;
import com.gallatinsystems.standards.domain.LevelOfServiceScore;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;
import com.google.appengine.api.datastore.Key;

public class KMLGenerator {
    private static final String IMAGE_ROOT = "mapiconimageroot";

    private static final Logger log = Logger.getLogger(KMLGenerator.class
            .getName());

    public static final String GOOGLE_EARTH_DISPLAY = "googleearth";
    // public static final String WATER_POINT_FUNCTIONING_GREEN_ICON_URL =
    // PropertyUtil
    // .getProperty(IMAGE_ROOT) + "/images/iconGreen36.png";
    // public static final String WATER_POINT_FUNCTIONING_YELLOW_ICON_URL =
    // PropertyUtil
    // .getProperty(IMAGE_ROOT) + "/images/iconYellow36.png";
    // public static final String WATER_POINT_FUNCTIONING_RED_ICON_URL =
    // PropertyUtil
    // .getProperty(IMAGE_ROOT) + "/images/iconRed36.png";
    public static final String WATER_POINT_FUNCTIONING_GREEN_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/glassGreen32.png";
    public static final String WATER_POINT_FUNCTIONING_YELLOW_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/glassOrange32.png";
    public static final String WATER_POINT_FUNCTIONING_RED_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/glassRed32.png";
    public static final String WATER_POINT_FUNCTIONING_BLACK_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/iconBlack36.png";
    public static final String PUBLIC_INSTITUTION_FUNCTIONING_GREEN_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/houseGreen36.png";
    public static final String PUBLIC_INSTITUTION_FUNCTIONING_YELLOW_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/houseYellow36.png";
    public static final String PUBLIC_INSTITUTION_FUNCTIONING_RED_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/houseRed36.png";
    public static final String PUBLIC_INSTITUTION_FUNCTIONING_BLACK_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/houseBlack36.png";
    public static final String PUBLIC_INSTITUTION_FUNCTION_BLACK_ICON_URL_2 = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/iconBlack36.png";
    public static final String SCHOOL_INSTITUTION_FUNCTIONING_GREEN_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/pencilGreen36.png";
    public static final String SCHOOL_INSTITUTION_FUNCTIONING_YELLOW_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/pencilYellow36.png";
    public static final String SCHOOL_INSTITUTION_FUNCTIONING_RED_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/pencilRed36.png";
    public static final String SCHOOL_INSTITUTION_FUNCTIONING_BLACK_ICON_URL = PropertyUtil
            .getProperty(IMAGE_ROOT) + "/images/pencilBlack36.png";
    public static final Boolean useScore = Boolean.parseBoolean(PropertyUtil
            .getProperty("scoreAPFlag"));
    public static final String ORGANIZATION_KEY = "organization";
    public static final String ORGANIZATION = PropertyUtil
            .getProperty("organization");
    private static final ThreadLocal<DateFormat> LONG_DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
        };
    };

    private static final Map<String, String> ICON_TYPE_MAPPING;
    private static final Map<String, String> ICON_COLOR_MAPPING;
    private static final String IMAGE_PREFIX = PropertyUtil
            .getProperty(IMAGE_ROOT);
    private static final String DEFAULT = "DEFAULT";
    public static final String defaultPhotoCaption = PropertyUtil
            .getProperty("defaultPhotoCaption");

    private static final String DYNAMIC_SCORING_FLAG = "scoreAPDynamicFlag";

    static {
        ICON_TYPE_MAPPING = new HashMap<String, String>();
        ICON_TYPE_MAPPING.put("WaterPoint", "glass");
        ICON_TYPE_MAPPING.put("PublicInstitution", "house");
        ICON_TYPE_MAPPING.put("Household", "house");
        ICON_TYPE_MAPPING.put("School", "pencil");
        ICON_TYPE_MAPPING.put("Trawler", "glass");
        ICON_TYPE_MAPPING.put(DEFAULT, "glass");

        ICON_COLOR_MAPPING = new HashMap<String, String>();
        ICON_COLOR_MAPPING.put(DEFAULT, "Black36.png");
        ICON_COLOR_MAPPING.put("FUNCTIONING_OK", "Green36.png");
        ICON_COLOR_MAPPING.put("FUNCTIONING_HIGH", "Green36.png");
        ICON_COLOR_MAPPING.put("FUNCTIONING_OK", "Yellow36.png");
        ICON_COLOR_MAPPING.put("FUNCTIONING_WITH_PROBLEMS", "Yellow36.png");
        ICON_COLOR_MAPPING.put("BROKEN_DOWN", "Black36.png");
        ICON_COLOR_MAPPING.put("NO_IMPROVED_SYSTEM", "Black36.png");
    }

    /**
     * forms the url for the placemark image based on the status
     * 
     * @param type
     * @param status
     * @return
     */
    public static String getMarkerImageUrl(String type, String status) {
        String url = KMLGenerator.IMAGE_PREFIX + "/images/";

        String typePart = KMLGenerator.ICON_TYPE_MAPPING
                .get(type != null ? type : KMLGenerator.DEFAULT);
        if (typePart == null) {
            typePart = KMLGenerator.ICON_TYPE_MAPPING.get(KMLGenerator.DEFAULT);
        }
        url += typePart;
        String statusPart = KMLGenerator.ICON_COLOR_MAPPING
                .get(status != null ? status : KMLGenerator.DEFAULT);
        if (statusPart == null) {
            statusPart = KMLGenerator.ICON_COLOR_MAPPING
                    .get(KMLGenerator.DEFAULT);
        }
        url += statusPart;
        return url;
    }

    public static final String useLongDates = PropertyUtil
            .getProperty("useLongDates");

    public KMLGenerator() {

    }

    public String generateRegionDocumentString(String regionVMName) {
        String regionKML = generateRegionOutlines(regionVMName);
        return regionKML;
    }

    public void generateCountryOrderedPlacemarks(String vmName,
            String countryCode, String technologyType) {

    }

    public HashMap<String, ArrayList<String>> generateCountrySpecificPlacemarks(
            String vmName, String countryCode) {
        if (countryCode.equals("MW")) {
            HashMap<String, ArrayList<AccessPoint>> techMap = new HashMap<String, ArrayList<AccessPoint>>();
            BaseDAO<TechnologyType> techDAO = new BaseDAO<TechnologyType>(
                    TechnologyType.class);
            List<TechnologyType> techTypeList = (List<TechnologyType>) techDAO
                    .list(Constants.ALL_RESULTS);
            AccessPointDao apDao = new AccessPointDao();
            List<AccessPoint> waterAPList = apDao.searchAccessPoints(
                    countryCode, null, null, null, "WATER_POINT", null, null,
                    null, null, null, null, Constants.ALL_RESULTS);
            for (TechnologyType techType : techTypeList) {
                // log.info("TechnologyType: " + techType.getName());
                ArrayList<AccessPoint> techTypeAPList = new ArrayList<AccessPoint>();
                for (AccessPoint item : waterAPList) {

                    if (techType.getName().toLowerCase()
                            .equals("unimproved waterpoint")
                            && item.getTypeTechnologyString().toLowerCase()
                                    .contains("unimproved waterpoint")) {
                        techTypeAPList.add(item);
                    } else if (item.getTypeTechnologyString().equals(
                            techType.getName())) {
                        techTypeAPList.add(item);
                    }
                }
                techMap.put(techType.getName(), techTypeAPList);
            }

            List<AccessPoint> sanitationAPList = apDao.searchAccessPoints(
                    countryCode, null, null, null, "SANITATION_POINT", null,
                    null, null, null, null, null, Constants.ALL_RESULTS);
            HashMap<String, AccessPoint> sanitationMap = new HashMap<String, AccessPoint>();
            for (AccessPoint item : sanitationAPList) {
                sanitationMap.put(item.getGeocells().toString(), item);
            }
            sanitationAPList = null;
            HashMap<String, ArrayList<String>> techPlacemarksMap = new HashMap<String, ArrayList<String>>();
            for (Entry<String, ArrayList<AccessPoint>> item : techMap
                    .entrySet()) {
                String key = item.getKey();
                ArrayList<String> placemarks = new ArrayList<String>();
                for (AccessPoint waterAP : item.getValue()) {

                    AccessPoint sanitationAP = sanitationMap.get(waterAP
                            .getGeocells().toString());
                    if (sanitationAP != null) {
                        placemarks.add(buildMainPlacemark(waterAP,
                                sanitationAP, vmName));
                    } else {
                        log.info("No matching sanitation point found for "
                                + waterAP.getLatitude() + ":"
                                + waterAP.getLongitude() + ":"
                                + waterAP.getCommunityName());
                    }
                }
                techPlacemarksMap.put(key, placemarks);
            }
            return techPlacemarksMap;
        }

        return null;
    }

    private HashMap<String, String> loadContextBindings(AccessPoint waterAP,
            AccessPoint sanitationAP) {
        // log.info(waterAP.getCommunityCode());
        try {
            HashMap<String, String> contextBindingsMap = new HashMap<String, String>();
            if (waterAP.getCollectionDate() != null) {
                String timestamp = DateFormatUtils.formatUTC(
                        waterAP.getCollectionDate(),
                        DateFormatUtils.ISO_DATE_FORMAT.getPattern());
                String formattedDate = DateFormat.getDateInstance(
                        DateFormat.SHORT).format(waterAP.getCollectionDate());
                contextBindingsMap.put("collectionDate", formattedDate);
                contextBindingsMap.put("timestamp", timestamp);
                String collectionYear = new SimpleDateFormat("yyyy")
                        .format(waterAP.getCollectionDate());
                contextBindingsMap.put("collectionYear", collectionYear);
            } else {
                // TODO: This block is a problem. We should never have data
                // without a collectionDate so this is a hack so it display
                // properly until I can sort out what to do with this data.
                String timestamp = DateFormatUtils.formatUTC(new Date(),
                        DateFormatUtils.ISO_DATE_FORMAT.getPattern());
                String formattedDate = DateFormat.getDateInstance(
                        DateFormat.SHORT).format(new Date());
                contextBindingsMap.put("collectionDate", formattedDate);
                contextBindingsMap.put("timestamp", timestamp);
                String collectionYear = new SimpleDateFormat("yyyy")
                        .format(new Date());
                contextBindingsMap.put("collectionYear", collectionYear);
            }
            contextBindingsMap.put("communityCode",
                    encodeNullDefault(waterAP.getCommunityCode(), "Unknown"));
            contextBindingsMap.put("communityName",
                    encodeNullDefault(waterAP.getCommunityName(), "Unknown"));
            contextBindingsMap.put(
                    "typeOfWaterPointTechnology",
                    encodeNullDefault(waterAP.getTypeTechnologyString(),
                            "Unknown"));
            contextBindingsMap.put(
                    "constructionDateOfWaterPoint",
                    encodeNullDefault(waterAP.getConstructionDateYear(),
                            "Unknown"));
            contextBindingsMap.put(
                    "numberOfHouseholdsUsingWaterPoint",
                    encodeNullDefault(
                            waterAP.getNumberOfHouseholdsUsingPoint(),
                            "Unknown"));
            contextBindingsMap.put("costPer20ML",
                    encodeNullDefault(waterAP.getCostPer(), "Unknown"));
            contextBindingsMap.put(
                    "farthestHouseholdFromWaterPoint",
                    encodeNullDefault(waterAP.getFarthestHouseholdfromPoint(),
                            "Unknown"));
            contextBindingsMap.put(
                    "currentManagementStructureOfWaterPoint",
                    encodeNullDefault(
                            waterAP.getCurrentManagementStructurePoint(),
                            "Unknown"));
            contextBindingsMap.put("waterSystemStatus",
                    encodeStatusString(waterAP.getPointStatus()));
            contextBindingsMap.put("photoUrl",
                    encodeNullDefault(waterAP.getPhotoURL(), "Unknown"));
            contextBindingsMap
                    .put("waterPointPhotoCaption",
                            encodeNullDefault(waterAP.getPointPhotoCaption(),
                                    "Unknown"));
            contextBindingsMap.put(
                    "primarySanitationTechnology",
                    encodeNullDefault(sanitationAP.getTypeTechnologyString(),
                            "Unknown"));
            contextBindingsMap.put(
                    "percentageOfHouseholdsWithImprovedSanitation",
                    encodeNullDefault(
                            sanitationAP.getNumberOfHouseholdsUsingPoint(),
                            "Unknown"));
            contextBindingsMap.put("photoOfPrimarySanitationtechnology",
                    encodeNullDefault(sanitationAP.getPhotoURL(), "Unknown"));
            contextBindingsMap.put(
                    "sanitationPhotoCaption",
                    encodeNullDefault(sanitationAP.getPointPhotoCaption(),
                            "Unknown"));
            contextBindingsMap.put("footer",
                    encodeNullDefault(waterAP.getFooter(), "Unknown"));
            contextBindingsMap.put(
                    "longitude",
                    encodeNullDefault(waterAP.getLongitude().toString(),
                            "Unknown"));
            contextBindingsMap.put(
                    "latitude",
                    encodeNullDefault(waterAP.getLatitude().toString(),
                            "Unknown"));
            contextBindingsMap.put("altitude",
                    encodeNullDefault(waterAP.getAltitude().toString(), "0.0"));
            contextBindingsMap.put(
                    "pinStyle",
                    encodePinStyle(waterAP.getPointType(),
                            waterAP.getPointStatus()));
            return contextBindingsMap;
        } catch (NullPointerException nex) {
            log.log(Level.SEVERE, "Could not load context bindings", nex);
        }
        return null;
    }

    private String buildMainPlacemark(AccessPoint waterAP,
            AccessPoint sanitationAP, String vmName) {
        HashMap<String, String> contextBindingsMap = loadContextBindings(
                waterAP, sanitationAP);
        VelocityContext context = new VelocityContext();

        for (Map.Entry<String, String> entry : contextBindingsMap.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }
        StringBuilder sb = new StringBuilder();
        String output = null;
        try {
            output = mergeContext(context, vmName);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not build main placemark", e);
        }
        sb.append(output);

        return sb.toString();
    }

    private String encodeNullDefault(Object value, String defaultMissingVal) {
        try {
            if (value != null) {
                return value.toString();
            } else {
                return defaultMissingVal;
            }
        } catch (Exception ex) {
            // log.info("value that generated nex: " + value);
            log.log(Level.SEVERE, "Could not encode null default", ex);
        }
        return null;
    }

    public String generatePlacemarks(String vmName, String countryCode) {
        return generatePlacemarks(vmName, countryCode, GOOGLE_EARTH_DISPLAY);
    }

    public String generatePlacemarks(String vmName, String countryCode,
            String display) {
        StringBuilder sb = new StringBuilder();
        AccessPointDao apDAO = new AccessPointDao();
        List<AccessPoint> entries = null;
        if (countryCode.equals(Constants.ALL_RESULTS))
            entries = apDAO.list(Constants.ALL_RESULTS);
        else
            entries = apDAO.searchAccessPoints(countryCode, null, null, null,
                    null, null, null, null, null, null, null,
                    Constants.ALL_RESULTS);

        // loop through accessPoints and bind to variables
        int i = 0;
        try {
            for (AccessPoint ap : entries) {
                if (!ap.getPointType().equals(
                        AccessPoint.AccessPointType.SANITATION_POINT)) {
                    try {
                        VelocityContext context = new VelocityContext();
                        String pmContents = bindPlacemark(
                                ap,
                                display.equalsIgnoreCase(GOOGLE_EARTH_DISPLAY) ? "placemarkGoogleEarth.vm"
                                        : "placemarkExternalMap.vm", display,
                                null);

                        if (ap.getCollectionDate() != null) {
                            String timestamp = DateFormatUtils.formatUTC(ap
                                    .getCollectionDate(),
                                    DateFormatUtils.ISO_DATE_FORMAT
                                            .getPattern());
                            String formattedDate = DateFormat.getDateInstance(
                                    DateFormat.SHORT).format(
                                    ap.getCollectionDate());
                            context.put("collectionDate", formattedDate);
                            context.put("timestamp", timestamp);
                            String collectionYear = new SimpleDateFormat("yyyy")
                                    .format(ap.getCollectionDate());
                            context.put("collectionYear", collectionYear);
                        } else {
                            String timestamp = DateFormatUtils.formatUTC(
                                    new Date(), DateFormatUtils.ISO_DATE_FORMAT
                                            .getPattern());
                            String formattedDate = DateFormat.getDateInstance(
                                    DateFormat.SHORT).format(new Date());
                            context.put("collectionDate", formattedDate);
                            context.put("timestamp", timestamp);
                        }
                        if (ap.getCommunityName() == null) {
                            context.put("communityName", "Unknown");
                        } else {
                            context.put("communityName", ap.getCommunityName());
                        }
                        if (ap.getCommunityCode() != null)
                            context.put("communityCode", ap.getCommunityCode());
                        else
                            context.put("communityCode", "Unknown" + new Date());
                        // Need to check this
                        if (ap.getPointType() != null) {
                            if (Boolean.parseBoolean(PropertyUtil
                                    .getProperty(DYNAMIC_SCORING_FLAG))) {

                            } else {
                                encodeStatusString(ap, context);
                                context.put(
                                        "pinStyle",
                                        encodePinStyle(ap.getPointType(),
                                                ap.getPointStatus()));
                            }
                        } else {
                            context.put("pinStyle", "waterpushpinblk");
                        }
                        context.put("latitude", ap.getLatitude());
                        context.put("longitude", ap.getLongitude());
                        if (ap.getAltitude() == null)
                            context.put("altitude", 0.0);
                        else
                            context.put("altitude", ap.getAltitude());

                        context.put("balloon", pmContents);
                        String placemarkStr = mergeContext(context, vmName);
                        sb.append(placemarkStr);
                        i++;
                    } catch (Exception e) {
                        log.log(Level.INFO, "Error generating placemarks: "
                                + ap.getCommunityCode(), e);
                    }
                }
            }
        } catch (Exception ex) {
            log.log(Level.SEVERE, "Bad access point: "
                    + entries.get(i + 1).toString());
        }
        return sb.toString();
    }

    /**
     * attempts to merge the context with the template. The template will be resolved from cache
     * and, if there is a miss, it will use the EditorialPageDao as the templateBackingStore. If the
     * template is still not found, it will attempt to use a static template file from the
     * application context.
     * 
     * @param context
     * @param template
     * @return
     * @throws Exception
     */
    private String mergeContext(VelocityContext context, String template)
            throws Exception {
        return VelocityUtil.mergeContext(context, template,
                new VelocityUtil.TemplateCacheBackingStore() {
                    @Override
                    public String getByKey(String key) {
                        EditorialPageDao edDao = new EditorialPageDao();
                        EditorialPage page = edDao.findByTargetPage(key);
                        if (page != null) {
                            return page.getTemplate().getValue();
                        } else {
                            return null;
                        }
                    }
                });
    }

    public String bindPlacemark(SurveyedLocale ap, String vmName, String display)
            throws Exception {
        if (ap.getCountryCode() == null) {
            ap.setCountryCode("Unknown");
        }

        VelocityContext context = new VelocityContext();
        context.put("organization", ORGANIZATION);
        if (display != null) {
            context.put("display", display);
        }
        context.put("countryCode", ap.getCountryCode());
        if (ap.getLastSurveyedDate() != null) {
            String timestamp = DateFormatUtils.formatUTC(
                    ap.getLastSurveyedDate(),
                    DateFormatUtils.ISO_DATE_FORMAT.getPattern());
            String formattedDate = null;
            if ("true".equals(useLongDates)) {
                formattedDate = LONG_DATE_FORMAT.get().format(ap
                        .getLastSurveyedDate());
            } else {
                formattedDate = DateFormat.getDateInstance(DateFormat.SHORT)
                        .format(ap.getLastSurveyedDate());
            }
            context.put("collectionDate", formattedDate);
            context.put("timestamp", timestamp);
            String collectionYear = new SimpleDateFormat("yyyy").format(ap
                    .getLastSurveyedDate());
            context.put("collectionYear", collectionYear);
        } else {
            String timestamp = DateFormatUtils.formatUTC(
                    ap.getCreatedDateTime(),
                    DateFormatUtils.ISO_DATE_FORMAT.getPattern());
            String formattedDate = null;
            if ("true".equals(useLongDates)) {
                formattedDate = LONG_DATE_FORMAT.get()
                        .format(ap.getCreatedDateTime());
            } else {
                formattedDate = DateFormat.getDateInstance(DateFormat.SHORT)
                        .format(ap.getCreatedDateTime());
            }
            context.put("collectionDate", formattedDate);
            context.put("timestamp", timestamp);
        }

        if (ap.getIdentifier() != null) {
            context.put("identifier", ap.getIdentifier());
        } else {
            context.put("identifier", "Unknown" + new Date());
        }

        boolean foundPhoto = false;
        boolean foundStatus = false;
        if (ap.getSurveyalValues() != null) {
            // TODO: handle case where we have multiple values (with different
            // dates) for same question/metric
            List<SurveyalValue> valuesToBind = new ArrayList<SurveyalValue>(
                    ap.getSurveyalValues());
            for (SurveyalValue val : ap.getSurveyalValues()) {
                if (val.getQuestionType() != null) {
                    if (!"free_text".equalsIgnoreCase(val.getQuestionType())
                            && !"option"
                                    .equalsIgnoreCase(val.getQuestionType())) {
                        valuesToBind.remove(val);
                    }
                }
                if (val.getStringValue() == null) {
                    valuesToBind.remove(val);
                } else if (val.getStringValue().trim().toLowerCase()
                        .endsWith(".jpg")
                        || val.getStringValue().trim().toLowerCase()
                                .endsWith(".jpeg")) {
                    String urlBase = val.getStringValue();
                    if (urlBase.contains("/")) {
                        urlBase = urlBase
                                .substring(urlBase.lastIndexOf("/") + 1);
                    }
                    if (!urlBase.toLowerCase().startsWith("http")) {
                        if (urlBase.endsWith("/")) {
                            urlBase = urlBase
                                    .substring(0, urlBase.length() - 1);
                        }
                        urlBase = PropertyUtil.getProperty("photo_url_root")
                                + urlBase;
                    }
                    context.put("photoUrl", urlBase);
                    foundPhoto = true;
                    valuesToBind.remove(val);
                } else if (ap.getCurrentStatus() == null) {
                    if (val.getMetricName() != null
                            && val.getMetricName().trim().toLowerCase()
                                    .contains("status")) {
                        context.put("waterSystemStatus", val.getStringValue());
                        foundStatus = true;
                    }
                }
            }

            context.put("surveyalValues", valuesToBind);
        }

        if (ap.getCurrentStatus() != null) {
            try {
                context.put("waterSystemStatus",
                        encodeStatusString(AccessPoint.Status.valueOf(ap
                                .getCurrentStatus())));
            } catch (Exception e) {
                context.put("waterSystemStatus", "Unknown");
            }
        } else {
            if (!foundStatus) {
                context.put("waterSystemStatus", "Unknown");
            }
        }
        // TODO: parameterize the default logo
        if (!foundPhoto) {
            context.put("photoUrl",
                    "http://waterforpeople.s3.amazonaws.com/images/wfplogo.jpg");
        }

        context.put("latitude", ap.getLatitude());
        context.put("longitude", ap.getLongitude());

        if (ap.getLocaleType() != null) {
            context.put("type", ap.getLocaleType());
        } else {
            context.put("type", "water");
        }

        String output = mergeContext(context, vmName);
        context = null;
        return output;
    }

    public String bindPlacemark(AccessPoint ap, String vmName, String display,
            StandardType standardType) throws Exception {
        // if (ap.getCountryCode() != null && !ap.getCountryCode().equals("MW"))
        // {
        if (display != null
                && display.trim().equalsIgnoreCase(GOOGLE_EARTH_DISPLAY)) {
            vmName = "placemarkGoogleEarth.vm";
        }
        if (ap.getCountryCode() == null)
            ap.setCountryCode("Unknown");
        if (ap.getCountryCode() != null) {

            VelocityContext context = new VelocityContext();
            context.put("organization", ORGANIZATION);
            if (display != null) {
                context.put("display", display);
            }
            context.put("countryCode", ap.getCountryCode());
            if (ap.getCollectionDate() != null) {
                String timestamp = DateFormatUtils.formatUTC(
                        ap.getCollectionDate(),
                        DateFormatUtils.ISO_DATE_FORMAT.getPattern());
                String formattedDate = DateFormat.getDateInstance(
                        DateFormat.SHORT).format(ap.getCollectionDate());
                context.put("collectionDate", formattedDate);
                context.put("timestamp", timestamp);
                String collectionYear = new SimpleDateFormat("yyyy").format(ap
                        .getCollectionDate());
                context.put("collectionYear", collectionYear);
            } else {
                String timestamp = DateFormatUtils.formatUTC(
                        ap.getCreatedDateTime(),
                        DateFormatUtils.ISO_DATE_FORMAT.getPattern());
                String formattedDate = DateFormat.getDateInstance(
                        DateFormat.SHORT).format(ap.getCreatedDateTime());
                context.put("collectionDate", formattedDate);
                context.put("timestamp", timestamp);
            }

            if (ap.getCommunityCode() != null)
                context.put("communityCode", ap.getCommunityCode());
            else
                context.put("communityCode", "Unknown" + new Date());

            if (ap.getWaterForPeopleProjectFlag() != null) {
                context.put("waterForPeopleProject",
                        encodeBooleanDisplay(ap.getWaterForPeopleProjectFlag()));
            } else {
                context.put("waterForPeopleProject", "null");
            }

            if (ap.getCurrentProblem() != null) {
                context.put("currentProblem", ap.getCurrentProblem());
            } else {
                context.put("currentProblem", ap.getCurrentProblem());
            }

            if (ap.getWaterForPeopleRole() != null) {
                context.put("waterForPeopleRole", ap.getWaterForPeopleRole());
            } else {
                context.put("waterForPeopleRole", "null");
            }

            if (ap.getPhotoURL() != null && ap.getPhotoURL().trim() != "")
                context.put("photoUrl", ap.getPhotoURL());
            else
                context.put("photoUrl",
                        "http://waterforpeople.s3.amazonaws.com/images/wfplogo.jpg");
            if (ap.getPointType() != null) {
                if (ap.getPointType().equals(
                        AccessPoint.AccessPointType.WATER_POINT)) {
                    context.put("typeOfPoint", "Water");
                    context.put("type", "water");
                } else if (ap.getPointType().equals(
                        AccessPointType.SANITATION_POINT)) {
                    context.put("typeOfPoint", "Sanitation");
                    context.put("type", "sanitation");
                } else if (ap.getPointType().equals(
                        AccessPointType.PUBLIC_INSTITUTION)) {
                    context.put("typeOfPoint", "Public Institutions");
                    context.put("type", "public_institutions");
                } else if (ap.getPointType().equals(
                        AccessPointType.HEALTH_POSTS)) {
                    context.put("typeOfPoint", "Health Posts");
                    context.put("type", "health_posts");
                } else if (ap.getPointType().equals(AccessPointType.SCHOOL)) {
                    context.put("typeOfPoint", "School");
                    context.put("type", "school");
                }
            } else {
                context.put("typeOfPoint", "Water");
                context.put("type", "water");
            }

            if (ap.getTypeTechnologyString() == null) {
                context.put("primaryTypeTechnology", "Unknown");
            } else {
                context.put("primaryTypeTechnology",
                        ap.getTypeTechnologyString());
            }

            if (ap.getHasSystemBeenDown1DayFlag() == null) {
                context.put("down1DayFlag", "Unknown");
            } else {
                context.put("down1DayFlag",
                        encodeBooleanDisplay(ap.getHasSystemBeenDown1DayFlag()));
            }

            if (ap.getInstitutionName() == null) {
                context.put("institutionName", "Unknown");
            } else {
                context.put("institutionName", ap.getInstitutionName());
            }

            if (ap.getExtimatedPopulation() != null) {
                context.put("estimatedPopulation", ap.getExtimatedPopulation());
            } else {
                context.put("estimatedPopulation", "null");
            }

            if (ap.getConstructionDateYear() == null
                    || ap.getConstructionDateYear().trim().equals("")) {
                context.put("constructionDateOfWaterPoint", "Unknown");
            } else {
                String constructionDateYear = ap.getConstructionDateYear();
                if (constructionDateYear.contains(".0")) {
                    constructionDateYear = constructionDateYear.replace(".0",
                            "");
                }
                context.put("constructionDateOfWaterPoint",
                        constructionDateYear);
            }
            if (ap.getNumberOfHouseholdsUsingPoint() != null) {
                context.put("numberOfHouseholdsUsingWaterPoint",
                        ap.getNumberOfHouseholdsUsingPoint());
            } else {
                context.put("numberOfHouseholdsUsingWaterPoint", "null");
            }
            if (ap.getCostPer() == null) {
                context.put("costPer", "N/A");
            } else {
                context.put("costPer", ap.getCostPer());
            }
            if (ap.getFarthestHouseholdfromPoint() == null
                    || ap.getFarthestHouseholdfromPoint().trim().equals("")) {
                context.put("farthestHouseholdfromWaterPoint", "N/A");
            } else {
                context.put("farthestHouseholdfromWaterPoint",
                        ap.getFarthestHouseholdfromPoint());
            }
            if (ap.getCurrentManagementStructurePoint() == null) {
                context.put("currMgmtStructure", "N/A");
            } else {
                context.put("currMgmtStructure",
                        ap.getCurrentManagementStructurePoint());
            }
            if (ap.getPointPhotoCaption() == null
                    || ap.getPointPhotoCaption().trim().equals("")) {
                context.put("waterPointPhotoCaption", defaultPhotoCaption);
            } else {
                context.put("waterPointPhotoCaption", ap.getPointPhotoCaption());
            }
            if (ap.getCommunityName() == null) {
                context.put("communityName", "Unknown");
            } else {
                context.put("communityName", ap.getCommunityName());
            }

            if (ap.getHeader() == null) {
                context.put("header", "Water For People");
            } else {
                context.put("header", ap.getHeader());
            }

            if (ap.getFooter() == null) {
                context.put("footer", "Water For People");
            } else {
                context.put("footer", ap.getFooter());
            }

            if (ap.getPhotoName() == null) {
                context.put("photoName", "Water For People");
            } else {
                context.put("photoName", ap.getPhotoName());
            }

            // if (ap.getCountryCode() == "RW") {

            if (ap.getMeetGovtQualityStandardFlag() == null) {
                context.put("meetGovtQualityStandardFlag", "N/A");
            } else {
                context.put("meetGovtQualityStandardFlag",
                        encodeBooleanDisplay(ap
                                .getMeetGovtQualityStandardFlag()));
            }
            // } else {
            // context.put("meetGovtQualityStandardFlag", "unknown");
            // }
            if (ap.getMeetGovtQuantityStandardFlag() == null) {
                context.put("meetGovtQuantityStandardFlag", "N/A");
            } else {
                context.put("meetGovtQuantityStandardFlag",
                        encodeBooleanDisplay(ap
                                .getMeetGovtQuantityStandardFlag()));
            }

            if (ap.getWhoRepairsPoint() == null) {
                context.put("whoRepairsPoint", "N/A");
            } else {
                context.put("whoRepairsPoint", ap.getWhoRepairsPoint());
            }

            if (ap.getSecondaryTechnologyString() == null) {
                context.put("secondaryTypeTechnology", "N/A");
            } else {
                context.put("secondaryTypeTechnology",
                        ap.getSecondaryTechnologyString());
            }

            if (ap.getProvideAdequateQuantity() == null) {
                context.put("provideAdequateQuantity", "N/A");
            } else {
                context.put("provideAdequateQuantity",
                        encodeBooleanDisplay(ap.getProvideAdequateQuantity()));
            }

            if (ap.getBalloonTitle() == null) {
                context.put("title", "Water For People");
            } else {
                context.put("title", ap.getBalloonTitle());
            }

            if (ap.getProvideAdequateQuantity() == null) {
                context.put("provideAdequateQuantity", "N/A");
            } else {
                context.put("provideAdequateQuantity",
                        encodeBooleanDisplay(ap.getProvideAdequateQuantity()));
            }

            if (ap.getQualityDescription() != null) {
                context.put("qualityDescription", ap.getQualityDescription());
            }
            if (ap.getQuantityDescription() != null) {
                context.put("quantityDescription", ap.getQuantityDescription());
            }

            if (ap.getSub1() != null) {
                context.put("sub1", ap.getSub1());
            }
            if (ap.getSub2() != null) {
                context.put("sub2", ap.getSub2());
            }
            if (ap.getSub3() != null) {
                context.put("sub3", ap.getSub3());
            }
            if (ap.getSub4() != null) {
                context.put("sub4", ap.getSub4());
            }
            if (ap.getSub5() != null) {
                context.put("sub5", ap.getSub5());
            }
            if (ap.getSub6() != null) {
                context.put("sub6", ap.getSub6());
            }

            if (ap.getAccessPointCode() != null) {
                context.put("accessPointCode", ap.getAccessPointCode());
            }

            if (ap.getAccessPointUsage() != null) {
                context.put("accessPointUsage", ap.getAccessPointUsage());
            }

            if (ap.getDescription() != null)
                context.put("description", ap.getDescription());
            else
                context.put("description", "Unknown");

            // Need to check this
            if (ap.getPointType() != null) {
                if (Boolean.parseBoolean(PropertyUtil
                        .getProperty(DYNAMIC_SCORING_FLAG))) {
                    TreeMap<String, String> combinedScore = fetchLevelOfServiceScoreStatus(ap);
                    for (Map.Entry<String, String> entry : combinedScore
                            .entrySet()) {
                        context.put(entry.getKey(), entry.getValue());
                        String style = null;
                        if (standardType != null) {
                            if (standardType
                                    .equals(StandardType.WaterPointLevelOfService)
                                    && entry.getKey()
                                            .equals(StandardType.WaterPointLevelOfService
                                                    .toString() + "-pinStyle")) {
                                style = entry.getValue();
                            } else if (standardType
                                    .equals(StandardType.WaterPointSustainability)
                                    && entry.getKey()
                                            .equals(StandardType.WaterPointSustainability
                                                    .toString() + "-pinStyle")) {
                                style = entry.getValue();
                            }
                        }
                        context.put("pinStyle", style);
                    }
                } else {
                    encodeStatusString(ap, context);
                    context.put(
                            "pinStyle",
                            encodePinStyle(ap.getPointType(),
                                    ap.getPointStatus()));
                }
            } else {
                context.put("pinStyle", "waterpushpinblk");
            }
            String output = mergeContext(context, vmName);
            context = null;
            return output;

        }
        return null;

    }

    private TreeMap<String, String> fetchLevelOfServiceScoreStatus(
            AccessPoint ap) {
        TreeMap<String, String> losStyles = new TreeMap<String, String>();
        LevelOfServiceScoreDao losScoreDao = new LevelOfServiceScoreDao();
        List<LevelOfServiceScore> losList = losScoreDao.listByAccessPoint(ap
                .getKey());
        LOSScoreToStatusMappingDao losScoreToStatusMappingDao = new LOSScoreToStatusMappingDao();
        for (LevelOfServiceScore losItem : losList) {
            LOSScoreToStatusMapping losScoreToStatusMapping = losScoreToStatusMappingDao
                    .findByLOSScoreTypeAndScore(losItem.getScoreType(),
                            losItem.getScore());
            losStyles.put(losScoreToStatusMapping.getLevelOfServiceScoreType()
                    .toString() + "-color", losScoreToStatusMapping.getColor()
                    .toString());
            losStyles.put(losScoreToStatusMapping.getLevelOfServiceScoreType()
                    .toString() + "-desc",
                    losScoreToStatusMapping.getDescription());
            losStyles.put(losScoreToStatusMapping.getLevelOfServiceScoreType()
                    .toString() + "-score", losItem.getScore().toString());
            losStyles.put(losScoreToStatusMapping.getLevelOfServiceScoreType()
                    .toString() + "-pinstyle",
                    losScoreToStatusMapping.getIconStyle());
            losStyles.put(losScoreToStatusMapping.getLevelOfServiceScoreType()
                    .toString() + "-iconSmallUrl",
                    losScoreToStatusMapping.getIconSmallUrl());
            // losStyles.put(losScoreToStatusMapping.getLevelOfServiceScoreType().toString()+"details",
            // losItem.getScoreDetails().toString());

        }
        return losStyles;
    }

    public String generateRegionOutlines(String vmName) {
        StringBuilder sb = new StringBuilder();
        GeoRegionDAO grDAO = new GeoRegionDAO();
        List<GeoRegion> grList = grDAO.list();
        try {
            if (grList != null && grList.size() > 0) {
                String currUUID = grList.get(0).getUuid();
                VelocityContext context = new VelocityContext();
                StringBuilder sbCoor = new StringBuilder();

                // loop through GeoRegions and bind to variables
                for (int i = 0; i < grList.size(); i++) {
                    GeoRegion gr = grList.get(i);

                    if (currUUID.equals(gr.getUuid())) {
                        sbCoor.append(gr.getLongitude() + ","
                                + gr.getLatitiude() + "," + 0 + "\n");
                    } else {
                        currUUID = gr.getUuid();
                        context.put("coordinateString", sbCoor.toString());
                        sb.append(mergeContext(context, vmName));

                        context = new VelocityContext();
                        sbCoor = new StringBuilder();
                        sbCoor.append(gr.getLongitude() + ","
                                + gr.getLatitiude() + "," + 0 + "\n");
                    }
                }

                context.put("coordinateString", sbCoor.toString());
                sb.append(mergeContext(context, vmName));
                return sb.toString();
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error generating region outlines", e);
        }
        return "";
    }

    private String encodeBooleanDisplay(Boolean value) {
        if (value) {
            return "Yes";
        } else {
            return "No";
        }
    }

    public LOSScoreToStatusMapping encodePinStyle(Key accessPointKey,
            StandardType standardType) {
        LevelOfServiceScoreDao losScoreDao = new LevelOfServiceScoreDao();
        LevelOfServiceScore losScore = losScoreDao.findByAccessPoint(
                accessPointKey, standardType);
        LOSScoreToStatusMappingDao losMapDao = new LOSScoreToStatusMappingDao();
        LOSScoreToStatusMapping losMapItem = losMapDao
                .findByLOSScoreTypeAndScore(standardType, losScore.getScore());
        return losMapItem;
    }

    public static String encodePinStyle(AccessPointType type,
            AccessPoint.Status status) {
        String prefix = "water";
        if (AccessPointType.SANITATION_POINT == type) {
            prefix = "sani";
        } else if (AccessPointType.SCHOOL == type) {
            prefix = "schwater";
        } else if (AccessPointType.PUBLIC_INSTITUTION == type
                || AccessPointType.HEALTH_POSTS == type) {
            prefix = "pubwater";
        }
        if (AccessPoint.Status.FUNCTIONING_HIGH == status) {
            return prefix + "pushpingreen";
        } else if (AccessPoint.Status.FUNCTIONING_OK == status
                || AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS == status) {
            return prefix + "pushpinyellow";
        } else if (AccessPoint.Status.BROKEN_DOWN == status) {
            return prefix + "pushpinred";
        } else if (AccessPoint.Status.NO_IMPROVED_SYSTEM == status) {
            return prefix + "pushpinblk";
        } else {
            return prefix + "pushpinblk";
        }
    }

    public static String encodePinStyle(String type, String status) {
        String prefix = "water";
        if (type != null) {
            if ("SanitationPoint".equalsIgnoreCase(type)) {
                prefix = "sani";
            } else if ("School".equalsIgnoreCase(type)) {
                prefix = "schwater";
            } else if ("PublicInstitution".equalsIgnoreCase(type)) {
                prefix = "pubwater";
            }
        }

        if ("FUNCTIONING_HIGH".equalsIgnoreCase(status)) {
            return prefix + "pushpingreen";
        } else if ("FUNCTIONING_OK".equalsIgnoreCase(status)
                || "FUNCTIONING_WITH_PROBLEMS".equalsIgnoreCase(status)) {
            return prefix + "pushpinyellow";
        } else if ("BROKEN_DOWN".equalsIgnoreCase(status)) {
            return prefix + "pushpinred";
        } else if ("NO_IMPROVED_SYSTEM".equalsIgnoreCase(status)) {
            return prefix + "pushpinblk";
        } else {
            return prefix + "pushpinblk";
        }
    }

    private String encodeStatusString(AccessPoint ap, VelocityContext context) {
        AccessPoint.Status status = ap.getPointStatus();
        if (ap.getCollectionDate() == null
                || ap.getCollectionDate().before(new Date("01/01/2011"))
                || !useScore) {
            if (status != null) {
                if (AccessPoint.Status.FUNCTIONING_HIGH == status) {
                    context.put("waterSystemStatus",
                            "Meets Government Standards");
                    return "System Functioning and Meets Government Standards";
                } else if (AccessPoint.Status.FUNCTIONING_OK == status
                        || AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS == status) {
                    context.put("waterSystemStatus",
                            "Functioning but with Problems");
                    return "Functioning but with Problems";
                } else if (AccessPoint.Status.BROKEN_DOWN == status) {
                    context.put("waterSystemStatus", "Broken-down system");
                    return "Broken-down system";
                } else if (AccessPoint.Status.NO_IMPROVED_SYSTEM == status) {
                    context.put("waterSystemStatus", "No Improved System");
                    return "No Improved System";
                } else {
                    context.put("waterSystemStatus", "Unknown");
                    return "Unknown";
                }
            } else {
                context.put("waterSystemStatus", "Unknown");
                return "Unknown";
            }
        } else {
            String statusString = null;
            try {
                statusString = encodeStatusUsingScore(ap);
            } catch (Exception ex) {
                log.log(Level.INFO, "Couldn't score  ap: " + ap.toString()
                        + " " + ex);
            }
            if (statusString == null) {
                statusString = "Unknown";
            }
            context.put("waterSystemStatus", statusString);
            // will remove soon not necessary now that APs are getting scored on
            // save
            AccessPointDao apDao = new AccessPointDao();
            apDao.save(ap);
            return statusString;
        }
    }

    private String encodeStatusString(AccessPoint.Status status) {
        if (status == null) {
            return "Unknown";
        }
        if (status.equals(AccessPoint.Status.FUNCTIONING_HIGH)) {
            return "System Functioning and Meets Government Standards";
        } else if (status.equals(AccessPoint.Status.FUNCTIONING_OK)
                || status.equals(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS)) {
            return "Functioning but with Problems";
        } else if (status.equals(AccessPoint.Status.BROKEN_DOWN)) {
            return "Broken-down system";
        } else if (status.equals(AccessPoint.Status.NO_IMPROVED_SYSTEM)) {
            return "No Improved System";
        } else {
            return "Unknown";
        }
    }

    public String encodeStatusUsingScore(AccessPoint ap)
            throws InvocationTargetException, NoSuchMethodException {
        Integer score = AccessPointHelper.scoreAccessPoint(ap).getScore();

        new AccessPointHelper().scoreAccessPointDynamic(ap).getScore();

        if (score == 0) {
            return "No Improved System";
        } else if (score >= 1 && score <= 2) {
            return "Basic Level Service";
        } else if (score >= 3 && score <= 4) {
            return "Intermediate Level Service";
        } else if (score >= 5) {
            return "High Level Service";
        } else {
            return "Unknown";
        }
    }
}
