
package com.sm360.adverts.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

/**
 *
 * @author anatoleabe
 */

@Entity
@Data
@Table(name = "category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private String name;
    
    @Column(name = "tier_limit")
    private Integer tierLimit; //a number of published listings a dealer can have online

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Dealer> dealers;
    
    
    public Category() {}

    public Category(UUID id) {
        this.id = id;
    }
    
    public Category(String id) {
    	UUID categoryId = UUID.fromString(id);
        this.id = categoryId;
    }

    public Category(String name, Integer tierLimit) {
        this.name = name;
        this.tierLimit = tierLimit;
    }

	public Category(UUID id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Category(UUID id, String name, Integer tierLimit) {
		super();
		this.id = id;
		this.name = name;
		this.tierLimit = tierLimit;
	}
    
}
