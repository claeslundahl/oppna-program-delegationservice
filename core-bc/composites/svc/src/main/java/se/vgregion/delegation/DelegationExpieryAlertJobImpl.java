package se.vgregion.delegation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.persistence.DelegationRepository;

@Component
public class DelegationExpieryAlertJobImpl implements DelegationExpieryAlertJob {

	private Timer timer = new Timer();
	
	private long warnBeforeExpieryTime = 30l * 24l * 60l * 60l * 1000l;
	
    @Autowired(required = false)
    DelegationRepository delegationRepository;

    
    @Override
    public List<Delegation> scanRepoAndSendMails() {
    	System.out.println("Before calling " + warnBeforeExpieryTime);
		List<Delegation> soonToExpier = delegationRepository.findSoonToExpireWithUnsentWarning(warnBeforeExpieryTime);
		System.out.println("Soon: " + soonToExpier);
		
		List<Delegation> result = new ArrayList<Delegation>();
		for (Delegation delegation: soonToExpier) {
			delegation.setExpiryAlertSent(true);
			result.add(delegationRepository.merge(delegation));
		}
		delegationRepository.flush();
		
    	return soonToExpier;
    }
    
    @Override
	public void start() {
		timer.schedule(new TimerTask() {
			public void run() {
				scanRepoAndSendMails();
			}

		}, 5 * 1000, 5000);
	}

	public static void main(String[] args) {
		DelegationExpieryAlertJobImpl eggTimer = new DelegationExpieryAlertJobImpl();
		eggTimer.start();
	}

	public long getWarnBeforeExpieryTime() {
		return warnBeforeExpieryTime;
	}

	public void setWarnBeforeExpieryTime(long warnBeforeExpieryTime) {
		this.warnBeforeExpieryTime = warnBeforeExpieryTime;
	}

}
