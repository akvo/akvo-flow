package com.gallatinsystems.task.helper;

import java.util.ArrayList;
import java.util.List;

import com.gallatinsystems.gis.coordinate.utilities.CoordinateUtilities;
import com.gallatinsystems.task.dao.TaskDao;
import com.gallatinsystems.task.domain.Task;
import com.gallatinsystems.task.domain.Task.TaskStatus;

/**
 * helper class for the task domain. It can use geo distance computations to
 * filter task results by distances.
 * 
 * @author Christopher Fagiani
 * 
 */
public class TaskHelper {
	private TaskDao taskDao;

	public TaskHelper() {
		taskDao = new TaskDao();
	}

	/**
	 * lists all tasks for a country with an Incomplete status that are less
	 * than maxDistance miles from the point passed in
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
