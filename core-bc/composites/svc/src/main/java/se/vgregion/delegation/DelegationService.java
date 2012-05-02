package se.vgregion.delegation;

import java.util.List;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationBlock;

/**
 * @author Simon Göransson
 * @author Claes Lundahl
 * 
 */
public interface DelegationService {
    /**
     * Fetch currently active delegations for delegatedFor
     * 
     * @param delegatedFor
     *            - Id
     * @return - the active delegation
     */
    List<Delegation> getActiveDelegations(String delegatedFor);

    /**
     * Fetch currently inactive delegations for delegatedFor
     * 
     * @param delegatedFor
     *            - Id
     * @return - the inactive delegation
     */
    List<Delegation> getInActiveDelegations(String delegatedFor);

    /**
     * Fetch delegations for delegatedFor
     * 
     * @param delegatedFor
     *            - Id
     * @return - the inactive delegation
     */
    List<Delegation> getDelegations(String delegatedFor);

    /**
     * Fetch delegations for delegatedTo and
     * 
     * @param delegatedTo
     *            - Id
     * @param role
     *            - role
     * 
     * @return - the inactive delegation
     */
    List<Delegation> getDelegationsByRole(String delegatedTo, String role);

    /**
     * Fetches a delegation by it's id.
     * 
     * @param delegationId
     *            - the id off the delegation.
     * 
     * @return - the inactive delegation
     */
    Delegation getDelegation(Long delegationId);

    /**
     * Store the delegations and checks if the checksum is not null.
     * 
     * @param delegationBlock
     *            - a delegationBlock
     * 
     * @return - updated post.
     */
    DelegationBlock save(DelegationBlock delegationBlock) throws NotValidChecksumException;

    /**
     * Check if a delegatedTo have a valid delegation for a delegationFor whit a role.
     * 
     * @param delegatedFor
     *            - delegation for
     * @param delegatedTo
     *            - delegation to
     * @param role
     *            - the role
     * @return
     */
    boolean hasDelegations(String delegatedFor, String delegatedTo, String role);
}
