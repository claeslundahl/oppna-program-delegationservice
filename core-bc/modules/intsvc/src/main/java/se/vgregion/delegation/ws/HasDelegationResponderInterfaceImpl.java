/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package se.vgregion.delegation.ws;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.authorization.delegation.hasdelegation.v1.rivtabp21.HasDelegationResponderInterface;
import se.riv.authorization.delegation.hasdelegationresponder.v1.HasDelegationResponseType;
import se.riv.authorization.delegation.hasdelegationresponder.v1.HasDelegationType;
import se.riv.authorization.delegation.v1.ResultCodeEnum;
import se.vgregion.delegation.DelegationService;

/**
 * This class was generated by Apache CXF 2.2.2 Wed Apr 18 14:42:43 CEST 2012 Generated source version: 2.2.2
 * 
 */

@WebService(
        serviceName = "HasDelegationResponderService",
        endpointInterface = "se.riv.authorization.delegation.hasdelegation.v1.rivtabp21.HasDelegationResponderInterface",
        portName = "HasDelegationResponderPort",
        targetNamespace = "urn:riv:authorization:delegation:HasDelegation:1:rivtabp21",
        wsdlLocation = "schemas/interactions/HasDelegationInteraction/HasDelegationInteraction_1.0_RIVTABP21.wsdl")
public class HasDelegationResponderInterfaceImpl implements HasDelegationResponderInterface {

    DelegationService delegationService;

    public HasDelegationResponderInterfaceImpl() {
        super();
    }

    public HasDelegationResponderInterfaceImpl(DelegationService delegationService) {
        super();
        this.delegationService = delegationService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HasDelegationResponderInterfaceImpl.class);

    @Override
    public HasDelegationResponseType hasDelegation(String logicalAddress, HasDelegationType parameters) {

        HasDelegationResponseType hasDelegationResponseType = new HasDelegationResponseType();

        try {
            boolean result =
                    delegationService.hasDelegations(parameters.getDelegatedFor(),
                            parameters.getDelegatedTo(), parameters.getRole());

            hasDelegationResponseType.setResult(result);
            hasDelegationResponseType.setResultCode(ResultCodeEnum.OK);
        } catch (Exception e) {
            hasDelegationResponseType.setResultCode(ResultCodeEnum.ERROR);
            hasDelegationResponseType.setComment(e.getMessage());
            LOGGER.error("Error: " + e.getStackTrace());
        }

        return hasDelegationResponseType;
    }

}
