package com.sm360.adverts.model;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;


/**
 *
 * @author anatoleabe
 */
@Entity
@Data
@Table(name = "listing")
public class Listing {
	public enum ListingState {
	    DRAFT,
	    PUBLISHED
	}
	
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "dealer_id")
    private Dealer dealer;
    
    private String vehicle;
    private Double price;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;
    
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(9)")
    private ListingState state;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.state = ListingState.DRAFT; //All the created listings should have state draft by default;
    }
    
}


