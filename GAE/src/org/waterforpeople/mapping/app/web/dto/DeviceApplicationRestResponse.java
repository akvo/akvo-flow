
package org.waterforpeople.mapping.app.web.dto;

import com.gallatinsystems.framework.rest.RestResponse;

public class DeviceApplicationRestResponse extends RestResponse {

    private static final long serialVersionUID = 3189491674758284779L;
    private String version;
    private String fileName;
    private String md5Checksum;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMd5Checksum() {
        return md5Checksum;
    }

    public void setMd5Checksum(String md5Checksum) {
        this.md5Checksum = md5Checksum;
    }

}
