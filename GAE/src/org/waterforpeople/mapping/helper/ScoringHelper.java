package org.waterforpeople.mapping.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.common.util.StringUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.standards.dao.StandardDao;
import com.gallatinsystems.standards.domain.LevelOfServiceScore;
import com.gallatinsystems.standards.domain.LevelOfServiceScore.ScoreObject;
import com.gallatinsystems.standards.domain.Standard;
import com.gallatinsystems.standards.domain.Standard.StandardComparisons;
import com.gallatinsystems.standards.domain.Standard.StandardScope;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.gallatinsystems.standards.domain.Standard.StandardValueType;

public class ScoringHelper {
	private static Logger log = Logger.getLogger(ScoringHelper.class
			.getName());

	public void scoreWaterPointByLevelOfService(AccessPoint ap, StandardType scoreType) {
		LevelOfServiceScore los = new LevelOfServiceScore();
		los.setScoreType(scoreType);
		los.setObjectKey(ap.getKey());
		los.setScoreObject(ScoreObject.AccessPoint);
		ArrayList<String> scoreDetails = new ArrayList<String>();
		BaseDAO<LevelOfServiceScore> losDao = new BaseDAO<LevelOfServiceScore>(
				LevelOfServiceScore.class);
		if (ap.getImprovedWaterPointFlag()) {
			StandardDao standardDao = new StandardDao();
			List<Standard> standardList = standardDao.listByAccessPointTypeAndStandardType(AccessPointType.WATER_POINT, scoreType);
			if (standardList != null) {
				for (Standard standard : standardList) {
					if (standard.getStandardScope()
							.equals(StandardScope.Global)
							|| (standard.getStandardScope()
									.equals(StandardScope.Local))
							&& standard.getCountry()
									.equals(ap.getCountryCode())) {
						String value = getAccessPointFieldValue(ap,
								standard.getAccessPointAttribute());
						if (standard.getAcessPointAttributeType().equals(
								StandardValueType.Boolean)) {
							if (compareBoolean(value,
									Boolean.parseBoolean(standard
											.getPositiveValues().get(0)))) {
								los.setScore(los.getScore() + 1);
								scoreDetails.add(formScoreDetailMessage(1,
										standard.getStandardDescription(),
										" WaterPoint ",
										standard.getAccessPointAttribute(),
										value,
										standard.getStandardComparison().toString(),
										standard.getPositiveValues().toString()));
							} else {
								scoreDetails.add(formScoreDetailMessage(0,
										standard.getStandardDescription(),
										" WaterPoint ",
										standard.getAccessPointAttribute(),
										value,
										standard.getStandardComparison().toString(),
										standard.getPositiveValues().toString()));
							}
						} else if (standard.getAcessPointAttributeType()
								.equals(StandardValueType.String)) {
							if (this.compareStrings(value,
									standard.getPositiveValues())) {
								los.setScore(los.getScore() + 1);
								scoreDetails.add(formScoreDetailMessage(1,
										standard.getStandardDescription(),
										" WaterPoint ",
										standard.getAccessPointAttribute(),
										value,
										standard.getStandardComparison().toString(),
										standard.getPositiveValues().toString()));
							} else {
								scoreDetails.add(formScoreDetailMessage(0,
										standard.getStandardDescription(),
										" WaterPoint ",
										standard.getAccessPointAttribute(),
										value,
										standard.getStandardComparison().toString(),
										standard.getPositiveValues().toString()));
							}
						} else if (standard.getAcessPointAttributeType()
								.equals(StandardValueType.Number)) {
							if (this.compareDouble(standard
									.getStandardComparison(), value, Double
									.parseDouble(standard.getPositiveValues()
											.get(0)))) {
								los.setScore(los.getScore() + 1);
								scoreDetails.add(formScoreDetailMessage(1,
										standard.getStandardDescription(),
										" WaterPoint ",
										standard.getAccessPointAttribute(),
										value,
										standard.getStandardComparison().toString(),
										standard.getPositiveValues().toString()));
							} else {
								scoreDetails.add(formScoreDetailMessage(0,
										standard.getStandardDescription(),
										" WaterPoint ",
										standard.getAccessPointAttribute(),
										value,
										standard.getStandardComparison().toString(),
										standard.getPositiveValues().toString()));
							}
						}
					}
				}
				los.setScoreDetails(scoreDetails);
				
				losDao.save(los);
			}
		} else {
			los.setScore(0);
			ArrayList<String> details = new ArrayList<String>();
			details.add("0 not improved waterpoint");
			losDao.save(los);
		}
	}

	private String formScoreDetailMessage(Integer score, String desc,
			String type, String attribute, String value, String operator,
			String compareTo) {
		return "Plus " + score + " for " + desc + " " + type + " " + attribute
				+ " Value " + value + " " + operator + " " + compareTo;

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
		String value = null;
		try {
			m = AccessPoint.class
					.getMethod(
							"get"
									+ StringUtil
											.capitalizeFirstCharacterString(accessPointAttribute),
							null);
			if (!m.getReturnType().getName().equals("java.lang.String")) {
				value = m.invoke(ap, null).toString();
			} else {
				value = (String) m.invoke(ap, null);
			}
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
		} catch(NullPointerException e){
			log.info("AccessPoint Attribute: " + accessPointAttribute );
			e.printStackTrace();
		}
		return value;
	}

	private Boolean compareBoolean(String uncastvalue, Boolean compareTo) {
		Boolean answer = null;
		Boolean value = Boolean.parseBoolean(uncastvalue);
		if (value.equals(compareTo)) {
			answer = true;
		} else {
			answer = false;
		}
		return answer;
	}

	private Boolean compareStrings(String apvalue, ArrayList<String> valueList) {
		Boolean answer = false;
		for (String item : valueList) {
			if (item.equals(apvalue)) {
				answer = true;
			}
		}
		return answer;
	}

	private Boolean compareDouble(
			Standard.StandardComparisons standardComparisons, String apvalue,
			Double compareTo) {
		Boolean answer = false;
		if (standardComparisons.equals(StandardComparisons.lessthan)) {
			if (Double.parseDouble(apvalue) < compareTo) {
				answer = true;
			}
		} else if (standardComparisons
				.equals(StandardComparisons.lessthanorequal)) {
			if (Double.parseDouble(apvalue) <= compareTo) {
				answer = true;
			}
		} else if (standardComparisons.equals(StandardComparisons.greaterthan)) {
			if (Double.parseDouble(apvalue) > compareTo) {
				answer = true;
			}
		} else if (standardComparisons
				.equals(StandardComparisons.greaterthanorequal)) {
			if (Double.parseDouble(apvalue) >= compareTo) {
				answer = true;
			}
		} else if (standardComparisons.equals(StandardComparisons.equal)) {
			if (Double.parseDouble(apvalue) == compareTo) {
				answer = true;
			}
		}
		return answer;
	}
}
