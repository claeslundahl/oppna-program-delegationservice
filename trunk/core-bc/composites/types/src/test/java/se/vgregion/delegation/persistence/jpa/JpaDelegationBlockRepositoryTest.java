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
        delegationBlock.setSignToken("st");

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

}
