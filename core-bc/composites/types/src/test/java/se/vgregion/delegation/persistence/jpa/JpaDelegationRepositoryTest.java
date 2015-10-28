package se.vgregion.delegation.persistence.jpa;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationStatus;
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
	@Ignore // is not in the ordinary test-set. (Test to be run against a 'special' db).
	public void testActiveDelegations() {
		List<Delegation> delegation = delegationRepository.getActiveDelegations("df");
		assertEquals(2, delegation.size());
	}


	@Test
	@Ignore // is not in the ordinary test-set. (Test to be run against a 'special' db).
	public void findBySample_informationField() {
		Delegation match = new Delegation();
		match.setInformation("SE2321000131-E000000000109");
		List<Delegation> delegation = delegationRepository.findBySample(match);
		assertEquals(1, delegation.size());
	}

	@Test
	public void testInActiveDelegations() {
		List<Delegation> delegation = delegationRepository.getInActiveDelegations("df");
		assertEquals(2, delegation.size());
	}

	@Test
	public void getDelegationsForToRole() {
		List<Delegation> delegation = delegationRepository.getDelegationsForToRole("df", "dt", "role");
		assertEquals(2, delegation.size());
	}

	@Test
	public void getDelegationsForToRoleStar() {
		List<Delegation> delegation = delegationRepository.getDelegationsForToRole("*", "*", "*");
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
		assertEquals(false, b);
	}

	@Test
	public void testfindByDelegationKey() {
		Delegation delegation = delegationRepository.findByDelegationKey(-1L);
		assertEquals("df", delegation.getDelegatedFor());
	}

	@Test
	@Transactional
	public void testFindSoonToExpireWithUnsentWarning() {
		delegationRepository.store(mkDelegation());
		delegationRepository.flush();
		List<Delegation> result = delegationRepository.findSoonToExpireWithUnsentWarning(
		        System.currentTimeMillis() + 2000000, 1);
		System.out.println("Result Count = " + result.size());

		System.out.println(result.get(0).getValidFrom());
		Assert.assertNotNull(result);
	}

	Delegation mkDelegation() {
		// 30l * 24l * 60l * 60l * 1000l
		Delegation entity = new Delegation();
		entity.setDelegatedFor("df");
		entity.setDelegatedForEmail("some@epost.com");
		entity.setDelegateTo("dt");
		entity.setDelegationKey(123l);
		entity.setRole("role");
		entity.setStatus(DelegationStatus.ACTIVE);
		entity.setValidFrom(new Date());
		entity.setValidTo(new Date(System.currentTimeMillis() + 1000000));
		return entity;
	}

	@Test
	public void findBySample() {
		Delegation bean = new Delegation();
		bean.setStatus(DelegationStatus.ACTIVE);
		List<Delegation> result = delegationRepository.findBySample(bean);
		System.out.println("result.size(): " + result.size());
		Assert.assertEquals(2, result.size());
	}

}
