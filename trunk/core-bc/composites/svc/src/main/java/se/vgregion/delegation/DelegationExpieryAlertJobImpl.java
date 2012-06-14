package se.vgregion.delegation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.mail.DelegationMailSenderService;
import se.vgregion.delegation.persistence.DelegationRepository;

/**
 * The Class DelegationExpieryAlertJobImpl.
 */
@Component
public class DelegationExpieryAlertJobImpl implements DelegationExpieryAlertJob {

    static private final Logger logger = LoggerFactory.getLogger(DelegationExpieryAlertJobImpl.class);

    private long warnBeforeExpieryTimeStart;
    private long warnBeforeExpieryTimeEnd;
    private long daysBeforeStart;
    private long daysBeforeEnd;
    private final static long oneDayMilis = 24L * 60L * 60L * 1000L;
    private int emailToSendKey;

    private String contextPath;

    @Autowired(required = false)
    private DelegationRepository delegationRepository;

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.DelegationExpieryAlertJob#scanRepoAndSendMails()
     */
    @Override
    @Transactional
    public void scanRepoAndSendMails() {

        ApplicationContext context = new ClassPathXmlApplicationContext(contextPath);
        DelegationMailSenderService service =
                (DelegationMailSenderService) context.getBean("delegationMailSenderService");

        VelocityEngine velocityEngine = (VelocityEngine) context.getBean("velocityEngine");

        List<Delegation> soonToExpier =
                delegationRepository.findSoonToExpireWithUnsentWarning(warnBeforeExpieryTimeStart,
                        emailToSendKey);

        logger.debug("size to email " + soonToExpier.size() + " for job with daysBeforeStart "
                + daysBeforeStart);

        List<Delegation> result = new ArrayList<Delegation>();
        for (Delegation delegation : soonToExpier) {

            delegation.setExpiryAlertSentCount(delegation.getExpiryAlertSentCount().intValue() + 1);
            result.add(delegationRepository.merge(delegation));
            delegationRepository.flush();

            String text = generateContent(velocityEngine, delegation);
            String subject = generateSubject(velocityEngine, delegation);

            if (delegation.getValidTo().getTime() > (System.currentTimeMillis() + warnBeforeExpieryTimeEnd)) {

                if (delegation.getDelegatedForEmail() != null && !delegation.getDelegatedForEmail().isEmpty()) {
                    service.sendMail("no-replay@vgregion.se", delegation.getDelegatedForEmail(), subject,
                            text);
                    logger.debug("Will email " + delegation.getDelegatedForEmail() + " for delegation "
                            + delegation.getDelegationKey());
                }
            }
        }
    }

    /**
     * Help method that generates the text to the E-mail subject.
     * 
     * @param velocityEngine
     * @param delegation
     * @return
     */
    private String generateSubject(VelocityEngine velocityEngine, Delegation delegation) {
        return String.format("Delegering upphör %s.", formateDate(delegation.getValidTo()));
    }

    /**
     * Help method for converting a date to a string on the form "yyyy-MM-dd".
     * 
     * @param a
     *            date
     * @return a date as a String
     */
    private String formateDate(Date validTo) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        return simpleDateFormat.format(validTo);

    }

    /**
     * Help method that generates the text to the E-mail content using a velocity template. For an delegation.
     * 
     * @param velocityEngine
     *            and the delegation to create the text for.
     * @return the content text.
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

    public void setEmailToSendKey(int emailToSendKey) {
        this.emailToSendKey = emailToSendKey;
    }

    public int getEmailToSendKey() {
        return emailToSendKey;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public String getContextPath() {
        return contextPath;
    }

}
