package se.vgregion.delegation;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationStatus;
import se.vgregion.delegation.persistence.DelegationRepository;

@ContextConfiguration({"classpath:jpa-delegation-service-configuration.xml",
    "classpath:JpaRepositoryTest-context.xml", "classpath:test-delegation-service-configuration.xml"})
public class DelegationExpieryAlertJobImplTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@Autowired
	DelegationExpieryAlertJob delegationExpieryAlertJob;
	
    @Autowired(required = false)
    private DelegationRepository delegationRepository;
	
	@Test
	public void testStart() throws InterruptedException {
		System.out.println("Start of test.");
		delegationExpieryAlertJob.start();
		wait(10000);
		System.out.println("End of test.");
	}

	@Test
	public void scanRepoAndSendMails(){
		DelegationExpieryAlertJobImpl d = (DelegationExpieryAlertJobImpl) delegationExpieryAlertJob;
		Delegation entity = mkDelegation();
		d.delegationRepository.store(entity);		
		entity = mkDelegation();
		entity.setExpiryAlertSent(true);
		d.delegationRepository.store(entity);		
		entity = mkDelegation();
		entity.setValidTo(new Date(System.currentTimeMillis()+30l * 24l * 60l * 60l * 1000l * 2l));
		d.delegationRepository.store(entity);
		d.delegationRepository.flush();
		
		List<Delegation> result = delegationExpieryAlertJob.scanRepoAndSendMails();
		assertEquals(1, result.size());
		assertNotNull(result.get(0).getExpiryAlertSent());
		assertTrue(result.get(0).getExpiryAlertSent());
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
		entity.setValidTo(new Date(System.currentTimeMillis() +1000000));
		return entity;
	}
	
	
	
}
