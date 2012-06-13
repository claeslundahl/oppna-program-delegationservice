/**
 * 
 */
package se.vgregion;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import se.vgregion.delegation.DelegationExpieryAlertJob;
import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationBlock;

/**
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 * 
 */
public class TestDelegationExpieryAlertJob {

    private static final long DayInMilis = 86400000;

    ClassPathXmlApplicationContext context;
    DelegationService delegationService;
    DelegationExpieryAlertJob delegationExpieryAlertJob;

    @Before
    public void setUp() throws Exception {

        context = new ClassPathXmlApplicationContext("Spring-job-test.xml");

        delegationExpieryAlertJob = (DelegationExpieryAlertJob) context.getBean("delegationExpieryAlertJob");
        delegationService = (DelegationService) context.getBean("delegationService");

    }

    @After
    public void tearDown() throws Exception {
        DriverManagerDataSource dataSource = (DriverManagerDataSource) context.getBean("dataSource");

        Connection conn = dataSource.getConnection();

        Statement st = conn.createStatement();
        st.execute("SHUTDOWN");
        conn.close();
    }

    @Test
    public void test() throws AddressException, MessagingException {

        saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));

        delegationExpieryAlertJob.scanRepoAndSendMails();

    }

    private long saveADelegation(Date approvedOndate, Date validFromDate, Date validToDate) {

        DelegationBlock delegationBlock = new DelegationBlock();
        delegationBlock.setApprovedOn(approvedOndate);
        delegationBlock.setSignToken("st");

        Delegation delegation = new Delegation();

        delegation.setDelegatedFor("df");
        delegation.setDelegatedForEmail("test@vgregion.se");
        delegation.setDelegateTo("dt");
        delegation.setRole("role");
        delegation.setValidFrom(validFromDate);
        delegation.setValidTo(validToDate);

        delegationBlock.addDelegation(delegation);
        DelegationBlock delegationBlock2 = delegationService.save(delegationBlock);

        return delegationBlock2.getDelegations().iterator().next().getDelegationKey();
    }

    private Date getADateWithOfset(long ofsetDays) {

        Date today = new Date();
        Date returnDate = new Date(today.getTime() + ofsetDays * DayInMilis);

        return returnDate;

    }

}
