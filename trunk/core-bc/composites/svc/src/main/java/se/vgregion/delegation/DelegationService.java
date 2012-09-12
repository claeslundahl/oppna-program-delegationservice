package se.vgregion.delegation;

import java.util.List;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationBlock;

/**
 * The DelegationService inteface.
 * 
 * @author Simon Göransson
 * @author Claes Lundahl
 * 
 */
public interface DelegationService {
    /**
     * Fetch currently active delegations for delegatedFor.
     * 
     * @param delegatedFor
     *            - Id
     * @return - the active delegation
     */
    List<Delegation> getActiveDelegations(String delegatedFor);

    /**
     * Fetch currently inactive delegations for delegatedFor.
     * 
     * @param delegatedFor
     *            - Id
     * @return - the inactive delegation
     */
    List<Delegation> getInActiveDelegations(String delegatedFor);

    /**
     * Fetch delegations for delegatedFor.
     * 
     * @param delegatedFor
     *            - Id
     * @return - the inactive delegation
     */
    List<Delegation> getDelegations(String delegatedFor);

    /**
     * Finds active delegations that matches the criteria in provided parameters.
     * 
     * @param delegatedFor
     * @param delegatedTo
     * @param role
     * @return
     */
    List<Delegation> getDelegationsForToRole(String delegatedFor, String delegatedTo, String role);

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
    DelegationBlock save(DelegationBlock delegationBlock);

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

    Delegation findByDelegationKey(Long delegationKey);


    List<Delegation> findBySample(Delegation sample);

    /**
     * Removes a delegation by it´s key.
     * 
     * @param delegationKey
     * @return
     */
    boolean removeDelegation(Long delegationKey);
}
