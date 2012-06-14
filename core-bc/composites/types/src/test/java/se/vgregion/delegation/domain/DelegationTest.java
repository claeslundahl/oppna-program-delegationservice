package se.vgregion.delegation.domain;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import se.riv.authorization.delegation.v1.DelegationType;
import se.vgregion.delegation.util.DelegationUtil;

public class DelegationTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void toDelegation() {
        DelegationType delegationType = new DelegationType();
        delegationType.setDelegationKey(100L);
        GregorianCalendar dateValue = new GregorianCalendar();
        try {
            XMLGregorianCalendar xmlValue = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateValue);
            delegationType.setValidTo(xmlValue);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }

        Delegation result = DelegationUtil.toDelegation(delegationType);
        Assert.assertEquals(new DelegationUtil.MyBeanMap(delegationType), new DelegationUtil.MyBeanMap(
                DelegationUtil.convert(result, DelegationType.class)));
    }

    @Test
    public void toDelegations() {
        Delegation obj = new Delegation();
        obj.setId(100L);

        List<Object> delegations = new ArrayList<Object>();
        delegations.add(obj);
        List<Delegation> result = DelegationUtil.toDelegations(delegations);
        Assert.assertEquals(delegations.get(0), result.get(0));
    }

}
