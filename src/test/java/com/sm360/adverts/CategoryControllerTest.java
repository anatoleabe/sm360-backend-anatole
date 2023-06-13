package com.sm360.adverts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sm360.adverts.controller.CategoryController;
import com.sm360.adverts.model.Category;
import com.sm360.adverts.repository.CategoryRepository;

/**
*
* @author anatoleabe
*/

@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    public void testGetAllCategories() {
        // create the list for simulation
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(UUID.randomUUID(), "Category A", 10));
        categories.add(new Category(UUID.randomUUID(), "Category B", 20));

        when(categoryRepository.findAll()).thenReturn(categories); // init the mock

        List<Category> result = categoryController.getAllCategories(); // call  getAllCategories method
        
        assertEquals(categories, result);// verify that it matches
    }

    @Test
    public void testGetCategoryById() {
        // create a mock category to be returned by the repository
        UUID categoryId = UUID.randomUUID();
        Category category = new Category(categoryId, "Category A", 10);

        // mock the repository's findById method to return the category
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // call the method with the category's ID and verify the result
        ResponseEntity<Category> result = categoryController.getCategoryById(categoryId);
        assertEquals(category, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    
    @Test
    public void testCreateCategory() {
        // create a mock category to be saved by the repository
        Category category = new Category(UUID.randomUUID(), "Category A", 10);

        // mock the repository's save method to return the category
        Mockito.when(categoryRepository.save(category)).thenReturn(category);

        // call the method with the category object and verify the result
        ResponseEntity<Category> result = categoryController.createCategory(category);
        assertEquals(category, result.getBody());
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }
    
    @Test
    public void testUpdateCategory() {
        // create a mock category to be updated by the repository
        UUID categoryId = UUID.randomUUID();
        Category category = new Category(categoryId, "Category A", 10);

        // mock the repository's findById method to return the category
        Mockito.when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // modify the category's name and tier limit
        category.setName("Category 2");
        category.setTierLimit(20);

        // call the method with the updated category object and verify the result
        ResponseEntity<Category> result = categoryController.updateCategory(categoryId, category);
        assertEquals(category, result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }
    
    @Test
    public void testDeleteCategory() {
        // create a mock category ID to be deleted by the repository
        UUID categoryId = UUID.randomUUID();

        // call the method with the category ID and verify the result
        ResponseEntity<HttpStatus> result = categoryController.deleteCategory(categoryId);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}