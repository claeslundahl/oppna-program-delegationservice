package se.vgregion.delegation.delegation.view.directory;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Class used to expose properties in a static context.
 */
@Service
public class ApplicationProperties {

    private static ApplicationProperties INSTANCE;

    @Value("${ldap.certificate.path}")
    private String ldapCertificatePath;

    @PostConstruct
    public void init() {
        ApplicationProperties.INSTANCE = this;
    }

    public static ApplicationProperties getInstance() {
        return INSTANCE;
    }

    public String getLdapCertificatePath() {
        return ldapCertificatePath;
    }

}
