package se.vgregion.delegation.domain;

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
        DelegationBlock result = DelegationUtil.toDelegationBlock(obj);

        Assert.assertEquals(obj, result);
    }

}
