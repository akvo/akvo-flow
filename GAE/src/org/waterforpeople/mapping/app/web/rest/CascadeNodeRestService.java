/*  Copyright (C) 2014,2017 Stichting Akvo (Akvo Foundation)
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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.survey.CascadeNodeDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.CascadeNodeBulkPayload;
import org.waterforpeople.mapping.app.web.rest.dto.CascadeNodePayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.survey.dao.CascadeNodeDao;
import com.gallatinsystems.survey.domain.CascadeNode;

@Controller
@RequestMapping("/cascade_nodes")
public class CascadeNodeRestService {

    private CascadeNodeDao cascadeNodeDao = new CascadeNodeDao();

	@RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<CascadeNodeDto>> listCascadeNodes(
    		@RequestParam(value = "cascadeResourceId", defaultValue = "")
            Long cascadeResourceId,
            @RequestParam(value = "parentNodeId", defaultValue = "")
            Long parentNodeId) {
        final Map<String, List<CascadeNodeDto>> response = new HashMap<String, List<CascadeNodeDto>>();
        List<CascadeNodeDto> results = new ArrayList<CascadeNodeDto>();      
        List<CascadeNode> cnList = cascadeNodeDao.listCascadeNodesByResourceAndParentId(cascadeResourceId, parentNodeId);
            if (cnList != null) {
                for (CascadeNode cn : cnList) {
                    CascadeNodeDto dto = new CascadeNodeDto();
                    BeanUtils.copyProperties(cn, dto);
                    if (cn.getKey() != null) {
                        dto.setKeyId(cn.getKey().getId());
                    }
                    results.add(dto);
                }
            }
        response.put("cascade_nodes", results);
        return response;
    }

    // delete cascade node by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteCascadeNodeById(@PathVariable("id")
    Long id) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        CascadeNode cr = cascadeNodeDao.getByKey(id);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if cascadeNode exists in the datastore
        if (cr != null) {
        	// TODO check if any questions use this cascadeNode. If yes, we can't delete.

        	// delete cascade node and all its children
        	cascadeNodeDao.deleteRecursive(cr.getCascadeResourceId(),id);
            statusDto.setStatus("ok");
        }
        response.put("meta", statusDto);
        return response;
    }
	
    // update existing cascade resource
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingCascadeNode(@RequestBody
    CascadeNodePayload payLoad) {
        final CascadeNodeDto cascadeNodeDto = payLoad.getCascade_node();
        final Map<String, Object> response = new HashMap<String, Object>();
        CascadeNodeDto dto = null;
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid cascadeNodeDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (cascadeNodeDto != null) {
            Long keyId = cascadeNodeDto.getKeyId();
            CascadeNode cr;

            // if the cascadeNodeDto has a key, try to get the CascadeNode.
            if (keyId != null) {
                cr = cascadeNodeDao.getByKey(keyId);
                // if we find the user, update it's properties
                if (cr != null) {
                    // copy the properties, except the createdDateTime property,
                    // because it is set in the Dao.
                    BeanUtils.copyProperties(cascadeNodeDto, cr, new String[] {
                            "createdDateTime"});
                    cr = cascadeNodeDao.save(cr);
                    dto = new CascadeNodeDto();
                    BeanUtils.copyProperties(cr, dto);
                    if (cr.getKey() != null) {
                        dto.setKeyId(cr.getKey().getId());
                    }
                    statusDto.setStatus("ok");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("cascade_node", dto);
        return response;
    }

    // create new cascade node
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewCascadeNode(@RequestBody
    CascadeNodePayload payLoad) {
        final CascadeNodeDto cascadeNodeDto = payLoad.getCascade_node();
        final Map<String, Object> response = new HashMap<String, Object>();
        CascadeNodeDto dto = null;
        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid cascDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (cascadeNodeDto != null) {
        	dto = createCascadeNode(cascadeNodeDto);
        	if (dto != null){
        		statusDto.setStatus("ok");
        	}
        }

        response.put("meta", statusDto);
        response.put("cascade_node", dto);
        return response;
    }

    private CascadeNodeDto createCascadeNode(CascadeNodeDto cascadeNodeDto){
    	CascadeNode cn = new CascadeNode();
        BeanUtils.copyProperties(cascadeNodeDto, cn);
        if (StringUtils.isEmpty(cascadeNodeDto.getCode())) {
            cn.setCode(cn.getName());
        }
    	cn = cascadeNodeDao.save(cn);
    	CascadeNodeDto cnDto = new CascadeNodeDto();
    	DtoMarshaller.copyToDto(cn,cnDto);
    	return cnDto;
    }

    // bulk save new cascade nodes
    @RequestMapping(method = RequestMethod.POST, value = "/bulk")
    @ResponseBody
    public Map<String, Object> saveNewCascadeNodeBulk(@RequestBody
    CascadeNodeBulkPayload payLoad) {
    	final List<CascadeNodeDto> cascadeNodeDtoList = payLoad
                .getCascade_nodes();
        final Map<String, Object> response = new HashMap<String, Object>();
        List<CascadeNodeDto> results = new ArrayList<CascadeNodeDto>();
        CascadeNodeDto dto = null;
        RestStatusDto statusDto = new RestStatusDto();

        Boolean stateSuccess = true;
        for (CascadeNodeDto cnDto : cascadeNodeDtoList) {
            dto = createCascadeNode(cnDto);
            if (dto != null) {
                results.add(dto);
            } else {
                stateSuccess = false;
            }
        }

        String status = stateSuccess ? "ok" : "failed";
        if (status.equals("failed")) {
            statusDto.setMessage("Cannot save cascade nodes");
        }
        statusDto.setStatus(status);
        response.put("meta", statusDto);
        response.put("cascade_nodes", results);
        return response;
    }       
}