package com.sm360.adverts.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.sm360.adverts.model.Dealer;
import com.sm360.adverts.model.Listing;
import com.sm360.adverts.model.Listing.ListingState;
import com.sm360.adverts.repository.DealerRepository;
import com.sm360.adverts.repository.ListingRepository;

import jakarta.transaction.Transactional;

/**
 *
 * @author anatoleabe
 */

@RestController
@RequestMapping("/api/listings")
@Transactional
public class ListingController {


	@Autowired
	ListingRepository ListingRepository;

	@Autowired
	DealerRepository dealerRepository;

	private static final Logger logger = LoggerFactory.getLogger(ListingController.class);

	@GetMapping("")
	public ResponseEntity<List<Listing>> getAllListings(@RequestParam(required = false) ListingState state) {
		try {
			List<Listing> Listings = new ArrayList<Listing>();
			if (state == null)
				ListingRepository.findAll().forEach(Listings::add);
			else
				ListingRepository.findByState(state).forEach(Listings::add);

			if (Listings.isEmpty()) {
				logger.info("No listings were found");
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			logger.info("Retrieved {} listings", Listings.size());
			return new ResponseEntity<>(Listings, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("An error occurred while retrieving listings", e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Listing> getListingById(@PathVariable("id") UUID id) {
		try {
			Optional<Listing> ListingData = ListingRepository.findById(id);

			if (ListingData.isPresent()) {
				logger.info("Retrieved a listing with id {}", id);
				return new ResponseEntity<>(ListingData.get(), HttpStatus.OK);
			} else {
				logger.info("No listing found with id {}", id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error("An error occurred while retrieving listing with id {}", id, e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("")
	public ResponseEntity<Listing> createListing(@RequestBody Listing listing) {
		try {
			UUID dealerId = UUID.fromString(listing.getDealer().getId().toString());
			Dealer dealer = new Dealer(dealerId);
			dealer = dealerRepository.findById(dealerId).orElse(dealer);
			if (dealer.getId() == null) {
				logger.warn("A listing was created with a non-existent dealer id: {}", dealerId);
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
			listing.setDealer(dealer);
			Listing _Listing = ListingRepository.save(listing);
			logger.info("Created a new listing with id {}", _Listing.getId());
			return new ResponseEntity<>(_Listing, HttpStatus.CREATED);
		} catch (Exception e) {
			logger.error("An error occurred while creating a new listing", e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Listing> updateListing(@PathVariable("id") UUID id, @RequestBody Listing listing) {
		try {
			Optional<Listing> ListingData = ListingRepository.findById(id);
			if (ListingData.isPresent()) {
				UUID dealerId = UUID.fromString(listing.getDealer().getId().toString());
				Dealer dealer = dealerRepository.findById(dealerId).orElseThrow(() -> new IllegalArgumentException("Invalid dealer ID"));
				Listing _listing = ListingData.get();
				_listing.setVehicle(listing.getVehicle());
				_listing.setPrice(listing.getPrice());
				if (listing.getState() != null) {
					_listing.setState(listing.getState());
				}
				if (listing.getDealer() != null) {
					_listing.setDealer(dealer);
				}
				Listing updatedListing = ListingRepository.save(_listing);
				logger.info("Updated listing with id {}", updatedListing.getId());
				return new ResponseEntity<>(updatedListing, HttpStatus.OK);
			} else {
				logger.info("No listing found with id {}", id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (IllegalArgumentException e) {
			logger.warn("An invalid dealer ID was provided while updating the listing with id {}", id, e);
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("An error occurred while updating listing with id {}", id, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update the listing", e);
		}
	}

	/**
	 * Publish a listing
	 * @param id
	 * @return ResponseEntity<Listing>
	 */
	@PutMapping("/{id}/publish")
	public ResponseEntity<?> publishListing(@PathVariable("id") UUID id,
			@RequestParam(required = false) String tierLimitBehavior) {
		try {
			Optional<Listing> listingData = ListingRepository.findById(id);
			if (!listingData.isPresent()) {
				logger.info("No listing found with id {}", id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			Listing listing = listingData.get();
			if (listing.getState() == ListingState.PUBLISHED) {
				logger.info("Listing with id {} is already published", id);
				return new ResponseEntity<>("Listing is already published", HttpStatus.BAD_REQUEST);
			}
			Dealer dealer = listing.getDealer();
			List<Listing> dealerPublishedListings = ListingRepository.findByDealerAndStateOrderByCreatedAtAsc(dealer,
					ListingState.PUBLISHED);
			int publishedCount = dealerPublishedListings.size();
			int tierLimit = dealer.getCategory().getTierLimit();
			if (publishedCount < tierLimit) {
				listing.setState(ListingState.PUBLISHED);
				Listing updatedListing = ListingRepository.save(listing);
				logger.info("Published listing with id {}", id);
				return new ResponseEntity<>(updatedListing, HttpStatus.OK);
			} else {
				if (tierLimitBehavior != null && tierLimitBehavior.equals("unpublish")) {
					Listing oldestListing = dealerPublishedListings.get(0);
					oldestListing.setState(ListingState.DRAFT);
					ListingRepository.save(oldestListing);
					listing.setState(ListingState.PUBLISHED);
					Listing updatedListing = ListingRepository.save(listing);
					logger.info("Published listing with id {} and unpublished oldest listing with id {}", id, oldestListing.getId());
					return new ResponseEntity<>(updatedListing, HttpStatus.OK);
				} else {
					logger.info("Dealer tier limit reached while publishing listing with id {}", id);
					return new ResponseEntity<>("Dealer tier limit reached", HttpStatus.BAD_REQUEST);
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred while publishing listing with id {}", id, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to publish the listing", e);
		}
	}


	/**
	 * Unpublish a listing.
	 * @param id
	 * @return ResponseEntity<Listing>
	 */
	@PutMapping("/{id}/unpublish")
	public ResponseEntity<Listing> draftListing(@PathVariable("id") UUID id) {
		try {
			Optional<Listing> listingData = ListingRepository.findById(id);
			if (listingData.isPresent()) {
				Listing listing = listingData.get();
				listing.setState(ListingState.DRAFT);
				Listing updatedListing = ListingRepository.save(listing);
				logger.info("Unpublished listing with id {}", id);
				return new ResponseEntity<>(updatedListing, HttpStatus.OK);
			} else {
				logger.info("No listing found with id {}", id);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			logger.error("An error occurred while unpublishing listing with id {}", id, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to unpublish the listing", e);
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteListing(@PathVariable("id") String uuid) {
		try {
			UUID id = UUID.fromString(uuid);
			ListingRepository.deleteById(id);
			logger.info("Deleted listing with id {}", id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (IllegalArgumentException e) {
			logger.warn("An invalid UUID was provided while deleting a listing", e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error("An error occurred while deleting a listing with id {}", uuid, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete the listing", e);
		}
	}

	@DeleteMapping("")
	public ResponseEntity<HttpStatus> deleteAllListings() {
		try {
			ListingRepository.deleteAll();
			logger.info("Deleted all listings");
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			logger.error("An error occurred while deleting all listings", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete all listings", e);
		}
	}

	@GetMapping("/drafted")
	public ResponseEntity<List<Listing>> findByStateDrafted() {
		try {
			List<Listing> Listings = ListingRepository.findByState(ListingState.DRAFT);
			if (Listings.isEmpty()) {
				logger.info("No drafted listings found");
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			logger.info("Found {} drafted listings", Listings.size());
			return new ResponseEntity<>(Listings, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("An error occurred while finding drafted listings", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to find drafted listings", e);
		}
	}


	@GetMapping("/published")
	public ResponseEntity<List<Listing>> findByStatePublished() {
		try {
			List<Listing> Listings = ListingRepository.findByState(ListingState.PUBLISHED);

			if (Listings.isEmpty()) {
				logger.info("No published listings found");
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			logger.info("Found {} published listings", Listings.size());
			return new ResponseEntity<>(Listings, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("An error occurred while finding published listings", e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to find published listings", e);
		}
	}

	/**
	 * Get all listings of a dealer with a given state;
	 * @param dealerId
	 * @param state, optional
	 * @return
	 */
	@GetMapping("/dealer/{dealerId}")
	public ResponseEntity<List<Listing>> findByDealerAndState(@PathVariable UUID dealerId,
			@RequestParam(required = false) ListingState state) {
		try {
			Optional<Dealer> dealerData = dealerRepository.findById(dealerId);
			if (!dealerData.isPresent()) {
				logger.info("No dealer found with id {}", dealerId);
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			Dealer dealer = dealerData.get();
			List<Listing> listings = new ArrayList<Listing>();
			if (state == null) {
				listings = dealer.getListings();
			} else {
				for (Listing listing : dealer.getListings()) {
					if (listing.getState() == state) {
						listings.add(listing);
					}
				}
			}
			if (listings.isEmpty()) {
				logger.info("No listings found for dealer with id {} and state {}", dealerId, state);
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			logger.info("Found {} listings for dealer with id {} and state {}", listings.size(), dealerId, state);
			return new ResponseEntity<>(listings, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("An error occurred while finding listings for dealer with id {} and state {}", dealerId, state, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to find listings for dealer", e);
		}
	}
}


