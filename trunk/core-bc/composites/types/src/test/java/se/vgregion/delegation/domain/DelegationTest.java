package se.vgregion.delegation.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.delegation.util.DelegationUtil;

public class DelegationTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void toDelegation() {
        Delegation obj = new Delegation();
        obj.setId(100L);
        obj.setValidTo(new Date());
        Delegation result = DelegationUtil.toDelegation(obj);
        Assert.assertEquals(obj, result);
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
