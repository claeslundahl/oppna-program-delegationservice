package se.vgregion.delegation.delegation.view.domain;

/**
 * Enum for status of an delegation.
 * 
 * @author Simon Göransson
 * @author Claes Lundahl
 */
public enum DelegationStatus {

    /** Signed delegation - currently active (only one per VerksamhetsChef and OrganizationUnit) */
    ACTIVE,
    /** Deleted delegation - a delegation that have been deleted by a consumer of the service. */
    DELETED,
    /** History delegation - has at some time been active and replaced by a new delegation. */
    HISTORY;

}
