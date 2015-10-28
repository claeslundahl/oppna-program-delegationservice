/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

package se.vgregion.delegation.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.itintegration.monitoring.pingforconfiguration.v1.rivtabp21.PingForConfigurationResponderInterface;
import se.riv.itintegration.monitoring.pingforconfigurationresponder.v1.PingForConfigurationResponseType;
import se.riv.itintegration.monitoring.pingforconfigurationresponder.v1.PingForConfigurationType;
import se.vgregion.delegation.DelegationService;
import se.vgregion.delegation.domain.Delegation;

import javax.jws.WebService;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation for checking whether or not a person have a valid delegation for a certain role and unit.
 *
 * @author Simon Göransson
 * @author Claes Lundahl
 */

@WebService(serviceName = "PingForConfigurationResponderService", endpointInterface = "se.riv.itintegration.monitoring.pingforconfiguration.v1.rivtabp21.PingForConfigurationResponderInterface", portName = "PingForConfigurationResponderPort",

        targetNamespace = "urn:riv:itintegration:monitoring:PingForConfiguration:1:rivtabp21",

        wsdlLocation = "http://rivta.googlecode.com/svn-history/r860/ServiceInteractions/riv/itintegration/monitoring/trunk/schemas/interactions/PingForConfigurationInteraction/PingForConfigurationInteraction_1.0_RIVTABP21.wsdl")
public class PingForConfigurationResponderInterfaceImpl implements PingForConfigurationResponderInterface {

    private DelegationService delegationService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMDDhhmmss");

    /**
     * Default constructor.
     */
    public PingForConfigurationResponderInterfaceImpl() {
        super();
    }

    /**
     * Construtor with possibilty to pass in an service instance, giving this implementation access to underlying
     * services.
     *
     * @param delegationService the service instance that can be used to fulfill the operations supported by this class.
     */
    public PingForConfigurationResponderInterfaceImpl(DelegationService delegationService) {
        super();
        this.delegationService = delegationService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PingForConfigurationResponderInterfaceImpl.class);

    @Override
    public PingForConfigurationResponseType pingForConfiguration(String s, PingForConfigurationType pingForConfigurationType) {
        PingForConfigurationResponseType result = new PingForConfigurationResponseType();
        String pingDateTime = sdf.format(new Date());
        result.setPingDateTime(pingDateTime);
        result.setVersion("1.0");

        // Contact the db to get an exception if there is something wrong with the connection.
        Delegation sample = new Delegation();
        sample.setId(-100l);
        delegationService.findBySample(sample);

        return result;

    }

    // @Override
    // public PingResponseType ping(String logicalAddress, PingType parameters) {
    // PingResponseType result = new PingResponseType();
    // try {
    // // Dummy data - does not need to produce any hits.
    // delegationService.hasDelegations("df", "dt", "r");
    // result.setResultCode(ResultCodeEnum.OK);
    // } catch (Exception e) {
    // e.printStackTrace();
    // result.setResultCode(ResultCodeEnum.ERROR);
    // }
    // return result;
    // }

}
