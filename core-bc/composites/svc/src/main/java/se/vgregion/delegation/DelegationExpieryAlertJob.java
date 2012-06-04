package se.vgregion.delegation;

import java.util.List;

import se.vgregion.delegation.domain.Delegation;

public interface DelegationExpieryAlertJob {

	void start();
 
	List<Delegation> scanRepoAndSendMails();

}
