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
import org.waterforpeople.mapping.app.web.rest.dto.NotificationSubscriptionPayload;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.notification.app.gwt.client.NotificationSubscriptionDto;
import com.gallatinsystems.notification.dao.NotificationSubscriptionDao;
import com.gallatinsystems.notification.domain.NotificationSubscription;

@Controller
@RequestMapping("/notification_subscriptions")
public class NotificationSubscriptionRestService {

    private NotificationSubscriptionDao notificationSubscriptionDao = new NotificationSubscriptionDao();

    // TODO put in meta information?
    // list all notificationSubscriptions
    @RequestMapping(method = RequestMethod.GET, value = "/all")
    @ResponseBody
    public Map<String, List<NotificationSubscriptionDto>> listNotificationSubscriptions() {
        final Map<String, List<NotificationSubscriptionDto>> response = new HashMap<String, List<NotificationSubscriptionDto>>();
        List<NotificationSubscriptionDto> results = new ArrayList<NotificationSubscriptionDto>();
        List<NotificationSubscription> notificationSubscriptions = notificationSubscriptionDao
                .list(Constants.ALL_RESULTS);
        if (notificationSubscriptions != null) {
            for (NotificationSubscription s : notificationSubscriptions) {
                NotificationSubscriptionDto dto = new NotificationSubscriptionDto();
                DtoMarshaller.copyToDto(s, dto);

                results.add(dto);
            }
        }
        response.put("notification_subscriptions", results);
        return response;
    }

    // TODO put in meta information?
    // list notificationSubscriptions by survey id
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, List<NotificationSubscriptionDto>> listNotificationSubscriptionBySurvey(
            @RequestParam("surveyId")
            Long surveyId) {
        final Map<String, List<NotificationSubscriptionDto>> response = new HashMap<String, List<NotificationSubscriptionDto>>();
        List<NotificationSubscriptionDto> results = new ArrayList<NotificationSubscriptionDto>();
        List<NotificationSubscription> notificationSubscriptions = notificationSubscriptionDao
                .listSubscriptions(surveyId, null, false);
        if (notificationSubscriptions != null) {
            for (NotificationSubscription s : notificationSubscriptions) {
                NotificationSubscriptionDto dto = new NotificationSubscriptionDto();
                DtoMarshaller.copyToDto(s, dto);

                results.add(dto);
            }
        }
        response.put("notification_subscriptions", results);
        return response;
    }

    // find a single notificationSubscription by the notificationSubscriptionId
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, NotificationSubscriptionDto> findNotificationSubscription(
            @PathVariable("id")
            Long id) {
        final Map<String, NotificationSubscriptionDto> response = new HashMap<String, NotificationSubscriptionDto>();
        NotificationSubscription s = notificationSubscriptionDao.getByKey(id);
        NotificationSubscriptionDto dto = null;
        if (s != null) {
            dto = new NotificationSubscriptionDto();
            DtoMarshaller.copyToDto(s, dto);
        }
        response.put("notification_subscription", dto);
        return response;

    }

    // delete notificationSubscription by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteNotificationSubscriptionById(
            @PathVariable("id")
            Long id) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        NotificationSubscription s = notificationSubscriptionDao.getByKey(id);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if notificationSubscription exists in the datastore
        if (s != null) {
            // delete notificationSubscription group
            notificationSubscriptionDao.delete(s);
            statusDto.setStatus("ok");
        }
        response.put("meta", statusDto);
        return response;
    }

    // update existing notificationSubscription
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingNotificationSubscription(
            @RequestBody
            NotificationSubscriptionPayload payLoad) {
        final NotificationSubscriptionDto notificationSubscriptionDto = payLoad
                .getNotification_subscription();
        final Map<String, Object> response = new HashMap<String, Object>();
        NotificationSubscriptionDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid notificationSubscriptionDto,
        // continue.
        // Otherwise,
        // server will respond with 400 Bad Request
        if (notificationSubscriptionDto != null) {
            Long keyId = notificationSubscriptionDto.getKeyId();
            NotificationSubscription s;

            // if the notificationSubscriptionDto has a key, try to get the
            // notificationSubscription.
            if (keyId != null) {
                s = notificationSubscriptionDao.getByKey(keyId);
                // if we find the notificationSubscription, update it's
                // properties
                if (s != null) {
                    // copy the properties, except the createdDateTime property,
                    // because it is set in the Dao.
                    BeanUtils.copyProperties(notificationSubscriptionDto, s,
                            new String[] {
                                "createdDateTime"
                            });
                    s = notificationSubscriptionDao.save(s);
                    dto = new NotificationSubscriptionDto();
                    DtoMarshaller.copyToDto(s, dto);
                    statusDto.setStatus("ok");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("notification_subscription", dto);
        return response;
    }

    // create new notificationSubscription
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewNotificationSubscription(
            @RequestBody
            NotificationSubscriptionPayload payLoad) {
        final NotificationSubscriptionDto notificationSubscriptionDto = payLoad
                .getNotification_subscription();
        final Map<String, Object> response = new HashMap<String, Object>();
        NotificationSubscriptionDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid notificationSubscriptionDto,
        // continue.
        // Otherwise,
        // server will respond with 400 Bad Request
        if (notificationSubscriptionDto != null) {
            NotificationSubscription s = new NotificationSubscription();

            // copy the properties, except the createdDateTime property, because
            // it is set in the Dao.
            BeanUtils.copyProperties(notificationSubscriptionDto, s,
                    new String[] {
                        "createdDateTime"
                    });
            s = notificationSubscriptionDao.save(s);

            dto = new NotificationSubscriptionDto();
            DtoMarshaller.copyToDto(s, dto);
            statusDto.setStatus("ok");
        }

        response.put("meta", statusDto);
        response.put("notification_subscription", dto);
        return response;
    }
}
