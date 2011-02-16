package com.gallatinsystems.common.util;

import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.gallatinsystems.notification.NotificationRequest;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Attachment;
import com.google.appengine.api.mail.MailServiceFactory;

public class MailUtil {
	private static final String RECIPIENT_LIST_STRING = "recipientListString";
	private static final Logger log = Logger
			.getLogger(MailUtil.class.getName());

	public static Boolean sendMail(String fromAddress, String fromName,
			TreeMap<String, String> recipientList, String subject,
			String messageBody) {

		try {
			Message msg = createMessage();
			msg.setFrom(new InternetAddress(fromAddress, fromName));
			for (Map.Entry<String, String> recipientMap : recipientList
					.entrySet()) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						recipientMap.getKey(), recipientMap.getValue()));
			}
			msg.setSubject(subject);
			msg.setText(messageBody);
			Transport.send(msg);
			return true;

		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not send mail subj:" + subject + " ",
					e.getMessage());
			return false;
		}
	}

	public static TreeMap<String, String> loadRecipientList() {
		TreeMap<String, String> recipientList = new TreeMap<String, String>();
		String recipientListString = com.gallatinsystems.common.util.PropertyUtil
				.getProperty(RECIPIENT_LIST_STRING);
		StringTokenizer st = new StringTokenizer(recipientListString, "|");
		while (st.hasMoreTokens()) {
			String[] emailParts = st.nextToken().split(";");
			recipientList.put(emailParts[0], emailParts[1]);
		}
		return recipientList;
	}

	private static Message createMessage() {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		return new MimeMessage(session);
	}

	/**
	 * sends an html email to 1 or more recipients with an optional attachment
	 * 
	 * @param fromAddr
	 * @param toAddressList
	 * @param toDelimiter
	 * @param subject
	 * @param body
	 * @param attachmentBytes
	 * @param attachmentName
	 * @param mimeType
	 * @return
	 */
	public static Boolean sendMail(String fromAddr, String toAddressList,
			String toDelimiter, String subject, String body,
			byte[] attachmentBytes, String attachmentName, String mimeType) {
		try {
			//Message msg = createMessage();
			MailService service = MailServiceFactory.getMailService();
			MailService.Attachment attachment = new MailService.Attachment(attachmentName,attachmentBytes);
			
			com.google.appengine.api.mail.MailService.Message msg = new MailService.Message();
			msg.setSender(fromAddr);
			msg.setTextBody(body);
			
			//msg.setFrom(new InternetAddress(fromAddr));
			// TODO: parse and handle multiple destinations
			if (toDelimiter != null) {
				StringTokenizer strTok = new StringTokenizer(toAddressList,
						NotificationRequest.DELIMITER);
				while (strTok.hasMoreTokens()) {
//					msg.addRecipient(Message.RecipientType.TO,
//							new InternetAddress(strTok.nextToken()));
					msg.setTo(strTok.nextToken());
				}
			} else {
//				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
//						toAddressList));
				msg.setTo(toAddressList);
			}
			// msg.setSubject(subject);

			Multipart mp = new MimeMultipart();

			// MimeBodyPart htmlPart = new MimeBodyPart();
			// htmlPart.setContent(body, "text/html");
			// mp.addBodyPart(htmlPart);
			// msg.setText(body);
			// if (attachmentName != null && attachmentBytes != null) {
			// MimeBodyPart attachment = new MimeBodyPart();
			// attachment.setFileName(attachmentName);
			// attachment.setContent(attachmentBytes, mimeType);
			// mp.addBodyPart(attachment);
			// }
			// msg.setContent(mp);

			
			

			msg.setSubject(subject);
			msg.setTextBody(body);
			msg.setAttachments(attachment);
			service.send(msg);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Could not send mail subj:" + subject + " ",
					e);
			return false;
		}
		return true;
	}
}
