package com.gallatinsystems.notification.app.gwt.server;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.notification.app.gwt.client.NotificationSubscriptionDto;
import com.gallatinsystems.notification.app.gwt.client.NotificationSubscriptionService;
import com.gallatinsystems.notification.dao.NotificationSubscriptionDao;
import com.gallatinsystems.notification.domain.NotificationSubscription;
import com.gallatinsystems.util.DtoMarshaller;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Service for editing notificationSubscription objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class NotificationSubscriptionServiceImpl extends RemoteServiceServlet implements
		NotificationSubscriptionService {


	private static final long serialVersionUID = -6085743838802553835L;
	private NotificationSubscriptionDao notifDao;

	public NotificationSubscriptionServiceImpl() {
		notifDao = new NotificationSubscriptionDao();
	}

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
				DtoMarshaller.getInstance().copyToDto(sub, dto);
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	@Override
	public List<NotificationSubscriptionDto> saveSubscriptions(
			List<NotificationSubscriptionDto> dtoList) {
		List<NotificationSubscription> subList = new ArrayList<NotificationSubscription>();
		if (dtoList != null) {
			for (NotificationSubscriptionDto dto : dtoList) {
				NotificationSubscription domain = new NotificationSubscription();
				DtoMarshaller.getInstance().copyToCanonical(domain, dto);
				subList.add(domain);
			}
			if (subList.size() > 0) {
				notifDao.save(subList);
				// now reform the dtos so we have keys populated
				dtoList.clear();
				for (NotificationSubscription sub : subList) {
					NotificationSubscriptionDto dto = new NotificationSubscriptionDto();
					DtoMarshaller.getInstance().copyToDto(sub, dto);
					dtoList.add(dto);
				}
			}
		}
		return dtoList;
	}

}
