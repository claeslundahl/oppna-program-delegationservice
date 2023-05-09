package se.vgregion.delegation.delegation.view.repos;


import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.vgregion.delegation.delegation.view.domain.Delegation;

import java.util.List;

public interface DelegationRepo  extends JpaRepository<Delegation, Long> {

    @Query("select d from Delegation d where d.validFrom < CURRENT_DATE and d.validTo > current_date")
    List<Delegation> findAllActive();



}
