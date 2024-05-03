package com.dauphine.blogger.models;

import java.util.UUID;

public class Category {

    private UUID id;
    private String name;

    public Category(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
    }

    public Category() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
