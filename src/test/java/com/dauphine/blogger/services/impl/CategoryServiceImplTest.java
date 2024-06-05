
package com.dauphine.blogger.services.impl;

import com.dauphine.blogger.exceptions.CategoryNameAlreadyExistsException;
import com.dauphine.blogger.exceptions.CategoryNotFoundByIdException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dauphine.blogger.utils.ObjectInitializer.categories;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    void shouldReturnCategoriesWhenGetAll() {
        // Arrange
        List<Category> expected = categories();
        when(categoryRepository.findAll()).thenReturn(expected);
        // Act
        List<Category> actual = categoryService.getAll();
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnCategoriesWhenGetAllLikeName() {
        // Arrange
        String name = "category name";
        List<Category> expected = categories();
        when(categoryRepository.findAllLikeName(name)).thenReturn(expected);
        // Act
        List<Category> actual = categoryService.getAllLikeName(name);
        // Assert
        assertEquals(expected, actual);
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

    @Test
    void shouldReturnCreatedCategory() throws CategoryNameAlreadyExistsException {
        // Arrange
        String name = "existing name";
        when(categoryRepository.existsByName(name)).thenReturn(false);
        Category expected = new Category(name);
        when(categoryRepository.save(any(Category.class))).thenReturn(expected);
        // Act
        Category actual = categoryService.create(name);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenCreatingWithExistingName() {
        // Arrange
        String name = "existing name";
        when(categoryRepository.existsByName(name)).thenReturn(true);
        // Act
        CategoryNameAlreadyExistsException exception = assertThrows(
                CategoryNameAlreadyExistsException.class,
                () -> categoryService.create(name)
        );
        // Assert
        assertEquals("Category " + name + " already exists", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowCategoryNameAlreadyExistsWhenNewCategoryNameIsAlreadyACategory() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "new category name";
        Category existingCategory = new Category("old name");
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName(name)).thenReturn(true);
        // Act
        CategoryNameAlreadyExistsException exception = assertThrows(
                CategoryNameAlreadyExistsException.class,
                () -> categoryService.update(id, name)
        );
        // Assert
        assertEquals("Category " + name + " already exists", exception.getMessage());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void shouldReturnUpdatedCategoryWithoutCheckingIfNameExists() throws CategoryNotFoundByIdException, CategoryNameAlreadyExistsException {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "new category name";
        Category existingCategory = new Category(name);
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        existingCategory.setName(name);
        Category expected = new Category(name);
        when(categoryRepository.save(existingCategory)).thenReturn(expected);
        // Act
        Category actual = categoryService.update(id, name);
        // Assert
        assertEquals(expected, actual);
        verify(categoryRepository, never()).existsByName(any());
    }

    @Test
    void shouldReturnUpdatedCategory() throws CategoryNotFoundByIdException, CategoryNameAlreadyExistsException {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "new category name";
        Category existingCategory = new Category("old name");
        when(categoryRepository.findById(id)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName(name)).thenReturn(false);
        existingCategory.setName(name);
        Category expected = new Category(name);
        when(categoryRepository.save(existingCategory)).thenReturn(expected);
        // Act
        Category actual = categoryService.update(id, name);
        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void shouldDeleteById() throws CategoryNotFoundByIdException {
        // Arrange
        UUID id = UUID.randomUUID();
        when(categoryRepository.existsById(id)).thenReturn(true);
        // Act
        categoryService.deleteById(id);
        // Assert
        verify(categoryRepository).deleteById(id);
    }

    @Test
    void shouldThrowNotFoundByIdExceptionWhenDeletingWithNonExistingId() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(categoryRepository.existsById(id)).thenReturn(false);
        // Act
        CategoryNotFoundByIdException exception = assertThrows(
                CategoryNotFoundByIdException.class,
                () -> categoryService.deleteById(id)
        );
        // Assert
        assertEquals("Category with id " + id + " not found", exception.getMessage());
        verify(categoryRepository, never()).deleteById(any());
    }

}
