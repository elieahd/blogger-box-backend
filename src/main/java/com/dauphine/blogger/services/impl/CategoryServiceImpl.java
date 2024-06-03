package com.dauphine.blogger.services.impl;

import com.dauphine.blogger.exceptions.CategoryNameAlreadyExistsException;
import com.dauphine.blogger.exceptions.CategoryNotFoundByIdException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.repositories.CategoryRepository;
import com.dauphine.blogger.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> getAllLikeName(String name) {
        return categoryRepository.findAllLikeName(name);
    }

    @Override
    public Category getById(UUID id) throws CategoryNotFoundByIdException {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            return category.get();
        } else {
            throw new CategoryNotFoundByIdException(id);
        }
    }

    @Override
    public Category create(String name) throws CategoryNameAlreadyExistsException {
        Category category = new Category(name);
        if (categoryRepository.existsByName(name)) {
            throw new CategoryNameAlreadyExistsException(name);
        }
        return categoryRepository.save(category);
    }

    @Override
    public Category update(UUID id, String name) throws CategoryNotFoundByIdException, CategoryNameAlreadyExistsException {
        Category category = getById(id);
        if (!category.getName().equals(name) && categoryRepository.existsByName(name)) {
            throw new CategoryNameAlreadyExistsException(name);
        }
        category.setName(name);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteById(UUID id) throws CategoryNotFoundByIdException {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundByIdException(id);
        }
        categoryRepository.deleteById(id);
    }

}
