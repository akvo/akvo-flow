package org.waterforpeople.mapping.app.web;

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointMetricSummaryDto;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.dao.GeoRegionDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.GeoRegion;
import org.waterforpeople.mapping.domain.TechnologyType;
import org.waterforpeople.mapping.helper.AccessPointHelper;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.surveyal.domain.SurveyalValue;
import com.gallatinsystems.surveyal.domain.SurveyedLocale;

public class KMLGenerator {
	private static final String IMAGE_ROOT = "imageroot";

	private static final Logger log = Logger.getLogger(KMLGenerator.class
			.getName());

	private VelocityEngine engine;

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
	public static final String WATER_POINT_FUNCTIONING_BLACK_ICON_URL = "http://watermapmonitordev.appspot.com/images/iconBlack36.png";
	public static final String PUBLIC_INSTITUTION_FUNCTIONING_GREEN_ICON_URL = "http://watermapmonitordev.appspot.com/images/houseGreen36.png";
	public static final String PUBLIC_INSTITUTION_FUNCTIONING_YELLOW_ICON_URL = "http://watermapmonitordev.appspot.com/images/houseYellow36.png";
	public static final String PUBLIC_INSTITUTION_FUNCTIONING_RED_ICON_URL = "http://watermapmonitordev.appspot.com/images/houseRed36.png";
	public static final String PUBLIC_INSTITUTION_FUNCTIONING_BLACK_ICON_URL = "http://watermapmonitordev.appspot.com/images/houseBlack36.png";
	public static final String SCHOOL_INSTITUTION_FUNCTIONING_GREEN_ICON_URL = "http://watermapmonitordev.appspot.com/images/pencilGreen36.png";
	public static final String SCHOOL_INSTITUTION_FUNCTIONING_YELLOW_ICON_URL = "http://watermapmonitordev.appspot.com/images/pencilYellow36.png";
	public static final String SCHOOL_INSTITUTION_FUNCTIONING_RED_ICON_URL = "http://watermapmonitordev.appspot.com/images/pencilRed36.png";
	public static final String SCHOOL_INSTITUTION_FUNCTIONING_BLACK_ICON_URL = "http://watermapmonitordev.appspot.com/images/pencilBlack36.png";
	public static final Boolean useScore = Boolean.parseBoolean(PropertyUtil
			.getProperty("scoreAPFlag"));
	public static final String ORGANIZATION_KEY = "organization";
	public static final String ORGANIZATION = PropertyUtil
			.getProperty("organization");

	public KMLGenerator() {
		engine = new VelocityEngine();
		engine.setProperty("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.NullLogChute");
		try {
			engine.init();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not initialize velocity", e);
		}
	}

	public static final String defaultPhotoCaption = PropertyUtil
			.getProperty("defaultPhotoCaption");

	public String generateRegionDocumentString(String regionVMName) {
		String regionKML = generateRegionOutlines(regionVMName);
		return regionKML;
	}

	/**
	 * merges a hydrated context with a template identified by the templateName
	 * passed in.
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
		context = null;
		return writer.toString();
	}

	public String generateDocument(String placemarksVMName) {
		return generateDocument(placemarksVMName, Constants.ALL_RESULTS);
	}

	public String generateDocument(String placemarksVMName, String countryCode) {
		try {
			VelocityContext context = new VelocityContext();
			String placemarks = generatePlacemarks(placemarksVMName,
					countryCode);
			context.put("folderContents", placemarks);
			context.put("regionPlacemark", generateRegionOutlines("Regions.vm"));
			return mergeContext(context, "Document.vm");
		} catch (Exception ex) {
			log.log(Level.SEVERE, "Could create kml", ex);
		}
		return null;
	}

	@SuppressWarnings("unused")
	private String generateFolderContents(
			HashMap<String, ArrayList<String>> contents, String vmName)
			throws Exception {
		VelocityContext context = new VelocityContext();
		StringBuilder techFolders = new StringBuilder();

		for (Entry<String, ArrayList<String>> techItem : contents.entrySet()) {
			String key = techItem.getKey();
			StringBuilder sbFolderPl = new StringBuilder();
			for (String placemark : techItem.getValue()) {
				sbFolderPl.append(placemark);
			}
			context.put("techFolderName", key);
			context.put("techPlacemarks", sbFolderPl);
			techFolders.append(mergeContext(context, "techFolders.vm"));
		}
		context.put("techFolders", techFolders.toString());
		return mergeContext(context, vmName);

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
										: "placemarkExternalMap.vm", display);

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
							encodeStatusString(ap, context);
							context.put(
									"pinStyle",
									encodePinStyle(ap.getPointType(),
											ap.getPointStatus()));
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
			String formattedDate = DateFormat.getDateInstance(DateFormat.SHORT)
					.format(ap.getLastSurveyedDate());
			context.put("collectionDate", formattedDate);
			context.put("timestamp", timestamp);
			String collectionYear = new SimpleDateFormat("yyyy").format(ap
					.getLastSurveyedDate());
			context.put("collectionYear", collectionYear);
		} else {
			String timestamp = DateFormatUtils.formatUTC(
					ap.getCreatedDateTime(),
					DateFormatUtils.ISO_DATE_FORMAT.getPattern());
			String formattedDate = DateFormat.getDateInstance(DateFormat.SHORT)
					.format(ap.getCreatedDateTime());
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
							|| !"option"
									.equalsIgnoreCase(val.getQuestionType())) {
						valuesToBind.remove(val);
					}
				}
				if (val.getStringValue() == null) {
					valuesToBind.remove(val);
				} else if (val.getStringValue().trim().toLowerCase()
						.equals(".jpg")) {
					context.put("photoUrl", val.getStringValue());
					foundPhoto = true;
					valuesToBind.remove(val);
				} else {
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

		if (!foundStatus) {
			context.put("waterSystemStatus", "Unknown");
		}
		// TODO: parameterize the default logo
		if (!foundPhoto) {
			context.put("photoUrl",
					"http://waterforpeople.s3.amazonaws.com/images/wfplogo.jpg");
		}

		if (ap.getLocaleType() != null) {
			context.put("type", ap.getLocaleType());
		} else {
			context.put("type", "water");
		}

		String output = mergeContext(context, vmName);
		context = null;
		return output;
	}

	public String bindPlacemark(AccessPoint ap, String vmName, String display)
			throws Exception {
		// if (ap.getCountryCode() != null && !ap.getCountryCode().equals("MW"))
		// {
if(display != null && display.trim().equalsIgnoreCase(GOOGLE_EARTH_DISPLAY)){
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
				encodeStatusString(ap, context);
				context.put("pinStyle",
						encodePinStyle(ap.getPointType(), ap.getPointStatus()));
			} else {
				context.put("pinStyle", "waterpushpinblk");
			}
			String output = mergeContext(context, vmName);
			context = null;
			return output;

		}
		return null;

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

	public static String encodePinStyle(AccessPointType type,
			AccessPoint.Status status) {
		String prefix = "water";
		if (AccessPointType.SANITATION_POINT == type) {
			prefix = "sani";
		} else if (AccessPointType.SCHOOL == type) {
			prefix = "schwater";
		} else if (AccessPointType.PUBLIC_INSTITUTION == type) {
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

		Integer score2 = new AccessPointHelper().scoreAccessPointDynamic(ap)
				.getScore();

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

	public String bindSummaryPlacemark(AccessPointMetricSummaryDto apsDto,
			String vmName) throws Exception {
		VelocityContext context = new VelocityContext();
		StringBuilder sb = new StringBuilder();
		context.put("organization", ORGANIZATION);
		if (apsDto.getParentSubName() != null) {
			context.put("subPath", apsDto.getParentSubName()
					.replace("/", " | "));
		}

		context.put("subValue", apsDto.getSubValue());
		context.put("waterPointCount", apsDto.getCount());
		context.put("type", apsDto.getMetricValue());
		sb.append(mergeContext(context, vmName));
		return sb.toString();

	}
}
