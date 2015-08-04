/*
 *  Copyright (C) 2014-2015 Stichting Akvo (Akvo Foundation)
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

package org.akvo.flow;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClient;
import com.amazonaws.services.identitymanagement.model.AccessKey;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccessKeyResult;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.GetUserRequest;
import com.amazonaws.services.identitymanagement.model.NoSuchEntityException;
import com.amazonaws.services.identitymanagement.model.PutUserPolicyRequest;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Region;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class InstanceConfigurator {

    private static final String GAE_SUFFIX = "-gae";
    private static final String APK_SUFFIX = "-apk";

    public static void main(String[] args) throws Exception {

        Options opts = getOptions();
        CommandLineParser parser = new BasicParser();
        CommandLine cli = null;

        try {
            cli = parser.parse(opts, args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(InstanceConfigurator.class.getName(), opts);
            System.exit(1);
        }

        String awsAccessKey = cli.getOptionValue("ak");
        String awsSecret = cli.getOptionValue("as");
        String bucketName = cli.getOptionValue("bn");
        String gaeId = cli.getOptionValue("gae");
        String outFolder = cli.getOptionValue("o");
        String flowServices = cli.getOptionValue("fs");
        String eventNotification = cli.getOptionValue("en");
        String enableChangeEvents = cli.getOptionValue("ce", "false");
        String mapsProvider = cli.getOptionValue("mapsProvider", "mapbox");
        String googleMapsRegionBias = cli.getOptionValue("rb", "");
        String cartodbApiKey = cli.getOptionValue("ck", "");
        String cartodbSqlApi = cli.getOptionValue("cs", "");
        String cartodbHost = cli.getOptionValue("ch", "");
        String cartodbTilerPort = cli.getOptionValue("ctp", "8181");
        String alias = cli.getOptionValue("a");
        String emailFrom = cli.getOptionValue("ef");
        String emailTo = cli.getOptionValue("et");
        String orgName = cli.getOptionValue("on");
        String signingKey = cli.getOptionValue("sk");

        File out = new File(outFolder);

        if (!out.exists()) {
            out.mkdirs();
        }

        Map<String, AccessKey> accessKeys = new HashMap<String, AccessKey>();
        String apiKey = UUID.randomUUID().toString().replaceAll("-", "");

        AWSCredentials creds = new BasicAWSCredentials(awsAccessKey, awsSecret);
        AmazonIdentityManagementClient iamClient = new AmazonIdentityManagementClient(
                creds);
        AmazonS3Client s3Client = new AmazonS3Client(creds);

        // Creating bucket

        System.out.println("Creating bucket: " + bucketName);

        try {
            if (s3Client.doesBucketExist(bucketName)) {
                System.out.println(bucketName
                        + " already exists, skipping creation");
            } else {
                s3Client.createBucket(bucketName, Region.EU_Ireland);
            }
        } catch (Exception e) {
            System.err.println("Error trying to create bucket " + bucketName
                    + " : " + e.getMessage());
            System.exit(1);
        }

        // Creating users and groups

        String gaeUser = bucketName + GAE_SUFFIX;
        String apkUser = bucketName + APK_SUFFIX;

        // GAE

        System.out.println("Creating user: " + gaeUser);

        GetUserRequest gaeUserRequest = new GetUserRequest();
        gaeUserRequest.setUserName(gaeUser);

        try {
            iamClient.getUser(gaeUserRequest);
            System.out.println("User already exists, skipping creation");
        } catch (NoSuchEntityException e) {
            iamClient.createUser(new CreateUserRequest(gaeUser));
        }

        System.out.println("Requesting security credentials for " + gaeUser);

        CreateAccessKeyRequest gaeAccessRequest = new CreateAccessKeyRequest();
        gaeAccessRequest.setUserName(gaeUser);

        CreateAccessKeyResult gaeAccessResult = iamClient
                .createAccessKey(gaeAccessRequest);
        accessKeys.put(gaeUser, gaeAccessResult.getAccessKey());

        // APK

        System.out.println("Creating user: " + apkUser);

        GetUserRequest apkUserRequest = new GetUserRequest();
        apkUserRequest.setUserName(apkUser);

        try {
            iamClient.getUser(apkUserRequest);
            System.out.println("User already exists, skipping creation");
        } catch (NoSuchEntityException e) {
            iamClient.createUser(new CreateUserRequest(apkUser));
        }

        System.out.println("Requesting security credentials for " + apkUser);

        CreateAccessKeyRequest apkAccessRequest = new CreateAccessKeyRequest();
        apkAccessRequest.setUserName(apkUser);

        CreateAccessKeyResult apkAccessResult = iamClient
                .createAccessKey(apkAccessRequest);
        accessKeys.put(apkUser, apkAccessResult.getAccessKey());

        System.out.println("Configuring security policies...");

        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(InstanceConfigurator.class,
                "/org/akvo/flow/templates");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setDefaultEncoding("UTF-8");

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("bucketName", bucketName);
        data.put("version",
                new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        data.put("accessKey", accessKeys);

        Template t0 = cfg.getTemplate("bucket-policy.ftl");
        StringWriter bucketPolicy = new StringWriter();
        t0.process(data, bucketPolicy);

        Template t1 = cfg.getTemplate("apk-s3-policy.ftl");
        StringWriter apkPolicy = new StringWriter();
        t1.process(data, apkPolicy);

        Template t2 = cfg.getTemplate("gae-s3-policy.ftl");
        StringWriter gaePolicy = new StringWriter();
        t2.process(data, gaePolicy);

        s3Client.setBucketPolicy(bucketName, t0.toString());

        iamClient.putUserPolicy(new PutUserPolicyRequest(apkUser, apkUser,
                Policy.fromJson(apkPolicy.toString()).toJson()));

        iamClient.putUserPolicy(new PutUserPolicyRequest(gaeUser, gaeUser,
                Policy.fromJson(gaePolicy.toString()).toJson()));

        System.out.println("Creating configuration files...");

        // survey.properties
        Map<String, Object> apkData = new HashMap<String, Object>();
        apkData.put("awsBucket", bucketName);
        apkData.put("awsAccessKeyId", accessKeys.get(apkUser).getAccessKeyId());
        apkData.put("awsSecretKey", accessKeys.get(apkUser)
                .getSecretAccessKey());
        apkData.put("serverBase", "https://" + gaeId + ".appspot.com");
        apkData.put("restApiKey", apiKey);

        Template t3 = cfg.getTemplate("survey.properties.ftl");
        FileWriter fw = new FileWriter(new File(out, "/survey.properties"));
        t3.process(apkData, fw);

        // appengine-web.xml
        Map<String, Object> webData = new HashMap<String, Object>();
        webData.put("awsBucket", bucketName);
        webData.put("awsAccessKeyId", accessKeys.get(gaeUser).getAccessKeyId());
        webData.put("awsSecretAccessKey", accessKeys.get(gaeUser)
                .getSecretAccessKey());
        webData.put("s3url", "https://" + bucketName + ".s3.amazonaws.com");
        webData.put("instanceId", gaeId);
        webData.put("alias", alias);
        webData.put("flowServices", flowServices);
        webData.put("eventNotification", eventNotification);
        webData.put("enableChangeEvents", enableChangeEvents);
        webData.put("mapsProvider", mapsProvider);
        webData.put("googleMapsRegionBias", googleMapsRegionBias);
        webData.put("cartodbApiKey", cartodbApiKey);
        webData.put("cartodbSqlApi", cartodbSqlApi);
        webData.put("cartodbHost", cartodbHost);
        webData.put("cartodbTilerPort", cartodbTilerPort);
        webData.put("apiKey", apiKey);
        webData.put("emailFrom", emailFrom);
        webData.put("emailTo", emailTo);
        webData.put("organization", orgName);
        webData.put("signingKey", signingKey);

        Template t5 = cfg.getTemplate("appengine-web.xml.ftl");
        FileWriter fw3 = new FileWriter(new File(out, "/appengine-web.xml"));
        t5.process(webData, fw3);

        System.out.println("Done");
    }

    private static Options getOptions() {

        Options options = new Options();

        Option orgName = new Option("on", "Organzation name");
        orgName.setLongOpt("orgName");
        orgName.setArgs(1);
        orgName.setRequired(true);

        Option awsId = new Option("ak", "AWS Access Key");
        awsId.setLongOpt("awsKey");
        awsId.setArgs(1);
        awsId.setRequired(true);

        Option awsSecret = new Option("as", "AWS Access Secret");
        awsSecret.setLongOpt("awsSecret");
        awsSecret.setArgs(1);
        awsSecret.setRequired(true);

        Option bucketName = new Option("bn", "AWS S3 bucket name");
        bucketName.setLongOpt("bucketName");
        bucketName.setArgs(1);
        bucketName.setRequired(true);

        Option gaeServer = new Option("gae",
                "GAE instance id - The `x` in https://x.appspot.com");
        gaeServer.setLongOpt("gaeId");
        gaeServer.setArgs(1);
        gaeServer.setRequired(true);

        Option emailFrom = new Option("ef",
                "Sender email - NOTE: Must be developer in GAE instance");
        emailFrom.setLongOpt("emailFrom");
        emailFrom.setArgs(1);
        emailFrom.setRequired(false);

        Option emailTo = new Option("et",
                "Recipient email of error notifications");
        emailTo.setLongOpt("emailTo");
        emailTo.setArgs(1);
        emailTo.setRequired(true);

        Option flowServices = new Option("fs",
                "FLOW Services url, e.g. http://services.akvoflow.org");
        flowServices.setLongOpt("flowServices");
        flowServices.setArgs(1);
        flowServices.setRequired(true);

        Option eventNotification = new Option(
                "en",
                "FLOW Services event notification endpoint, e.g. http://services.akvoflow.org:3030/event_notification");
        eventNotification.setLongOpt("eventNotification");
        eventNotification.setArgs(1);
        eventNotification.setRequired(true);

        Option enableChangeEvents = new Option(
                "ce",
                "true if the instance should store change event data and notify FLOW services event notification endpoint of new events");
        enableChangeEvents.setLongOpt("enableChangeEvents");
        enableChangeEvents.setArgs(1);
        enableChangeEvents.setRequired(false);

        Option mapsProvider = new Option("mp",
                "The maps provider to use. One of 'mapbox', 'google', 'cartodb'");
        mapsProvider.setLongOpt("mapsProvider");
        mapsProvider.setArgs(1);
        mapsProvider.setRequired(false);

        Option googleMapsRegionBias = new Option("rb",
                "Region bias code (only available for google maps layers)");
        googleMapsRegionBias.setLongOpt("googleMapsRegionBias");
        googleMapsRegionBias.setArgs(1);
        googleMapsRegionBias.setRequired(false);

        Option cartodbApiKey = new Option("ck", "Cartodb api key");
        cartodbApiKey.setLongOpt("cartodbApiKey");
        cartodbApiKey.setArgs(1);
        cartodbApiKey.setRequired(false);

        Option cartodbSqlApi = new Option("cs",
                "Url endpoint for the cartodb sql api");
        cartodbSqlApi.setLongOpt("cartodbSqlApi");
        cartodbSqlApi.setArgs(1);
        cartodbSqlApi.setRequired(false);

        Option cartodbHost = new Option("ch", "Cartodb host");
        cartodbSqlApi.setLongOpt("cartodbHost");
        cartodbSqlApi.setArgs(1);
        cartodbSqlApi.setRequired(false);

        Option cartodbTilerPort = new Option("ctp", "Cartodb tiler port");
        cartodbSqlApi.setLongOpt("cartodbTilerPort");
        cartodbSqlApi.setArgs(1);
        cartodbSqlApi.setRequired(false);

        Option outputFolder = new Option("o",
                "Output folder for configuration files");
        outputFolder.setLongOpt("outFolder");
        outputFolder.setArgs(1);
        outputFolder.setRequired(true);

        Option alias = new Option("a",
                "Instance alias, e.g. instance.akvoflow.org");
        alias.setLongOpt("alias");
        alias.setArgs(1);
        alias.setRequired(true);

        Option signingKey = new Option("sk", "Signing Key");
        signingKey.setLongOpt("signingKey");
        signingKey.setArgs(1);
        signingKey.setRequired(true);

        options.addOption(orgName);
        options.addOption(awsId);
        options.addOption(awsSecret);
        options.addOption(bucketName);
        options.addOption(gaeServer);
        options.addOption(emailFrom);
        options.addOption(emailTo);
        options.addOption(outputFolder);
        options.addOption(flowServices);
        options.addOption(eventNotification);
        options.addOption(enableChangeEvents);
        options.addOption(mapsProvider);
        options.addOption(googleMapsRegionBias);
        options.addOption(cartodbApiKey);
        options.addOption(cartodbSqlApi);
        options.addOption(cartodbHost);
        options.addOption(cartodbTilerPort);
        options.addOption(alias);
        options.addOption(signingKey);

        return options;
    }
}
