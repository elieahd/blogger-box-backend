package com.dauphine.blogger.controllers;

import com.dauphine.blogger.dto.CategoryRequest;
import com.dauphine.blogger.exceptions.CategoryNameAlreadyExistsException;
import com.dauphine.blogger.exceptions.CategoryNotFoundByIdException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.models.Post;
import com.dauphine.blogger.services.CategoryService;
import com.dauphine.blogger.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/categories")
@Tag(
        name = "Categories API",
        description = "Categories endpoints"
)
public class CategoryController {

    private final CategoryService categoryService;
    private final PostService postService;

    public CategoryController(CategoryService categoryService,
                              PostService postService) {
        this.categoryService = categoryService;
        this.postService = postService;
    }

    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Retrieve all categories or filter like name"
    )
    public ResponseEntity<List<Category>> getAll(@RequestParam(required = false) String name) {
        List<Category> categories = name == null || name.isBlank()
                ? categoryService.getAll()
                : categoryService.getAllLikeName(name);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get category by id",
            description = "Retrieve a category by id"
    )
    public ResponseEntity<Category> getById(@PathVariable UUID id) throws CategoryNotFoundByIdException {
        Category category = categoryService.getById(id);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    @Operation(
            summary = "Create new category",
            description = "Create new category, only required field is the name of the category to create"
    )
    public ResponseEntity<Category> create(@RequestBody CategoryRequest categoryRequest) throws CategoryNameAlreadyExistsException {
        Category category = categoryService.create(categoryRequest.getName());
        return ResponseEntity
                .created(URI.create("v1/categories/" + category.getId()))
                .body(category);
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Update existing category",
            description = "Update the name of an existing category"
    )
    public ResponseEntity<Category> update(@PathVariable UUID id,
                                           @RequestBody CategoryRequest categoryRequest) throws CategoryNotFoundByIdException, CategoryNameAlreadyExistsException {
        Category category = categoryService.update(id, categoryRequest.getName());
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete existing category",
            description = "Delete existing category"
    )
    public ResponseEntity<Boolean> deleteById(@PathVariable UUID id) throws CategoryNotFoundByIdException {
        categoryService.deleteById(id);
        return ResponseEntity.accepted().body(true);
    }

    @GetMapping("{id}/posts")
    @Operation(
            summary = "Retrieve posts by category id",
            description = "Retrieve posts by category id"
    )
    public ResponseEntity<List<Post>> getPostsByCategoryId(@PathVariable UUID id) throws CategoryNotFoundByIdException {
        List<Post> posts = postService.getAllByCategoryId(id);
        return ResponseEntity.ok(posts);
    }

}
