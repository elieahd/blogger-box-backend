package com.dauphine.blogger.repositories;

import com.dauphine.blogger.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByCategoryId(UUID categoryId);

}
