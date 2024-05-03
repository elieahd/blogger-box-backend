package com.dauphine.blogger.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "Hello World API",
        description = "My first hello world endpoints"
)
public class HelloWorldController {

    @GetMapping("hello-world")
    @Operation(
            summary = "Hello world endpoint",
            description = "Returns 'Hello World!' as it is the first endpoint"
    )
    public String helloWorld() {
        return "Hello World!";
    }

    @GetMapping("hello-by-name")
    @Operation(
            summary = "Hello by name endpoint",
            description = "Returns 'Hello {name}' with the name being passed as a request parameter, meaning : '/hello-by-name?name={name}' in the URL"
    )
    public String helloByNameWithRequestParam(@RequestParam @Parameter(description = "Name to greet") String name) {
        return "Hello " + name;
    }

    @GetMapping("hello/{name}")
    @Operation(
            summary = "Hello by name endpoint",
            description = "Returns 'Hello {name}' with the name being passed as a path variable, meaning : '/hello/{name}' in the URL"
    )
    public String helloByNameWithPathVariable(@PathVariable @Parameter(description = "Name to greet") String name) {
        return "Hello " + name;
    }

}
