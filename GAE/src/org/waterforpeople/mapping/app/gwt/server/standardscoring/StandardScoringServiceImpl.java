/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.waterforpeople.mapping.app.gwt.server.standardscoring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.standardscoring.CompoundStandardDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardContainerDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoreBucketDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerService;
import org.waterforpeople.mapping.domain.AccessPoint;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.common.util.ClassAttributeUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.common.util.StringUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.metric.dao.MetricDao;
import com.gallatinsystems.metric.domain.Metric;
import com.gallatinsystems.standards.dao.CompoundStandardDao;
import com.gallatinsystems.standards.dao.DistanceStandardDao;
import com.gallatinsystems.standards.dao.StandardDao;
import com.gallatinsystems.standards.dao.StandardScoringDao;
import com.gallatinsystems.standards.domain.CompoundStandard;
import com.gallatinsystems.standards.domain.CompoundStandard.Operator;
import com.gallatinsystems.standards.domain.CompoundStandard.RuleType;
import com.gallatinsystems.standards.domain.DistanceStandard;
import com.gallatinsystems.standards.domain.Standard;
import com.gallatinsystems.standards.domain.Standard.StandardComparisons;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.gallatinsystems.standards.domain.Standard.StandardValueType;
import com.gallatinsystems.standards.domain.StandardDef;
import com.gallatinsystems.standards.domain.StandardScoreBucket;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class StandardScoringServiceImpl extends RemoteServiceServlet implements
		StandardScoringManagerService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6828550495789554024L;

	@Override
	public ResponseDto<ArrayList<StandardScoringDto>> listStandardScoring(
			Long scoreBucketId, String cursorString) {
		// StandardScoringDao ssDao = new StandardScoringDao();
		// List<StandardScoring> ssList = null;
		// if (scoreBucketId != null) {
		// ssList = ssDao.listStandardScoring(scoreBucketId);
		// } else {
		// ssList = ssDao.list(cursorString);
		// }
		ArrayList<StandardScoringDto> ssDtoList = new ArrayList<StandardScoringDto>();
		// for (StandardScoring item : ssList) {
		// StandardScoringDto dto = new StandardScoringDto();
		// DtoMarshaller.copyToDto(item, dto);
		// ssDtoList.add(dto);
		// }
		StandardDao standardDao = new StandardDao();
		DistanceStandardDao distanceStandardDao = new DistanceStandardDao();
		AccessPointType apt = null;
		StandardType standardType = null;
		if (scoreBucketId == 0) {
			standardType = StandardType.WaterPointLevelOfService;
			apt = AccessPointType.WATER_POINT;
		} else if (scoreBucketId == 1) {
			standardType = StandardType.WaterPointSustainability;
			apt = AccessPointType.WATER_POINT;
		} else if (scoreBucketId == 2) {
			standardType = StandardType.PublicInstitutionLevelOfService;
			apt = AccessPointType.PUBLIC_INSTITUTION;
		} else if (scoreBucketId == 3) {
			standardType = StandardType.PublicInstitutionSustainability;
			apt = AccessPointType.PUBLIC_INSTITUTION;
		}
		List<Standard> sList = standardDao
				.listByAccessPointTypeAndStandardType(apt, standardType);
		List<DistanceStandard> distanceList = distanceStandardDao
				.listDistanceStandard(standardType, null);
		for (DistanceStandard dsItem : distanceList) {
			StandardScoringDto dtoDist = new StandardScoringDto();
			dtoDist = marshallStandard(dsItem);
			dtoDist.setScoreBucket(standardType.toString());
			ssDtoList.add(dtoDist);
		}
		for (Standard item : sList) {
			StandardScoringDto dto = marshallStandard(item);
			dto.setScoreBucket(standardType.toString());
			ssDtoList.add(dto);
		}
		ResponseDto<ArrayList<StandardScoringDto>> response = new ResponseDto<ArrayList<StandardScoringDto>>();
		response.setCursorString(StandardScoringDao.getCursor(sList));
		response.setPayload(ssDtoList);
		return response;
	}

	private StandardScoringDto marshallStandard(StandardDef item) {
		if (item instanceof Standard) {
			StandardScoringDto dto = new StandardScoringDto();
			dto.setCountryCode(((Standard) item).getCountry());
			dto.setDisplayName(item.getStandardDescription());
			dto.setEvaluateField(((Standard) item).getAccessPointAttribute());
			if (((Standard) item).getCountry() == null)
				dto.setGlobalStandard(true);
			else
				dto.setGlobalStandard(false);
			dto.setKeyId(((Standard) item).getKey().getId());
			if (((Standard) item).getStandardComparison() != null) {
				if (((Standard) item).getStandardComparison().equals(
						Standard.StandardComparisons.equal)) {
					dto.setPositiveOperator("==");
				} else if (((Standard) item).getStandardComparison().equals(
						Standard.StandardComparisons.greaterthan)) {
					dto.setPositiveOperator(">");
				} else if (((Standard) item).getStandardComparison().equals(
						Standard.StandardComparisons.lessthan)) {
					dto.setPositiveOperator("<");
				} else if (((Standard) item).getStandardComparison().equals(
						Standard.StandardComparisons.greaterthanorequal)) {
					dto.setPositiveOperator(">=");
				} else if (((Standard) item).getStandardComparison().equals(
						Standard.StandardComparisons.lessthanorequal)) {
					dto.setPositiveOperator("<=");
				} else if (((Standard) item).getStandardComparison().equals(
						Standard.StandardComparisons.notequal)) {
					dto.setPositiveOperator("!=");
				}
			}
			dto.setMapToObject("AccessPoint");
			for (String crit : ((Standard) item).getPositiveValues()) {
				dto.addPositiveCriteria(crit);
			}
			dto.setPositiveScore(1);
			dto.setPointType("WaterPoint");
			if (((Standard) item).getAcessPointAttributeType() != null) {
				if (((Standard) item).getAcessPointAttributeType().equals(
						StandardValueType.Boolean)) {
					dto.setCriteriaType("Boolean");
				} else if (((Standard) item).getAcessPointAttributeType()
						.equals(StandardValueType.Number)) {
					dto.setCriteriaType("Number");
				} else if (((Standard) item).getAcessPointAttributeType()
						.equals(StandardValueType.String)) {
					dto.setCriteriaType("String");
				}
			}
			dto.setEffectiveEndDate(item.getEffectiveEndDate());
			dto.setEffectiveStartDate(item.getEffectiveStartDate());
			return dto;
		} else {
			StandardScoringDto dtoDist = new StandardScoringDto();
			dtoDist.setGlobalStandard(false);
			dtoDist.setCountryCode(item.getCountryCode());
			dtoDist.setDisplayName(((DistanceStandard) item).getLocationType()
					.toString());
			if (((DistanceStandard) item).getMaxDistance() != null)
				dtoDist.addPositiveCriteria(((DistanceStandard) item)
						.getMaxDistance().toString());
			dtoDist.setPositiveOperator("<");
			dtoDist.setCriteriaType("Distance");
			dtoDist.setPointType("WaterPoint");
			dtoDist.setKeyId(((DistanceStandard) item).getKey().getId());
			dtoDist.setEffectiveStartDate(((DistanceStandard) item)
					.getEffectiveStartDate());
			dtoDist.setEffectiveEndDate(item.getEffectiveEndDate());
			return dtoDist;
		}
	}

	@Override
	public StandardScoringDto save(StandardScoringDto item) {
		StandardDef standard = null;
		if (item.getCriteriaType().equalsIgnoreCase("DISTANCE")) {
			DistanceStandardDao dsDao = new DistanceStandardDao();
			standard = new DistanceStandard();
			standard = setStandardFields(standard, item);
			((DistanceStandard) standard)
					.setLocationType(AccessPoint.LocationType.valueOf(item
							.getEvaluateField()));
			((DistanceStandard) standard).setMaxDistance(Integer.parseInt(item
					.getPositiveCriteria().get(0)));
			standard = dsDao.save((DistanceStandard) standard);
			item = marshallStandard(standard);
			return item;
		} else {
			StandardDao ssDao = new StandardDao();
			standard = new Standard();
			standard = setStandardFields(standard, item);
			((Standard) standard).setPositiveValues(item.getPositiveCriteria());
			((Standard) standard).setAccessPointAttribute(item
					.getEvaluateField());
			((Standard) standard).setAcessPointAttributeType(StandardValueType
					.valueOf(item.getCriteriaType()));
			standard = ssDao.save(((Standard) standard));
			item = marshallStandard(standard);
			return item;
		}
	}

	private StandardDef setStandardFields(StandardDef standard,
			StandardScoringDto item) {
		if (item.getKeyId() != null) {
			// by default, the JDO key kind uses the Simple name
			standard.setKey(KeyFactory.createKey(standard.getClass()
					.getSimpleName(), item.getKeyId()));
		}

		standard.setStandardType(encodeStandardTypeString(item.getScoreBucket()));
		if (item.getPointType().equals("WATER_POINT")) {
			standard.setAccessPointType(AccessPointType.WATER_POINT);
		} else if (item.getPointType().equals("HOUSEHOLD")) {
			standard.setAccessPointType(AccessPointType.HOUSEHOLD);
		} else if (item.getPointType().equals("PUBLICINSTITUTION")) {
			standard.setAccessPointType(AccessPointType.PUBLIC_INSTITUTION);
		} else if (item.getPointType().equals("SANITATION")) {
			standard.setAccessPointType(AccessPointType.SANITATION_POINT);
		}
		if (item.getCountryCode() != null)
			standard.setCountryCode(item.getCountryCode());
		StandardComparisons sc = null;
		if (item.getPositiveOperator().equals("==")) {
			sc = StandardComparisons.equal;
		} else if (item.getPositiveOperator().equals("<")) {
			sc = StandardComparisons.lessthan;
		} else if (item.getPositiveOperator().equals(">")) {
			sc = StandardComparisons.greaterthan;
		} else if (item.getPositiveOperator().equals(">=")) {
			sc = StandardComparisons.greaterthanorequal;
		} else if (item.getPositiveOperator().equals("<=")) {
			sc = StandardComparisons.lessthanorequal;
		} else if (item.getPositiveOperator().equals("!=")) {
			sc = StandardComparisons.notequal;
		}
		standard.setStandardComparisons(sc);

		// criteriaType.addItem("Text", "String");
		// criteriaType.addItem("Number", "Number");
		// criteriaType.addItem("True/False", "Boolean");
		// criteriaType.addItem("Distance", "Distance");
		StandardValueType apAttrType = null;
		if (item.getCriteriaType().equalsIgnoreCase("Number")) {
			apAttrType = StandardValueType.Number;
		} else if (item.getCriteriaType().equalsIgnoreCase("STRING")) {
			apAttrType = StandardValueType.String;
		} else if (item.getCriteriaType().equalsIgnoreCase("BOOLEAN")) {
			apAttrType = StandardValueType.Boolean;
		}
		standard.setEffectiveStartDate(item.getEffectiveStartDate());
		standard.setEffectiveEndDate(item.getEffectiveEndDate());
		standard.setStandardDescription(item.getDisplayName());
		return standard;
	}

	@Override
	public void delete(Long id) {
		StandardDao ssDao = new StandardDao();
		if (ssDao.getByKey(id) != null) {
			ssDao.delete(ssDao.getByKey(id));
		} else {
			DistanceStandardDao dDao = new DistanceStandardDao();
			dDao.delete(dDao.getByKey(id));
		}
	}

	@Override
	public ArrayList<StandardScoreBucketDto> listStandardScoreBuckets() {
		// BaseDAO<StandardScoreBucket> sbDao = new
		// BaseDAO<StandardScoreBucket>(
		// StandardScoreBucket.class);
		// // List<StandardScoreBucket> sbList = sbDao.list("all");
		// ArrayList<StandardScoreBucketDto> sbDtoList = new
		// ArrayList<StandardScoreBucketDto>();
		// for (StandardScoreBucket canonical : sbList) {
		// StandardScoreBucketDto dto = new StandardScoreBucketDto();
		// DtoMarshaller.copyToDto(canonical, dto);
		// sbDtoList.add(dto);
		// }
		ArrayList<StandardScoreBucketDto> sbDtoList = new ArrayList<StandardScoreBucketDto>();
		StandardScoreBucketDto sbItem = new StandardScoreBucketDto();
		sbItem.setName("Water Point Level Of Services");
		sbItem.setKeyId(1L);
		sbDtoList.add(sbItem);
		StandardScoreBucketDto sbItem2 = new StandardScoreBucketDto();
		sbItem.setName("Water Point Sustainability");
		sbItem.setKeyId(2L);
		sbDtoList.add(sbItem2);
		return sbDtoList;
	}

	@Override
	public TreeMap<String, String> listObjectAttributes(String objectName) {
		if (PropertyUtil.getProperty("domainType").equals("accessPoint")) {
			return ClassAttributeUtil.listObjectAttributes(objectName);
		} else {
			TreeMap<String, String> metricsMap = new TreeMap<String, String>();
			MetricDao metricDao = new MetricDao();
			List<Metric> metricsList = metricDao.list("all");
			if (metricsList != null && metricsList.size() > 0) {
				for (Metric metric : metricsList) {
					metricsMap.put(metric.getName(), metric.getName());
				}
			}
			return metricsMap;
		}
	}

	@Override
	public StandardScoreBucketDto save(StandardScoreBucketDto item) {
		StandardScoreBucket ssb = new StandardScoreBucket();
		ssb.setName(item.getName());
		BaseDAO<StandardScoreBucket> ssbDao = new BaseDAO<StandardScoreBucket>(
				StandardScoreBucket.class);
		ssbDao.save(ssb);
		item.setKeyId(ssb.getKey().getId());
		return item;
	}

	@Override
	public ArrayList<StandardContainerDto> listStandardContainer(
			String standardType) {
		HashMap<String, String> attributesMap = null;

		attributesMap = loadStandardAttributesMap();
		ArrayList<StandardContainerDto> stcDtoList = new ArrayList<StandardContainerDto>();

		StandardDao standardDao = new StandardDao();

		List<Standard> standardList = standardDao
				.listByAccessPointTypeAndStandardType(
						AccessPointType.WATER_POINT,
						encodeStandardTypeString(standardType));

		for (Standard itemStandard : standardList) {
			for (Entry<String, String> entry : attributesMap.entrySet()) {
				Method m;
				try {
					m = Standard.class
							.getMethod(
									"get"
											+ StringUtil
													.capitalizeFirstCharacterString(entry
															.getKey()),
									(Class<?>[]) null);

					if (!m.getReturnType().getName().equals("java.lang.String")) {
						m.invoke(itemStandard, (Object[]) null).toString();
					} else {
						m.invoke(itemStandard, (Object[]) null);
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
				}

			}
		}
		return stcDtoList;
	}

	private HashMap<String, String> loadStandardAttributesMap() {
		HashMap<String, String> attributesMap = new HashMap<String, String>();
		attributesMap.put("key", "Key");
		attributesMap.put("accessPointAttribute", "String");
		attributesMap.put("accessPointType", "String");
		attributesMap.put("accessPointAttributeType", "String");
		attributesMap.put("country", "String");
		attributesMap.put("partCompoundRule", "Boolean");
		attributesMap.put("positivesValues", "ArrayList<String>");
		attributesMap.put("StandardComparison", "String");
		attributesMap.put("standardDescription", "String");
		attributesMap.put("standardScope", "String");
		attributesMap.put("standardType", "String");
		return attributesMap;
	}

	@Override
	public Long saveCompoundRule(Long compoundRuleId, String name,
			String standardType, Long leftRuleId, String leftRuleType,
			Long rightRuleId, String rightRuleType, String operator) {
		CompoundStandardDao csDao = new CompoundStandardDao();
		CompoundStandard cs = null;
		if (compoundRuleId != null) {
			cs = csDao.getByKey(compoundRuleId);
		} else {
			cs = new CompoundStandard();
		}
		cs.setName(name);
		cs.setStandardIdLeft(leftRuleId);
		cs.setStandardIdRight(rightRuleId);
		cs.setStandardType(encodeStandardTypeString(standardType));
		cs.setStandardLeftRuleType(RuleType.valueOf(leftRuleType));
		cs.setStandardRightRuleType(RuleType.valueOf(rightRuleType));
		if (operator.equalsIgnoreCase("and"))
			cs.setOperator(Operator.AND);
		else
			cs.setOperator(Operator.OR);
		cs = csDao.save(cs);
		return cs.getKey().getId();
	}

	@Override
	public ResponseDto<ArrayList<CompoundStandardDto>> listCompoundRule(
			String standardType) {
		// TODO Auto-generated method stub
		ResponseDto<ArrayList<CompoundStandardDto>> response = new ResponseDto<ArrayList<CompoundStandardDto>>();

		CompoundStandardDao csDao = new CompoundStandardDao();
		StandardType st = null;
		st = encodeStandardTypeString(standardType);

		List<CompoundStandard> csList = csDao.listByType(st);
		ArrayList<CompoundStandardDto> dtoList = new ArrayList<CompoundStandardDto>();

		for (CompoundStandard item : csList) {
			CompoundStandardDto dto = new CompoundStandardDto();
			dto.setKeyId(item.getKey().getId());
			dto.setOperator(CompoundStandardDto.Operator.valueOf(item
					.getOperator().toString()));
			dto.setStandardIdLeft(item.getStandardIdLeft());
			dto.setStandardLeftRuleType(CompoundStandardDto.RuleType
					.valueOf(item.getStandardLeftRuleType().toString()));
			dto.setStandardIdRight(item.getStandardIdRight());
			dto.setStandardRightRuleType(CompoundStandardDto.RuleType
					.valueOf(item.getStandardRightRuleType().toString()));
			dto.setStandardLeftDesc(item.getStandardLeft()
					.getStandardDescription());
			dto.setStandardRightDesc(item.getStandardRight()
					.getStandardDescription());
			dto.setName(item.getName());
			dtoList.add(dto);
		}
		response.setPayload(dtoList);
		response.setCursorString(BaseDAO.getCursor(csList));
		return response;
	}

	private StandardType encodeStandardTypeString(String standardType) {
		StandardType st = null;
		if (standardType.equalsIgnoreCase("waterpointlevelofservice"))
			st = StandardType.WaterPointLevelOfService;
		else if (standardType
				.equalsIgnoreCase(StandardType.WaterPointSustainability
						.toString()))
			st = StandardType.WaterPointSustainability;
		else if (standardType
				.equalsIgnoreCase(StandardType.PublicInstitutionLevelOfService
						.toString())) {
			st = StandardType.PublicInstitutionLevelOfService;
		} else if (standardType
				.equalsIgnoreCase(StandardType.PublicInstitutionSustainability
						.toString())) {
			st = StandardType.PublicInstitutionSustainability;
		}
		return st;
	}

	@Override
	public void deleteCompoundStandard(Long id) {
		CompoundStandardDao csDao = new CompoundStandardDao();
		csDao.delete(id);
	}
}
