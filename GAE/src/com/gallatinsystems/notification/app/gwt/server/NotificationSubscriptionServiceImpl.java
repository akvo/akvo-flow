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

package com.gallatinsystems.notification.app.gwt.server;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.notification.app.gwt.client.NotificationSubscriptionDto;
import com.gallatinsystems.notification.app.gwt.client.NotificationSubscriptionService;
import com.gallatinsystems.notification.dao.NotificationSubscriptionDao;
import com.gallatinsystems.notification.domain.NotificationSubscription;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Service for editing notificationSubscription objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class NotificationSubscriptionServiceImpl extends RemoteServiceServlet
		implements NotificationSubscriptionService {

	private static final long serialVersionUID = -6085743838802553835L;
	private NotificationSubscriptionDao notifDao;

	public NotificationSubscriptionServiceImpl() {
		notifDao = new NotificationSubscriptionDao();
	}

	/**
	 * deletes a subscription if it exists.
	 */
	@Override
	public void deleteSubscription(NotificationSubscriptionDto dto) {
		if (dto != null) {
			NotificationSubscription sub = notifDao.getByKey(dto.getKeyId());
			if (sub != null) {
				notifDao.delete(sub);
			}
		}
	}

	/**
	 * lists subscriptions for the given type and entity id
	 */
	@Override
	public List<NotificationSubscriptionDto> listSubscriptions(Long entityId,
			String type) {
		List<NotificationSubscription> subList = notifDao.listSubscriptions(
				entityId, type, false);
		List<NotificationSubscriptionDto> dtoList = new ArrayList<NotificationSubscriptionDto>();
		if (subList != null) {
			for (NotificationSubscription sub : subList) {
				NotificationSubscriptionDto dto = new NotificationSubscriptionDto();
				DtoMarshaller.copyToDto(sub, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	/**
	 * saves all teh subscriptions passed in (either creates or updates
	 * depending on whether or not the id is set).
	 */
	@Override
	public List<NotificationSubscriptionDto> saveSubscriptions(
			List<NotificationSubscriptionDto> dtoList) {
		List<NotificationSubscription> subList = new ArrayList<NotificationSubscription>();
		if (dtoList != null) {
			for (NotificationSubscriptionDto dto : dtoList) {
				NotificationSubscription domain = new NotificationSubscription();
				DtoMarshaller.copyToCanonical(domain, dto);
				subList.add(domain);
			}
			if (subList.size() > 0) {
				notifDao.save(subList);
				// now reform the dtos so we have keys populated
				dtoList.clear();
				for (NotificationSubscription sub : subList) {
					NotificationSubscriptionDto dto = new NotificationSubscriptionDto();
					DtoMarshaller.copyToDto(sub, dto);
					dtoList.add(dto);
				}
			}
		}
		return dtoList;
	}

}
