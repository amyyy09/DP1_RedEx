package src.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController2 {

    // Simple GET endpoint to test if the controller is active
    @GetMapping("/tests")
    public String test() {
        return "Test endpoint is working!";
    }
}
