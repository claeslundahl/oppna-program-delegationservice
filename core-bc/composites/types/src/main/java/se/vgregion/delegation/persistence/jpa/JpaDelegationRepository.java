package se.vgregion.delegation.persistence.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.dao.domain.patterns.repository.db.jpa.DefaultJpaRepository;
import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationStatus;
import se.vgregion.delegation.persistence.DelegationRepository;

/**
 * 
 * @author Simon Göransson
 * @author Claes Lundahl
 * 
 */
public class JpaDelegationRepository extends DefaultJpaRepository<Delegation, Long> implements
        DelegationRepository {

    private static final String validDateInterval = "d.validTo >=:currentDate"
            + " and d.validFrom <=:currentDate";

    private static final String delegatedStatusIsActive = "d.status =:delegatedStatus";

    // private long timeBeforeExpiryAlert = 30 * 24 * 60 * 60 * 1000;

    @SuppressWarnings("unchecked")
    @Override
    public List<Delegation> getActiveDelegations(String delegatedFor) {
        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
                        + "WHERE d.delegatedFor = :delegatedFor" + " and " + delegatedStatusIsActive
                        + " and " + validDateInterval;

        Query q = entityManager.createQuery(query);

        q.setParameter("delegatedFor", delegatedFor);
        addCurrentDateConstrintAndStatusIsActive(q);

        return q.getResultList();

    }

    @Override
    public List<Delegation> getInActiveDelegations(String delegatedFor) {
        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
                        + "WHERE d.delegatedFor = :delegatedFor" + " and " + delegatedStatusIsActive
                        + " and not (" + validDateInterval + ")";

        Query q = entityManager.createQuery(query);

        q.setParameter("delegatedFor", delegatedFor);
        addCurrentDateConstrintAndStatusIsActive(q);
        return q.getResultList();
    }

    @Override
    public List<Delegation> getDelegations(String delegatedFor) {
        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
                        + "WHERE d.delegatedFor = :delegatedFor" + " and " + delegatedStatusIsActive;

        Query q = entityManager.createQuery(query);

        q.setParameter("delegatedFor", delegatedFor);
        q.setParameter("delegatedStatus", DelegationStatus.ACTIVE);

        return q.getResultList();
    }

    @Override
    public List<Delegation> getDelegationsForToRole(String delegatedFor, String delegatedTo, String role) {
        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
                        + "WHERE d.delegateTo = :delegateTo" + " and d.delegatedFor = :delegatedFor and "
                        + delegatedStatusIsActive + " and d.role =:role ";

        Query q = entityManager.createQuery(query);

        q.setParameter("delegateTo", delegatedTo);
        q.setParameter("delegatedFor", delegatedFor);
        q.setParameter("delegatedStatus", DelegationStatus.ACTIVE);
        q.setParameter("role", role);

        return q.getResultList();
    }

    @Override
    public Delegation getDelegation(Long delegationId) {
        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d " + "WHERE d.id = :delegationId";

        Query q = entityManager.createQuery(query);

        q.setParameter("delegationId", delegationId);

        return (Delegation) q.getSingleResult();
    }

    @Override
    public boolean hasDelegations(String delegatedFor, String delegatedTo, String role) {

        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
                        + "WHERE d.delegateTo = :delegateTo and d.delegatedFor=:delegatedFor"
                        + " and d.status =:delegatedStatus " + " and d.role =:role and " + validDateInterval
                        + " and " + delegatedStatusIsActive;

        Query q = entityManager.createQuery(query);
        q.setParameter("delegateTo", delegatedTo);
        q.setParameter("role", role);
        q.setParameter("delegatedFor", delegatedFor);

        addCurrentDateConstrintAndStatusIsActive(q);

        return !q.getResultList().isEmpty();
    }

    protected Date getCurrentDate() {
        return new Date();
    }

    private void addCurrentDateConstrintAndStatusIsActive(Query q) {
        q.setParameter("delegatedStatus", DelegationStatus.ACTIVE);
        q.setParameter("currentDate", getCurrentDate());
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.persistence.DelegationRepository#findByDelegationKey(java.lang.Long)
     */
    @Override
    public Delegation findByDelegationKey(Long delegationKey) {

        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
                        + "WHERE d.delegationKey = :delegationKey" + " and d.status =:delegatedStatus ";

        Query q = entityManager.createQuery(query);

        q.setParameter("delegationKey", delegationKey);
        q.setParameter("delegatedStatus", DelegationStatus.ACTIVE);

        return (Delegation) q.getSingleResult();

    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.persistence.DelegationRepository#findByDelegationKey(java.lang.Long)
     */
    @Override
    public List<Delegation> findSoonToExpireWithUnsentWarning(long timeBeforeExpiryAlert) {
        Date today = new Date();

        System.out.println("today-zero: " + today + " + " + timeBeforeExpiryAlert);

        today = (new Date(today.getTime() + timeBeforeExpiryAlert));

        System.out.println("\n\ntoday " + today + "\n");

        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
                        + "WHERE d.validTo < :today and (d.expiryAlertSent is null) and "
                        + delegatedStatusIsActive;

        System.out.println("entityManager " + entityManager.toString());

        Query q = entityManager.createQuery(query);

        q.setParameter("today", today, TemporalType.DATE);

        q.setParameter("delegatedStatus", DelegationStatus.ACTIVE);

        return q.getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean removeDelegation(Long delegationKey) {

        Delegation delegation = findByDelegationKey(delegationKey);

        if (delegation == null) {
            return false;
        }
        delegation.setStatus(DelegationStatus.DELETED);
        merge(delegation);
        flush();

        return true;

    }

}