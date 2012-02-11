package org.waterforpeople.mapping.adapter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.MappingSpreadsheetColumnToAttribute;
import org.waterforpeople.mapping.domain.MappingSpreadsheetDefinition;
import org.waterforpeople.mapping.domain.TechnologyType;
import org.waterforpeople.mapping.helper.AccessPointHelper;
import org.waterforpeople.mapping.helper.SpreadsheetMappingAttributeHelper;
import org.waterforpeople.mapping.helper.TechnologyTypeHelper;

import com.gallatinsystems.common.data.spreadsheet.GoogleSpreadsheetAdapter;
import com.gallatinsystems.common.data.spreadsheet.domain.ColumnContainer;
import com.gallatinsystems.common.data.spreadsheet.domain.RowContainer;
import com.gallatinsystems.common.data.spreadsheet.domain.SpreadsheetContainer;
import com.google.gdata.util.ServiceException;

/**
 * Adapter for loading AccessPoints using a google spreadsheet.
 * 
 *@deprecated
 */
public class SpreadsheetAccessPointAdapter {
	private static final Logger log = Logger
			.getLogger(SpreadsheetAccessPointAdapter.class.getName());

	private PrivateKey privateKey = null;
	private String sessionToken = null;
	private AccessPointHelper apHelper;

	public SpreadsheetAccessPointAdapter(String sessionToken,
			PrivateKey privateKey) {
		this.privateKey = privateKey;
		this.sessionToken = sessionToken;
		apHelper = new AccessPointHelper();
	}

	public void processSpreadsheetOfAccessPoints(String spreadsheetName)
			throws IOException, ServiceException {
		loadTechnologyTypes();
		GoogleSpreadsheetAdapter gsa = new GoogleSpreadsheetAdapter(
				sessionToken, privateKey);
		SpreadsheetContainer sc = gsa.getSpreadsheetContents(spreadsheetName);
		for (RowContainer row : sc.getRowContainerList()) {
			AccessPoint ap = processRow(row, spreadsheetName);
			ap = apHelper.saveAccessPoint(ap);
		}
	}

	public ArrayList<String> listColumns(String spreadsheetName)
			throws IOException, ServiceException {
		GoogleSpreadsheetAdapter gas = new GoogleSpreadsheetAdapter(
				sessionToken, privateKey);
		return gas.listColumns(spreadsheetName);
	}

	public ArrayList<String> listSpreadsheets(String feedURL)
			throws IOException, ServiceException, GeneralSecurityException {
		return new GoogleSpreadsheetAdapter(sessionToken, privateKey)
				.listSpreasheets(feedURL);
	}

	HashMap<String, TechnologyType> techTypeMap = new HashMap<String, TechnologyType>();

	private void loadTechnologyTypes() {
		TechnologyTypeHelper tth = new TechnologyTypeHelper();
		for (TechnologyType item : tth.listTechnologyTypes()) {
			techTypeMap.put(item.getCode(), item);
		}
	}

	@SuppressWarnings({"unchecked","rawtypes"})
	private AccessPoint processRow(RowContainer row, String spreadsheetName) {
		AccessPoint ap = new AccessPoint();
		// Structure of string from GoogleSpreadsheet
		// <gsx:dateofvisit>10/27/2009</gsx:dateofvisit>
		// <gsx:latitude>8.153982</gsx:latitude>
		// <gsx:longitude>-15.4330992</gsx:longitude>
		// <gsx:communitycode>PC24</gsx:communitycode>
		// <gsx:watersystemstatus>System needs repair but is
		// functioning</gsx:watersystemstatus>
		// <gsx:photocodeforprimarywatertechnology>No
		// photo</gsx:photocodeforprimarywatertechnology>
		// <gsx:linksforphotos>No photo</gsx:linksforphotos>
		// <gsx:captianforwaterpointphoto>Gravity fed system that needs
		// repairs</gsx:captianforwaterpointphoto>
		// <gsx:photoofprimarysanitationtechnology>No
		// photo</gsx:photoofprimarysanitationtechnology>
		// <gsx:linkforphotos>PC24san</gsx:linkforphotos>
		// <gsx:typeofsanitaitontechnology>Ventilated improved pit
		// latrines</gsx:typeofsanitaitontechnology>
		// <gsx:primaryimprovedsanitationtechnologyinuseinthecommunity>Don't
		// know</gsx:primaryimprovedsanitationtechnologyinuseinthecommunity>
		// <gsx:numberofhouseholdswithimprovedsanitation>Don't
		// know</gsx:numberofhouseholdswithimprovedsanitation>
		Class cls = null;
		HashMap<String, String> attributeTypeMap = new HashMap<String, String>();

		try {
			cls = Class
					.forName("org.waterforpeople.mapping.domain.AccessPoint");
			Method methlist[] = cls.getDeclaredMethods();

			for (Method method : methlist) {
				String methodName = method.getName();
				if (methodName.contains("set")) {
					Class[] paramTypes = method.getParameterTypes();
					if (paramTypes.length > 0) {
						attributeTypeMap.put(methodName,
								paramTypes[0].getName());
					}
				}
			}

		} catch (ClassNotFoundException e1) {
			log.log(Level.SEVERE, "Class not found", e1);
		}

		HashMap<String, String> colsToAttributesMap = getColsToAttributeMap(spreadsheetName);
		for (ColumnContainer col : row.getColumnContainersList()) {
			String colName = col.getColName();
			String attributeName = colsToAttributesMap.get(colName);
			 log.info("attributeName: " + attributeName);
			if (attributeName != null && !attributeName.trim().isEmpty()) {
				if (attributeName.trim().toLowerCase()
						.equals("typetechnologystring")
						|| attributeName.trim().toLowerCase()
								.equals("typetechnology")
						|| attributeName.trim().toLowerCase()
								.equals("pointtype")
						|| attributeName.trim().toLowerCase()
								.equals("pointstatus")) {
					if (attributeName.trim().toLowerCase()
							.equals("typetechnology")) {
						TechnologyType tt = techTypeMap.get(col
								.getColContents());
						if (tt != null) {
							// ToDo: fix after Tuesday demo
							// log.info("Matched tt type: " + tt.getCode());
							// ap.setTypeTechnology(tt);
						} else {
							ap.setTechnologyTypeOther(col.getColContents());
						}
					} else if (attributeName.toLowerCase().equals(
							"typetechnologystring")) {
						TechnologyType tt = techTypeMap.get(col
								.getColContents());
						if (tt != null) {
							// ToDo: fix after Tuesday demo
							// log.info("Matched tt type: " + tt.getCode());
							ap.setTypeTechnologyString(tt.getCode());
						} else {
							ap.setTypeTechnologyString(col.getColContents());
						}
					} else if (attributeName.trim().toLowerCase()
							.equals("pointtype")) {
						if (col.getColContents().trim().toLowerCase()
								.equals("sanitation")) {
							ap.setPointType(AccessPointType.SANITATION_POINT);
						} else if (col.getColContents().trim().toLowerCase()
								.equals("water")) {
							ap.setPointType(AccessPointType.WATER_POINT);
						} else if (col.getColContents().trim().toLowerCase()
								.equals("school")) {
							ap.setPointType(AccessPointType.SCHOOL);
						} else if (col.getColContents().trim().toLowerCase()
								.equals("public institution")) {
							ap.setPointType(AccessPointType.PUBLIC_INSTITUTION);
						} else if (col.getColContents().trim().toLowerCase()
								.equals("health post")) {
							ap.setPointType(AccessPointType.HEALTH_POSTS);
						}
					} else if (attributeName.trim().toLowerCase()
							.equals("pointstatus")) {
						if (col.getColContents() != null) {
							ap.setPointStatus(AccessPointHelper.encodeStatus(col.getColContents(),ap.getPointType()));
						}
					}
				} else {
					try {

						Class partypes[] = new Class[1];
						String paramTypeClass = attributeTypeMap.get("set"
								+ attributeName);
						partypes[0] = Class.forName(paramTypeClass);
						java.lang.reflect.Method meth = cls.getMethod("set"
								+ attributeName, partypes);
						if (paramTypeClass.contains("Double")) {
							Object arglist[] = new Object[1];
							arglist[0] = parseDouble(col.getColContents());
							meth.invoke(ap, arglist);
						} else if (paramTypeClass.contains("String")) {
							Object arglist[] = new Object[1];
							arglist[0] = col.getColContents();
							meth.invoke(ap, arglist);
						} else if (paramTypeClass.contains("Date")) {
							Object arglist[] = new Object[1];
							arglist[0] = parseDate(col.getColContents());
							meth.invoke(ap, arglist);
						} else if (paramTypeClass.contains("Long")) {
							Object arglist[] = new Object[1];
							arglist[0] = parseLong(col.getColContents());
							meth.invoke(ap, arglist);
						} else if (paramTypeClass.contains("Boolean")) {
							Object arglist[] = new Object[1];
							arglist[0] = parseBoolean(col.getColContents());
							meth.invoke(ap, arglist);
						}
					} catch (Exception e) {
						log.log(Level.SEVERE,
								"Could not process Access Point row", e);
					}
				}

			}
		}

		// for (ColumnContainer col : row.getColumnContainersList()) {
		// if (col.getColName().equals("dateofvisit")) {
		// ap.setCreatedDateTime(parseDate(col.getColContents()));
		// } else if (col.getColName().equals("latitude")) {
		// ap.setLatitude(parseDouble(col.getColContents()));
		// } else if (col.getColName().equals("longitude")) {
		// ap.setLongitude(parseDouble(col.getColContents()));
		// } else if (col.getColName().equals("communitycode")) {
		// ap.setCommunityCode(col.getColContents());
		// } else if (col.getColName().equals("watersystemstatus")) {
		// ap.setPointStatus(col.getColContents());
		// } else if (col.getColName().equals(
		// "photocodeforprimarywatertechnology")) {
		//
		// } else if (col.getColName().equals("linksforphotos")) {
		// ap.setPhotoURL(col.getColContents());
		// } else if (col.getColName().equals("captianforwaterpointphoto")) {
		// ap.setPointPhotoCaption(col.getColContents());
		// } else if (col.getColName().equals(
		// "photoofprimarysanitationtechnology")) {
		//
		// } else if (col.getColName().equals("typeofsanitaitontechnology")) {
		//
		// } else if (col.getColName().equals(
		// "primaryimprovedsanitationtechnologyinuseinthecommunity")) {
		//
		// } else if (col.getColName().equals(
		// "numberofhouseholdswithimprovedsanitation")) {
		//
		// }
		// }
		return ap;
	}

	
	private Date parseDate(String value) {
		Date date;
		try {
			date = new Date(value);
		} catch (Exception ex) {
			date = null;
		}
		return date;
	}

	private Double parseDouble(String value) {
		Double valueD;
		try {
			valueD = new Double(value);
		} catch (Exception ex) {
			valueD = 0.0;
		}
		return valueD;
	}

	private Long parseLong(String value) {
		Long longVal = null;
		try {
			longVal = new Long(value);
		} catch (Exception ex) {
			longVal = null;
			log.info("In process spreadsheet couldn't parse long :" + value);
		}
		return longVal;
	}

	private Boolean parseBoolean(String value) {
		Boolean retVal = null;
		try {
			if (value.trim().toLowerCase().equals("y")
					|| value.trim().toLowerCase().equals("yes")
					|| value.trim().toLowerCase().equals("true")
					|| value.trim().toLowerCase().equals("t")
					|| value.trim().toLowerCase().equals("1")
					|| value.trim().toLowerCase().equals("one")) {
				retVal = true;
			} else {
				retVal = false;
			}
		} catch (Exception ex) {
			log.info("In process spreadsheet couldn't parse Boolean: " + value);
		}
		return retVal;
	}

	private HashMap<String, String> getColsToAttributeMap(String spreadsheetName) {
		SpreadsheetMappingAttributeHelper samh = new SpreadsheetMappingAttributeHelper(
				sessionToken, privateKey);
		MappingSpreadsheetDefinition mapDef = new MappingSpreadsheetDefinition();
		mapDef = samh.getMappingSpreadsheetDefinition(spreadsheetName);
		HashMap<String, String> colsToAttributesMap = new HashMap<String, String>();
		for (MappingSpreadsheetColumnToAttribute item : mapDef.getColumnMap()) {
			String capedString = item.getObjectAttribute().substring(0, 1)
					.toUpperCase();
			capedString = capedString + item.getObjectAttribute().substring(1);
			colsToAttributesMap.put(item.getSpreadsheetColumn(), capedString);
		}
		return colsToAttributesMap;
	}

}
