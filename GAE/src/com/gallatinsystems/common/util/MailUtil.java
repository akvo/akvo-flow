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

import java.io.IOException;
import java.util.List;
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

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;

/**
 * utility class for using the Google email service to send system-generated emails
 */
public class MailUtil {
    private static final String RECIPIENT_LIST_STRING = "recipientListString";
    private static final Logger log = Logger
            .getLogger(MailUtil.class.getName());

    /**
     * conviencence method for sending email to a single recipient. In this case, the email address
     * is used as the recipient name
     * 
     * @param fromAddress
     * @param fromName
     * @param recipient
     * @param subject
     * @param messageBody
     * @return
     */
    public static Boolean sendMail(String fromAddress, String fromName,
            String recipient, String subject, String messageBody) {
        TreeMap<String, String> recip = new TreeMap<String, String>();
        recip.put(recipient, recipient);
        return sendMail(fromAddress, fromName, recip, subject, messageBody);
    }

    /**
     * sends an email to a list of recipients
     * 
     * @param fromAddress
     * @param fromName
     * @param recipientList
     * @param subject
     * @param messageBody
     * @return
     */
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
            if (messageBody != null) {
                msg.setText(messageBody);
            } else {
                msg.setText("");
            }
            Transport.send(msg);
            return true;

        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not send mail subj:" + subject + "\n" + messageBody,
                    e);
            return false;
        }
    }

    /**
     * loads the recipient list configured in the application properties (appengine-web.xml)
     * 
     * @return
     */
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
     * @param subject
     * @param body
     * @param attachmentBytes
     * @param attachmentName
     * @param mimeType
     * @return
     */
    public static Boolean sendMail(String fromAddr, List<String> toAddressList,
            String subject, String body, byte[] attachmentBytes,
            String attachmentName, String mimeType) {

        MailService mailService = MailServiceFactory.getMailService();
        MailService.Message message = new MailService.Message();
        message.setSender(fromAddr);
        message.setSubject(subject);
        message.setTo(toAddressList);

        message.setHtmlBody("<HTML>" + body + "</HTML>");
        if (attachmentName == null) {
            attachmentName = "Report.txt";
        }
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
