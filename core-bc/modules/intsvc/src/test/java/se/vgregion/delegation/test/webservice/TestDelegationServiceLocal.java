/**
 * 
 */
package se.vgregion.delegation.test.webservice;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.BeforeClass;
import org.quartz.impl.StdScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import se.riv.authorization.delegation.getactivedelegations.v1.rivtabp21.GetActiveDelegationsResponderInterface;
import se.riv.authorization.delegation.getdelegation.v1.rivtabp21.GetDelegationResponderInterface;
import se.riv.authorization.delegation.getdelegations.v1.rivtabp21.GetDelegationsResponderInterface;
import se.riv.authorization.delegation.getdelegationsbyunitandrole.v1.rivtabp21.GetDelegationsbyUnitAndRoleResponderInterface;
import se.riv.authorization.delegation.getinactivedelegations.v1.rivtabp21.GetInactiveDelegationsResponderInterface;
import se.riv.authorization.delegation.hasdelegation.v1.rivtabp21.HasDelegationResponderInterface;
import se.riv.authorization.delegation.removedelegation.v1.rivtabp21.RemoveDelegationResponderInterface;
import se.riv.authorization.delegation.savedelegations.v1.rivtabp21.SaveDelegationsResponderInterface;
import se.vgregion.delegation.mail.DelegationMailSenderService;
import se.vgregion.delegation.ws.util.PropertiesBean;

import com.dumbster.smtp.SimpleSmtpServer;

/**
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 * 
 */
public class TestDelegationServiceLocal extends TestDelegationServiceWS {
    static private final Logger logger = LoggerFactory.getLogger(TestDelegationServiceLocal.class);

    @BeforeClass
    public static void startUpTestInfo() {
        logger.info("Starting test for local services!");
    }

    @Override
    protected void setUpContext() {
        String path = "classpath:/settings/serverConfLocal.xml";
        context = new ClassPathXmlApplicationContext(path);
    }

    @Override
    public void setUp() throws Exception {

        setUpContext();

        delegationMailSenderService =
                (DelegationMailSenderService) context.getBean("delegationMailSenderService");

        PropertiesBean propertiesBean = (PropertiesBean) context.getBean("propertiesBean");

        smtpServer = SimpleSmtpServer.start(Integer.valueOf(propertiesBean.getMailServerPort()));

        activeDelegationsResponderInterface =
                (GetActiveDelegationsResponderInterface) context
                        .getBean("activeDelegationsResponderInterface");
        getDelegationResponderInterface =
                (GetDelegationResponderInterface) context.getBean("getDelegationResponderInterface");
        inactiveDelegationsResponderInterface =
                (GetInactiveDelegationsResponderInterface) context
                        .getBean("inactiveDelegationsResponderInterface");
        getDelegationsbyUnitAndRoleResponderInterface =
                (GetDelegationsbyUnitAndRoleResponderInterface) context
                        .getBean("getDelegationsbyUnitAndRoleResponderInterface");
        getDelegationsResponderInterface =
                (GetDelegationsResponderInterface) context.getBean("getDelegationsResponderInterface");
        hasDelegationResponderInterface =
                (HasDelegationResponderInterface) context.getBean("hasDelegationResponderInterface");
        saveDelegationsResponderInterface =
                (SaveDelegationsResponderInterface) context.getBean("saveDelegationsResponderInterface");
        removeDelegationResponderInterface =
                (RemoveDelegationResponderInterface) context.getBean("removeDelegationResponderInterface");

    }

    @Override
    public void tearDown() throws Exception {
        smtpServer.stop();

        StdScheduler schedulerFactoryBean = (StdScheduler) context.getBean("schedulerFactoryBean");

        schedulerFactoryBean.shutdown();

        DriverManagerDataSource dataSource = (DriverManagerDataSource) context.getBean("dataSource");

        Connection conn = dataSource.getConnection();

        Statement st = conn.createStatement();
        st.execute("SHUTDOWN");
        conn.close();
    }

}
