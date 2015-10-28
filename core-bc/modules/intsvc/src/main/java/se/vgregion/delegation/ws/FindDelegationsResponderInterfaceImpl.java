/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package se.vgregion.delegation.ws;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.collections.BeanMap;

import se.riv.authorization.delegation.finddelegations.v1.rivtabp21.FindDelegationsResponderInterface;
import se.riv.authorization.delegation.finddelegationsresponder.v1.FindDelegationsResponseType;
import se.riv.authorization.delegation.finddelegationsresponder.v1.FindDelegationsType;
import se.riv.authorization.delegation.v1.DelegationType;
import se.riv.authorization.delegation.v1.DelegationsType;
import se.riv.authorization.delegation.v1.ResultCodeEnum;
import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationBlock;
import se.vgregion.delegation.util.DelegationUtil;

/**
 * Implementation of service that adds or update a delegation.
 * 
 * @author Simon Göransson
 * @author Claes Lundahl
 */
@WebService(serviceName = "FindDelegationsResponderService", endpointInterface = "se.riv.authorization.delegation.finddelegations.v1.rivtabp21.FindDelegationsResponderInterface", portName = "FindDelegationsResponderPort", targetNamespace = "urn:riv:authorization:delegation:FindDelegations:1:rivtabp21", wsdlLocation = "schemas/interactions/FindDelegationsInteraction/FindDelegationsInteraction_1.0_RIVTABP21.wsdl")
public class FindDelegationsResponderInterfaceImpl implements FindDelegationsResponderInterface {

	private DelegationService delegationService;

	/**
	 * Default constructor.
	 */
	public FindDelegationsResponderInterfaceImpl() {
		super();
	}

	/**
	 * Constructor that allows passing a service object to the instance that will alow it to work with underlying
	 * resources, db etc.
	 * 
	 * @param delegationService
	 *            the service instance to be used.
	 */
	public FindDelegationsResponderInterfaceImpl(DelegationService delegationService) {
		super();
		this.delegationService = delegationService;

	}

	@Override
	public FindDelegationsResponseType findDelegations(String logicalAddress, FindDelegationsType parameters) {
		Delegation match = DelegationUtil.toDelegation(parameters.getDelegationMatch());

		turnPropertyZerosIntoNull(match);

		List<Delegation> searchResult = delegationService.findBySample(match);
		FindDelegationsResponseType result = new FindDelegationsResponseType();
		DelegationsType delegations = new DelegationsType();
		delegations.getContent().addAll(DelegationUtil.convert(searchResult, DelegationType.class));
		result.setDelegations(delegations);
		result.setResultCode(ResultCodeEnum.OK);

		return result;
	}

	/**
	 * Since the marshaller seems to turn null into 0 sometimes this method is handy.
	 * 
	 * @param match
	 */
	private void turnPropertyZerosIntoNull(Object match) {
		BeanMap bm = new BeanMap(match);

		for (Object key : bm.keySet()) {
			Object value = bm.get(key);
			if (value instanceof Number) {
				Number n = (Number) value;
				if (bm.getWriteMethod(key.toString()) != null) {
					Double d = Double.parseDouble(n.toString());
					if (d.equals(new Double(0))) {
						bm.put(key, null);
					}
				}
			}
		}
	}

	// /**
	// * Saves a new post or updates an old one. A new is created if no delegation key is found (one is generated
	// by
	// * the system and returned with the answer).
	// *
	// * @param logicalAddress
	// * is not used currently.
	// * @param parameters
	// * contains the delegation to save / create.
	// * @return an object containing the freshly saved delegation and or an result code.
	// */
	// @Override
	// public FindDelegationsResponseType saveDelegations(String logicalAddress, FindDelegationsType parameters) {
	//
	// FindDelegationsResponseType result = new FindDelegationsResponseType();
	//
	// DelegationBlock block = DelegationUtil.toDelegationBlock(parameters.getDelegationBlockType());
	// DelegationBlock savedBlock = delegationService.save(block);
	// result.setDelegations(DelegationServiceUtil.parseDelegations(savedBlock.getDelegations()));
	// result.setResultCode(ResultCodeEnum.OK);
	//
	// return result;
	//
	// }

}
