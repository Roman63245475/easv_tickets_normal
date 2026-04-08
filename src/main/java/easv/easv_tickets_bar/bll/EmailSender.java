package easv.easv_tickets_bar.bll;

import easv.easv_tickets_bar.CustomExceptions.MyException;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class EmailSender {

    private final String PROP_FILE = "config/email_config.settings";
    private File pdfFile;
    private String toEmail;
    private Properties emailProperties;

    public EmailSender(File file, String toEmail) throws IOException {
        this.pdfFile = file;
        this.toEmail = toEmail;
        this.emailProperties = new Properties();
        emailProperties.load(new FileInputStream(new File(PROP_FILE)));
    }

    public boolean sendEmail(String name, String eventName) {
        try {
            Authenticator auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailProperties.getProperty("email"), emailProperties.getProperty("password"));
                }
            };
            Session session = Session.getInstance(this.emailProperties, auth);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.emailProperties.getProperty("email")));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(this.toEmail));
            message.setSubject("Purchase of a ticket");
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Hello " + name+"\n" + "You've recently bought a ticket for the " + eventName + "!\n" + "Here's a digital ticket that you've bought");
            MimeBodyPart attachment = new MimeBodyPart();
            attachment.attachFile(this.pdfFile);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachment);
            message.setContent(multipart);
            Transport.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }

    }


}
