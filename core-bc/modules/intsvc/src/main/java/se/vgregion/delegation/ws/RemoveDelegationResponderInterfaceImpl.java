/**
 * 
 */
package se.vgregion.delegation.ws;

import javax.persistence.NoResultException;

import se.riv.authorization.delegation.removedelegation.v1.rivtabp21.RemoveDelegationResponderInterface;
import se.riv.authorization.delegation.removedelegationresponder.v1.RemoveDelegationResponseType;
import se.riv.authorization.delegation.removedelegationresponder.v1.RemoveDelegationType;
import se.riv.authorization.delegation.v1.ResultCodeEnum;
import se.vgregion.delegation.DelegationService;

/**
 * @author Simon Göransson
 * @author Claes Lundahl
 * Implementation of service that permits the removal of a delegation.
 */
public class RemoveDelegationResponderInterfaceImpl implements RemoveDelegationResponderInterface {

    DelegationService delegationService;

    /**
     * Constructor that allows a service instance to be passed into this. 
     * @param delegationService instance used to access underlying services.
     */
    public RemoveDelegationResponderInterfaceImpl(DelegationService delegationService) {
        super();
        this.delegationService = delegationService;

    }

    /**
     * Removes a delegation - or specifically remove it from the possibility of being retrieved later by other services (actually the data is not removed).
     * @param logicalAddress is not used by this implementation.
     * @param parameters contains the delegation key used to find and then 'remove' the delegation in question.
     */ 
    @Override
    public RemoveDelegationResponseType removeDelegation(String logicalAddress,
            RemoveDelegationType parameters) {

        RemoveDelegationResponseType removeDelegationResponseType = new RemoveDelegationResponseType();

        try {
            boolean result =
                    delegationService.removeDelegation(Long.parseLong(parameters.getDelegationKey()));
            if (result) {
                removeDelegationResponseType.setResultCode(ResultCodeEnum.OK);
            } else {
                removeDelegationResponseType.setResultCode(ResultCodeEnum.ERROR);
            }
        } catch (NoResultException e) {
            removeDelegationResponseType.setResultCode(ResultCodeEnum.ERROR);
            removeDelegationResponseType.setComment("No delegation found.");
        }

        return removeDelegationResponseType;
    }

}
