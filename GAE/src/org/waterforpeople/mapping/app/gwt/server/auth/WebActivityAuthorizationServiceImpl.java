package org.waterforpeople.mapping.app.gwt.server.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationDto;
import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationService;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.auth.dao.WebActivityAuthorizationDao;
import com.gallatinsystems.auth.domain.WebActivityAuthorization;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class WebActivityAuthorizationServiceImpl extends RemoteServiceServlet
		implements WebActivityAuthorizationService {

	private static final long serialVersionUID = -6092289285517276493L;
	private WebActivityAuthorizationDao authDao;
	private Random random;

	public WebActivityAuthorizationServiceImpl() {
		authDao = new WebActivityAuthorizationDao();
		random = new Random();
	}

	@Override
	public void deleteAuthorization(WebActivityAuthorizationDto dto) {
		if (dto.getKeyId() != null) {
			WebActivityAuthorization auth = authDao.getByKey(dto.getKeyId());
			if (auth != null) {
				authDao.delete(auth);
			}
		}
	}

	@Override
	public WebActivityAuthorizationDto isAuthorized(String token,
			String activityName) {
		List<WebActivityAuthorization> authList = authDao.listByToken(token,
				activityName, null, true);
		WebActivityAuthorizationDto dto = null;
		if (authList != null && authList.size() > 0) {
			dto = new WebActivityAuthorizationDto();
			DtoMarshaller.copyToDto(authList.get(0), dto);
		}
		return dto;
	}

	@Override
	public ResponseDto<ArrayList<WebActivityAuthorizationDto>> listAuthorizations(
			String cursor) {
		List<WebActivityAuthorization> authList = authDao.list(cursor);
		ResponseDto<ArrayList<WebActivityAuthorizationDto>> resp = new ResponseDto<ArrayList<WebActivityAuthorizationDto>>();
		if (authList != null) {
			ArrayList<WebActivityAuthorizationDto> dtoList = new ArrayList<WebActivityAuthorizationDto>();
			resp.setPayload(dtoList);
			resp.setCursorString(WebActivityAuthorizationDao
					.getCursor(authList));
			for (WebActivityAuthorization a : authList) {
				WebActivityAuthorizationDto dto = new WebActivityAuthorizationDto();
				DtoMarshaller.copyToDto(a, dto);
				dtoList.add(dto);
			}
		}
		return resp;
	}

	@Override
	public WebActivityAuthorizationDto saveAuthorization(
			WebActivityAuthorizationDto authDto) {
		if (authDto != null) {
			WebActivityAuthorization auth = new WebActivityAuthorization();
			DtoMarshaller.copyToCanonical(auth, authDto);
			if (auth.getToken() == null) {
				auth.setToken(generateToken());
			}
			auth = authDao.save(auth);
			authDto.setKeyId(auth.getKey().getId());
		}
		return authDto;
	}

	private String generateToken() {
		Long code = Long.parseLong(random.nextInt(900000) + ""
				+ random.nextInt(1800000));
		return Long.toString(code, 36);
	}
}
