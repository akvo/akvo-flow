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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.util.DtoMarshaller;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.messaging.app.gwt.client.MessageDto;
import com.gallatinsystems.messaging.dao.MessageDao;
import com.gallatinsystems.messaging.domain.Message;

@Controller
@RequestMapping("/messages")
public class MessageRestService {

    private MessageDao messageDao = new MessageDao();

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> listMessages(
            @RequestParam(value = "since", defaultValue = "")
            String since) {
        final Map<String, Object> response = new HashMap<String, Object>();
        final List<MessageDto> messageList = new ArrayList<MessageDto>();
        final List<Message> list = messageDao.listBySubject(null, null, since);
        final RestStatusDto statusDto = new RestStatusDto();

        if (list != null) {
            final String newSince = MessageDao.getCursor(list);

            for (Message m : list) {
                final MessageDto dto = new MessageDto();
                DtoMarshaller.copyToDto(m, dto);
                messageList.add(dto);
            }

            statusDto.setSince(newSince);
            statusDto.setNum(list.size());
        }
        response.put("meta", statusDto);
        response.put("messages", messageList);
        return response;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, MessageDto> getMessage(@PathVariable("id")
    Long id) {
        final Map<String, MessageDto> response = new HashMap<String, MessageDto>();
        final MessageDto dto = new MessageDto();
        final Message m = messageDao.getByKey(id);

        if (m != null) {
            DtoMarshaller.copyToDto(m, dto);
        }
        response.put("message", dto);
        return response;
    }
}
