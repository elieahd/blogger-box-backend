package com.dauphine.blogger.services.impl;

import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.models.Post;
import com.dauphine.blogger.repositories.CategoryRepository;
import com.dauphine.blogger.repositories.PostRepository;
import com.dauphine.blogger.services.PostService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public PostServiceImpl(PostRepository postRepository,
                           CategoryRepository categoryRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Post> getAll() {
        return postRepository.findAllByOrderByCreatedDateDesc();
    }

    @Override
    public List<Post> getAllByCategoryId(UUID categoryId) {
        return postRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public List<Post> getAllLikeTitleOrContent(String value) {
        return postRepository.findAllLikeTitleOrContentAndOrderByCreatedDateDesc(value);
    }

    @Override
    public Post getById(UUID id) {
        return postRepository.findById(id)
                .orElse(null);
    }

    @Override
    public Post create(String title,
                       String content,
                       UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElse(null);
        if (category == null) {
            return null;
        }
        Post post = new Post(title, content, category);
        return postRepository.save(post);
    }

    @Override
    public Post update(UUID id,
                       String title,
                       String content,
                       UUID categoryId) {
        Post post = getById(id);
        if (post == null) {
            return null;
        }
        Category category = categoryRepository.findById(categoryId)
                .orElse(null);
        if (category == null) {
            return null;
        }
        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        return postRepository.save(post);
    }

    @Override
    public void deleteById(UUID id) {
        postRepository.deleteById(id);
    }

}
