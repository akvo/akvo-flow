package org.waterforpeople.mapping.app.gwt.server.standardscoring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardContainerDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoreBucketDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.domain.AccessPoint.AccessPointType;

import com.gallatinsystems.common.util.ClassAttributeUtil;
import com.gallatinsystems.common.util.StringUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.standards.dao.StandardDao;
import com.gallatinsystems.standards.dao.StandardScoringDao;
import com.gallatinsystems.standards.domain.Standard;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.gallatinsystems.standards.domain.StandardScoreBucket;
import com.gallatinsystems.standards.domain.StandardScoring;
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
		StandardScoringDao ssDao = new StandardScoringDao();
		List<StandardScoring> ssList = null;
		if (scoreBucketId != null) {
			ssList = ssDao.listStandardScoring(scoreBucketId);
		} else {
			ssList = ssDao.list(cursorString);
		}
		ArrayList<StandardScoringDto> ssDtoList = new ArrayList<StandardScoringDto>();
		for (StandardScoring item : ssList) {
			StandardScoringDto dto = new StandardScoringDto();
			DtoMarshaller.copyToDto(item, dto);
			ssDtoList.add(dto);
		}
		ResponseDto<ArrayList<StandardScoringDto>> response = new ResponseDto<ArrayList<StandardScoringDto>>();
		response.setCursorString(StandardScoringDao.getCursor(ssList));
		response.setPayload(ssDtoList);
		return response;
	}

	@Override
	public StandardScoringDto save(StandardScoringDto item) {
		StandardScoringDao ssDao = new StandardScoringDao();
		StandardScoring canonical = new StandardScoring();
		DtoMarshaller.copyToCanonical(canonical, item);
		ssDao.save(canonical);
		DtoMarshaller.copyToDto(canonical, item);
		return item;
	}

	@Override
	public void delete(Long id) {
		StandardScoringDao ssDao = new StandardScoringDao();
		ssDao.delete(ssDao.getByKey(id));
	}

	@Override
	public ArrayList<StandardScoreBucketDto> listStandardScoreBuckets() {
		BaseDAO<StandardScoreBucket> sbDao = new BaseDAO<StandardScoreBucket>(
				StandardScoreBucket.class);
		List<StandardScoreBucket> sbList = sbDao.list("all");
		ArrayList<StandardScoreBucketDto> sbDtoList = new ArrayList<StandardScoreBucketDto>();
		for (StandardScoreBucket canonical : sbList) {
			StandardScoreBucketDto dto = new StandardScoreBucketDto();
			DtoMarshaller.copyToDto(canonical, dto);
			sbDtoList.add(dto);
		}
		return sbDtoList;
	}

	@Override
	public TreeMap<String, String> listObjectAttributes(String objectName) {
		return ClassAttributeUtil.listObjectAttributes(objectName);

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
		HashMap<String,String> attributesMap = null;
		
		attributesMap  = loadStandardAttributesMap();
		ArrayList<StandardContainerDto> stcDtoList = new ArrayList<StandardContainerDto>();
		String value = null;
		StandardDao standardDao = new StandardDao();
		@SuppressWarnings("unchecked")
		ArrayList<Standard> standardList = (ArrayList) standardDao
				.listByAccessPointTypeAndStandardType(
						AccessPointType.WATER_POINT,
						StandardType.WaterPointLevelOfService);

		for (Standard itemStandard : standardList) {
			for (Entry<String, String> entry: attributesMap.entrySet()) {
				Method m;
				try {
					m = Standard.class
							.getMethod(
									"get"
											+ StringUtil
													.capitalizeFirstCharacterString(entry.getKey()),
									null);

					if (!m.getReturnType().getName().equals("java.lang.String")) {
						value = m.invoke(itemStandard, null).toString();
					} else {
						value = (String) m.invoke(itemStandard, null);
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
		HashMap<String,String> attributesMap = new HashMap<String,String>();
		attributesMap.put("key", "Key");
		attributesMap.put("accessPointAttribute", "String");
		attributesMap.put("accessPointType", "String");
		attributesMap.put("accessPointAttributeType", "String");
		attributesMap.put("country", "String");
		attributesMap.put("partCompoundRule", "Boolean");
		attributesMap.put("positivesValues","ArrayList<String>");
		attributesMap.put("StandardComparison", "String");
		attributesMap.put("standardDescription", "String");
		attributesMap.put("standardScope", "String");
		attributesMap.put("standardType", "String");
		return attributesMap;
	}
}
