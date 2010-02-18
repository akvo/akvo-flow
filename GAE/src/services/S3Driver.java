package services;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import com.amazon.s3shell.S3Store;

public class S3Driver {
	private static final Logger log = Logger.getLogger(S3Driver.class
			.getName());

	
	Properties props;
	
	String aws_secret_key = "";
	String aws_identifier = "";
	
	public S3Driver(){
		props = System.getProperties();
		aws_secret_key=props.getProperty("aws_secret_key");
		aws_identifier = props.getProperty("aws_identifier");
	}
	
	public void uploadFile(String bucketName, final String fileName, byte[] file){
		S3Store s3Store = new com.amazon.s3shell.S3Store("s3.amazonaws.com", aws_identifier, aws_secret_key);
		s3Store.setBucket(bucketName);
		try {
			boolean storedFlag=s3Store.storeItem(fileName, file,"public-read");
			log.info("Stored image: " + fileName + " " + storedFlag);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
