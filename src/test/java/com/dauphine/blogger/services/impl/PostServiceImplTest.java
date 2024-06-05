package com.dauphine.blogger.services.impl;

import com.dauphine.blogger.exceptions.CategoryNotFoundByIdException;
import com.dauphine.blogger.exceptions.PostNotFoundByIdException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.models.Post;
import com.dauphine.blogger.repositories.CategoryRepository;
import com.dauphine.blogger.repositories.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dauphine.blogger.utils.ObjectInitializer.posts;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostServiceImplTest {

    private PostRepository postRepository;
    private CategoryRepository categoryRepository;
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        postRepository = mock(PostRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        postService = new PostServiceImpl(postRepository, categoryRepository);
    }

    @Test
    void shouldReturnAllPosts() {
        // Arrange
        List<Post> posts = posts();
        when(postRepository.findAllByOrderByCreatedDateDesc()).thenReturn(posts);
        // Act
        List<Post> actual = postService.getAll();
        // Assert
        assertEquals(posts, actual);
    }

    @Test
    void shouldThrowCategoryNotFoundByIdExceptionWhenGetAllByNotFoundCategoryId() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        when(categoryRepository.existsById(categoryId)).thenReturn(false);
        List<Post> posts = posts();
        when(postRepository.findAllByCategoryId(categoryId)).thenReturn(posts);
        // Act
        CategoryNotFoundByIdException exception = assertThrows(
                CategoryNotFoundByIdException.class,
                () -> postService.getAllByCategoryId(categoryId)
        );
        // Assert
        assertEquals("Category with id " + categoryId + " not found", exception.getMessage());
        verify(postRepository, never()).findAllByCategoryId(any());
    }

    @Test
    void shouldReturnAllPostsByCategoryId() throws CategoryNotFoundByIdException {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        List<Post> posts = posts();
        when(postRepository.findAllByCategoryId(categoryId)).thenReturn(posts);
        // Act
        List<Post> actual = postService.getAllByCategoryId(categoryId);
        // Assert
        assertEquals(posts, actual);
    }

    @Test
    void shouldReturnAllPostsLikeTitleOrContent() {
        // Arrange
        String value = "post title";
        List<Post> posts = posts();
        when(postRepository.findAllLikeTitleOrContentAndOrderByCreatedDateDesc(value)).thenReturn(posts);
        // Act
        List<Post> actual = postService.getAllLikeTitleOrContent(value);
        // Assert
        assertEquals(posts, actual);
    }

    @Test
    void shouldReturnPostById() throws PostNotFoundByIdException {
        // Arrange
        UUID id = UUID.randomUUID();
        Post post = new Post("title1", "content1", new Category("category1"));
        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        // Act
        Post actual = postService.getById(id);
        // Assert
        assertEquals(post, actual);
    }

    @Test
    void shouldThrowPostNotFoundByIdExceptionWhenGetByIdNotFoundId() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(postRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        PostNotFoundByIdException exception = assertThrows(
                PostNotFoundByIdException.class,
                () -> postService.getById(id)
        );
        // Assert
        assertEquals("Post with id " + id + " not found", exception.getMessage());
    }

    @Test
    void shouldThrowCategoryNotFoundByIdExceptionWhenCreatingWithNonExistingCategoryId() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        String title = "title";
        String content = "content";
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        // Act
        CategoryNotFoundByIdException exception = assertThrows(
                CategoryNotFoundByIdException.class,
                () -> postService.create(title, content, categoryId)
        );
        // Assert
        assertEquals("Category with id " + categoryId + " not found", exception.getMessage());
        verify(postRepository, never()).save(any());
    }

    @Test
    void shouldCreatePost() throws CategoryNotFoundByIdException {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        String title = "title";
        String content = "content";
        Category category = new Category("categoryName");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Post post = new Post(title, content, new Category("category1"));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        // Act
        Post actual = postService.create(title, content, categoryId);
        // Assert
        assertEquals(post, actual);
    }

    @Test
    void shouldUpdatePost() throws CategoryNotFoundByIdException, PostNotFoundByIdException {
        // Arrange
        UUID id = UUID.randomUUID();
        String title = "title";
        String content = "content";
        UUID categoryId = UUID.randomUUID();
        Post postInDatabase = new Post(title, content, new Category("categoryName"));
        when(postRepository.findById(id)).thenReturn(Optional.of(postInDatabase));
        Category category = new Category("category in database");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        Post postUpdated = new Post(title, content, new Category("categoryName"));
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        when(postRepository.save(postCaptor.capture())).thenReturn(postUpdated);
        // Act
        Post actual = postService.update(id, title, content, categoryId);
        // Assert
        assertEquals(postUpdated, actual);
        Post postToSave = postCaptor.getValue();
        assertEquals(postInDatabase.getId(), postToSave.getId());
        assertEquals(title, postToSave.getTitle());
        assertEquals(content, postToSave.getContent());
        assertEquals(category, postToSave.getCategory());
        assertEquals(postInDatabase.getCreatedDate(), postToSave.getCreatedDate());
    }

    @Test
    void shouldThrowPostNotFoundByIdWhenUpdatingWithNonExistingPostId() {
        // Arrange
        UUID id = UUID.randomUUID();
        String title = "title";
        String content = "content";
        UUID categoryId = UUID.randomUUID();
        when(postRepository.findById(id)).thenReturn(Optional.empty());
        // Act
        PostNotFoundByIdException exception = assertThrows(
                PostNotFoundByIdException.class,
                () -> postService.update(id, title, content, categoryId)
        );
        // Assert
        assertEquals("Post with id " + id + " not found", exception.getMessage());
        verify(categoryRepository, never()).findById(any());
        verify(postRepository, never()).save(any());
    }

    @Test
    void shouldThrowCategoryNotFoundByIdWhenUpdatingWithNonExistingCategoryId() {
        // Arrange
        UUID id = UUID.randomUUID();
        String title = "title";
        String content = "content";
        UUID categoryId = UUID.randomUUID();
        when(postRepository.findById(id)).thenReturn(Optional.of(new Post(title, content, new Category("category1"))));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        // Act
        CategoryNotFoundByIdException exception = assertThrows(
                CategoryNotFoundByIdException.class,
                () -> postService.update(id, title, content, categoryId)
        );
        // Assert
        assertEquals("Category with id " + categoryId + " not found", exception.getMessage());
        verify(postRepository, never()).save(any());
    }
    // update

    @Test
    void shouldDeleteById() throws PostNotFoundByIdException {
        // Arrange
        UUID id = UUID.randomUUID();
        when(postRepository.existsById(id)).thenReturn(true);
        // Act
        postService.deleteById(id);
        // Assert
        verify(postRepository).deleteById(id);
    }

    @Test
    void shouldThrowNotFoundByIdExceptionWhenDeletingWithNonExistingId() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(postRepository.existsById(id)).thenReturn(false);
        // Act
        PostNotFoundByIdException exception = assertThrows(
                PostNotFoundByIdException.class,
                () -> postService.deleteById(id)
        );
        // Assert
        assertEquals("Post with id " + id + " not found", exception.getMessage());
        verify(postRepository, never()).deleteById(any());
    }

}
