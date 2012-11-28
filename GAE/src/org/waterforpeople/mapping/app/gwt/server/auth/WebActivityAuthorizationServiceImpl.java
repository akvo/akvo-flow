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

package org.waterforpeople.mapping.app.gwt.server.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationDto;
import org.waterforpeople.mapping.app.gwt.client.auth.WebActivityAuthorizationService;
import org.waterforpeople.mapping.app.gwt.client.survey.SurveyDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.auth.dao.WebActivityAuthorizationDao;
import com.gallatinsystems.auth.domain.WebActivityAuthorization;
import com.gallatinsystems.framework.gwt.dto.client.BaseDto;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.gallatinsystems.user.app.gwt.server.UserServiceImpl;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class WebActivityAuthorizationServiceImpl extends RemoteServiceServlet
		implements WebActivityAuthorizationService {

	private static final long serialVersionUID = -6092289285517276493L;
	private WebActivityAuthorizationDao authDao;
	private Random random;
	private UserServiceImpl userService;
	private SurveyDAO surveyDao;

	public WebActivityAuthorizationServiceImpl() {
		authDao = new WebActivityAuthorizationDao();
		random = new Random();
		userService = new UserServiceImpl();
		surveyDao = new SurveyDAO();
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
		UserDto user = userService.getCurrentUserConfig(false);
		if (authList != null) {
			WebActivityAuthorization authToUse = null;
			for (WebActivityAuthorization auth : authList) {
				// make sure the logged-in user is the right one
				if (WebActivityAuthorizationDto.USER_TYPE.equals(auth
						.getAuthType())) {
					if (user != null && user.getKeyId() != null
							&& user.getKeyId().equals(auth.getUserId())) {
						authToUse = auth;
						break;
					}
				} else {
					authToUse = auth;
					break;
				}
			}
			if (authToUse != null) {
				dto = convertToDto(authList.get(0));
			}
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
				dtoList.add(convertToDto(a));
			}
		}
		return resp;
	}

	@Override
	public WebActivityAuthorizationDto saveAuthorization(
			WebActivityAuthorizationDto authDto) {
		if (authDto != null) {
			WebActivityAuthorization auth = convertToCannonical(authDto);
			if (auth.getToken() == null) {
				auth.setToken(generateToken());
			}
			auth = authDao.save(auth);
			authDto.setKeyId(auth.getKey().getId());
			authDto.setToken(auth.getToken());
		}
		return authDto;
	}

	@Override	
	public ResponseDto<HashMap<BaseDto, WebActivityAuthorizationDto>> listUserAuthorizations(
			String activityName) {
		UserDto user = userService.getCurrentUserConfig(false);

		ResponseDto<HashMap<BaseDto, WebActivityAuthorizationDto>> response = new ResponseDto<HashMap<BaseDto, WebActivityAuthorizationDto>>();
		HashMap<BaseDto, WebActivityAuthorizationDto> authMap = new HashMap<BaseDto, WebActivityAuthorizationDto>();
		Map<String, Survey> surveyCache = new HashMap<String, Survey>();
		if (user != null) {
			List<WebActivityAuthorization> authList = authDao.listByUser(user
					.getKeyId(), activityName);
			if (authList != null) {
				for (WebActivityAuthorization auth : authList) {
					if (surveyCache.get(auth.getPayload()) == null) {
						Survey s = surveyDao
								.getById(new Long(auth.getPayload()));
						if (s != null) {
							surveyCache.put(auth.getPayload(), s);
							SurveyDto sDto = new SurveyDto();
							sDto.setKeyId(s.getKey().getId());
							sDto.setName(s.getName());
							sDto.setPath(s.getPath());
							authMap.put(sDto, convertToDto(auth));
						}
					}
				}
			}
		}
		response.setPayload(authMap);
		return response;
	}

	/**
	 * converts a domain object to a dto, performing the steps needed to fix
	 * errors introduced by beanutils
	 * 
	 * @param auth
	 * @return
	 */
	private WebActivityAuthorizationDto convertToDto(
			WebActivityAuthorization auth) {

		WebActivityAuthorizationDto dto = null;
		if (auth != null) {
			dto = new WebActivityAuthorizationDto();
			DtoMarshaller.copyToDto(auth, dto);
			if (auth.getMaxUses() == null) {
				dto.setMaxUses(null);
			}
		}
		return dto;
	}

	/**
	 * converts a dto object to a domain object, performing the steps needed to
	 * fix errors introduced by beanutils
	 * 
	 * @param auth
	 * @return
	 */
	private WebActivityAuthorization convertToCannonical(
			WebActivityAuthorizationDto dto) {
		WebActivityAuthorization auth = null;
		if (dto != null) {
			auth = new WebActivityAuthorization();
			DtoMarshaller.copyToCanonical(auth, dto);
			if (dto.getMaxUses() == null) {
				auth.setMaxUses(null);
			}
		}
		return auth;
	}

	private String generateToken() {
		Long code = Long.parseLong(random.nextInt(900000) + ""
				+ random.nextInt(1800000));
		return Long.toString(code, 36);
	}

}
