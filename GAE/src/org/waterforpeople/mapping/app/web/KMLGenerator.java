package org.waterforpeople.mapping.app.web;

import java.io.StringWriter;
import java.util.List;
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

	public KMLGenerator() {
		kmlDAO = new KMLDAO();		
	}

	public String generateRegionDocumentString(String regionVMName) {
		String regionKML = generateRegionOutlines(regionVMName);
		return regionKML;
	}

	public String generateDocument(String placemarksVMName) {
		VelocityEngine ve = new VelocityEngine();
		String document = null;
		try {
			ve.setProperty("runtime.log.logsystem.class",
					"org.apache.velocity.runtime.log.NullLogChute");
			ve.init();
			org.apache.velocity.Template t = ve.getTemplate("Document.vm");
			VelocityContext context = new VelocityContext();

			context.put("Placemark", generatePlacemarks("PlacemarkTabs.vm"));
			context
					.put("regionPlacemark",
							generateRegionOutlines("Regions.vm"));
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			System.out.println(writer.toString());
			document = writer.toString();
			kmlDAO.saveKML(document);
		} catch (Exception ex) {

		}
		return document;
	}

	public String generatePlacemarks(String vmName) {
		VelocityEngine ve = new VelocityEngine();
		StringBuilder sb = new StringBuilder();
		AccessPointDAO apDAO = new AccessPointDAO();

		List<AccessPoint> entries = apDAO.listAccessPoints();
		CaptionsDAO captionDAO = new CaptionsDAO();
		List<CaptionDefinition> captions = captionDAO.listCaptions();
		try {
			for (AccessPoint ap : entries) {
				ve.setProperty("runtime.log.logsystem.class",
						"org.apache.velocity.runtime.log.NullLogChute");
				ve.init();
				org.apache.velocity.Template t = ve.getTemplate(vmName);
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
				System.out.println(writer.toString());
				sb.append(writer.toString());

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	public String generateRegionOutlines(String vmName) {
		StringBuilder sb = new StringBuilder();
		GeoRegionDAO grDAO = new GeoRegionDAO();
		List<GeoRegion> grList = grDAO.listGeoRegions();

		VelocityEngine ve = new VelocityEngine();
		ve.setProperty("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.NullLogChute");
		try {
			ve.init();
			org.apache.velocity.Template t = ve.getTemplate(vmName);
			StringBuilder sbPlacemarks = new StringBuilder();

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
			System.out.println(sb.toString());
			return sb.toString();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
