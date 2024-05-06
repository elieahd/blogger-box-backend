
package com.dauphine.blogger.services.impl;

import com.dauphine.blogger.exceptions.CategoryNotFoundByIdException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CategoryServiceImplTest {

    private CategoryRepository categoryRepository;
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryServiceImpl(categoryRepository);
    }

    @Test
    void shouldReturnCategoryWhenIdExists() throws CategoryNotFoundByIdException {
        // Arrange
        UUID id = UUID.randomUUID();
        Category expected = new Category("Category");
        when(categoryRepository.findById(id)).thenReturn(Optional.of(expected));
        // Act
        Category actual = categoryService.getById(id);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenNotFoundById() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(categoryRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        CategoryNotFoundByIdException exception = assertThrows(
                CategoryNotFoundByIdException.class,
                () -> categoryService.getById(id)
        );
        // Assert
        assertEquals("Category with id " + id + " not found", exception.getMessage());
    }

}
