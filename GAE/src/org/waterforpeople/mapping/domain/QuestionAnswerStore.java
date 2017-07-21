/*
 *  Copyright (C) 2010-2014 Stichting Akvo (Akvo Foundation)
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
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.akvo.flow.domain.DataUtils;
import org.apache.commons.lang.StringUtils;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.domain.BaseDomain;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class QuestionAnswerStore extends BaseDomain {
    private static final long serialVersionUID = 3726562582080475960L;

    @Persistent
    private Long arbitratyNumber;
    @Persistent
    private String questionID;
    @Persistent
    private String type;
    @Persistent
    private String value;
    /**
     * This property holds the value response value when exceeds 500 characters<br>
     * See: https://developers
     * .google.com/appengine/docs/java/javadoc/com/google/appengine/api/datastore/Text
     */
    private Text valueText;
    private Date collectionDate;
    private Long surveyId;
    private Long surveyInstanceId;
    private String scoredValue;
    private String strength;
    private Integer iteration;

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getScoredValue() {
        return scoredValue;
    }

    public void setScoredValue(String scoredValue) {
        this.scoredValue = scoredValue;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public Long getSurveyInstanceId() {
        return surveyInstanceId;
    }

    public void setSurveyInstanceId(Long surveyInstanceId) {
        this.surveyInstanceId = surveyInstanceId;
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    public Long getArbitratyNumber() {
        return arbitratyNumber;
    }

    public void setArbitratyNumber(Long arbitratyNumber) {
        this.arbitratyNumber = arbitratyNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        if (value != null) {
            return value;
        }
        if (valueText != null) {
            return valueText.getValue();
        }
        return null;
    }

    public void setValue(String value) {
        // Explicitly set the non used property to null
        // to avoid problems when reading the value
        if (value != null && value.length() > Constants.MAX_LENGTH) {
            this.value = null;
            this.valueText = new Text(value);
        } else {
            this.valueText = null;
            this.value = value;
        }
    }

    public Long getQuestionIDLong() {
        try {
            return Long.valueOf(questionID);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }
    
    public void setIteration(Integer iteration) {
        this.iteration = iteration;
    }

    public Integer getIteration() {
        return iteration;
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
    
    public String getDatapointNameValue() {
        return getDatapointNameValue(type, value);
    }
    
    public static String getDatapointNameValue(String theType, String theValue) {
        if (theType == null || theValue == null) {
            return "";
        }

        String name;
        switch (theType) {
            case "CASCADE":
                name = StringUtils.join(DataUtils.cascadeResponseValues(theValue), " - ");
                break;
            case "OPTION":
            case "OTHER":
                name = StringUtils.join(DataUtils.optionResponsesTextArray(theValue), " - ");
                break;
            default:
                name = theValue;
                break;
        }
        
        name = name.replaceAll("\\s+", " ");// Trim line breaks, multiple spaces, etc
        name = name.replaceAll("\\s*\\|\\s*", " - ");// Replace pipes with hyphens

        return name.trim();
    }
}
