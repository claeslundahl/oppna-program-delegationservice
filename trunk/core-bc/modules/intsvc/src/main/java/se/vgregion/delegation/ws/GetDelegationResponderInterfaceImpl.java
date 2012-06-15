/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package se.vgregion.delegation.ws;

import javax.jws.WebService;
import javax.persistence.NoResultException;

import se.riv.authorization.delegation.getdelegation.v1.rivtabp21.GetDelegationResponderInterface;
import se.riv.authorization.delegation.getdelegationresponder.v1.GetDelegationResponseType;
import se.riv.authorization.delegation.getdelegationresponder.v1.GetDelegationType;
import se.riv.authorization.delegation.v1.ResultCodeEnum;
import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.ws.util.DelegationServiceUtil;

/**
 * Class implementing the {@link GetDelegationResponderInterface} interface.
 * @author Simon Göransson
 * @author Claes Lundahl
 */
@WebService(
        serviceName = "GetDelegationResponderService",
        endpointInterface = "se.riv.authorization.delegation.getdelegation.v1.rivtabp21.GetDelegationResponderInterface",
        portName = "GetDelegationResponderPort",
        targetNamespace = "urn:riv:authorization:delegation:GetDelegation:1:rivtabp21",
        wsdlLocation = "schemas/interactions/GetDelegationInteraction/GetDelegationInteraction_1.0_RIVTABP21.wsdl")
public class GetDelegationResponderInterfaceImpl implements GetDelegationResponderInterface {

    private DelegationService delegationService;

    /**
     * Constructor passing reference to service-object giving the instance capability to access underlying
     * resources to fulfill
     * calls to its functions.
     * @param delegationService service object to provide access to various services, access to db etc.
     */
    public GetDelegationResponderInterfaceImpl(DelegationService delegationService) {
        super();
        this.delegationService = delegationService;
    }

    /**
     * Method to find and get a Delegation with a certain delegation key. 
     * @param logicalAddress is not used in this implementation but might be later on.
     * @param parameters contains the search parameter (delegation key) to use as criteria to find the data.
     * @return an object bundling the actual delegation and the result code of the entire operation. If 
     * no data is found (with the provided delegation key) then the code signals an error.
     */
    @Override
    public GetDelegationResponseType getDelegation(String logicalAddress, GetDelegationType parameters) {

        GetDelegationResponseType delegationResponseType = new GetDelegationResponseType();

        try {
            delegationResponseType.setDelegation(DelegationServiceUtil.convertDelegation(delegationService
                    .findByDelegationKey(Long.parseLong(parameters.getDelegationKey()))));
            delegationResponseType.setResultCode(ResultCodeEnum.OK);
        } catch (NoResultException e) {
            delegationResponseType.setResultCode(ResultCodeEnum.ERROR);
            delegationResponseType.setComment("No delegation found for delegationKey = "
                    + parameters.getDelegationKey());
        }

        return delegationResponseType;

    }

}
