package se.vgregion.delegation.domain;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import se.vgregion.delegation.util.DelegationUtil;

public class DelegationBlockTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void toDelegationBlock() {
        DelegationBlock obj = new DelegationBlock();
        obj.setId(100L);
        obj.setApprovedOn(new Date());

        Delegation delegation = new Delegation();
        obj.setId(101L);
        Delegation delegation2 = new Delegation();
        obj.setId(102L);
        obj.addDelegation(delegation);
        obj.addDelegation(delegation2);
        DelegationBlock result = DelegationUtil.toDelegationBlock(obj);

        Assert.assertEquals(obj, result);
    }

}
