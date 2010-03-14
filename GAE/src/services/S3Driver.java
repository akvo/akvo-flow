package services;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.amazon.s3shell.S3Store;

public class S3Driver {
	private static final Logger log = Logger
			.getLogger(S3Driver.class.getName());

	Properties props;

	String aws_secret_key = "";
	String aws_identifier = "";
	private S3Store s3Store;

	private void init() {
		if (s3Store == null) {
			s3Store = new com.amazon.s3shell.S3Store("s3.amazonaws.com",
					aws_identifier, aws_secret_key);
		}
	}

	public S3Driver() {
		props = System.getProperties();
		aws_secret_key = props.getProperty("aws_secret_key");
		aws_identifier = props.getProperty("aws_identifier");
		init();
	}

	public S3Driver(String awsSecretKey, String awsIdentifier) {
		this.aws_secret_key = awsSecretKey;
		this.aws_identifier = awsIdentifier;
		init();
	}
	

	public void uploadFile(String bucketName, final String fileName,  byte[] file) {

		s3Store.setBucket(bucketName);
		try {
			boolean storedFlag = s3Store.storeItem(fileName, file,
					"public-read");
			log.info("Stored image: " + fileName + " " + storedFlag);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private HashMap<String, String> getListOfFilesToUpload(String fileList) {
		HashMap<String, String> fileListMap = new HashMap<String, String>();

		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(fileList));
			try {
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null) {
					if (!line.isEmpty() && !line.trim().equals(""))
						fileListMap.put(line, line);
				}
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		log.info("Number of items added to Map: " + fileListMap.size());
		return fileListMap;
	}

	public void uploadAllFilesInDirectory(String bucketName, String fileDir,
			String fileListPath) {
		HashMap<String, String> filesToUpload = getListOfFilesToUpload(fileListPath);
		for (String key : filesToUpload.keySet()) {
			log.info("map values: " + key);
		}
		s3Store.setBucket(bucketName);
		
		File dir = new File(fileDir);
		log.info("Files to upload: " + dir.list().length);
		int i = 0;
		ArrayList<String> files = new ArrayList<String>();
		List<String> filesInBucket = null;
		List<String> filesInBucket1 = null;
		List<String> filesInBucket2 = null;
		List<String> filesInBucket3 = null;
		try {
			filesInBucket = s3Store.listItems("images/africa/malawi/");
			int iCountInBucket = 0;
			String marker = null;
			for (String fileInBucket : filesInBucket) {
				log.info(iCountInBucket++ + "file in bucket: " + fileInBucket);
				marker = fileInBucket;
			}
			filesInBucket1 = s3Store.listItems("images/africa/malawi/", marker);
			for (String fileInBucket : filesInBucket1) {
				log.info(iCountInBucket++ + "file in bucket: " + fileInBucket);
				marker = fileInBucket;
			}
			filesInBucket2 = s3Store.listItems("images/africa/malawi/", marker);
			for (String fileInBucket : filesInBucket2) {
				log.info(iCountInBucket++ + "file in bucket: " + fileInBucket);
				marker = fileInBucket;
			}
			filesInBucket3 = s3Store.listItems("images/africa/malawi/", marker);
			for (String fileInBucket : filesInBucket3) {
				log.info(iCountInBucket++ + "file in bucket: " + fileInBucket);
				marker = fileInBucket;
			}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		List<String> uberFiles = new ArrayList<String>();
		uberFiles.addAll(filesInBucket);
		uberFiles.addAll(filesInBucket1);
		uberFiles.addAll(filesInBucket2);
		uberFiles.addAll(filesInBucket3);
		log.info("uberfiles: " + uberFiles.size());
		
		for (File file : dir.listFiles()) {

			if (file.getName().toLowerCase().contains(".jpg")) {
				String fileName = file.getName().toLowerCase();
				String searchKey = fileName.substring(0, fileName
						.indexOf(".jpg"));
				if (filesToUpload.get(searchKey) != null) {
					String searchForFile = "images/africa/malawi/"+fileName;
					boolean fileExistsInBucket = uberFiles.contains(searchForFile);
					if (!fileExistsInBucket) {
						files.add(file.getName());
						log.info("adding: " + file.getName());
						byte[] fileContents = null;
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						FileInputStream fis = null;
						try {
							fis = new FileInputStream(file.toString());
							byte[] buffer = new byte[2048];
							int size;
							while ((size = fis.read(buffer, 0, buffer.length)) != -1) {
								out.write(buffer, 0, size);
							}
							boolean storedFlag = s3Store.storeItem(fileName,
									out.toByteArray(), "public-read");
							log.info("Stored image: " + fileName + " "
									+ storedFlag);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				i++;
				log.info("   Uploaded file number: " + i + " "
						+ file.toString());
			}
		}
		log.info("found " + files.size() + " to upload");

	}

	public static void main(String[] args) {
		S3Driver s3driver = new S3Driver(args[0], args[1]);
		s3driver.uploadAllFilesInDirectory(args[2], args[3], args[4]);

	}

	class Uploader implements Runnable {
		private String bucketName;
		public ArrayList<String> fileNames = new ArrayList<String>();

		public void run() {

			s3Store.setBucket(bucketName);
			for (String fileName : fileNames) {
				byte[] fileContents = null;
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(fileName);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] buffer = new byte[2048];
				int size;
				try {
					while ((size = fis.read(buffer, 0, buffer.length)) != -1) {
						out.write(buffer, 0, size);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					boolean storedFlag = s3Store.storeItem(fileName, out
							.toByteArray(), "public-read");
					log.info("Stored image: " + fileName + " " + storedFlag);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
