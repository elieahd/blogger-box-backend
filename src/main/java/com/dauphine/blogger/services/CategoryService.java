package com.dauphine.blogger.services;

import com.dauphine.blogger.models.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<Category> getAll();

    List<Category> getAllLikeName(String name);

    Category getById(UUID id);

    Category create(String name);

    Category update(UUID id, String name);

    void deleteById(UUID id);
}
