package se.vgregion.delegation.domain;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DelegationBlockTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    @Ignore
    public void toDelegationBlock() {
        DelegationBlock obj = new DelegationBlock();
        obj.setId(100L);
        DelegationBlock result = DelegationBlock.toDelegationBlock(obj);

        Assert.assertEquals(obj, result);
    }

}
