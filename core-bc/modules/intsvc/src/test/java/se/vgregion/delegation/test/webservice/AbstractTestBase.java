package se.vgregion.delegation.test.webservice;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import se.riv.authorization.delegation.finddelegations.v1.rivtabp21.FindDelegationsResponderInterface;
import se.riv.authorization.delegation.getactivedelegations.v1.rivtabp21.GetActiveDelegationsResponderInterface;
import se.riv.authorization.delegation.getdelegation.v1.rivtabp21.GetDelegationResponderInterface;
import se.riv.authorization.delegation.getdelegations.v1.rivtabp21.GetDelegationsResponderInterface;
import se.riv.authorization.delegation.getdelegationsbyunitandrole.v1.rivtabp21.GetDelegationsbyUnitAndRoleResponderInterface;
import se.riv.authorization.delegation.getinactivedelegations.v1.rivtabp21.GetInactiveDelegationsResponderInterface;
import se.riv.authorization.delegation.hasdelegation.v1.rivtabp21.HasDelegationResponderInterface;
import se.riv.authorization.delegation.removedelegation.v1.rivtabp21.RemoveDelegationResponderInterface;
import se.riv.authorization.delegation.savedelegations.v1.rivtabp21.SaveDelegationsResponderInterface;
import se.riv.authorization.delegation.savedelegationsresponder.v1.SaveDelegationsResponseType;
import se.riv.authorization.delegation.savedelegationsresponder.v1.SaveDelegationsType;
import se.riv.authorization.delegation.v1.DelegationBlockType;
import se.riv.authorization.delegation.v1.DelegationType;
import se.riv.itintegration.monitoring.pingforconfiguration.v1.rivtabp21.PingForConfigurationResponderInterface;
import se.vgregion.delegation.mail.DelegationMailSenderService;
import se.vgregion.delegation.server.Server;
import se.vgregion.delegation.ws.util.PropertiesBean;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.Connection;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public abstract class AbstractTestBase {

    static private final Logger LOGGER = LoggerFactory.getLogger(AbstractTestBase.class);

    DelegationMailSenderService delegationMailSenderService;
    GetActiveDelegationsResponderInterface activeDelegationsResponderInterface;
    GetDelegationResponderInterface getDelegationResponderInterface;
    GetInactiveDelegationsResponderInterface inactiveDelegationsResponderInterface;
    GetDelegationsbyUnitAndRoleResponderInterface getDelegationsbyUnitAndRoleResponderInterface;
    GetDelegationsResponderInterface getDelegationsResponderInterface;
    HasDelegationResponderInterface hasDelegationResponderInterface;
    SaveDelegationsResponderInterface saveDelegationsResponderInterface;
    RemoveDelegationResponderInterface removeDelegationResponderInterface;
    FindDelegationsResponderInterface findDelegationsResponderInterface;
    PingForConfigurationResponderInterface pingForConfigurationResponderInterface;

    PropertiesBean propertiesBean;

    Server server;

    ClassPathXmlApplicationContext context;

    @Before
    public void setUp() throws Exception {
        setUpContext();
        initStuffFromContext();
    }

    public void initStuffFromContext() throws Exception {

        // Server
        server = new Server();

        delegationMailSenderService = (DelegationMailSenderService) context.getBean("delegationMailSenderService");

        propertiesBean = (PropertiesBean) context.getBean("propertiesBean");

        server.startServer(context, "localhost", "24004");

        LOGGER.info("Server Ready !!!! ");

        boolean https = (propertiesBean.getCertPass() != null && !propertiesBean.getCertPass().equals(""));

        ClassPathXmlApplicationContext contextClient;

        if (https) {
            contextClient = new ClassPathXmlApplicationContext("classpath:settings/clientConfHttps.xml");
        } else {
            contextClient = new ClassPathXmlApplicationContext("classpath:settings/clientConf.xml");
        }

        activeDelegationsResponderInterface = (GetActiveDelegationsResponderInterface) contextClient
                .getBean("activeDelegationsResponderInterface");
        getDelegationResponderInterface = (GetDelegationResponderInterface) contextClient
                .getBean("getDelegationResponderInterface");
        inactiveDelegationsResponderInterface = (GetInactiveDelegationsResponderInterface) contextClient
                .getBean("inactiveDelegationsResponderInterface");
        getDelegationsbyUnitAndRoleResponderInterface = (GetDelegationsbyUnitAndRoleResponderInterface) contextClient
                .getBean("getDelegationsbyUnitAndRoleResponderInterface");
        getDelegationsResponderInterface = (GetDelegationsResponderInterface) contextClient
                .getBean("getDelegationsResponderInterface");
        hasDelegationResponderInterface = (HasDelegationResponderInterface) contextClient
                .getBean("hasDelegationResponderInterface");
        saveDelegationsResponderInterface = (SaveDelegationsResponderInterface) contextClient
                .getBean("saveDelegationsResponderInterface");
        removeDelegationResponderInterface = (RemoveDelegationResponderInterface) contextClient
                .getBean("removeDelegationResponderInterface");
        findDelegationsResponderInterface = (FindDelegationsResponderInterface) contextClient
                .getBean("findDelegationsResponderInterface");

        pingForConfigurationResponderInterface = (PingForConfigurationResponderInterface) contextClient
                .getBean("pingForConfigurationResponderInterface");

        LOGGER.info("Client Ready !!!! ");

    }

    protected void setUpContext() {
        String path = "classpath:/settings/serverConf.xml";
        context = new ClassPathXmlApplicationContext(path);
    }

    @After
    public void tearDown() throws Exception {
        server.stopServer("24004");

        DriverManagerDataSource dataSource = (DriverManagerDataSource) context.getBean("dataSource");

        Connection conn = dataSource.getConnection();

        Statement st = conn.createStatement();
        st.execute("delete from vgr_delegation");
        st.execute("delete from vgr_delegation_block");
        conn.commit();

        st.execute("SHUTDOWN");
        conn.close();

        context.close();
    }

    protected long saveADelegation(String aO, String vF, String vT) {
        return saveADelegation(aO, vF, vT, "test@vgregion.se");
    }

    protected long saveADelegation(String aO, String vF, String vT, String epost) {
        SaveDelegationsType parameters = new SaveDelegationsType();

        DelegationBlockType block = new DelegationBlockType();

        block.setApprovedOn(getADate(parseDate(aO)));
        block.setSignToken("dfglkjgklgfjdkgjjlkfdjklgjdfklgjdfklgjkdlfjgkldfjgklfdjgkldjgklgjhdfgjkhdfgjkhdfjkgjdfhgjkdfhgjkdfhgjkdfhgjkdfhgjkdfhgjkfhdgjkdfjkghdfjkghdfkjghkdjfhgjkdhgjkdhgjkdhgjkdjgkldjgklgjdlkdkjklgjfdk");

        List<DelegationType> list = block.getDelegations();

        DelegationType delegationType = new DelegationType();

        delegationType.setDelegatedFor("df");
        //delegationType.setDelegatedForEmail("test@vgregion.se");
        delegationType.setDelegatedForEmail(epost);
        delegationType.setDelegateTo("dt");
        delegationType.setRole("role");

        delegationType.setValidFrom(getADate(parseDate(vF)));
        delegationType.setValidTo(getADate(parseDate(vT)));

        list.add(delegationType);

        parameters.setDelegationBlockType(block);

        SaveDelegationsResponseType delegationsResponseType = saveDelegationsResponderInterface.saveDelegations(
                "", parameters);

        return delegationsResponseType.getDelegations().getContent().get(0).getDelegationKey();
    }

    protected Date parseDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    protected static XMLGregorianCalendar getADate(Date date) {
        GregorianCalendar dateValue = new GregorianCalendar();
        dateValue.setTime(date);
        try {
            XMLGregorianCalendar xmlValue = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateValue);
            return xmlValue;
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}
