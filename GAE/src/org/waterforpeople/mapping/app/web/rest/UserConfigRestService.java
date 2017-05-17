/*
 *  Copyright (C) 2012,2017 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.app.web.rest.dto.UserConfigPayload;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.user.app.gwt.client.UserConfigDto;
import com.gallatinsystems.user.dao.UserConfigDao;
import com.gallatinsystems.user.domain.UserConfig;

@Controller
@RequestMapping("/user_configs")
public class UserConfigRestService {

    private UserConfigDao userConfigDao = new UserConfigDao();

    // list all userConfigs
    @RequestMapping(method = RequestMethod.GET, value = "/all")
    @ResponseBody
    public Map<String, List<UserConfigDto>> listUserConfigs() {
        final Map<String, List<UserConfigDto>> response = new HashMap<String, List<UserConfigDto>>();
        List<UserConfigDto> results = new ArrayList<UserConfigDto>();
        List<UserConfig> userConfigs = userConfigDao
                .list(Constants.ALL_RESULTS);
        if (userConfigs != null) {
            for (UserConfig s : userConfigs) {
                UserConfigDto dto = new UserConfigDto();
                DtoMarshaller.copyToDto(s, dto);

                results.add(dto);
            }
        }
        response.put("user_configs", results);
        return response;
    }

    // list userConfig by user id
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<UserConfigDto>> listConfigsByUserId(
            @RequestParam(value = "userId", defaultValue = "")
            Long userId) {
        final Map<String, List<UserConfigDto>> response = new HashMap<String, List<UserConfigDto>>();
        List<UserConfigDto> results = new ArrayList<UserConfigDto>();
        List<UserConfig> userConfigs = null;

        if (userId != null) {
            userConfigs = userConfigDao.listConfigsByUser(userId);
        }

        if (userConfigs != null) {
            for (UserConfig s : userConfigs) {
                UserConfigDto dto = new UserConfigDto();
                DtoMarshaller.copyToDto(s, dto);
                results.add(dto);
            }
        }
        response.put("user_configs", results);
        return response;
    }

    // find a single userConfig by the userConfigId
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, UserConfigDto> findUserConfig(@PathVariable("id")
    Long id) {
        final Map<String, UserConfigDto> response = new HashMap<String, UserConfigDto>();
        UserConfig s = userConfigDao.getByKey(id);
        UserConfigDto dto = null;
        if (s != null) {
            dto = new UserConfigDto();
            DtoMarshaller.copyToDto(s, dto);
        }
        response.put("user_config", dto);
        return response;
    }

    // delete userConfig by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteUserConfigById(
            @PathVariable("id")
            Long id) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        UserConfig s = userConfigDao.getByKey(id);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if userConfig exists in the datastore
        if (s != null) {
            // delete userConfig group
            userConfigDao.delete(s);
            statusDto.setStatus("ok");
        }
        response.put("meta", statusDto);
        return response;
    }

    // update existing userConfig
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingUserConfig(
            @RequestBody
            UserConfigPayload payLoad) {
        final UserConfigDto userConfigDto = payLoad.getUser_config();
        final Map<String, Object> response = new HashMap<String, Object>();
        UserConfigDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid userConfigDto, continue.
        // Otherwise,
        // server will respond with 400 Bad Request
        if (userConfigDto != null) {
            Long keyId = userConfigDto.getKeyId();
            UserConfig s;

            // if the userConfigDto has a key, try to get the userConfig.
            if (keyId != null) {
                s = userConfigDao.getByKey(keyId);
                // if we find the userConfig, update it's properties
                if (s != null) {
                    // copy the properties, except the createdDateTime property,
                    // because it is set in the Dao.
                    BeanUtils.copyProperties(userConfigDto, s,
                            new String[] {
                                "createdDateTime"
                            });
                    s = userConfigDao.save(s);
                    dto = new UserConfigDto();
                    DtoMarshaller.copyToDto(s, dto);
                    statusDto.setStatus("ok");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("user_config", dto);
        return response;
    }

    // create new userConfig
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewUserConfig(
            @RequestBody
            UserConfigPayload payLoad) {
        final UserConfigDto userConfigDto = payLoad.getUser_config();
        final Map<String, Object> response = new HashMap<String, Object>();
        UserConfigDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid userConfigDto, continue.
        // Otherwise,
        // server will respond with 400 Bad Request
        if (userConfigDto != null) {
            UserConfig s = new UserConfig();

            // copy the properties, except the createdDateTime property, because
            // it is set in the Dao.
            BeanUtils.copyProperties(userConfigDto, s,
                    new String[] {
                        "createdDateTime"
                    });
            s = userConfigDao.save(s);

            dto = new UserConfigDto();
            DtoMarshaller.copyToDto(s, dto);
            statusDto.setStatus("ok");
        }

        response.put("meta", statusDto);
        response.put("user_config", dto);
        return response;
    }
}
