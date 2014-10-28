
package org.waterforpeople.mapping.app.web.dto;

import com.gallatinsystems.framework.rest.RestResponse;

public class DeviceTimeRestResponse extends RestResponse {

    private static final long serialVersionUID = 3189491674758284779L;
    private String time;
    
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

}
