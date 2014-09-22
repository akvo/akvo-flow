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

package org.waterforpeople.mapping.notification;

import java.util.TreeMap;

import com.gallatinsystems.common.util.MailUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.notification.NotificationHandler;
import com.gallatinsystems.notification.dao.NotificationSubscriptionDao;
import com.gallatinsystems.notification.domain.NotificationHistory;

/**
 * base for functionality common across notification handlers
 * 
 * @author Christopher Fagiani
 */
public abstract class BaseNotificationHandler implements NotificationHandler {
    private final static String EMAIL_FROM_ADDRESS_KEY = "emailFromAddress";
    protected static String FROM_ADDRESS;
    protected NotificationSubscriptionDao dao;

    protected BaseNotificationHandler() {
        FROM_ADDRESS = PropertyUtil.getProperty(EMAIL_FROM_ADDRESS_KEY);
        dao = new NotificationSubscriptionDao();
    }

    protected NotificationHistory getHistory(String type, Long id) {

        NotificationHistory hist = dao.findNotificationHistory(type, id);
        if (hist == null) {
            hist = new NotificationHistory();
            hist.setType(type);
            hist.setEntityId(id);
        }
        return hist;
    }

    /**
     * sends a mail
     * 
     * @param recipients
     * @param subject
     * @param body
     * @return
     */
    protected Boolean sendMail(TreeMap<String, String> recipients,
            String subject, String body) {
        return MailUtil.sendMail(FROM_ADDRESS, "FLOW", recipients, subject,
                body);
    }

}
