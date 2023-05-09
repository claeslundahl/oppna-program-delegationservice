package se.vgregion.delegation.delegation.view.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

/**
 * This class represents a delegation.
 *
 * @author Simon Göransson
 * @author Claes Lundahl
 */
@Entity
@Table(name = "vgr_delegation")
@Data
public class Delegation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "delegationkey")
    private Long delegationKey;

    @ManyToOne
    @JoinColumn(name="delegationblock_id", nullable=false)
    private DelegationBlock delegationBlock;

    @Column(name="delegatedfor", nullable = false)
    private String delegatedFor;

    @Column(name="delegateto", nullable = false)
    private String delegateTo;

    @Column(name="validfrom")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;

    @Column(name="validto")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validTo;

    @Column(name="role")
    private String role;

    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DelegationStatus status;

    @Column(name="information")
    private String information;

    @Column(name="delegatedforemail")
    private String delegatedForEmail;

    @Column(name="expiryalertsentcount")
    private Integer expiryAlertSentCount = 1;

}
