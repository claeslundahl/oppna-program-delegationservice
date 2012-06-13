package se.vgregion.delegation.mail;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 * 
 */

public class DelegationMailSenderService {

    // private MailSender mailSender;
    private JavaMailSender mailSender;

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(final String from, final String to, final String subject, final String text) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {

                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mimeMessage.setSubject(subject, "UTF-8");
                mimeMessage.setContent(text, "text/html; charset=UTF-8");
                mimeMessage.setFrom(new InternetAddress(from));
                Address[] addresses = new Address[]{new InternetAddress("no-replay@vgregion.se")};
                mimeMessage.setReplyTo(addresses);
            }
        };
        try {
            this.mailSender.send(preparator);
        } catch (MailException ex) {
            // simply log it and go on...
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }
    }

    // public static void main(String[] args) {
    // ApplicationContext context = new ClassPathXmlApplicationContext("mail-spring.xml");
    //
    // DelegationMailSenderService service =
    // (DelegationMailSenderService) context.getBean("userRegistrationService");
    // service.sendMail("apa@gmail.com", "test@gmail.com", "test", "test test");
    //
    // }
}
