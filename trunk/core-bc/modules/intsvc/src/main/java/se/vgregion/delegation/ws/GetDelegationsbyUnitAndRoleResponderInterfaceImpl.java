/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package se.vgregion.delegation.ws;

import javax.jws.WebService;

import se.riv.authorization.delegation.getdelegation.v1.rivtabp21.GetDelegationResponderInterface;
import se.riv.authorization.delegation.getdelegationsbyunitandrole.v1.rivtabp21.GetDelegationsbyUnitAndRoleResponderInterface;
import se.riv.authorization.delegation.getdelegationsbyunitandroleresponder.v1.GetDelegationsbyUnitAndRoleResponseType;
import se.riv.authorization.delegation.getdelegationsbyunitandroleresponder.v1.GetDelegationsbyUnitAndRoleType;
import se.riv.authorization.delegation.v1.ResultCodeEnum;
import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.ws.util.DelegationServiceUtil;

/**
 * Gives possibility to search database for delegations with some matching constraints.
 * @author Simon Göransson
 * @author Claes Lundahl
 * Class implementing the {@link GetDelegationsbyUnitAndRoleResponderInterface} interface.
 */
@WebService(
        serviceName = "GetDelegationsbyUnitAndRoleResponderService",
        endpointInterface = "se.riv.authorization.delegation.getdelegationsbyunitandrole.v1.rivtabp21.GetDelegationsbyUnitAndRoleResponderInterface",
        portName = "GetDelegationsbyUnitAndRoleResponderPort",
        targetNamespace = "urn:riv:authorization:delegation:GetDelegationsbyUnitAndRole:1:rivtabp21",
        wsdlLocation = "schemas/interactions/GetDelegationsbyUnitAndRoleInteraction/GetDelegationsbyUnitAndRoleInteraction_1.0_RIVTABP21.wsdl")
public class GetDelegationsbyUnitAndRoleResponderInterfaceImpl implements
        GetDelegationsbyUnitAndRoleResponderInterface {

    DelegationService delegationService;

    /**
     * Default constructor.
     */
    public GetDelegationsbyUnitAndRoleResponderInterfaceImpl() {
        super();
    }

    /**
     * Constructor providing the instance with a service-object giving it ability to access underlying data.
     * @param delegationService a service object for the public functions of this implementation.
     */
    public GetDelegationsbyUnitAndRoleResponderInterfaceImpl(DelegationService delegationService) {
        super();
        this.delegationService = delegationService;
    }

    /**
     * Find-method for delegations. Uses 'delegated-for', 'delegated-to' and 'role' to find its data.
     * It is possible to use the '*' char/string to ignore one of the parameters for this search.
     * @param logicalAddress is not used, might be later though.
     * @param parameters contains the actual search parameters 'delegated-for', 'delegated-to' and 'role'.
     * @return a list of results and a code signaling if an error occurd or not. 
     */
    @Override
    public GetDelegationsbyUnitAndRoleResponseType getDelegationsbyUnitAndRole(String logicalAddress,
            GetDelegationsbyUnitAndRoleType parameters) {

        GetDelegationsbyUnitAndRoleResponseType getDelegationsbyUnitAndRoleResponseType =
                new GetDelegationsbyUnitAndRoleResponseType();

        getDelegationsbyUnitAndRoleResponseType.setDelegations(DelegationServiceUtil
                .parseDelegations(delegationService.getDelegationsForToRole(parameters.getDelegatedFor(),
                        parameters.getDelegatedTo(), parameters.getRole())));

        if (getDelegationsbyUnitAndRoleResponseType.getDelegations().getContent().size() > 0) {
            getDelegationsbyUnitAndRoleResponseType.setResultCode(ResultCodeEnum.OK);
        } else {
            getDelegationsbyUnitAndRoleResponseType.setResultCode(ResultCodeEnum.INFO);
            getDelegationsbyUnitAndRoleResponseType
                    .setComment("No delegations found for parameters, delegationFor: "
                            + parameters.getDelegatedFor() + " delegationTo: " + parameters.getDelegatedTo()
                            + " role: " + parameters.getRole());
        }

        return getDelegationsbyUnitAndRoleResponseType;
    }

}
