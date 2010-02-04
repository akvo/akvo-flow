package org.waterforpeople.mapping.app.web;

import java.io.StringWriter;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.CaptionManager;

public class KMLGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			System.out.println(writer.toString());
			document = writer.toString();
		} catch (Exception ex) {

		}
		return document;
	}

	PersistenceManager pm = PMF.get().getPersistenceManager();

	private List<AccessPoint> loadAccessPoints(String whereClause) {
		javax.jdo.Query query = pm.newQuery("select from  "
				+ AccessPoint.class.getName());
		List<AccessPoint> entries = (List<AccessPoint>) query.execute();
		return entries;
	}
	
	private List<CaptionManager> loadCaptions(String whereClause) {
		javax.jdo.Query query = pm.newQuery("select from  "
				+ CaptionManager.class.getName());
		List<CaptionManager> entries = (List<CaptionManager>) query.execute();
		return entries;
	}

	private String generatePlacemarks(String vmName) {
		VelocityEngine ve = new VelocityEngine();
		StringBuilder sb = new StringBuilder();
		List<AccessPoint> entries = loadAccessPoints(null);
		try {
			ve.setProperty("runtime.log.logsystem.class",
					"org.apache.velocity.runtime.log.NullLogChute");
			ve.init();
			org.apache.velocity.Template t = ve.getTemplate(vmName);
			VelocityContext context = new VelocityContext();

			// loop through accessPoints and bind to variables
			for (AccessPoint ap : entries) {
				context.put("collectionDate", ap.getCollectionDate());
				context.put("latitude", ap.getLatitude());
				context.put("longitude", ap.getLongitude());
				context.put("altitude", ap.getAltitude());
				context.put("communityCode", ap.getCommunityCode());
				context.put("waterPointPhotoURL", ap.getWaterPointPhotoURL());
				context.put(" typeOfWaterPointTechnology", ap
						.getTypeOfWaterPointTechnology());
				context.put("constructionDateOfWaterPoint", ap
						.getConstructionDateOfWaterPoint());
				context.put("numberOfHouseholdsUsingWaterPoint", ap
						.getNumberOfHouseholdsUsingWaterPoint());
				context.put("costPer", ap.getCostPer());
				context.put("farthestHouseholdfromWaterPoint", ap
						.getFarthestHouseholdfromWaterPoint());
				context.put("CurrentManagementStructureWaterPoint", ap
						.getCurrentManagementStructureWaterPoint());
				context.put("waterSystemStatus", ap.getWaterSystemStatus());
				context.put("sanitationPointPhotoURL", ap
						.getSanitationPointPhotoURL());
				context.put("primaryImprovedSanitationTech", ap
						.getPrimaryImprovedSanitationTech());
				context.put("percentageOfHouseholdsWithImprovedSanitation", ap
						.getPercentageOfHouseholdsWithImprovedSanitation());
				context.put("waterPointPhotoCaption", ap
						.getWaterPointPhotoCaption());
				context.put("description", ap.getDescription());
			}
			
			List<CaptionManager> captions = loadCaptions(null);
			for(CaptionManager caption:captions){
				context.put("collectionDateCaption",caption.getCollectionDateCaption());	
				context.put("communityCodeCaption",caption.getCommunityCodeCaption());
				context.put("waterPointPhotoURLCaption",caption.getWaterPointPhotoURLCaption());
				context.put("typeOfWaterPointTechnologyCaption",caption.getTypeOfWaterPointTechnologyCaption());
				context.put("constructionDateOfWaterPointCaption",caption.getConstructionDateOfWaterPointCaption());
				context.put("numberOfHouseholdsUsingWaterPointCaption",caption.getNumberOfHouseholdsUsingWaterPointCaption());
				context.put("costPerCaption",caption.getCostPerCaption());
				context.put("farthestHouseholdfromWaterPointCaption",caption.getFarthestHouseholdfromWaterPointCaption());
				context.put("CurrentManagementStructureWaterPointCaption",caption.getCurrentManagementStructureWaterPointCaption());
				context.put("waterSystemStatusCaption",caption.getWaterSystemStatusCaption());
				context.put("sanitationPointPhotoURLCaption",caption.getSanitationPointPhotoURLCaption());
				context.put("primaryImprovedSanitationTechCaption",caption.getPrimaryImprovedSanitationTechCaption());
				context.put("percentageOfHouseholdsWithImprovedSanitationCaption",caption.getPercentageOfHouseholdsWithImprovedSanitationCaption());
				context.put("waterPointPhotoCaption",caption.getWaterPointPhotoCaption());
				context.put("descriptionCaption",caption.getDescriptionCaption());
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
