package com.sm360.adverts.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sm360.adverts.model.Category;
import com.sm360.adverts.repository.CategoryRepository;

import jakarta.transaction.Transactional;

/**
*
* @author anatoleabe
*/

@RestController
@RequestMapping("/api/categories")
@Transactional
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @GetMapping("")
    public List<Category> getAllCategories() {
    	try {
    		return categoryRepository.findAll();
    	} catch (Exception e) {
    		logger.error("An error occurred while retrieving all categories", e);
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve all categories", e);
    	}
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") UUID id) {
    	try {
    		Optional<Category> categoryData = categoryRepository.findById(id);
    		if (categoryData.isPresent()) {
    			return new ResponseEntity<>(categoryData.get(), HttpStatus.OK);
    		} else {
    			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    		}
    	} catch (Exception e) {
    		logger.error("An error occurred while retrieving category by id: " + id, e);
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve category by id: " + id, e);
    	}
    }

    @PostMapping("")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
    	try {
    		Category _category = categoryRepository.save(category);
    		logger.info("Created category with id {}", _category.getId());
    		return new ResponseEntity<>(_category, HttpStatus.CREATED);
    	} catch (Exception e) {
    		logger.error("An error occurred while creating a category", e);
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create a category", e);
    	}
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") UUID id, @RequestBody Category category) {
    	try {
    		Optional<Category> categoryData = categoryRepository.findById(id);
    		if (categoryData.isPresent()) {
    			Category _category = categoryData.get();
    			_category.setName(category.getName());
    			_category.setTierLimit(category.getTierLimit());
    			categoryRepository.save(_category);
    			logger.info("Updated category with id {}", _category.getId());
    			return new ResponseEntity<>(_category, HttpStatus.OK);
    		} else {
    			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    		}
    	} catch (Exception e) {
    		logger.error("An error occurred while updating category by id {}", id, e);
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update category by id: " + id, e);
    	}
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable("id") UUID id) {
    	try {
    		categoryRepository.deleteById(id);
    		logger.info("Deleted category with id {}", id);
    		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	} catch (Exception e) {
    		logger.error("An error occurred while deleting category by id: " + id, e);
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete category by id: " + id, e);
    	}
    }
}