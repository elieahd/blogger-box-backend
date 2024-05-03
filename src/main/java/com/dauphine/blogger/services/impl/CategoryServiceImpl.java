package com.dauphine.blogger.services.impl;

import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.services.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final List<Category> categories;

    public CategoryServiceImpl() {
        categories = new ArrayList<>();
        categories.add(new Category("Adoption"));
        categories.add(new Category("Children"));
        categories.add(new Category("Dating"));
        categories.add(new Category("Love"));
        categories.add(new Category("Anxiety"));
        categories.add(new Category("Education"));
        categories.add(new Category("Java"));
    }

    @Override
    public List<Category> getAll() {
        return categories;
    }

    @Override
    public Category getById(UUID id) {
        return categories.stream()
                .filter(category -> category.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Category create(String name) {
        return new Category(name);
    }

    @Override
    public Category update(UUID id, String name) {
        categories.stream()
                .filter(category -> category.getId().equals(id))
                .findFirst()
                .ifPresent(category -> category.setName(name));
        return getById(id);
    }

    @Override
    public void deleteById(UUID id) {
        categories.removeIf(category -> category.getId().equals(id));
    }

}
