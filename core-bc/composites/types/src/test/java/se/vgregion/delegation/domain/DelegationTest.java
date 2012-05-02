package se.vgregion.delegation.domain;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class DelegationTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void toDelegation() {
        Delegation obj = new Delegation();
        obj.setId(100L);
        Delegation result = Delegation.toDelegation(obj);
        Assert.assertEquals(obj, result);
    }

}
