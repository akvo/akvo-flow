package org.waterforpeople.mapping.app.web;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.waterforpeople.mapping.helper.TechnologyTypeHelper;

public class DataInitializerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8258043140461014174L;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response){
		
	}
	@SuppressWarnings("unused")
	private void reinitializeDataStore(HttpServletResponse response){
		
	}
	@SuppressWarnings("unused")
	private void deleteObjects(HttpServletResponse response){
		
	}
	@SuppressWarnings("unused")
	private void insertObjects(HttpServletResponse response){
		
	}
	@SuppressWarnings("unused")
	private void insertTechnologyTypes(HttpServletResponse response){
		TechnologyTypeHelper techTypeHelper =new TechnologyTypeHelper();
				
	}
}
