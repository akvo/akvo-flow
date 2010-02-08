package org.waterforpeople.mapping.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;

import org.waterforpeople.mapping.db.PMF;
import org.waterforpeople.mapping.domain.CaptionDefinition;

public class CaptionsDAO {
	PersistenceManager pm;

	public Long save(CaptionDefinition ap) {
		Boolean savedSuccessFlag = false;
		pm.makePersistent(ap);
		return ap.getId();
	}

	public CaptionsDAO() {
		init();
	}

	private List<CaptionDefinition> listCaptionsHardCoded() {
		List<CaptionDefinition> points = new ArrayList<CaptionDefinition>();
		CaptionDefinition capDef = new CaptionDefinition();
		CaptionDefinition capDef1 = new CaptionDefinition();
		CaptionDefinition capDef2= new CaptionDefinition();
		CaptionDefinition capDef3 = new CaptionDefinition();
		CaptionDefinition capDef4 = new CaptionDefinition();
		CaptionDefinition capDef5 = new CaptionDefinition();
		
		capDef.setCaptionVariableName("typeOfWaterPointTechnologyCaption");
		capDef.setCaptionValue("Type of Water Point Technology");
		points.add(capDef);
		capDef1.setCaptionVariableName("constructionDateOfWaterPointCaption");
		capDef1.setCaptionValue("Construction Date of Water Point");
		points.add(capDef1);
		capDef2.setCaptionVariableName("numberOfHouseholdsUsingWaterPointCaption");
		capDef2.setCaptionValue("Number of Households Using Water Point");
		points.add(capDef2);
		capDef3.setCaptionVariableName("costCaption");
		capDef3.setCaptionValue("Cost");
		points.add(capDef3);
		capDef4.setCaptionVariableName("waterSystemStatusCaption");
		capDef4.setCaptionValue("Water System Status");
		points.add(capDef4);
		return points;
	}

	public List<CaptionDefinition> listCaptions() {
		// javax.jdo.Query query = pm.newQuery(CaptionDefinition.class);
		// List<CaptionDefinition> points = (List<CaptionDefinition>) query
		// .execute();

		return listCaptionsHardCoded();
	}

	private void init() {
		pm = PMF.get().getPersistenceManager();
	}

}
