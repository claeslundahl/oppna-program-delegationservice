package se.vgregion.delegation.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

/**
 * This class represents a delegation block.
 * 
 * @author Simon Göransson
 * @author Claes Lundahl
 */
@Entity
@Table(name = "vgr_delegation_block")
public class DelegationBlock extends AbstractEntity<Long> implements
        se.vgregion.dao.domain.patterns.entity.Entity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 20000)
    private String signToken;

    private String delegatedBy;

    @OneToMany(mappedBy = "delegationBlock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Delegation> delegations = new HashSet<Delegation>();

    @Column(nullable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvedOn;

    /**
     * Adds an delegatonTo to this delegation.
     * 
     * IMPORTANT: The call to changeAllowed are aligned so that all checks are made before any change.
     * 
     * @param delegation
     *            - the Delegation to be added.
     */
    public void addDelegation(Delegation delegation) {
        DelegationBlock oldDelegation = delegation.getDelegationBlock();
        if (oldDelegation != null && oldDelegation.getDelegations().contains(delegation)) {
            oldDelegation.removeDelegation(delegation);
        }
        delegation.setDelegationBlock(this);
        delegations.add(delegation);
    }

    /**
     * Remove an delegatonTo from this delegation.
     * 
     * IMPORTANT: The call to changeAllowed are aligned so that all checks are made before any change.
     * 
     * @param delegation
     *            - the Delegation to be added.
     */
    public void removeDelegation(Delegation delegation) {
        DelegationBlock oldDelegation = delegation.getDelegationBlock();
        if (oldDelegation != null && oldDelegation != this
                && oldDelegation.getDelegations().contains(delegation)) {
            oldDelegation.removeDelegation(delegation);
        }
        delegation.setDelegationBlock(null);
        delegations.remove(delegation);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Date getApprovedOn() {
        if (approvedOn != null) {
            return (Date) approvedOn.clone();
        } else {
            return null;
        }
    }

    public void setApprovedOn(Date approvedOn) {
        this.approvedOn = (Date) approvedOn.clone();
    }

    public String getDelegatedBy() {
        return delegatedBy;
    }

    public void setDelegatedBy(String delegatedBy) {
        this.delegatedBy = delegatedBy;
    }

    /**
     * Protected access to Delegations handled. Use addDelegation, removeDelegation to change delegations.
     * 
     * @return An unmodifiable representation of delegations.
     */
    public Set<Delegation> getDelegations() {
        // return Collections.unmodifiableSet(delegations);
        return delegations;
    }

    public String getSignToken() {
        return signToken;
    }

    public void setSignToken(String signToken) {
        this.signToken = signToken;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // @SuppressWarnings("unchecked")
    // public static void main(String[] args) {
    // DelegationBlock db = new DelegationBlock();
    // db.setApprovedOn(new Date());
    // db.setDelegatedBy("db");
    // db.setSignToken("st");
    // db.setId(-1L);
    // // Set<Delegation> delegations = db.getDelegations();
    //
    // BeanMap bm = new BeanMap(db);
    // @SuppressWarnings("rawtypes")
    // Set keys = new HashSet();
    // keys.addAll(bm.keySet());
    // keys.remove("class");
    // keys.remove("delegations");
    // StringBuilder def = new StringBuilder("insert into vgr_" + "delegation_block" + " ( ");
    // StringBuilder val = new StringBuilder(" values (");
    // for (Object o : keys) {
    // Object v = bm.get(o);
    // if (v != null) {
    // def.append(o + ", ");
    // val.append("'" + format(bm.get(o)) + "', ");
    // }
    // }
    // def.delete(def.length() - 2, def.length());
    // val.delete(val.length() - 2, val.length());
    //
    // System.out.println(def + ") " + val + ");");
    //
    // }

    // private static String format(Object o) {
    // if (o instanceof Date) {
    // Date d = (Date) o;
    // // 2011-09-01 10:00:00.0
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0");
    // return sdf.format(d);
    // }
    // return o + "";
    // }


    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + id;
    }
}
