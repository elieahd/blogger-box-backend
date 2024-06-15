package com.dauphine.blogger.controllers;

import com.dauphine.blogger.controllers.handlers.GlobalDefaultExceptionHandler;
import com.dauphine.blogger.dto.PostRequest;
import com.dauphine.blogger.exceptions.CategoryNotFoundByIdException;
import com.dauphine.blogger.exceptions.PostNotFoundByIdException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.models.Post;
import com.dauphine.blogger.services.PostService;
import com.dauphine.blogger.utils.JsonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.dauphine.blogger.utils.ObjectInitializer.posts;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class PostControllerTest {

    private PostService postService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        postService = mock(PostService.class);
        mockMvc = standaloneSetup(new PostController(postService))
                .setControllerAdvice(new GlobalDefaultExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnAllPosts() throws Exception {
        // Arrange
        List<Post> posts = posts();
        when(postService.getAll()).thenReturn(posts);
        // Act
        ResultActions actual = mockMvc.perform(get("/v1/posts"));
        // Assert
        actual.andExpect(status().isOk());
        assertThatResultActionsHasPosts(actual, posts);
        verify(postService, never()).getAllLikeTitleOrContent(any());
    }

    @Test
    void shouldReturnAllPostsWhenValueIsBlank() throws Exception {
        // Arrange
        List<Post> posts = posts();
        when(postService.getAll()).thenReturn(posts);
        // Act
        ResultActions actual = mockMvc.perform(get("/v1/posts?value= "));
        // Assert
        actual.andExpect(status().isOk());
        assertThatResultActionsHasPosts(actual, posts);
        verify(postService, never()).getAllLikeTitleOrContent(any());
    }

    @Test
    void shouldReturnAllPostsLikeTitleOrContent() throws Exception {
        // Arrange
        String value = "search filter";
        List<Post> posts = posts();
        when(postService.getAllLikeTitleOrContent(value)).thenReturn(posts);
        // Act
        ResultActions actual = mockMvc.perform(get("/v1/posts?value=" + value));
        // Assert
        actual.andExpect(status().isOk());
        assertThatResultActionsHasPosts(actual, posts);
        verify(postService, never()).getAll();
    }

    @Test
    void shouldReturnPostById() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        Post post = new Post("title", "content", new Category("category"));
        when(postService.getById(id)).thenReturn(post);
        // Act
        ResultActions actual = mockMvc.perform(
                get("/v1/posts/" + id)
        );
        // Assert
        actual.andExpect(status().isOk());
        assertThatResultActionsHasPost(actual, post);
    }

    @Test
    void shouldReturnPostNotFoundByIdResponse() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(postService.getById(id)).thenThrow(new PostNotFoundByIdException(id));
        // Act
        ResultActions actual = mockMvc.perform(
                get("/v1/posts/" + id)
        );
        // Assert
        actual.andExpect(status().isNotFound());
        assertEquals("Post with id " + id + " not found", actual.andReturn().getResponse().getContentAsString());
    }

    @Test
    void shouldReturnCreatedPost() throws Exception {
        // Arrange
        PostRequest postRequest = postRequest("title", "content", UUID.randomUUID());
        Post post = new Post("title", "content", new Category("category"));
        when(postService.create(postRequest.getTitle(), postRequest.getContent(), postRequest.getCategoryId())).thenReturn(post);
        // Act
        ResultActions actual = mockMvc.perform(
                post("/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.stringify(postRequest))
        );
        // Assert
        actual.andExpect(status().isCreated())
                .andExpect(header().string("Location", "v1/posts/" + post.getId()));
        assertThatResultActionsHasPost(actual, post);
    }

    @Test
    void shouldThrowCategoryNotFoundByIdWhenCreatingPostWithInvalidCategoryId() throws Exception {
        // Arrange
        PostRequest postRequest = postRequest("title", "content", UUID.randomUUID());
        when(postService.create(postRequest.getTitle(), postRequest.getContent(), postRequest.getCategoryId()))
                .thenThrow(new CategoryNotFoundByIdException(postRequest.getCategoryId()));
        // Act
        ResultActions actual = mockMvc.perform(
                post("/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.stringify(postRequest))
        );
        // Assert
        actual.andExpect(status().isNotFound());
        assertEquals("Category with id " + postRequest.getCategoryId() + " not found", actual.andReturn().getResponse().getContentAsString());
    }

    @Test
    void shouldThrowCategoryNotFoundByIdWhenUpdatingPostWithInvalidCategoryId() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        PostRequest postRequest = postRequest("title", "content", UUID.randomUUID());
        when(postService.update(id, postRequest.getTitle(), postRequest.getContent(), postRequest.getCategoryId())).thenThrow(new CategoryNotFoundByIdException(postRequest.getCategoryId()));
        // Act
        ResultActions actual = mockMvc.perform(
                put("/v1/posts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.stringify(postRequest))
        );
        // Assert
        actual.andExpect(status().isNotFound());
        assertEquals("Category with id " + postRequest.getCategoryId() + " not found", actual.andReturn().getResponse().getContentAsString());
    }

    @Test
    void shouldThrowPostNotFoundByIdWhenUpdatingNonExistingPost() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        PostRequest postRequest = postRequest("title", "content", UUID.randomUUID());
        when(postService.update(id, postRequest.getTitle(), postRequest.getContent(), postRequest.getCategoryId())).thenThrow(new PostNotFoundByIdException(id));
        // Act
        ResultActions actual = mockMvc.perform(
                put("/v1/posts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.stringify(postRequest))
        );
        // Assert
        actual.andExpect(status().isNotFound());
        assertEquals("Post with id " + id + " not found", actual.andReturn().getResponse().getContentAsString());
    }

    @Test
    void shouldReturnUpdatedPost() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        PostRequest postRequest = postRequest("title1", "content1", UUID.randomUUID());
        Post post = new Post("title", "content", new Category("category"));
        when(postService.update(id, postRequest.getTitle(), postRequest.getContent(), postRequest.getCategoryId())).thenReturn(post);
        // Act
        ResultActions actual = mockMvc.perform(
                put("/v1/posts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.stringify(postRequest))
        );
        // Assert
        actual.andExpect(status().isOk());
        assertThatResultActionsHasPost(actual, post);
    }

    @Test
    void shouldThrowPostNotFoundByIdWhenDeletingNonExistingPost() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new PostNotFoundByIdException(id)).when(postService).deleteById(id);
        // Act
        ResultActions actual = mockMvc.perform(
                delete("/v1/posts/" + id)
        );
        // Assert
        actual.andExpect(status().isNotFound());
        assertEquals("Post with id " + id + " not found", actual.andReturn().getResponse().getContentAsString());
    }

    @Test
    void shouldReturnTrueWhenDeletingPostById() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        // Act
        ResultActions actual = mockMvc.perform(
                delete("/v1/posts/" + id)
        );
        // Assert
        actual.andExpect(status().isAccepted());
        assertEquals("true", actual.andReturn().getResponse().getContentAsString());
        verify(postService).deleteById(id);
    }

    private PostRequest postRequest(String title,
                                    String content,
                                    UUID categoryId) {
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle(title);
        postRequest.setContent(content);
        postRequest.setCategoryId(categoryId);
        return postRequest;
    }

    public static void assertThatResultActionsHasPosts(ResultActions actions, List<Post> posts) throws Exception {
        actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        actions.andExpect(jsonPath("$", hasSize(posts.size())));
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            actions.andExpect(jsonPath("$[" + i + "].id").value(post.getId().toString()))
                    .andExpect(jsonPath("$[" + i + "].title").value(post.getTitle()))
                    .andExpect(jsonPath("$[" + i +"].content").value(post.getContent()))
                    .andExpect(jsonPath("$[" + i + "].createdDate").value(post.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))))
                    .andExpect(jsonPath("$[" + i + "].category.id").value(post.getCategory().getId().toString()))
                    .andExpect(jsonPath("$[" + i + "].category.name").value(post.getCategory().getName()));
        }
    }

    private void assertThatResultActionsHasPost(ResultActions actions, Post post) throws Exception {
        actions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(post.getId().toString()))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.createdDate").value(post.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))))
                .andExpect(jsonPath("$.category.id").value(post.getCategory().getId().toString()))
                .andExpect(jsonPath("$.category.name").value(post.getCategory().getName()));
    }

}
