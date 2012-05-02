package se.vgregion.delegation.domain;

/**
 * @author <a href="mailto:david.rosell@redpill-linpro.com">David Rosell</a>
 */
public enum DelegationStatus {

    /** Signed delegation - currently active (only one per VerksamhetsChef and OrganizationUnit) */
    ACTIVE,

    /** deleted delegation - has at some time been active */
    DELETED;

}
