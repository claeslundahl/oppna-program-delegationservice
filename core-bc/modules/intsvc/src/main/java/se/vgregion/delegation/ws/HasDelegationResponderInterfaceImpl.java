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
 * Implementation for checking whether or not a person have a valid delegation for a certain role and unit.
 * @author Simon Göransson
 * @author Claes Lundahl
 */

@WebService(
        serviceName = "HasDelegationResponderService",
        endpointInterface = "se.riv.authorization.delegation.hasdelegation.v1.rivtabp21.HasDelegationResponderInterface",
        portName = "HasDelegationResponderPort",
        targetNamespace = "urn:riv:authorization:delegation:HasDelegation:1:rivtabp21",
        wsdlLocation = "schemas/interactions/HasDelegationInteraction/HasDelegationInteraction_1.0_RIVTABP21.wsdl")
public class HasDelegationResponderInterfaceImpl implements HasDelegationResponderInterface {

    private DelegationService delegationService;

    /**
     * Default constructor.
     */
    public HasDelegationResponderInterfaceImpl() {
        super();
    }

    /**
     * Construtor with possibilty to pass in an service instance, giving this implementation access to underlying services.
     * @param delegationService the service instance that can be used to fulfill the operations supported by this class.
     */
    public HasDelegationResponderInterfaceImpl(DelegationService delegationService) {
        super();
        this.delegationService = delegationService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(HasDelegationResponderInterfaceImpl.class);

    /**
     * Checks if a 'delegated to', person or otherwise, 'delegated for' and role have a valid delegation at this (current) point in time.
     * @param logicalAddress is not used at the moment.
     * @param parameters holds the actual values for 'delegated-to' and '-for' and 'role'.
     * @return a wrapper object with an boolean result and the result code bundled within. 
     */
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
            LOGGER.error("Error: ", e);
        }

        return hasDelegationResponseType;
    }

}
