package org.waterforpeople.mapping.app.web;

import java.io.StringWriter;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.waterforpeople.mapping.dao.KMLDAO;
import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.CaptionDefinition;

public class KMLGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public String generateRegionDocumentString(String regionVMName){
		KMLDAO kmlDAO = new KMLDAO();
		String regionKML = kmlDAO.generateRegionOutlines(regionVMName);
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
			KMLDAO kmlDAO = new KMLDAO();
			context.put("Placemark", kmlDAO.generatePlacemarks("PlacemarkTabs.vm"));
			context.put("regionPlacemark", kmlDAO.generateRegionOutlines("Regions.vm"));
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			System.out.println(writer.toString());
			document = writer.toString();
			kmlDAO.saveKML(document);
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

	private List<CaptionDefinition> loadCaptions(String whereClause) {
		javax.jdo.Query query = pm.newQuery("select from  "
				+ CaptionDefinition.class.getName());
		List<CaptionDefinition> entries = (List<CaptionDefinition>) query
				.execute();
		return entries;
	}


}
