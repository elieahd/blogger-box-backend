package com.dauphine.blogger.repositories;

import com.dauphine.blogger.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByCategoryId(UUID categoryId);

    @Query("""
        SELECT post
        FROM Post post
        WHERE UPPER(post.title) LIKE UPPER(CONCAT('%', :value, '%'))
        OR UPPER(post.content) LIKE UPPER(CONCAT('%', :value, '%'))
        ORDER BY post.createdDate DESC
    """)
    List<Post> findAllLikeTitleOrContentAndOrderByCreatedDateDesc(@Param("value") String value);

    List<Post> findAllByOrderByCreatedDateDesc();

}
