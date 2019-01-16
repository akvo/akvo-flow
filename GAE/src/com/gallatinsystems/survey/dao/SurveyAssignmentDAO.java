/*
 *  Copyright (C) 2012, 2019 Stichting Akvo (Akvo Foundation)
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

package com.gallatinsystems.survey.dao;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.domain.SurveyAssignment;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.dao.BaseDAO;

public class SurveyAssignmentDAO extends BaseDAO<SurveyAssignment> {

    public SurveyAssignmentDAO() {
        super(SurveyAssignment.class);
    }
    
    /**
     * Return a list of survey assignments containing a specified device
     *
     * @return list of assignments
     */
    public List<SurveyAssignment> listAllContainingDevice(Long devId) {
    	List<SurveyAssignment> all = list(Constants.ALL_RESULTS);
    	List<SurveyAssignment> selected = new ArrayList<>();
    	for (SurveyAssignment ass : all) {
    		List<Long> devs = ass.getDeviceIds();
    		if (devs.contains(devId)) {
    			selected.add(ass);
    		}
    	}
        return selected;
    }

    /**
     * Remove a device from all assignments
     *
     * @return number of affected assignments
     */
    public int removeDevice(Long devId) {
    	List<SurveyAssignment> all = list(Constants.ALL_RESULTS);
    	List<SurveyAssignment> changed = new ArrayList<>();
    	for (SurveyAssignment ass : all) {
    		List<Long> devs = ass.getDeviceIds();
    		if (devs.remove(devId)) {
    			changed.add(ass);
    		}
    	}
    	save(changed);
        return changed.size();
    }
}
