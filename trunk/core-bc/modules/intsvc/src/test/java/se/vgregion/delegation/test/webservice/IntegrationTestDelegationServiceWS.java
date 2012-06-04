package se.vgregion.delegation.test.webservice;

/**
 * 
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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
import se.riv.authorization.delegation.hasdelegation.v1.rivtabp21.HasDelegationResponderInterface;
import se.riv.authorization.delegation.hasdelegationresponder.v1.HasDelegationResponseType;
import se.riv.authorization.delegation.hasdelegationresponder.v1.HasDelegationType;
import se.riv.authorization.delegation.removedelegation.v1.rivtabp21.RemoveDelegationResponderInterface;
import se.riv.authorization.delegation.removedelegationresponder.v1.RemoveDelegationResponseType;
import se.riv.authorization.delegation.removedelegationresponder.v1.RemoveDelegationType;
import se.riv.authorization.delegation.savedelegations.v1.rivtabp21.SaveDelegationsResponderInterface;
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

    GetActiveDelegationsResponderInterface activeDelegationsResponderInterface;
    GetDelegationResponderInterface getDelegationResponderInterface;
    GetInactiveDelegationsResponderInterface inactiveDelegationsResponderInterface;
    GetDelegationsbyUnitAndRoleResponderInterface getDelegationsbyUnitAndRoleResponderInterface;
    GetDelegationsResponderInterface getDelegationsResponderInterface;
    HasDelegationResponderInterface hasDelegationResponderInterface;
    SaveDelegationsResponderInterface saveDelegationsResponderInterface;
    RemoveDelegationResponderInterface removeDelegationResponderInterface;

    Server server;

    @Before
    public void setUp() throws Exception {

        // Server
        server = new Server();
        String serverPath = "classpath:/settings/serverConf.xml";

        server.startServer(serverPath, "localhost", "24004");

        logger.info("Server Ready !!!! ");

        // Client
        String clientPath = "classpath:/settings/clientConf.xml";
        ApplicationContext context = new ClassPathXmlApplicationContext(clientPath);

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
        removeDelegationResponderInterface =
                (RemoveDelegationResponderInterface) context.getBean("removeDelegationResponderInterface");

        logger.info("Client Ready !!!! ");
    }

    @After
    public void tearDown() throws Exception {
        server.stopServer();
    }

    @Test
    public void testSaveDelegationResponderInterface() {

        long responsedelegationKey = saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        Assert.assertTrue(responsedelegationKey == 1);

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

    @Test
    public void testRemoveDelegationResponderInterface() {

        long delegationKey = saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        RemoveDelegationType parameters = new RemoveDelegationType();
        parameters.setDelegationKey("" + delegationKey);

        RemoveDelegationResponseType removeDelegationResponseType =
                removeDelegationResponderInterface.removeDelegation("", parameters);

        Assert.assertTrue(removeDelegationResponseType.getResultCode().equals(
                se.riv.authorization.delegation.removedelegationresponder.v1.ResultCodeEnum.OK));
    }

    @Test
    public void testRemoveDelegationResponderInterface2() {

        long delegationKey = saveADelegation("2010/01/02", "2010/01/01", "2014/01/01");

        RemoveDelegationType parameters = new RemoveDelegationType();
        parameters.setDelegationKey("" + delegationKey);

        removeDelegationResponderInterface.removeDelegation("", parameters);

        GetDelegationType parameters2 = new GetDelegationType();

        parameters2.setDelegationKey("" + delegationKey);

        GetDelegationResponseType getDelegationResponseType =
                getDelegationResponderInterface.getDelegation("", parameters2);

        DelegationType delegationType = getDelegationResponseType.getDelegation();

        Assert.assertTrue(getDelegationResponseType.getResultCode().equals(
                se.riv.authorization.delegation.getdelegationresponder.v1.ResultCodeEnum.ERROR));
    }

    private long saveADelegation(String aO, String vF, String vT) {
        SaveDelegationsType parameters = new SaveDelegationsType();

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

        System.out.println("test - save - " + saveDelegationsResponderInterface.hashCode());

        SaveDelegationsResponseType delegationsResponseType =
                saveDelegationsResponderInterface.saveDelegations("", parameters);

        return delegationsResponseType.getDelegations().getContent().get(0).getDelegationKey();
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

}
