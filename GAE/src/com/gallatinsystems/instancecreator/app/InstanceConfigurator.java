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

package com.gallatinsystems.instancecreator.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.gallatinsystems.instancecreator.util.S3PolicySigner;

/**
 * 
 * Class to facilitate configuring new instances based on template based
 * configuration files.
 * The files being generated are:
 * appengine-web.xml
 * UploadConstants.properties
 * survey.properties
 * 
 * To use this utility, you must supply all of following values on the command
 * line in order:
 * 
 * <pre>
 * awsSecretKey - secret key used when connecting to Amazon S3 (for file uploads)
 * awsIdentifier  - the Amazon account identifier for S3
 * instanceName  - the name for this flow Instance. This will be used in url formation (so if the value supplied here is "flow-myorg" then the instance url would be flow-myorg.appspot.com
 * directories - pipe (|) delimited list of S3 sub-directories (within the s3Bucket) for which upload keys should be generated. Usually this is the string "reports|devicezip|surveys|helpcontent|bootstrap|images
 * s3policyFileTemplateName - name of the template file to use for the s3 policy file generation. Usually this is s3policy.vm
 * signingKey - the secret key used to sign submissions from the Handheld. This can be any unique string though for best results, a large prime number is prefered
 * storepass - password for the keystore used to sign the applet jar
 * keypass - password for the certificate key in the keystore for signing the applet jar
 * alias - certificate alias in the keystore for signing the applet jar
 * reportsEmailAddress - email address the system should use as the "from" address when sending notifications. NOTE: this address must be a gmail account that has been set up as an instance developer in the appengine console)
 * scoreAPFlag - either true or false (depending on whether the instance should use the scoring module or not)
 * organization - name to use for the organization (this will be auto populated on some fields and can show up on the public map)
 * localLocation - directory on the local file system that will store the output of running this utililty
 * keystore - full path to a java keystore that contains a certificate that will be used to sign the applet jar
 * mapsApiKey - a Google Maps API key that has been registered for the new instance's domain
 * restApiKey - test
 * </pre>
 * 
 * derived quantities are: 
 * * s3bucket - the root directory on S3 for this instance - assumed to be same as instance name
 * * dataUploadUrl - url used for data uploads (assumed to be the root of the S3 account ... http://instanceName.s3.amazonaws.com/)
 * * serverBase - base url for the instance (assumed to be http://instanceName.appspot.com)
 * 
 * 
 */
public class InstanceConfigurator {
	private VelocityEngine engine = null;
	private static final Logger log = Logger
			.getLogger(InstanceConfigurator.class.getName());
	private static String aws_secret_key = null;

	public static void main(String[] args) {
		InstanceConfigurator ic = new InstanceConfigurator();
		checkUsage(args);
		String[] directories;
		String s3policyFileTemplateName;
		aws_secret_key = args[0];
		String instanceName = args[2];
		ic.addAttribute("awsSecretKey", args[0]);
		ic.addAttribute("awsIdentifier", args[1]);
		ic.addAttribute("instanceName", instanceName);
		ic.addAttribute("s3bucket", instanceName);
		String s3bucket = instanceName;
		directories = args[3].split("\\|");
		s3policyFileTemplateName = args[4];
		ic.addAttribute("s3Id", args[1]);
		ic.addAttribute("signingKey", args[5]);
		ic.addAttribute("instanceName", instanceName);
		ic.addAttribute("dataUploadUrl", "http://" + instanceName + ".s3.amazonaws.com");
		ic.addAttribute("serverBase", "http://" + instanceName + ".appspot.com");
		ic.addAttribute("surveyS3Url", "http://" + instanceName + ".s3.amazonaws.com/surveys");
		ic.addAttribute("s3urldevicezip", "http://" + instanceName + ".s3.amazonaws.com/devicezip");
		ic.addAttribute("storepass", args[6]);
		ic.addAttribute("keypass", args[7]);
		ic.addAttribute("alias", args[8]);
		ic.addAttribute("alais", args[8]);
		ic.addAttribute("reportsEmailAddress", args[9]);
		ic.addAttribute("scoreAPFlag", args[10]);
		ic.addAttribute("organization", args[11]);
        ic.addAttribute("s3Url", "http://" + instanceName + ".s3.amazonaws.com");
        ic.addAttribute("s3url", "http://" + instanceName + ".s3.amazonaws.com");
		String localLocation = args[12];
		ic.addAttribute("keystore", args[13]);
		ic.addAttribute("mapsApiKey", args[14]);
		ic.addAttribute("restApiKey", args[15].equals("test") || args[15].equals("") ? UUID.randomUUID().toString() : args[15]);

		localLocation = ic.createLocalDeployDir(localLocation, args[2]);

		TreeMap<String, String[]> policyFiles = new TreeMap<String, String[]>();
		S3PolicySigner s3PolicySigner = new S3PolicySigner();
		try {
			for (String directory : directories) {
				String policyFile = ic.buildPolicyFile(s3bucket, directory,
						s3policyFileTemplateName);
				String[] documents = s3PolicySigner.createPolicyString(
						policyFile, aws_secret_key);
				log.log(Level.INFO, "bucket: " + s3bucket + " directory: "
						+ directory + "\nsig: " + documents[0] + "\nkey:"
						+ documents[1] + "\n" + policyFile);
				if (directory.equals("reports")) {
					ic.addAttribute("reportS3Sig", documents[0]);
					ic.addAttribute("reportsS3Policy", documents[1]);
				} else if (directory.equals("devicezip")) {
					ic.addAttribute("surveyDataS3Sig", documents[0]);
					ic.addAttribute("surveyDataS3Policy", documents[1]);
				} else if (directory.equals("bootstrap")) {
					ic.addAttribute("bootstrapS3Sig", documents[0]);
					ic.addAttribute("bootstrapS3Policy", documents[1]);
				} else if (directory.equals("helpcontent")) {
					ic.addAttribute("helpcontentS3Sig", documents[0]);
					ic.addAttribute("helpcontentS3Policy", documents[1]);
				} else if (directory.equals("images")) {
					ic.addAttribute("imagesS3Sig", documents[0]);
					ic.addAttribute("imagesS3Policy", documents[1]);
				} else if (directory.equals("surveys")) {
					ic.addAttribute("surveyS3Sig", documents[0]);
					ic.addAttribute("surveyPolicy", documents[1]);
                } else if ("apk".equals(directory)) {
					ic.addAttribute("apkS3Sig", documents[0]);
					ic.addAttribute("apkS3Policy", documents[1]);
				}
				policyFiles.put(directory, documents);
			}

			String appenginexml = ic.buildfile("war/appengine-web.vm");
			ic.writeFile(localLocation, "appengine-web.xml", appenginexml);
            String uploadconstantproperties = ic.buildfile("war/UploadConstants.vm");
            ic.writeFile(localLocation, "UploadConstants.properties", uploadconstantproperties);
			String surveyproperties = ic.buildfile("war/surveyProperties.vm");
			ic.writeFile(localLocation, "survey.properties", surveyproperties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void checkUsage(String[] args) {
		if (args.length != 16) {
			System.out.println("Invalid command line arguments.\n\t" +
				               "Usage:\n\t java com.gallatinsystems.instancecreator.app.InstanceConfigurator " +
							   "<awsSecretKey> <awsIdentifier> <instanceName> <directories> <s3policyFileTemplateName> " +
							   "<signingKey> <storepass> <keypass> <alias> <reportsEmailAddress> <scoreAPFlag> " +
							   "<organization> <localLocation> <keystore> <mapsApiKey> <restApiKey>");
			System.exit(1);
		}
	}

	private void writeFile(String location, String name, String contents)
			throws IOException {
		File file = new File(location + '/' + name);

		if (file.exists()) {
			if (file.delete()) {
				file.createNewFile();
			}
		}

		Writer output = new BufferedWriter(new FileWriter(file));
		try {
			output.write(contents);
		} finally {
			output.close();
		}
	}

	private String createLocalDeployDir(String dir, String instanceName) {
		File f = new File(dir + instanceName);
		if (!f.exists()) {
			if (f.mkdir()) {
				return f.getAbsolutePath();
			}
		} else {
			return f.getAbsolutePath();
		}
		return null;
	}

	private String buildfile(String vmName) throws Exception {
		VelocityContext context = new VelocityContext();
		for (Entry<String, String> item : attributeMap.entrySet()) {
			context.put(item.getKey(), item.getValue());
		}
		return mergeContext(context, vmName);
	}

	private HashMap<String, String> attributeMap = new HashMap<String, String>();

	public void addAttribute(String attributeName, String attributeValue) {
		attributeMap.put(attributeName, attributeValue);
	}

	public InstanceConfigurator() {
		engine = new VelocityEngine();
		engine.setProperty("runtime.log.logsystem.class",
				"org.apache.velocity.runtime.log.NullLogChute");
		try {
			engine.init();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not initialize velocity", e);
		}
	};

	private String buildPolicyFile(String s3bucket, String directory,
			String templateName) throws Exception {
		VelocityContext context = new VelocityContext();
		context.put("s3bucket", s3bucket);
		context.put("directory", directory);
		return mergeContext(context, templateName);
	}

	/**
	 * merges a hydrated context with a template identified by the templateName
	 * passed in.
	 * 
	 * @param context
	 * @param templateName
	 * @return
	 * @throws Exception
	 */
	private String mergeContext(VelocityContext context, String templateName)
			throws Exception {
		Template t = engine.getTemplate(templateName);
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
		context = null;
		return writer.toString();
	}

}
