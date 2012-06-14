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

    /** The delegation mail sender service. */
    DelegationMailSenderService delegationMailSenderService;

    /** The context. */
    ClassPathXmlApplicationContext context;

    /** The smtp server. */
    SimpleSmtpServer smtpServer;

    /**
     * Sets the up environment before performing the tests.
     * 
     * @throws Exception
     *             the exception
     */
    @Before
    public void setUp() throws Exception {

        String path = "classpath*:mail-spring-test.xml";
        context = new ClassPathXmlApplicationContext(path);

        delegationMailSenderService =
                (DelegationMailSenderService) context.getBean("delegationMailSenderService");

        smtpServer = SimpleSmtpServer.start(43025);

    }

    /**
     * Stops the smtp server after the tests.
     * 
     * @throws Exception
     *             the exception
     */
    public void tearDown() throws Exception {
        smtpServer.stop();
    }

    /**
     * Test delegation mail service.
     * 
     * @throws AddressException
     *             the address exception
     * @throws MessagingException
     *             the messaging exception
     */
    @Test
    public void testDelegationMailService() throws MessagingException {

        delegationMailSenderService.sendMail("sender@here.com", "receiver@there.com", "cepa", "depa");

        Assert.assertTrue(smtpServer.getReceivedEmailSize() == 1);

        Iterator<?> emailIter = smtpServer.getReceivedEmail();
        SmtpMessage email = (SmtpMessage) emailIter.next();
        Assert.assertTrue(email.getHeaderValue("Subject").equals("cepa"));
        Assert.assertTrue(email.getBody().equals("depa"));

    }

}
