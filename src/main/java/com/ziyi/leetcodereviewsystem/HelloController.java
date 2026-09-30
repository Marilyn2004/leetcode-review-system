package com.ziyi.leetcodereviewsystem;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public HelloResponse hello() {
        return new HelloResponse("Hello from LeetCode Review System!", "running");
    }
}
