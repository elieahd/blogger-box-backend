package com.dauphine.blogger.controllers;

import com.dauphine.blogger.dto.PostRequest;
import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.models.Post;
import com.dauphine.blogger.services.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("v1/posts")
@Tag(
        name = "Posts API",
        description = "Posts endpoints"
)
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    @Operation(
            summary = "Get all posts",
            description = "Retrieve all posts ordered by creation date desc and ability to filter by title or content"
    )
    public List<Post> getAllPosts(@RequestParam(required = false) String value) {
        List<Post> posts = value == null || value.isBlank()
                ? postService.getAll()
                : postService.getAllLikeTitleOrContent(value);
        return posts;
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get post by id",
            description = "Retrieve post by id"
    )
    public Post getById(@PathVariable UUID id) {
        Post post = postService.getById(id);
        return post;
    }

    @PostMapping
    @Operation(
            summary = "Create new post",
            description = "Create new post, with title, content and category id"
    )
    public Post create(@RequestBody PostRequest postRequest) {
        Post post = postService.create(
                postRequest.getTitle(),
                postRequest.getContent(),
                postRequest.getCategoryId()
        );
        return post;
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Update existing post",
            description = "Update existing post's title, content and category id"
    )
    public Post update(@PathVariable UUID id,
                       @RequestBody PostRequest postRequest) {
        Post post = postService.update(
                id,
                postRequest.getTitle(),
                postRequest.getContent(),
                postRequest.getCategoryId()
        );
        return post;
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete an existing post by id",
            description = "Delete an existing post by id"
    )
    public boolean deleteById(@PathVariable UUID id) {
        postService.deleteById(id);
        return true;
    }

}
