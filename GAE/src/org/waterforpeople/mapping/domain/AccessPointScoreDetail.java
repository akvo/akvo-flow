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

package org.waterforpeople.mapping.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class AccessPointScoreDetail extends BaseDomain {

    /**
	 * 
	 */
    private static final long serialVersionUID = -8688043975799269589L;
    private Long accessPointId = null;
    private Integer score = null;
    private ArrayList<AccessPointScoreComputationItem> scoreComputationItems = null;
    private String status = null;
    private Date computationDate = null;
    private String scoreBucket = null;
    private Long scoreBucketId = null;

    public Long getAccessPointId() {
        return accessPointId;
    }

    public void setAccessPointId(Long accessPointId) {
        this.accessPointId = accessPointId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public ArrayList<AccessPointScoreComputationItem> getScoreComputationItems() {
        return scoreComputationItems;
    }

    public void setScoreComputationItems(
            ArrayList<AccessPointScoreComputationItem> scoreComputationItems) {
        this.scoreComputationItems = scoreComputationItems;
    }

    public void addScoreComputationItem(Integer score, String item) {
        if (scoreComputationItems == null) {
            scoreComputationItems = new ArrayList<AccessPointScoreComputationItem>();
        }
        AccessPointScoreComputationItem apsi = new AccessPointScoreComputationItem(score, item);
        scoreComputationItems.add(apsi);

    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setComputationDate(Date computationDate) {
        this.computationDate = computationDate;
    }

    public Date getComputationDate() {
        return computationDate;
    }

    public void setScoreBucket(String scoreBucket) {
        this.scoreBucket = scoreBucket;
    }

    public String getScoreBucket() {
        return scoreBucket;
    }

    public void setScoreBucketId(Long scoreBucketId) {
        this.scoreBucketId = scoreBucketId;
    }

    public Long getScoreBucketId() {
        return scoreBucketId;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        // determine fields declared in this class only (no fields of
        // superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        // print field names paired with their values
        for (Field field : fields) {
            field.setAccessible(true);
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                // requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }

}
