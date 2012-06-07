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

    private long warnBeforeExpieryTimeStart;
    private long warnBeforeExpieryTimeEnd;
    private long daysBeforeStart;
    private long daysBeforeEnd;
    private final static long oneDayMilis = 24l * 60l * 60l * 1000l;
    private int emailToSendKey;

    @Autowired(required = false)
    DelegationRepository delegationRepository;

    @Override
    @Transactional
    public void scanRepoAndSendMails() {
        ApplicationContext context = new ClassPathXmlApplicationContext("mail-spring.xml");
        DelegationMailSenderService service =
                (DelegationMailSenderService) context.getBean("userRegistrationService");

        // System.out.println("Before calling " + warnBeforeExpieryTime);
        List<Delegation> soonToExpier =
                delegationRepository.findSoonToExpireWithUnsentWarning(warnBeforeExpieryTimeStart,
                        emailToSendKey);

        System.out.println("Size to email: " + soonToExpier.size());

        List<Delegation> result = new ArrayList<Delegation>();
        for (Delegation delegation : soonToExpier) {
            delegation.setExpiryAlertSentCount(delegation.getExpiryAlertSentCount().intValue() + 1);
            result.add(delegationRepository.merge(delegation));

            if (delegation.getValidTo().getTime() < System.currentTimeMillis() + warnBeforeExpieryTimeEnd) {
                System.out.println("Will email " + delegation.getDelegatedForEmail() + " for delegation "
                        + delegation.getDelegationKey());
                service.sendMail("no-replay-temp@vgregion.se", "simon.goransson@gmail.com", "Hej "
                        + delegation.getDelegatedFor() + " , delegering upphör.",
                        "Delegering " + delegation.getDelegationKey() + " upphör " + delegation.getValidTo());
            }

        }
        delegationRepository.flush();

        // return soonToExpier;
    }

    public long getWarnBeforeExpieryTime() {
        return warnBeforeExpieryTimeStart;
    }

    public void setWarnBeforeExpieryTime(long warnBeforeExpieryTime) {
        this.warnBeforeExpieryTimeStart = warnBeforeExpieryTime;
    }

    public DelegationRepository getDelegationRepository() {
        return delegationRepository;
    }

    public void setDelegationRepository(DelegationRepository delegationRepository) {
        this.delegationRepository = delegationRepository;
    }

    public long getDaysBeforeStart() {
        return daysBeforeStart;
    }

    public void setDaysBeforeStart(long daysBeforeStart) {
        warnBeforeExpieryTimeStart = daysBeforeStart * oneDayMilis;
        this.daysBeforeStart = daysBeforeStart;
    }

    public long getDaysBeforeEnd() {
        return daysBeforeEnd;
    }

    public void setDaysBeforeEnd(long daysBeforeEnd) {
        warnBeforeExpieryTimeEnd = daysBeforeEnd * oneDayMilis;
        this.daysBeforeEnd = daysBeforeEnd;
    }

    /**
     * @param emailToSendKey
     *            the emailToSendKey to set
     */
    public void setEmailToSendKey(int emailToSendKey) {
        this.emailToSendKey = emailToSendKey;
    }

    /**
     * @return the emailToSendKey
     */
    public int getEmailToSendKey() {
        return emailToSendKey;
    }

}
