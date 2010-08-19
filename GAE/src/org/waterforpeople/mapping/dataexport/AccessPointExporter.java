package org.waterforpeople.mapping.dataexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.waterforpeople.mapping.app.gwt.client.accesspoint.AccessPointDto;

import com.gallatinsystems.framework.dataexport.applet.DataExporter;

public class AccessPointExporter implements DataExporter {

	

	public AccessPointExporter() {
		
	}

	@Override
	public void export(Map<String, String> criteria, File file, String serverBase) {

		try {
			PrintWriter pw = new PrintWriter(file);
			pw.println("id\tCommunity Name\tCommunity Code\tEstimated Population");
			//TODO: call json client write data, if cursor,repeat			
			URL url = new URL(serverBase + "pointofinterest?action=getnearby&country=AQ");		
			System.out.println("Calling: "+url.toString());
			HttpURLConnection conn = (HttpURLConnection) url
					.openConnection();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			String line;
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			while ((line = reader.readLine()) != null) {
				pw.println(line);
			}
			pw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	private void writeData(List<AccessPointDto> dtoList, PrintWriter pw) throws Exception{
		
	}
}
