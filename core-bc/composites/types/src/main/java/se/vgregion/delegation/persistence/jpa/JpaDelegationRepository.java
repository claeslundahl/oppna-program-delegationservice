package se.vgregion.delegation.persistence.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.commons.collections.BeanMap;
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

	private static final String validDateInterval = "d.validTo >=:currentDate" + " and d.validFrom <=:currentDate";

	private static final String delegatedStatusIsActive = "d.status =:delegatedStatus";

	// private long timeBeforeExpiryAlert = 30 * 24 * 60 * 60 * 1000;

	@SuppressWarnings("unchecked")
	@Override
	public List<Delegation> getActiveDelegations(String delegatedFor) {
		String query = "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
		        + "WHERE d.delegatedFor = :delegatedFor" + " and " + delegatedStatusIsActive + " and "
		        + validDateInterval;

		Query q = entityManager.createQuery(query);

		q.setParameter("delegatedFor", delegatedFor);
		addCurrentDateConstrintAndStatusIsActive(q);

		return q.getResultList();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Delegation> getInActiveDelegations(String delegatedFor) {
		String query = "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
		        + "WHERE d.delegatedFor = :delegatedFor" + " and " + delegatedStatusIsActive + " and not ("
		        + validDateInterval + ")";

		Query q = entityManager.createQuery(query);

		q.setParameter("delegatedFor", delegatedFor);
		addCurrentDateConstrintAndStatusIsActive(q);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Delegation> getDelegations(String delegatedFor) {
		String query = "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
		        + "WHERE d.delegatedFor = :delegatedFor" + " and " + delegatedStatusIsActive;

		Query q = entityManager.createQuery(query);

		q.setParameter("delegatedFor", delegatedFor);
		q.setParameter("delegatedStatus", DelegationStatus.ACTIVE);

		return q.getResultList();
	}

	@Override
	public List<Delegation> findAllActive() {
		String query = "SELECT d FROM " + Delegation.class.getSimpleName() + " d  WHERE "
		        + delegatedStatusIsActive;

		Query q = entityManager.createQuery(query);

		q.setParameter("delegatedStatus", DelegationStatus.ACTIVE);

		return q.getResultList();
	}

	private boolean isWildCard(String mayBeStar) {
		return mayBeStar != null && "*".equals(mayBeStar.trim());
	}

	private String blankIfWildCard(String value, String condition) {
		if (isWildCard(value)) {
			return "";
		} else {
			return condition;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Delegation> getDelegationsForToRole(String delegatedFor, String delegatedTo, String role) {
		String query = "SELECT d FROM " + Delegation.class.getSimpleName() + " d " + "WHERE "
		        + blankIfWildCard(delegatedTo, " d.delegateTo = :delegateTo and ")
		        + blankIfWildCard(delegatedFor, " d.delegatedFor = :delegatedFor and ")
		        + blankIfWildCard(role, " d.role =:role and ") + delegatedStatusIsActive;

		Query q = entityManager.createQuery(query);

		if (!isWildCard(delegatedTo)) {
			q.setParameter("delegateTo", delegatedTo);
		}

		if (!isWildCard(delegatedFor)) {
			q.setParameter("delegatedFor", delegatedFor);
		}

		q.setParameter("delegatedStatus", DelegationStatus.ACTIVE);

		if (!isWildCard(role)) {
			q.setParameter("role", role);
		}

		return q.getResultList();
	}

	@Override
	public boolean hasDelegations(String delegatedFor, String delegatedTo, String role) {

		String query = "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
		        + "WHERE d.delegateTo = :delegateTo and d.delegatedFor=:delegatedFor"
		        + " and d.status =:delegatedStatus " + " and d.role =:role and " + validDateInterval + " and "
		        + delegatedStatusIsActive;

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

	@Override
	public Delegation findByDelegationKey(Long delegationKey) {

		String query = "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
		        + "WHERE d.delegationKey = :delegationKey" + " and d.status =:delegatedStatus ";

		Query q = entityManager.createQuery(query);

		q.setParameter("delegationKey", delegationKey);
		q.setParameter("delegatedStatus", DelegationStatus.ACTIVE);

		return (Delegation) q.getSingleResult();

	}

	@Override
	public Delegation getDelegation(Long delegationId) {
		String query = "SELECT d FROM " + Delegation.class.getSimpleName() + " d " + "WHERE d.id = :delegationId";

		Query q = entityManager.createQuery(query);

		q.setParameter("delegationId", delegationId);

		return (Delegation) q.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Delegation> findSoonToExpireWithUnsentWarning(long timeBeforeExpiryAlert, int emailToSendKey) {

		Date start = (new Date(System.currentTimeMillis() + timeBeforeExpiryAlert));

		String query = "SELECT d FROM " + Delegation.class.getSimpleName() + " d "
		        + "WHERE d.validTo < :start and " + "d.expiryAlertSentCount = :expiryAlertSentCount and "
		        + delegatedStatusIsActive;

		Query q = entityManager.createQuery(query);

		q.setParameter("start", start, TemporalType.DATE);

		q.setParameter("delegatedStatus", DelegationStatus.ACTIVE);

		q.setParameter("expiryAlertSentCount", emailToSendKey);

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

	@SuppressWarnings("unchecked")
	@Override
	public List<Delegation> findBySample(Delegation bean) {
		StringBuilder jpqlOut = new StringBuilder();
		List<Object> valuesToSetIntoJpql = new ArrayList<Object>();
		List<String> keysOut = new ArrayList<String>();
		try {
			mkJpqlAndGetValues(bean, jpqlOut, keysOut, valuesToSetIntoJpql, "expiryAlertSentCount");
			Query q = entityManager.createQuery(jpqlOut.toString());
			int position = 0;
			for (Object value : valuesToSetIntoJpql) {
				q.setParameter(keysOut.get(position++), value);
			}
			return q.getResultList();
		} catch (Exception e) {
			System.out.println("Jpql: " + jpqlOut);
			System.out.println("Values: " + valuesToSetIntoJpql);
			System.out.println("Keys: " + keysOut);
			throw new RuntimeException(e);
		}
	}

	void mkJpqlAndGetValues(final Object bean, final StringBuilder jpqlOut, final List<String> keysOut,
	        final List<Object> valuesToSetIntoJpql, String... excludedProperties) {
		BeanMap bm = new BeanMap(bean);
		Set<String> keys = new HashSet<String>(bm.keySet());
		keys.remove("class");
		keys.removeAll(Arrays.asList(excludedProperties));

		List<String> condition = new ArrayList<String>();

		jpqlOut.append("select d from " + bean.getClass().getSimpleName() + " d");

		for (String key : keys) {
			Object value = bm.get(key);
			if (value != null && key != null && !key.trim().equals("")) {
				keysOut.add(key);
				StringBuilder comparison = new StringBuilder("d.");
				comparison.append(key);
				if (value instanceof String && value.toString().contains("*")) {
					comparison.append(" like :" + key + " ");
					value = value.toString().replaceAll(Pattern.quote("*"), "%");
				} else {
					comparison.append(" = :" + key + " ");
				}
				condition.add(comparison.toString());
				valuesToSetIntoJpql.add(value);
			}
		}

		if (!valuesToSetIntoJpql.isEmpty()) {
			jpqlOut.append(" where ");
			jpqlOut.append(join(condition, " and "));
		}

		System.out.println(jpqlOut);

	}

	private String join(List<String> items, String junction) {
		if (items.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0, j = items.size() - 1; i < j; i++) {
			sb.append(items.get(i));
			sb.append(junction);
		}
		sb.append(items.get(items.size() - 1));
		return sb.toString();
	}

}
