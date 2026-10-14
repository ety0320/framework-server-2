package com.bank.webframwork.hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String Hello() {
        String abc = "abc";
        abc.trim(); // "abc"
        return "Hello kimdo";
    }
}
