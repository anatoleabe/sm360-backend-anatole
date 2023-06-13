
package com.sm360.adverts.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sm360.adverts.model.Category;

/**
*
* @author anatoleabe
*/

public interface CategoryRepository  extends JpaRepository<Category, Long> {
	
    List<Category> findByName(String name);
    
    List<Category> findByTierLimit(int tierLimit);
    
    
    @Modifying
    @Query(nativeQuery=true, value="DELETE FROM Category WHERE id = :id")
    void deleteById(@Param("id") UUID id);
    
    @Query("SELECT c FROM Category c WHERE c.id = :id")
    Optional<Category> findById(@Param("id") UUID id);

}
