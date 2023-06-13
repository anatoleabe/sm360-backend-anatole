
package com.sm360.adverts.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/**
 *
 * @author anatoleabe
 */
@Entity
@Data
@Table(name = "dealer")

public class Dealer implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;//Car dealer category. It contains a number of published listings a dealer can have online

    @OneToMany(mappedBy = "dealer")
    @JsonIgnore
    private List<Listing> listings;
    
    public Dealer() {}

    public Dealer(UUID id) {
        this.id = id;
    }
    
    public Dealer(String id) {
    	UUID dealerId = UUID.fromString(id);
        this.id = dealerId;
    }

    public Dealer(String name, Category category) {
        this.name = name;
        this.category = category;
    }

	public Dealer(UUID id, String name, Category category) {
		super();
		this.id = id;
		this.name = name;
		this.category = category;
	}
}
