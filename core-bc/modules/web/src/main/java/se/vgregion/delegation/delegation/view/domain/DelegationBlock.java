package se.vgregion.delegation.delegation.view.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a delegation block.
 *
 * @author Simon Göransson
 * @author Claes Lundahl
 */
@Entity
@Table(name = "vgr_delegation_block")
@Data
public class DelegationBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "signtoken", length = 20000)
    private String signToken;

    @Column(name = "delegatedby")
    private String delegatedBy;

    @OneToMany(mappedBy = "delegationBlock", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Delegation> delegations = new HashSet<Delegation>();

    @Column(name = "approvedon", nullable = true, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date approvedOn;

}
