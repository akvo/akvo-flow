/*
 *  Copyright (C) 2013,2017 Stichting Akvo (Akvo Foundation)
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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.gwt.client.community.SubCountryDto;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.gis.geography.dao.SubCountryDao;
import com.gallatinsystems.gis.geography.domain.SubCountry;

@Controller
@RequestMapping("/sub_countrys")
public class SubCountryRestService {

    private SubCountryDao subCountryDao = new SubCountryDao();

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> listSubCountry(
            @RequestParam(value = "countryCode", defaultValue = "")
            String countryCode,
            @RequestParam(value = "level", defaultValue = "")
            Integer level,
            @RequestParam(value = "parentId", defaultValue = "")
            Long parentId) {
        final Map<String, Object> response = new HashMap<String, Object>();
        final List<SubCountryDto> subCountryList = new ArrayList<SubCountryDto>();
        List<SubCountry> list = new ArrayList<SubCountry>();
        // if we ask for level 1, we give back all the level 1 items in the country.
        if (level != null && level == 1) {
            list = subCountryDao.listSubCountryByLevel(countryCode, level, null);
        } else if (level != null && level >= 2) {
            // if we are at level 2 or higher, we need to look at the parent
            list = subCountryDao.listSubCountryByParent(parentId);
        }
        final RestStatusDto statusDto = new RestStatusDto();

        if (list != null) {
            for (SubCountry sc : list) {
                final SubCountryDto dto = new SubCountryDto();
                DtoMarshaller.copyToDto(sc, dto);
                subCountryList.add(dto);
            }
        }
        response.put("meta", statusDto);
        response.put("sub_countrys", subCountryList);
        return response;
    }

    // @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    // @ResponseBody
    // public Map<String, MessageDto> getMessage(@PathVariable("id") Long id) {
    // final Map<String, MessageDto> response = new HashMap<String, MessageDto>();
    // final MessageDto dto = new MessageDto();
    // final Message m = messageDao.getByKey(id);
    //
    // if (m != null) {
    // DtoMarshaller.copyToDto(m, dto);
    // }
    // response.put("message", dto);
    // return response;
    // }
}
