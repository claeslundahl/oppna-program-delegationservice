package se.vgregion.delegation;

/**
 * The Interface DelegationExpieryAlertJob.
 */
public interface DelegationExpieryAlertJob {

    /**
     * Scans the repo and send Email's to all expiring delegations.
     */
    void scanRepoAndSendMails();

}
