package org.waterforpeople.mapping.dao;

import java.io.StringWriter;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.CaptionDefinition;
import org.waterforpeople.mapping.domain.GeoRegion;

public class KMLDAO {
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
			VelocityContext context = new VelocityContext();

			// loop through GeoRegions and bind to variables
			for (GeoRegion gr : grList) {
				context.put("latitude", gr.getLatitiude());
				context.put("longitude", gr.getLatitiude());
			}
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			System.out.println(writer.toString());
			sb.append(writer.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();
	}
}
