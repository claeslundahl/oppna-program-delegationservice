/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package se.vgregion.delegation.ws;

import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.riv.authorization.delegation.getactivedelegations.v1.rivtabp21.GetActiveDelegationsResponderInterface;
import se.riv.authorization.delegation.getactivedelegationsresponder.v1.GetActiveDelegationsResponseType;
import se.riv.authorization.delegation.getactivedelegationsresponder.v1.GetActiveDelegationsType;
import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.ws.util.DelegationServiceUtil;
import se.vgregion.ticket.TicketManager;

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

    public GetActiveDelegationsResponderInterfaceImpl(DelegationService delegationService,
            TicketManager ticketManager) {
        super();
        this.delegationService = delegationService;
    }

    static private final Logger logger = LoggerFactory
            .getLogger(GetActiveDelegationsResponderInterfaceImpl.class);

    @Override
    public GetActiveDelegationsResponseType getActiveDelegations(String logicalAddress,
            GetActiveDelegationsType parameters) {

        GetActiveDelegationsResponseType activeDelegationsResponseType =
                new GetActiveDelegationsResponseType();

        activeDelegationsResponseType.setDelegations(DelegationServiceUtil.parseDelegations(delegationService
                .getActiveDelegations(parameters.getDelegationFor())));

        return activeDelegationsResponseType;

    }
}
