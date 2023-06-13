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
import com.sm360.adverts.model.Dealer;
import com.sm360.adverts.repository.CategoryRepository;
import com.sm360.adverts.repository.DealerRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/dealers")
@Transactional
public class DealerController {

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private static final Logger logger = LoggerFactory.getLogger(DealerController.class);

    @GetMapping("")
    public List<Dealer> getAllDealers() {
        try {
            return dealerRepository.findAll();
        } catch (Exception e) {
            logger.error("Error occurred while retrieving all dealers", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve all dealers", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dealer> getDealerById(@PathVariable("id") UUID id) {
        try {
            Optional<Dealer> dealerData = dealerRepository.findById(id);
            if (dealerData.isPresent()) {
                return new ResponseEntity<>(dealerData.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error occurred while retrieving dealer by id: " + id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve dealer by id: " + id, e);
        }
    }

    @PostMapping("")
    public ResponseEntity<Dealer> createDealer(@RequestBody Dealer dealer) {
        try {
            UUID categoryId = UUID.fromString(dealer.getCategory().getId().toString());
            Category category = new Category(categoryId);
            category = categoryRepository.findById(categoryId).orElse(category);
            if (category.getId() == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            dealer.setCategory(category);
            Dealer _dealer = dealerRepository.save(dealer);
            return new ResponseEntity<>(_dealer, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error occurred while creating a dealer", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create a dealer", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dealer> updateDealer(@PathVariable("id") UUID id, @RequestBody Dealer dealer) {
        try {
            Optional<Dealer> dealerData = dealerRepository.findById(id);
            if (dealerData.isPresent()) {
                UUID categoryId = UUID.fromString(dealer.getCategory().getId().toString());
                Category category = new Category(categoryId);
                category = categoryRepository.findById(categoryId).orElse(category);
                if (category.getId() == null) {
                    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
                }
                Dealer _dealer = dealerData.get();
                _dealer.setName(dealer.getName());
                _dealer.setCategory(category);
                dealerRepository.save(_dealer);
                return new ResponseEntity<>(_dealer, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error occurred while updating dealer by id: " + id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update dealer by id: " + id, e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteDealer(@PathVariable("id") UUID id) {
        try {
            dealerRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error occurred while deleting dealer by id: " + id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete dealer by id: " + id, e);
        }
    }
}