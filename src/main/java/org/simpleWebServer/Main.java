package org.simpleWebServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.simpleWebServer.Coffee.CoffeeSize;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("/coffee")
    public ResponseEntity<Coffee> getCoffee(
            @RequestHeader HttpHeaders headers,
            @RequestParam(name="name") String name,
            @RequestParam(name="size") String size
    ) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        if (!headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized request for coffee.");
        }
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.toLowerCase().startsWith("basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);
            if (!(values[0].equals("Coffee drinker") && values[1].equals("I love coffee"))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized coffee drinker :).");
            }
        }

        CoffeeSize coffeeSize;
        try {
            coffeeSize = Coffee.CoffeeSize.valueOf(size);
        } catch (IllegalArgumentException ignored) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No coffee size of %s.".formatted(size));
        }

        if (coffeeSize.equals(CoffeeSize.XL)) {
            // Don't serve extra large coffees.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Extra large coffees are not available at this time.");
        } else if (coffeeSize.equals(CoffeeSize.L)) {
            // Large coffees take 0 - 5 seconds.
            int sleepInterval = (int) (Math.random() * 5000);
            Thread.sleep(sleepInterval);

            Coffee coffee = new Coffee();
            coffee.setName(name);
            coffee.setSize(coffeeSize);
            long endTime = System.currentTimeMillis();
            coffee.setPrepTime(endTime - startTime);

            return new ResponseEntity<>(coffee, HttpStatus.OK);
        } else {
            Coffee coffee = new Coffee();
            coffee.setName(name);
            coffee.setSize(coffeeSize);
            long endTime = System.currentTimeMillis();
            coffee.setPrepTime(endTime - startTime);

            return new ResponseEntity<>(coffee, HttpStatus.OK);
        }
    }
}
