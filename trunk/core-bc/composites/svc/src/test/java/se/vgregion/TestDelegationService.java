/**
 * 
 */
package se.vgregion;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.NotValidChecksumException;
import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationBlock;
import se.vgregion.delegation.domain.DelegationStatus;

/**
 * Test Class for testing the DelegationService.
 * 
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 * 
 */
public class TestDelegationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestDelegationService.class);

    private DelegationService delegationService;
    private ClassPathXmlApplicationContext context;

    private static final long DAYSINMILIS = 86400000;

    /**
     * Gives startup test info logging.
     */
    @BeforeClass
    public static void startUpTestInfo() {
        LOGGER.info("Starting test for local services!");
    }

    /**
     * Sets the up environment before performing the tests.
     * 
     * @throws Exception
     *             the exception
     */
    @Before
    public void setUp() throws Exception {

        String path = "classpath*:Spring-conf-test.xml";
        context = new ClassPathXmlApplicationContext(path);
        delegationService = (DelegationService) context.getBean("delegationService");

    }

    /**
     * Shouting down connection to database.
     * 
     * @throws Exception
     *             the exception
     */
    @After
    public void tearDown() throws Exception {
        DriverManagerDataSource dataSource = (DriverManagerDataSource) context.getBean("dataSource");

        Connection conn = dataSource.getConnection();

        Statement st = conn.createStatement();
        st.execute("SHUTDOWN");
        conn.close();
    }

    /**
     * Test save delegation.
     */
    @Test
    public void testSaveDelegation() {

        long responsedelegationKey =
                saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));

        Assert.assertTrue(responsedelegationKey == 1);

    }

    /**
     * Test update delegation.
     */
    @Test
    public void testUpdateDelegation() {

        long responsedelegationKey =
                saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));

        Delegation delegationResult1 = delegationService.getDelegation(responsedelegationKey);

        DelegationBlock delegationBlock = new DelegationBlock();
        delegationBlock.setApprovedOn(getADateWithOfset(-18));
        delegationBlock.setSignToken("st");

        Delegation delegation = new Delegation();

        delegation.setDelegationKey(responsedelegationKey);
        delegation.setDelegatedFor("newdf");
        delegation.setDelegatedForEmail("test@vgregion.se");
        delegation.setDelegateTo("dt");
        delegation.setRole("role");
        delegation.setValidFrom(getADateWithOfset(-28));
        delegation.setValidTo(getADateWithOfset(20));

        delegationBlock.addDelegation(delegation);

        delegationService.save(delegationBlock);

        Delegation delegationResult2 = delegationService.getDelegation(responsedelegationKey);

        Assert.assertTrue(delegationResult1.getId() != delegationResult2.getId());
        Assert.assertTrue(delegationResult1.getDelegationKey().equals(delegationResult2.getDelegationKey()));

    }

    /**
     * Test save delegation with SignToken set to null (negative test).
     */
    @Test
    public void testSaveDelegationFail() {

        DelegationBlock delegationBlock = new DelegationBlock();
        delegationBlock.setSignToken(null);

        Delegation delegation = new Delegation();
        delegationBlock.addDelegation(delegation);

        try {
            delegationService.save(delegationBlock);
            fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NotValidChecksumException);
        }

    }

    /**
     * Test get active delegations.
     */
    @Test
    public void testGetActiveDelegations() {
        saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));
        List<Delegation> delegations = delegationService.getActiveDelegations("df");
        Assert.assertTrue(delegations.size() > 0);
    }

    /**
     * Test get delegation.
     */
    @Test
    public void testGetDelegation() {

        // Creates a delegetion to get.
        long responsedelegationKey =
                saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));

        Delegation delegation = delegationService.getDelegation(responsedelegationKey);
        Assert.assertEquals(delegation.getDelegationKey().longValue(), responsedelegationKey);
    }

    /**
     * Test get delegation2.
     */
    @Test
    public void testGetDelegation2() {

        // Creates a delegetion to get.
        long responsedelegationKey =
                saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));

        Delegation delegation = delegationService.findByDelegationKey(responsedelegationKey);
        Assert.assertEquals(delegation.getDelegationKey().longValue(), responsedelegationKey);
    }

    /**
     * Test get inactive delegations.
     */
    @Test
    public void testGetInactiveDelegations() {
        saveADelegation(getADateWithOfset(-118), getADateWithOfset(-120), getADateWithOfset(-40));
        List<Delegation> delegations = delegationService.getInActiveDelegations("df");
        Assert.assertTrue(delegations.size() > 0);

    }

    /**
     * Test get delegationsby unit and role.
     */
    @Test
    public void testGetDelegationsbyUnitAndRole() {
        saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));
        List<Delegation> delegations = delegationService.getDelegationsForToRole("df", "dt", "role");
        Assert.assertTrue(delegations.size() > 0);

    }

    /**
     * Test get delegations.
     */
    @Test
    public void testGetDelegations() {
        saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));
        List<Delegation> delegations = delegationService.getDelegations("df");
        Assert.assertTrue(delegations.size() > 0);

    }

    /**
     * Test has delegation.
     */
    @Test
    public void testHasDelegation() {
        saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));
        Assert.assertTrue(delegationService.hasDelegations("df", "dt", "role"));
    }

    /**
     * Test remove delegation.
     */
    @Test
    public void testRemoveDelegation() {
        long responsedelegationKey =
                saveADelegation(getADateWithOfset(-18), getADateWithOfset(-20), getADateWithOfset(20));
        Delegation delegation = delegationService.getDelegation(responsedelegationKey);
        Assert.assertTrue(delegationService.removeDelegation(responsedelegationKey));
        Assert.assertTrue(delegation.getStatus().compareTo(DelegationStatus.ACTIVE) == 0);
        Delegation delegation2 = delegationService.getDelegation(responsedelegationKey);
        Assert.assertTrue(delegation2.getStatus().compareTo(DelegationStatus.DELETED) == 0);
    }

    /**
     * Help method for saving a delegation.
     * 
     * @param approvedOndate
     * @param validFromDate
     * @param validToDate
     * @return
     */
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
        Date returnDate = new Date(today.getTime() + ofsetDays * DAYSINMILIS);

        return returnDate;

    }
}
