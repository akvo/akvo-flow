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

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.TechnologyType;

import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.gis.geography.domain.Country;
import com.gallatinsystems.gis.map.dao.MapControlDao;
import com.gallatinsystems.gis.map.dao.MapFragmentDao;
import com.gallatinsystems.gis.map.domain.MapControl;
import com.gallatinsystems.gis.map.domain.MapFragment;
import com.gallatinsystems.gis.map.domain.MapFragment.FRAGMENTTYPE;
import com.google.appengine.api.datastore.Blob;

public class KMLHelper {
    private static final Logger log = Logger.getLogger(KMLHelper.class
            .getName());

    private VelocityEngine engine;

    public KMLHelper() {
        engine = new VelocityEngine();
        engine.setProperty("runtime.log.logsystem.class",
                "org.apache.velocity.runtime.log.NullLogChute");
        try {
            engine.init();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not initialize velocity", e);
        }
    }

    /**
     * merges a hydrated context with a template identified by the templateName passed in.
     * 
     * @param context
     * @param templateName
     * @return
     * @throws Exception
     */
    private String mergeContext(VelocityContext context, String templateName)
            throws Exception {
        Template t = engine.getTemplate(templateName);
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        return writer.toString();
    }

    public String bindPlacemark(AccessPoint ap, String vmName) throws Exception {
        if (ap.getCountryCode() != null && !ap.getCountryCode().equals("MW")) {
            VelocityContext context = new VelocityContext();
            if (ap.getCollectionDate() != null) {
                String formattedDate = DateFormat.getDateInstance(
                        DateFormat.SHORT).format(ap.getCollectionDate());
                context.put("collectionDate", formattedDate);
            } else {
                context.put("collectionDate", "N/A");
            }

            context.put("latitude", ap.getLatitude());
            context.put("longitude", ap.getLongitude());
            context.put("altitude", ap.getAltitude());

            if (ap.getCommunityCode() != null)
                context.put("communityCode", ap.getCommunityCode());
            else
                context.put("communityCode", "Unknown" + new Date());

            if (ap.getPhotoURL() != null)
                context.put("photoUrl", ap.getPhotoURL());
            else
                context
                        .put("photoUrl",
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
                context.put("primaryTypeTechnology", ap
                        .getTypeTechnologyString());
            }

            if (ap.getHasSystemBeenDown1DayFlag() == null) {
                context.put("down1DayFlag", "Unknown");
            } else {
                context.put("down1DayFlag", encodeBooleanDisplay(ap
                        .getHasSystemBeenDown1DayFlag()));
            }

            if (ap.getInstitutionName() == null) {
                context.put("institutionName", "Unknown");
            } else {
                context.put("institutionName", "Unknown");
            }

            if (ap.getConstructionDateYear() == null
                    || ap.getConstructionDateYear().trim().equals("")) {
                context.put("constructionDateOfWaterPoint", "Unknown");
            } else {
                context.put("constructionDateOfWaterPoint", ap
                        .getConstructionDateYear());
            }
            if (ap.getNumberOfHouseholdsUsingPoint() == null) {
                context.put("numberOfHouseholdsUsingWaterPoint", "Unknown");
            } else {
                context.put("numberOfHouseholdsUsingWaterPoint", ap
                        .getNumberOfHouseholdsUsingPoint());
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
                context.put("farthestHouseholdfromWaterPoint", ap
                        .getFarthestHouseholdfromPoint());
            }
            if (ap.getCurrentManagementStructurePoint() == null) {
                context.put("currMgmtStructure", "N/A");
            } else {
                context.put("currMgmtStructure", ap
                        .getCurrentManagementStructurePoint());
            }
            if (ap.getPointPhotoCaption() == null
                    || ap.getPointPhotoCaption().trim().equals("")) {
                context.put("waterPointPhotoCaption", "Water For People");
            } else {
                context
                        .put("waterPointPhotoCaption", ap
                                .getPointPhotoCaption());
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

            if (ap.getMeetGovtQualityStandardFlag() == null) {
                context.put("meetGovtQualityStandardFlag", "N/A");
            } else {
                context.put("meetGovtQualityStandardFlag",
                        encodeBooleanDisplay(ap
                                .getMeetGovtQualityStandardFlag()));
            }
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
                context.put("secondaryTypeTechnology", ap
                        .getSecondaryTechnologyString());
            }

            if (ap.getProvideAdequateQuantity() == null) {
                context.put("provideAdequateQuantity", "N/A");
            } else {
                context.put("provideAdequateQuantity", encodeBooleanDisplay(ap
                        .getProvideAdequateQuantity()));
            }

            if (ap.getBalloonTitle() == null) {
                context.put("title", "Water For People");
            } else {
                context.put("title", ap.getBalloonTitle());
            }

            if (ap.getProvideAdequateQuantity() == null) {
                context.put("provideAdequateQuantity", "N/A");
            } else {
                context.put("provideAdequateQuantity", encodeBooleanDisplay(ap
                        .getProvideAdequateQuantity()));
            }

            if (ap.getDescription() != null)
                context.put("description", ap.getDescription());
            else
                context.put("description", "Unknown");

            // Need to check this
            if (ap.getPointType() != null)
                encodeStatus(ap.getPointType(), ap.getPointStatus(), context);
            else {
                context.put("pinStyle", "pushpinblk");
            }
            String output = mergeContext(context, vmName);
            return output;
        }
        return null;

    }

    private String encodeBooleanDisplay(Boolean value) {
        if (value) {
            return "Yes";
        } else {
            return "No";
        }
    }

    private void encodeStatus(AccessPointType type, AccessPoint.Status status,
            VelocityContext context) {
        if (type.equals(AccessPointType.SANITATION_POINT)) {
            context.put("pinStyle", "pushpinpurple");
        } else {
            if (status.equals(AccessPoint.Status.FUNCTIONING_HIGH)) {
                context.put("pinStyle", "pushpingreen");
            } else if (status.equals(AccessPoint.Status.FUNCTIONING_OK)) {
                context.put("pinStyle", "pushpinyellow");
            } else if (status
                    .equals(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS)) {
                context.put("pinStyle", "pushpinred");
            } else if (status.equals(AccessPoint.Status.NO_IMPROVED_SYSTEM)) {
                context.put("pinStyle", "pushpinblk");
            } else {
                context.put("pinStyle", "pushpinblk");
            }
        }
        encodeStatusString(status, context);
    }

    private String encodeStatusString(AccessPoint.Status status,
            VelocityContext context) {
        if (status != null) {
            if (status.equals(AccessPoint.Status.FUNCTIONING_HIGH)) {
                context.put("waterSystemStatus",
                        "System Functioning and Meets Government Standards");
                return "System Functioning and Meets Government Standards";
            } else if (status.equals(AccessPoint.Status.FUNCTIONING_OK)) {
                context.put("waterSystemStatus",
                        "Functioning but with Problems");
                return "Functioning but with Problems";
            } else if (status
                    .equals(AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS)) {
                context.put("waterSystemStatus", "Broken-down system");
                return "Broken-down system";
            } else if (status.equals(AccessPoint.Status.NO_IMPROVED_SYSTEM)) {
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
    }

    private String generateFolderContents(String countryCode, String techType,
            String mapFragmentText) throws Exception {
        VelocityContext context = new VelocityContext();
        StringBuilder techFolders = new StringBuilder();
        context.put("techFolderName", techType);
        context.put("techPlacemarks", mapFragmentText);
        techFolders.append(mergeContext(context, "techFolders.vm"));
        return techFolders.toString();
    }

    public void buildMap() {
        // Select all individual placemarks, but techtype and country
        // Save Each tech type to MapFragment
        // Select all Placemarks by techType order by techType for a country
        // bind to folder vm
        // Save a complete country order by countryName
        // Save complete kml to mapfragment
        // Save to s3?
        BaseDAO<Country> countryDao = new BaseDAO<Country>(Country.class);

        MapControl mc = new MapControl();

        mc.setStartDate(new Date());
        List<Country> countryList = countryDao.list("all");
        if (countryList != null)
            for (Country country : countryList) {
                if (country != null) {

                    /*
                     * Queue mapAssemblyQueue = QueueFactory .getQueue("mapAssembly"); TaskOptions
                     * task = url("/app_worker/mapassembly").param( "action",
                     * Constants.BUILD_COUNTRY_FRAGMENTS).param( "countryCode",
                     * country.getIsoAlpha2Code()); mapAssemblyQueue.add(task);
                     */
                }
            }
    }

    public void buildCountryFragments(String countryCode) {
        BaseDAO<TechnologyType> techTypeDao = new BaseDAO<TechnologyType>(
                TechnologyType.class);
        List<TechnologyType> techTypeList = techTypeDao.list("all");
        if (countryCode != null)
            for (TechnologyType tt : techTypeList) {
                if (tt != null) {
                    /*
                     * Queue mapAssemblyQueue = QueueFactory .getQueue("mapAssembly"); TaskOptions
                     * task = url("/app_worker/mapassembly").param( "action",
                     * Constants.BUILD_COUNTRY_FRAGMENTS).param( "countryCode",
                     * countryCode).param("techType", tt.getCode()); mapAssemblyQueue.add(task);
                     */
                }
            }

    }

    public void assembleCountryTechTypeFragments(String countryCode,
            String techType) {
        MapFragmentDao mfDao = new MapFragmentDao();
        StringBuilder kml = new StringBuilder();
        List<MapFragment> mfList = mfDao.searchMapFragments(countryCode, null,
                null, FRAGMENTTYPE.COUNTRY_TECH_PLACEMARK_LIST, "all",
                null, null);
        StringBuilder sbAllCountryPlacemark = new StringBuilder();

        for (MapFragment mfItem : mfList) {
            try {
                sbAllCountryPlacemark.append(ZipUtil.unZip(mfItem.getBlob()
                        .getBytes()));
            } catch (Exception e) {
                log.log(Level.SEVERE,
                        "Could not assemble country tech type fragments", e);
            }
        }
        VelocityContext context = new VelocityContext();

        context.put("country", countryCode);
        context.put("techFolders", sbAllCountryPlacemark.toString());
        try {
            kml.append(mergeContext(context, "Folders.vm"));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not generate folders", e);
        }

    }

    public void assembleCompleteMap() {
        VelocityContext context = new VelocityContext();
        // context.put("folderContents", kml.toString());
        try {
            String completeKML = mergeContext(context, "Document.vm");
            MapFragment mf = new MapFragment();
            mf.setCountryCode("ALL");
            mf.setFragmentType(FRAGMENTTYPE.GLOBAL_ALL_PLACEMARKS);
            ByteArrayOutputStream bos = ZipUtil.generateZip(completeKML,
                    "waterforpeoplemapping.kml");
            mf.setBlob(new Blob(bos.toByteArray()));
            // mfDao.save(mf);
            // mc.setEndDate(new Date());
            // mc.setStatus(MapControl.Status.SUCCESS);
            // mcDao.save(mc);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not assemble map", e);
            // mc.setEndDate(new Date());
            // mc.setStatus(MapControl.Status.FAILURE);
            // mcDao.save(mc);
        }
    }

    public void buildCountryTechTypeFragment(String countryCode, String techType) {
        StringBuilder sbTechList = new StringBuilder();
        MapFragmentDao mfDao = new MapFragmentDao();
        String mfTechItemsByCountry = null;
        List<MapFragment> mfList = mfDao.listFragmentsByCountryAndTechType(
                countryCode, techType);
        for (MapFragment mfItem : mfList) {
            sbTechList.append(mfItem.getFragmentValue().getValue());
        }
        try {
            mfTechItemsByCountry = generateFolderContents(countryCode,
                    techType, sbTechList.toString());
        } catch (Exception e) {
            // log.Log(LogLevel.ERROR,
            // "Could not generate country tech folders for: " + countryCode +
            // ":" + techType + e);
        }
        MapFragment mf = new MapFragment();
        mf.setFragmentType(FRAGMENTTYPE.COUNTRY_TECH_PLACEMARK_LIST);
        mf.setCountryCode(countryCode);
        mf.setTechnologyType(techType);
        // mf.setFragmentValue(new Text(mfTechItemsByCountry));
        ByteArrayOutputStream os = ZipUtil.generateZip(mfTechItemsByCountry,
                "waterforpeoplemapping.kml");
        Blob blob = new Blob(os.toByteArray());

        mf.setBlob(blob);
        log.log(Level.INFO, "Size of techItemsByCountry: "
                + mfTechItemsByCountry.length());
        mfDao.save(mf);
    }

    public Boolean checkCreateNewMap() {
        Boolean runFlag = false;
        Date lastPointDate = null;
        AccessPointDao apDao = new AccessPointDao();

        List<AccessPoint> apList = apDao.listAccessPointsByDateOrdered(
                "createdDateTime", "desc", null);
        MapControlDao mcDao = new MapControlDao();

        MapControl mc = mcDao.getLatestRunTime();

        if (apList != null)
            if (apList.size() > 0)
                if (apList.get(0) != null)
                    lastPointDate = apList.get(0).getCreatedDateTime();

        if (lastPointDate != null
                && (mc == null || mc.getCreatedDateTime().before(lastPointDate)))
            runFlag = true;

        return runFlag;
    }
}
