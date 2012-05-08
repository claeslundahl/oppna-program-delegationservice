package se.vgregion.delegation.domain;

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
        Delegation result = DelegationUtil.toDelegation(obj);
        Assert.assertEquals(obj, result);
    }

}
