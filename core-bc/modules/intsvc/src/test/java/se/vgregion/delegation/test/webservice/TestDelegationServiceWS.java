package se.vgregion.delegation.test.webservice;

/**
 *
 */

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.riv.authorization.delegation.finddelegationsresponder.v1.FindDelegationsResponseType;
import se.riv.authorization.delegation.finddelegationsresponder.v1.FindDelegationsType;
import se.riv.authorization.delegation.getactivedelegationsresponder.v1.GetActiveDelegationsResponseType;
import se.riv.authorization.delegation.getactivedelegationsresponder.v1.GetActiveDelegationsType;
import se.riv.authorization.delegation.getdelegationresponder.v1.GetDelegationResponseType;
import se.riv.authorization.delegation.getdelegationresponder.v1.GetDelegationType;
import se.riv.authorization.delegation.getdelegationsbyunitandroleresponder.v1.GetDelegationsbyUnitAndRoleResponseType;
import se.riv.authorization.delegation.getdelegationsbyunitandroleresponder.v1.GetDelegationsbyUnitAndRoleType;
import se.riv.authorization.delegation.getdelegationsresponder.v1.GetDelegationsResponseType;
import se.riv.authorization.delegation.getdelegationsresponder.v1.GetDelegationsType;
import se.riv.authorization.delegation.getinactivedelegationsresponder.v1.GetInactiveDelegationsResponseType;
import se.riv.authorization.delegation.getinactivedelegationsresponder.v1.GetInactiveDelegationsType;
import se.riv.authorization.delegation.hasdelegationresponder.v1.HasDelegationResponseType;
import se.riv.authorization.delegation.hasdelegationresponder.v1.HasDelegationType;
import se.riv.authorization.delegation.removedelegationresponder.v1.RemoveDelegationResponseType;
import se.riv.authorization.delegation.removedelegationresponder.v1.RemoveDelegationType;
import se.riv.authorization.delegation.v1.DelegationType;
import se.riv.authorization.delegation.v1.ResultCodeEnum;
import se.riv.itintegration.monitoring.pingforconfigurationresponder.v1.PingForConfigurationType;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 */
public class TestDelegationServiceWS extends AbstractTestBase {
    static private final Logger LOGGER = LoggerFactory.getLogger(TestDelegationServiceWS.class);

    @BeforeClass
    public static void startUpTestInfo() {
        LOGGER.info("Starting test for web services!");
    }

    @Test
    public void testSaveDelegation() {
        long responsedelegationKey = saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");
        Assert.assertNotSame(0, responsedelegationKey);
    }

    @Test
    public void testGetActiveDelegations() {

        saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        GetActiveDelegationsType parameters = new GetActiveDelegationsType();

        parameters.setDelegationFor("df");

        GetActiveDelegationsResponseType activeDelegationsResponseType = activeDelegationsResponderInterface
                .getActiveDelegations("", parameters);
        activeDelegationsResponseType.getDelegations().getContent().size();

        Assert.assertTrue(activeDelegationsResponseType.getDelegations().getContent().size() == 0);
    }

    @Test
    public void testGetActiveDelegationsNegative() {

        GetActiveDelegationsType parameters = new GetActiveDelegationsType();

        parameters.setDelegationFor("dfxxx");

        GetActiveDelegationsResponseType activeDelegationsResponseType = activeDelegationsResponderInterface
                .getActiveDelegations("", parameters);
        activeDelegationsResponseType.getDelegations().getContent().size();

        Assert.assertTrue(activeDelegationsResponseType.getDelegations().getContent().size() == 0);
        Assert.assertTrue(activeDelegationsResponseType.getResultCode().equals(ResultCodeEnum.INFO));
    }

    @Test
    public void testGetDelegation() {

        // Creates a delegetion to get.
        long responsedelegationKey = saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        GetDelegationType parameters = new GetDelegationType();
        parameters.setDelegationKey(String.valueOf(responsedelegationKey));

        // Gets delegation
        GetDelegationResponseType delegationResponseType = getDelegationResponderInterface.getDelegation("",
                parameters);

        delegationResponseType.getDelegation().getDelegationKey();

        Assert.assertEquals(delegationResponseType.getDelegation().getDelegationKey(), responsedelegationKey);
    }

    @Test
    public void testGetDelegationNegative() {

        GetDelegationType parameters = new GetDelegationType();
        parameters.setDelegationKey(String.valueOf(1873475839477878943L));

        // Gets delegation
        GetDelegationResponseType delegationResponseType = getDelegationResponderInterface.getDelegation("",
                parameters);

        Assert.assertTrue(delegationResponseType.getResultCode().equals(ResultCodeEnum.ERROR));
    }

    @Test
    public void testGetInactiveDelegations() {

        saveADelegation("2010/01/02", "2010/01/01", "2011/01/01");

        GetInactiveDelegationsType parameters = new GetInactiveDelegationsType();

        parameters.setDelegationFor("df");

        GetInactiveDelegationsResponseType inactiveDelegationsResponseType = inactiveDelegationsResponderInterface
                .getInactiveDelegations("", parameters);
        inactiveDelegationsResponseType.getDelegations().getContent().size();

        Assert.assertTrue(inactiveDelegationsResponseType.getDelegations().getContent().size() > 0);
    }

    @Test
    public void testGetInactiveDelegationsNegative() {

        GetInactiveDelegationsType parameters = new GetInactiveDelegationsType();

        parameters.setDelegationFor("dfxxx");

        GetInactiveDelegationsResponseType inactiveDelegationsResponseType = inactiveDelegationsResponderInterface
                .getInactiveDelegations("", parameters);
        inactiveDelegationsResponseType.getDelegations().getContent().size();

        Assert.assertTrue(inactiveDelegationsResponseType.getDelegations().getContent().size() == 0);
        Assert.assertTrue(inactiveDelegationsResponseType.getResultCode().equals(ResultCodeEnum.INFO));
    }

    @Test
    public void testGetDelegationsbyUnitAndRole() {

        saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        GetDelegationsbyUnitAndRoleType parameters = new GetDelegationsbyUnitAndRoleType();

        parameters.setDelegatedFor("df");
        parameters.setDelegatedTo("dt");
        parameters.setRole("role");

        GetDelegationsbyUnitAndRoleResponseType getDelegationsbyUnitAndRoleResponseType = getDelegationsbyUnitAndRoleResponderInterface
                .getDelegationsbyUnitAndRole("", parameters);

        Assert.assertTrue(getDelegationsbyUnitAndRoleResponseType.getDelegations().getContent().size() > 0);

    }

    @Test
    public void testGetDelegationsbyUnitAndRoleNegative() {

        GetDelegationsbyUnitAndRoleType parameters = new GetDelegationsbyUnitAndRoleType();

        parameters.setDelegatedFor("dfxxx");
        parameters.setDelegatedTo("dt");
        parameters.setRole("role");

        GetDelegationsbyUnitAndRoleResponseType getDelegationsbyUnitAndRoleResponseType = getDelegationsbyUnitAndRoleResponderInterface
                .getDelegationsbyUnitAndRole("", parameters);

        Assert.assertTrue(getDelegationsbyUnitAndRoleResponseType.getDelegations().getContent().size() == 0);
        Assert.assertTrue(getDelegationsbyUnitAndRoleResponseType.getResultCode().equals(ResultCodeEnum.INFO));

    }

    @Test
    public void testGetDelegations() {

        saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        GetDelegationsType parameters = new GetDelegationsType();

        parameters.setDelegationFor("df");

        GetDelegationsResponseType getDelegationsResponseType = getDelegationsResponderInterface.getDelegations(
                "", parameters);

        Assert.assertTrue(getDelegationsResponseType.getDelegations().getContent().size() > 0);

    }

    @Test
    public void testGetDelegationsNegative() {

        GetDelegationsType parameters = new GetDelegationsType();

        parameters.setDelegationFor("dfxxx");

        GetDelegationsResponseType getDelegationsResponseType = getDelegationsResponderInterface.getDelegations(
                "", parameters);

        Assert.assertTrue(getDelegationsResponseType.getDelegations().getContent().size() == 0);
        Assert.assertTrue(getDelegationsResponseType.getResultCode().equals(ResultCodeEnum.INFO));

    }

    @Test
    public void testHasDelegation() {

        saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        HasDelegationType parameters = new HasDelegationType();

        parameters.setDelegatedFor("df");
        parameters.setDelegatedTo("dt");
        parameters.setRole("role");

        HasDelegationResponseType hasDelegationResponseType = hasDelegationResponderInterface.hasDelegation("",
                parameters);

        Assert.assertTrue(!hasDelegationResponseType.isResult());
    }

    @Test
    public void testHasDelegationNegative() {

        HasDelegationType parameters = new HasDelegationType();

        parameters.setDelegatedFor("dfxxx");
        parameters.setDelegatedTo("dt");
        parameters.setRole("role");

        HasDelegationResponseType hasDelegationResponseType = hasDelegationResponderInterface.hasDelegation("",
                parameters);

        Assert.assertFalse(hasDelegationResponseType.isResult());
    }

    @Test
    public void testRemoveDelegation() {

        long delegationKey = saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        RemoveDelegationType parameters = new RemoveDelegationType();
        parameters.setDelegationKey("" + delegationKey);

        RemoveDelegationResponseType removeDelegationResponseType = removeDelegationResponderInterface
                .removeDelegation("", parameters);

        Assert.assertTrue(removeDelegationResponseType.getResultCode().equals(ResultCodeEnum.OK));

        GetDelegationType getParameters = new GetDelegationType();
        getParameters.setDelegationKey(delegationKey + "");

        GetDelegationResponseType failingResult = getDelegationResponderInterface.getDelegation("", getParameters);

        Assert.assertTrue(failingResult.getResultCode().equals(ResultCodeEnum.ERROR));
        Assert.assertNull(failingResult.getDelegation());
    }

    @Test
    public void testRemoveDelegationNegative() {

        RemoveDelegationType parameters = new RemoveDelegationType();
        parameters.setDelegationKey("5784464");

        RemoveDelegationResponseType removeDelegationResponseType = removeDelegationResponderInterface
                .removeDelegation("", parameters);

        Assert.assertTrue(removeDelegationResponseType.getResultCode().equals(ResultCodeEnum.ERROR));

    }

    @Test
    public void testRemoveDelegation2() {

        long delegationKey = saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        RemoveDelegationType parameters = new RemoveDelegationType();
        parameters.setDelegationKey("" + delegationKey);

        removeDelegationResponderInterface.removeDelegation("", parameters);

        GetDelegationType parameters2 = new GetDelegationType();

        parameters2.setDelegationKey("" + delegationKey);

        GetDelegationResponseType getDelegationResponseType = getDelegationResponderInterface.getDelegation("",
                parameters2);

        Assert.assertTrue(getDelegationResponseType.getResultCode().equals(ResultCodeEnum.ERROR));
    }

    @Test
    @Ignore
    public void testConnectionSimple() throws UnrecoverableKeyException, KeyManagementException,
            NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException {

        testConnection("localhost", 24002, "delegering-test.jks", "changeit", "delegering-test.jks", "changeit");

    }

    private void testConnection(String host, int hostPort, String trustStoreFileName, String trustStorePassword,
                                String keyStoreFileName, String keyStorePassword) throws NoSuchAlgorithmException, KeyStoreException,
            IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        Socket socket = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("SSLv3");

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(this.getClass().getClassLoader().getResourceAsStream(trustStoreFileName),
                    trustStorePassword.toCharArray());

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
                    .getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(this.getClass().getClassLoader().getResourceAsStream(keyStoreFileName),
                    keyStorePassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory
                    .getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

            socket = sslContext.getSocketFactory().createSocket(host, hostPort);

            socket.getOutputStream().write(1);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    @Test
    public void testFindDelegationsResponderInterface() {
        saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        FindDelegationsType parameters = new FindDelegationsType();
        FindDelegationsResponseType result = findDelegationsResponderInterface.findDelegations("", parameters);

        Assert.assertEquals(1, result.getDelegations().getContent().size());

        saveADelegation("2010/01/02", "2010/01/01", "2014/01/02");
        DelegationType dm = new DelegationType();
        dm.setValidTo(getADate(parseDate("2014/01/02")));
        parameters.setDelegationMatch(dm);
        result = findDelegationsResponderInterface.findDelegations("", parameters);
        Assert.assertEquals(1, result.getDelegations().getContent().size());

        dm = new DelegationType();
        dm.setValidFrom(getADate(parseDate("2010/01/01")));
        dm.setDelegatedFor("d*");
        parameters.setDelegationMatch(dm);
        result = findDelegationsResponderInterface.findDelegations("", parameters);
        Assert.assertEquals(2, result.getDelegations().getContent().size());
    }

    @Test
    public void testPing() {
        PingForConfigurationType config = new PingForConfigurationType();
        pingForConfigurationResponderInterface.pingForConfiguration("", config);
    }
}
