/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package se.vgregion.delegation.ws;

import java.util.logging.Logger;

import javax.jws.WebService;

import se.riv.authorization.delegation.getinactivedelegations.v1.rivtabp21.GetInactiveDelegationsResponderInterface;
import se.riv.authorization.delegation.getinactivedelegationsresponder.v1.GetInactiveDelegationsResponseType;
import se.riv.authorization.delegation.getinactivedelegationsresponder.v1.GetInactiveDelegationsType;
import se.riv.authorization.delegation.getinactivedelegationsresponder.v1.ResultCodeEnum;
import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.ws.util.DelegationServiceUtil;

/**
 * This class was generated by Apache CXF 2.2.2 Wed Apr 18 14:42:43 CEST 2012 Generated source version: 2.2.2
 * 
 */

@WebService(
        serviceName = "GetInactiveDelegationsResponderService",
        endpointInterface = "se.riv.authorization.delegation.getinactivedelegations.v1.rivtabp21.GetInactiveDelegationsResponderInterface",
        portName = "GetInactiveDelegationsResponderPort",
        targetNamespace = "urn:riv:authorization:delegation:GetInactiveDelegations:1:rivtabp21",
        wsdlLocation = "schemas/interactions/GetInactiveDelegationsInteraction/GetInactiveDelegationsInteraction_1.0_RIVTABP21.wsdl")
public class GetInactiveDelegationsResponderInterfaceImpl implements GetInactiveDelegationsResponderInterface {

    DelegationService delegationService;

    public GetInactiveDelegationsResponderInterfaceImpl() {
        super();
    }

    public GetInactiveDelegationsResponderInterfaceImpl(DelegationService delegationService) {
        super();
        this.delegationService = delegationService;
    }

    private static final Logger LOG = Logger.getLogger(GetInactiveDelegationsResponderInterfaceImpl.class
            .getName());

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
