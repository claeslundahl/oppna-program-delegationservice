/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package se.vgregion.delegation.ws;

import javax.jws.WebService;

import se.riv.authorization.delegation.getactivedelegations.v1.rivtabp21.GetActiveDelegationsResponderInterface;
import se.riv.authorization.delegation.getactivedelegationsresponder.v1.GetActiveDelegationsResponseType;
import se.riv.authorization.delegation.getactivedelegationsresponder.v1.GetActiveDelegationsType;
import se.riv.authorization.delegation.v1.ResultCodeEnum;
import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.ws.util.DelegationServiceUtil;

/**
 * This class was generated by Apache CXF 2.2.2 Wed Apr 18 14:42:43 CEST 2012 Generated source version: 2.2.2
 * 
 */

@WebService(
        serviceName = "GetActiveDelegationsResponderService",
        endpointInterface = "se.riv.authorization.delegation.getactivedelegations.v1.rivtabp21.GetActiveDelegationsResponderInterface",
        portName = "GetActiveDelegationsResponderPort",
        targetNamespace = "urn:riv:authorization:delegation:GetActiveDelegations:1:rivtabp21",
        wsdlLocation = "schemas/interactions/GetActiveDelegationsInteraction/GetActiveDelegationsInteraction_1.0_RIVTABP21.wsdl")
public class GetActiveDelegationsResponderInterfaceImpl implements GetActiveDelegationsResponderInterface {

    DelegationService delegationService;

    public GetActiveDelegationsResponderInterfaceImpl() {
        super();
    }

    public GetActiveDelegationsResponderInterfaceImpl(DelegationService delegationService) {
        super();
        this.delegationService = delegationService;
    }

    @Override
    public GetActiveDelegationsResponseType getActiveDelegations(String logicalAddress,
            GetActiveDelegationsType parameters) {

        GetActiveDelegationsResponseType activeDelegationsResponseType =
                new GetActiveDelegationsResponseType();

        activeDelegationsResponseType.setDelegations(DelegationServiceUtil.parseDelegations(delegationService
                .getActiveDelegations(parameters.getDelegationFor())));

        if (activeDelegationsResponseType.getDelegations().getContent().size() > 0) {
            activeDelegationsResponseType.setResultCode(ResultCodeEnum.OK);
        } else {
            activeDelegationsResponseType.setResultCode(ResultCodeEnum.INFO);
            activeDelegationsResponseType.setComment("No delegations found for this delegationFor: "
                    + parameters.getDelegationFor());
        }

        return activeDelegationsResponseType;

    }
}
