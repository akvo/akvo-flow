/*
 *  Copyright (C) 2010-2012, 2020 Stichting Akvo (Akvo Foundation)
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

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.email.Recipient;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import javax.mail.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * utility class for using the Google email service to send system-generated emails
 */
public class MailUtil {
    public static final String EMAIL_HOST = "emailHost";
    public static final String EMAIL_PORT = "emailPort";
    public static final String EMAIL_USER = "emailUser";
    public static final String EMAIL_PASSWORD = "emailPassword";
    private static final Logger log = Logger.getLogger(MailUtil.class.getName());

    // Both senders below use SMTPS -- implicit TLS, the relay speaks TLS from the
    // first byte -- because that is what emailPort 465 means. STARTTLS (SMTP_TLS,
    // used while mail went through Mailjet on 587) is a different handshake, and
    // the port alone does not select between them: pointing STARTTLS at 465 opens
    // a plaintext socket against a TLS-only port and blocks until the session
    // timeout rather than failing outright. Changing emailPort without changing
    // this is therefore a silent hang, not a configuration error. akvo-flow-services
    // and akvo-lumen carry the same warning over their own SMTP settings.

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
        TreeMap<String, String> recip = new TreeMap<>();
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
    public static Boolean sendMail(String fromAddress, String fromName, Map<String, String> recipientList,
                                   String subject, String messageBody) {

        try {
            List<Recipient> recipients = new ArrayList<>();

            for (Map.Entry<String, String> recipientMap : recipientList.entrySet()) {
                recipients.add(new Recipient(recipientMap.getKey(), recipientMap.getValue(), Message.RecipientType.BCC));
            }

            Email email = EmailBuilder.startingBlank()
                    .from(fromName, fromAddress)
                    .withSubject(subject)
                    .withPlainText(messageBody)
                    .withRecipients(recipients)
                    .buildEmail();

            String host = PropertyUtil.getProperty(EMAIL_HOST);
            Integer port = Integer.valueOf(PropertyUtil.getProperty(EMAIL_PORT));
            String username = PropertyUtil.getProperty(EMAIL_USER);
            String password = PropertyUtil.getProperty(EMAIL_PASSWORD);

            Mailer mailer = MailerBuilder
                    .withSMTPServer(host, port, username, password)
                    .withTransportStrategy(TransportStrategy.SMTPS)
                    .buildMailer();

            mailer.sendMail(email);

            return true;
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not send mail subj:" + subject + "\n" + messageBody,
                    e);
            return false;
        }
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

        Email email = EmailBuilder.startingBlank()
                .from(fromAddr)
                .toMultiple(toAddressList)
                .withSubject(subject)
                .appendTextHTML(body)
                .withAttachment(attachmentName, attachmentBytes, mimeType)
                .buildEmail();

        String host = PropertyUtil.getProperty(EMAIL_HOST);
        Integer port = Integer.valueOf(PropertyUtil.getProperty(EMAIL_PORT));
        String username = PropertyUtil.getProperty(EMAIL_USER);
        String password = PropertyUtil.getProperty(EMAIL_PASSWORD);

        Mailer mailer = MailerBuilder
                .withSMTPServer(host, port, username, password)
                .withTransportStrategy(TransportStrategy.SMTPS)
                .buildMailer();

        try {
            mailer.sendMail(email);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Could not send email with attachment", e);
            return false;
        }
        return true;
    }

}
