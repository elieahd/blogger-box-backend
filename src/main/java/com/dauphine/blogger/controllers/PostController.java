package com.dauphine.blogger.controllers;

import com.dauphine.blogger.dto.PostRequest;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.models.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/posts")
@Tag(
        name = "Posts API",
        description = "Posts endpoints"
)
public class PostController {

    private final List<Post> posts;

    public PostController() {
        posts = new ArrayList<>();
        posts.add(
                new Post(
                        "Navigating the Complexities of International Adoption",
                        "This article explores the intricacies of international adoption, including cultural considerations, legal requirements, and the challenges faced by families embarking on this journey. It offers valuable insights and practical advice for those considering adopting a child from another country.",
                        new Category("Adoption")
                )
        );
        posts.add(
                new Post(
                        "Understanding Childhood Anxiety",
                        "Childhood anxiety is a common concern that can impact a child well-being and functioning. This article delves into the various types of childhood anxiety, signs and symptoms to look out for, and strategies for parents and caregivers to support anxious children effectively.",
                        new Category("Children")
                )
        );
        posts.add(
                new Post(
                        "Meditation for Beginners",
                        "For those new to meditation, this article provides a comprehensive guide on how to get started. It covers basic meditation techniques, tips for finding a comfortable posture, and guidance on establishing a regular meditation routine.",
                        new Category("Meditation")
                )
        );
    }

    @GetMapping
    @Operation(
            summary = "Get all posts",
            description = "Retrieve all posts ordered by creation date desc"
    )
    public List<Post> getAllPosts() {
        return posts;
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get post by id",
            description = "Retrieve post by id"
    )
    public Post getById(@PathVariable UUID id) {
        return posts.stream()
                .filter(post -> post.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @PostMapping
    @Operation(
            summary = "Create new post",
            description = "Create new post, with title, content and category id"
    )
    public Post create(@RequestBody PostRequest postRequest) {
        Category category = new Category("Adoption");
        category.setId(postRequest.getCategoryId());
        Post post = new Post(
                postRequest.getTitle(),
                postRequest.getContent(),
                category
        );
        posts.add(post);
        return post;
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Update existing post",
            description = "Update existing post's title, content and category id"
    )
    public Post update(@PathVariable UUID id,
                       @RequestBody PostRequest postRequest) {
        posts.stream()
                .filter(post -> post.getId().equals(id))
                .findFirst()
                .ifPresent(post -> {
                    post.setTitle(postRequest.getTitle());
                    post.setContent(postRequest.getContent());
                    post.getCategory().setId(postRequest.getCategoryId());
                });
        return getById(id);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete an existing post by id",
            description = "Delete an existing post by id"
    )
    public boolean deleteById(@PathVariable UUID id) {
        posts.removeIf(post -> post.getId().equals(id));
        return true;
    }

}
