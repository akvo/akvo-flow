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

package com.gallatinsystems.notification.domain;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Used to record who has opted-in to notifications and how to notify them
 * 
 * @author Christopher Fagiani
 */

@PersistenceCapable
public class NotificationSubscription extends BaseDomain {

    private static final long serialVersionUID = 5531603859192908616L;
    private String notificationType;
    private String notificationMethod;
    private String notificationDestination;
    private String notificationOption;
    private Long entityId;
    private Date expiryDate;

    public String getNotificationOption() {
        return notificationOption;
    }

    public void setNotificationOption(String notificationOption) {
        this.notificationOption = notificationOption;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(String notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    public String getNotificationDestination() {
        return notificationDestination;
    }

    public void setNotificationDestination(String notificationDestination) {
        this.notificationDestination = notificationDestination;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

}
