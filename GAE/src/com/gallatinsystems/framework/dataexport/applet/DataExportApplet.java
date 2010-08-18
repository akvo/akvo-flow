package com.gallatinsystems.framework.dataexport.applet;

import java.util.Map;


//TODO: add annotations for the wrapper and put this in the gwt client package
/*
* 
* @ImplementingClass(com.google.gwt.gwtai.demo.impl.CounterAppletImpl.class)
@Height("60")
@Width("350")
@Archive("GwtAI-Client.jar,GwtAI-Demo.jar")

MUST ALSO Extend applet from the library
*/
public interface DataExportApplet {
	
	public enum ExportType  {ACCESS_POINT, SURVEY_STATS}	
	
	public void doExport(ExportType type, Map<String,String> criteriaMap);
	

}
