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

package org.waterforpeople.mapping.app.web.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.app.web.ScoreProcessor;
import org.waterforpeople.mapping.app.web.TestHarnessServlet;
import org.waterforpeople.mapping.app.web.dto.DeleteTaskRequest;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.dao.CompoundStandardDao;
import com.gallatinsystems.standards.dao.LOSScoreToStatusMappingDao;
import com.gallatinsystems.standards.dao.LevelOfServiceScoreDao;
import com.gallatinsystems.standards.dao.StandardDao;
import com.gallatinsystems.standards.domain.CompoundStandard;
import com.gallatinsystems.standards.domain.CompoundStandard.Operator;
import com.gallatinsystems.standards.domain.CompoundStandard.RuleType;
import com.gallatinsystems.standards.domain.DistanceStandard;
import com.gallatinsystems.standards.domain.LOSScoreToStatusMapping;
import com.gallatinsystems.standards.domain.LOSScoreToStatusMapping.LOSColor;
import com.gallatinsystems.standards.domain.LevelOfServiceScore;
import com.gallatinsystems.standards.domain.Standard;
import com.gallatinsystems.standards.domain.Standard.StandardComparisons;
import com.gallatinsystems.standards.domain.Standard.StandardScope;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.gallatinsystems.standards.domain.Standard.StandardValueType;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class StandardTestLoader {
    private HttpServletRequest req;
    private HttpServletResponse resp;

    private static Logger log = Logger.getLogger(TestHarnessServlet.class
            .getName());

    public StandardTestLoader(HttpServletRequest req, HttpServletResponse resp) {
        this.req = req;
        this.resp = resp;
    }

    public void scoreAllPoints() {
        this.fireAsnycRescoreAllPoints();
    }

    public void loadWaterPointStandard() {
        StandardDao standardDao = new StandardDao();
        // # of Users Standard
        Standard standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointLevelOfService);
        standard.setStandardScope(StandardScope.Local);
        standard.setCountry("BO");
        ArrayList<String> posValues = new ArrayList<String>();
        posValues.add("500");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Number);
        standard.setStandardComparison(StandardComparisons.lessthan);
        standard.setStandardDescription("Estimated Number of Users");
        standard.setAccessPointAttribute("extimatedPopulation");

        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        // hasSystemBeenDown1DayFlag global boolean true=0 false=1
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointLevelOfService);
        standard.setStandardScope(StandardScope.Global);
        standard.setCountry("");
        posValues.removeAll(posValues);
        posValues.add("false");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Boolean);
        standard.setStandardComparison(StandardComparisons.equal);
        standard.setStandardDescription("Has System Been down in last 30 days");
        standard.setAccessPointAttribute("hasSystemBeenDown1DayFlag");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        // provideAdequateQuantity global boolean true=1 flase=0
        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointLevelOfService);
        standard.setStandardScope(StandardScope.Global);
        standard.setCountry("");
        posValues.removeAll(posValues);
        posValues.add("true");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Boolean);
        standard.setStandardComparison(StandardComparisons.equal);
        standard.setStandardDescription("Does the water source provide enough drinking water for the community every day of the year?");
        standard.setAccessPointAttribute("provideAdequateQuantity");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);
        // ppmFecalColiform local double <
        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointLevelOfService);
        standard.setStandardScope(StandardScope.Local);
        standard.setCountry("BO");
        posValues.removeAll(posValues);
        posValues.add("1");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Number);
        standard.setStandardComparison(StandardComparisons.lessthan);
        standard.setStandardDescription("How much fecal coliform were present on the day of collection?");
        standard.setAccessPointAttribute("ppmFecalColiform");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        // numberOfLitersPerPersonPerDay local < govt standard
        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointLevelOfService);
        standard.setStandardScope(StandardScope.Local);
        standard.setCountry("BO");
        posValues.removeAll(posValues);
        posValues.add("10");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Number);
        standard.setStandardComparison(StandardComparisons.greaterthan);
        standard.setStandardDescription("How many liters of water per person per day does this source provide?");
        standard.setAccessPointAttribute("numberOfLitersPerPersonPerDay");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        writeln("Saved: " + standard.toString());

        DistanceStandard ds = new DistanceStandard();
        ds.setAccessPointType(AccessPointType.WATER_POINT);
        ds.setStandardType(StandardType.WaterPointLevelOfService);
        ds.setStandardScope(StandardScope.Local);
        ds.setCountryCode("BO");
        ds.setMaxDistance(100);
        ds.setLocationType(AccessPoint.LocationType.URBAN);
        ds.setStandardDescription("Distance standard for Urban waterpoints");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(ds);

        ds = new DistanceStandard();
        ds.setAccessPointType(AccessPointType.WATER_POINT);
        ds.setStandardType(StandardType.WaterPointLevelOfService);
        ds.setStandardScope(StandardScope.Local);
        ds.setCountryCode("BO");
        ds.setMaxDistance(500);
        ds.setLocationType(AccessPoint.LocationType.RURAL);
        ds.setStandardDescription("Distance standard for Rural waterpoints");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(ds);

        ds = new DistanceStandard();
        ds.setAccessPointType(AccessPointType.WATER_POINT);
        ds.setStandardType(StandardType.WaterPointLevelOfService);
        ds.setStandardScope(StandardScope.Local);
        ds.setCountryCode("BO");
        ds.setMaxDistance(200);
        ds.setLocationType(AccessPoint.LocationType.PERIURBAN);
        ds.setStandardDescription("Distance standard for Peri-Urban waterpoints");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(ds);

        ds = new DistanceStandard();
        ds.setAccessPointType(AccessPointType.WATER_POINT);
        ds.setStandardType(StandardType.WaterPointLevelOfService);
        ds.setStandardScope(StandardScope.Local);
        ds.setCountryCode("BO");
        ds.setMaxDistance(100);
        ds.setLocationType(AccessPoint.LocationType.OTHER);
        ds.setStandardDescription("Distance standard for other than rural, urban, or peri-urban waterpoints");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(ds);

    }

    private void loadWaterPointScoreToStatus() {
        ArrayList<LOSScoreToStatusMapping> losList = new ArrayList<LOSScoreToStatusMapping>();

        LOSScoreToStatusMapping losScoreToStatusMapping = new LOSScoreToStatusMapping();
        losScoreToStatusMapping
                .setLevelOfServiceScoreType(StandardType.WaterPointLevelOfService);
        losScoreToStatusMapping.setFloor(0);
        losScoreToStatusMapping.setCeiling(0);
        losScoreToStatusMapping.setColor(LOSColor.Black);
        losScoreToStatusMapping.setDescription("No Improved System");
        losScoreToStatusMapping
                .setIconLargeUrl("http://watermapmonitordev.appspot.com/images/iconBlack36.png");
        losScoreToStatusMapping
                .setIconSmallUrl("http://watermapmonitordev.appspot.com/images/iconBlack32.png");
        losScoreToStatusMapping.setIconStyle("waterpushpinblack");
        losList.add(losScoreToStatusMapping);

        losScoreToStatusMapping = new LOSScoreToStatusMapping();
        losScoreToStatusMapping
                .setLevelOfServiceScoreType(StandardType.WaterPointLevelOfService);
        losScoreToStatusMapping.setFloor(1);
        losScoreToStatusMapping.setCeiling(1);
        losScoreToStatusMapping.setColor(LOSColor.Red);
        losScoreToStatusMapping.setDescription("Basic Level Of Service");
        losScoreToStatusMapping
                .setIconLargeUrl("http://watermapmonitordev.appspot.com/images/glassRed32.png");
        losScoreToStatusMapping
                .setIconSmallUrl("http://watermapmonitordev.appspot.com/images/glassRed32.png");
        losScoreToStatusMapping.setIconStyle("waterpushpinred");
        losList.add(losScoreToStatusMapping);

        losScoreToStatusMapping = new LOSScoreToStatusMapping();
        losScoreToStatusMapping
                .setLevelOfServiceScoreType(StandardType.WaterPointLevelOfService);
        losScoreToStatusMapping.setFloor(2);
        losScoreToStatusMapping.setCeiling(5);
        losScoreToStatusMapping.setColor(LOSColor.Yellow);
        losScoreToStatusMapping.setDescription("Intermediate Level Of Service");
        losScoreToStatusMapping
                .setIconLargeUrl("http://watermapmonitordev.appspot.com/images/glassOrange32.png");
        losScoreToStatusMapping
                .setIconSmallUrl("http://watermapmonitordev.appspot.com/images/glassOrange32.png");
        losScoreToStatusMapping.setIconStyle("waterpushpinyellow");
        losList.add(losScoreToStatusMapping);

        losScoreToStatusMapping = new LOSScoreToStatusMapping();
        losScoreToStatusMapping
                .setLevelOfServiceScoreType(StandardType.WaterPointLevelOfService);
        losScoreToStatusMapping.setFloor(6);
        losScoreToStatusMapping.setCeiling(8);
        losScoreToStatusMapping.setColor(LOSColor.Green);
        losScoreToStatusMapping.setDescription("High Level Of Service");
        losScoreToStatusMapping
                .setIconLargeUrl("http://watermapmonitordev.appspot.com/images/glassGreen32.png");
        losScoreToStatusMapping
                .setIconSmallUrl("http://watermapmonitordev.appspot.com/images/glassGreen32.png");
        losScoreToStatusMapping.setIconStyle("waterpushpingreen");
        losList.add(losScoreToStatusMapping);

        BaseDAO<LOSScoreToStatusMapping> losBaseDao = new BaseDAO<LOSScoreToStatusMapping>(
                LOSScoreToStatusMapping.class);
        losBaseDao.save(losList);

        losList = new ArrayList<LOSScoreToStatusMapping>();

        losScoreToStatusMapping = new LOSScoreToStatusMapping();
        losScoreToStatusMapping
                .setLevelOfServiceScoreType(StandardType.WaterPointSustainability);
        losScoreToStatusMapping.setFloor(0);
        losScoreToStatusMapping.setCeiling(0);
        losScoreToStatusMapping.setColor(LOSColor.Black);
        losScoreToStatusMapping.setDescription("No Improved System");
        losScoreToStatusMapping
                .setIconLargeUrl("http://watermapmonitordev.appspot.com/images/iconBlack36.png");
        losScoreToStatusMapping
                .setIconSmallUrl("http://watermapmonitordev.appspot.com/images/iconBlack32.png");
        losScoreToStatusMapping.setIconStyle("waterpushpinblack");
        losList.add(losScoreToStatusMapping);

        losScoreToStatusMapping = new LOSScoreToStatusMapping();
        losScoreToStatusMapping
                .setLevelOfServiceScoreType(StandardType.WaterPointSustainability);
        losScoreToStatusMapping.setFloor(1);
        losScoreToStatusMapping.setCeiling(1);
        losScoreToStatusMapping.setColor(LOSColor.Red);
        losScoreToStatusMapping.setDescription("Unlikely to be Sustainable");
        losScoreToStatusMapping
                .setIconLargeUrl("http://watermapmonitordev.appspot.com/images/iconRed36.png");
        losScoreToStatusMapping
                .setIconSmallUrl("http://watermapmonitordev.appspot.com/images/iconRed32.png");
        losScoreToStatusMapping.setIconStyle("waterpushpinred");
        losList.add(losScoreToStatusMapping);

        losScoreToStatusMapping = new LOSScoreToStatusMapping();
        losScoreToStatusMapping
                .setLevelOfServiceScoreType(StandardType.WaterPointSustainability);
        losScoreToStatusMapping.setFloor(2);
        losScoreToStatusMapping.setCeiling(5);
        losScoreToStatusMapping.setColor(LOSColor.Yellow);
        losScoreToStatusMapping.setDescription("Likely to be Sustainable");
        losScoreToStatusMapping
                .setIconLargeUrl("http://watermapmonitordev.appspot.com/images/iconYellow36.png");
        losScoreToStatusMapping
                .setIconSmallUrl("http://watermapmonitordev.appspot.com/images/iconYellow32.png");
        losScoreToStatusMapping.setIconStyle("waterpushpinyellow");
        losList.add(losScoreToStatusMapping);

        losScoreToStatusMapping = new LOSScoreToStatusMapping();
        losScoreToStatusMapping
                .setLevelOfServiceScoreType(StandardType.WaterPointSustainability);
        losScoreToStatusMapping.setFloor(6);
        losScoreToStatusMapping.setCeiling(8);
        losScoreToStatusMapping.setColor(LOSColor.Green);
        losScoreToStatusMapping
                .setDescription("Highly Likely to be Sustainable");
        losScoreToStatusMapping
                .setIconLargeUrl("http://watermapmonitordev.appspot.com/images/glassGreen32.png");
        losScoreToStatusMapping
                .setIconSmallUrl("http://watermapmonitordev.appspot.com/images/glassGreen32.png");
        losScoreToStatusMapping.setIconStyle("waterpushpingreen");
        losList.add(losScoreToStatusMapping);

        // //&& ap.getImprovedWaterPointFlag()

        losBaseDao.save(losList);
    }

    public void setReq(HttpServletRequest req) {
        this.req = req;
    }

    public HttpServletRequest getReq() {
        return req;
    }

    private void writeln(String message) {
        try {
            log.info(message);
            resp.getWriter().println(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void write(String message) {
        try {
            log.info(message);
            resp.getWriter().print(message);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void runTest() {
        clearAPs();
        loadWaterPointStandard();
        loadWaterPointSustainability();
        loadWaterPointScoreToStatus();
        AccessPointTest apt = new AccessPointTest();
        apt.loadLots(resp, 50);
        // scoreAllPoints();
    }

    private void clearAPs() {
        DeleteObjectUtil dou = new DeleteObjectUtil();
        // dou.deleteAllObjects("AccessPoint");
        // writeln("Deleted APs");
        // dou.deleteAllObjects("AccessPointScoreComputationItem");
        // writeln("Deleted APSCI");
        // dou.deleteAllObjects("AccessPointScoreDetail");
        // writeln("Deleted APSD");
        // dou.deleteAllObjects("AccessPointsStatusSummary");
        // writeln("Deleted AccessPointsStatusSummary");
        dou.deleteAllObjects("Standard");
        writeln("Deleted All the Standards");
        dou.deleteAllObjects("LevelOfServiceScore");
        writeln("Deleted All the LevelOfServiceScore");
        dou.deleteAllObjects("LOSScoreToStatusMapping");
        writeln("Deleted All LevelOfServiceScoreToStatusMappings");
        dou.deleteAllObjects("DistanceStandard");
        writeln("Deleted All DistanceStandards");
        dou.deleteAllObjects("CompoundStandard");
        writeln("Deleted All CompoundStandards");
    }

    public void listResults(String countryCode, String communityCode,
            String accessPointCode, String cursorString) {
        listAPScoreAndStatus(countryCode, communityCode, accessPointCode,
                cursorString);
    }

    private void listAPScoreAndStatus(String countryCode, String communityCode,
            String accessPointCode, String cursorString) {
        AccessPointDao apDao = new AccessPointDao();
        // List<AccessPoint> apList = apDao.list("all");

        LevelOfServiceScoreDao lesScoreDao = new LevelOfServiceScoreDao();
        writeln("<html><table border=1>");
        write("<tr><td>AccessPoint Key</td><td>Country Code</td><td>Community Name</td><td>Access Point Code</td><td>Access Point Collection Date</td><td>Number HH within Acceptable Distance</td>"
                + "<td>Number Outside Acceptable Distance</td><td>LOS Score</td><td>ScoreDate</td><td>status color</td><td>Score Status String</td><td>Score Details</td>"
                + "<td>Sustainability Score</td><td>ScoreDate</td><td>status color</td><td>Score Status String</td><td>Score Details</td></tr>");
        Iterable<Entity> entList = null;
        entList = apDao.listRawEntity(false, countryCode, communityCode,
                accessPointCode, cursorString);
        // for (AccessPoint item : extent) {
        for (Entity result : entList) {
            AccessPoint item = new AccessPoint();
            item.setKey(result.getKey());
            item.setCommunityCode((String) result.getProperty("communityCode"));
            item.setCollectionDate((Date) result.getProperty("collectionDate"));
            List<LevelOfServiceScore> losScoreList = lesScoreDao
                    .listByAccessPoint(item.getKey());
            write("<tr><td>" + item.getKeyString() + "</td><td>"
                    + item.getCountryCode() + "</td><td>"
                    + item.getCommunityName() + "</td><td>"
                    + item.getAccessPointCode() + "</td><td>"
                    + item.getCollectionDate() + "</td><td>"
                    + item.getNumberWithinAcceptableDistance() + " </td><td> "
                    + item.getNumberWithinAcceptableDistance() + "</td>");
            for (LevelOfServiceScore losItem : losScoreList) {
                LOSScoreToStatusMappingDao losMap = new LOSScoreToStatusMappingDao();
                LOSScoreToStatusMapping losMapItem = losMap
                        .findByLOSScoreTypeAndScore(losItem.getScoreType(),
                                losItem.getScore());
                if (losMapItem != null) {
                    write("<td>" + losItem.getScore() + "</td><td>"
                            + losItem.getLastUpdateDateTime() + "</td><td>"
                            + losMapItem.getColor() + "</td><td>"
                            + losMapItem.getLevelOfServiceScoreType() + "</td>");
                }
                write("<td>");
                for (String detail : losItem.getScoreDetails()) {
                    write(" " + detail + "<br>");
                }
                write("</td>");
            }
            write("</tr>");
        }
        writeln("</table></html>");
    }

    private void loadWaterPointSustainability() {
        StandardDao standardDao = new StandardDao();
        // Water Available Day of Visit
        Standard standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointSustainability);
        standard.setStandardScope(StandardScope.Global);
        ArrayList<String> posValues = new ArrayList<String>();
        posValues.add("true");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Boolean);
        standard.setStandardComparison(StandardComparisons.equal);
        standard.setStandardDescription("Water Available day of visit");
        standard.setAccessPointAttribute("waterAvailableDayVisitFlag");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointSustainability);
        standard.setStandardScope(StandardScope.Global);
        posValues = new ArrayList<String>();
        posValues.add("true");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Boolean);
        standard.setStandardComparison(StandardComparisons.equal);
        standard.setStandardDescription("Is there a tariff or user fee");
        standard.setAccessPointAttribute("collectTariffFlag");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointSustainability);
        standard.setStandardScope(StandardScope.Global);
        posValues = new ArrayList<String>();
        posValues.add("true");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Boolean);
        standard.setStandardComparison(StandardComparisons.equal);
        standard.setStandardDescription("Are there financial records");
        standard.setAccessPointAttribute("financialRecordsAvailableDayOfVisitFlag");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointSustainability);
        standard.setStandardScope(StandardScope.Global);
        posValues = new ArrayList<String>();
        posValues.add("true");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Boolean);
        standard.setStandardComparison(StandardComparisons.equal);
        standard.setStandardDescription("Are there financial records");
        standard.setAccessPointAttribute("positiveBalance");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointSustainability);
        standard.setStandardScope(StandardScope.Global);
        posValues = new ArrayList<String>();
        posValues.add("true");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Boolean);
        standard.setStandardComparison(StandardComparisons.equal);
        standard.setStandardDescription("Are there financial records");
        standard.setAccessPointAttribute("positiveBalance");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointSustainability);
        standard.setStandardScope(StandardScope.Global);
        posValues = new ArrayList<String>();
        posValues.add("No One");
        posValues.add("Don't Know");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.String);
        standard.setStandardComparison(StandardComparisons.notequal);
        standard.setStandardDescription("Who is responsible for performing maintenance");
        standard.setAccessPointAttribute("whoRepairsPoint");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointSustainability);
        standard.setStandardScope(StandardScope.Global);
        posValues = new ArrayList<String>();
        posValues.add("No");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.String);
        standard.setStandardComparison(StandardComparisons.equal);
        standard.setStandardDescription("Current Problems");
        standard.setAccessPointAttribute("whoRepairsPoint");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointSustainability);
        standard.setStandardScope(StandardScope.Global);
        posValues = new ArrayList<String>();
        posValues.add("Yes");
        posValues.add("Don't Know");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.String);
        standard.setStandardComparison(StandardComparisons.equal);
        standard.setStandardDescription("System Support Expansion");
        standard.setAccessPointAttribute("systemExpansion");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);

        CompoundStandard cs = new CompoundStandard();
        cs.setStandardType(StandardType.WaterPointSustainability);
        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointSustainability);
        standard.setStandardScope(StandardScope.Global);
        cs.setStandardLeftRuleType(RuleType.NONDISTANCE);
        cs.setStandardRightRuleType(RuleType.NONDISTANCE);
        posValues = new ArrayList<String>();
        posValues.add("true");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Boolean);
        standard.setStandardComparison(StandardComparisons.equal);
        standard.setStandardDescription("Spare Parts on Hand");
        standard.setAccessPointAttribute("sparePartsOnHand");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);
        cs.setStandardLeft(standard);
        cs.setStandardIdLeft(standard.getKey().getId());

        standard = new Standard();
        standard.setAccessPointType(AccessPointType.WATER_POINT);
        standard.setStandardType(StandardType.WaterPointSustainability);
        standard.setStandardScope(StandardScope.Global);
        posValues = new ArrayList<String>();
        posValues.add("true");
        standard.setPositiveValues(posValues);
        standard.setAcessPointAttributeType(StandardValueType.Boolean);
        standard.setStandardComparison(StandardComparisons.equal);
        standard.setStandardDescription("Local Spare Parts");
        standard.setAccessPointAttribute("localSparePartsFlag");
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        cs.setStandardRight(standard);
        CompoundStandardDao csDao = new CompoundStandardDao();
        standard.setEffectiveStartDate(new GregorianCalendar(1990, 0, 1).getTime());
        standard.setEffectiveEndDate(new GregorianCalendar(2013, 0, 1).getTime());
        standardDao.save(standard);
        cs.setStandardIdRight(standard.getKey().getId());
        cs.setOperator(Operator.OR);
        csDao.save(cs);

    }

    private void fireAsnycRescoreAllPoints() {
        Queue rescoreQueue = QueueFactory
                .getQueue(ScoreProcessor.ACCESSPOINT_QUEUE_NAME);
        rescoreQueue.add(TaskOptions.Builder
                .withUrl(ScoreProcessor.OBJECT_TASK_URL)
                .param(DeleteTaskRequest.TASK_COUNT_PARAM, "0")
                .param("cursor", "null"));

    }
}
