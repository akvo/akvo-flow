package org.waterforpeople.mapping.app.gwt.server.standardscoring;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoreBucketDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.common.util.ClassAttributeUtil;
import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.standards.dao.StandardScoringDao;
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
	public ResponseDto<ArrayList<StandardScoringDto>> listStandardScoring(Long scoreBucketId,String cursorString) {
		StandardScoringDao ssDao = new StandardScoringDao();
		List<StandardScoring> ssList = null;
		if(scoreBucketId!=null){
			ssList = ssDao.listStandardScoring(scoreBucketId);
		}else{
			ssList= ssDao.list(cursorString);
		}
		ArrayList<StandardScoringDto> ssDtoList = new ArrayList<StandardScoringDto>();
		for(StandardScoring item: ssList){
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
		DtoMarshaller.copyToDto(canonical,item);
		return item;
	}

	@Override
	public void delete(Long id) {
		StandardScoringDao ssDao = new StandardScoringDao();
		ssDao.delete(ssDao.getByKey(id));
	}

	@Override
	public ArrayList<StandardScoreBucketDto> listStandardScoreBuckets() {
		BaseDAO<StandardScoreBucket> sbDao = new BaseDAO<StandardScoreBucket>(StandardScoreBucket.class);
		List<StandardScoreBucket> sbList = sbDao.list("all");
		ArrayList<StandardScoreBucketDto> sbDtoList = new ArrayList<StandardScoreBucketDto>();
		for(StandardScoreBucket canonical:sbList){
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
		BaseDAO<StandardScoreBucket> ssbDao = new BaseDAO<StandardScoreBucket>(StandardScoreBucket.class);
		ssbDao.save(ssb);
		item.setKeyId(ssb.getKey().getId());
		return item;
	}
}
