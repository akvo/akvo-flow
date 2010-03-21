package org.waterforpeople.mapping.app.web;

import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.dao.AccessPointDAO;
import org.waterforpeople.mapping.dao.CaptionsDAO;
import org.waterforpeople.mapping.dao.GeoRegionDAO;
import org.waterforpeople.mapping.dao.KMLDAO;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.CaptionDefinition;
import org.waterforpeople.mapping.domain.GeoRegion;

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

	private org.apache.velocity.Template getVelocityTemplate(String templateName)
			throws Exception {
		return engine.getTemplate(templateName);
	}

	public String generateDocument(String placemarksVMName) {
		String document = null;
		try {
			org.apache.velocity.Template t = getVelocityTemplate("Document.vm");
			VelocityContext context = new VelocityContext();
			context.put("Placemark", generatePlacemarks("PlacemarkTabs.vm"));
			context
					.put("regionPlacemark",
							generateRegionOutlines("Regions.vm"));
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			document = writer.toString();
			kmlDAO.saveKML(document);
		} catch (Exception ex) {

		}
		return document;
	}

	public String generatePlacemarks(String vmName) {

		StringBuilder sb = new StringBuilder();
		AccessPointDAO apDAO = new AccessPointDAO();

		List<AccessPoint> entries = apDAO.listAccessPoints();
		CaptionsDAO captionDAO = new CaptionsDAO();
		List<CaptionDefinition> captions = captionDAO.listCaptions();
		try {
			for (AccessPoint ap : entries) {

				org.apache.velocity.Template t = getVelocityTemplate(vmName);
				VelocityContext context = new VelocityContext();

				// loop through accessPoints and bind to variables

				context.put("collectionDate", ap.getCollectionDate());
				context.put("latitude", ap.getLatitude());
				context.put("longitude", ap.getLongitude());
				context.put("altitude", ap.getAltitude());
				context.put("communityCode", ap.getCommunityCode());
				context.put("photoUrl", ap.getPhotoURL());
				context.put("typeOfWaterPointTechnology", ap
						.getTypeTechnology());
				context.put("constructionDateOfWaterPoint", ap
						.getConstructionDate());
				context.put("numberOfHouseholdsUsingWaterPoint", ap
						.getNumberOfHouseholdsUsingPoint());
				context.put("costPer", ap.getCostPer());
				context.put("farthestHouseholdfromWaterPoint", ap
						.getFarthestHouseholdfromPoint());
				context.put("currMgmtStructure", ap
						.getCurrentManagementStructurePoint());
				context.put("waterSystemStatus", ap.getPointStatus());
				context
						.put("waterPointPhotoCaption", ap
								.getPointPhotoCaption());
				/*
				 * context.put("sanitationPointPhotoURL", ap
				 * .getSanitationPointPhotoURL());
				 * context.put("primaryImprovedSanitationTech", ap
				 * .getPrimaryImprovedSanitationTech());
				 * context.put("percentageOfHouseholdsWithImprovedSanitation",
				 * ap .getPercentageOfHouseholdsWithImprovedSanitation());
				 */
				context
						.put("waterPointPhotoCaption", ap
								.getPointPhotoCaption());
				context.put("description", ap.getDescription());
				// Need to check this
				if (ap.getPointStatus().equals("High")) {
					context.put("pinStyle", "pushpingreen");
				} else if (ap.getPointStatus().equals("Ok")) {
					context.put("pinStyle", "pushpinyellow");
				} else if (ap.getPointStatus().equals("Poor")) {
					context.put("pinStyle", "pushpinred");
				} else {
					context.put("pinStyle", "pushpinblk");
				}
				for (CaptionDefinition caption : captions) {
					context.put(caption.getCaptionVariableName(), caption
							.getCaptionValue());
				}

				StringWriter writer = new StringWriter();
				t.merge(context, writer);
				sb.append(writer.toString());

			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating placemarks", e);
		}
		return sb.toString();
	}

	public String generateRegionOutlines(String vmName) {
		StringBuilder sb = new StringBuilder();
		GeoRegionDAO grDAO = new GeoRegionDAO();
		List<GeoRegion> grList = grDAO.listGeoRegions();
		try {

			org.apache.velocity.Template t = getVelocityTemplate(vmName);
			String currUUID = grList.get(0).getUuid();
			StringWriter writer = null;
			VelocityContext context = new VelocityContext();
			StringBuilder sbCoor = new StringBuilder();

			for (int i = 0; i < grList.size(); i++) {
				GeoRegion gr = grList.get(i);
				// loop through GeoRegions and bind to variables

				if (currUUID.equals(gr.getUuid())) {
					sbCoor.append(gr.getLongitude() + "," + gr.getLatitiude()
							+ "," + 0 + "\n");
				} else {
					currUUID = gr.getUuid();
					context.put("coordinateString", sbCoor.toString());
					writer = new StringWriter();
					t.merge(context, writer);
					sb.append(writer.toString());
					log.info(sbCoor.toString());
					context = new VelocityContext();
					sbCoor = new StringBuilder();
					sbCoor.append(gr.getLongitude() + "," + gr.getLatitiude()
							+ "," + 0 + "\n");

				}
			}

			context.put("coordinateString", sbCoor.toString());
			writer = new StringWriter();
			t.merge(context, writer);
			sb.append(writer.toString());
			return sb.toString();

		} catch (Exception e) {
			log.log(Level.SEVERE, "Error generating region outlines", e);
		}
		return null;
	}
}
