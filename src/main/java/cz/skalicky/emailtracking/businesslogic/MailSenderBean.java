package cz.skalicky.emailtracking.businesslogic;

import java.nio.charset.Charset;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Provides a set of common static functions related to emails. The implementation borrowed from <a href=
 * "http://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/" >Mkyong</a>.
 *
 * @author <a href="mailto:skalicky.tomas@gmail.com">Tomas Skalicky</a>
 */
public class MailSenderBean {

    private static final Logger log = LogManager.getLogger(MailSenderBean.class);

    // DO NOT FORGET to change the implementation of the sendMail function if
    // you change these constants.
    private static final String HOST = "smtp.gmail.com";
    private static final int TLS_PORT = 587;

    private static final String MAIL_SMTP_HOST = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    private final String senderEmail;
    private final String senderUsername;
    private final String senderPassword;
    private final String recipientEmail;

    public MailSenderBean(final String senderEmail, final String senderUsername, final String senderPassword,
            final String recipientEmail) {
        this.senderEmail = senderEmail;
        this.senderUsername = senderUsername;
        this.senderPassword = senderPassword;
        this.recipientEmail = recipientEmail;
    }

    /**
     * Sends an email with the given arguments; no matter which way of sending (i.e. SSL, TLS, or something
     * else) is used. <b>Precondition:</b> The function assumes that the given <code>recipient</code>
     * represents a correct email address.
     */
    public void sendMail(String subject, String messageText) {

        log.info(String.format("Sending email subject='%s', messageText='%s'", subject, messageText));

        sendMailViaTLS(subject, messageText);
    }

    /**
     * Sends an email with the given arguments via TLS/STARTTLS protocol. <b>Precondition:</b> The function
     * assumes that the given <code>recipient</code> represents a correct email address.
     */
    private void sendMailViaTLS(String subject, String messageText) {
        Properties props = new Properties();
        props.put(MAIL_SMTP_AUTH, Boolean.TRUE);
        props.put("mail.smtp.starttls.enable", Boolean.TRUE);
        props.put(MAIL_SMTP_HOST, MailSenderBean.HOST);
        props.put(MAIL_SMTP_PORT, MailSenderBean.TLS_PORT);

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderUsername, senderPassword);
            }
        });

        try {
            sendMail(session, subject, messageText);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The final part of all functions <code>sendMailWithoutSSL</code>, <code>sendMailViaSSL</code> and
     * <code>sendMailViaTLS</code>.
     * <p>
     * NOTE: the content of the result email is HTML text.
     */
    private void sendMail(Session session, String subject, String messageText) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);
        message.setContent(getMessageBody(messageText));

        Transport.send(message);

        MailSenderBean.log.info(String.format("Email '%s' has been sent to '%s'.", subject, recipientEmail));
    }

    /**
     * Creates an email body which contents the given <code>messageText</code>. The MIME subtype is
     * <code>html</code>.
     */
    private Multipart getMessageBody(String messageText) throws MessagingException {
        MimeBodyPart messageBodyPart = new MimeBodyPart();

        // Fills the message
        messageBodyPart.setText(messageText, Charset.forName("UTF-8").displayName(), "html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        return multipart;
    }

}
