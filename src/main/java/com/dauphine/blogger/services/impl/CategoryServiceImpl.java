package com.dauphine.blogger.services.impl;

import com.dauphine.blogger.exceptions.CategoryNotFoundByIdException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.repositories.CategoryRepository;
import com.dauphine.blogger.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundByIdException(id));
    }

    @Override
    public Category create(String name) {
        Category category = new Category(name);
        return categoryRepository.save(category);
    }

    @Override
    public Category update(UUID id, String name) throws CategoryNotFoundByIdException {
        Category category = getById(id);
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
