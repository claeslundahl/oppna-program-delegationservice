package se.vgregion.delegation;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationBlock;
import se.vgregion.delegation.domain.DelegationStatus;
import se.vgregion.delegation.persistence.DelegationKeySequenceRepository;
import se.vgregion.delegation.persistence.DelegationRepository;

@ContextConfiguration({ "classpath:jpa-delegation-service-configuration.xml",
        "classpath:JpaRepositoryTest-context.xml", "classpath:test-delegation-service-configuration.xml" })
public class DelegationServiceImplIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired(required = false)
    private DelegationKeySequenceRepository delegationKeySequenceRepository;

    @Autowired
    private DelegationService delegationService;

    @Autowired
    private DelegationRepository delegationRepository;

    @Before
    public void setUp() throws Exception {
        executeSqlScript("classpath:dbsetup/test-data-delegation.sql", false);
    }

    @Test
    // @Ignore
    public void save() {
        DelegationBlock db = new DelegationBlock();
        db.setApprovedOn(new Date());
        Delegation delegation = new Delegation();
        delegation.setDelegatedFor("delegationFor");
        delegation.setDelegateTo("delegateTo");
        delegation.setRole("role");
        delegation.setValidFrom(new Date());
        delegation.setStatus(DelegationStatus.ACTIVE);
        db.setSignToken("foo bar baz");

        db.addDelegation(delegation);

        DelegationBlock result = delegationService.save(db);
        Set<Delegation> delegations = result.getDelegations();
        Delegation fromDb = delegations.iterator().next();

        Assert.assertNotNull(fromDb.getDelegationKey());
        Assert.assertTrue(delegationKeySequenceRepository.findAll().isEmpty());

        // Try an update.
        db = new DelegationBlock();
        delegation.setDelegationKey(fromDb.getDelegationKey());
        delegation.setDelegatedFor("delegationFor2");
        db.setSignToken("bla bla");

        db.addDelegation(delegation);
        DelegationBlock db2 = delegationService.save(db);

        fromDb = delegationRepository.getDelegation(fromDb.getId());

        List<Delegation> result2 = delegationService.getDelegations("delegationFor2");
    }

    @Test
    @Ignore
    public void saveWithNotValidChecksumException() {
        try {
            delegationService.save(new DelegationBlock());
            Assert.fail();
        } catch (NotValidChecksumException e) {
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail();
        }
    }

}
