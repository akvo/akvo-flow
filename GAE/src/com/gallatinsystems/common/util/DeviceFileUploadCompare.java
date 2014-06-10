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

package com.gallatinsystems.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Level;
import org.jfree.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.waterforpeople.mapping.app.gwt.client.devicefiles.DeviceFilesDto;
import org.waterforpeople.mapping.dataexport.service.BulkDataServiceClient;

import services.S3Driver;

public class DeviceFileUploadCompare {
    private String serverBase = null;
    static String eol = System.getProperty("line.separator");

    class DeviceFileResponseInternalContainer {
        private Boolean foundFlag = false;
        private Boolean foundByUUID = false;
        private Boolean foundByGEOKey = false;
        private DeviceFilesDto deviceFilesDto = null;
        private Boolean emptyFileOnParse = false;
        private Integer surveysFound = 0;
        private Boolean errorUnzippingFile = false;

        public Boolean getErrorUnzippingFile() {
            return errorUnzippingFile;
        }

        public void setErrorUnzippingFile(Boolean errorUnzippingFile) {
            this.errorUnzippingFile = errorUnzippingFile;
        }

        public Integer getSurveysFound() {
            return surveysFound;
        }

        public void setSurveysFound(Integer surveysFound) {
            this.surveysFound = surveysFound;
        }

        public Boolean getEmptyFileOnParse() {
            return emptyFileOnParse;
        }

        public void setEmptyFileOnParse(Boolean emptyFileOnParse) {
            this.emptyFileOnParse = emptyFileOnParse;
        }

        public Boolean getFoundFlag() {
            return foundFlag;
        }

        public void setFoundFlag(Boolean foundFlag) {
            this.foundFlag = foundFlag;
        }

        public DeviceFilesDto getDeviceFilesDto() {
            return deviceFilesDto;
        }

        public void setDeviceFilesDto(DeviceFilesDto deviceFilesDto) {
            this.deviceFilesDto = deviceFilesDto;
        }

        public void setFoundByUUID(Boolean foundByUUID) {
            this.foundByUUID = foundByUUID;
        }

        public Boolean getFoundByUUID() {
            return foundByUUID;
        }

        public void setFoundByGEOKey(Boolean foundByGEOKey) {
            this.foundByGEOKey = foundByGEOKey;
        }

        public Boolean getFoundByGEOKey() {
            return foundByGEOKey;
        }
    }

    private String awsident = null;

    public String getAwsident() {
        return awsident;
    }

    public void setAwsident(String awsident) {
        this.awsident = awsident;
    }

    public String getAwssecret() {
        return awssecret;
    }

    public void setAwssecret(String awssecret) {
        this.awssecret = awssecret;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String awssecret = null;

    /**
     * @param args
     */
    public static void main(String[] args) {
        DeviceFileUploadCompare dfuc = new DeviceFileUploadCompare();
        dfuc.setServerBase(args[2]);
        dfuc.fileName = args[3];
        dfuc.checkAllFiles(args[0], args[1]);

    }

    private void checkAllFiles(String key, String identifier) {
        compare(key, identifier);
    }

    private ArrayList<S3Item> s3ItemList = new ArrayList<S3Item>();

    public void addS3Item(String name, Long sizeBytes, Date lastUpdateDate,
            Boolean processed, Date processedDate) {
        S3Item item = new S3Item();
        item.setLastUpdateDate(lastUpdateDate);
        item.setName(name);
        item.setSizeBytes(sizeBytes);
        item.setProcessed(processed);
        item.setProcessedDate(processedDate);
        s3ItemList.add(item);
    }

    public class S3Item {
        private String name = null;
        private Long sizeBytes = null;
        private Date lastUpdateDate = null;
        private Boolean processed = null;
        private Date processedDate = null;

        public Boolean getProcessed() {
            return processed;
        }

        public void setProcessed(Boolean processed) {
            this.processed = processed;
        }

        public Date getProcessedDate() {
            return processedDate;
        }

        public void setProcessedDate(Date processedDate) {
            this.processedDate = processedDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getSizeBytes() {
            return sizeBytes;
        }

        public void setSizeBytes(Long sizeBytes) {
            this.sizeBytes = sizeBytes;
        }

        public Date getLastUpdateDate() {
            return lastUpdateDate;
        }

        public void setLastUpdateDate(Date lastUpdateDate) {
            this.lastUpdateDate = lastUpdateDate;
        }

    }

    private void compare(String key, String identifier) {
        S3Driver s3 = new S3Driver(key, identifier);
        String bucketName = "waterforpeople";
        String fileDir = "devicezip";
        String fileListPath = null;
        List<String> fileList = s3.listAllFiles(bucketName, fileDir,
                fileListPath);
        HashMap<String, DeviceFileResponseInternalContainer> filesMap = new HashMap<String, DeviceFileResponseInternalContainer>();
        Integer iCount = 0;
        for (String item : fileList) {
            if (item.startsWith("devicezip/wfp") && item.endsWith(".zip")) {
                System.out.print("iCount: " + iCount++ + "   ");
                DeviceFileResponseInternalContainer container = findFile(item);
                filesMap.put(item, container);
            }
        }
        writeReport(filesMap);
    }

    public void compare() {
        Integer iCount = 0;
        this.writeHeader();
        for (S3Item item : s3ItemList) {
            if (item.getName().startsWith("devicezip/wfp")
                    && item.getName().endsWith(".zip")) {
                String itemName = item.getName();
                System.out.print("iCount: " + iCount++ + "   ");
                DeviceFileResponseInternalContainer container = findFile(itemName);
                this.writeDetailRow(item, container);
            }
        }
    }

    private void writeReport(
            HashMap<String, DeviceFileResponseInternalContainer> filesMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("File on S3,Found in FLOW,Status,SurveyInstanceId,Processed Date\n");
        for (Entry<String, DeviceFileResponseInternalContainer> item : filesMap
                .entrySet()) {
            String s3file = item.getKey();
            DeviceFileResponseInternalContainer df = item.getValue();
            sb.append(s3file + ",");
            if (df != null && df.getFoundFlag() != null) {
                sb.append(df.getFoundFlag() + ",");
                if (df.getFoundFlag()) {
                    sb.append(df.getDeviceFilesDto().getProcessedStatus() + ","
                            + df.getDeviceFilesDto().getSurveyInstanceId()
                            + "," + df.getDeviceFilesDto().getProcessDate()
                            + "\n");
                } else {
                    sb.append("\n");
                }
            } else {
                sb.append("Couldn't obtain FLOW data for file\n");
            }
        }
        System.out.println(sb.toString());
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter(fileName));
            out.write(sb.toString());
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void writeHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("FileonS3,S3LastUpdateDate, SizeBytes,FoundinDeviceFileFLOW,FoundByUUID,FoundByGEOKey,Status,SurveyInstanceId,ProcessedDate,EmptyFileOnParse,NumberSurveyFoundInFile,ErrorUnzipping,FullPathZip"
                + eol);
        this.writeLineToFile(sb.toString());
    }

    private void writeDetailRow(S3Item s3file,
            DeviceFileResponseInternalContainer df) {
        StringBuilder sb = new StringBuilder();
        sb.append(s3file.getName() + "," + s3file.getLastUpdateDate() + ","
                + s3file.getSizeBytes() + ",");
        String fullUri = s3file.getName().replace("devicezip/", prefix);
        if (df != null && df.getFoundFlag() != null) {
            sb.append(df.getFoundFlag() + ",");
            sb.append(df.getFoundByUUID() + ",");
            sb.append(df.getFoundByGEOKey() + ",");
            if (df.getDeviceFilesDto() != null) {
                sb.append(df.getDeviceFilesDto().getProcessedStatus() + ","
                        + df.getDeviceFilesDto().getSurveyInstanceId() + ","
                        + df.getDeviceFilesDto().getProcessDate() + ","
                        + df.getEmptyFileOnParse() + "," + df.getSurveysFound()
                        + "," + df.getErrorUnzippingFile());
            } else {
                sb.append("null,null,null," + df.getEmptyFileOnParse() + ","
                        + df.getSurveysFound() + "," + df.getErrorUnzippingFile());
            }
            sb.append("," + fullUri + eol);
        } else {
            sb.append("Couldn't obtain FLOW data for file" + eol);
        }
        FileUtil.writeToFile(sb.toString(), fileName);
    }

    private void writeLineToFile(String line) {
        try {
            FileUtil.appendLineToFile(line, fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String fileName = null;
    String prefix = "http://waterforpeople.s3.amazonaws.com/";

    private DeviceFileResponseInternalContainer findFile(String fileName) {
        String serviceUrl = "/devicefilesrestapi?action=%s&"
                + "deviceFullPath=%s";
        String urlRequest = null;
        String actionParam = "findDeviceFile";

        urlRequest = String.format(serviceUrl, actionParam, prefix + fileName);
        try {
            return sendRequest(getServerBase(), urlRequest, prefix + fileName);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private String exectueRequest(String serverBase, String urlString)
            throws IOException {
        URL url = new URL("http://" + serverBase + urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(urlString);
        wr.flush();
        System.out.println("      Sent: " + url.toString());

        // Get the response
        BufferedReader rd = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = rd.readLine()) != null) {
            System.out.println("    Got: " + line);
            response.append(line);
        }
        wr.close();
        rd.close();
        return response.toString();
    }

    private DeviceFileResponseInternalContainer sendRequest(String serverBase,
            String urlString, String fileName) throws IOException {
        String response = exectueRequest(serverBase, urlString);
        DeviceFileResponseInternalContainer container = parseResponse(response,
                fileName);
        return container;
    }

    private DeviceFileResponseInternalContainer parseResponse(String response,
            String fileName) {
        DeviceFileResponseInternalContainer container = new DeviceFileResponseInternalContainer();
        try {
            if (response.startsWith("{")) {
                JSONObject json = new JSONObject(response);
                if (json.has("foundFlag")) {
                    Boolean foundFlag = Boolean.parseBoolean(json
                            .getString("foundFlag"));
                    container.setFoundFlag(foundFlag);
                    if (foundFlag) {
                        if (json.has("deviceFile")) {
                            DeviceFilesDto item = BulkDataServiceClient
                                    .parseDeviceFile(json
                                            .getJSONObject("deviceFile"));
                            container.setDeviceFilesDto(item);
                        }
                    } else {
                        // download zip
                        // extract zip
                        // look for UUID in file call
                        // surveyinstanceservlet?type=uuid&value=
                        // if no UUID then look up by GEO Key
                        // surveyinstanceservlet?type=GEO&value=
                        ArrayList<String> lines = this
                                .downloadExtractDeviceFile(fileName);
                        ArrayList<String> linesNew = new ArrayList<String>();
                        for (String line : lines) {
                            String[] linesSplit = line.split("\n");
                            for (String s : linesSplit) {
                                if (s.contains("\u0000")) {
                                    s = s.replaceAll("\u0000", "");
                                }
                                linesNew.add(s);
                            }
                        }
                        container = parseFile(linesNew, "\t", container);
                    }
                }
                return container;
            }
        } catch (Exception ex) {
            container.setErrorUnzippingFile(true);
            Log.log(Level.ERROR_INT, "error inflating " + fileName);
            return container;
        }
        return null;
    }

    public void setServerBase(String serverBase) {
        this.serverBase = serverBase;
    }

    public String getServerBase() {
        return serverBase;
    }

    private ArrayList<String> downloadExtractDeviceFile(String urlString)
            throws IOException {
        URL url = new URL(urlString);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        ZipInputStream zis = new ZipInputStream(bis);
        ArrayList<String> lines = new ArrayList<String>();
        while (zis.getNextEntry() != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int size;
            while ((size = zis.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, size);
            }
            lines.add(out.toString());
        }
        zis.closeEntry();
        return lines;
    }

    private DeviceFileResponseInternalContainer parseFile(
            ArrayList<String> unparsedLines, String delimiter,
            DeviceFileResponseInternalContainer container) {
        Integer surveysFound = 0;
        for (String line : unparsedLines) {
            String[] parts = null;
            if (line.contains(",")) {
                parts = line.split(",");
            } else if (line.contains("/t")) {
                parts = line.split("/t");
            }
            if (parts == null) {
                container.setFoundByGEOKey(false);
                DeviceFilesDto dfDto = new DeviceFilesDto();
                dfDto.setSurveyInstanceId(null);
                container.setDeviceFilesDto(dfDto);
                container.setFoundByUUID(false);
                container.setEmptyFileOnParse(true);
            } else if (parts.length <= 11) {
                // pre uuid so look for geo question
                int i = 0;
                String geoCoord = null;
                for (String item : parts) {
                    if (item.equalsIgnoreCase("GEO")) {
                        geoCoord = parts[i + 1];
                        if (geoCoord.equals("||")) {
                            Log.info("Empty Geo Coordinates");
                        } else {
                            // search by geo
                            try {
                                // http://watermappingmonitoring.appspot.com/surveyinstance?fieldName=GEO&value=-16.320276260375504|35.07026910781759|80.0|22z2grqm
                                String response = exectueRequest(
                                        "watermappingmonitoring.appspot.com",
                                        "/surveyinstance?fieldName=GEO&value="
                                                + geoCoord);
                                Long surveyInstanceId = parseSurveyInstanceResponse(response);

                                if (surveyInstanceId != null) {
                                    container.setFoundByGEOKey(true);
                                    DeviceFilesDto dfDto = new DeviceFilesDto();
                                    dfDto.setSurveyInstanceId(surveyInstanceId);
                                    container.setDeviceFilesDto(dfDto);
                                    container.setFoundByUUID(false);
                                } else {
                                    Log.log(Level.DEBUG_INT,
                                            "didn't find survey by geo: "
                                                    + geoCoord);
                                    container.setFoundByGEOKey(false);
                                    DeviceFilesDto dfDto = new DeviceFilesDto();
                                    dfDto.setSurveyInstanceId(null);
                                    container.setFoundByUUID(false);
                                    container.setDeviceFilesDto(dfDto);
                                    surveysFound++;
                                    container.setSurveysFound(surveysFound);
                                }
                            } catch (IOException e) {
                                Log.log(Level.ERROR_INT,
                                        "Got an ioexception while parsing for geo surveyinstance lookup:"
                                                + e.getMessage());
                                container.setFoundByGEOKey(false);
                                DeviceFilesDto dfDto = new DeviceFilesDto();
                                dfDto.setSurveyInstanceId(null);
                                container.setDeviceFilesDto(dfDto);
                                container.setFoundByUUID(false);
                            } catch (JSONException e) {
                                Log.log(Level.ERROR_INT,
                                        "Got an exception while parsing for geo surveyinstance lookup:"
                                                + e.getMessage());
                                container.setFoundByGEOKey(false);
                                DeviceFilesDto dfDto = new DeviceFilesDto();
                                dfDto.setSurveyInstanceId(null);
                                container.setDeviceFilesDto(dfDto);
                                container.setFoundByUUID(false);
                            }
                        }
                    }
                    i++;
                }
            } else if (parts.length > 11) {
                String uuid = parts[parts.length - 1];
                if (uuid != null && uuid.trim().length() > 0) {
                    try {
                        String response = exectueRequest(
                                "watermappingmonitoring.appspot.com",
                                "/surveyinstance?fieldName=uuid&value=" + uuid);
                        Long surveyInstanceId = parseSurveyInstanceResponse(response);
                        if (surveyInstanceId != null) {
                            container.setFoundByUUID(true);
                            DeviceFilesDto dfDto = new DeviceFilesDto();
                            dfDto.setSurveyInstanceId(surveyInstanceId);
                            container.setDeviceFilesDto(dfDto);
                            container.setFoundByGEOKey(false);
                            break;
                        }
                    } catch (IOException e) {
                        Log.log(Level.ERROR_INT, e.getMessage());
                        container.setFoundByUUID(false);
                        DeviceFilesDto dfDto = new DeviceFilesDto();
                        dfDto.setSurveyInstanceId(null);
                        container.setDeviceFilesDto(dfDto);
                        container.setFoundByGEOKey(false);
                        break;
                    } catch (JSONException e) {
                        Log.log(Level.ERROR_INT, e.getMessage());
                        container.setFoundByUUID(false);
                        DeviceFilesDto dfDto = new DeviceFilesDto();
                        dfDto.setSurveyInstanceId(null);
                        container.setDeviceFilesDto(dfDto);
                        container.setFoundByGEOKey(false);
                        break;
                    }
                }
            }
        }
        return container;
    }

    private Long parseSurveyInstanceResponse(String response)
            throws JSONException {
        Long surveyInstanceId = null;
        if (response.startsWith("{")) {
            JSONObject json = new JSONObject(response);
            if (json.has("surveyInstanceId")) {
                if (json.getString("surveyInstanceId").equalsIgnoreCase("null")) {
                    return null;
                } else {
                    surveyInstanceId = json.getLong("surveyInstanceId");
                }
            }
        }
        return surveyInstanceId;
    }
}
