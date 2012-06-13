/**
 * 
 */
package se.vgregion;

import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.vgregion.delegation.mail.DelegationMailSenderService;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;

/**
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 * 
 */
public class TestMailService {

    DelegationMailSenderService delegationMailSenderService;
    ClassPathXmlApplicationContext context;
    SimpleSmtpServer smtpServer;

    @Before
    public void setUp() throws Exception {

        String path = "classpath*:mail-spring-test.xml";
        context = new ClassPathXmlApplicationContext(path);

        delegationMailSenderService =
                (DelegationMailSenderService) context.getBean("delegationMailSenderService");

        smtpServer = SimpleSmtpServer.start(43025);

    }

    public void tearDown() throws Exception {
        smtpServer.stop();
    }

    @Test
    public void testDelegationMailService() throws AddressException, MessagingException {

        delegationMailSenderService.sendMail("sender@here.com", "receiver@there.com", "cepa", "depa");

        Assert.assertTrue(smtpServer.getReceivedEmailSize() == 1);

        Iterator<?> emailIter = smtpServer.getReceivedEmail();
        SmtpMessage email = (SmtpMessage) emailIter.next();
        Assert.assertTrue(email.getHeaderValue("Subject").equals("cepa"));
        Assert.assertTrue(email.getBody().equals("depa"));

    }

}
