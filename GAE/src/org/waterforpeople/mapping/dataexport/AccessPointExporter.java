package org.waterforpeople.mapping.dataexport;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;

import com.gallatinsystems.framework.dataexport.applet.DataExporter;

public class AccessPointExporter implements DataExporter {

	

	public AccessPointExporter() {
		
	}

	@Override
	public void export(Map<String, String> criteria, File file) {

		try {
			PrintWriter pw = new PrintWriter(file);
			pw.println("id\tCommunity Name\tCommunity Code\tEstimated Population");
			//TODO: call json client write data, if cursor,repeat 
			
			pw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	private void writeData(List<AccessPointDto> dtoList, PrintWriter pw) throws Exception{
		
	}
}
