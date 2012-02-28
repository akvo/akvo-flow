package org.waterforpeople.mapping.app.web.dto;

import javax.servlet.http.HttpServletRequest;

import com.gallatinsystems.framework.rest.RestRequest;

public class SurveyInstanceRequest extends RestRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6642806619258697157L;
	private static final String FIELD_NAME_PARAM = "fieldName";
	private static final String VALUE_NAME_PARAM = "value";
	
	private String fieldName = null;
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private String value=null;
	

	@Override
	protected void populateFields(HttpServletRequest req) throws Exception {
		if(req.getParameter(FIELD_NAME_PARAM)!=null){
			setFieldName(req.getParameter(FIELD_NAME_PARAM));
		}
		if(req.getParameter(VALUE_NAME_PARAM)!=null){
			setValue(req.getParameter(VALUE_NAME_PARAM));
		}
	}

	@Override
	protected void populateErrors() {
		// TODO Auto-generated method stub
		
	}

}
