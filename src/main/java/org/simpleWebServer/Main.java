package org.simpleWebServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// This annotation instructs Spring to initialize its configuration - which is needed to start a new application
@SpringBootApplication
// Indicates that this class contains RESTful methods to handle incoming HTTP requests
@RestController
public class Main {
    // Following this tutorial: https://www.sohamkamani.com/java/spring-rest-http-server/#google_vignette
    /* TODO:
        1. Auth: Store secrets somewhere (not in code) and use request headers.
        2. Async: Enable concurrent requests to the web-server.
        3. Timeout: Introduce artificial lag with sleeps to simulate retries?
        3. Retries: ___.
     */

    // We can start our application by calling the run method with the primary class
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // The `GetMapping` annotation indicates that this method should be called
    // when handling GET requests to the "/simple-request" endpoint
    @GetMapping("/hello-world")
    public String helloWorldRequest() {
        // In this case, we return the plain text response "ok"
        return "Hello World";
    }

    @GetMapping("/coffee")
    public Coffee getCoffee(@RequestParam(name="name") String name, @RequestParam(name="size") String size) {
        Coffee coffee = new Coffee();
        coffee.setName(name);
        coffee.setSize(size);

        return coffee;
    }
}
