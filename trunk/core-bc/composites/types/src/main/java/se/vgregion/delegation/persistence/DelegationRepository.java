package se.vgregion.delegation.persistence;

import java.util.List;

import se.vgregion.dao.domain.patterns.repository.Repository;
import se.vgregion.delegation.domain.Delegation;

/**
 * 
 * @author Simon Göransson
 * @author Claes Lundahl
 */
public interface DelegationRepository extends Repository<Delegation, Long> {

    List<Delegation> getActiveDelegations(String delegatedFor);

    List<Delegation> getInActiveDelegations(String delegatedFor);

    List<Delegation> getDelegations(String delegatedFor);

    List<Delegation> getDelegationsForToRole(String delegatedFor, String delegatedTo, String role);

    Delegation getDelegation(Long delegationId);

    boolean hasDelegations(String delegatedFor, String delegatedTo, String role);

    Delegation findByDelegationKey(Long id);

}
