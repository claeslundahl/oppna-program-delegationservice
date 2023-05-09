package se.vgregion.delegation.delegation.view.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 'Dummy' entity whose sole purpose is to get a sequence for delegationKey in {@link Delegation}.
 *
 * @author clalu4
 */

@Entity
@Table(name = "vgr_delegation_key_sequence")
public class DelegationKeySequence {

    @Id
    @GeneratedValue
    private Long id;

}