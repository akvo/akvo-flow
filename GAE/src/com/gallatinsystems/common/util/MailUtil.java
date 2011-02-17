package com.gallatinsystems.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.gallatinsystems.notification.NotificationRequest;
import com.google.appengine.api.mail.MailService;
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

		MailService mailService = MailServiceFactory.getMailService();
		MailService.Message message = new MailService.Message();
		message.setSender(fromAddr);
		message.setSubject(subject);

		if (toDelimiter != null) {
			Collection<String> toList = new ArrayList<String>();
			StringTokenizer strTok = new StringTokenizer(toAddressList,
					NotificationRequest.DELIMITER);
			while (strTok.hasMoreTokens()) {
				toList.add(strTok.nextToken());
			}
			message.setTo(toList);
		} else {
			message.setTo(toAddressList);
		}

		message.setHtmlBody("<HTML>" + body + "</HTML>");
		MailService.Attachment attachment = new MailService.Attachment(
				attachmentName, attachmentBytes);
		message.setAttachments(attachment);
		try {
			mailService.send(message);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Could not send email with attachment", e);
			return false;
		}
		return true;
	}

}
