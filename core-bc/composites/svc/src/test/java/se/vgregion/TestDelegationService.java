/**
 * 
 */
package se.vgregion;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationBlock;
import se.vgregion.delegation.domain.DelegationStatus;

/**
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 * 
 */
public class TestDelegationService {
    static private final Logger logger = LoggerFactory.getLogger(TestDelegationService.class);

    DelegationService delegationService;
    ClassPathXmlApplicationContext context;

    private static final long DayInMilis = 86400000;

    @BeforeClass
    public static void startUpTestInfo() {
        logger.info("Starting test for local services!");
    }

    @Before
    public void setUp() throws Exception {

        String path = "classpath*:Spring-conf-test.xml";
        context = new ClassPathXmlApplicationContext(path);
        delegationService = (DelegationService) context.getBean("delegationService");

    }

    public void tearDown() throws Exception {
        DriverManagerDataSource dataSource = (DriverManagerDataSource) context.getBean("dataSource");

        Connection conn = dataSource.getConnection();

        Statement st = conn.createStatement();
        st.execute("SHUTDOWN");
        conn.close();
    }

    @Test
    public void testSaveDelegationResponderInterface() {

        long responsedelegationKey =
                saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));

        Assert.assertTrue(responsedelegationKey == 1);

    }

    @Test
    public void testGetActiveDelegationsResponderInterface() {
        saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));
        List<Delegation> delegations = delegationService.getActiveDelegations("df");
        Assert.assertTrue(delegations.size() > 0);
    }

    @Test
    public void testGetDelegationResponderInterface() {

        // Creates a delegetion to get.
        long responsedelegationKey =
                saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));

        Delegation delegation = delegationService.getDelegation(responsedelegationKey);

        Assert.assertEquals(delegation.getDelegationKey().longValue(), responsedelegationKey);
    }

    @Test
    public void testGetInactiveDelegationsResponderInterface() {
        saveADelegation(getADateWithOfset(-118), getADateWithOfset(-120), getADateWithOfset(-40));
        List<Delegation> delegations = delegationService.getInActiveDelegations("df");
        Assert.assertTrue(delegations.size() > 0);

    }

    @Test
    public void testGetDelegationsbyUnitAndRoleResponderInterface() {
        saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));
        List<Delegation> delegations = delegationService.getDelegationsForToRole("df", "dt", "role");
        Assert.assertTrue(delegations.size() > 0);

    }

    @Test
    public void testGetDelegationsResponderInterface() {
        saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));
        List<Delegation> delegations = delegationService.getDelegations("df");
        Assert.assertTrue(delegations.size() > 0);

    }

    @Test
    public void testHasDelegationResponderInterface() {
        saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));
        Assert.assertTrue(delegationService.hasDelegations("df", "dt", "role"));
    }

    @Test
    public void testRemoveDelegationResponderInterface() {
        long responsedelegationKey =
                saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));
        Delegation delegation = delegationService.getDelegation(responsedelegationKey);
        Assert.assertTrue(delegationService.removeDelegation(responsedelegationKey));
        Assert.assertTrue(delegation.getStatus().compareTo(DelegationStatus.ACTIVE) == 0);
        Delegation delegation2 = delegationService.getDelegation(responsedelegationKey);
        Assert.assertTrue(delegation2.getStatus().compareTo(DelegationStatus.DELETED) == 0);
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
