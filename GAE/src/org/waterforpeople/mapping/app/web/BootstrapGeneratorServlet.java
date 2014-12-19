/*
 *  Copyright (C) 2010-2014 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.waterforpeople.mapping.app.web.dto.BootstrapGeneratorRequest;

import com.gallatinsystems.common.util.MailUtil;
import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.common.util.S3Util;
import com.gallatinsystems.common.util.ZipUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.CascadeResourceDao;
import com.gallatinsystems.survey.dao.QuestionDao;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.domain.CascadeResource;
import com.gallatinsystems.survey.domain.Question;
import com.gallatinsystems.survey.domain.Survey;

/**
 * downloads publishes survey xml files and forms a zip that conforms to the structure of the
 * bootstrap.zip file expected by the device
 * 
 * @author Christopher Fagiani
 */
public class BootstrapGeneratorServlet extends AbstractRestApiServlet {

    private static final Logger log = Logger.getLogger(BootstrapGeneratorServlet.class.getName());

    private static final long serialVersionUID = -6645180848307957119L;
    private static final String DB_INST_ENTRY = "dbinstructions.sql";
    private static final String BOOTSTRAP_UPLOAD_DIR = "bootstrapdir";
    private static final String EMAIL_FROM_ADDRESS_KEY = "emailFromAddress";
    private static final String EMAIL_SUB = "FLOW Bootstrap File";
    private static final String EMAIL_BODY = "Click the link to download the bootstrap file";
    private static final String ERROR_BODY = "There were errors while attempting to generate the bootstrap file:";

    private SurveyDAO surveyDao;
    private CascadeResourceDao cascadeDao;
    private String bucketName;
    private String keyPrefix;

    public BootstrapGeneratorServlet() {
        super();
        surveyDao = new SurveyDAO();
        cascadeDao = new CascadeResourceDao();
        bucketName = PropertyUtil.getProperty("s3bucket");
        keyPrefix = PropertyUtil.getProperty("surveyuploaddir");
    }

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new BootstrapGeneratorRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;
    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        RestResponse response = new RestResponse();
        BootstrapGeneratorRequest bootStrapReq = (BootstrapGeneratorRequest) req;
        if (BootstrapGeneratorRequest.GEN_ACTION.equalsIgnoreCase(bootStrapReq
                .getAction())) {
            generateFile(bootStrapReq);
        }
        return response;
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {
        // no-op
    }

    private void generateFile(BootstrapGeneratorRequest req) {
        Map<String, String> contentMap = new HashMap<String, String>();
        Set<String> resourcesSet = new HashSet<String>();
        StringBuilder errors = new StringBuilder();
        if (req.getSurveyIds() != null) {
            for (Long id : req.getSurveyIds()) {
                try {
                    Survey s = surveyDao.getById(id);
                    String name = s.getName().replaceAll(" ", "_");
                    StringBuilder buf = new StringBuilder();
                    URLConnection conn = S3Util.getConnection(bucketName, keyPrefix + "/"
                            + s.getKey().getId() + ".xml");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buf.append(line).append("\n");
                    }
                    reader.close();
                    contentMap.put(s.getKey().getId() + "/" + name + ".xml",
                            buf.toString());
                    
                    resourcesSet.addAll(getSurveyResources(id));// Add survey resources
                } catch (Exception e) {
                    errors.append("Could not include survey id " + id + "\n");
                }
            }
        }
        if (req.getDbInstructions() != null
                && req.getDbInstructions().trim().length() > 0) {
            contentMap.put(DB_INST_ENTRY, req.getDbInstructions().trim());
        }
        
        String filename = System.currentTimeMillis() + "-bs.zip";
        String objectKey = PropertyUtil.getProperty(BOOTSTRAP_UPLOAD_DIR) + "/" + filename;
        try {
            Map<String, byte[]> resources = fetchResources(resourcesSet);
            ByteArrayOutputStream os = ZipUtil.generateZip(contentMap, resources);
    
            S3Util.put(bucketName, objectKey, os.toByteArray(), "application/zip", false);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error uploading bootstrap file: " + e.getMessage(), e);
            return; // skip the email
        }

        String body = EMAIL_BODY;

        if (errors.toString().trim().length() > 0) {
            body = ERROR_BODY + "\n\n" + errors.toString();
        } else {
            body += "\n\n" + S3Util.getBrowserLink(bucketName, objectKey);
        }
        MailUtil.sendMail(PropertyUtil.getProperty(EMAIL_FROM_ADDRESS_KEY),
                "FLOW", req.getEmail(), EMAIL_SUB, body);
    }
    
    private Set<String> getSurveyResources(Long surveyId) {
        Set<String> resources = new HashSet<String>();
        for (Question q : new QuestionDao().listQuestionByType(surveyId, Question.Type.CASCADE)) {
            Long cascadeResourceId = q.getCascadeResourceId();
            if (cascadeResourceId != null) {
                CascadeResource cr = cascadeDao.getByKey(cascadeResourceId);
                if (cr != null) {
                    String resName = cr.getResourceId() + ".zip";
                    resources.add(resName);
                }
            }
        }
        return resources;
    }
    
    private Map<String, byte[]> fetchResources(Set<String> resources) throws IOException {
        Map<String, byte[]> resData = new HashMap<String, byte[]>();
        for (String resource : resources) {
            byte[] data = fetchResource(resource);
            resData.put(resource, data);
        }
        return resData;
    }
    
    private byte[] fetchResource(String res) throws IOException {
        URLConnection conn = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            conn = S3Util.getConnection(bucketName, keyPrefix + "/" + res);
            out = new ByteArrayOutputStream();
            in = conn.getInputStream();
            IOUtils.copy(in, out);
            out.flush();
            return out.toByteArray();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

}
