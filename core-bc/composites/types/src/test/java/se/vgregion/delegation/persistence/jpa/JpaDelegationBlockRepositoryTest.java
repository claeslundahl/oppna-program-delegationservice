/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

package se.vgregion.delegation.persistence.jpa;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import se.vgregion.delegation.domain.Delegation;
import se.vgregion.delegation.domain.DelegationBlock;
import se.vgregion.delegation.domain.DelegationStatus;
import se.vgregion.delegation.persistence.DelegationBlockRepository;
import se.vgregion.delegation.persistence.DelegationRepository;

/**
 * 
 * @author Simon Göransson
 * @author Claes Lundahl
 * 
 */
@ContextConfiguration("classpath:JpaRepositoryTest-context.xml")
public class JpaDelegationBlockRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Autowired
    private DelegationBlockRepository delegationBlockRepository;

    @Autowired
    private DelegationRepository delegationRepository;

    @Before
    public void setUp() throws Exception {
        executeSqlScript("classpath:dbsetup/test-data-delegation.sql", false);
    }

    @Test
    public void testFindAll() {
        Collection<DelegationBlock> delegations = delegationBlockRepository.findAll();
        assertEquals(1, delegations.size());

        for (DelegationBlock delegation : delegations) {
            System.out.println(delegation);
        }
    }

    @Test
    public void testStore() {

        int sizeBefor = delegationRepository.findAll().size();

        DelegationBlock delegationBlock = new DelegationBlock();
        // delegationBlock.setId(-1L);
        delegationBlock.setApprovedOn(new Date());
        delegationBlock.setDelegatedBy("db");
        delegationBlock
                .setSignToken("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");

        Delegation delegation = new Delegation();

        delegation.setDelegatedFor("df");
        delegation.setDelegateTo("dt");
        delegation.setValidTo(new Date());
        // delegation.setId(-4l);
        delegation.setValidFrom(new Date());
        delegation.setRole("role");
        delegation.setStatus(DelegationStatus.ACTIVE);
        delegation.setDelegationKey(-4L);

        delegationBlock.addDelegation(delegation);

        delegationBlockRepository.store(delegationBlock);

        int sizeAfter = delegationRepository.findAll().size();

        assertEquals(sizeBefor + 1, sizeAfter);

    }

    @Test
    public void testStoreTwoTimes() {
        int sizeBefor = delegationRepository.findAll().size();

        DelegationBlock delegationBlock = new DelegationBlock();
        // delegationBlock.setId(-1L);
        delegationBlock.setApprovedOn(new Date());
        delegationBlock.setDelegatedBy("db");
        delegationBlock.setSignToken("MeU8Cfja6a3dGPIswyc3vBcGLRjOVNzuecPDWYWbd6qRkkpmIhDem8TOKmUe2");
        Delegation delegation = new Delegation();

        delegation.setDelegatedFor("df");
        delegation.setDelegateTo("dt");
        delegation.setValidTo(new Date());
        // delegation.setId(-4l);
        delegation.setValidFrom(new Date());
        delegation.setRole("role");
        delegation.setStatus(DelegationStatus.ACTIVE);
        delegation.setDelegationKey(-4L);

        delegationBlock.addDelegation(delegation);

        DelegationBlock delegationBlock2 = new DelegationBlock();
        delegationBlock2.setApprovedOn(new Date());
        delegationBlock2.setDelegatedBy("db");
        delegationBlock2.setSignToken("MeU8Cfja6a3dGPIswyc3vBcGLRjOVNzuecPDWYWbd6qRkkpmIhDem8TOKmUe2");
        delegationBlock2.addDelegation(delegation);

        delegationBlockRepository.store(delegationBlock);
        delegationBlockRepository.store(delegationBlock2);

        int sizeAfter = delegationRepository.findAll().size();

        assertEquals(sizeBefor + 1, sizeAfter);
    }

}
