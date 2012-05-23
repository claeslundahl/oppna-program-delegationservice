package se.vgregion.delegation.test.webservice;

/**
 * 
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.net.ssl.TrustManagerFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import se.riv.authorization.delegation.getactivedelegations.v1.rivtabp21.GetActiveDelegationsResponderInterface;
import se.riv.authorization.delegation.getactivedelegationsresponder.v1.GetActiveDelegationsResponseType;
import se.riv.authorization.delegation.getactivedelegationsresponder.v1.GetActiveDelegationsType;
import se.riv.authorization.delegation.getdelegation.v1.rivtabp21.GetDelegationResponderInterface;
import se.riv.authorization.delegation.getdelegationresponder.v1.GetDelegationResponseType;
import se.riv.authorization.delegation.getdelegationresponder.v1.GetDelegationType;
import se.riv.authorization.delegation.getdelegations.v1.rivtabp21.GetDelegationsResponderInterface;
import se.riv.authorization.delegation.getdelegationsbyunitandrole.v1.rivtabp21.GetDelegationsbyUnitAndRoleResponderInterface;
import se.riv.authorization.delegation.getdelegationsbyunitandroleresponder.v1.GetDelegationsbyUnitAndRoleResponseType;
import se.riv.authorization.delegation.getdelegationsbyunitandroleresponder.v1.GetDelegationsbyUnitAndRoleType;
import se.riv.authorization.delegation.getdelegationsresponder.v1.GetDelegationsResponseType;
import se.riv.authorization.delegation.getdelegationsresponder.v1.GetDelegationsType;
import se.riv.authorization.delegation.getinactivedelegations.v1.rivtabp21.GetInactiveDelegationsResponderInterface;
import se.riv.authorization.delegation.getinactivedelegationsresponder.v1.GetInactiveDelegationsResponseType;
import se.riv.authorization.delegation.getinactivedelegationsresponder.v1.GetInactiveDelegationsType;
import se.riv.authorization.delegation.getticket.v1.rivtabp21.GetTicketResponderInterface;
import se.riv.authorization.delegation.getticketresponder.v1.GetTicketResponseType;
import se.riv.authorization.delegation.getticketresponder.v1.GetTicketType;
import se.riv.authorization.delegation.hasdelegation.v1.rivtabp21.HasDelegationResponderInterface;
import se.riv.authorization.delegation.hasdelegationresponder.v1.HasDelegationResponseType;
import se.riv.authorization.delegation.hasdelegationresponder.v1.HasDelegationType;
import se.riv.authorization.delegation.savedelegations.v1.rivtabp21.SaveDelegationsResponderInterface;
import se.riv.authorization.delegation.savedelegationsresponder.v1.ResultCodeEnum;
import se.riv.authorization.delegation.savedelegationsresponder.v1.SaveDelegationsResponseType;
import se.riv.authorization.delegation.savedelegationsresponder.v1.SaveDelegationsType;
import se.riv.authorization.delegation.v1.DelegationBlockType;
import se.riv.authorization.delegation.v1.DelegationType;
import se.vgregion.delegation.server.Server;

/**
 * @author Simon Göransson - simon.goransson@monator.com - vgrid: simgo3
 * 
 */
public class IntegrationTestDelegationServiceWS {
    static private final Logger logger = LoggerFactory.getLogger(IntegrationTestDelegationServiceWS.class);

    GetTicketResponderInterface getTicketResponderInterface;
    GetActiveDelegationsResponderInterface activeDelegationsResponderInterface;
    GetDelegationResponderInterface getDelegationResponderInterface;
    GetInactiveDelegationsResponderInterface inactiveDelegationsResponderInterface;
    GetDelegationsbyUnitAndRoleResponderInterface getDelegationsbyUnitAndRoleResponderInterface;
    GetDelegationsResponderInterface getDelegationsResponderInterface;
    HasDelegationResponderInterface hasDelegationResponderInterface;
    SaveDelegationsResponderInterface saveDelegationsResponderInterface;
    Server server;

    @Before
    public void setUp() throws Exception {

        // Server
        server = new Server();
        String serverPath = "classpath:/spring/serverConf.xml";
        server.startServer(serverPath);

        logger.info("Server Ready !!!! ");

        // Client
        String clientPath = "classpath:/spring/clientConf.xml";
        ApplicationContext context = new ClassPathXmlApplicationContext(clientPath);

        getTicketResponderInterface =
                (GetTicketResponderInterface) context.getBean("getTicketResponderInterface");
        activeDelegationsResponderInterface =
                (GetActiveDelegationsResponderInterface) context
                        .getBean("activeDelegationsResponderInterface");
        getDelegationResponderInterface =
                (GetDelegationResponderInterface) context.getBean("getDelegationResponderInterface");
        inactiveDelegationsResponderInterface =
                (GetInactiveDelegationsResponderInterface) context
                        .getBean("inactiveDelegationsResponderInterface");
        getDelegationsbyUnitAndRoleResponderInterface =
                (GetDelegationsbyUnitAndRoleResponderInterface) context
                        .getBean("getDelegationsbyUnitAndRoleResponderInterface");
        getDelegationsResponderInterface =
                (GetDelegationsResponderInterface) context.getBean("getDelegationsResponderInterface");
        hasDelegationResponderInterface =
                (HasDelegationResponderInterface) context.getBean("hasDelegationResponderInterface");
        saveDelegationsResponderInterface =
                (SaveDelegationsResponderInterface) context.getBean("saveDelegationsResponderInterface");

        logger.info("Client Ready !!!! ");
    }

    @After
    public void tearDown() throws Exception {
        server.stopServer();
    }

    @Test
    public void testGetTicketResponderInterface() {

        Assert.assertTrue(getTicket() != null);

    }

    @Test
    public void testSaveDelegationResponderInterface() {

        long responsedelegationKey = saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        Assert.assertTrue(responsedelegationKey == 1);

    }

    @Test
    public void testInvalidTicket() {

        SaveDelegationsType parameters = new SaveDelegationsType();

        parameters
                .setTicket("2012-05-23T11:51:42+0200_ACwCFEFwPR86LQM1sCAoLdfZ+RuTb3G7AhRe5BgE0Zd9xsOkXfC3G9RspOrYXg==");

        DelegationBlockType block = new DelegationBlockType();
        List<DelegationType> list = block.getDelegations();
        DelegationType delegationType = new DelegationType();
        list.add(delegationType);
        parameters.setDelegationBlockType(block);

        SaveDelegationsResponseType delegationsResponseType =
                saveDelegationsResponderInterface.saveDelegations("", parameters);

        Assert.assertTrue(delegationsResponseType.getResultCode().equals(ResultCodeEnum.ERROR));

    }

    @Test
    public void testGetActiveDelegationsResponderInterface() {

        // saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        GetActiveDelegationsType parameters = new GetActiveDelegationsType();

        parameters.setDelegationFor("df");

        GetActiveDelegationsResponseType activeDelegationsResponseType =
                activeDelegationsResponderInterface.getActiveDelegations("", parameters);
        activeDelegationsResponseType.getDelegations().getContent().size();

        Assert.assertTrue(activeDelegationsResponseType.getDelegations().getContent().size() > 0);
    }

    @Test
    public void testGetDelegationResponderInterface() {

        // Creates a delegetion to get.
        long responsedelegationKey = saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        GetDelegationType parameters = new GetDelegationType();
        parameters.setDelegationKey(String.valueOf(responsedelegationKey));

        // Gets delegation
        GetDelegationResponseType delegationResponseType =
                getDelegationResponderInterface.getDelegation("", parameters);

        delegationResponseType.getDelegation().getDelegationKey();

        Assert.assertEquals(delegationResponseType.getDelegation().getDelegationKey(), responsedelegationKey);
    }

    @Test
    public void testGetInactiveDelegationsResponderInterface() {

        saveADelegation("2010/01/02", "2010/01/01", "2011/01/01");

        GetInactiveDelegationsType parameters = new GetInactiveDelegationsType();

        parameters.setDelegationFor("df");

        GetInactiveDelegationsResponseType inactiveDelegationsResponseType =
                inactiveDelegationsResponderInterface.getInactiveDelegations("", parameters);
        inactiveDelegationsResponseType.getDelegations().getContent().size();

        Assert.assertTrue(inactiveDelegationsResponseType.getDelegations().getContent().size() > 0);
    }

    @Test
    public void testGetDelegationsbyUnitAndRoleResponderInterface() {

        saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        GetDelegationsbyUnitAndRoleType parameters = new GetDelegationsbyUnitAndRoleType();

        parameters.setDelegatedFor("df");
        parameters.setDelegatedTo("dt");
        parameters.setRole("role");

        GetDelegationsbyUnitAndRoleResponseType getDelegationsbyUnitAndRoleResponseType =
                getDelegationsbyUnitAndRoleResponderInterface.getDelegationsbyUnitAndRole("", parameters);

        Assert.assertTrue(getDelegationsbyUnitAndRoleResponseType.getDelegations().getContent().size() > 0);

    }

    @Test
    public void testGetDelegationsResponderInterface() {

        saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        GetDelegationsType parameters = new GetDelegationsType();

        parameters.setDelegationFor("df");

        GetDelegationsResponseType getDelegationsResponseType =
                getDelegationsResponderInterface.getDelegations("", parameters);

        Assert.assertTrue(getDelegationsResponseType.getDelegations().getContent().size() > 0);

    }

    @Test
    public void testHasDelegationResponderInterface() {

        saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        HasDelegationType parameters = new HasDelegationType();

        parameters.setDelegatedFor("df");
        parameters.setDelegatedTo("dt");
        parameters.setRole("role");

        HasDelegationResponseType hasDelegationResponseType =
                hasDelegationResponderInterface.hasDelegation("", parameters);

        Assert.assertTrue(hasDelegationResponseType.isResult());
    }

    private long saveADelegation(String aO, String vF, String vT) {
        SaveDelegationsType parameters = new SaveDelegationsType();

        parameters.setTicket(getTicket());

        DelegationBlockType block = new DelegationBlockType();

        block.setApprovedOn(getADate(parseDate(aO)));
        block.setSignToken("st");

        List<DelegationType> list = block.getDelegations();

        DelegationType delegationType = new DelegationType();

        delegationType.setDelegatedFor("df");
        delegationType.setDelegateTo("dt");
        delegationType.setRole("role");

        delegationType.setValidFrom(getADate(parseDate(vF)));
        delegationType.setValidTo(getADate(parseDate(vT)));

        list.add(delegationType);

        parameters.setDelegationBlockType(block);

        SaveDelegationsResponseType delegationsResponseType =
                saveDelegationsResponderInterface.saveDelegations("", parameters);

        return delegationsResponseType.getDelegations().getContent().get(0).getDelegationKey();
    }

    private String getTicket() {
        GetTicketType parameters = new GetTicketType();
        parameters.setServiceId("someServiceId");

        GetTicketResponseType getTicketResponseType = getTicketResponderInterface.getTicket("", parameters);
        return getTicketResponseType.getTicket();
    }

    private Date parseDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private static XMLGregorianCalendar getADate(Date date) {
        GregorianCalendar dateValue = new GregorianCalendar();
        dateValue.setTime(date);
        try {
            XMLGregorianCalendar xmlValue = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateValue);
            return xmlValue;
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public Object createSomeWebService(Class someWebServiceClass, String address) throws IOException,
            NoSuchAlgorithmException, KeyStoreException, CertificateException {

        ClientProxyFactoryBean clientProxyFactoryBean = new ClientProxyFactoryBean();
        ClientFactoryBean clientFactoryBean = clientProxyFactoryBean.getClientFactoryBean();
        clientFactoryBean.setAddress(address);
        clientFactoryBean.setServiceClass(someWebServiceClass);

        PrintWriter printWriter = new PrintWriter(System.out, true);

        LoggingOutInterceptor loggingOutInterceptor = new LoggingOutInterceptor(printWriter);
        loggingOutInterceptor.setPrettyLogging(true);

        LoggingInInterceptor loggingInInterceptor = new LoggingInInterceptor(printWriter);
        loggingInInterceptor.setPrettyLogging(true);

        clientFactoryBean.getOutInterceptors().add(loggingOutInterceptor);

        clientFactoryBean.getInInterceptors().add(loggingInInterceptor);

        // // WSS4JOutInterceptor
        // HashMap<String, Object> outProps = new HashMap<String, Object>();
        // outProps.put(WSHandlerConstants.ACTION, "Encrypt");
        // outProps.put(WSHandlerConstants.USER, "mykey");
        // outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientCallbackHandler.class.getName());
        // // outProps.put(WSHandlerConstants.SIG_PROP_FILE, "client_key.properties");
        // outProps.put(WSHandlerConstants.ENC_PROP_FILE, "server_key.properties");
        // // outProps.put(WSHandlerConstants.ENC_PROP_FILE, "client_trust.properties");
        // WSS4JOutInterceptor wss4JOutInterceptor = new WSS4JOutInterceptor(outProps);
        // clientFactoryBean.getOutInterceptors().add(wss4JOutInterceptor);
        //
        // // WSS4JInInterceptor
        // HashMap<String, Object> inProps = new HashMap<String, Object>();
        // inProps.put(WSHandlerConstants.ACTION, "Encrypt");
        // inProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientCallbackHandler.class.getName());
        // inProps.put(WSHandlerConstants.DEC_PROP_FILE, "client_key.properties");
        // WSS4JInInterceptor wss4JInInterceptor = new WSS4JInInterceptor(inProps);
        // clientFactoryBean.getInInterceptors().add(wss4JInInterceptor);

        Object someWebService = clientProxyFactoryBean.create(someWebServiceClass);
        Client clientProxy = ClientProxy.getClient(someWebService);
        HTTPConduit httpConduit = (HTTPConduit) clientProxy.getConduit();
        httpConduit.setTlsClientParameters(setupTlsClientParameters());

        return someWebService;
    }

    private TLSClientParameters setupTlsClientParameters() throws KeyStoreException,
            NoSuchAlgorithmException, IOException, CertificateException {
        // InputStream resourceAsStream = getClass().getResourceAsStream("/client/clientkeystore.jks");
        InputStream resourceAsStream = getClass().getResourceAsStream("/server/serverkeystore.jks");

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(resourceAsStream, "changeit".toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);

        TLSClientParameters tlsClientParameters = new TLSClientParameters();
        tlsClientParameters.setTrustManagers(trustManagerFactory.getTrustManagers());
        tlsClientParameters.setDisableCNCheck(true);

        return tlsClientParameters;
    }
}
