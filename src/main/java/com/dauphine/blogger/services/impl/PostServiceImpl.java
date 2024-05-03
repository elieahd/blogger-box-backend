package com.dauphine.blogger.services.impl;

import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.models.Post;
import com.dauphine.blogger.services.PostService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private final List<Post> posts;

    public PostServiceImpl() {
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

    @Override
    public List<Post> getAll() {
        return posts;
    }

    @Override
    public List<Post> getAllByCategoryId(UUID categoryId) {
        return posts.stream()
                .filter(post -> post.getCategory().getId().equals(categoryId))
                .toList();
    }

    @Override
    public Post getById(UUID id) {
        return posts.stream()
                .filter(post -> post.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Post create(String title,
                       String content,
                       UUID categoryId) {
        Category category = new Category("Adoption");
        category.setId(categoryId);
        Post post = new Post(title, content, category);
        posts.add(post);
        return post;
    }

    @Override
    public Post update(UUID id,
                       String title,
                       String content,
                       UUID categoryId) {
        posts.stream()
                .filter(post -> post.getId().equals(id))
                .findFirst()
                .ifPresent(post -> {
                    post.setTitle(title);
                    post.setContent(content);
                    post.getCategory().setId(categoryId);
                });
        return getById(id);
    }

    @Override
    public void deleteById(UUID id) {
        posts.removeIf(post -> post.getId().equals(id));
    }

}
