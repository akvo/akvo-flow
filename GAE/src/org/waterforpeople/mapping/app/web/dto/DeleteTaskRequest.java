package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

public class DeleteTaskRequest extends RestRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8342489912346343508L;
	public static final String OBJECT_PARAM ="object";
	public static final String KEY_PARAM = "key";
	public static final String CURSOR_PARAM = "cursor";
	public static final String TASK_COUNT_PARAM = "taskCount";
	private String objectName = null;
	private String key = null;
	private String cursor = null;
	private String taskCount = null;
	
	
	public String getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(String taskCount) {
		this.taskCount = taskCount;
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if(req.getParameter(OBJECT_PARAM)!=null){
			setObjectName(req.getParameter(OBJECT_PARAM));
			if(req.getParameter(KEY_PARAM)!=null){
				setKey(req.getParameter(KEY_PARAM));
			}else{
				throw new Exception("Parameter " + KEY_PARAM + " is mandatory");
			}
			if(req.getParameter(CURSOR_PARAM)!=null){
				setCursor(req.getParameter(CURSOR_PARAM));
			}
			if(req.getParameter(TASK_COUNT_PARAM)!=null){
				setTaskCount(req.getParameter(TASK_COUNT_PARAM));
			}
		}else{
			throw new Exception("Parameter " + OBJECT_PARAM + " is mandatory");
		}
	}

	@Override
	protected void populateErrors() {
	}

}
