/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package se.vgregion.delegation.ws;

import javax.jws.WebService;

import se.riv.authorization.delegation.getdelegations.v1.rivtabp21.GetDelegationsResponderInterface;
import se.riv.authorization.delegation.getdelegationsbyunitandrole.v1.rivtabp21.GetDelegationsbyUnitAndRoleResponderInterface;
import se.riv.authorization.delegation.getdelegationsresponder.v1.GetDelegationsResponseType;
import se.riv.authorization.delegation.getdelegationsresponder.v1.GetDelegationsType;
import se.riv.authorization.delegation.v1.ResultCodeEnum;
import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.ws.util.DelegationServiceUtil;


/**
 * @author Simon Göransson
 * @author Claes Lundahl
 * Service implementation for retreiving/searching for a collection of delegations.
 */
@WebService(
        serviceName = "GetDelegationsResponderService",
        endpointInterface = "se.riv.authorization.delegation.getdelegations.v1.rivtabp21.GetDelegationsResponderInterface",
        portName = "GetDelegationsResponderPort",
        targetNamespace = "urn:riv:authorization:delegation:GetDelegations:1:rivtabp21",
        wsdlLocation = "schemas/interactions/GetDelegationsInteraction/GetDelegationsInteraction_1.0_RIVTABP21.wsdl")
public class GetDelegationsResponderInterfaceImpl implements GetDelegationsResponderInterface {

    DelegationService delegationService;

    /**
     * Default constructor.
     */
    public GetDelegationsResponderInterfaceImpl() {
        super();
    }

    /**
     * Constructor that accepts implementation of a service-instance that permits access to underlying services.
     * @param delegationService
     */
    public GetDelegationsResponderInterfaceImpl(DelegationService delegationService) {
        super();
        this.delegationService = delegationService;
    }

    /**
     * Interface for searching and returning delegations. 
     * @param logicalAddress is not used at the moment, but might be later on.
     * @param parameters contains the actual search criteria 'delegation for'.
     */
    @Override
    public GetDelegationsResponseType getDelegations(String logicalAddress, GetDelegationsType parameters) {
        GetDelegationsResponseType gdrt = new GetDelegationsResponseType();

        gdrt.setDelegations(DelegationServiceUtil.parseDelegations(delegationService
                .getDelegations(parameters.getDelegationFor())));

        if (gdrt.getDelegations().getContent().size() > 0) {
            gdrt.setResultCode(ResultCodeEnum.OK);
        } else {
            gdrt.setResultCode(ResultCodeEnum.INFO);
            gdrt.setComment("No delegations found for this delegationFor: " + parameters.getDelegationFor());
        }

        return gdrt;
    }

}
