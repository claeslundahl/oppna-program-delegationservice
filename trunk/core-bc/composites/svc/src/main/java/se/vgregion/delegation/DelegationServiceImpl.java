package se.vgregion.delegation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationBlock;
import se.vgregion.delegation.domain.DelegationStatus;
import se.vgregion.delegation.persistence.DelegationBlockRepository;
import se.vgregion.delegation.persistence.DelegationKeySequenceRepository;
import se.vgregion.delegation.persistence.DelegationRepository;
import se.vgregion.delegation.util.DelegationUtil;

/**
 * the class DelegationServiceImpl. implements the DelegationService inteface.
 * 
 * @author Simon Göransson
 * @author Claes Lundahl
 * 
 */
@Component
public class DelegationServiceImpl implements DelegationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelegationServiceImpl.class);

    @Autowired
    private DelegationBlockRepository delegationBlockRepository;

    @Autowired(required = false)
    private DelegationRepository delegationRepository;

    @Autowired(required = false)
    private DelegationKeySequenceRepository delegationKeySequenceRepository;

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.DelegationService#getActiveDelegations(java.lang.String)
     */
    @Override
    public List<Delegation> getActiveDelegations(String delegatedFor) {
        return delegationRepository.getActiveDelegations(delegatedFor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.DelegationService#getInActiveDelegations(java.lang.String)
     */
    @Override
    public List<Delegation> getInActiveDelegations(String delegatedFor) {
        return delegationRepository.getInActiveDelegations(delegatedFor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.DelegationService#getDelegations(java.lang.String)
     */
    @Override
    public List<Delegation> getDelegations(String delegatedFor) {
        return delegationRepository.getDelegations(delegatedFor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.DelegationService#getDelegationsForToRole(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<Delegation> getDelegationsForToRole(String delegatedFor, String delegatedTo, String role) {
        return delegationRepository.getDelegationsForToRole(delegatedFor, delegatedTo, role);
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.DelegationService#getDelegation(java.lang.Long)
     */
    @Override
    public Delegation getDelegation(Long delegationId) {
        return delegationRepository.getDelegation(delegationId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.DelegationService#save(se.vgregion.delegation.domain.DelegationBlock)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public DelegationBlock save(DelegationBlock delegationBlock) {

        delegationBlock.setId(null);

        if (validateSigning(delegationBlock)) {
            List<Delegation> old = new ArrayList<Delegation>();

            for (Delegation delegation : new HashSet<Delegation>(delegationBlock.getDelegations())) {

                delegation.setStatus(DelegationStatus.ACTIVE);

                if (delegation.getDelegationKey() == null || delegation.getDelegationKey() == 0) {
                    delegation.setDelegationKey(delegationKeySequenceRepository.nextSequenceNumber());
                } else {
                    Delegation fresh = DelegationUtil.toDelegation(delegation);

                    fresh.setId(null);

                    old.add(delegation);
                    delegationBlock.getDelegations().remove(delegation);
                    delegationBlock.getDelegations().add(fresh);
                }
            }

            for (Delegation delegation : old) {
                Delegation stored = delegationRepository.findByDelegationKey(delegation.getDelegationKey());
                if (stored != null) {
                    stored.setStatus(DelegationStatus.HISTORY);
                    delegationRepository.merge(stored);
                }
            }

            DelegationBlock result = delegationBlockRepository.store(delegationBlock);

            return result;
        } else {
            throw new NotValidChecksumException();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.DelegationService#hasDelegations(java.lang.String, java.lang.String,
     * java.lang.String)
     */
    @Override
    public boolean hasDelegations(String delegatedFor, String delegatedTo, String role) {
        return delegationRepository.hasDelegations(delegatedFor, delegatedTo, role);
    }

    /**
     * Validate that signToken is signed by signer.
     * 
     * @param delegation
     * @return
     */
    private boolean validateSigning(DelegationBlock delegation) {
        // TODO check if the signtoken is valid using the signservice.
        if (delegation.getSignToken() != null) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.DelegationService#findByDelegationKey(java.lang.Long)
     */
    @Override
    public Delegation findByDelegationKey(Long delegationKey) {
        return delegationRepository.findByDelegationKey(delegationKey);
    }

    /*
     * (non-Javadoc)
     * 
     * @see se.vgregion.delegation.DelegationService#removeDelegation(java.lang.Long)
     */
    @Override
    public boolean removeDelegation(Long delegationKey) {
        return delegationRepository.removeDelegation(delegationKey);
    }
}
