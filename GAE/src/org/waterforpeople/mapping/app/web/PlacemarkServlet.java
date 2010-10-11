package org.waterforpeople.mapping.app.web;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.json.JSONArray;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.location.PlacemarkDto;
import org.waterforpeople.mapping.app.web.dto.PlacemarkRestRequest;
import org.waterforpeople.mapping.app.web.dto.PlacemarkRestResponse;
import org.waterforpeople.mapping.dao.AccessPointDao;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;
import org.waterforpeople.mapping.domain.AccessPoint.Status;

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;

public class PlacemarkServlet extends AbstractRestApiServlet {
	private static final Logger log = Logger.getLogger(PlacemarkServlet.class
			.getName());

	private VelocityEngine engine;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9031594440737716966L;

	@Override
	protected RestRequest convertRequest() throws Exception {
		HttpServletRequest req = getRequest();
		RestRequest restRequest = new PlacemarkRestRequest();
		restRequest.populateFromHttpRequest(req);
		return restRequest;

	}

	@Override
	protected RestResponse handleRequest(RestRequest req) throws Exception {
		PlacemarkRestRequest piReq = (PlacemarkRestRequest) req;
		AccessPointDao apDao = new AccessPointDao();
		List<AccessPoint> results = apDao.searchAccessPoints(
				piReq.getCountry(), null, null, null, null, null, null, null,
				null, null, piReq.getCursor());
		return convertToResponse(results, AccessPointDao.getCursor(results));
	}

	private RestResponse convertToResponse(List<AccessPoint> apList,
			String cursor) {
		PlacemarkRestResponse resp = new PlacemarkRestResponse();
		if (apList != null) {
			initVelocityEngine();
			List<PlacemarkDto> dtoList = new ArrayList<PlacemarkDto>();
			for (AccessPoint ap : apList) {
				PlacemarkDto pdto = new PlacemarkDto();
				pdto.setLatitude(ap.getLatitude());
				pdto.setLongitude(ap.getLongitude());
				pdto.setIconUrl(getUrlFromStatus(ap.getPointStatus()));
				String placemarkString = null;
				try {
					placemarkString = bindPlacemark(ap,
							"placemarkExternalMap.vm");
					pdto.setPlacemarkContents(placemarkString);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (placemarkString != null)
					dtoList.add(pdto);
			}
			resp.setPlacemarks(dtoList);
		}
		resp.setCursor(cursor);
		return resp;
	}

	private String getUrlFromStatus(Status pointStatus) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void writeOkResponse(RestResponse resp) throws Exception {
		getResponse().setStatus(200);
		PlacemarkRestResponse piResp = (PlacemarkRestResponse) resp;
		JSONObject result = new JSONObject(piResp);
		JSONArray arr = result.getJSONArray("placemarks");
		if (arr != null) {
			for (int i = 0; i < arr.length(); i++) {
				// ((JSONObject) arr.get(i)).put("propertyNames", piResp
				// .getPointsOfInterest().get(i).getPropertyNames());
			}
		}
		getResponse().getWriter().println(result.toString());
	}

	private void initVelocityEngine() {
		engine = new VelocityEngine();
		engine.setProperty("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.NullLogChute");
		try {
			engine.init();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not initialize velocity", e);
		}
	}

	public String bindPlacemark(AccessPoint ap, String vmName) throws Exception {
		// if (ap.getCountryCode() != null && !ap.getCountryCode().equals("MW"))
		// {
		if (ap.getCountryCode() == null)
			ap.setCountryCode("Unknown");
		if (ap.getCountryCode() != null) {

			VelocityContext context = new VelocityContext();
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
				String timestamp = DateFormatUtils.formatUTC(new Date(),
						DateFormatUtils.ISO_DATE_FORMAT.getPattern());
				String formattedDate = DateFormat.getDateInstance(
						DateFormat.SHORT).format(new Date());
				context.put("collectionDate", formattedDate);
				context.put("timestamp", timestamp);
			}

			if (ap.getCommunityCode() != null)
				context.put("communityCode", ap.getCommunityCode());
			else
				context.put("communityCode", "Unknown" + new Date());

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
				context.put("institutionName", "Unknown");
			}

			if (ap.getConstructionDateYear() == null
					|| ap.getConstructionDateYear().trim().equals("")) {
				context.put("constructionDateOfWaterPoint", "Unknown");
			} else {
				context.put("constructionDateOfWaterPoint",
						ap.getConstructionDateYear());
			}
			if (ap.getNumberOfHouseholdsUsingPoint() == null) {
				context.put("numberOfHouseholdsUsingWaterPoint", "Unknown");
			} else {
				context.put("numberOfHouseholdsUsingWaterPoint",
						ap.getNumberOfHouseholdsUsingPoint());
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
				context.put("waterPointPhotoCaption", "Water For People");
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

			if (ap.getDescription() != null)
				context.put("description", ap.getDescription());
			else
				context.put("description", "Unknown");

			// Need to check this
			if (ap.getPointType() != null) {
				context.put("pinStyle",
						encodePinStyle(ap.getPointType(), ap.getPointStatus()));
				encodeStatusString(ap.getPointStatus(), context);
			} else {
				context.put("pinStyle", "waterpushpinblk");
			}
			String output = mergeContext(context, vmName);
			context = null;
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

	private String encodePinStyle(AccessPointType type,
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

	private String encodeStatusString(AccessPoint.Status status,
			VelocityContext context) {

		if (status != null) {
			if (AccessPoint.Status.FUNCTIONING_HIGH == status) {
				context.put("waterSystemStatus",
						"System Functioning and Meets Government Standards");
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

}
