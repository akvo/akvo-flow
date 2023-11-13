package org.akvo.flow.domain;

public class FormSubmissionsLimit {

    private Integer hardLimit;
    private Integer softLimit;

    public FormSubmissionsLimit(Integer limit) {
        this(limit, 80);
    }

    public FormSubmissionsLimit(Integer hardLimit, Integer soft_percentage) {
        this.hardLimit = hardLimit;
        this.softLimit = Math.round(hardLimit * ((float) soft_percentage / 100));
    }

    public Integer getHardLimit() {
        return this.hardLimit;
    }

    public Integer getSoftLimit() {
        return this.softLimit;
    }
}
