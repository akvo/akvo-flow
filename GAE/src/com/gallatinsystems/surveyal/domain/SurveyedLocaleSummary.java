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

package com.gallatinsystems.surveyal.domain;

import javax.jdo.annotations.PersistenceCapable;

import com.gallatinsystems.framework.domain.BaseDomain;

/**
 * Domain structure to hold summary information on surveyedLocales
 * At the moment, we only can group on surveyId. In future, we could 
 * improve this to include country or region. 
 * We could also implement shards, if necessary.
 * 
 * @author Mark Tiele Westra
 * 
 */
@PersistenceCapable
public class SurveyedLocaleSummary extends BaseDomain {
	private static final long serialVersionUID = -790856708459480822L;
	
	private Long count;
	private Long surveyGroupId;
	
	public Long getCount() {
		return count;
	}
	
	public void setCount(Long count) {
		this.count = count;
	}

	public Long getSurveyGroupId() {
		return surveyGroupId;
	}

	public void setSurveyGroupId(Long surveyGroupId) {
		this.surveyGroupId = surveyGroupId;
	}

	
}