package com.gallatinsystems.common.util;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {
	private static final Logger log = Logger
			.getLogger(MailUtil.class.getName());

	public static Boolean sendMail(String fromAddress, String fromName,
			TreeMap<String, String> recipientList, String subject,
			String messageBody) {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		try {
			Message msg = new MimeMessage(session);
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

		} catch (AddressException e) {
			log.log(Level.SEVERE, "Could not send mail subj:" + subject + " "
					+ e.getMessage());
			return false;
		} catch (MessagingException e) {
			log.log(Level.SEVERE, "Could not send mail subj:" + subject + " "
					+ e.getMessage());
			return false;
		} catch (UnsupportedEncodingException e) {
			log.log(Level.SEVERE, "Could not send mail subj:" + subject + " "
					+ e.getMessage());
			return false;
		}
	}

}
