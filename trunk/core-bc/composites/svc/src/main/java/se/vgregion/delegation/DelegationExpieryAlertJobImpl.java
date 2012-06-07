package se.vgregion.delegation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.mail.DelegationMailSenderService;
import se.vgregion.delegation.persistence.DelegationRepository;

@Component
public class DelegationExpieryAlertJobImpl implements DelegationExpieryAlertJob {

    private long warnBeforeExpieryTime;
    private long days;
    private final static long oneDayMilis = 24l * 60l * 60l * 1000l;

    @Autowired(required = false)
    DelegationRepository delegationRepository;

    @Override
    @Transactional
    public void scanRepoAndSendMails() {
        ApplicationContext context = new ClassPathXmlApplicationContext("mail-spring.xml");
        DelegationMailSenderService service =
                (DelegationMailSenderService) context.getBean("userRegistrationService");

        System.out.println("Before calling " + warnBeforeExpieryTime);
        List<Delegation> soonToExpier =
                delegationRepository.findSoonToExpireWithUnsentWarning(warnBeforeExpieryTime);

        System.out.println("Size to email: " + soonToExpier.size());

        List<Delegation> result = new ArrayList<Delegation>();
        for (Delegation delegation : soonToExpier) {
            delegation.setExpiryAlertSent(true);
            result.add(delegationRepository.merge(delegation));

            System.out.println("Will email " + delegation.getDelegatedForEmail() + " for delegation "
                    + delegation.getDelegationKey());
            service.sendMail("no-replay@vgregion.se", "simon.goransson@gmail.com",
                    "hej " + delegation.getRole(), delegation.getInformation());

        }
        delegationRepository.flush();

        // return soonToExpier;
    }

    public long getWarnBeforeExpieryTime() {
        return warnBeforeExpieryTime;
    }

    public void setWarnBeforeExpieryTime(long warnBeforeExpieryTime) {
        this.warnBeforeExpieryTime = warnBeforeExpieryTime;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
        warnBeforeExpieryTime = days * oneDayMilis;
    }

    public DelegationRepository getDelegationRepository() {
        return delegationRepository;
    }

    public void setDelegationRepository(DelegationRepository delegationRepository) {
        this.delegationRepository = delegationRepository;
    }

}
