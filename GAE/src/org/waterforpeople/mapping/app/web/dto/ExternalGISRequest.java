package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

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
	private String geometryString =null; 

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
			this.setProjectCoordinateSystemIdentifier(req.getParameter(PROJECT_COORDINATE_SYSTEM_IDENTIFIER_PARAM));
		}
		if (req.getParameter(GEO_COORDINATE_SYSTEM_IDENTIFIER_PARAM) != null) {
			this.setGeoCoordinateSystemIdentifier(req.getParameter(GEO_COORDINATE_SYSTEM_IDENTIFIER_PARAM));
		}
		if (req.getParameter(DATUM_IDENTIFIER_PARAM) != null) {
			this.setDatumIdentifier(req.getParameter(DATUM_IDENTIFIER_PARAM));
		}
		if (req.getParameter(SPHEROID_PARAM) != null) {
			this.setSpheroid(Double.parseDouble(req.getParameter(SPHEROID_PARAM)));
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
			this.setReciprocalOfFlattening(Double.parseDouble(req.getParameter(RECIPROCAL_OF_FLATTENING_PARAM)));
		}
		if(req.getParameter(GEOMETRY_STRING_PARAM)!=null){
			this.setGeometryString(req.getParameter(GEOMETRY_STRING_PARAM));
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

}
