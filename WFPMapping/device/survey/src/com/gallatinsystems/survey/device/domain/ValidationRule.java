package com.gallatinsystems.survey.device.domain;

/**
 * data structure defining what rules should be used to validate question
 * responses
 * 
 * @author Christopher Fagiani
 * 
 */
public class ValidationRule {
  
    private static final int DEFAULT_MAX_LENGTH = 9999;

    private String validationType;
    private Integer maxLength;

    private Boolean allowSigned;
    private Boolean allowDecimal;

    public ValidationRule(String type) {
        validationType = type;
        allowSigned = true;
        allowDecimal = true;
        maxLength = DEFAULT_MAX_LENGTH;
    }

    public String getValidationType() {
        return validationType;
    }

    public void setValidationType(String validationType) {
        this.validationType = validationType;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Boolean getAllowSigned() {
        return allowSigned;
    }

    public void setAllowSigned(Boolean allowSigned) {
        this.allowSigned = allowSigned;
    }

    public Boolean getAllowDecimal() {
        return allowDecimal;
    }

    public void setAllowDecimal(Boolean allowDecimal) {
        this.allowDecimal = allowDecimal;
    }

    public void setAllowDecimal(String val) {
        if (val != null) {
            allowDecimal = new Boolean(val.trim());
        } else {
            allowDecimal = true;
        }
    }

    public void setAllowSigned(String val) {
        if (val != null) {
            allowSigned = new Boolean(val.trim());

        } else {
            allowSigned = true;
        }
    }

    public void setMaxLength(String val) {
        if (val != null) {
            maxLength = new Integer(val.trim());
        } else {
            maxLength = DEFAULT_MAX_LENGTH;
        }

    }

}
