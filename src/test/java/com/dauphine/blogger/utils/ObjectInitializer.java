package com.dauphine.blogger.utils;

import com.dauphine.blogger.models.Category;
import com.dauphine.blogger.models.Post;

import java.util.List;

public class ObjectInitializer {

    public static List<Post> posts() {
        return List.of(
                new Post("title1", "content1", new Category("category1")),
                new Post("title2", "content2", new Category("category2")),
                new Post("title3", "content3", new Category("category3"))
        );
    }

    public static List<Category> categories() {
        return List.of(
                new Category("category1"),
                new Category("category2"),
                new Category("category3")
        );
    }

}
