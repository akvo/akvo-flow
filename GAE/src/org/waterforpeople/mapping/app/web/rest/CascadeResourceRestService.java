/*  Copyright (C) 2014-2015,2017 Stichting Akvo (Akvo Foundation)
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.CascadeResourceDto;
import org.waterforpeople.mapping.app.web.rest.dto.CascadeResourcePayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.domain.CascadeResource;

@Controller
@RequestMapping("/cascade_resources")
public class CascadeResourceRestService {

    private CascadeResourceDao cascadeResourceDao = new CascadeResourceDao();

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<CascadeResourceDto>> listCascadeResources() {
        final Map<String, List<CascadeResourceDto>> response = new HashMap<String, List<CascadeResourceDto>>();
        List<CascadeResourceDto> results = new ArrayList<CascadeResourceDto>();
        List<CascadeResource> crList = cascadeResourceDao.list(Constants.ALL_RESULTS);
        if (crList != null) {
            for (CascadeResource cr : crList) {
                CascadeResourceDto dto = new CascadeResourceDto();
                BeanUtils.copyProperties(cr, dto);
                dto.setLevelNames(cr.getLevelNames());
                if (cr.getKey() != null) {
                    dto.setKeyId(cr.getKey().getId());
                }
                results.add(dto);
            }
        }

        response.put("cascade_resources", results);
        return response;
    }

    // delete cascade by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteCascadeResourceById(@PathVariable("id") Long id) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        CascadeResource cr = cascadeResourceDao.getByKey(id);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if cascadeResource exists in the datastore
        if (cr != null) {
            // TODO check if any questions use this cascadeResource. If yes, we can't delete.
            cascadeResourceDao.delete(cr);
            statusDto.setStatus("ok");
        }
        response.put("meta", statusDto);
        return response;
    }

    // update existing cascade resource
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingCascadeResource(
            @RequestBody CascadeResourcePayload payLoad) {
        final CascadeResourceDto cascadeResourceDto = payLoad.getCascade_resource();
        final Map<String, Object> response = new HashMap<String, Object>();
        CascadeResourceDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid cascadeResourceDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (cascadeResourceDto != null) {
            Long keyId = cascadeResourceDto.getKeyId();
            CascadeResource cr;

            // if the cascadeResourceDto has a key, try to get the CascadeResource.
            if (keyId != null) {
                cr = cascadeResourceDao.getByKey(keyId);
                // if we find the cascade resource, update it's properties
                if (cr != null) {
                    // copy the properties, except the createdDateTime property,
                    // because it is set in the Dao.
                    BeanUtils.copyProperties(cascadeResourceDto, cr, new String[] {
                            "createdDateTime"
                    });
                    cr.setLevelNames(cascadeResourceDto.getLevelNames());
                    cr = cascadeResourceDao.save(cr);
                    dto = new CascadeResourceDto();
                    BeanUtils.copyProperties(cr, dto);
                    dto.setLevelNames(cr.getLevelNames());
                    if (cr.getKey() != null) {
                        dto.setKeyId(cr.getKey().getId());
                    }
                    statusDto.setStatus("ok");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("cascade_resource", dto);
        return response;
    }

    // create new cascade resource
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewCascadeResource(@RequestBody CascadeResourcePayload payLoad) {
        final CascadeResourceDto cascadeResourceDto = payLoad.getCascade_resource();
        final Map<String, Object> response = new HashMap<String, Object>();
        CascadeResourceDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid cascDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (cascadeResourceDto != null) {
            CascadeResource cr = new CascadeResource();

            // copy the properties, except the createdDateTime property, because
            // it is set in the Dao.
            BeanUtils.copyProperties(cascadeResourceDto, cr, new String[] {
                    "createdDateTime"
            });
            cr.setLevelNames(cascadeResourceDto.getLevelNames());
            cr = cascadeResourceDao.save(cr);

            dto = new CascadeResourceDto();
            BeanUtils.copyProperties(cr, dto);
            dto.setLevelNames(cr.getLevelNames());
            if (cr.getKey() != null) {
                dto.setKeyId(cr.getKey().getId());
            }
            statusDto.setStatus("ok");
        }

        response.put("meta", statusDto);
        response.put("cascade_resource", dto);
        return response;
    }
}
