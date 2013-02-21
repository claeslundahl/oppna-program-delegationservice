package se.vgregion.delegation.test.webservice;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.quartz.impl.StdScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;

public class TestMail extends AbstractTestBase {

    static private final Logger LOGGER = LoggerFactory.getLogger(AbstractTestBase.class);

    SimpleSmtpServer smtpServer;

    @Override
    @Before
    public void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("classpath:settings/Spring-Quartz-test.xml");
        initStuffFromContext();
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

    @Test
    public void testEmailNotificationJob1() throws Exception {
        long dayInMilis = 86400000;
        int mailsBeforeTest = smtpServer.getReceivedEmailSize();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Date today = new Date();
        Date ao = new Date(today.getTime() - 95 * dayInMilis);
        Date vt = new Date(today.getTime() + 20 * dayInMilis);
        Date vf = new Date(today.getTime() - 100 * dayInMilis);

        saveADelegation(simpleDateFormat.format(ao), simpleDateFormat.format(vf), simpleDateFormat.format(vt));

        for (int i = 1; i <= 30 && 1 + mailsBeforeTest != smtpServer.getReceivedEmailSize(); i++) {
            Thread.sleep(500);
        }
        Assert.assertEquals(1 + mailsBeforeTest, smtpServer.getReceivedEmailSize());

        StdScheduler schedulerFactoryBean = (StdScheduler) context.getBean("schedulerFactoryBean");

        schedulerFactoryBean.shutdown();
    }

    @Test
    public void testEmailNotificationJob2() throws Exception {
        long dayInMilis = 86400000;
        int mailsBeforeTest = smtpServer.getReceivedEmailSize();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Date today = new Date();
        Date ao = new Date(today.getTime() - 95 * dayInMilis);
        Date vt = new Date(today.getTime() + 10 * dayInMilis);
        Date vf = new Date(today.getTime() - 100 * dayInMilis);

        saveADelegation(simpleDateFormat.format(ao), simpleDateFormat.format(vf), simpleDateFormat.format(vt));

        for (int i = 1; i <= 30 && 1 + mailsBeforeTest != smtpServer.getReceivedEmailSize(); i++) {
            Thread.sleep(500);
        }
        Assert.assertEquals(1 + mailsBeforeTest, smtpServer.getReceivedEmailSize());

        StdScheduler schedulerFactoryBean = (StdScheduler) context.getBean("schedulerFactoryBean");

        schedulerFactoryBean.shutdown();
    }

    @Test
    public void testEmailNotificationJobTwoEmails() throws Exception {
        long dayInMilis = 86400000;
        int mailsBeforeTest = smtpServer.getReceivedEmailSize();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Date today = new Date();
        Date ao = new Date(today.getTime() - 95 * dayInMilis);
        Date vt = new Date(today.getTime() + 10 * dayInMilis);
        Date vf = new Date(today.getTime() - 100 * dayInMilis);

        saveADelegation(simpleDateFormat.format(ao), simpleDateFormat.format(vf), simpleDateFormat.format(vt), "test@vgregion.se,test@vgregion.se");

        for (int i = 1; i <= 30 && 1 + mailsBeforeTest != smtpServer.getReceivedEmailSize(); i++) {
            Thread.sleep(500);
        }
        Assert.assertEquals(2 + mailsBeforeTest, smtpServer.getReceivedEmailSize());

        StdScheduler schedulerFactoryBean = (StdScheduler) context.getBean("schedulerFactoryBean");

        schedulerFactoryBean.shutdown();
    }

    @Override
    public void initStuffFromContext() throws Exception {
        super.initStuffFromContext();
        smtpServer = SimpleSmtpServer.start(Integer.valueOf(propertiesBean.getMailServerPort()));
    }

    @Override
    public void tearDown() throws Exception {
        if (smtpServer != null) {
            smtpServer.stop();
        }
        super.tearDown();
    }
}
