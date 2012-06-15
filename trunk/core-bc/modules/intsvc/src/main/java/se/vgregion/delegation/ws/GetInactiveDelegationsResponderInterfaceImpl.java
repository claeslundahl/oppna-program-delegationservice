/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package se.vgregion.delegation.ws;

import javax.jws.WebService;

import se.riv.authorization.delegation.getinactivedelegations.v1.rivtabp21.GetInactiveDelegationsResponderInterface;
import se.riv.authorization.delegation.getinactivedelegationsresponder.v1.GetInactiveDelegationsResponseType;
import se.riv.authorization.delegation.getinactivedelegationsresponder.v1.GetInactiveDelegationsType;
import se.riv.authorization.delegation.v1.ResultCodeEnum;
import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.ws.util.DelegationServiceUtil;

/**
 * @author Simon Göransson
 * @author Claes Lundahl
 * Implementation for getting inactive delegations from the server.
 */

@WebService(
        serviceName = "GetInactiveDelegationsResponderService",
        endpointInterface = "se.riv.authorization.delegation.getinactivedelegations.v1.rivtabp21.GetInactiveDelegationsResponderInterface",
        portName = "GetInactiveDelegationsResponderPort",
        targetNamespace = "urn:riv:authorization:delegation:GetInactiveDelegations:1:rivtabp21",
        wsdlLocation = "schemas/interactions/GetInactiveDelegationsInteraction/GetInactiveDelegationsInteraction_1.0_RIVTABP21.wsdl")
public class GetInactiveDelegationsResponderInterfaceImpl implements GetInactiveDelegationsResponderInterface {

    DelegationService delegationService;

    /**
     * Default constructor.
     */
    public GetInactiveDelegationsResponderInterfaceImpl() {
        super();
    }

    /**
     * Constructor with possibility to provide a service instance. 
     * @param delegationService instance that provides access to underlying services used to support the operations of this type.
     */
    public GetInactiveDelegationsResponderInterfaceImpl(DelegationService delegationService) {
        super();
        this.delegationService = delegationService;
    }

    /**
     * Uses 'delegation for' to search the db and return matching results among rows that have become inactive.
     * @param logicalAddress is not used so far.
     * @param parameters contains the 'delegation for' value to be used in the search.
     */
    @Override
    public GetInactiveDelegationsResponseType getInactiveDelegations(String logicalAddress,
            GetInactiveDelegationsType parameters) {

        GetInactiveDelegationsResponseType result = new GetInactiveDelegationsResponseType();

        result.setDelegations(DelegationServiceUtil.parseDelegations(delegationService
                .getInActiveDelegations(parameters.getDelegationFor())));

        if (result.getDelegations().getContent().size() > 0) {
            result.setResultCode(ResultCodeEnum.OK);
        } else {
            result.setResultCode(ResultCodeEnum.INFO);
            result.setComment("No delegations found for this delegationFor: " + parameters.getDelegationFor());
        }

        return result;
    }

}
