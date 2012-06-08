package se.vgregion.delegation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

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
                (DelegationMailSenderService) context.getBean("delegationMailSenderService");

        VelocityEngine velocityEngine = (VelocityEngine) context.getBean("velocityEngine");

        List<Delegation> soonToExpier =
                delegationRepository.findSoonToExpireWithUnsentWarning(warnBeforeExpieryTimeStart,
                        emailToSendKey);

        List<Delegation> result = new ArrayList<Delegation>();
        for (Delegation delegation : soonToExpier) {

            delegation.setExpiryAlertSentCount(delegation.getExpiryAlertSentCount().intValue() + 1);
            result.add(delegationRepository.merge(delegation));
            delegationRepository.flush();

            String text = generateContent(velocityEngine, delegation);
            String subject = generateSubject(velocityEngine, delegation);

            if (delegation.getValidTo().getTime() < (System.currentTimeMillis() + warnBeforeExpieryTimeEnd)) {
                System.out.println("Will email " + delegation.getDelegatedForEmail() + " for delegation "
                        + delegation.getDelegationKey());
                service.sendMail("no-replay-temp@vgregion.se", "simon.goransson@gmail.com", subject, text);
            }
        }
    }

    /**
     * @param velocityEngine
     * @param delegation
     * @return
     */
    private String generateSubject(VelocityEngine velocityEngine, Delegation delegation) {
        return String.format("Delegering upphör %s.", formateDate(delegation.getValidTo()));
    }

    /**
     * @param validTo
     * @return
     */
    private String formateDate(Date validTo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return simpleDateFormat.format(validTo);

    }

    /**
     * @param velocityEngine
     * @return
     */
    @Transactional
    private String generateContent(VelocityEngine velocityEngine, Delegation delegation) {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("expDate", formateDate(delegation.getValidTo()));
        model.put("type", delegation.getRole());
        model.put("delegatedFor", delegation.getDelegatedFor());
        model.put("delegationTo", delegation.getDelegateTo());
        model.put("delegatedBy", delegation.getDelegationBlock().getDelegatedBy());
        String text =
                VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,
                        "mailtemplates/remainder-mail-template.vm", model);

        return text;
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
