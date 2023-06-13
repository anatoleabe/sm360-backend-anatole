package com.sm360.adverts.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sm360.adverts.model.Dealer;

/**
*
* @author anatoleabe
*/

public interface DealerRepository  extends JpaRepository<Dealer, Long> {
	
    List<Dealer> findByName(String name);
    
    @Modifying
    @Query(nativeQuery=true, value="DELETE FROM Dealer WHERE id = :id")
    void deleteById(@Param("id") UUID id);
    
    @Query("SELECT d FROM Dealer d WHERE d.id = :id")
    Optional<Dealer> findById(@Param("id") UUID id);
}
