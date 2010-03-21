package org.waterforpeople.mapping.dao;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.domain.CaptionDefinition;

import com.gallatinsystems.framework.dao.BaseDAO;

public class CaptionsDAO extends BaseDAO<CaptionDefinition>{

	public CaptionsDAO() {
		super(CaptionDefinition.class);
	}

	private List<CaptionDefinition> listCaptionsHardCoded() {
		List<CaptionDefinition> points = new ArrayList<CaptionDefinition>();
		CaptionDefinition capDef = new CaptionDefinition();
		CaptionDefinition capDef1 = new CaptionDefinition();
		CaptionDefinition capDef2= new CaptionDefinition();
		CaptionDefinition capDef3 = new CaptionDefinition();
		CaptionDefinition capDef4 = new CaptionDefinition();		
		
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

	//TODO: remove this method once we can stop using hard-coded captions
	public List<CaptionDefinition> list() {	
		return listCaptionsHardCoded();
	}
	

}
