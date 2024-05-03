package com.dauphine.blogger.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("hello-world")
    public String helloWorld() {
        return "Hello World!";
    }

    @GetMapping("hello-by-name")
    public String helloByNameWithRequestParam(@RequestParam String name){
        return "Hello " + name;
    }

    @GetMapping("hello/{name}")
    public String helloByNameWithPathVariable(@PathVariable String name){
        return "Hello " + name;
    }

}
