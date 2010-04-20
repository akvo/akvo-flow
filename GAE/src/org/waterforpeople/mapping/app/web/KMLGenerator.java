package org.waterforpeople.mapping.app.web;

import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.dao.CaptionsDAO;
import org.waterforpeople.mapping.dao.GeoRegionDAO;
import org.waterforpeople.mapping.dao.KMLDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.CaptionDefinition;
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
		CaptionsDAO captionDAO = new CaptionsDAO();
		List<CaptionDefinition> captions = captionDAO.list();
		try {
			// loop through accessPoints and bind to variables
			for (AccessPoint ap : entries) {
				try {
					VelocityContext context = new VelocityContext();

					context.put("collectionDate", ap.getCollectionDate());
					context.put("latitude", ap.getLatitude());
					context.put("longitude", ap.getLongitude());
					context.put("altitude", ap.getAltitude());
					context.put("communityCode", ap.getCommunityCode());
					context.put("photoUrl", ap.getPhotoURL());
					if (ap.getPointType().equals(
							AccessPoint.AccessPointType.WATER_POINT)) {
						context.put("typeOfPoint", "Water");
					} else {
						context.put("typeOfPoint", "Sanitation");
					}
					context.put("typeOfWaterPointTechnology", ap
							.getTypeTechnologyString());
					if (ap.getConstructionDateYear() == null
							|| ap.getConstructionDateYear().trim().equals("")) {
						context.put("constructionDateOfWaterPoint", "Unknown");
					} else {
						context.put("constructionDateOfWaterPoint", ap
								.getConstructionDateYear());
					}
					if (ap.getNumberOfHouseholdsUsingPoint() == null) {
						context.put("numberOfHouseholdsUsingWaterPoint",
								"Unkown");
					} else {
						context.put("numberOfHouseholdsUsingWaterPoint", ap
								.getNumberOfHouseholdsUsingPoint());
					}
					context.put("costPer", ap.getCostPer());
					if (ap.getFarthestHouseholdfromPoint() == null
							|| ap.getFarthestHouseholdfromPoint().trim()
									.equals("")) {
						context.put("farthestHouseholdfromWaterPoint", "N/A");
					} else {
						context.put("farthestHouseholdfromWaterPoint", ap
								.getFarthestHouseholdfromPoint());
					}
					context.put("currMgmtStructure", ap
							.getCurrentManagementStructurePoint());
					context.put("waterSystemStatus", ap.getPointStatus()
							.toString());
					context.put("waterPointPhotoCaption", ap
							.getPointPhotoCaption());
					/*
					 * context.put("sanitationPointPhotoURL", ap
					 * .getSanitationPointPhotoURL());
					 * context.put("primaryImprovedSanitationTech", ap
					 * .getPrimaryImprovedSanitationTech());
					 * context.put("percentageOfHouseholdsWithImprovedSanitation"
					 * , ap .getPercentageOfHouseholdsWithImprovedSanitation());
					 */
					context.put("waterPointPhotoCaption", ap
							.getPointPhotoCaption());
					context.put("description", ap.getDescription());
					// Need to check this
					if (ap.getPointType().equals(
							AccessPointType.SANITATION_POINT)) {
						context.put("pinStyle", "pushpinpurple");
					} else {
						if (AccessPoint.Status.FUNCTIONING_HIGH == ap
								.getPointStatus()) {
							context.put("pinStyle", "pushpingreen");
						} else if (AccessPoint.Status.FUNCTIONING_OK == ap
								.getPointStatus()) {
							context.put("pinStyle", "pushpinyellow");
						} else if (AccessPoint.Status.FUNCTIONING_WITH_PROBLEMS == (ap
								.getPointStatus())||ap.getOtherStatus().toLowerCase().trim().equals("broken-down system")) {
							context.put("pinStyle", "pushpinred");
						} else if (AccessPoint.Status.NO_IMPROVED_SYSTEM == ap
								.getPointStatus()) {
							context.put("pinStyle", "pushpinblk");
						}
						for (CaptionDefinition caption : captions) {
							context.put(caption.getCaptionVariableName(),
									caption.getCaptionValue());
						}
					}
					sb.append(mergeContext(context, vmName));
				} catch (Exception ex) {
					// TO-DO Fix
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
						log.info(sbCoor.toString());
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
}
