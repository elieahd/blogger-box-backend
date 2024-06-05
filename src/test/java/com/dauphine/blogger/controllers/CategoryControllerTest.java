package com.dauphine.blogger.controllers;

import com.dauphine.blogger.controllers.handlers.GlobalDefaultExceptionHandler;
import com.dauphine.blogger.dto.CategoryRequest;
import com.dauphine.blogger.exceptions.CategoryNameAlreadyExistsException;
import com.dauphine.blogger.exceptions.CategoryNotFoundByIdException;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.models.Post;
import com.dauphine.blogger.services.CategoryService;
import com.dauphine.blogger.services.PostService;
import com.dauphine.blogger.utils.JsonUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static com.dauphine.blogger.controllers.PostControllerTest.assertThatResultActionsHasPosts;
import static com.dauphine.blogger.utils.ObjectInitializer.categories;
import static com.dauphine.blogger.utils.ObjectInitializer.posts;
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

class CategoryControllerTest {

    private CategoryService categoryService;
    private PostService postService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        categoryService = mock(CategoryService.class);
        postService = mock(PostService.class);
        mockMvc = standaloneSetup(new CategoryController(categoryService, postService))
                .setControllerAdvice(new GlobalDefaultExceptionHandler())
                .build();
    }

    @Test
    void shouldReturnAllCategories() throws Exception {
        // Arrange
        List<Category> categories = categories();
        when(categoryService.getAll()).thenReturn(categories);
        // Act
        ResultActions actual = mockMvc.perform(get("/v1/categories"));
        // Assert
        actual.andExpect(status().isOk());
        assertThatResultActionsHasCategories(actual, categories);
        verify(categoryService, never()).getAllLikeName(any());
    }

    @Test
    void shouldReturnAllCategoriesWhenNameIsBlank() throws Exception {
        // Arrange
        List<Category> categories = categories();
        when(categoryService.getAll()).thenReturn(categories);
        // Act
        ResultActions actual = mockMvc.perform(get("/v1/categories?name= "));
        // Assert
        actual.andExpect(status().isOk());
        assertThatResultActionsHasCategories(actual, categories);
        verify(categoryService, never()).getAllLikeName(any());
    }

    @Test
    void shouldReturnAllCategoriesLikeName() throws Exception {
        // Arrange
        String name = "likeCategory";
        List<Category> categories = categories();
        when(categoryService.getAllLikeName(name)).thenReturn(categories);
        // Act
        ResultActions actual = mockMvc.perform(get("/v1/categories?name=" + name));
        // Assert
        actual.andExpect(status().isOk());
        assertThatResultActionsHasCategories(actual, categories);
        verify(categoryService, never()).getAll();
    }

    @Test
    void shouldReturnCategoryById() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        Category category = new Category("category");
        when(categoryService.getById(id)).thenReturn(category);
        // Act
        ResultActions actual = mockMvc.perform(get("/v1/categories/" + id));
        // Assert
        actual.andExpect(status().isOk());
        assertThatResultActionsHasCategory(actual, category);
    }

    @Test
    void shouldReturnCategoryNotFoundByIdResponse() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        when(categoryService.getById(id)).thenThrow(new CategoryNotFoundByIdException(id));
        // Act
        ResultActions actual = mockMvc.perform(get("/v1/categories/" + id));
        // Assert
        actual.andExpect(status().isNotFound());
        assertEquals("Category with id " + id + " not found", actual.andReturn().getResponse().getContentAsString());
    }

    @Test
    void shouldReturnCategoryNameAlreadyExistingWhenCreating() throws Exception {
        // Arrange
        CategoryRequest categoryRequest = categoryRequest("category1");
        when(categoryService.create(categoryRequest.getName())).thenThrow(new CategoryNameAlreadyExistsException(categoryRequest.getName()));
        // Act
        ResultActions actual = mockMvc.perform(
                post("/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.stringify(categoryRequest))
        );
        // Assert
        actual.andExpect(status().isBadRequest());
        assertEquals("Category " + categoryRequest.getName() + " already exists", actual.andReturn().getResponse().getContentAsString());
    }

    @Test
    void shouldReturnCreatedCategory() throws Exception {
        // Arrange
        CategoryRequest categoryRequest = categoryRequest("category1");
        Category category = new Category("category");
        when(categoryService.create(categoryRequest.getName())).thenReturn(category);
        // Act
        ResultActions actual = mockMvc.perform(
                post("/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.stringify(categoryRequest))
        );
        // Assert
        actual.andExpect(status().isCreated())
                .andExpect(header().string("Location", "v1/categories/" + category.getId()));
        assertThatResultActionsHasCategory(actual, category);
    }

    @Test
    void shouldReturnCategoryNameAlreadyExistingWhenUpdating() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryRequest categoryRequest = categoryRequest("category1");
        when(categoryService.update(id, categoryRequest.getName())).thenThrow(new CategoryNameAlreadyExistsException(categoryRequest.getName()));
        // Act
        ResultActions actual = mockMvc.perform(
                put("/v1/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.stringify(categoryRequest))
        );
        // Assert
        actual.andExpect(status().isBadRequest());
        assertEquals("Category " + categoryRequest.getName() + " already exists", actual.andReturn().getResponse().getContentAsString());
    }

    @Test
    void shouldReturnCategoryNotFoundByIsWhenUpdating() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryRequest categoryRequest = categoryRequest("category1");
        when(categoryService.update(id, categoryRequest.getName())).thenThrow(new CategoryNotFoundByIdException(id));
        // Act
        ResultActions actual = mockMvc.perform(
                put("/v1/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.stringify(categoryRequest))
        );
        // Assert
        actual.andExpect(status().isNotFound());
        assertEquals("Category with id " + id + " not found", actual.andReturn().getResponse().getContentAsString());
    }

    @Test
    void shouldReturnUpdatedCategory() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryRequest categoryRequest = categoryRequest("category2");
        Category category = new Category("category");
        when(categoryService.update(id, categoryRequest.getName())).thenReturn(category);
        // Act
        ResultActions actual = mockMvc.perform(
                put("/v1/categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.stringify(categoryRequest))
        );
        // Assert
        actual.andExpect(status().isOk());
        assertThatResultActionsHasCategory(actual, category);
    }

    @Test
    void shouldThrowCategoryNotFoundByIdWhenDeletingNonExistingCategory() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        doThrow(new CategoryNotFoundByIdException(id)).when(categoryService).deleteById(id);
        // Act
        ResultActions actual = mockMvc.perform(
                delete("/v1/categories/" + id)
        );
        // Assert
        actual.andExpect(status().isNotFound());
        assertEquals("Category with id " + id + " not found", actual.andReturn().getResponse().getContentAsString());
    }

    @Test
    void shouldReturnTrueWhenDeletingCategoryById() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        // Act
        ResultActions actual = mockMvc.perform(
                delete("/v1/categories/" + id)
        );
        // Assert
        actual.andExpect(status().isAccepted());
        assertEquals("true", actual.andReturn().getResponse().getContentAsString());
        verify(categoryService).deleteById(id);
    }

    @Test
    void shouldReturnPostsByCategoryId() throws Exception {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        List<Post> posts = posts();
        when(postService.getAllByCategoryId(categoryId)).thenReturn(posts);
        // Act
        ResultActions actual = mockMvc.perform(get("/v1/categories/" + categoryId + "/posts"));
        // Assert
        actual.andExpect(status().isOk());
        assertThatResultActionsHasPosts(actual, posts);
    }

    @Test
    void shouldThrowCategoryNotFoundByIdWhenGetPostsByCategoryId() throws Exception {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        when(postService.getAllByCategoryId(categoryId)).thenThrow(new CategoryNotFoundByIdException(categoryId));
        // Act
        ResultActions actual = mockMvc.perform(get("/v1/categories/" + categoryId + "/posts"));
        // Assert
        actual.andExpect(status().isNotFound());
        assertEquals("Category with id " + categoryId + " not found", actual.andReturn().getResponse().getContentAsString());
    }

    private CategoryRequest categoryRequest(String categoryName) {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName(categoryName);
        return categoryRequest;
    }

    private void assertThatResultActionsHasCategories(ResultActions actions, List<Category> categories) throws Exception {
        actions.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            actions.andExpect(jsonPath("$[" + i + "].id").value(category.getId().toString()))
                    .andExpect(jsonPath("$[" + i + "].name").value(category.getName()));
        }
    }

    private void assertThatResultActionsHasCategory(ResultActions actions, Category category) throws Exception {
        actions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(category.getId().toString()))
                .andExpect(jsonPath("$.name").value(category.getName()));
    }

}
