package se.vgregion.delegation.persistence.jpa;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.persistence.DelegationRepository;

/**
 * 
 * @author Simon Göransson
 * @author Claes Lundahl
 * 
 */
@ContextConfiguration("classpath:JpaRepositoryTest-context.xml")
public class JpaDelegationRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private DelegationRepository delegationRepository;

    @Before
    public void setUp() throws Exception {
        executeSqlScript("classpath:dbsetup/test-data-delegation.sql", false);
    }

    @Test
    public void testFindAll() {
        Collection<Delegation> delegationTos = delegationRepository.findAll();
        assertEquals(3, delegationTos.size());

        for (Delegation delegationTo : delegationTos) {
            System.out.println(delegationTo);
        }
    }

    @Test
    public void testActiveDelegations() {
        List<Delegation> delegation = delegationRepository.getActiveDelegations("df");
        assertEquals(1, delegation.size());
    }

    @Test
    public void testInActiveDelegations() {
        List<Delegation> delegation = delegationRepository.getInActiveDelegations("df");
        assertEquals(1, delegation.size());
    }

    @Test
    public void getDelegationsForToRole() {
        List<Delegation> delegation = delegationRepository.getDelegationsForToRole("df", "dt", "role");
        assertEquals(2, delegation.size());
    }

    @Test
    public void testDelegation() {
        Delegation delegation = delegationRepository.getDelegation(-1L);
        assertEquals("df", delegation.getDelegatedFor());
    }

    @Test
    public void testhasDelegations() {
        boolean b = delegationRepository.hasDelegations("df", "dt", "role");
        assertEquals(true, b);
    }

    @Test
    public void testFindSoonToExpireWithUnsentWarning() {
        List<Delegation> result = delegationRepository.findSoonToExpireWithUnsentWarning(100l, 1);
        System.out.println("Result Count = " + result.size());
        System.out.println(result.get(0).getValidFrom());
        Assert.assertNotNull(result);
    }

}
