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

package com.gallatinsystems.task.helper;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.gis.coordinate.utilities.CoordinateUtilities;
import com.gallatinsystems.task.dao.TaskDao;
import com.gallatinsystems.task.domain.Task;
import com.gallatinsystems.task.domain.Task.TaskStatus;

/**
 * helper class for the task domain. It can use geo distance computations to filter task results by
 * distances.
 * 
 * @author Christopher Fagiani
 */
public class TaskHelper {
    private TaskDao taskDao;

    public TaskHelper() {
        taskDao = new TaskDao();
    }

    /**
     * lists all tasks for a country with an Incomplete status that are less than maxDistance miles
     * from the point passed in
     * 
     * @param countryCode
     * @param lat
     * @param lon
     * @param cursorString
     * @return
     */
    public List<Task> listNearbyIncompleteTasks(String countryCode, Double lat,
            Double lon, String cursorString) {
        List<Task> countryTasks = taskDao.listTasksByCountry(countryCode,
                TaskStatus.INCOMPLETE, cursorString);
        List<Task> closeTasks = new ArrayList<Task>();
        // now that we have the tasks for this country, filter by distance
        if (countryTasks != null) {
            for (Task task : countryTasks) {
                if (CoordinateUtilities.computeDistanceInMiles(lat, lon, task
                        .getLat(), task.getLon()) <= task.getMaxDistance()) {
                    closeTasks.add(task);
                }
            }
        }
        return closeTasks;
    }
}
