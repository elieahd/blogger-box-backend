package com.dauphine.blogger.controllers;

import com.dauphine.blogger.dto.PostRequest;
import com.dauphine.blogger.exceptions.CategoryNotFoundByIdException;
import com.dauphine.blogger.exceptions.PostNotFoundByIdException;
import com.dauphine.blogger.models.Post;
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
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam(required = false) String value) {
        List<Post> posts = value == null || value.isBlank()
                ? postService.getAll()
                : postService.getAllLikeTitleOrContent(value);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get post by id",
            description = "Retrieve post by id"
    )
    public ResponseEntity<Post> getById(@PathVariable UUID id) throws PostNotFoundByIdException {
        Post post = postService.getById(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    @Operation(
            summary = "Create new post",
            description = "Create new post, with title, content and category id"
    )
    public ResponseEntity<Post> create(@RequestBody PostRequest postRequest) throws CategoryNotFoundByIdException {
        Post post = postService.create(
                postRequest.getTitle(),
                postRequest.getContent(),
                postRequest.getCategoryId()
        );
        return ResponseEntity
                .created(URI.create("v1/posts/" + post.getId()))
                .body(post);
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Update existing post",
            description = "Update existing post's title, content and category id"
    )
    public ResponseEntity<Post> update(@PathVariable UUID id,
                                       @RequestBody PostRequest postRequest) throws CategoryNotFoundByIdException, PostNotFoundByIdException {
        Post post = postService.update(
                id,
                postRequest.getTitle(),
                postRequest.getContent(),
                postRequest.getCategoryId()
        );
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("{id}")
    @Operation(
            summary = "Delete an existing post by id",
            description = "Delete an existing post by id"
    )
    public ResponseEntity<Boolean> deleteById(@PathVariable UUID id) throws PostNotFoundByIdException {
        postService.deleteById(id);
        return ResponseEntity.accepted().body(true);
    }

}
