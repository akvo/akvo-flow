package org.waterforpeople.mapping.app.web;

import java.io.StringWriter;
import java.text.DateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.dao.GeoRegionDAO;
import org.waterforpeople.mapping.dao.KMLDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.GeoRegion;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.dao.BaseDAO;

public class KMLGenerator {
	private static final Logger log = Logger.getLogger(KMLGenerator.class
			.getName());

	private KMLDAO kmlDAO;
	private VelocityEngine engine;

	public KMLGenerator() {
		kmlDAO = new KMLDAO();
		engine = new VelocityEngine();
		engine.setProperty("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.NullLogChute");
		try {
			engine.init();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not initialize velocity", e);
		}
	}

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
		return writer.toString();
	}

	public String generateDocument(String placemarksVMName) {
		String document = null;
		try {
			VelocityContext context = new VelocityContext();
			context.put("Placemark", generatePlacemarks("PlacemarkTabs.vm"));
			context
					.put("regionPlacemark",
							generateRegionOutlines("Regions.vm"));
			document = mergeContext(context, "Document.vm");
			kmlDAO.saveKML(document);
		} catch (Exception ex) {

		}
		return document;
	}

	public String generatePlacemarks(String vmName) {

		StringBuilder sb = new StringBuilder();
		BaseDAO<AccessPoint> apDAO = new BaseDAO<AccessPoint>(AccessPoint.class);

		List<AccessPoint> entries = apDAO.list(Constants.ALL_RESULTS);
		try {
			// loop through accessPoints and bind to variables
			for (AccessPoint ap : entries) {
				try {

					VelocityContext context = new VelocityContext();
					if (ap.getCollectionDate() != null) {
						String formattedDate = DateFormat.getDateInstance(
								DateFormat.SHORT)
								.format(ap.getCollectionDate());
						context.put("collectionDate", formattedDate);
					} else {
						context.put("collectionDate", "N/A");
					}
					context.put("latitude", ap.getLatitude());
					context.put("longitude", ap.getLongitude());
					context.put("altitude", ap.getAltitude());
					context.put("communityCode", ap.getCommunityCode());
					context.put("photoUrl", ap.getPhotoURL());
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

					if (ap.getTypeTechnologyString() == null) {
						context.put("primaryTypeTechnology", "Unknown");
					} else {
						context.put("primaryTypeTechnology", ap
								.getTypeTechnologyString());
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
						context.put("numberOfHouseholdsUsingWaterPoint",
								"Unknown");
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
							|| ap.getFarthestHouseholdfromPoint().trim()
									.equals("")) {
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
						context.put("waterPointPhotoCaption",
								"Water For People");
					} else {
						context.put("waterPointPhotoCaption", ap
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
						context.put("meetGovtQualityStandardFlag", encodeBooleanDisplay(ap
								.getMeetGovtQualityStandardFlag()));
					}
					if (ap.getMeetGovtQuantityStandardFlag() == null) {
						context.put("meetGovtQuantityStandardFlag", "N/A");
					} else {
						context.put("meetGovtQuantityStandardFlag", encodeBooleanDisplay(ap
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

					context.put("description", ap.getDescription());
					// Need to check this
					encodeStatus(ap.getPointType(),ap.getPointStatus(),context);
					String output = mergeContext(context, vmName);
					sb.append(output);
				} catch (Exception ex) {
					log.info(ex.getMessage());
				}
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating placemarks", e);
		}
		return sb.toString();
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
	
	private String encodeBooleanDisplay(Boolean value){
		if(value){
			return "Yes";
		}else{
			return "No";
		}
	}
	
	private void encodeStatus(AccessPointType type, AccessPoint.Status status, VelocityContext context){
		if (type.equals(
				AccessPointType.SANITATION_POINT)) {
			context.put("pinStyle", "pushpinpurple");
		} else {
			if (status.equals(
					AccessPoint.Status.FUNCTIONING_HIGH)) {
				context.put("pinStyle", "pushpingreen");
			} else if (status.equals(
					AccessPoint.Status.FUNCTIONING_OK)) {
				context.put("pinStyle", "pushpinyellow");
			} else if (status.equals(
					AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS)) {
				context.put("pinStyle", "pushpinred");
			} else if (status.equals(
					AccessPoint.Status.NO_IMPROVED_SYSTEM)) {
				context.put("pinStyle", "pushpinblk");
			} else {
				context.put("pinStyle", "pushpinblk");
			}
		}
		encodeStatusString(status, context);
	}
	
	private void encodeStatusString(AccessPoint.Status status, VelocityContext context){
	
			if (status.equals(
					AccessPoint.Status.FUNCTIONING_HIGH)) {
				context
						.put("waterSystemStatus",
								"System Functioning and Meets Government Standards");
			} else if (status.equals(
					AccessPoint.Status.FUNCTIONING_OK)) {
				context.put("waterSystemStatus",
						"Functioning but with Problems");
			} else if (status.equals(
					AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS)) {
				context.put("waterSystemStatus",
						"Broken-down system");
			} else if (status.equals(
					AccessPoint.Status.NO_IMPROVED_SYSTEM)) {
				context.put("waterSystemStatus",
						"No Improved System");
			} else {
				context.put("waterSystemStatus", "Unknown");
			}
	}
}
