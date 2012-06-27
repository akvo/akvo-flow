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

package org.waterforpeople.mapping.app.gwt.client.accesspoint;

import java.io.Serializable;
import java.util.Date;

public class AccessPointSearchCriteriaDto implements Serializable {

	private static final long serialVersionUID = -4084451388783004593L;

	private String countryCode;
	private String communityCode;
	private Date collectionDateFrom;
	private Date collectionDateTo;
	private Date constructionDateFrom;
	private Date constructionDateTo;
	private String pointType;
	private String techType;
	private String orderBy;
	private String orderByDir;
	private Integer pageSize;
	private String metricId;
	private String metricValue;

	public String getMetricId() {
		return metricId;
	}

	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}

	public String getMetricValue() {
		return metricValue;
	}

	public void setMetricValue(String metricValue) {
		this.metricValue = metricValue;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderByDir() {
		return orderByDir;
	}

	public void setOrderByDir(String orderByDir) {
		this.orderByDir = orderByDir;
	}

	public Date getConstructionDateFrom() {
		return constructionDateFrom;
	}

	public void setConstructionDateFrom(Date constructionDateFrom) {
		this.constructionDateFrom = constructionDateFrom;
	}

	public Date getConstructionDateTo() {
		return constructionDateTo;
	}

	public void setConstructionDateTo(Date constructionDateTo) {
		this.constructionDateTo = constructionDateTo;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCommunityCode() {
		return communityCode;
	}

	public void setCommunityCode(String communityCode) {
		this.communityCode = communityCode;
	}

	public Date getCollectionDateFrom() {
		return collectionDateFrom;
	}

	public void setCollectionDateFrom(Date collectionDateFrom) {
		this.collectionDateFrom = collectionDateFrom;
	}

	public Date getCollectionDateTo() {
		return collectionDateTo;
	}

	public void setCollectionDateTo(Date collectionDateTo) {
		this.collectionDateTo = collectionDateTo;
	}

	public String getPointType() {
		return pointType;
	}

	public void setPointType(String pointType) {
		this.pointType = pointType;
	}

	public String getTechType() {
		return techType;
	}

	public void setTechType(String techType) {
		this.techType = techType;
	}

	public String toDelimitedString() {
		StringBuilder builder = new StringBuilder();
		appendNonNullParam(builder, "techType", techType);
		appendNonNullParam(builder, "pointType", pointType);
		appendNonNullParam(builder, "collectionDateTo", collectionDateTo);
		appendNonNullParam(builder, "collectionDateFrom", collectionDateFrom);
		appendNonNullParam(builder, "community", communityCode);
		appendNonNullParam(builder, "country", countryCode);
		appendNonNullParam(builder, "techType", techType);
		appendNonNullParam(builder, "constructionDateTo", constructionDateTo);
		appendNonNullParam(builder, "constructionDateFrom",
				constructionDateFrom);
		return builder.toString();

	}

	private void appendNonNullParam(StringBuilder builder, String name,
			Object value) {
		if (value != null) {
			if (builder.length() > 0) {
				builder.append(";");
			}
			builder.append(name).append(":=").append(value);
		}
	}
}
