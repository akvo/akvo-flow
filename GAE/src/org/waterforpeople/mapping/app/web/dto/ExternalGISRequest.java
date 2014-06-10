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

package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.gis.map.domain.OGRFeature.FeatureType;

public class ExternalGISRequest extends RestRequest {

    private static final String GEOMETRY_STRING_PARAM = "geometryString";
    private static final String RECIPROCAL_OF_FLATTENING_PARAM = "reciprocalOfFlattening";
    private static final String Y2_PARAM = "y2";
    private static final String Y1_PARAM = "y1";
    private static final String X2_PARAM = "x2";
    private static final String X1_PARAM = "x1";
    private static final String COUNTRY_CODE_PARAM = "countryCode";
    private static final String SPHEROID_PARAM = "spheroid";
    private static final String DATUM_IDENTIFIER_PARAM = "datumIdentifier";
    private static final String GEO_COORDINATE_SYSTEM_IDENTIFIER_PARAM = "geoCoordinateSystemIdentifier";
    private static final String PROJECT_COORDINATE_SYSTEM_IDENTIFIER_PARAM = "projectCoordinateSystemIdentifier";
    private static final String NAME_PARAM = "name";
    private static final String OGR_FEATURE_TYPE_PARAM = "ogrFeatureType";
    private static final String UN_CODE_PARAM = "unCode";
    private static final String CENTROID_LAT_PARAM = "centroidLat";
    private static final String CENTROID_LON_PARAM = "centroldLon";
    private static final String POP_2005_PARAM = "pop2005";
    private static final String SUBDIVISION_1_PARAM = "sub1";
    private static final String SUBDIVISION_2_PARAM = "sub2";
    private static final String SUBDIVISION_3_PARAM = "sub3";
    private static final String SUBDIVISION_4_PARAM = "sub4";
    private static final String SUBDIVISION_5_PARAM = "sub5";
    private static final String SUBDIVISION_6_PARAM = "sub6";
    private static final String DENSITY_PARAM = "density";
    private static final String TOTAL_POPULATION_PARAM = "totalPopulation";
    private static final String FEMALE_POPULATION_PARAM = "femalePopulation";
    private static final String MALE_POPULATION_PARAM = "malePopulation";
    private static final String NUMBER_HOUSEHOLDS_PARAM = "numberOfHouseholds";
    public static final String LIST_MATCHING_OGRFEATURE_ACTION = "listOGRFeature";

    /**
	 * 
	 */
    private static final long serialVersionUID = 7589676876969685689L;
    public static final String IMPORT_ACTION = "importOgrFeature";
    private String name = null;
    private String projectCoordinateSystemIdentifier = null;
    private String geoCoordinateSystemIdentifier = null;
    private String datumIdentifier = null;
    private Double spheroid = null;
    private String countryCode = null;
    private Double x1 = null;
    private Double x2 = null;
    private Double y1 = null;
    private Double y2 = null;
    private Double reciprocalOfFlattening = null;
    private String geometryString = null;
    private FeatureType ogrFeatureType = null;
    private String unCode = null;
    private Double centroidLat = null;
    private Double centroidLon = null;
    private Integer pop2005 = null;
    private String sub1 = null;
    private String sub2 = null;
    private String sub3 = null;
    private String sub4 = null;
    private String sub5 = null;
    private String sub6 = null;
    private Integer density = null;
    private Integer totalPopulation = null;
    private Integer femalePopulation = null;
    private Integer malePopulation = null;
    private Integer numberHouseholds = null;

    public Integer getDensity() {
        return density;
    }

    public void setDensity(Integer density) {
        this.density = density;
    }

    public Integer getTotalPopulation() {
        return totalPopulation;
    }

    public void setTotalPopulation(Integer totalPopulation) {
        this.totalPopulation = totalPopulation;
    }

    public Integer getFemalePopulation() {
        return femalePopulation;
    }

    public void setFemalePopulation(Integer femalePopulation) {
        this.femalePopulation = femalePopulation;
    }

    public Integer getMalePopulation() {
        return malePopulation;
    }

    public void setMalePopulation(Integer malePopulation) {
        this.malePopulation = malePopulation;
    }

    public Integer getNumberHouseholds() {
        return numberHouseholds;
    }

    public void setNumberHouseholds(Integer numberHouseholds) {
        this.numberHouseholds = numberHouseholds;
    }

    public String getSub1() {
        return sub1;
    }

    public void setSub1(String sub1) {
        this.sub1 = sub1;
    }

    public String getSub2() {
        return sub2;
    }

    public void setSub2(String sub2) {
        this.sub2 = sub2;
    }

    public String getSub3() {
        return sub3;
    }

    public void setSub3(String sub3) {
        this.sub3 = sub3;
    }

    public String getSub4() {
        return sub4;
    }

    public void setSub4(String sub4) {
        this.sub4 = sub4;
    }

    public String getSub5() {
        return sub5;
    }

    public void setSub5(String sub5) {
        this.sub5 = sub5;
    }

    public String getSub6() {
        return sub6;
    }

    public void setSub6(String sub6) {
        this.sub6 = sub6;
    }

    public String getUnCode() {
        return unCode;
    }

    public void setUnCode(String unCode) {
        this.unCode = unCode;
    }

    public Double getCentroidLat() {
        return centroidLat;
    }

    public void setCentroidLat(Double centroidLat) {
        this.centroidLat = centroidLat;
    }

    public Double getCentroidLon() {
        return centroidLon;
    }

    public void setCentroidLon(Double centroidLon) {
        this.centroidLon = centroidLon;
    }

    public Integer getPop2005() {
        return pop2005;
    }

    public void setPop2005(Integer pop2005) {
        this.pop2005 = pop2005;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectCoordinateSystemIdentifier() {
        return projectCoordinateSystemIdentifier;
    }

    public void setProjectCoordinateSystemIdentifier(
            String projectCoordinateSystemIdentifier) {
        this.projectCoordinateSystemIdentifier = projectCoordinateSystemIdentifier;
    }

    public String getGeoCoordinateSystemIdentifier() {
        return geoCoordinateSystemIdentifier;
    }

    public void setGeoCoordinateSystemIdentifier(
            String geoCoordinateSystemIdentifier) {
        this.geoCoordinateSystemIdentifier = geoCoordinateSystemIdentifier;
    }

    public String getDatumIdentifier() {
        return datumIdentifier;
    }

    public void setDatumIdentifier(String datumIdentifier) {
        this.datumIdentifier = datumIdentifier;
    }

    public Double getSpheroid() {
        return spheroid;
    }

    public void setSpheroid(Double spheroid) {
        this.spheroid = spheroid;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Double getX1() {
        return x1;
    }

    public void setX1(Double x1) {
        this.x1 = x1;
    }

    public Double getX2() {
        return x2;
    }

    public void setX2(Double x2) {
        this.x2 = x2;
    }

    public Double getY1() {
        return y1;
    }

    public void setY1(Double y1) {
        this.y1 = y1;
    }

    public Double getY2() {
        return y2;
    }

    public void setY2(Double y2) {
        this.y2 = y2;
    }

    public Double getReciprocalOfFlattening() {
        return reciprocalOfFlattening;
    }

    public void setReciprocalOfFlattening(Double reciprocalOfFlattening) {
        this.reciprocalOfFlattening = reciprocalOfFlattening;
    }

    @Override
    protected void populateFields(HttpServletRequest req) throws Exception {
        if (req.getParameter(NAME_PARAM) != null) {
            setName(req.getParameter(NAME_PARAM));
        }
        if (req.getParameter(PROJECT_COORDINATE_SYSTEM_IDENTIFIER_PARAM) != null) {
            this.setProjectCoordinateSystemIdentifier(req
                    .getParameter(PROJECT_COORDINATE_SYSTEM_IDENTIFIER_PARAM));
        }
        if (req.getParameter(GEO_COORDINATE_SYSTEM_IDENTIFIER_PARAM) != null) {
            this.setGeoCoordinateSystemIdentifier(req
                    .getParameter(GEO_COORDINATE_SYSTEM_IDENTIFIER_PARAM));
        }
        if (req.getParameter(DATUM_IDENTIFIER_PARAM) != null) {
            this.setDatumIdentifier(req.getParameter(DATUM_IDENTIFIER_PARAM));
        }
        if (req.getParameter(SPHEROID_PARAM) != null) {
            this.setSpheroid(Double.parseDouble(req
                    .getParameter(SPHEROID_PARAM)));
        }
        if (req.getParameter(COUNTRY_CODE_PARAM) != null) {
            this.setCountryCode(req.getParameter(COUNTRY_CODE_PARAM));
        }
        if (req.getParameter(X1_PARAM) != null) {
            this.setX1(Double.parseDouble(req.getParameter(X1_PARAM)));
        }
        if (req.getParameter(X2_PARAM) != null) {
            this.setX2(Double.parseDouble(req.getParameter(X2_PARAM)));
        }
        if (req.getParameter(Y1_PARAM) != null) {
            this.setY1(Double.parseDouble(req.getParameter(Y1_PARAM)));
        }
        if (req.getParameter(Y2_PARAM) != null) {
            this.setY2(Double.parseDouble(req.getParameter(Y2_PARAM)));
        }
        if (req.getParameter(RECIPROCAL_OF_FLATTENING_PARAM) != null) {
            this.setReciprocalOfFlattening(Double.parseDouble(req
                    .getParameter(RECIPROCAL_OF_FLATTENING_PARAM)));
        }
        if (req.getParameter(GEOMETRY_STRING_PARAM) != null) {
            this.setGeometryString(req.getParameter(GEOMETRY_STRING_PARAM));
        }
        if (req.getParameter(OGR_FEATURE_TYPE_PARAM) != null) {
            this.setOgrFeatureType(FeatureType.valueOf(req
                    .getParameter(OGR_FEATURE_TYPE_PARAM)));
        }
        if (req.getParameter(CENTROID_LAT_PARAM) != null) {
            this.setCentroidLat(Double.parseDouble(req
                    .getParameter(CENTROID_LAT_PARAM)));
        }
        if (req.getParameter(CENTROID_LON_PARAM) != null) {
            this.setCentroidLon(Double.parseDouble(req
                    .getParameter(CENTROID_LON_PARAM)));
        }
        if (req.getParameter(UN_CODE_PARAM) != null) {
            this.setUnCode(req.getParameter(UN_CODE_PARAM));
        }
        if (req.getParameter(POP_2005_PARAM) != null) {
            this.setPop2005(Integer.parseInt(req.getParameter(POP_2005_PARAM)));
        }
        if (req.getParameter(SUBDIVISION_1_PARAM) != null
                && !req.getParameter(SUBDIVISION_1_PARAM).equals("-")) {
            this.setSub1(req.getParameter(SUBDIVISION_1_PARAM));
        }
        if (req.getParameter(SUBDIVISION_2_PARAM) != null
                && !req.getParameter(SUBDIVISION_2_PARAM).equals("-")) {
            this.setSub2(req.getParameter(SUBDIVISION_2_PARAM));
        }
        if (req.getParameter(SUBDIVISION_3_PARAM) != null
                && !req.getParameter(SUBDIVISION_3_PARAM).equals("-")) {
            this.setSub3(req.getParameter(SUBDIVISION_3_PARAM));
        }
        if (req.getParameter(SUBDIVISION_4_PARAM) != null
                && !req.getParameter(SUBDIVISION_4_PARAM).equals("-")) {
            this.setSub4(req.getParameter(SUBDIVISION_4_PARAM));
        }
        if (req.getParameter(SUBDIVISION_5_PARAM) != null
                && !req.getParameter(SUBDIVISION_5_PARAM).equals("-")) {
            this.setSub5(req.getParameter(SUBDIVISION_5_PARAM));
        }
        if (req.getParameter(SUBDIVISION_6_PARAM) != null
                && !req.getParameter(SUBDIVISION_6_PARAM).equals("-")) {
            this.setSub6(req.getParameter(SUBDIVISION_6_PARAM));
        }
        if (req.getParameter(TOTAL_POPULATION_PARAM) != null) {
            this.setTotalPopulation(Integer.parseInt(req.getParameter(TOTAL_POPULATION_PARAM)));
        }
        if (req.getParameter(FEMALE_POPULATION_PARAM) != null) {
            this.setFemalePopulation(Integer.parseInt(req.getParameter(FEMALE_POPULATION_PARAM)));
        }
        if (req.getParameter(MALE_POPULATION_PARAM) != null) {
            this.setMalePopulation(Integer.parseInt(req.getParameter(MALE_POPULATION_PARAM)));
        }
        if (req.getParameter(DENSITY_PARAM) != null) {
            this.setDensity(Integer.parseInt(req.getParameter(DENSITY_PARAM)));
        }
        if (req.getParameter(NUMBER_HOUSEHOLDS_PARAM) != null) {
            this.setNumberHouseholds(Integer.parseInt(req.getParameter(NUMBER_HOUSEHOLDS_PARAM)));
        }
    }

    @Override
    protected void populateErrors() {
        // TODO Auto-generated method stub

    }

    public void setGeometryString(String geometryString) {
        this.geometryString = geometryString;
    }

    public String getGeometryString() {
        return geometryString;
    }

    public void setOgrFeatureType(FeatureType ogrFeatureType) {
        this.ogrFeatureType = ogrFeatureType;
    }

    public FeatureType getOgrFeatureType() {
        return ogrFeatureType;
    }

}
