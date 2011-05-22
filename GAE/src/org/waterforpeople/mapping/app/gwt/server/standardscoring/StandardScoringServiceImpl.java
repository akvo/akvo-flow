package org.waterforpeople.mapping.app.gwt.server.standardscoring;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringDto;
import org.waterforpeople.mapping.app.gwt.client.standardscoring.StandardScoringManagerService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.standards.dao.StandardScoringDao;
import com.gallatinsystems.standards.domain.StandardScoring;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class StandardScoringServiceImpl extends RemoteServiceServlet implements
		StandardScoringManagerService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6828550495789554024L;

	@Override
	public ResponseDto<ArrayList<StandardScoringDto>> listStandardScoring(String cursorString) {
		StandardScoringDao ssDao = new StandardScoringDao();
		List<StandardScoring> ssList = ssDao.list(cursorString);
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
		// TODO Auto-generated method stub

	}

}
