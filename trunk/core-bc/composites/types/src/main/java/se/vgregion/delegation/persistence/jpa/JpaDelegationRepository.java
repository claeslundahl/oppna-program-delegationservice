package se.vgregion.delegation.persistence.jpa;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

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

    private static final String activeConstraint = "d.validTo >=:currentDate"
            + " and d.validFrom <=:currentDate";

    private static final String statusIsActiveConstraint = "d.status =:delegatedStatus";

    @SuppressWarnings("unchecked")
    @Override
    public List<Delegation> getActiveDelegations(String delegatedFor) {
        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
                        + "WHERE d.delegatedFor = :delegatedFor" + " and " + statusIsActiveConstraint
                        + " and " + activeConstraint;

        Query q = entityManager.createQuery(query);

        q.setParameter("delegatedFor", delegatedFor);
        addCurrentDateConstrintAndStatusIsActive(q);

        return q.getResultList();

    }

    @Override
    public List<Delegation> getInActiveDelegations(String delegatedFor) {
        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
                        + "WHERE d.delegatedFor = :delegatedFor" + " and " + statusIsActiveConstraint
                        + " and not (" + activeConstraint + ")";

        Query q = entityManager.createQuery(query);

        q.setParameter("delegatedFor", delegatedFor);
        addCurrentDateConstrintAndStatusIsActive(q);
        return q.getResultList();
    }

    @Override
    public List<Delegation> getDelegations(String delegatedFor) {
        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
                        + "WHERE d.delegatedFor = :delegatedFor" + " and " + statusIsActiveConstraint;

        Query q = entityManager.createQuery(query);

        q.setParameter("delegatedFor", delegatedFor);
        q.setParameter("delegatedStatus", DelegationStatus.ACTIVE);

        return q.getResultList();
    }

    @Override
    public List<Delegation> getDelegationsByRole(String delegatedTo, String role) {
        String query =
                "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
                        + "WHERE d.delegateTo = :delegateTo" + " and " + statusIsActiveConstraint
                        + " and d.role =:role and " + activeConstraint;

        Query q = entityManager.createQuery(query);

        q.setParameter("delegateTo", delegatedTo);
        addCurrentDateConstrintAndStatusIsActive(q);
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
                        + " and d.status =:delegatedStatus " + " and d.role =:role and " + activeConstraint
                        + " and " + statusIsActiveConstraint;

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
}
