
package com.sm360.adverts.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sm360.adverts.model.Dealer;
import com.sm360.adverts.model.Listing;
import com.sm360.adverts.model.Listing.ListingState;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    
    List<Listing> findByState(ListingState state);
    
    List<Listing> findByVehicleContaining(String vehicle);
    @Modifying
    @Query(nativeQuery=true, value="DELETE FROM Listing WHERE id = :id")
    void deleteById(@Param("id") UUID id);
    
    @Query("SELECT l FROM Listing l WHERE l.id = :id")
    Optional<Listing> findById(@Param("id") UUID id);
    
    List<Listing> findByDealerAndStateOrderByCreatedAtAsc(Dealer dealer, ListingState state);

}
