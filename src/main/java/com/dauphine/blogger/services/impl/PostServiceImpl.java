package com.dauphine.blogger.services.impl;

import com.dauphine.blogger.exceptions.CategoryNotFoundByIdException;
import com.dauphine.blogger.exceptions.PostNotFoundByIdException;
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
    public List<Post> getAllByCategoryId(UUID categoryId) throws CategoryNotFoundByIdException {
        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundByIdException(categoryId);
        }
        return postRepository.findAllByCategoryId(categoryId);
    }

    @Override
    public List<Post> getAllLikeTitleOrContent(String value) {
        return postRepository.findAllLikeTitleOrContentAndOrderByCreatedDateDesc(value);
    }

    @Override
    public Post getById(UUID id) throws PostNotFoundByIdException {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundByIdException(id));
    }

    @Override
    public Post create(String title,
                       String content,
                       UUID categoryId) throws CategoryNotFoundByIdException {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundByIdException(categoryId));
        Post post = new Post(title, content, category);
        return postRepository.save(post);
    }

    @Override
    public Post update(UUID id,
                       String title,
                       String content,
                       UUID categoryId) throws PostNotFoundByIdException, CategoryNotFoundByIdException {
        Post post = getById(id);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundByIdException(categoryId));
        post.setTitle(title);
        post.setContent(content);
        post.setCategory(category);
        return postRepository.save(post);
    }

    @Override
    public void deleteById(UUID id) throws PostNotFoundByIdException {
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundByIdException(id);
        }
        postRepository.deleteById(id);
    }

}
