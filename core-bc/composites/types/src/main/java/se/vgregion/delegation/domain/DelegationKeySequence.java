package se.vgregion.delegation.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import se.vgregion.dao.domain.patterns.entity.AbstractEntity;

/**
 * 'Dummy' entity whose sole purpose is to get a sequence for delegationKey in {@link Delegation}.
 * 
 * @author clalu4
 * 
 */

@Entity
@Table(name = "vgr_delegation_key_sequence")
public class DelegationKeySequence extends AbstractEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}