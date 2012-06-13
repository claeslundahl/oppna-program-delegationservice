/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package se.vgregion.delegation.ws.util;

import java.util.Collection;
import java.util.List;

import se.riv.authorization.delegation.v1.DelegationType;
import se.riv.authorization.delegation.v1.DelegationsType;
import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.util.DelegationUtil;

public final class DelegationServiceUtil {

    private DelegationServiceUtil() {

    }

    public static DelegationType convertDelegation(Delegation delegation) {
        return DelegationUtil.convert(delegation, DelegationType.class);
    }

    public static DelegationsType parseDelegations(Collection<Delegation> delegations) {

        DelegationsType delegationsReturn = new DelegationsType();
        List<DelegationType> delegationsList = delegationsReturn.getContent();

        for (Delegation delegation : delegations) {
            delegationsList.add(convertDelegation(delegation));
        }
        return delegationsReturn;
    }

}
