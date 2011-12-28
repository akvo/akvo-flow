package org.waterforpeople.mapping.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.dao.StandardDao;
import com.gallatinsystems.standards.domain.LevelOfServiceScore;
import com.gallatinsystems.standards.domain.LevelOfServiceScore.LevelOfServiceScoreType;
import com.gallatinsystems.standards.domain.Standard;

public class ScoringHelper {

	public void scoreWaterPointLevelOfService(AccessPoint ap) {
		LevelOfServiceScore los = new LevelOfServiceScore();
		los.setScoreType(LevelOfServiceScoreType.WaterPointLevelOfService);
		ArrayList<String> scoreDetails = new ArrayList<String>();
		if (ap.getImprovedWaterPointFlag()) {
			StandardDao standardDao = new StandardDao();
			List<Standard> standardList = standardDao
					.listByAccessPointType(AccessPointType.WATER_POINT);
			if (standardList != null) {
				for (Standard standard : standardList) {
					String value= getAccessPointFieldValue(ap, standard.getAccessPointAttribute());
					if(standard.getAcessPointAttributeType().equals("Boolean")){
						if(compareBoolean(value, Boolean.parseBoolean(standard.getPositiveValues().get(0)))){
							los.setScore(los.getScore()+1);
							scoreDetails.add("Plus 1 for " + standard.getStandardDescription() + " WaterPoint " +standard.getAccessPointAttribute()+ "Value " + value);
						}else{
							scoreDetails.add("Plus 0 for " + standard.getStandardDescription() + " WaterPoint " +standard.getAccessPointAttribute()+ "Value " + value);
						}
					}else if(standard.getAcessPointAttributeType().equals("String")){
						if(this.compareStrings(value, standard.getPositiveValues())){
							los.setScore(los.getScore()+1);
							scoreDetails.add("Plus 1 for " + standard.getStandardDescription() + " WaterPoint " +standard.getAccessPointAttribute()+ "Value " + value);
						}else{
							scoreDetails.add("Plus 0 for " + standard.getStandardDescription() + " WaterPoint " +standard.getAccessPointAttribute()+ "Value " + value);
						}
					}else if(standard.getAcessPointAttributeType().equals("Integer")){
						if(this.compareLessThanEqualInteger(value, Integer.parseInt(standard.getPositiveValues().get(0)))){
							los.setScore(los.getScore()+1);
							scoreDetails.add("Plus 1 for " + standard.getStandardDescription() + " WaterPoint " +standard.getAccessPointAttribute()+ "Value " + value);
						}else{
							scoreDetails.add("Plus 0 for " + standard.getStandardDescription() + " WaterPoint " +standard.getAccessPointAttribute()+ "Value " + value);
						}
					}
				}
				los.setScoreDetails(scoreDetails);
				BaseDAO<LevelOfServiceScore> losDao = new BaseDAO<LevelOfServiceScore>(LevelOfServiceScore.class);
				losDao.save(los);
			}
		} else {
			los.setScore(0);
			ArrayList<String> details = new ArrayList<String>();
			details.add("0 not improved waterpoint");
		}
	}

	public void scoreWaterPointSustainability(AccessPoint ap) {

	}

	public void scorePILevelOfService(AccessPoint ap) {

	}

	public void scorePISustainability(AccessPoint ap) {

	}

	private String getAccessPointFieldValue(AccessPoint ap,
			String accessPointAttribute) {
		Method m;
		String value=null;
		try {
			m = AccessPoint.class.getMethod("get" + accessPointAttribute, null);
			value = (String) m.invoke(ap, null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	private Boolean compareBoolean(String uncastvalue, Boolean compareTo){
		Boolean answer = null;
		Boolean value = Boolean.parseBoolean(uncastvalue);
		if(value.equals(compareTo)){
			answer=true;
		}else{
			answer=false;
		}
		return answer;
	}
	
	private Boolean compareStrings(String apvalue,ArrayList<String> valueList){
		Boolean answer = false;
		for(String item:valueList){
			if(item.equals(apvalue)){
				answer=true;
			}
		}
		return answer;
	}
	
	private Boolean compareLessThanEqualInteger(String apvalue,Integer compareTo ){
		Boolean answer = false;
		if(Integer.parseInt(apvalue)<=compareTo){
			answer=true;
		}
		return answer;
	}
}
