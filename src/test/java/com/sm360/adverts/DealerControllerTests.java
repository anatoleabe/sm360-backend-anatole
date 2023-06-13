package com.sm360.adverts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sm360.adverts.controller.DealerController;
import com.sm360.adverts.model.Category;
import com.sm360.adverts.model.Dealer;
import com.sm360.adverts.repository.CategoryRepository;
import com.sm360.adverts.repository.DealerRepository;


/**
*
* @author anatoleabe
*/


@ExtendWith(MockitoExtension.class)
public class DealerControllerTests {

    @Mock
    private DealerRepository dealerRepository;
    
    @InjectMocks
    private DealerController dealerController;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    public void testGetAllDealers() {
        // create a mock list of dealers to be returned by the controller
        List<Dealer> dealers = new ArrayList<>();
        dealers.add(new Dealer(UUID.randomUUID(), "Dealer A", new Category(UUID.randomUUID())));
        dealers.add(new Dealer(UUID.randomUUID(), "Dealer 2", new Category(UUID.randomUUID())));

        // mock the repository's findAll method to return the list of dealers
        Mockito.when(dealerRepository.findAll()).thenReturn(dealers);

        // call the method and verify the result
        List<Dealer> result = dealerController.getAllDealers();
        assertEquals(dealers, result);
    }

    @Test
    public void testGetDealerById() {
        // create a mock dealer to be returned by the repository
        UUID dealerId = UUID.randomUUID();
        Dealer dealer = new Dealer(dealerId, "Dealer A", new Category(UUID.randomUUID()));

        // mock the repository's findById method to return the dealer
        Mockito.when(dealerRepository.findById(dealerId)).thenReturn(Optional.of(dealer));

        // call the method with the dealer's ID and verify the result
        ResponseEntity<Dealer> result = dealerController.getDealerById(dealerId);
        assertEquals(dealer, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testCreateDealer() {
        // create a mock dealer to be saved by the repository
        Dealer dealer = new Dealer(UUID.randomUUID(), "Dealer A", new Category(UUID.randomUUID()));

        // create a mock category to be returned by the repository
        UUID categoryId = UUID.randomUUID();
        Category category = new Category(categoryId);

        // mock the repository's findById method to return the category
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // mock the repository's save method to return the dealer
        Mockito.when(dealerRepository.save(dealer)).thenReturn(dealer);

        // call the method with the dealer object and verify the result
        ResponseEntity<Dealer> result = dealerController.createDealer(dealer);
        assertEquals(dealer, result.getBody());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    public void testUpdateDealer() {
        // create a mock dealer to be updated by the repository
        UUID dealerId = UUID.randomUUID();
        Dealer dealer = new Dealer(dealerId, "Dealer A", new Category(UUID.randomUUID()));

        // create a mock category to be returned by the repository
        UUID categoryId = UUID.randomUUID();
        Category category = new Category(categoryId);

        // mock the repository's findById method to return the dealer and category
        Mockito.when(dealerRepository.findById(dealerId)).thenReturn(Optional.of(dealer));
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // modify the dealer's name and category
        dealer.setName("Dealer 2");
        dealer.setCategory(category);

        // call the method with the updated dealer object and verify the result
        ResponseEntity<Dealer> result = dealerController.updateDealer(dealerId, dealer);
        assertEquals(dealer, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testDeleteDealer() {
        // create a mock dealer ID to be deleted by the repository
        UUID dealerId = UUID.randomUUID();

        // call the method with the dealer ID and verify the result
        ResponseEntity<HttpStatus> result = dealerController.deleteDealer(dealerId);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
