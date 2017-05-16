/*  Copyright (C) 2016-2017 Stichting Akvo (Akvo Foundation)
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.domain.CaddisflyResource;

import com.gallatinsystems.survey.dao.CaddisflyResourceDao;

@Controller
@RequestMapping("/caddisfly_resources")
public class CaddisflyResourceRestService {

    private CaddisflyResourceDao caddisflyResourceDao = new CaddisflyResourceDao();

    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<CaddisflyResource>> listCaddisflyResources() {
        final Map<String, List<CaddisflyResource>> response = new HashMap<String, List<CaddisflyResource>>();
        List<CaddisflyResource> caddisList = caddisflyResourceDao
                .listResources();

        for (CaddisflyResource cr : caddisList) {
            Long id = UUID.fromString(cr.getUuid()).getLeastSignificantBits();
            cr.setKeyId(id);
        }
        response.put("caddisfly_resources", caddisList);
        return response;
    }
}
