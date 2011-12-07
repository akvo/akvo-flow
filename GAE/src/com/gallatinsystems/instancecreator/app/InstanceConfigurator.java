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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.gallatinsystems.instancecreator.util.S3PolicySigner;

public class InstanceConfigurator {
	private VelocityEngine engine = null;
	private static final Logger log = Logger
			.getLogger(InstanceConfigurator.class.getName());
	private static String aws_secret_key = null;

	public static void main(String[] args) {
		InstanceConfigurator ic = new InstanceConfigurator();
		String[] directories;
		String s3policyFileTemplateName;
		aws_secret_key = args[0];
		ic.addAttribute("awsSecretKey", args[0]);
		ic.addAttribute("awsIdentifier", args[1]);
		ic.addAttribute("instanceName", args[2]);
		ic.addAttribute("s3bucket", args[3]);
		String s3bucket = args[3];
		directories = args[4].split("\\|");
		s3policyFileTemplateName = args[5];
		ic.addAttribute("s3Id", args[1]);
		ic.addAttribute("signingKey", args[6]);
		ic.addAttribute("dataUploadUrl", args[7]);
		ic.addAttribute("serverBase", args[8]);
		ic.addAttribute("surveyS3Url", args[7] + "surveys" );
		ic.addAttribute("s3urldevicezip",args[7]+"devicezip");
		ic.addAttribute("storepass", args[9]);
		ic.addAttribute("keypass", args[10]);
		ic.addAttribute("alias", args[11]);
		ic.addAttribute("alais",args[11]);
		ic.addAttribute("reportsEmailAddress", args[12]);
		ic.addAttribute("defaultPhotoCaption", args[13]);
		ic.addAttribute("scoreAPFlag", args[14]);
		ic.addAttribute("organization", args[15]);
		ic.addAttribute("s3Url",args[7]);
		ic.addAttribute("s3url",args[7]);
		String localLocation = args[16];
		ic.addAttribute("keystore",args[17]);
		ic.addAttribute("mapsApiKey",args[18]);
		
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
				}
				policyFiles.put(directory, documents);
			}
			String appenginexml = ic.buildfile("war/appengine-web.vm");
			ic.writeFile(localLocation, "appengine-web.xml", appenginexml);
			String portalgwtxml = ic.buildfile("war/portalgwt.vm");
			ic.writeFile(localLocation, "portal.gwt.xml", portalgwtxml);
			String surveyentrygwtxml = ic.buildfile("war/surveyentrygwtxml.vm");
			ic.writeFile(localLocation, "surveyEntry.gwt.xml", surveyentrygwtxml);
			String uploadconstantproperties = ic.buildfile("war/UploadConstants.vm");
			ic.writeFile(localLocation, "UploadConstants.properties", uploadconstantproperties);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void writeFile(String location, String name, String contents)
			throws IOException {
		File file = new File(location + System.getProperty("file.separator")
				+ name);
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
		return mergeContext(context,vmName);
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
