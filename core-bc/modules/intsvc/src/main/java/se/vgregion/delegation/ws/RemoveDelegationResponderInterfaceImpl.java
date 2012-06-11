/**
 * 
 */
package se.vgregion.delegation.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.authorization.delegation.removedelegation.v1.rivtabp21.RemoveDelegationResponderInterface;
import se.riv.authorization.delegation.removedelegationresponder.v1.RemoveDelegationResponseType;
import se.riv.authorization.delegation.removedelegationresponder.v1.RemoveDelegationType;
import se.riv.authorization.delegation.v1.ResultCodeEnum;
import se.vgregion.delegation.DelegationService;

/**
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 * 
 */
public class RemoveDelegationResponderInterfaceImpl implements RemoveDelegationResponderInterface {
    static private final Logger logger = LoggerFactory
            .getLogger(RemoveDelegationResponderInterfaceImpl.class);

    DelegationService delegationService;

    public RemoveDelegationResponderInterfaceImpl(DelegationService delegationService) {
        super();
        this.delegationService = delegationService;

    }

    @Override
    public RemoveDelegationResponseType removeDelegation(String logicalAddress,
            RemoveDelegationType parameters) {

        RemoveDelegationResponseType removeDelegationResponseType = new RemoveDelegationResponseType();

        boolean result = delegationService.removeDelegation(Long.parseLong(parameters.getDelegationKey()));

        if (result) {
            removeDelegationResponseType.setResultCode(ResultCodeEnum.OK);
        } else {
            removeDelegationResponseType.setResultCode(ResultCodeEnum.ERROR);
        }

        return removeDelegationResponseType;
    }

}
