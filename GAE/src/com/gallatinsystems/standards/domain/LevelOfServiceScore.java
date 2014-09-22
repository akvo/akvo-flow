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

package com.gallatinsystems.standards.domain;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;
import com.gallatinsystems.standards.domain.Standard.StandardType;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class LevelOfServiceScore extends BaseDomain {

    public enum ScoreObject {
        AccessPoint, SurveyedLocale
    };

    /**
	 * 
	 */
    private static final long serialVersionUID = 2125766896569232295L;
    private Standard.StandardType scoreType = null;
    private ArrayList<String> scoreDetails = null;
    private Integer score = 0;
    private Key objectKey = null;
    private ScoreObject scoreObject = null;

    public Key getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(Key objectKey) {
        this.objectKey = objectKey;
    }

    public ScoreObject getScoreObject() {
        return scoreObject;
    }

    public void setScoreObject(ScoreObject scoreObject) {
        this.scoreObject = scoreObject;
    }

    public StandardType getScoreType() {
        return scoreType;
    }

    public void setScoreType(StandardType scoreType) {
        this.scoreType = scoreType;
    }

    public ArrayList<String> getScoreDetails() {
        return scoreDetails;
    }

    public void setScoreDetails(ArrayList<String> scoreDetails) {
        this.scoreDetails = scoreDetails;
    }

    public void addScoreDetail(String message) {
        if (scoreDetails == null) {
            scoreDetails = new ArrayList<String>();
        }
        scoreDetails.add(message);
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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
